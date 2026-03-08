# Flyweight — Deduplicate Map Marker Styles (Refactoring)

## Overview

This repository demonstrates the refactoring of a map rendering application (**GeoDash**) using the **Flyweight Design Pattern**. The application generates and attempts to render thousands of map markers.

## Design Before Refactoring

Initially, the application was designed such that every `MapMarker` stored its own private duplicate of `MarkerStyle`.
- **Inefficiency**: Creating 30,000 map markers meant instantiating 30,000 identical instances of `MarkerStyle`.
- **Issue**: Extrinsic state (location and label) was bundled indiscriminately with intrinsic state (visual appearance). This leads to massive memory overhead, especially when you have a small, bounded set of possible styles (e.g. shapes, colors).

## Refactoring Process & The Flyweight Pattern

To solve the memory explosion, we implemented the Flyweight Pattern to deduplicate and share instances of the `MarkerStyle` object.

### 1. Extracting Intrinsic State into Immutable Flyweight
The intrinsic state across all markers—their visual style configuration (`shape`, `color`, `size`, `filled`)—was isolated in `MarkerStyle`. We ensured this object is **immutable** by marking all fields `final` and removing all setter methods.

### 2. Implementing the Flyweight Factory
We created `MarkerStyleFactory` to serve as a cache for our style objects. When a specific style is requested (using a unique key like `"PIN|RED|12|F"`), the Factory checks its `HashMap` cache. If the instance exists, it returns it; otherwise, it allocates a new `MarkerStyle`, caches it, and returns it.

### 3. Updating Map Data Source and Marker
- `MapMarker` was refactored. We stripped the individual style properties from its constructor and instead made it hold a reference to the shared `MarkerStyle` (the Flyweight) along with its specific extrinsic state (`lat`, `lng`, `label`).
- `MapDataSource` was updated to use `MarkerStyleFactory` during the marker generation loop. Instead of `new MarkerStyle(...)` for each marker, it now fetches and reuses the cached styles from the factory.

## Design After Refactoring

Through this refactoring, the number of distinct `MarkerStyle` objects in memory plummeted drastically. The rendering cost itself remains exactly the same, but memory footprint is vastly minimized.

- **Markers rendered**: 30,000 
- **Unique style objects produced pre-refactor**: 30,000
- **Unique style objects produced post-refactor**: $\le 96$ (because $3 \times 4 \times 4 \times 2 = 96$ total possible parameter combinations)

## Run the Code

To run the application and execute the verification check:

```bash
cd flyweight-markers/src
javac com/example/map/*.java
java com.example.map.App
# Validation Check
java com.example.map.QuickCheck
```
