package com.movieticketsystem.movieticketapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ShowTimes")
public class ShowTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShowTimeID")
    private Integer showTimeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MovieID", nullable = false)
    private Movie movie;

    @Column(name = "ShowTimeDateTime", nullable = false)
    private LocalDateTime showTimeDateTime;

    @Column(name = "TheaterNumber", nullable = false)
    private Integer theaterNumber;

    @Column(name = "TotalSeats", nullable = false)
    private Integer totalSeats;

    @Column(name = "AvailableSeats", nullable = false)
    private Integer availableSeats; // This will be updated by the application

    public ShowTime() {
    }

    public ShowTime(Movie movie, LocalDateTime showTimeDateTime, Integer theaterNumber, Integer totalSeats, Integer availableSeats) {
        this.movie = movie;
        this.showTimeDateTime = showTimeDateTime;
        this.theaterNumber = theaterNumber;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    // Getters and Setters
    public Integer getShowTimeID() {
        return showTimeID;
    }

    public void setShowTimeID(Integer showTimeID) {
        this.showTimeID = showTimeID;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public LocalDateTime getShowTimeDateTime() {
        return showTimeDateTime;
    }

    public void setShowTimeDateTime(LocalDateTime showTimeDateTime) {
        this.showTimeDateTime = showTimeDateTime;
    }

    public Integer getTheaterNumber() {
        return theaterNumber;
    }

    public void setTheaterNumber(Integer theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
}