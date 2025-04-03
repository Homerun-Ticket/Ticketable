package com.example.ticketable.domain.stadium.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.dto.request.SectionCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SectionUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SectionCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionUpdateResponse;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Server;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;

    private final StadiumService stadiumService;

    public SectionCreateResponse createSection(Long stadiumId, SectionCreateRequest request) {
        Stadium stadium = stadiumService.getStadium(stadiumId);

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

    public SectionUpdateResponse updateSection(Long sectionId, SectionUpdateRequest request) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new ServerException(ErrorCode.SECTION_NOT_FOUND));

        section.updateType(request.getType());
        section.updateCode(request.getCode());
        section.updateExtraChange(request.getExtraCharge());

        return SectionUpdateResponse.of(section);
    }

    public void delete(Long sectionId) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new ServerException(ErrorCode.SECTION_NOT_FOUND));

        section.delete();
    }
}
