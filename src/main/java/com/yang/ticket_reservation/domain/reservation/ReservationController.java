package com.yang.ticket_reservation.domain.reservation;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats/{seatId}/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationResponse reserve(@PathVariable Long seatId, @RequestBody ReservationRequest request) {
        Long reservationId = reservationService.reserve(seatId, request.reserverName());
        return new ReservationResponse(reservationId);
    }

    public record ReservationRequest(@NotBlank String reserverName) {
    }

    public record ReservationResponse(Long reservationId) {
    }
}
