package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.robot.utils.MotorGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class MotorManager {
    public static MotorManager instance;
    public static MotorManager getInstance() { return instance; }

    /**
     * Creates a new motor manager. Only one can exist.
     * */
    public MotorManager() {
        if (instance != null) {
            System.out.println("WARNING: Only one Motor Manager can exist at a time. Didn't create a second one.");
            return;
        }
        instance = this;
    }

    private HashMap<Integer, PWMMotorController> motorControllers = new HashMap<>();

    /**
     * Gets the motor controller in the specified port.
     * @param port The port of the motor controller.
     * @return The motor controller in the specified port.
     * */
    public PWMMotorController getMotor(int port) {
        return motorControllers.get(port);
    }

    /**
     * Creates a new Spark Max motor controller at the specified port.
     * @param port The PWM port of the motor controller.
     * @return The controller in the specified port.
     * */
    public PWMSparkMax newSparkMax(int port) {
        PWMSparkMax motorController = new PWMSparkMax(port);
        motorControllers.put(port, motorController);
        return motorController;
    }

    /**
     * Creates a new Spark motor controller at the specified port.
     * @param port The PWM port of the motor controller.
     * @return The controller in the specified port.
     * */
    public Spark newSpark(int port) {
        Spark motorController = new Spark(port);
        motorControllers.put(port, motorController);
        return motorController;
    }

    /**
     * Create new Spark Max motor controllers.
     * @param ports The PWM ports of the motor controllers.
     * */
    public PWMSparkMax[] newSparkMaxMotors(int... ports) {
        PWMSparkMax[] cs = new PWMSparkMax[ports.length];
        int i = 0;

        for (int port : ports) {
            cs[i] = newSparkMax(port);
            i++;
        }

        return cs;
    }

    /**
     * Inverse specified motor controllers.
     * @param ports The ports to inverse.
     * */
    public void inverseMotors(int... ports) {
        for (int port : ports) {
            getMotor(port).setInverted(true);
        }
    }

    /**
     * Get all the motor controllers on the robot.
     * @return All the motor controllers on the robot.
     * */
    public ArrayList<PWMMotorController> getAllMotors() {
        return new ArrayList<>(motorControllers.values());
    }

    private HashMap<String, MotorGroup> drivers = new HashMap<>();

    /**
     * Creates a new driver with the specified key.
     * @param name The key of the driver.
     * @param motors The motors to use in the driver.
     * */
    public void addDriver(String name, PWMMotorController... motors) {        
        ArrayList<PWMMotorController> mlist = new ArrayList<>();
        for (PWMMotorController mc : motors) {
            if (mc.getChannel() != 7) mlist.add(mc);
        }
        drivers.put(name, new MotorGroup(mlist.toArray(new PWMMotorController[0])));
    }

    /**
     * Gets a driver using the specified key.
     * @param driver The key of the driver.
     * @return The driver with the specified key.
     * */
    public MotorGroup getDriver(String driver) {
        return drivers.get(driver);
    }

    /**
     * Stops all motors and drivers.
     * */
    public void stopAllMotors() {
        drivers.values().forEach(MotorGroup::stopMotor);
        getAllMotors().forEach((e) -> {
            e.set(0);
        });
    }
}
