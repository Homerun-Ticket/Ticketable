package com.example.ticketable.common.util;

import static com.example.ticketable.common.exception.ErrorCode.*;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.auction.entity.Auction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionBidRedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	private static final Duration AUCTION_BID_TTL = Duration.ofHours(24);

	public void createBidKey(Auction auction) {
		String key = "auction:" + auction.getId() + ":bid";
		redisTemplate.opsForValue().set(key, String.valueOf(auction.getBidPoint()), AUCTION_BID_TTL);
	}

	public void validateBid(Long auctionId, int currentBid) {
		String key = "auction:" + auctionId + ":bid";
		String cachedBid = redisTemplate.opsForValue().get(key);
		if (cachedBid == null || !cachedBid.equals(String.valueOf(currentBid))) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}
	}

	public void updateBidKey(Long auctionId, int nextBid) {
		String key = "auction:" + auctionId + ":bid";
		redisTemplate.opsForValue().set(key, String.valueOf(nextBid), AUCTION_BID_TTL);
	}


	public void deleteBidKey(Long auctionId) {
		String key = "auction:" + auctionId + ":bid";
		redisTemplate.delete(key);
	}
}