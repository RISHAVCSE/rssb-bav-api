package com.RSSBAMB.API.Repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.RSSBAMB.API.model.BooksHistory;

public interface BooksHistoryRepo extends JpaRepository<BooksHistory,Long> {
	

}
