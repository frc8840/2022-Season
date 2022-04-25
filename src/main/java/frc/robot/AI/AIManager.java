package frc.robot.AI;

import frc.robot.Input.Vision.CameraController;
import frc.robot.utils.Colors;
import frc.robot.utils.MathUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class AIManager {
    private final int numInputs = 1024;
    private final int numHiddenLayers = 1;
    private final int numHiddenNodes = 15;
    //First is for if there is a ball in the image, second is for if there is no ball in the image
    private final int numOutputs = 1;

    private NeuralNetwork nn;

    private boolean loadedSuccessfully;

    public AIManager() {
        //Create the AI
        nn = new NeuralNetwork(numInputs, numHiddenNodes, numOutputs);
        try {
            loadedSuccessfully = nn.loadNumbersFromFiles();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load AI weights and biases from files.");
            loadedSuccessfully = false;
        }
    }

    public List<Rect> runAllContoursThroughMat(Mat mat) {
        if (!loadedSuccessfully) return new ArrayList<Rect>(0);

        List<Rect> rects = new ArrayList<>();

        //Get the blue frame
        Mat blueFrame = CameraController.getColor(mat, Colors.lower_blue, Colors.upper_blue);

        List<MatOfPoint> contours = CameraController.getContours(blueFrame);
        contours.removeIf(contour -> {
            Rect rect = Imgproc.boundingRect(contour);
            return rect.area() < Math.pow(35, 2) || rect.area() > 640 * 480 - 50;
        });

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            //Get the rect on the image
            Mat rectOnImage = new Mat(mat, rect);

            //Resize the rectOnImage to 32 by 32.
            rectOnImage = CameraController.resizeImage(rectOnImage, 32, 32);

            if (nn.predict(createInput(rectOnImage)).get(0) > 0.5) {
                rects.add(rect);
            }
        }

        //Get the red frame
        Mat redFrame = CameraController.getColor(mat, Colors.lower_red, Colors.upper_red);

        contours = CameraController.getContours(redFrame);
        contours.removeIf(contour -> {
            Rect rect = Imgproc.boundingRect(contour);
            return rect.area() < Math.pow(35, 2) || rect.area() > 640 * 480 - 50;
        });

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            //Get the rect on the image
            Mat rectOnImage = new Mat(mat, rect);

            //Resize the rectOnImage to 32 by 32.
            rectOnImage = CameraController.resizeImage(rectOnImage, 32, 32);

            if (nn.predict(createInput(rectOnImage)).get(0) > 0.5) {
                rects.add(rect);
            }
        }

        return rects;
    }

    public double[] createInput(Mat mat) {
        if (mat.rows() * mat.cols() != numInputs) {
            throw new IllegalArgumentException("Mat must be of size " + numInputs);
        }

        double[] input = new double[numInputs];

        //Loop through each pixel
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                //Check how close it is to white
                double[] pixel = mat.get(i, j);
                double distanceToWhite = MathUtils.distance(pixel[0], pixel[1], 255, 255);

                //Normalize the distance to white
                input[i * mat.cols() + j] = distanceToWhite / 255;
            }
        }

        return input;
    }
}
