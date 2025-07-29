package com.RSSBAMB.API.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RSSBAMB.API.DTO.BookAllocationDTO;
import com.RSSBAMB.API.DTO.BookAllocationDTO.BookAllocationDetail;
import com.RSSBAMB.API.DTO.CentreBookDTO;
import com.RSSBAMB.API.Repo.BookAllocationRepo;
import com.RSSBAMB.API.Repo.BooksHistoryRepo;
import com.RSSBAMB.API.Repo.BooksRepo;
import com.RSSBAMB.API.Repo.CentreBookRepository;
import com.RSSBAMB.API.Repo.CentreRepo;
import com.RSSBAMB.API.helper.AllotmentAllocationSaleHelper;
import com.RSSBAMB.API.model.BookAllocation;
import com.RSSBAMB.API.model.Books;
import com.RSSBAMB.API.model.BooksHistory;
import com.RSSBAMB.API.model.CentreBook;
import com.RSSBAMB.API.model.Centres;

@Service
public class BookAllocationService {
	
	@Autowired
	private BookAllocationRepo bookAllocationRepo;
	
	@Autowired
	private CentreBookRepository centreBookRepository;
	
	@Autowired
	private CentreRepo centreRepository;
	
	@Autowired
	private BooksHistoryRepo booksHistoryRepo;
	
	@Autowired
	private BooksRepo booksRepo;
	
	@Autowired
	private AllotmentAllocationSaleHelper logger;

	
	public void saveBookAllocation(BookAllocationDTO request) {
		
		LocalDateTime current=LocalDateTime.now();
		for(BookAllocationDetail book: request.getBooks()) {
			CentreBook stockOpt=centreBookRepository.findByBookMmsIdAndCentreCentreCode(book.getMmsId(),request.getCentreCode());

			if(stockOpt.getAllocatedQuantity()<=book.getQuantity()) {
                throw new IllegalArgumentException("Stock available nahi hai for book: " + book.getBookName());

			}
			stockOpt.setAllocatedQuantity(stockOpt.getAllocatedQuantity()-book.getQuantity());
			centreBookRepository.save(stockOpt);
		}
		
		List<BookAllocation> allocations=request.getBooks().stream()
				.map(book ->{
					BookAllocation allocation=new BookAllocation();
					allocation.setCentreCode(request.getCentreCode());
					allocation.setMmsId(book.getMmsId());
					allocation.setBookName(book.getBookName());
					allocation.setQuantity(book.getQuantity());
					allocation.setAmount(book.getAmount());
					allocation.setAllocationTime(current);
					BooksHistory historyRecord=allocation.createHistoryRecord("Allocation","SuperAdmin");
					booksHistoryRepo.save(historyRecord);
					
					return allocation;
					
				})
				.collect(Collectors.toList());
		
		bookAllocationRepo.saveAll(allocations);
	}
	
	
	public List<BookAllocationDTO> getAllBookAllocationsGroupedByCentre(){
		List<BookAllocation> allocations=bookAllocationRepo.findAll();
		
		//Group it by Centre Code First
		HashMap<Integer,HashMap<String,BookAllocationDTO.BookAllocationDetail>> groupedData=new HashMap<>();
		
		for(BookAllocation allocation: allocations) {
			int centreCode=allocation.getCentreCode();
			String mmsId=allocation.getMmsId();
			
			groupedData
			.computeIfAbsent(centreCode, k-> new HashMap<>())
			.merge(mmsId, 
					createBookDetail(allocation), 
			         (existing,newBook)->{
						existing.setQuantity(existing.getQuantity()+newBook.getQuantity());
						if(newBook.getAllocationTime().isBefore(existing.getAllocationTime())) {
							existing.setAllocationTime(newBook.getAllocationTime());
						}
						return existing;
					});
		}
		
		return groupedData.entrySet().stream().map(entry->{
			BookAllocationDTO dto=new BookAllocationDTO();
			dto.setCentreCode(entry.getKey());
			dto.setBooks(new ArrayList<>(entry.getValue().values()));
			 
			dto.setCurrentTime(dto.getBooks().stream()
					.map(BookAllocationDTO.BookAllocationDetail::getAllocationTime)
					.min(LocalDateTime::compareTo)
					.orElse(null));
			return dto;
		}).collect(Collectors.toList());
		

	}
	
