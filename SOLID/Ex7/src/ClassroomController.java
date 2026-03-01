public class ClassroomController {
    private final DeviceRegistry reg;

    public ClassroomController(DeviceRegistry reg) { this.reg = reg; }

    public void startClass() {
        ProjectorDevice pj = reg.getDevice(ProjectorDevice.class);
        pj.powerOn();
        pj.connectInput("HDMI-1");

        LightsPanelDevice lights = reg.getDevice(LightsPanelDevice.class);
        lights.setBrightness(60);

        ACDevice ac = reg.getDevice(ACDevice.class);
        ac.setTemperatureC(24);

        AttendanceScannerDevice scan = reg.getDevice(AttendanceScannerDevice.class);
        System.out.println("Attendance scanned: present=" + scan.scanAttendance());
    }

    public void endClass() {
        System.out.println("Shutdown sequence:");
        reg.getDevice(ProjectorDevice.class).powerOff();
        reg.getDevice(LightsPanelDevice.class).powerOff();
        reg.getDevice(ACDevice.class).powerOff();
    }
}
