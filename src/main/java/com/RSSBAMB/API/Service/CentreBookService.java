package com.RSSBAMB.API.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RSSBAMB.API.Repo.BookAllotmentAllocationRecordRepo;
import com.RSSBAMB.API.Repo.BooksRepo;
import com.RSSBAMB.API.Repo.CentreBookRepository;
import com.RSSBAMB.API.Repo.CentreRepo;
import com.RSSBAMB.API.helper.AllotmentAllocationSaleHelper;
import com.RSSBAMB.API.model.BookAllotmentAllocationRecord;
import com.RSSBAMB.API.model.Books;
import com.RSSBAMB.API.model.CentreBook;
import com.RSSBAMB.API.model.Centres;

@Service
public class CentreBookService {
	
	@Autowired
	private CentreBookRepository centreBookRepository;
	
	@Autowired
	private BooksRepo bookRepository;
	
	@Autowired
	private CentreRepo centreRepository;
	
	@Autowired
	private AllotmentAllocationSaleHelper logger;
	
	@Autowired
	private BookAllotmentAllocationRecordRepo bookAllotmentAllocationRecordRepo;
	
	public CentreBook allocateBookToCentre(String mmsId,int centreCode,int quantity) {
		Books book=bookRepository.findById(mmsId)
				.orElseThrow(()-> new RuntimeException("Book not found"));
		String allocationType="";
		
		Centres centre=centreRepository.findById(centreCode)
				.orElseThrow(()-> new RuntimeException("Centre not found"));
		
      

		if(quantity>0) {
			allocationType="Alloted";
		}else if(quantity<0){
			allocationType="DeAllocated";
		}else {
			throw new RuntimeException("Quantity value not allowed");
		}
		
		CentreBook existingCentreBook=centreBookRepository.findByBookMmsIdAndCentreCentreCode(mmsId, centreCode);
		int availableQuantity=book.getQuantity();
		if(existingCentreBook!=null) {
			int newQuantity=existingCentreBook.getAllocatedQuantity()+quantity;
			if(availableQuantity<newQuantity) {
				throw new RuntimeException("Quantity value not allowed");
			}
			Date current = new Date(); // from java.util.Date
			// Get total allocated amount - handle null case properly
			Integer totalAmount = centreBookRepository.getTotalAmountForCentre(centreCode);
			int total = totalAmount != null ? totalAmount : 0;

			// Get sanctioned amount - properly unwrap Optional
			int sanctionedAmount = getSanctionedAmountByCentreCode(centreCode)
			                     .orElseThrow(() -> new RuntimeException("Sanctioned amount not found for centre"));

			// Calculate new value
			int newValue = quantity * book.getAmount();
			// Check if new allocation exceeds limit (with 100 buffer)
			if (newValue + total > sanctionedAmount + 100) {
			    throw new RuntimeException(String.format(
			        "Allocation would exceed limit. Current: %d, New: %d, Sanctioned: %d (Buffer: 100)",
			        total, newValue, sanctionedAmount
			    ));
			}
			existingCentreBook.setAllotedDate(current);
			int previousquantity=existingCentreBook.getAllocatedQuantity();

			existingCentreBook.setAllocatedQuantity(newQuantity);
			Centres centre1 = centreRepository.findById(centreCode)
				    .orElseThrow(() -> new RuntimeException("Centre not found"));
				centre1.setAmountUtilized(newValue + total);  // Update if needed
			logger.logAllocation(mmsId, centreCode, book.getBookName(), 
					book.getAmount(), quantity, allocationType,
					"NA", previousquantity, sanctionedAmount, 
					newValue + total, newQuantity, current,
					"SuperAdmin", allocationType);
			
			
			return centreBookRepository.save(existingCentreBook);
		}
		else {
			CentreBook centreBook=new CentreBook();
			centreBook.setBook(book);
			centreBook.setCentre(centre);
			if(availableQuantity<quantity) {
				throw new RuntimeException("Quantity value not allowed");
			}
			Date current = new Date(); // from java.util.Date

			centreBook.setAllotedDate(current);
			// Get total allocated amount - handle null case properly
						Integer totalAmount = centreBookRepository.getTotalAmountForCentre(centreCode);
						int total = totalAmount != null ? totalAmount : 0;

						// Get sanctioned amount - properly unwrap Optional
						int sanctionedAmount = getSanctionedAmountByCentreCode(centreCode)
						                     .orElseThrow(() -> new RuntimeException("Sanctioned amount not found for centre"));

						// Calculate new value
						int newValue = quantity * book.getAmount();
						// Check if new allocation exceeds limit (with 100 buffer)
						if (newValue + total > sanctionedAmount + 100) {
						    throw new RuntimeException(String.format(
						        "Allocation would exceed limit. Current: %d, New: %d, Sanctioned: %d (Buffer: 100)",
						        total, newValue, sanctionedAmount
						    ));
						}
						
						Centres centre1 = centreRepository.findById(centreCode)
							    .orElseThrow(() -> new RuntimeException("Centre not found"));
							centre1.setAmountUtilized(newValue + total);  // Update if needed

			centreBook.setAllocatedQuantity(quantity);
			logger.logAllocation(mmsId, centreCode, book.getBookName(), 
					book.getAmount(), quantity, allocationType,
					"NA", 0, sanctionedAmount, 
					newValue + total, quantity, current,
					"SuperAdmin", allocationType);
			
			
			return centreBookRepository.save(centreBook);
		}
		
		
	}
		
