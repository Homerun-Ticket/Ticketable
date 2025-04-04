package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.dto.request.SeatCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SeatUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SeatCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatUpdateResponse;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    // CRUD
    private final SeatRepository seatRepository;

    private final SectionService sectionService;

    private final StadiumService stadiumService;

    @Transactional
    public List<SeatCreateResponse> createSeats(Long stadiumId, Long sectionId, SeatCreateRequest request) {
        Stadium stadium = stadiumService.getStadium(stadiumId);
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
        stadium.updateCapacity(sum);
        return seatList;
    }

    public List<SeatGetResponse> getSeats(Long sectionId) {
        List<Seat> seatList = seatRepository.findBySectionId(sectionId);
        List<Seat> unbookSeatList = seatRepository.findUnbookSeatsBySectionId(sectionId);

        Set<Long> unbookedSeatIds = new HashSet<>();
        for (Seat seat : unbookSeatList) {
                unbookedSeatIds.add(seat.getId());
        }

        List<SeatGetResponse> responseList = new ArrayList<>();
        for (Seat seat : seatList) {
            boolean isBooked = !unbookedSeatIds.contains(seat.getId());
            SeatGetResponse response = SeatGetResponse.of(seat, isBooked);
            responseList.add(response);
        }
        return responseList;
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
}
