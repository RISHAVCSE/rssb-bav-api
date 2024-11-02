package com.RSSBAMB.API.Service;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;

import com.RSSBAMB.API.DTO.BooksRegisterDTO;
import com.RSSBAMB.API.Repo.BooksRepo;
import com.RSSBAMB.API.model.Books;

@Service
public class BooksService {
	
	private final BooksRepo booksRepo;
	
	public BooksService(BooksRepo booksRepo) {
		this.booksRepo=booksRepo;
	}
	
	public Books save(BooksRegisterDTO booksRegisterDTO) {
		Books books=new Books();
		
		books.setBook_name(booksRegisterDTO.getBook_name());
		books.setAmount(booksRegisterDTO.getAmount());
		books.setMms_Id(booksRegisterDTO.getMms_Id());
		books.setQuantity(booksRegisterDTO.getQuantity());
		
		return booksRepo.save(books);
		
	}
	
	public Books updateBook(Long id,Books updatedBook) {
		// Check if the book with the given id exists
		String str = id+"";
		 Optional<Books> existingBook = booksRepo.findByMms_Id(str);

		    // Check if the book with the provided bookId exists
		    if (existingBook.isEmpty()) {
		        throw new RuntimeException("Book not found with Id " + id);
		    }

		    // Update the book details
		    Books book = existingBook.get();
		    book.setBook_name(updatedBook.getBook_name());
		    book.setAmount(updatedBook.getAmount());
		    book.setQuantity(updatedBook.getQuantity());

		    return booksRepo.save(book);					
	}
	public List<Books> getAllBooks(){
		return booksRepo.findAll();
		
	}
	
	public void deleteBook(Long id) {
		booksRepo.deleteById(id);
	}
	

}
