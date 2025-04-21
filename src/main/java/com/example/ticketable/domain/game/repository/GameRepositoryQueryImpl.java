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
    public List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameIdV3(Long gameId) {
        QSeat seat = QSeat.seat;
        QSection section = QSection.section;
        QStadium stadium = QStadium.stadium;
        QGame game = QGame.game;
        QTicketSeat ts = QTicketSeat.ticketSeat;
        QTicket ticket = QTicket.ticket;

        return jpaQueryFactory
                .select(Projections.constructor(
                        SectionTypeSeatCountResponse.class,
                        section.type,
                        new CaseBuilder()
                                .when(ticket.id.isNull().or(ticket.deletedAt.isNotNull()))
                                .then(1L)
                                .otherwise(0L)
                                .sum()
                ))
                .from(seat)
                .join(seat.section, section)
                .join(section.stadium, stadium)
                .join(game).on(game.stadium.eq(stadium))
                .leftJoin(ts).on(ts.seat.eq(seat))
                .leftJoin(ticket).on(ts.ticket.eq(ticket).and(ticket.game.id.eq(gameId)))
                .where(game.id.eq(gameId))
                .groupBy(section.type)
                .fetch();
    }

    @Override
    public List<Game> findGamesV3(String team, LocalDateTime start, LocalDateTime end) {
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

    @Override
    public List<SectionSeatCountResponse> findSectionSeatCountsBySectionIdV3(Long gameId, String type) {
        QSeat seat = QSeat.seat;
        QSection section = QSection.section;
        QStadium stadium = QStadium.stadium;
        QGame game = QGame.game;
        QTicketSeat ts = QTicketSeat.ticketSeat;
        QTicket ticket = QTicket.ticket;
        return jpaQueryFactory
                .select(Projections.constructor(
                        SectionSeatCountResponse.class,
                        section.code,
                        seat.id.count().subtract(ticket.id.count())
                ))
                .from(seat)
                .join(seat.section, section)
                .join(section.stadium, stadium)
                .join(game).on(game.stadium.eq(stadium))
                .leftJoin(ts).on(ts.seat.eq(seat))
                .leftJoin(ticket).on(ticket.eq(ts.ticket)
                        .and(ticket.game.eq(game))
                        .and(ticket.deletedAt.isNull()))
                .where(game.id.eq(gameId)
                        .and(section.type.eq(type)))
                .groupBy(section.id)
                .fetch();
    }

    @Override
    public List<SeatGetResponse> findSeatsWithBookingStatusBySectionIdAndGameIdV3(Long sectionId, Long gameId) {
        QSeat seat = QSeat.seat;
        QTicketSeat ts = QTicketSeat.ticketSeat;
        QTicket t = QTicket.ticket;
        return jpaQueryFactory
                .select(Projections.constructor(
                        SeatGetResponse.class,
                        seat.id,
                        seat.position,
                        seat.isBlind,
                        new CaseBuilder()
                                .when(t.id.count().gt(0L))
                                .then(true)
                                .otherwise(false)
                        ))
                .from(seat)
                .leftJoin(ts).on(ts.seat.eq(seat))
                .leftJoin(t).on(t.id.eq(ts.ticket.id)
                        .and(t.deletedAt.isNull())
                        .and(t.game.id.eq(gameId)))
                .where(seat.section.id.eq(sectionId))
                .groupBy(seat.id, seat.position, seat.isBlind)
                .fetch();
    }
}
