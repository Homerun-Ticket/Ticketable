package com.example.ticketable.domain.stadium.repository;

import com.example.ticketable.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StadiumRepository extends JpaRepository<Stadium, Long> {


    boolean existsByName(String name);

}
