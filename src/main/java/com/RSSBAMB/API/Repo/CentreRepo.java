package com.RSSBAMB.API.Repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RSSBAMB.API.model.Centres;

public interface CentreRepo extends JpaRepository<Centres,Integer> {
	interface CentreView {
		int getCentreCode();
		String getCentreName();
	}
	Optional<Centres> findByCentreCode(Integer centreCode);
	List<CentreView> findAllProjectedBy();


}
