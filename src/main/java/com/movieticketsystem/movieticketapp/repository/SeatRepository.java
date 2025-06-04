package com.movieticketsystem.movieticketapp.repository;

import com.movieticketsystem.movieticketapp.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByShowTimeShowTimeID(Integer showTimeID);
    Optional<Seat> findByShowTimeShowTimeIDAndSeatRowAndSeatNumber(Integer showTimeID, String seatRow, Integer seatNumber);
}
