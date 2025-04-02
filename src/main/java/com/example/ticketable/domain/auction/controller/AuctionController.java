package com.example.ticketable.domain.auction.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.ticketable.domain.auction.service.AuctionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuctionController {

	private final AuctionService auctionService;


}
