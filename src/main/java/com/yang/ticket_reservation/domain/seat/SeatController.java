package com.yang.ticket_reservation.domain.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts/{concertId}/seats")
public class SeatController {

    private final SeatRepository seatRepository;

    @GetMapping
    public List<SeatResponse> seats(@PathVariable("concertId") Long concertId) {
        return seatRepository.findByConcertId(concertId).stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getStatus()))
                .toList();
    }

    public record SeatResponse(Long id, String seatNumber, SeatStatus status) {
    }
}
