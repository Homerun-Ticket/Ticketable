package com.example.ticketable.domain.stadium.repository;


import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

        @Query("SELECT new com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse( " +
            "sn.code, COUNT(st.id)) " +
            "FROM Section sn " +
            "JOIN Seat st ON sn.id = st.section.id " +
            "LEFT JOIN TicketSeat ts ON st.id = ts.seat.id " +
            "WHERE sn.stadium.id = :stadiumId " +
            "AND ts.id IS NULL " +
            "GROUP BY sn.code, sn.type " +
            "HAVING sn.type = :type"
        )
        List<SectionSeatCountResponse> findSectionSeatCountsBySectionId(@Param("stadiumId") Long stadiumId, @Param("type") String type);

    boolean existsByCodeAndStadium(String code, Stadium stadium);
}

