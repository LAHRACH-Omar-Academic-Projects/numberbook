package com.ensaj.geolocation;

import com.ensaj.geolocation.beans.User;
import com.ensaj.geolocation.controllers.UserController;
import com.ensaj.geolocation.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class GeolocationApplication {
	public static void main(String[] args) {
		SpringApplication.run(GeolocationApplication.class, args);
	}
}
