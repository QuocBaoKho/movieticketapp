package com.movieticketsystem.movieticketapp.repository;

import com.movieticketsystem.movieticketapp.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    // Basic CRUD operations are inherited
}
