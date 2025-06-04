package com.movieticketsystem.movieticketapp.service;

import com.movieticketsystem.movieticketapp.model.Seat;
import com.movieticketsystem.movieticketapp.model.ShowTime;
import com.movieticketsystem.movieticketapp.model.Ticket;
import com.movieticketsystem.movieticketapp.repository.SeatRepository;
import com.movieticketsystem.movieticketapp.repository.ShowTimeRepository;
import com.movieticketsystem.movieticketapp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookingService {

    private final SeatManager seatManager;
    private final TicketRepository ticketRepository;
    private final ShowTimeRepository showTimeRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public BookingService(SeatManager seatManager, TicketRepository ticketRepository,
                          ShowTimeRepository showTimeRepository, SeatRepository seatRepository) {
        this.seatManager = seatManager;
        this.ticketRepository = ticketRepository;
        this.showTimeRepository = showTimeRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Attempts to book a seat for a given showtime.
     * This method handles the in-memory seat availability check and update,
     * and then persists the ticket to the database.
     *
     * @param showTimeId The ID of the showtime.
     * @param seatId The ID of the seat.
     * @param customerName The name of the customer booking the ticket.
     * @return The created Ticket object if successful, null otherwise.
     */
    @Transactional // Ensures atomicity for database operations
    public Ticket bookSeat(Integer showTimeId, Integer seatId, String customerName) {
        // 1. Try to book the seat in memory using SeatManager (atomic operation)
        if (!seatManager.tryBookSeat(showTimeId, seatId)) {
            System.out.println("Seat " + seatId + " for showtime " + showTimeId + " is already booked or not found.");
            return null; // Seat is not available or doesn't exist
        }

        // 2. If in-memory booking is successful, proceed with database operations
        Optional<ShowTime> showTimeOpt = showTimeRepository.findById(showTimeId);
        Optional<Seat> seatOpt = seatRepository.findById(seatId);

        if (showTimeOpt.isEmpty() || seatOpt.isEmpty()) {
            // This should ideally not happen if SeatManager is initialized correctly
            // but as a fallback, release the in-memory seat if DB lookup fails.
            seatManager.tryCancelSeat(showTimeId, seatId);
            System.err.println("Error: Showtime or Seat not found in DB after in-memory booking. Releasing seat.");
            return null;
        }

        ShowTime showTime = showTimeOpt.get();
        Seat seat = seatOpt.get();

        // 3. Create and save the Ticket
        Ticket ticket = new Ticket(showTime, seat, customerName, showTime.getMovie().getPrice());
        ticketRepository.save(ticket);

        // 4. Update available seats count in ShowTime (in DB)
        // This update is also part of the transaction.
        showTime.setAvailableSeats(showTime.getAvailableSeats() - 1);
        showTimeRepository.save(showTime);

        System.out.println("Successfully booked seat " + seat.getSeatRow() + seat.getSeatNumber() +
                " for movie '" + showTime.getMovie().getTitle() + "' at " + showTime.getShowTimeDateTime());
        return ticket;
    }

    /**
     * Attempts to cancel a ticket.
     * This method updates the in-memory seat availability and the ticket status in the database.
     *
     * @param ticketId The ID of the ticket to cancel.
     * @return true if the ticket was successfully canceled, false otherwise.
     */
    @Transactional
    public boolean cancelTicket(Integer ticketId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isEmpty()) {
            System.out.println("Ticket with ID " + ticketId + " not found.");
            return false;
        }

        Ticket ticket = ticketOpt.get();
        if (ticket.getIsCanceled()) {
            System.out.println("Ticket with ID " + ticketId + " is already canceled.");
            return false;
        }

        Integer showTimeId = ticket.getShowTime().getShowTimeID();
        Integer seatId = ticket.getSeat().getSeatID();

        // 1. Try to cancel the seat in memory using SeatManager (atomic operation)
        if (!seatManager.tryCancelSeat(showTimeId, seatId)) {
            System.err.println("Failed to release in-memory seat " + seatId + " for showtime " + showTimeId);
            return false; // Should ideally not happen if ticket was valid and booked
        }

        // 2. Update ticket status in database
        ticket.setIsCanceled(true);
        ticketRepository.save(ticket);

        // 3. Update available seats count in ShowTime (in DB)
        ShowTime showTime = ticket.getShowTime();
        showTime.setAvailableSeats(showTime.getAvailableSeats() + 1);
        showTimeRepository.save(showTime);

        System.out.println("Successfully canceled ticket " + ticketId + " for seat " +
                ticket.getSeat().getSeatRow() + ticket.getSeat().getSeatNumber());
        return true;
    }
}