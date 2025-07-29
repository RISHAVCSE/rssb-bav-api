package com.RSSBAMB.API.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.RSSBAMB.API.model.BookAllotmentAllocationRecord;

@Repository
public interface BookAllotmentAllocationRecordRepo extends JpaRepository<BookAllotmentAllocationRecord,Integer> {
    List<BookAllotmentAllocationRecord> findByCentreCodeOrderByAllotedDateDesc(int centreCode);
	
}
