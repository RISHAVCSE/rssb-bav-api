package com.RSSBAMB.API.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RSSBAMB.API.DTO.BookAllocationDTO;
import com.RSSBAMB.API.Service.BookAllocationService;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/allocation")
public class BookAllocationController {
	
	@Autowired BookAllocationService bookAllocationService;
	
	@PostMapping("/add")
	public ResponseEntity<String> allocateBooks(@RequestBody BookAllocationDTO request ){
		try {
			bookAllocationService.saveBookAllocation(request);
			return ResponseEntity.ok("Book Send for Review");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to get Response"+ e.getMessage());
		}
	}
	
	@PostMapping("/update")
	public ResponseEntity<String> allocateUpdate(@RequestBody BookAllocationDTO request, @RequestParam boolean status){
		try {
			bookAllocationService.allocationApproval(request, status);
			return ResponseEntity.ok("Operation Completed");
		} catch(Exception e) {
			return ResponseEntity.badRequest().body("Failed to get Response"+ e.getMessage());
		}
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<BookAllocationDTO>> getAllAllocations(){
		return ResponseEntity.ok(bookAllocationService.getAllBookAllocationsGroupedByCentre());
	}

}
