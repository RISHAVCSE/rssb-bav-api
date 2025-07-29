package com.RSSBAMB.API.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RSSBAMB.API.DTO.CentreBookDTO;
import com.RSSBAMB.API.Service.CentreBookService;
import com.RSSBAMB.API.model.BookAllotmentAllocationRecord;
import com.RSSBAMB.API.model.CentreBook;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/centre-book")
public class CentreBookController {
	
	@Autowired
	private CentreBookService centreBookService;
	
	
	@PostMapping("/allocateBooktoCentre")
	public ResponseEntity<Map<String,String>> allocateBooktoCentre(@RequestBody CentreBookDTO request){
		try {
			centreBookService.allocateBookToCentre(request.getMmsId(), request.getCentreCode(),request.getQuantity());
 return ResponseEntity.status(HttpStatus.ACCEPTED).body(Collections.singletonMap("message", "Books allocated Successfully"));
			
		} catch(RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("error",e.getMessage() ));
			
		}
		
	}

	
	
	@PostMapping("/allocateOrChangeCenterData")
	public ResponseEntity<CentreBook> allocateOrChangeCenterData(@RequestBody CentreBookDTO request){
		CentreBook centreBook=centreBookService.allocateOrChangeCenterData(request.getMmsId(), request.getCentreCode(),request.getQuantity());
		
		return ResponseEntity.ok(centreBook);
	}
	
    @GetMapping("/getBookBasedUponCentre")
    public ResponseEntity<List<CentreBook>> getBookBasedUponCentre(@RequestParam String mmsId) {
        List<CentreBook> centreBooks = centreBookService.getCentresByMmsId(mmsId);
        return ResponseEntity.ok(centreBooks);
    }

	
	@GetMapping("/getAllRecords")
	public ResponseEntity<List<CentreBook>> getAllRecords(){
		List<CentreBook> allRecords=centreBookService.getAllRecords();
		return ResponseEntity.ok(allRecords);
	}
	
	@GetMapping("/getAllBookBasedUponCentre")
	public ResponseEntity<List<CentreBook>> getAllBookBasedUponCentre(@RequestParam int centreCode){
		List<CentreBook> centreBooks=centreBookService.getBooksByCentreCode(centreCode);
		
		return ResponseEntity.ok(centreBooks);
	}
	
	@GetMapping("/getAllRecordsBasedUponCentre")
	public ResponseEntity <Map<String, Map<LocalDate, List<BookAllotmentAllocationRecord>>>> getAllRecordsBasedUponCentre(
	        @RequestParam int centreCode) {

		Map<String,Map<LocalDate, List<BookAllotmentAllocationRecord>>> result = 
	    		centreBookService.getRecordBasedUponCentre(centreCode);

	    return ResponseEntity.ok(result);
	}

	
	

}
