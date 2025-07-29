package com.RSSBAMB.API.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RSSBAMB.API.DTO.BookWithStatusDTO;
import com.RSSBAMB.API.DTO.BooksRegisterDTO;
import com.RSSBAMB.API.Repo.BookAllocationRepo;
import com.RSSBAMB.API.Repo.BooksHistoryRepo;
import com.RSSBAMB.API.Repo.BooksRepo;
import com.RSSBAMB.API.Repo.CentreBookRepository;
import com.RSSBAMB.API.model.Books;
import com.RSSBAMB.API.model.BooksHistory;

@Service
public class BooksService {
	
	private final BooksRepo booksRepo;
	
	public BooksService(BooksRepo booksRepo) {
		this.booksRepo=booksRepo;
	}
	
	@Autowired
	private BooksHistoryRepo booksHistoryRepo;
	
	@Autowired
	private BookAllocationRepo bookAllocationRepo;
	
	@Autowired
	private CentreBookRepository centreBookRepository;
	
	public void save(BooksRegisterDTO booksRegisterDTO, String changedBy) {
		
		 if (booksRegisterDTO.getMmsId() == null || booksRegisterDTO.getMmsId().isEmpty()) {
		        throw new RuntimeException("mmsId must be provided before saving the book.");
		    }
		Books books=new Books();
		 Optional<Books> existingBook = booksRepo.findByMmsId(booksRegisterDTO.getMmsId());
		 if(!existingBook.isEmpty()) {
			 throw new RuntimeException("MMS Code already exist");
			 
		 }
		
		
		
		books.setBookName(booksRegisterDTO.getBookName());
		books.setAmount(booksRegisterDTO.getAmount());
		books.setMmsId(booksRegisterDTO.getMmsId());
		books.setQuantity(booksRegisterDTO.getQuantity());
		booksRepo.save(books);
		BooksHistory historyRecord=books.createHistoryRecord("ADD","SuperAdmin");
		
		booksHistoryRepo.save(historyRecord);	 

		
	}
	
	public Books updateBook(Long id,BooksRegisterDTO updatedBook) {
		// Check if the book with the given id exists
		String str = id+"";
		 Optional<Books> existingBook = booksRepo.findByMmsId(str);

		    // Check if the book with the provided bookId exists
		    if (existingBook.isEmpty()) {
		        throw new RuntimeException("Book not found with Id " + id);
		    }

		    // Update the book details
		    Books book = existingBook.get();
		    book.setBookName(updatedBook.getBookName());
		    book.setAmount(updatedBook.getAmount());
		    book.setQuantity(updatedBook.getStockavailable()+book.getQuantity());

		    return booksRepo.save(book);					
	}
	public List<Books> getAllBooks(){
		return booksRepo.findAll();
		
	}
	public List<BookWithStatusDTO> getAllBooksWithStatus(){
	  List<Books> books=booksRepo.findAll();
	  
	  
	  return books.stream().map(book -> {
          // Fetch additional quantities for each book
			int pendingForReview= bookAllocationRepo.sumQuantityByMmsId(book.getMmsId());
			
			int allotedQuantityByCentre= centreBookRepository.sumAllocatedQuantityByMmsId(book.getMmsId());
          
          return new BookWithStatusDTO(
              book.getMmsId(),
              book.getBookName(),
              book.getQuantity(),
              book.getAmount(),
              allotedQuantityByCentre,
              pendingForReview
          );
      }).collect(Collectors.toList());
  }
		
	
	public void testingData() {
	int allotedQuantity= bookAllocationRepo.sumQuantityByMmsId("585");
	
	int allotedQuantityByCentre= centreBookRepository.sumAllocatedQuantityByMmsId("585");
	System.out.println(allotedQuantityByCentre);
	}
	
//	public void deleteBook(Long id) {
//		booksRepo.deleteById(id);
//	}
	

}
