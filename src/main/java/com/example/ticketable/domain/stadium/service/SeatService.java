package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.dto.request.SeatCreateRequest;
import com.example.ticketable.domain.stadium.dto.response.SeatCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<SeatCreateResponse> createSeats(Long sectionId, SeatCreateRequest request) {
        Section section = sectionService.getById(sectionId);

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
        List<SeatCreateResponse> seatList = new ArrayList<>();
        for (int i = 1; i <= colNums.size(); i++) {
            for (int j = 0; j < colNums.get(i-1).size(); j++) {
                Seat seat = seatRepository.save(
                        Seat.builder()
                                .position(i+"열 "+ colNums.get(i-1).get(j))
                                .isBlind(isBlind.get(i-1).get(j))
                                .section(section)
                                .build()
                );
                seatList.add(SeatCreateResponse.of(seat));
            }
        }
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

    // PRICE
}
