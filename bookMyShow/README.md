# BookMyShow LLD Implementation

A simple Low-Level Design (LLD) project for a movie ticket booking system. This project demonstrates seat booking logic, ensuring consistency and preventing double-booking.

## Project Structure

The project is organized into the following packages:

- **`models`**: Contains the core data entities:
    - `Movie`: Id, name, and duration.
    - `Theatre`: Id, name, and location.
    - `Screen`: Represents a cinema screen with its total capacity.
    - `Show`: Represents a specific movie screening.
    - `Seat`: Represents an individual seat in a theater screen.
    - `Booking`: Represents a completed theater booking.

- **`service`**:
    - `BookingService`: Handles the logic for booking seats, utilizing a `Map` of booked seats for each show to ensure quick lookups and prevent duplicate bookings.

## How to Run

To compile and run the application, use the following command from the `bookMyShow` directory:

```bash
javac Main.java models/*.java service/*.java && java Main
```

## Features

- **Model Relationships**: Clear representation of Movies, Shows, and Screens within a Theater.
- **Booking Logic**: Simple and effective check for seat availability.
- **Testable Data**: Includes a `Main.java` entry point with pre-configured sample data for instant verification.

---
*Created as part of the LLD Assignments series.*
