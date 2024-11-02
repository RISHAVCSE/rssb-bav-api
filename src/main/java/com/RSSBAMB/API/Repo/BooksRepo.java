package com.RSSBAMB.API.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.RSSBAMB.API.model.Books;

@RepositoryRestResource
public interface BooksRepo extends JpaRepository<Books,Long> {
//	Optional<Books> findByBooks(String book_name);
    Optional<Books> findByBookId(Long bookId); // Assuming bookId is a field in Books
    @Query("SELECT b FROM Books b WHERE b.mms_Id = :mms_Id")
    Optional<Books> findByMms_Id(@Param("mms_Id") String mmsId);

   



	
	
}