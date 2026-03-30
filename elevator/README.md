# Multi-Elevator Control System 🛗

An object-oriented software design for a scalable multi-elevator system in Java. The architecture manages external floor requests, internal panel inputs, weight constraints, and emergency protocols while adhering to SOLID principles.

## System Architecture

* **Strategy Pattern:** The assignment of an elevator to an external floor request is delegated to an `ElevatorSelectionStrategy`. This allows energy-efficient algorithms (like `ShortestSeekTimeStrategy`) to be swapped or upgraded without modifying the `ElevatorController`.
* **Centralized Dispatcher:** The `ElevatorController` acts as the central brain. Both `InternalPanel` and `ExternalPanel` route their requests through the controller, ensuring a single source of truth for concurrency and state management.
* **State Management:** Each `ElevatorCar` independently manages its state (`UP`, `DOWN`, `IDLE`, `MAINTENANCE`), current weight, and door status.
* **Design Trade-offs:** To maintain simplicity and avoid deadlock complexity, background thread event loops and request queues for each elevator were abstracted. The controller uses basic `synchronized` blocks for thread safety to handle concurrent button presses.

## How to Run

Compile and execute all classes using standard Java commands in the root directory:

```bash
javac *.java
java Main