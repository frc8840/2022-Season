package frc.robot.Input;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Commands.Process;
import frc.robot.utils.Axis;
import jdk.jfr.BooleanFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * GameController to handle all input from the game controller(s).
 *
 * @author JaidenAGrimminck
 * */
public class GameController {
    //Port of the controller.
    private final int port;
    //Type of the controller.
    private final int type;

    private final Type typeEnum;

    //Where the controllers are stored. This might be simplified with a new GameController<T>(); in the future for efficiency, but for now this is easier.
    private Joystick m_stick;

    public Joystick getJoystick() { return m_stick; }

    private XboxController m_controller;

    public XboxController getXbox() { return m_controller; }

    private PS4Controller p_controller;

    private final HashMap<Button, ArrayList<Process>> subscribers = new HashMap<>();

    /**
     * Subscribes a process to an event.
     * @param button The button to subscribe to.
     * */
    public void subscribe(Button button, Process process) {
        subscribers.computeIfAbsent(button, k -> new ArrayList<>());
        subscribers.get(button).add(process);
    }

    /**
     * Checks all button states, and calls the subscribed processes.
     * */
    public void checkAndRunSubscribers() {
        if (type != 0) return;

        for (Button button : Button.values()) {
            if (getButtonInstant(button, false)) {
                subscribers.get(button).forEach(Process::run);
            }
        }

        subscribers.get(Button.ALL).forEach(Process::run);
    }

    /**
    * This is the constructor for the GameController class.
    * GameController will be responsible for handling all input from the gamepad or joystick.
    * @param port The gamepad is plugged into.
    * @param controllerType The controller type. Check GameController.Type for more information.
    * */
    public GameController(Type controllerType, int port) {
        this.port = port;
        this.typeEnum = controllerType;
        this.type = controllerType == Type.Joystick ? 0 : (controllerType == Type.Xbox ? 1 : (controllerType == Type.PS4 ? 2 : 3));

        //Assign the controller. Might just be easier to use the Type enum, but I don't really want to static import them.
        switch (type) {
            case 0:
            case 3:
                m_stick = new Joystick(port);
                break;
            case 1:
                m_controller = new XboxController(port);
                break;
            case 2:
                p_controller = new PS4Controller(port);
                break;
            default:
                throw new Error("Invalid controller type.");
        }

    }

    /**
     * Gets the thumbstick value of the joystick and left thumbstick on the gamepad.
     * @returns The thumbstick values.
     * */
    @SuppressWarnings("SpellCheckingInspection")
    public Thumbstick getThumbstick() {
        if (type == 0) {
            return new Thumbstick(m_stick.getX(), m_stick.getY(), m_stick.getZ(), m_stick.getTop(), m_stick.getMagnitude());
        } else if (type == 1) {
            //Get the left thumbstick on the gamepad.
            return new Thumbstick(m_controller.getLeftX(), m_controller.getLeftY(), 0, m_controller.getLeftStickButton(), 0);
        } else if (type == 2) {
            //Get the left thumbstick on the ps4.
            return new Thumbstick(p_controller.getLeftX(), p_controller.getLeftY(), 0, p_controller.getL3Button(), 0);
        }

        return null;
    }

