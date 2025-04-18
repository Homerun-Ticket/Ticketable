package com.example.ticketable.domain.game.repository;

import com.example.ticketable.domain.game.entity.QGame;
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
}
