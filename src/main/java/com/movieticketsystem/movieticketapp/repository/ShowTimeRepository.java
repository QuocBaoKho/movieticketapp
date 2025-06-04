package com.movieticketsystem.movieticketapp.repository;

import com.movieticketsystem.movieticketapp.model.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Integer> {
    List<ShowTime> findByMovieMovieID(Integer movieID);
}