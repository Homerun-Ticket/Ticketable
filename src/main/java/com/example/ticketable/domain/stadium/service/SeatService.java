package com.example.ticketable.domain.stadium.service;

import static com.example.ticketable.common.exception.ErrorCode.SEAT_NOT_FOUND;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.common.util.SeatHoldRedisUtil;
import com.example.ticketable.domain.stadium.dto.request.SeatCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SeatHoldRequest;
import com.example.ticketable.domain.stadium.dto.request.SeatUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SeatCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatUpdateResponse;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.SeatRepository;
import com.example.ticketable.domain.ticket.service.TicketSeatService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {
    // CRUD
    private final SeatRepository seatRepository;

    private final SectionService sectionService;

    private final SeatHoldRedisUtil seatHoldRedisUtil;

    private final TicketSeatService ticketSeatService;

    private final SeatValidator seatValidator;

    @Transactional
    public List<SeatCreateResponse> createSeats(Long sectionId, SeatCreateRequest request) {
        Section section = sectionService.getById(sectionId);

        if (seatRepository.existsBySectionId(sectionId)){
            throw new ServerException(ErrorCode.SEATS_ALREADY_EXISTS);
        }

        List<List<String>> colNums = request.getColNums();
        List<List<Boolean>> isBlind = request.getIsBlind();;

        // 일관성 검사
        if (colNums.size() != isBlind.size()) {
            throw new ServerException(ErrorCode.COLUMN_NUMS_AND_BLIND_STATUS_NOT_SAME_SIZE);
        }
        for (int i = 0; i < colNums.size(); i++) {
            if (colNums.get(i).size() != isBlind.get(i).size()) {
                throw new ServerException(ErrorCode.COLUMN_NUMS_AND_BLIND_STATUS_NOT_SAME_SIZE);
            }
        }

        int sum = 0;
        List<SeatCreateResponse> seatList = new ArrayList<>();
        for (int i = 0; i < colNums.size(); i++) {
            for (int j = 0; j < colNums.get(i).size(); j++) {
                Seat seat = seatRepository.save(
                        Seat.builder()
                                .position(i+1+"열 "+ colNums.get(i).get(j))
                                .isBlind(isBlind.get(i).get(j))
                                .section(section)
                                .build()
                );
                sum++;
                seatList.add(SeatCreateResponse.of(seat));
            }
        }
        section.getStadium().updateCapacity(sum);
        return seatList;
    }

    @Transactional
    public SeatUpdateResponse updateSeat(Long seatId, SeatUpdateRequest request) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new ServerException(ErrorCode.SEAT_NOT_FOUND));

        if (seat.isBlind() == request.isBlind()){
            throw new ServerException(ErrorCode.BLIND_STATUS_ALREADY_SET);
        }
        seat.updateBlind();

        return SeatUpdateResponse.of(seat);
    }

    @Transactional
    public void delete(Long seatId) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new ServerException(ErrorCode.SEAT_NOT_FOUND));

        seat.delete();
    }


    // PRICE
    public List<Seat> getAllSeatEntity(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllByIds(seatIds);
        if (seats.size() != seatIds.size()) {
            log.debug("요청한 좌석을 찾을 수 없습니다.");
            throw new ServerException(SEAT_NOT_FOUND);
        }
        return seats;
    }

    public void holdSeat(Auth auth, SeatHoldRequest seatHoldRequest) {
        seatValidator.validateSeatsBelongToGame(seatHoldRequest.getGameId(), seatHoldRequest.getSeatIds());
        ticketSeatService.checkDuplicateSeats(seatHoldRequest.getSeatIds(), seatHoldRequest.getGameId());
        seatHoldRedisUtil.holdSeatAtomic(seatHoldRequest.getSeatIds(), seatHoldRequest.getGameId(), String.valueOf(auth.getId()));
    }
}
