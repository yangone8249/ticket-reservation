package com.yang.ticket_reservation.domain.seat;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;


@Component
@RequiredArgsConstructor
public class SeatLockService {

	private final StringRedisTemplate redisTemplate;

//	- Redis 분산락	
//	- Redis를 사용해서 락을 건다. 
//	- 현재 순차적 처리 로직에서는 (SET lock:seat:1 "locked" NX EX 5)을 사용한다.
//	lock:seat:1이라는 이름의 key를 가지고 경쟁한다.
//	"locked"은 그냥 개발자가 지어준 이름 (잠금상태를 확인하기위함이니 locked라고 표기
//	NX : 아무도 점유하지않았다면 성공 (True 반환), 누군가 점유하고있다면 실패 (False 반환)
//	EX 5 : 점유성공한 녀석의 Lock이 5초동안 안풀리면 강제로 Lock 해제함 (무제한 Lock 상태를 막기위한 안전장치임) 
	
	public boolean tryLock(Long seatId) {
		System.out.println("tryLock() : 점유 시도");
		String key = lockKey(seatId);
		System.out.println("seatId : "+seatId);
		Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", Duration.ofSeconds(5));

		System.out.println("success 여부 : "+success);
		if(success) {
    		System.out.println("락 점유 성공!");
		}else {
    		System.out.println("락 점유 실패!");
		}
		
		
		return Boolean.TRUE.equals(success);
	}
	
	public void unlock(Long seatId) {
		redisTemplate.delete(lockKey(seatId));
		System.out.println("seatId : "+seatId+" 번 좌석 락 해제");
	}
	
	private String lockKey(Long seatId) {
		return "lock:seat:"+seatId;
	}
	
	public boolean tryLockWithRetry(Long seatId, int maxRetries, long retryIntervalMillis) {
		System.out.println("tryLockWithRetry() : 분산락 진입");
		for (int i=0; i<maxRetries; i++) {
			System.out.println("tryLock "+i+1+"번 시도...");
			if(tryLock(seatId)) {
				return true;
			}
			try {
				Thread.sleep(retryIntervalMillis);
			}catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}
}
