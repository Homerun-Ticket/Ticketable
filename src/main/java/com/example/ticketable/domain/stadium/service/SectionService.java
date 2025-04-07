package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.dto.request.SectionCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SectionUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SectionCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionUpdateResponse;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;

    private final StadiumService stadiumService;

    @Transactional
    public SectionCreateResponse createSection(Long stadiumId, SectionCreateRequest request) {
        Stadium stadium = stadiumService.getStadium(stadiumId);

        if(sectionRepository.existsByCodeAndStadium(request.getCode(), stadium)){
            throw new ServerException(ErrorCode.SECTION_CODE_DUPLICATION);
        }

        Section section = sectionRepository.save(
                Section.builder()
                        .type(request.getType())
                        .code(request.getCode())
                        .extraCharge(request.getExtraCharge())
                        .stadium(stadium)
                        .build()
        );
        return SectionCreateResponse.of(section);
    }

    @Transactional
    public SectionUpdateResponse updateSection(Long sectionId, SectionUpdateRequest request) {
        Section section = getById(sectionId);

        if(sectionRepository.existsByCodeAndStadium(request.getCode(), section.getStadium())) {
            throw new ServerException(ErrorCode.SECTION_CODE_DUPLICATION);
        }

        section.updateType(request.getType());
        section.updateCode(request.getCode());
        section.updateExtraChange(request.getExtraCharge());

        return SectionUpdateResponse.of(section);
    }

    @Transactional
    public void delete(Long sectionId) {
        Section section = getById(sectionId);
        section.delete();
    }

    public Section getById(Long sectionId) {
        return sectionRepository.findById(sectionId).orElseThrow(() -> new ServerException(ErrorCode.SECTION_NOT_FOUND));
    }
}
