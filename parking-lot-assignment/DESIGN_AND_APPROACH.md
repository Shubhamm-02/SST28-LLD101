# Multi-Level Parking Lot - Design and Approach

The multi-level parking lot uses highly decoupled entities and embraces robust **Object-Oriented Design (OOD)** and **SOLID principles**. Here is a breakdown of the architectural choices:

## Core Principles & Patterns Applied

1. **Strategy Pattern:**
    * **`SlotAssignmentStrategy`**: The system outsources the complex logic of finding the proper parking slot depending on structural constraints (where the gates and slots are physically located). The `NearestEuclideanSlotStrategy` currently implements this via a 3D distance approach to find the nearest slot on the nearest floor. If requirements change (e.g., assigning EV vehicles to charging slots, or VIP members getting prime spots), it can be seamlessly supported by passing a new assignment strategy.
    * **`PricingStrategy`**: Fee calculation is decoupled through this interface. The `HourlyPricingStrategy` charges based on the `SlotType` and hours parked. Using this design, if the parking lot wants to switch to surge pricing or flat-rate fees in the future, the core system remains undisturbed.

2. **Open-Closed Principle (OCP)**: 
    * The main coordinating class, `ParkingLot`, does not need any modifications to support new pricing behaviors or assignment strategies. It merely coordinates between domain entities and their assigned strategies.

3. **Information Expert / Encapsulation**: 
    * Entities own their calculations. For instance, `Slot` inherently calculates its distance to `Gate` internally using 3D Euclidean representation `(floor level abstracted as a Z-axis with 10 units height difference)`. 

4. **Structural Simplicity**:
    * Instead of using massive God classes, standard domain primitives (like Enums `SlotType` and `VehicleType`) and basic POJOs ensure the physical states of the parking system (Gates, Slots, Tickets, and Vehicles) map effortlessly.

## Design Trade-off
The slot search logic operates in $O(N)$ linear time by traversing available slots. For highly scaled systems, maintaining an active state using Priority Queues/Min-Heaps map grouped by `SlotType` and physical gateways could optimize retrieval overhead to near $O(\log k)$ or $O(1)$.
