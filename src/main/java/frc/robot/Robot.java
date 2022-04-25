// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.robot.Commands.Process;
import frc.robot.Input.GameController;
import frc.robot.Input.UltrasonicSensor;
import frc.robot.Input.GameController.Button;
import frc.robot.Input.Vision.CameraController;
import frc.robot.utils.Axis;
import frc.robot.utils.Timings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * From WPILib 2022:
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * BTW note for why there's a *lot* of comments in this file:
 * While making this, I didn't understand that in order to set the speed of the speed controllers, you only needed to set it once, not every frame.
 * This lead to a few issues, but it was fine. Later, one speed controller was set up to our brushless motor, and it would move at the same time as any other motor.
 * I was really confused, so I spent a long time trying to figure out what was going on. I did find out that I needed to call <code>PWMMotorController#stopMotor()</code> in order to stop it moving.
 * I was too lazy/scared to reformat it back into the MotorManager class, BUT it should work next year.
 *
 * @author JaidenAGrimminck
 *
 * Note from Jaiden:
 * - Here's the package documentation: https://first.wpi.edu/wpilib/allwpilib/docs/release/java/index.html
 */
public class Robot extends TimedRobot {
    /**
     * These sets up the Robot Instance, so we can reference it from anywhere in the code.
     *
     * */
    public Robot() {instance = this;}
    private static Robot instance; //Robot Instance
    /**
     * Gets the instance of the robot.
     * @return The robot instance.
     * */
    public static Robot getInstance() {
        return instance;
    }

    private final Timer m_timer = new Timer();

    private ArrayList<GameController> controllers = new ArrayList<GameController>();

    /**
     * Gets the main controller - this is usually the controller in channel 0.
     * @return The main controller.
     * */
    public GameController getMainController() {
        if (controllers.size() > 0) return controllers.get(0);
        else return null;
    }

    /**
     * Gets the second controller used.
     * @return The secondary controller
     */
    public GameController getSecondaryController() {
        if (controllers.size() > 1) return controllers.get(1);
        else return null;
    }

    /*
    * Sensors. Mainly for testing (for now).
    * */
    private CameraController cam;
    private UltrasonicSensor sensor;

    private Spark collectorMotor;
    private Spark armMotor;

    PWMSparkMax motor0;
    PWMSparkMax motor1;
    PWMSparkMax motor2;
    PWMSparkMax motor3;

    public Spark climbMotor;

    //Climbing stuff that is very unorganized.
    private int framesGoingDown = 0;
    private boolean climbMotorChanging = false;
    private boolean climbMotorGoingBackUp = false;

    final private int maxFramesDown = 30;

    PowerDistribution pdp;

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        MotorManager m_manager = new MotorManager();

        cam = new CameraController();

        sensor = new UltrasonicSensor(0);

        controllers.add(new GameController(GameController.Type.Joystick, 0));
        controllers.add(new GameController(GameController.Type.Xbox, 1));

        pdp = new PowerDistribution();

        //Left side motors
        PWMSparkMax[] sparkMotors = m_manager.newSparkMaxMotors(0, 1, 2, 3);

        motor0 = sparkMotors[0];
        motor1 = sparkMotors[1]; //PWMSpark
        motor2 = sparkMotors[2];
        motor3 = sparkMotors[3];

        m_manager.inverseMotors(2, 3);

        collectorMotor = new Spark(4); //WTF idk why it's being so annoying/??
        armMotor = new Spark(5);
        climbMotor = new Spark(6);

        //Put the drivers in the hashmap.
        // m_manager.addDriver("left", m_manager.getMotor(0), m_manager.getMotor(1));
        // m_manager.addDriver("right", m_manager.getMotor(2), m_manager.getMotor(3));
        //context: i absolutely hated making this work.
        //m_manager.addDriver("****_you", collectorMotor);

        //Create subscribers
        //Try statement is there to prevent a memory leak btw.
        try (Robot robo = this) {
            getMainController().subscribe(GameController.Button.ALL, new Process("speed_adjust") {
                @Override
                public void run() {
                    if (Robot.getInstance().getMainController().getJoystick().getTopPressed()) {
                        Robot.getInstance().overrideSpeedLimit = !robo.overrideSpeedLimit;

                        System.out.println(robo.overrideSpeedLimit ? "WARNING: Overridden speed limit." : "WARNING: Speed limit set back to normal.");
                    }
                }
            });

            // getMainController().subscribe(GameController.Button.ALL, new Process("ball_collector_adjust") {
            //     @Override
            //     public void run() {
            //         if (Robot.getInstance().getMainController().getRawButton(7)) {
            //             Robot.getInstance().ballCollectorForward = true;
            //             Robot.getInstance().ballCollectorBackward = false;
            //         } else if (Robot.getInstance().getMainController().getRawButton(8)) {
            //             Robot.getInstance().ballCollectorBackward = true;
            //             Robot.getInstance().ballCollectorForward = false;
            //         } else {
            //             Robot.getInstance().ballCollectorForward = false;
            //             Robot.getInstance().ballCollectorBackward = false;
            //         }

            //     }
            // });
        }

