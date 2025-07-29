package com.RSSBAMB.API.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.RSSBAMB.API.model.Books;

@RepositoryRestResource
public interface BooksRepo extends JpaRepository<Books,String> {
	
    @Query("SELECT b FROM Books b WHERE b.mmsId = :mmsId")
    Optional<Books> findByMmsId(@Param("mmsId") String mmsId);
    
    

   



	
	
}