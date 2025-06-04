package com.movieticketsystem.movieticketapp.service;

import com.movieticketsystem.movieticketapp.model.Seat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Manages the in-memory state of seats for concurrent booking.
 * Uses ConcurrentHashMap to store seat availability (AtomicBoolean) for each showtime.
 * The key for the inner map is a unique seat identifier (e.g., "A1", "B5").
 */
@Component
public class SeatManager {

    // Outer map: ShowTimeID -> (Inner map: SeatID (from DB) -> AtomicBoolean (isBooked))
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, AtomicBoolean>> showtimeSeatAvailability;
    // Store actual Seat objects for quick lookup
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Seat>> showtimeSeats;


    public SeatManager() {
        this.showtimeSeatAvailability = new ConcurrentHashMap<>();
        this.showtimeSeats = new ConcurrentHashMap<>();
    }

    /**
     * Adds a seat to the manager, initializing its availability to true (available).
     * This should be called when showtimes and seats are loaded/created.
     *
     * @param showTimeId The ID of the showtime.
     * @param seat The Seat object.
     */
    public void addSeat(Integer showTimeId, Seat seat) {
        showtimeSeatAvailability.computeIfAbsent(showTimeId, k -> new ConcurrentHashMap<>())
                .put(seat.getSeatID(), new AtomicBoolean(false)); // Initially not booked
        showtimeSeats.computeIfAbsent(showTimeId, k -> new ConcurrentHashMap<>())
                .put(seat.getSeatID(), seat);
    }

    /**
     * Checks if a specific seat for a showtime is available.
     *
     * @param showTimeId The ID of the showtime.
     * @param seatId The ID of the seat.
     * @return true if the seat is available, false otherwise.
     */
    public boolean isSeatAvailable(Integer showTimeId, Integer seatId) {
        ConcurrentHashMap<Integer, AtomicBoolean> seats = showtimeSeatAvailability.get(showTimeId);
        if (seats == null) {
            return false; // Showtime not found
        }
        AtomicBoolean isBooked = seats.get(seatId);
        return isBooked != null && !isBooked.get(); // If exists and is not booked
    }

    /**
     * Attempts to book a seat for a specific showtime.
     * This operation is atomic.
     *
     * @param showTimeId The ID of the showtime.
     * @param seatId The ID of the seat.
     * @return true if the seat was successfully booked, false if it was already booked or not found.
     */
    public boolean tryBookSeat(Integer showTimeId, Integer seatId) {
        ConcurrentHashMap<Integer, AtomicBoolean> seats = showtimeSeatAvailability.get(showTimeId);
        if (seats == null) {
            return false; // Showtime not found
        }
        AtomicBoolean isBooked = seats.get(seatId);
        if (isBooked == null) {
            return false; // Seat not found for this showtime
        }
        // Atomically sets the value to true if the current value is false.
        return isBooked.compareAndSet(false, true);
    }

    /**
     * Attempts to cancel a seat for a specific showtime, making it available again.
     * This operation is atomic.
     *
     * @param showTimeId The ID of the showtime.
     * @param seatId The ID of the seat.
     * @return true if the seat was successfully canceled (made available), false otherwise.
     */
    public boolean tryCancelSeat(Integer showTimeId, Integer seatId) {
        ConcurrentHashMap<Integer, AtomicBoolean> seats = showtimeSeatAvailability.get(showTimeId);
        if (seats == null) {
            return false; // Showtime not found
        }
        AtomicBoolean isBooked = seats.get(seatId);
        if (isBooked == null) {
            return false; // Seat not found for this showtime
        }
        // Atomically sets the value to false if the current value is true.
        return isBooked.compareAndSet(true, false);
    }

    /**
     * Retrieves all seats for a given showtime, along with their current in-memory availability status.
     *
     * @param showTimeId The ID of the showtime.
     * @return A map of SeatID to its Seat object, with an added 'isBooked' status.
     */
    public Map<Integer, SeatStatus> getSeatsWithStatusForShowTime(Integer showTimeId) {
        ConcurrentHashMap<Integer, Seat> seatsInShowtime = showtimeSeats.get(showTimeId);
        ConcurrentHashMap<Integer, AtomicBoolean> availability = showtimeSeatAvailability.get(showTimeId);

        if (seatsInShowtime == null || availability == null) {
            return new ConcurrentHashMap<>(); // No seats or showtime found
        }

        return seatsInShowtime.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new SeatStatus(entry.getValue(), availability.get(entry.getKey()).get())
                ));
    }

    /**
     * Helper class to return Seat object along with its current booking status.
     */
    public static class SeatStatus {
        private final Seat seat;
        private final boolean isBooked;

        public SeatStatus(Seat seat, boolean isBooked) {
            this.seat = seat;
            this.isBooked = isBooked;
        }

        public Seat getSeat() {
            return seat;
        }

        public boolean isBooked() {
            return isBooked;
        }
    }
}