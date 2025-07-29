package com.RSSBAMB.API.Repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RSSBAMB.API.model.Centres;

public interface CentreRepo extends JpaRepository<Centres,Integer> {
	
	Optional<Centres> findByCentreCode(Integer centreCode);

}
