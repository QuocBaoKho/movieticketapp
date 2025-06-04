package com.movieticketsystem.movieticketapp.controller;

import com.movieticketsystem.movieticketapp.model.Movie;
import com.movieticketsystem.movieticketapp.model.Seat;
import com.movieticketsystem.movieticketapp.model.ShowTime;
import com.movieticketsystem.movieticketapp.model.Ticket;
import com.movieticketsystem.movieticketapp.repository.MovieRepository;
import com.movieticketsystem.movieticketapp.repository.ShowTimeRepository;
import com.movieticketsystem.movieticketapp.service.BookingService;
import com.movieticketsystem.movieticketapp.service.SeatManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MovieController {

    private final MovieRepository movieRepository;
    private final ShowTimeRepository showTimeRepository;
    private final SeatManager seatManager;
    private final BookingService bookingService;

    @Autowired
    public MovieController(MovieRepository movieRepository, ShowTimeRepository showTimeRepository,
                           SeatManager seatManager, BookingService bookingService) {
        this.movieRepository = movieRepository;
        this.showTimeRepository = showTimeRepository;
        this.seatManager = seatManager;
        this.bookingService = bookingService;
    }

    /**
     * Displays the list of all movies.
     */
    @GetMapping
    public String listMovies(Model model) {
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        return "index"; // Renders src/main/resources/templates/index.html
    }

    /**
     * Displays showtimes for a selected movie.
     */
    @GetMapping("/movie/{movieId}")
    public String listShowTimes(@PathVariable Integer movieId, Model model) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie ID: " + movieId));
        List<ShowTime> showTimes = showTimeRepository.findByMovieMovieID(movieId);

        model.addAttribute("movie", movie);
        model.addAttribute("showTimes", showTimes);
        return "movie-details"; // Renders src/main/resources/templates/movie-details.html
    }

    /**
     * Displays seats for a selected showtime.
     * Seats are grouped by row for better display.
     */
    @GetMapping("/showtime/{showTimeId}")
    public String showSeats(@PathVariable Integer showTimeId, Model model) {
        ShowTime showTime = showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid showtime ID: " + showTimeId));

        // Get seats with their current availability status from SeatManager (in-memory)
        Map<Integer, SeatManager.SeatStatus> seatsWithStatus = seatManager.getSeatsWithStatusForShowTime(showTimeId);

        // Group seats by row for display
        Map<String, List<SeatManager.SeatStatus>> seatsByRow = seatsWithStatus.values().stream()
                .sorted((s1, s2) -> {
                    // Sort first by row (alphabetical), then by seat number
                    int rowCompare = s1.getSeat().getSeatRow().compareTo(s2.getSeat().getSeatRow());
                    if (rowCompare != 0) {
                        return rowCompare;
                    }
                    return s1.getSeat().getSeatNumber().compareTo(s2.getSeat().getSeatNumber());
                })
                .collect(Collectors.groupingBy(s -> s.getSeat().getSeatRow()));

        model.addAttribute("showTime", showTime);
        model.addAttribute("seatsByRow", seatsByRow);
        return "showtime"; // Renders src/main/resources/templates/showtime.html
    }

    /**
     * Handles the booking of a seat.
     */
    @PostMapping("/book")
    public String bookSeat(@RequestParam Integer showTimeId,
                           @RequestParam Integer seatId,
                           @RequestParam String customerName,
                           RedirectAttributes redirectAttributes) {
        try {
            Ticket bookedTicket = bookingService.bookSeat(showTimeId, seatId, customerName);
            if (bookedTicket != null) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Booking successful! Ticket ID: " + bookedTicket.getTicketID() +
                                " for seat " + bookedTicket.getSeat().getSeatRow() + bookedTicket.getSeat().getSeatNumber());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Seat is already booked or an error occurred.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error during booking: " + e.getMessage());
            e.printStackTrace(); // Log the exception for debugging
        }
        return "redirect:/showtime/" + showTimeId; // Redirect back to the showtime page
    }

    /**
     * Handles the cancellation of a ticket.
     */
    @PostMapping("/cancel")
    public String cancelTicket(@RequestParam Integer ticketId,
                               @RequestParam Integer showTimeId, // Need showTimeId to redirect back
                               RedirectAttributes redirectAttributes) {
        try {
            boolean canceled = bookingService.cancelTicket(ticketId);
            if (canceled) {
                redirectAttributes.addFlashAttribute("successMessage", "Ticket ID " + ticketId + " successfully canceled.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel ticket ID " + ticketId + ". It might be already canceled or not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error during cancellation: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/showtime/" + showTimeId; // Redirect back to the showtime page
    }
}
