package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.dto.request.StadiumCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.StadiumUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.StadiumCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumUpdateResponse;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                        .imagePath("이미지 주소")
                        .build()
        );
        return StadiumCreateResponse.of(stadium);
    }


    public StadiumGetResponse getStadiumDto(Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));

        // 좌석 정보 추가 예정
        return StadiumGetResponse.of(stadium);
    }

    public StadiumUpdateResponse updateStadium(Long stadiumId, StadiumUpdateRequest request) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));

        stadium.updateName(request.getName());
        stadium.updateImagePath("새로운 이미지 경로");

        return StadiumUpdateResponse.of(stadium);
    }

    public void deleteStadium(Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));
        stadium.delete();
    }

    public Stadium getStadium(Long stadiumId) {
         return stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));
    }
}