	private BookAllocationDTO.BookAllocationDetail createBookDetail(BookAllocation allocation){
		BookAllocationDTO.BookAllocationDetail detail=new BookAllocationDTO.BookAllocationDetail();
		detail.setMmsId(allocation.getMmsId());
		detail.setAmount(allocation.getAmount());
		detail.setBookName(allocation.getBookName());
		detail.setQuantity(allocation.getQuantity());
		detail.setAllocationTime(allocation.getAllocationTime());
		
		return detail;
	}
	
	public Optional<Integer> getSanctionedAmountByCentreCode(int centreCode){
		return centreRepository.findByCentreCode(centreCode).map(Centres::getSanctionedAmount);
	}
	public void allocationApproval(BookAllocationDTO request, boolean status) {
		if(status) {
			for(BookAllocationDetail book : request.getBooks() ) {
				Optional<Books> bookAlloted = booksRepo.findByMmsId(book.getMmsId());
				if(bookAlloted==null) {
					throw new RuntimeException("Book with MMS ID"+ book.getMmsId()+"Not available");
					
				}
				int requestedQuantity=book.getQuantity();
				int availableQuantity=bookAlloted.get().getQuantity();
				int newquantity=0;
				if(requestedQuantity>availableQuantity) {
					throw new RuntimeException("Red Alert");
					}else {
						newquantity=availableQuantity-requestedQuantity;
						if(newquantity>=0) {
							bookAlloted.get().setQuantity(newquantity);
						    BooksHistory historyRecord = new BooksHistory(bookAlloted.get(), "Sale Quantity before"+availableQuantity, "SuperAdmin");
						    booksHistoryRepo.save(historyRecord);
						    //Testing Code 
						    Date current = new Date(); // from java.util.Date
							// Get total allocated amount - handle null case properly
							Integer totalAmount = centreBookRepository.getTotalAmountForCentre(request.getCentreCode());
							int total = totalAmount != null ? totalAmount : 0;

							// Get sanctioned amount - properly unwrap Optional
							int sanctionedAmount = getSanctionedAmountByCentreCode(request.getCentreCode())
							                     .orElseThrow(() -> new RuntimeException("Sanctioned amount not found for centre"));

							// Calculate new value
							int newValue = requestedQuantity * book.getAmount();
							// Check if new allocation exceeds limit (with 100 buffer)
						
							CentreBook existingCentreBook=centreBookRepository.findByBookMmsIdAndCentreCentreCode(book.getMmsId(), request.getCentreCode());

							int previousquantity=existingCentreBook.getAllocatedQuantity();

							Centres centre1 = centreRepository.findById(request.getCentreCode())
								    .orElseThrow(() -> new RuntimeException("Centre not found"));
								centre1.setAmountUtilized(newValue + total);  // Update if needed
							logger.logAllocation(book.getMmsId(), request.getCentreCode(), book.getBookName(), 
									book.getAmount(), requestedQuantity, "Sales",
									"Sales Approved", previousquantity+requestedQuantity, sanctionedAmount, 
									total-newValue, previousquantity, current,
									"SuperAdmin", "Sales");

							

						    
						    
						    
						    //End
							booksRepo.save(bookAlloted.get());
							bookAllocationRepo.deleteByCentreCode(request.getCentreCode());
							
							
						}else {
							throw new RuntimeException("Quantity Values are Incorrect");
						}
						
					
				}
				
				
			}
			
		}else {
			
			for(BookAllocationDetail book: request.getBooks()) {
				CentreBook stockOpt=centreBookRepository.findByBookMmsIdAndCentreCentreCode(book.getMmsId(),request.getCentreCode());

				stockOpt.setAllocatedQuantity(stockOpt.getAllocatedQuantity()+book.getQuantity());
				

				centreBookRepository.save(stockOpt);
				bookAllocationRepo.deleteByCentreCode(request.getCentreCode());

			}
			
		}
		
	}
	
	

}
