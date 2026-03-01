import java.util.*;

public class DeviceRegistry {
    private final java.util.List<SmartClassroomDevice> devices = new ArrayList<>();

    public void add(SmartClassroomDevice d) { devices.add(d); }

    public <T extends SmartClassroomDevice> T getDevice(Class<T> clazz) {
        for (SmartClassroomDevice d : devices) {
            if (clazz.isInstance(d)) {
                return clazz.cast(d);
            }
        }
        throw new IllegalStateException("Missing: " + clazz.getSimpleName());
    }
}