    /**
     * Gets the thumbstick value of right thumbstick on the gamepad.
     * @returns The thumbstick values.
     * */
    @SuppressWarnings("SpellCheckingInspection")
    public Thumbstick getRightThumbstick() {
        if (type != 1) {
            throw new Error("This method is only available for gamepads.");
        }

        switch (type) {
            case 1:
                return new Thumbstick(m_controller.getRightX(), m_controller.getRightY(), 0, m_controller.getRightStickButton(), 0);
            case 2:
                return new Thumbstick(p_controller.getRightX(), p_controller.getRightY(), 0, p_controller.getR3Button(), 0);
            default:
                throw new Error("Invalid controller type.");
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public class Thumbstick {
        private double xAxis = 0d;
        private double yAxis = 0d;
        private double zAxis = 0d;
        private double magnitude = 0d;
        private boolean isDown = false;

        private final static double JOYSTICK_THRESHOLD = 0.1f;

        /**
         * This is the constructor for the Thumbstick class.
         * Thumbstick will be responsible for condensing the thumbstick input from the gamepad and joycon.
         * @param xAxis The x-axis of the thumbstick.
         *              This is the horizontal axis of the thumbstick.
         * @param yAxis The y-axis of the thumbstick.
         *              This is the vertical axis of the thumbstick.
         * @param zAxis The z axis of the thumbstick.
         * @param isDown Whether the thumbstick is being pressed down or not.
         * */
        public Thumbstick(double xAxis, double yAxis, double zAxis, boolean isDown, double magnitude) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            this.zAxis = zAxis;
            this.magnitude = magnitude;
            this.isDown = isDown;
        }

        /**
        * This will get an axis from the controller.
        * @param axis The axis to get.
        * @return The value of the axis.
        * */
        public double get(Axis axis) {
            switch (axis) { //Btw for the switch, you need to static import the Axis enums.
                case Horizontal:
                    return Math.abs(xAxis) < JOYSTICK_THRESHOLD ? 0 : xAxis;
                case Vertical:
                    return Math.abs(yAxis) < JOYSTICK_THRESHOLD ? 0 : yAxis;
                case Z:
                    return Math.abs(zAxis) < JOYSTICK_THRESHOLD ? 0 : zAxis;
                default:
                    return 0d;
            }
        }

        /**
         * This will get the magnitude of the thumbstick.
         * @return The magnitude of the thumbstick.
         * */
        public double getMagnitude() {
            return magnitude;
        }

        /**
         * This will get the isDown value of the thumbstick.
         * @return The isDown value of the thumbstick.
         * */
        public boolean getDown() {
            return isDown;
        }
    }

    public boolean getButtonDown(Button button) {
        int buttonNumber;
        switch (type) { //TODO: Add the other controllers.
            case 2:
                buttonNumber = Button.getButtonNumber(button, Type.PS4);
                return p_controller.getRawButton(buttonNumber);
            case 0:
            case 3:
                buttonNumber = Button.getButtonNumber(button, typeEnum);
                return m_stick.getRawButton(buttonNumber);
            default:
                return false;
        }
    }

    public boolean getRawButton(int buttonNumber) {
        switch (type) { //TODO: Add the other controllers.
            case 2:
                return p_controller.getRawButton(buttonNumber);
            case 0:
            case 3:
                return m_stick.getRawButton(buttonNumber);
            default:
                return false;
        }
    }

    /**
     * Gets the button instant, i.e. whether the button was just pressed down or released.
     * @param button The button to get.
     * @param release Whether the button is being released [true] or being pressed [false].
     * */
    public boolean getButtonInstant(Button button, boolean release) {
        int buttonNumber;
        switch (type) { //TODO: Add the other controllers.
            case 2:
                buttonNumber = Button.getButtonNumber(button, Type.PS4);
                return release ? p_controller.getRawButtonReleased(buttonNumber) : p_controller.getRawButtonPressed(buttonNumber);
            case 3:
                buttonNumber = Button.getButtonNumber(button, Type.Logitech);
                return release ? m_stick.getRawButtonReleased(buttonNumber) : m_stick.getRawButtonPressed(buttonNumber);
            default:
                return false;
        }
    }


    /**
     * Types of controllers that are able to be used.
     * */
    public enum Type {
        Joystick, Xbox, PS4, Logitech, None; //Logitech is the same as a joystick, just need to find constants
    }

    /**
     * Types of inputs on the controller. This is primarily used for button subscriptions.
     * */
    public enum Button {
        A, B, X, Y, UP, DOWN, LEFT, RIGHT, L1, L2, L3, R1, R2, R3, BACK, START, HOME, MODE, UNIQUE_LOGO_BUTTON, TOUCHPAD, ALL;

        //0 is empty.
        //Look at: https://www.chiefdelphi.com/t/logitech-controller-programming/117673/6
        private static final int[] correspondingButtonLogitech = {
                1, 2, 3, 4, //A, B, X, Y
                0, 0, 0, 0, //UP, DOWN, LEFT, RIGHT
                5, 0, 9, //L1, L2, L3
                6, 0, 10, //R1, R2, R3
                7, 8, 0, //BACK, START, HOME
                -1, -1 //MODE, LOGITECH / UNIQUE_LOGO_BUTTON
        };

        //Look at: https://docs.studica.com/en/latest/docs/FRCTraining/programming/trainer-tutorial/creating-a-gamepad-constants-class.html
        private static final int[] correspondingButtonPS4 = {
            2, 3, 1, 4, //X Button, Circle Button, Square Button, Triangle Button (In order of same positions of Logitech/XBOX)
            0, 0, 0, 0, //Up, Down, Left, Right
            5, 7, 11, //L1, L2, L3
            6, 8, 12, //R1, R2, R3
            9, 10, 0, //Share, Options, Home
            0, 13, //Mode, PS_logo / UNIQUE_LOGO_BUTTON
            14 //Touchpad (PS4 only)
        };

        //TODO: Map this out
        private static final int[] correspondingButtonJoystick = {
            0, 0, 0, 0, //A, B, X, Y
            0, 0, 0, 0, //UP, DOWN, LEFT, RIGHT
            1, 0, 0, //L1 - Main Trigger, L2, L3
            1, 0, 0, //R1 - Main Trigger, R2, R3
            0, 0, 0, //BACK, START, HOME
            0, 0, 0, //MODE, UNIQUE_LOGO_BUTTON
        };

        public static int getButtonNumber(Button button, Type type) {
            if (type == Type.Logitech) {
                if (button.ordinal() < correspondingButtonLogitech.length) return correspondingButtonLogitech[button.ordinal()];
            } else if (type == Type.PS4) {
                if (button.ordinal() < correspondingButtonPS4.length) return correspondingButtonPS4[button.ordinal()];
            } else if (type == Type.Joystick) {
                if (button.ordinal() < correspondingButtonJoystick.length) return correspondingButtonJoystick[button.ordinal()];
            }

            return 0;
        }

    }
}
