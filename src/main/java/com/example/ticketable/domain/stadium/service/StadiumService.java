package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.common.service.ImageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StadiumService {
    private final StadiumRepository stadiumRepository;

    private final ImageService imageService;

    private static final String STADIUM_FOLDER = "stadium/";

    @Transactional
    public StadiumCreateResponse createStadium(StadiumCreateRequest request, MultipartFile file) {
        if(stadiumRepository.existsByName(request.getName())){
            throw new ServerException(ErrorCode.STADIUM_NAME_DUPLICATION);
        }
        String originalFilename = file.getOriginalFilename();
        String fileKey = STADIUM_FOLDER + UUID.randomUUID()+ "_" + originalFilename;
        String imagePath = imageService.saveFile(file, fileKey);
        try {
            Stadium stadium = stadiumRepository.save(
                    Stadium.builder()
                            .name(request.getName())
                            .location(request.getLocation())
                            .capacity(0)
                            .imagePath(imagePath)
                            .build()
            );
            return StadiumCreateResponse.of(stadium);
        } catch (ServerException e) {
            imageService.deleteFile(imagePath);
            throw e;
        }
    }


    @Transactional
    public StadiumUpdateResponse updateStadium(Long stadiumId, StadiumUpdateRequest request) {
        Stadium stadium = getStadium(stadiumId);

        if(stadiumRepository.existsByName(request.getName())){
            throw new ServerException(ErrorCode.STADIUM_NAME_DUPLICATION);
        }

        stadium.updateName(request.getName());
        stadium.updateImagePath("새로운 이미지 경로");

        return StadiumUpdateResponse.of(stadium);
    }

    @Transactional
    public void deleteStadium(Long stadiumId) {
        Stadium stadium = getStadium(stadiumId);
        stadium.delete();
        imageService.deleteFile(stadium.getImagePath());
    }

    public Stadium getStadium(Long stadiumId) {
         return stadiumRepository.findById(stadiumId).orElseThrow(()-> new ServerException(ErrorCode.STADIUM_NOT_FOUND));
    }

}
