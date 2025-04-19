package com.example.ticketable.domain.game.repository;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PagedModel;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepositoryQuery {
    List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameIdV3(Long gameId);

    List<Game> findGamesV3(String team, LocalDateTime start, LocalDateTime end);

    List<SectionSeatCountResponse> findSectionSeatCountsBySectionIdV3(Long gameId, String type);
}
