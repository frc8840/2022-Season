package frc.robot;

import frc.robot.Commands.Process;
import frc.robot.Commands.TestProcess;
import frc.robot.Input.GameController;
import frc.robot.Input.Vision.CameraController;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A testing class for the robot.
 * This is primarily for testing on M1 Macs since there are issues with compiling on them.
 * */
public class TestMain {
    public static void main(String[] args) {
        System.out.println("Test Main.");

        CameraController cam = new CameraController();

        // TestProcess testProcess = new TestProcess("test");

        // Process.getProcess("test").runFrame();

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Process.getProcess("test").runFrame();
//            }
//        }, 0, 1000);
    }


}
