# Proxy Pattern — Secure & Lazy-Load Reports (Refactoring)

## The Original Structure (Before)
Before the refactoring, the application was severely flawed regarding optimization and system security:
1. **No Access Restrictions**: Any user account could instantiate and view any report in the system, even restricted faculty or admin files.
2. **Eager & Expensive Loading**: The `ReportFile` class performed heavy simulated IO (disk reading) every single time `display()` was called immediately upon use.
3. **No Memory Caching**: There was no way to reuse reports. If a user opened the exact same report twice, the heavy disk load operation executed twice.

## The Proxy Architecture (After)
To mitigate these problems, we applied the **Proxy Design Pattern**. The Proxy acts as a lightweight surrogate or placeholder that controls access to the original object.

### Implementations Made:
1. **The Abstract Target (`Report` Interface)**: Established a common ground between the real resource and the proxy ensuring they can be used interchangeably by the client.
2. **The "Real Subject" (`RealReport`)**: Transferred the heavy lifting (`loadFromDisk()`) into this class. It represents the resource-intensive object that we want to protect and defer loading on.
3. **The "Proxy" (`ReportProxy`)**:
    * **Protection Proxy**: It leverages the existing `AccessControl` utility to verify `$classification` against the `User` role. If unauthorized, the flow is blocked entirely.
    * **Virtual Proxy (Lazy Loading)**: It maintains a null reference to `RealReport`. It only constructs it using `new RealReport(...)` if (and only if) the access check passes.
    * **Caching Strategy**: Once the `RealReport` is loaded, the proxy saves that reference. Subsequent `display()` queries intercept the call and simply hit the memory cache instead of hitting the disk again.
4. **Client Cleanup (`ReportViewer` & `App`)**: Dropped the old `ReportFile` structure. The application was updated to depend entirely on the `Report` abstraction, injecting `ReportProxy` instances at runtime.

By implementing this, unprivileged users are explicitly blocked, disk reads are heavily optimized, and clients maintain simple and clean interaction calls (`report.display(user)`).
