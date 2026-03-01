# SOLID Principles: Intuition and Implementation Guide

This guide breaks down the refactoring journey from Exercise 1 to 6, focusing on the intuition behind the changes and how to explain them to others.

---

## 1. Single Responsibility Principle (SRP) — Ex 1 & 2
*"A class should have one, and only one, reason to change."*

### The Intuition (The "Smell")
- **The "And" problem**: If you describe a class and use the word "and" multiple times (e.g., "This class validates students **and** saves them **and** prints the result"), it's violating SRP.
- **Divergent Change**: You find yourself editing the same file for completely different reasons (e.g., changing database logic today, changing UI formatting tomorrow).

### What we did (Ex 1 & 2)
We took "God Classes" (`OnboardingService`, `CafeteriaSystem`) and split them into specialized collaborators:
- **Validators**: Just for business rules.
- **Repositories**: Just for data persistence.
- **Presenters/Formatters**: Just for UI representation.
- **Coordinators**: High-level services that just "direct traffic" between these specialists.

### How to Explain it
> "I refactored this because the original class was doing too much. By separating validation, storage, and presentation, we've made the code more **modular**. Now, if our database changes from a File to a SQL DB, we only touch the `Repository`, leaving the validation logic completely untouched."

---

## 2. Open/Closed Principle (OCP) — Ex 3 & 4
*"Software entities should be open for extension, but closed for modification."*

### The Intuition (The "Smell")
- **The Switch/If-Else Chain**: If you see a long `switch` statement or `if-else` block that checks a type or a flag to decide what logic to run, it's an OCP violation.
- **Repeated Editing**: Every time you add a new feature (e.g., a new "Scholarship" rule or a new "Late Fee" type), you have to open the same core engine file and add another `case`.

### What we did (Ex 3 & 4)
We replaced the conditional logic with a **Plugin Architecture** (Strategy Pattern):
- **Interfaces**: Defined `EligibilityRule` and `FeeRule`.
- **Concrete Classes**: Each `if` branch became its own class (e.g., `CgrRule`, `LateFeeRule`).
- **The Engine**: The engine now just loops through a `List<Rule>`. It doesn't know *what* the rules are; it just knows how to call them.

### How to Explain it
> "I refactored the engine to be **extensible**. Instead of hardcoding rules inside an if-else block, I created a rule interface. Now, we can add a 10th or 11th rule by simply creating a new class. We don't have to touch the core engine, which reduces the risk of breaking existing logic."

---

## 3. Liskov Substitution Principle (LSP) — Ex 5 & 6
*"Subtypes must be substitutable for their base types."*

### The Intuition (The "Smell")
- **"Special Cases" in the Caller**: If the `Main` method has to use `try-catch` blocks for only ONE specific subclass, or if it has to check `instanceof`, LSP is likely broken.
- **Silent Failures/Changing Meaning**: If a subclass secretly changes the input (like truncating a message) or throws an unexpected exception for a valid input, it's breaking the parent's "promise."

### What we did (Ex 5 & 6)
We hardened the **Contract**:
- **Unified Result Types**: Instead of throwing exceptions for logical errors (like "Empty data"), we returned an `ExportResult` (Ex 5). This makes failure a predictable part of the contract.
- **Contractual Clarity**: We updated the base class documentation to state that exceptions are possible for invalid inputs, and we removed "hidden" behaviors like silent truncation (Ex 6).

### How to Explain it
> "LSP is about **trust**. I refactored the hierarchy so that the caller can treat every sender or exporter exactly the same way. By moving from 'random exceptions' to a 'Result' object or a clear validation contract, we've made the system more predictable and eliminated runtime surprises."

---

## Summary Table: How to choose the Refactor?

| Principle | See this... | Do this... |
| :--- | :--- | :--- |
| **SRP** | Massive files, mixed logic (DB + UI). | Extract logic into small, focused classes. |
| **OCP** | Growing `if-else` or `switch` chains. | Replace branches with Interface + many Classes. |
| **LSP** | One subclass crashes where others don't. | Adjust the parent contract or use Result objects. |

### Final Tip for Your Intuition
Ask yourself: **"If I want to change X, how many files do I have to open?"**
- If it's **one** file that's hard to read: You need **SRP**.
- If it's **the same** core file every time: You need **OCP**.
- If you're **afraid** to swap one class for another: You need **LSP**.
