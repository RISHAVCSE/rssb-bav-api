package com.RSSBAMB.API.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.RSSBAMB.API.model.BookAllocation;

import jakarta.transaction.Transactional;

@Repository
public interface BookAllocationRepo extends JpaRepository<BookAllocation,Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM BookAllocation b WHERE b.CentreCode = :CentreCode")
	void deleteByCentreCode(@Param("CentreCode") int CentreCode);
	
	@Query("SELECT COALESCE(SUM(a.quantity),0) FROM BookAllocation a WHERE a.mmsId= :mmsId")
	int sumQuantityByMmsId(@Param("mmsId") String mmsId);
}
