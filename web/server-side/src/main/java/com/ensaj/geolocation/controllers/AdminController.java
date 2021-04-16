package com.ensaj.geolocation.controllers;

import com.ensaj.geolocation.beans.Admin;
import com.ensaj.geolocation.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admins")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/auth/login")
    public Admin login(@RequestBody Admin admin) {
        System.out.println(adminRepository.findByUsernameAndPassword(admin.getUsername(), admin.getPassword()).toString());
        return adminRepository.findByUsernameAndPassword(admin.getUsername(), admin.getPassword());
    }
}
