package frc.robot.Input;

import edu.wpi.first.wpilibj.RobotController;

public class UltrasonicSensor extends AnalogSensor {
    public UltrasonicSensor(int port) {
        super(port);
    }

    public double getValue(boolean metric) {
        double voltage_scale_value = 5 / RobotController.getVoltage5V();
        double distanceInCentimeters = this.getRawValue() * voltage_scale_value * 0.125;
        double distanceInInches = this.getRawValue() * voltage_scale_value * 0.0492;

        return metric ? distanceInCentimeters : distanceInInches;
    }

    public double getValue() {
        return getValue(false);
    }
}
