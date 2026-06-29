package com.yang.ticket_reservation.domain.queue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WaitingQueueService {

	private final StringRedisTemplate redisTemplate;

	public void register(Long concertId, String userId) {
		String key = queueKey(concertId);
		double score = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(key, userId, score);
	}

	public Long getRank(Long concertId, String userId) {
		String key = queueKey(concertId);
		Long rank = redisTemplate.opsForZSet().rank(key, userId);
		return rank == null ? null : rank + 1;
	}

	private String queueKey(Long concertId) {
		return "waiting-queue:" + concertId;
	}
}
