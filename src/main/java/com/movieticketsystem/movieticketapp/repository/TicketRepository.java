// src/main/java/com/movieticketsystem/movieticketapp/repository/TicketRepository.java
package com.movieticketsystem.movieticketapp.repository;

import com.movieticketsystem.movieticketapp.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    // Find a ticket by showtime and seat, ensuring it's not canceled
    Ticket findByShowTimeShowTimeIDAndSeatSeatIDAndIsCanceledFalse(Integer showTimeID, Integer seatID);
}