package frc.robot.utils;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;

public class MotorGroup {
    private PWMMotorController[] controllers;

    public MotorGroup(PWMMotorController... controllers) {
        this.controllers = controllers;
    }

    public void set(double speed) {
        for (PWMMotorController pmc : this.controllers) {
            pmc.set(speed);
        }
    }

    public void stopMotor() {
        this.set(0);
    }
}
