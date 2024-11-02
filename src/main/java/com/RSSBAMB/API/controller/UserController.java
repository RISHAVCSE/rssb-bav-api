package com.RSSBAMB.API.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RSSBAMB.API.DTO.UserLoginDTO;
import com.RSSBAMB.API.DTO.UserRegisterDTO;
import com.RSSBAMB.API.Repo.UserRepo;
import com.RSSBAMB.API.Service.UserService;
import com.RSSBAMB.API.model.User;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	UserRepo repo;
	@Autowired
	private UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody UserRegisterDTO userDTO){
		userService.save(userDTO);
		return new ResponseEntity<>("User registered successfullt",HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody UserLoginDTO userLoginDTO){
		boolean isValid=userService.validateUser(userLoginDTO.getUserName(), userLoginDTO.getPassword());
		if(isValid) {
			return ResponseEntity.ok("Login Successful");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
		}
	}
	
	@GetMapping("/getAllUsers")
	public List<User> getAllUsers(){
		return userService.getAllUsers();
	}

}
