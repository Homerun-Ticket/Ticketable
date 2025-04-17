package com.example.ticketable.domain.auction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "auction_ticket_info", indexes = {
    @Index(name = "idx_isTogether_seatCount", columnList = "isTogether, seatCount")
})
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
