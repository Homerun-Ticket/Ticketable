package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.TicketSeat;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface TicketSeatRepository extends JpaRepository<TicketSeat, Long> {

	@Query("select ts "
		+ "   from TicketSeat ts join fetch ts.seat "
		+ "  where ts.ticket.id = :ticketId "
		+ "    and ts.ticket.deletedAt is null ")
	List<TicketSeat> findByTicketIdWithSeat(Long ticketId);

	//@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<TicketSeat> findAllByGameIdAndSeatIdInAndTicketDeletedAtIsNull(Long gameId, List<Long> seatIds);

	void deleteAllByTicketId(Long ticketId);
}
