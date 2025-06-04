package com.movieticketsystem.movieticketapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tickets", uniqueConstraints = @UniqueConstraint(columnNames = {"ShowTimeID", "SeatID"}))
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TicketID")
    private Integer ticketID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ShowTimeID", nullable = false)
    private ShowTime showTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SeatID", nullable = false)
    private Seat seat;

    @Column(name = "BookingTime")
    private LocalDateTime bookingTime;

    @Column(name = "CustomerName")
    private String customerName;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "IsCanceled")
    private Boolean isCanceled;

    public Ticket() {
    }

    public Ticket(ShowTime showTime, Seat seat, String customerName, BigDecimal price) {
        this.showTime = showTime;
        this.seat = seat;
        this.bookingTime = LocalDateTime.now();
        this.customerName = customerName;
        this.price = price;
        this.isCanceled = false;
    }

    // Getters and Setters
    public Integer getTicketID() {
        return ticketID;
    }

    public void setTicketID(Integer ticketID) {
        this.ticketID = ticketID;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(Boolean canceled) {
        isCanceled = canceled;
    }
}
