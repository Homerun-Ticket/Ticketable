package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.domain.stadium.dto.request.StadiumCreateRequest;
import com.example.ticketable.domain.stadium.dto.response.StadiumCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;

@Service
@RequiredArgsConstructor
public class StadiumService {
    private final StadiumRepository stadiumRepository;

    public StadiumCreateResponse createStadium(StadiumCreateRequest request) {
        Stadium stadium = stadiumRepository.save(
                Stadium.builder()
                        .name(request.getName())
                        .location(request.getLocation())
                        .capacity(0)
                        .build()
        );
        return StadiumCreateResponse.of(stadium);
    }


    public StadiumGetResponse getStadium(Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));
    }
}
