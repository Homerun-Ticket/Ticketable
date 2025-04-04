package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.dto.request.StadiumCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.StadiumUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumUpdateResponse;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumService {
    private final StadiumRepository stadiumRepository;

    @Transactional
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
        List<SectionTypeSeatCountResponse> sectionSeatCounts = stadiumRepository.findSectionTypeAndSeatCountsByStadiumId(stadiumId);

        return StadiumGetResponse.of(stadium, sectionSeatCounts);
    }

    @Transactional
    public StadiumUpdateResponse updateStadium(Long stadiumId, StadiumUpdateRequest request) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));

        stadium.updateName(request.getName());
        stadium.updateImagePath("새로운 이미지 경로");

        return StadiumUpdateResponse.of(stadium);
    }

    @Transactional
    public void deleteStadium(Long stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));
        stadium.delete();
    }

    public Stadium getStadium(Long stadiumId) {
         return stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));
    }
}
