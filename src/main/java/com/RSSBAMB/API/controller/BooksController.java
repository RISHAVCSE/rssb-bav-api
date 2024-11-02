package com.RSSBAMB.API.controller;

import java.util.List;

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
	public ResponseEntity<String> addbooks(@RequestBody BooksRegisterDTO booksRegisterDTO){
		booksService.save(booksRegisterDTO);
		return new ResponseEntity<>("Books Added sucessfully",HttpStatus.CREATED);
		
	}
	
	@PutMapping("updateBook/{id}")
	public ResponseEntity<String> updateBook(@PathVariable Long id,  @RequestBody Books updatedBook){
		booksService.updateBook(id, updatedBook);
		return new ResponseEntity<>("Books Updated successfullly",HttpStatus.CREATED);
	}
	
	@GetMapping("/allbooks")
	public List<Books> getAllBooks(){
		return booksService.getAllBooks();
		}
	
	

}
