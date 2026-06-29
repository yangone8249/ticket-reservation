package com.yang.ticket_reservation.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertRepository concertRepository;

    @GetMapping
    public List<ConcertResponse> concerts() {
        return concertRepository.findAll().stream()
                .map(concert -> new ConcertResponse(concert.getId(), concert.getTitle()))
                .toList();
    }

    public record ConcertResponse(Long id, String title) {
    }
}
