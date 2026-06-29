package com.yang.ticket_reservation.domain.reservation;

import com.yang.ticket_reservation.domain.seat.Seat;
import com.yang.ticket_reservation.domain.seat.SeatLockService;
import com.yang.ticket_reservation.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final SeatLockService seatLockService;
    
    
    // 예약하는 메서드 (reserve)
    @Transactional
    public Long reserve(Long seatId, String reserverName) {
    	
    	
    	if(!seatLockService.tryLockWithRetry(seatId, 3, 50)) {
    		throw new IllegalStateException("다른 요청이 처리중입니다. 잠시 후 다시 시도해주세요. "
    				+ "seatId="+seatId);
    	}
    	
	    	try {
	    	
			//    	- 낙관적 락 (조회는 기본으로 하되, 업데이트 시 version 칼럼의 값 변동을 인식해서 잠금 여부를 정한다.) 
			//    	- 약 0.9초 소요. (100VU)
			//    	- 하단의 reserve() 업데이트 시 version이 0 아닐 시 409에러 반환 후 업데이트 취소
			        Seat seat = seatRepository.findById(seatId)
			                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다. seatId=" + seatId));
			
			
			//    	- 비낙관적 락 (조회 시 내가 조회한 row에 대해 잠금상태로 놔둔다.) 
			//      - 약 11.2초 소요. (100VU)
			//    	- row 잠금 이후 update를 하고 해당 서비스(reserve)에 대한 트랜잭션이 종료되어야 락을 해제한다. 
			//        Seat seat = seatRepository.findByIdForUpdate(seatId)
			//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다. seatId=" + seatId));
	        
	        
	        seat.reserve();
    		System.out.println("좌석 예매 확정 상태로 변경");
    		
	        Reservation reservation = reservationRepository.save(new Reservation(seat, reserverName));

 
    		
	        return reservation.getId();

    	} finally {

    		System.out.println("unlock() : 진입 성공");
			seatLockService.unlock(seatId);
		}
    }
    
}
