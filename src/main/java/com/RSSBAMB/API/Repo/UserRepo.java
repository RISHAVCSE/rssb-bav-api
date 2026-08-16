package com.RSSBAMB.API.Repo;

import java.util.List;
import java.util.Optional;

import com.RSSBAMB.API.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.RSSBAMB.API.model.User;

@RepositoryRestResource
public interface UserRepo extends JpaRepository<User,Long>  {
	Optional<User> findByUsername(String username);
	Optional<User> findByKeycloakId(String keycloakId);

	//Find all SubUsers mapped under a specific USER
	List<User> findByParentUserId(Long parentUserId);
	// Find all users by role
	List<User> findByRole(Role role);
	// Find all parent users (users who can have sub-users)
	List<User> findByRoleIn(List<Role> roles);
}
