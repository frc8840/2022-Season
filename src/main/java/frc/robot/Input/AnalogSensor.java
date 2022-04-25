package frc.robot.Input;

import edu.wpi.first.wpilibj.AnalogInput;

class AnalogSensor {
    private final AnalogInput analogInput;

    /**
     * Creates a new analog sensor.
     * @param port The port the sensor is plugged into.
     * */
    public AnalogSensor(int port) {
        analogInput = new AnalogInput(port);
    }

    /**
     * Gets the value of the sensor. Meant to be overridden.
     * @return The value of the sensor.
     * */
    public double getValue() {
        return this.getRawValue();
    }

    /**
     * Gets the raw value of the sensor.
     * @return The raw value of the sensor.
     * */
    public double getRawValue() {
        return analogInput.getValue();
    }

    /**
     * Gets the voltage of the sensor.
     * @return The voltage of the sensor.
     * */
    public double getVoltage() {
        return analogInput.getVoltage();
    }
}