        //Just generic match info.
        System.out.println("Robot, by Team 8840.");
        System.out.println("-- PLAYING AT THE " + DriverStation.getEventName() + " EVENT --");
        System.out.println("Alliance: " + DriverStation.getAlliance().toString());
        System.out.println("Match time? " + DriverStation.getMatchTime());
        System.out.println("Match Type: " + DriverStation.getMatchType().toString());
    }

    public boolean ballCollectorForward = false;
    public boolean ballCollectorBackward = false;

    //Used for tracking the timer.
    private int lastTimerCount = 0;

    /** This function is run once each time the robot enters autonomous mode. */
    @Override
    public void autonomousInit() {
        m_timer.reset();
        m_timer.start();

        lastTimerCount = 0;
    }

    double pickUpSpeed = 0.7;
    double sendit = -1.0;

    double framesMovedback = 0.0;

    /** This function is called periodically during autonomous. */
    @Override
    public void autonomousPeriodic() {

        //collectorMotor.set(0.5);

        // Really basic.
        //1.5 seconds - move forward.
        //1.5 seconds - move arm up
        //2 seconds - move collector out/expell the ball
        //5 seconds - move backwards outside of tarmac.
        /**
         * if (m_timer.get() < 1.5) {
           motor0.set(1.0);
           motor1.set(1.0);
           motor2.set(1.0);
           motor3.set(1.0);
           armMotor.stopMotor();
           collectorMotor.stopMotor();
       } else if (m_timer.get() < 3.0) {
            armMotor.set(1.0);
            motor0.stopMotor();
            motor1.stopMotor();
            motor2.stopMotor();
            motor3.stopMotor();
            collectorMotor.stopMotor();
       } else 
         */

        //  motor0.stopMotor();
        //  motor1.stopMotor();
        //     motor2.stopMotor();
        //     motor3.stopMotor();
        //     armMotor.stopMotor();
        //     collectorMotor.stopMotor();
        //     climbMotor.stopMotor();

         double backspeed = 0.2;

        //  if (m_timer.get() < 3.0) {
        //     motor0.set(backspeed);
        //     motor1.set(backspeed);
        //     motor2.set(backspeed);
        //     motor3.set(backspeed);
        //     armMotor.stopMotor();
        //     collectorMotor.stopMotor();
        // }
       if (m_timer.get() < 2.0) { //old: 5.0
           collectorMotor.set(sendit);
            motor0.stopMotor();
            motor1.stopMotor();
            motor2.stopMotor();
            motor3.stopMotor();
            armMotor.stopMotor();
       } else if (m_timer.get() < 12.0) {
            motor0.set(backspeed);
            motor1.set(backspeed);
            motor2.set(backspeed);
            motor3.set(backspeed);
            armMotor.stopMotor();
            collectorMotor.stopMotor();
       } else {
            motor0.stopMotor();
            motor1.stopMotor();
            motor2.stopMotor();
            motor3.stopMotor();
       } 
        // MotorManager.getInstance().getDriver("left").set(1.0);
        // MotorManager.getInstance().getDriver("right").set(1.0);
        

        // if (Math.floor(m_timer.get()) > lastTimerCount) { //Every second, print the time
        //     lastTimerCount = (int) Math.floor(m_timer.get());
        //     System.out.println("Time: " + m_timer.get() + ", time left in autonomous: " + (Timings.autonomousTime - m_timer.get()) );
        // }
    }

    /** This function is called once each time the robot enters teleoperated mode. */
    @Override
    public void teleopInit() {
        m_timer.reset();
        m_timer.start();

        lastTimerCount = 0;
    }

    //Thresholds/Constants. Might want to just move this to another class.
    private static final double MAX_SPEED = 0.7;
    private static final double MAX_TURN_ADDITION = 0.3;
    private static final double TURN_THRESHOLD = 0.1;
    private static final double MAX_X_THRESHOLD = 0.6;
    private static final double Z_THRESHOLD = 0.35;

    //Some overriders.
    public boolean overrideSpeedLimit = true;
    public boolean overrideAutobrakes = false;


    double lastTimeHeldArm = 0;
    boolean beingHeldArm = false;
    double lastCooldownActivatedTimeArm = -2;
    boolean cooldownActivated = false;

    final double armCooldownTime = 2d;
    final double maxTimeHeld = 2.5d;

    /** This function is called periodically during teleoperated mode. */
    @Override
    public void teleopPeriodic() {
        //Run all subscriber events
        controllers.forEach(GameController::checkAndRunSubscribers);

        boolean movingStraight = false;
        boolean sideTwist = false;

        if (Math.abs(getMainController().getThumbstick().get(Axis.Z)) < Z_THRESHOLD || getMainController().getButtonDown(Button.L1)) {
            movingStraight = true;

            double controllerY = -(getMainController().getThumbstick().get(Axis.Vertical));
            double controllerX = -(getMainController().getThumbstick().get(Axis.Horizontal));

            if (Math.abs(controllerX) < TURN_THRESHOLD) controllerX = 0;

            double leftAdjust = 0;
            double rightAdjust = 0;

            if (controllerY < 0.1 && controllerY > -0.1) controllerY = 0;

            //LEFT AND RIGHT ADJUST CAN BE CHANGED. TODO: Fix it into one or two lines instead of this if-statement.

            if (!overrideSpeedLimit) {
                if (controllerY < 0) {
                    if (controllerX < 0) {
                        rightAdjust = controllerX * MAX_TURN_ADDITION;
                    } else if (controllerX > 0) {
                        leftAdjust = -(controllerX * MAX_TURN_ADDITION);
                    }
                } else {
                    if (controllerX < 0) {
                        rightAdjust = -(controllerX * MAX_TURN_ADDITION);
                    } else if (controllerX > 0) {
                        leftAdjust = controllerX * MAX_TURN_ADDITION;
                    }
                }

                if (Math.abs(controllerY) > MAX_SPEED) controllerY = Math.signum(controllerY) * MAX_SPEED;
            }

            boolean alreadyUsingTwist = false;

            if (getMainController().getButtonDown(Button.L1) && overrideSpeedLimit) {
                if (controllerX > 0.8) {
                    movingStraight = false;
                    sideTwist = true;
                    alreadyUsingTwist = true;
                }
            }

            //In order to turn, one side will go faster than the other.
            // MotorManager.getInstance().getDriver("right").set(-(controllerY + leftAdjust));
            // MotorManager.getInstance().getDriver("left").set(-(controllerY + rightAdjust));

            if (!alreadyUsingTwist) {
                motor0.set(-(controllerY + leftAdjust));
                motor1.set(-(controllerY + leftAdjust));

                motor2.set(-(controllerY + rightAdjust));
                motor3.set(-(controllerY + rightAdjust));
            }

            //System.out.println("moving");
        }

        if (!movingStraight){
            double controllerZ = -(sideTwist ? getMainController().getThumbstick().get(Axis.Vertical) : getMainController().getThumbstick().get(Axis.Z));

            double adjust = getMainController().getJoystick().getRawAxis(3) * -1;
            adjust += 1;
            adjust /= 2;
            //now have percentage
            double lowerLimit = 0.2;
            if (adjust < lowerLimit) adjust = lowerLimit;

            //If the speed limit isn't overridden and the controllerZ is over the max_x_threshold, just set it to the max_x_threshold.
            if (Math.abs(controllerZ) > MAX_X_THRESHOLD && !overrideSpeedLimit) controllerZ = Math.signum(controllerZ) * MAX_X_THRESHOLD;

            controllerZ *= adjust;

            // MotorManager.getInstance().getDriver("left").set(controllerZ);
            // MotorManager.getInstance().getDriver("right").set(-controllerZ);
            motor0.set(controllerZ);
            motor1.set(controllerZ);

            motor2.set(-controllerZ);
            motor3.set(-controllerZ);
        }

        //TODO: fix this. might need to actually resolder the distance sensor though.
        if (getMainController().getRawButton(3)) {
            System.out.print("Distances (cm, inches): ");
            System.out.print(sensor.getValue(true) + ", " + sensor.getValue(false));
            System.out.println();
        }

        double armSpeed = 1;
        
        // if (ballCollectorForward || ballCollectorBackward) {
        //     //System.out.println(ballCollectorForward + ", " + ballCollectorBackward);
        //     collectorMotor.set(ballCollectorBackward ? pickUpSpeed : -pickUpSpeed);
        // } else {
        //     collectorMotor.stopMotor();
        // }

        // if (getMainController().getJoystick().getRawButton(9)) {
        //     armMotor.set(armSpeed);
        // } else if (getMainController().getJoystick().getRawButton(10)) {
        //     armMotor.set(-armSpeed);
        // } else if (getMainController().getJoystick().getRawButton(11)) {
        //     armMotor.set(0.2);
        // } else {
        //     armMotor.stopMotor();
        // }

        

        if (getSecondaryController().getXbox().getRawButton(5)) {
            armMotor.set(-armSpeed);
            //System.out.println("arm down");
        } else if (getSecondaryController().getXbox().getRawButton(6)) {
            if (getSecondaryController().getXbox().getRawButton(4)) {
                armMotor.set(armSpeed);
            } else {
                if (!beingHeldArm && !cooldownActivated) {
                    beingHeldArm = true;
                    lastTimeHeldArm = m_timer.get();
    
                    System.out.println("WARNING: Arm activated, releasing at " + (maxTimeHeld + m_timer.get()) + " seconds!");
                }
    
                if (cooldownActivated) {
                    armMotor.stopMotor();
                } else {
                    armMotor.set(armSpeed);
    
                    if (lastTimeHeldArm + maxTimeHeld <= m_timer.get()) {
                        System.out.println("WARNING: Arm Cooldown Activated! Must wait " + armCooldownTime + " seconds and release the trigger!");
                        cooldownActivated = true;
                        lastCooldownActivatedTimeArm = m_timer.get();
                    }
                }
            }

            //System.out.println("Arm up");
        } else if (getSecondaryController().getXbox().getRawButton(3)) {
            armMotor.set(0.2);
           //System.out.println("arm slow");
        } else {
            beingHeldArm = false;
            armMotor.stopMotor();

            if (cooldownActivated && m_timer.get() > lastCooldownActivatedTimeArm + armCooldownTime) {
                System.out.println("WARNING: Arm cooldown finished! You are allowed to hold the arm up for " + maxTimeHeld + " seconds!");
                cooldownActivated = false;
                beingHeldArm = false;
            }
        }

        if (getSecondaryController().getXbox().getRawAxis(3) > 0.9) {
            collectorMotor.set(sendit);
            //System.out.println("send it");
        } else if (getSecondaryController().getXbox().getRawAxis(2) > 0.9) {
            collectorMotor.set(pickUpSpeed);
            //System.out.println("Pick up");
        } else {
            collectorMotor.stopMotor();
        }

        if (getSecondaryController().getXbox().getRawButton(1) && !climbMotorChanging) {
            climbMotorChanging = true;
        }

        if (climbMotorChanging) {
            if (!climbMotorGoingBackUp) {
                framesGoingDown++;
            } else {
                framesGoingDown--;
            }

            double up = -1.0;

            climbMotor.set(climbMotorGoingBackUp ? up : -up);

            if (framesGoingDown == maxFramesDown) {
                climbMotorGoingBackUp = true;
            } else if (framesGoingDown == 0) {
                climbMotorGoingBackUp = false;
                climbMotorChanging = false;
            }
        } else {
            climbMotor.stopMotor();
        }

        if (getSecondaryController().getXbox().getPOV() == 180 && !climbMotorChanging) {
            climbMotor.set(0.5);
            //System.out.println("winch is slow (puling down)");
        } else if (getSecondaryController().getXbox().getPOV() == 0 && !climbMotorChanging) {
            climbMotor.set(-0.5);
            //System.out.println("winch is going back out");
        } else if (getSecondaryController().getXbox().getRawButton(2) && !climbMotorChanging) {
            climbMotor.set(1.0);
            //System.out.println("winch is going fast");
        } else {
            climbMotor.stopMotor();
        }

        if (Math.floor(m_timer.get()) > lastTimerCount) { //Every second, print the time
            lastTimerCount = (int) Math.floor(m_timer.get());
            //System.out.println("Time: " + Math.floor(m_timer.get()) + ", time left in autonomous: " + Math.floor(Timings.autonomousTime - m_timer.get()) );
        }
    }

    /** This function is called once each time the robot enters test mode. */
    @Override
    public void testInit() {

    }

    /** This function is called periodically during test mode. */
    @Override
    public void testPeriodic() {
        //Run all subscriber events
        //controllers.forEach(GameController::checkAndRunSubscribers);

        try {
            Spark spark = (Spark) MotorManager.getInstance().getMotor(4);

            double y = -(getMainController().getThumbstick().get(Axis.Vertical));

            spark.set(y);
        } catch (Exception e) {e.printStackTrace();}
    }
}
