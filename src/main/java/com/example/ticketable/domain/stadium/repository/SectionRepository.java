package com.example.ticketable.domain.stadium.repository;


import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SectionRepository extends JpaRepository<Section, Long> {


    boolean existsByCodeAndStadium(String code, Stadium stadium);
}

