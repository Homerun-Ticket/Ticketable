package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.TicketSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketSeatRepository extends JpaRepository<TicketSeat, Long> {

	@Query("select ts "
		+ "   from TicketSeat ts join fetch ts.seat "
		+ "  where ts.ticket.id = :ticketId "
		+ "    and ts.ticket.deletedAt is null ")
	List<TicketSeat> findByTicketIdWithSeat(Long ticketId);

	//@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	boolean existsByGameIdAndSeatIdInAndTicketDeletedAtIsNull(Long gameId, List<Long> seatIds);
}
