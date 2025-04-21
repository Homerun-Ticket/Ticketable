package com.example.ticketable.domain.game.repository;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.entity.QGame;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.entity.QSeat;
import com.example.ticketable.domain.stadium.entity.QSection;
import com.example.ticketable.domain.stadium.entity.QStadium;
import com.example.ticketable.domain.ticket.entity.QTicket;
import com.example.ticketable.domain.ticket.entity.QTicketSeat;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class GameRepositoryQueryImpl implements GameRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Game> findGames(String team, LocalDateTime start, LocalDateTime end) {
        QGame game = QGame.game;

        return jpaQueryFactory
                .selectFrom(game)
                .where(
                        game.startTime.gt(LocalDateTime.now()),
                        game.ticketingStartTime.loe(LocalDateTime.now()),
                        start == null ? null : game.startTime.goe(start),
                        end == null ? null : game.startTime.loe(end),
                        team == null ? null : game.home.eq(team)
                )
                .fetch();
    }
}
