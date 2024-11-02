package com.RSSBAMB.API.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.RSSBAMB.API.DTO.UserRegisterDTO;
import com.RSSBAMB.API.Repo.UserRepo;
import com.RSSBAMB.API.model.User;


@Service
public class UserService {
	
	
    private final UserRepo userRepo;
    public UserService(UserRepo userRepo) {
    	this.userRepo=userRepo;
    }
    
    public User save(UserRegisterDTO userDTO) {
    	User user=new User();
    	user.setUserName(userDTO.getUserName());
    	user.setPassword(userDTO.getPassword());
    	user.setRole(userDTO.getRole());
    	
    	return userRepo.save(user);
    	
    }
    public Optional<User> findByUsername(String username){
    	return userRepo.findByUserName(username);
    	
    }
    public boolean validateUser(String username, String password) {
    	Optional<User> userOptional=userRepo.findByUserName(username);
    	return userOptional.isPresent() && userOptional.get().getPassword().equals(password);
    }
    public List<User> getAllUsers(){
    	return userRepo.findAll();
    }

}
