package com.ensaj.geolocation.controllers;

import com.ensaj.geolocation.beans.Position;
import com.ensaj.geolocation.beans.User;
import com.ensaj.geolocation.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("positions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PositionController {
    @Autowired
    PositionRepository positionRepository;

    @PostMapping("/last")
    public Position findAllPositionsByUser(@RequestBody User user) {
        return positionRepository.findFirstByUserOrderByIdDesc(user);
    }

    @PostMapping("/save")
    public boolean savePosition(@RequestBody Position position){
        try {
            positionRepository.save(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DeleteMapping("/edit")
    public boolean updatePosition(@RequestBody Position position) {
        try {
            positionRepository.save(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DeleteMapping("/delete")
    public boolean deletePosition(@RequestBody Position position) {
        try {
            positionRepository.delete(position);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
