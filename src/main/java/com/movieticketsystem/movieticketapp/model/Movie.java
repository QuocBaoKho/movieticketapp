package com.movieticketsystem.movieticketapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MovieID")
    private Integer movieID;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Genre")
    private String genre;

    @Column(name = "DurationMinutes")
    private Integer durationMinutes;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    public Movie() {
    }

    public Movie(String title, String genre, Integer durationMinutes, BigDecimal price) {
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    // Getters and Setters
    public Integer getMovieID() {
        return movieID;
    }

    public void setMovieID(Integer movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}