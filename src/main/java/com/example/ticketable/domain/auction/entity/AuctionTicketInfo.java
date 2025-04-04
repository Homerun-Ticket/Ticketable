package com.example.ticketable.domain.auction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AuctionTicketInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer standardPoint;

    private String sectionInfo;

    private String seatInfo;

    private Integer seatCount;

    private Boolean isTogether;

    @Builder
    public AuctionTicketInfo(
        Integer standardPoint, String sectionInfo, String seatInfo, Integer seatCount, Boolean isTogether
    ) {
        this.standardPoint = standardPoint;
        this.sectionInfo = sectionInfo;
        this.seatInfo = seatInfo;
        this.seatCount = seatCount;
        this.isTogether = isTogether;
    }
}
