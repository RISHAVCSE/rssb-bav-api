package com.RSSBAMB.API.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.RSSBAMB.API.model.CentreBook;

@Repository
public interface CentreBookRepository extends JpaRepository<CentreBook,Long> {
	List<CentreBook> findByBook_mmsId(String mmsId);
List<CentreBook> findByCentreCentreCode(Integer centreCode );
CentreBook findByBookMmsIdAndCentreCentreCode(String mmsId,int centreCode);
@Query("SELECT COALESCE(SUM(a.allocatedQuantity), 0) FROM CentreBook a WHERE a.book.mmsId = :mmsId")
int sumAllocatedQuantityByMmsId(@Param("mmsId") String mmsId);

@Query("""
	    SELECT COALESCE(SUM(cb.allocatedQuantity * cb.book.amount), 0)
	    FROM CentreBook cb
	    WHERE cb.centre.centreCode = :centreCode
	""")
	int getTotalAmountForCentre(@Param("centreCode") Integer centreCode);

}
