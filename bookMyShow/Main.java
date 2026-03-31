import models.*;
import service.BookingService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 1. Setup sample data
        Movie movie = new Movie(1, "Inception", 148);
        Theatre theatre = new Theatre(1, "PVR Cinemas", "Mumbai");
        Screen screen = new Screen(1, 1, 100);
        Show show = new Show(1, 1, 1, "10:00 PM");

        BookingService bookingService = new BookingService();

        System.out.println("--- Welcome to BookMyShow ---");
        System.out.println("Movie: " + movie.name);
        System.out.println("Theatre: " + theatre.name + " (" + theatre.location + ")");
        System.out.println("Show Start: " + show.startTime);
        System.out.println("-----------------------------");

        // 2. Try booking seats
        List<Integer> seatsToBook = Arrays.asList(10, 11, 12);
        System.out.println("Booking seats: " + seatsToBook);
        
        boolean success1 = bookingService.bookSeats(show.id, seatsToBook);
        if (success1) {
            System.out.println("✅ Booking successful!");
        } else {
            System.out.println("❌ Booking failed!");
        }

        // 3. Try booking the same seats again
        System.out.println("\nTrying to book the same seats again: " + seatsToBook);
        boolean success2 = bookingService.bookSeats(show.id, seatsToBook);
        if (success2) {
            System.out.println("✅ Booking successful!");
        } else {
            System.out.println("❌ Booking failed! (Seats already booked)");
        }

        // 4. Try booking different seats
        List<Integer> differentSeats = Arrays.asList(14, 15);
        System.out.println("\nBooking different seats: " + differentSeats);
        boolean success3 = bookingService.bookSeats(show.id, differentSeats);
        if (success3) {
            System.out.println("✅ Booking successful!");
        } else {
            System.out.println("❌ Booking failed!");
        }
    }
}
