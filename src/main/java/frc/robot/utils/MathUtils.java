package frc.robot.utils;

public class MathUtils {
    public static double activation(double x, activation type) {
        if (type == activation.ReLU) {
            return ReLU(x);
        } else if (type == activation.sigmoid) {
            return sigmoid(x);
        } else if (type == activation.tanh) {
            return Math.tanh(x);
        } else if (type == activation.linear) {
            return x;
        } else if (type == activation.softmax) {
            return softmax(x);
        } else if (type == activation.binary_step) {
            return binaryStep(x);
        }

        return x;
    }

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        //Get the sigmoid derivative
        return Math.exp(-x) / Math.pow(1 + Math.exp(-x), 2);
    }

    public static double dsigmoid(double x) {
        return x * (1 - x);
    }

    public static double ReLU(double x) {
        return Math.max(0, x);
    }

    public static double binaryStep(double x) { //Pretty much the derivative of ReLU
        return x > 0 ? 1 : 0;
    }

    public static double softmax(double x) {
        double z = Math.exp(x);
        return z / (1 + z);
    }

    public static double distance(double x, double y, double x2, double y2) {
        return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
    }

    public enum activation {
        ReLU,
        sigmoid,
        binary_step,
        tanh,
        linear,
        softmax
    }
}