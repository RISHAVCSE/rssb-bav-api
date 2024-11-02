package com.RSSBAMB.API.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.RSSBAMB.API.model.User;

@RepositoryRestResource
public interface UserRepo extends JpaRepository<User,Long>  {
	Optional<User> findByUserName(String userName);

}
