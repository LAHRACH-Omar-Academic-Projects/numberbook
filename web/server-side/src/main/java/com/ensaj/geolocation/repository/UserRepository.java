package com.ensaj.geolocation.repository;

import com.ensaj.geolocation.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByDeviceImei(String deviceImei);
    Optional<User> findByPhoneNumber(String telephone);
}
