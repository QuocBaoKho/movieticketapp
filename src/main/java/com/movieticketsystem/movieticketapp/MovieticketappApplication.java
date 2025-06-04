package com.movieticketsystem.movieticketapp;

import com.movieticketsystem.movieticketapp.model.Movie;
import com.movieticketsystem.movieticketapp.model.Seat;
import com.movieticketsystem.movieticketapp.model.ShowTime;
import com.movieticketsystem.movieticketapp.repository.MovieRepository;
import com.movieticketsystem.movieticketapp.repository.SeatRepository;
import com.movieticketsystem.movieticketapp.repository.ShowTimeRepository;
import com.movieticketsystem.movieticketapp.service.SeatManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value; // Import Value annotation

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class MovieticketappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieticketappApplication.class, args);
	}

	/**
	 * CommandLineRunner to populate initial data into the database
	 * and initialize the in-memory SeatManager.
	 * This runner is conditional based on 'app.load-demo-data' property.
	 */
	@Bean
	public CommandLineRunner demoData(
			MovieRepository movieRepository,
			ShowTimeRepository showTimeRepository,
			SeatRepository seatRepository,
			SeatManager seatManager,
			@Value("${app.load-demo-data:true}") boolean loadDemoData) { // Inject property
		return args -> {
			if (!loadDemoData) {
				System.out.println("Skipping demo data loading as 'app.load-demo-data' is set to false.");
				return;
			}

			// Check if data already exists to prevent re-inserting on 'update' ddl-auto
			if (movieRepository.count() > 0 || showTimeRepository.count() > 0 || seatRepository.count() > 0) {
				System.out.println("Database already contains data. Skipping demo data loading.");
				// Re-initialize SeatManager with existing seats from DB if data exists
				seatRepository.findAll().forEach(seat -> seatManager.addSeat(seat.getShowTime().getShowTimeID(), seat));
				return;
			}


			System.out.println("Loading demo data...");
			// 1. Create Movies
			Movie movie1 = new Movie("Kẻ Cắp Mặt Trăng 4", "Hoạt hình, Hài", 95, new BigDecimal("120000.0"));
			Movie movie2 = new Movie("Lật Mặt 7: Một Điều Ước", "Hài, Tâm lý", 130, new BigDecimal("150000.0"));
			Movie movie3 = new Movie("Dune: Part Two", "Khoa học viễn tưởng, Phiêu lưu", 166, new BigDecimal(140000.0));

			movieRepository.save(movie1);
			movieRepository.save(movie2);
			movieRepository.save(movie3);

			// 2. Create ShowTimes
			// ShowTime 1 for movie1 (Kẻ Cắp Mặt Trăng 4)
			ShowTime showTime1 = new ShowTime(movie1, LocalDateTime.of(2025, 6, 4, 10, 0), 1, 100, 100);
			// ShowTime 2 for movie1 (Kẻ Cắp Mặt Trăng 4)
			ShowTime showTime2 = new ShowTime(movie1, LocalDateTime.of(2025, 6, 4, 14, 0), 1, 100, 100);
			// ShowTime 3 for movie2 (Lật Mặt 7)
			ShowTime showTime3 = new ShowTime(movie2, LocalDateTime.of(2025, 6, 4, 12, 30), 2, 80, 80);
			// ShowTime 4 for movie3 (Dune: Part Two)
			ShowTime showTime4 = new ShowTime(movie3, LocalDateTime.of(2025, 6, 4, 18, 0), 3, 120, 120);

			showTimeRepository.save(showTime1);
			showTimeRepository.save(showTime2);
			showTimeRepository.save(showTime3);
			showTimeRepository.save(showTime4);

			// 3. Initialize Seats for each ShowTime and populate SeatManager
			// For ShowTime 1 (Theater 1, 10x10 = 100 seats)
			for (char row = 'A'; row <= 'J'; row++) {
				for (int num = 1; num <= 10; num++) {
					Seat seat = new Seat(showTime1, String.valueOf(row), num);
					seatRepository.save(seat);
					seatManager.addSeat(showTime1.getShowTimeID(), seat);
				}
			}

			// For ShowTime 3 (Theater 2, 8x10 = 80 seats)
			for (char row = 'A'; row <= 'H'; row++) {
				for (int num = 1; num <= 10; num++) {
					Seat seat = new Seat(showTime3, String.valueOf(row), num);
					seatRepository.save(seat);
					seatManager.addSeat(showTime3.getShowTimeID(), seat);
				}
			}

			// You can add seats for showTime2 and showTime4 similarly if needed
			// For ShowTime 2 (Theater 1, 10x10 = 100 seats)
			for (char row = 'A'; row <= 'J'; row++) {
				for (int num = 1; num <= 10; num++) {
					Seat seat = new Seat(showTime2, String.valueOf(row), num);
					seatRepository.save(seat);
					seatManager.addSeat(showTime2.getShowTimeID(), seat);
				}
			}

			// For ShowTime 4 (Theater 3, 12x10 = 120 seats)
			for (char row = 'A'; row <= 'L'; row++) { // Assuming 12 rows
				for (int num = 1; num <= 10; num++) {
					Seat seat = new Seat(showTime4, String.valueOf(row), num);
					seatRepository.save(seat);
					seatManager.addSeat(showTime4.getShowTimeID(), seat);
				}
			}


			System.out.println("Sample data loaded and SeatManager initialized.");
		};
	}
}