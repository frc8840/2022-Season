// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what
 * you are doing, do not modify this file except to change the parameter class to the startRobot
 * call.
 *
 * ^^ That was added by WPILib. I don't know why static variables are not allowed, but abide by it, I guess.
 */
public class Main {
    private Main() {}

    /**
     * Start the robot.
     * @param args The arguments, not really used (in our case).
     * @see Robot
     * */
    public static void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }

    /**
     *
     * Ignore all the stuff below.
     * Just some stuff I made a while ago.
     * -Jaiden G
     *
     *
     * */

//    static Scanner scanner;
//    static Controller controller;

    /**
     * Get the controller.
     * @return the controller
     * */
//    @Deprecated
//    public static Controller getController() { return controller; }

    /**
     * Creates a new controller.
     * Note: I have no clue what this was used for.
     * @return a new controller
     * */
//    @Deprecated
//    public static void newController() {
//        controller = new Controller();
//    }

    /**
     * Old code that I made to test a GUI, and create a simulation to simulate speeds that the robot needs to travel at in order to reach a certain distance.
     * */
//    @Deprecated
//    private void createWindow() {
//        scanner = new Scanner(System.in);
//        //Create new toplevelwindow
//        TopLevelWindow.createWindow();
//        //Create a new contoller
//        controller = new Controller();
//    }
}
