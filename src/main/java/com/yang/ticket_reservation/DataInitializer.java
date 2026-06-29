package com.yang.ticket_reservation;

import com.yang.ticket_reservation.domain.concert.Concert;
import com.yang.ticket_reservation.domain.concert.ConcertRepository;
import com.yang.ticket_reservation.domain.seat.Seat;
import com.yang.ticket_reservation.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        if (concertRepository.count() > 0) {
            return;
        }

        Concert concert = concertRepository.save(new Concert("테스트 콘서트", LocalDateTime.now().plusDays(7)));
        seatRepository.save(new Seat(concert, "A1"));
    }
}
