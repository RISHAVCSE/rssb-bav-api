package com.RSSBAMB.API.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.RSSBAMB.API.DTO.CentreDTO;
import com.RSSBAMB.API.Repo.CentreRepo;
import com.RSSBAMB.API.model.Centres;

@Service
public class CentreService {
	private final CentreRepo centreRepo;
	
	public CentreService(CentreRepo centreRepo) {
		this.centreRepo=centreRepo;
	}
	
	public Centres addCentre(CentreDTO centreDTO) {
		  Optional<Centres> existing = centreRepo.findByCentreCode(centreDTO.getCentreCode());

		    if (existing.isPresent()) {
		        throw new RuntimeException("Centre already exists with code: " + centreDTO.getCentreCode());
		    }
		Centres centres=new Centres();
		
		centres.setCentreCode(centreDTO.getCentreCode());
		centres.setCentreName(centreDTO.getCentreName());
		centres.setSanctionedAmount(centreDTO.getSanctionedAmount());
		centres.setAmountUtilized(centreDTO.getAmountUtilized());
		centres.setEmail(centreDTO.getEmail());
		centres.setPhoneNumber(centreDTO.getPhoneNumber());
		
		
		return centreRepo.save(centres);
	}
	
	public List<Centres> getAllCentres(){
		return centreRepo.findAll();
	}
	
	public Centres updateCentre(CentreDTO centreDTO) {
		
		Optional<Centres> existing=centreRepo.findByCentreCode(centreDTO.getCentreCode());
		if(existing.isEmpty()) {
			throw new  RuntimeException("Centre Code Not existing"+centreDTO.getCentreCode());
		}
		Centres centres=existing.get();
		centres.setCentreName(centreDTO.getCentreName());
		centres.setSanctionedAmount(centreDTO.getSanctionedAmount());
		centres.setAmountUtilized(centreDTO.getAmountUtilized());
		centres.setEmail(centreDTO.getEmail());
		centres.setPhoneNumber(centreDTO.getPhoneNumber());
		
		return centreRepo.save(centres);
	}
	

}
