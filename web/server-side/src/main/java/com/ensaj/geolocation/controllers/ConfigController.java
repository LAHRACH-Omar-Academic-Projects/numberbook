package com.ensaj.geolocation.controllers;

import com.ensaj.geolocation.beans.Config;
import com.ensaj.geolocation.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("config")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConfigController {
    @Autowired
    ConfigRepository configRepository;

    @GetMapping("/{id}")
    public Optional<Config> getConfig(@PathVariable int id) {
        return configRepository.findById(id);
    }

    @PostMapping("/save")
    public boolean saveConfig(@RequestBody Config config) {
        try {
            configRepository.save(config);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