	public Optional<Integer> getSanctionedAmountByCentreCode(int centreCode){
		return centreRepository.findByCentreCode(centreCode).map(Centres::getSanctionedAmount);
	}
	
	
	public CentreBook allocateOrChangeCenterData(String mmsId,int centreCode,int quantity) {
		Books book=bookRepository.findById(mmsId)
				.orElseThrow(()-> new RuntimeException("Book not found"));
		
		Centres centre=centreRepository.findById(centreCode)
				.orElseThrow(()-> new RuntimeException("Centre not found"));
		int availableQuantity=book.getQuantity();
		String allocationType="";
		
      

		


		CentreBook existingCentreBook=centreBookRepository.findByBookMmsIdAndCentreCentreCode(mmsId, centreCode);
		
		if(existingCentreBook!=null) {
			if(availableQuantity<quantity) {
				throw new RuntimeException("Quantity value not allowed");
			}
			Date current = new Date(); // from java.util.Date
			if(quantity>existingCentreBook.getAllocatedQuantity()) {
				allocationType="Alloted";
			}else if(quantity<existingCentreBook.getAllocatedQuantity()){
				allocationType="DeAllocated";
			}else {
				throw new RuntimeException("Quantity value not allowed");
			}

			existingCentreBook.setAllotedDate(current);
			existingCentreBook.setAllocatedQuantity(quantity);
			// Get total allocated amount - handle null case properly
						Integer totalAmount = centreBookRepository.getTotalAmountForCentre(centreCode);
						int total = totalAmount != null ? totalAmount : 0;

						// Get sanctioned amount - properly unwrap Optional
						int sanctionedAmount = getSanctionedAmountByCentreCode(centreCode)
						                     .orElseThrow(() -> new RuntimeException("Sanctioned amount not found for centre"));

						// Calculate new value
						int newValue = quantity * book.getAmount();
						// Check if new allocation exceeds limit (with 100 buffer)
						if (newValue + total > sanctionedAmount + 100) {
						    throw new RuntimeException(String.format(
						        "Allocation would exceed limit. Current: %d, New: %d, Sanctioned: %d (Buffer: 100)",
						        total, newValue, sanctionedAmount
						    ));
						}
						
						Centres centre1 = centreRepository.findById(centreCode)
							    .orElseThrow(() -> new RuntimeException("Centre not found"));
							centre1.setAmountUtilized(newValue + total);  // Update if needed

			logger.logAllocation(mmsId, centreCode, book.getBookName(), 
					book.getAmount(), quantity, allocationType,
					"NA", 0, sanctionedAmount, 
					newValue + total, quantity, current,
					"SuperAdmin", allocationType);
			
			return centreBookRepository.save(existingCentreBook);
		}
		else {
			CentreBook centreBook=new CentreBook();
			centreBook.setBook(book);
			centreBook.setCentre(centre);
			if(availableQuantity<quantity) {
				throw new RuntimeException("Quantity value not allowed");
			}
			Date current = new Date(); // from java.util.Date

			centreBook.setAllotedDate(current);
			centreBook.setAllocatedQuantity(quantity);
			
			return centreBookRepository.save(centreBook);
		}
		
		
	}
	
	public List<CentreBook> getCentresByMmsId(String mmsID){
		return centreBookRepository.findByBook_mmsId(mmsID);
	}
	
	public List<CentreBook> getAllRecords(){
		return centreBookRepository.findAll();	
		
	}
	public List<CentreBook> getBooksByCentreCode(int centreCode){
		return centreBookRepository.findByCentreCentreCode(centreCode);
	}
	
	public Map<String,Map<LocalDate, List<BookAllotmentAllocationRecord>>> getRecordBasedUponCentre(int centreCode){
		List<BookAllotmentAllocationRecord> record=
				bookAllotmentAllocationRecordRepo.findByCentreCodeOrderByAllotedDateDesc(centreCode);
		
		return record.stream()
				.collect(Collectors.groupingBy(
						BookAllotmentAllocationRecord::getType,
						LinkedHashMap::new,
						Collectors.groupingBy(
								r-> r.getAllotedDate().toInstant()
								.atZone(ZoneId.systemDefault())
								.toLocalDate(),
								LinkedHashMap::new,
								Collectors.toList()
								)
						));
	}

}
