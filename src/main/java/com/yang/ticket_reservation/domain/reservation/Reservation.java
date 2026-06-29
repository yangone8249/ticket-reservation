package com.yang.ticket_reservation.domain.reservation;

import com.yang.ticket_reservation.domain.seat.Seat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Seat seat;

    private String reserverName;

    private LocalDateTime reservedAt;

    public Reservation(Seat seat, String reserverName) {
        this.seat = seat;
        this.reserverName = reserverName;
        this.reservedAt = LocalDateTime.now();
    }
}
