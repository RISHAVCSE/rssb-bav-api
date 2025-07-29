package com.RSSBAMB.API.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RSSBAMB.API.DTO.BookWithStatusDTO;
import com.RSSBAMB.API.DTO.BooksRegisterDTO;
import com.RSSBAMB.API.Repo.BooksRepo;
import com.RSSBAMB.API.Service.BooksService;
import com.RSSBAMB.API.model.Books;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BooksController {
	
	@Autowired
	BooksRepo bookrepo;
	@Autowired
	BooksService booksService;
	
	@PostMapping("/addbooks")
	public ResponseEntity<Map<String, String>> addbooks(@RequestBody BooksRegisterDTO booksRegisterDTO,Principal  createdby){
		
		 String changedBy = "Rishav";
		    try {
		        booksService.save(booksRegisterDTO, changedBy);
		        return ResponseEntity.status(HttpStatus.CREATED)
		                .body(Collections.singletonMap("message", "Book added successfully"));
		    } catch (RuntimeException e) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                .body(Collections.singletonMap("error", e.getMessage()));
		    }
		
	}
	
	@PutMapping("updateBook/{id}")
	public ResponseEntity<Map<String, String>> updateBook(@PathVariable Long id,  @RequestBody BooksRegisterDTO updatedBook){
		  try {
		        booksService.updateBook(id, updatedBook);
		        return ResponseEntity.status(HttpStatus.CREATED)
		                .body(Collections.singletonMap("message", "Book updated successfully"));
		    } catch (RuntimeException e) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                .body(Collections.singletonMap("error", e.getMessage()));
		    }
	}
	
	@GetMapping("/allbooks")
	public List<Books> getAllBooks(){
		return booksService.getAllBooks();
		}
	
	@GetMapping("/booksData")
	public List<BookWithStatusDTO> getAllBooksByStatus(){
		return booksService.getAllBooksWithStatus();
	}
	
	@GetMapping("/testing")
	public  void testing(){
		booksService.testingData();
		}
	
	

}
