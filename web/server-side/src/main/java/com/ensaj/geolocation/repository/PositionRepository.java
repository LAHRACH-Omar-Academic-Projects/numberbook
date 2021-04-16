package com.ensaj.geolocation.repository;

import com.ensaj.geolocation.beans.Position;
import com.ensaj.geolocation.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Position findFirstByUserOrderByIdDesc(User user);
}
