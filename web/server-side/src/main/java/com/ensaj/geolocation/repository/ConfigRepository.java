package com.ensaj.geolocation.repository;

import com.ensaj.geolocation.beans.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {

}
