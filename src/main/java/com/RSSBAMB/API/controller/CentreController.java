package com.RSSBAMB.API.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RSSBAMB.API.DTO.BooksRegisterDTO;
import com.RSSBAMB.API.DTO.CentreDTO;
import com.RSSBAMB.API.Repo.CentreRepo;
import com.RSSBAMB.API.Service.CentreService;
import com.RSSBAMB.API.model.Centres;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/centres")
public class CentreController {
	
	@Autowired
	CentreService centreService;
	
	@GetMapping("/getAllCentres")
	public List<Centres> getAllCentres(){
		return centreService.getAllCentres();
	}
	
	@PostMapping("/addCentre")
	public ResponseEntity<String> addCentre(@RequestBody CentreDTO centreDTO){
		centreService.addCentre(centreDTO);
		return new ResponseEntity<>("Centre Added Successfully",HttpStatus.CREATED);
	}
	
	@PutMapping("/updateCentre")
	public ResponseEntity<String> updateCentre(@RequestBody CentreDTO centreDTO){
		centreService.updateCentre(centreDTO);
		
		return new ResponseEntity<>("Centre Update Successfully",HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteCentre/{id}")
	public ResponseEntity<String> deleteCentre(@PathVariable int centreCode){
		
		return new ResponseEntity<>("Centre Deleted Successfully",HttpStatus.OK);
	}
	
	

}
