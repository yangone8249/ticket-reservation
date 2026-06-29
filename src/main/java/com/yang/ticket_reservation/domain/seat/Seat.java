package com.yang.ticket_reservation.domain.seat;

import com.yang.ticket_reservation.domain.concert.Concert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Concert concert;

    private String seatNumber;
    
    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    public Seat(Concert concert, String seatNumber) {
        this.concert = concert;
        this.seatNumber = seatNumber;
        this.status = SeatStatus.AVAILABLE;
    }

    public void reserve() {
        if (this.status == SeatStatus.RESERVED) {
            throw new IllegalStateException("이미 예약된 좌석입니다. seatId=" + id);
        }
        this.status = SeatStatus.RESERVED;
    }
}
