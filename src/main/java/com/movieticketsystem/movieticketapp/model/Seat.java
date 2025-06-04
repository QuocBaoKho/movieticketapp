package com.movieticketsystem.movieticketapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Seats", uniqueConstraints = @UniqueConstraint(columnNames = {"ShowTimeID", "SeatRow", "SeatNumber"}))
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SeatID")
    private Integer seatID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ShowTimeID", nullable = false)
    private ShowTime showTime;

    @Column(name = "SeatRow", nullable = false)
    private String seatRow;

    @Column(name = "SeatNumber", nullable = false)
    private Integer seatNumber;

    // Note: SeatStatus (isBooked) will be managed in-memory by SeatManager using AtomicBoolean
    // This column is not directly mapped to the database for concurrency reasons as per requirements.
    // The database will reflect actual bookings via the Tickets table.

    public Seat() {
    }

    public Seat(ShowTime showTime, String seatRow, Integer seatNumber) {
        this.showTime = showTime;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
    }

    // Getters and Setters
    public Integer getSeatID() {
        return seatID;
    }

    public void setSeatID(Integer seatID) {
        this.seatID = seatID;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    // Helper method to get a unique identifier for a seat within a showtime
    public String getUniqueSeatIdentifier() {
        return showTime.getShowTimeID() + "-" + seatRow + seatNumber;
    }
}
