package com.example.ticketable.common.util;

import static com.example.ticketable.common.exception.ErrorCode.*;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.auction.dto.event.BidUpdateEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionBidRedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	private static final Duration AUCTION_BID_TTL = Duration.ofHours(24);

	public void createBidKey(Auction auction) {
		String key = buildKey(auction.getId());
		redisTemplate.opsForValue().set(key, String.valueOf(auction.getBidPoint()), AUCTION_BID_TTL);
	}

	public Integer getBidPoint(Long auctionId) {
		String value = redisTemplate.opsForValue().get(buildKey(auctionId));
		if (value == null) return null;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void validateBid(Long auctionId, int currentBid) {
		String key = buildKey(auctionId);
		String cachedBid = redisTemplate.opsForValue().get(key);
		if (cachedBid == null || !cachedBid.equals(String.valueOf(currentBid))) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}
	}

	// 기존 트랜잭션 커밋 이후 실행.
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void updateBidKey(BidUpdateEvent event) {
		String key = buildKey(event.getAuctionId());
		redisTemplate.opsForValue().set(key, String.valueOf(event.getNextBid()));
	}


	public void deleteBidKey(Long auctionId) {
		String key = buildKey(auctionId);
		redisTemplate.delete(key);
	}

	private static String buildKey(Long auction) {
		return "auction:" + auction + ":bid";
	}
}