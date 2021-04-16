package com.ensaj.geolocation.controllers;

import java.util.List;
import java.util.Optional;

import com.ensaj.geolocation.beans.FriendingState;
import com.ensaj.geolocation.repository.FriendingStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ensaj.geolocation.beans.User;
import com.ensaj.geolocation.repository.UserRepository;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/all")
	public List<User> findAllUsers(){
		return userRepository.findAll();
	}

	@GetMapping("/findUserByDeviceImei/{deviceImei}")
	public Optional<User> findUserByDeviceImei(@PathVariable String deviceImei) {
		return userRepository.findByDeviceImei(deviceImei);
	}

	@GetMapping("/findUserByPhoneNumber/{phoneNumber}")
	public Optional<User> findUserByPhoneNumber(@PathVariable String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber);
	}

	@PostMapping("/save")
	public User saveUser(@RequestBody User user){
		return userRepository.save(user);
	}

	@PutMapping("/edit")
	public boolean updateUser(@RequestBody User user) {
		try {
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@DeleteMapping("/delete")
	public boolean deleteUser(@RequestBody User user) {
		try {
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
