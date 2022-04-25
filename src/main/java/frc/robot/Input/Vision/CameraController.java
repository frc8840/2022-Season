package frc.robot.Input.Vision;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import frc.robot.AI.AIManager;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import frc.robot.utils.Colors;


public class CameraController {
    //Thread for the camera controller.
    private Thread thread;

    private AIManager aiManager;

    /**
     * Creates a camera controller and starts capture of the camera.
     * */
    public CameraController() {

        // String di_r = "/";

        // //Print the current directory to the console
        // System.out.println("Current Directory: " + di_r);

        // File dir = new File(di_r);

        // for (String file_ : dir.list()) {
        //     System.out.println("In folder: " + file_);
        // }

        //aiManager = new AIManager();

        thread = new Thread(() -> {
            //Get the camera
            UsbCamera camera = CameraServer.startAutomaticCapture();
            //Set the resolution of the camera
            camera.setResolution(640, 480);

            //Get the camera's sink.
            CvSink cvSink = CameraServer.getVideo();
            //Put the camera's sink into a source.
            CvSource outputStream = CameraServer.putVideo("Blur", 640, 480);
        
            Mat source = new Mat();
            Mat output = new Mat();

            //While the frame isn't interrupted, keep reading the camera.
            while (!Thread.interrupted()) {
                //Get the camera content, if there's an error, skip the frame.
                if (cvSink.grabFrame(source) == 0) {
                    continue;
                }

                try {
                    Mat frame = new Mat();

                    //Copy the source to frame.
                    source.copyTo(frame);

                    //Resize the frame.
//                    Imgproc.resize(frame, frame, new Size(640, 480));
//
//                    //Get the frame colors
//                    Mat blue_frame = getColor(frame, Colors.lower_blue, Colors.upper_blue);
//                    Mat red_frame = getColor(frame, Colors.lower_red, Colors.upper_red);
//
//                    //Draw contours
//                    frame = drawContours(frame, blue_frame, Colors.blue);
//                    frame = drawContours(frame, red_frame, Colors.red);
//
//                    //Copy the frame to output. This is what will be sent to the dashboard.
//                    frame.copyTo(output);

                    // List<Rect> rects = aiManager.runAllContoursThroughMat(frame);

                    // //Draw the rectangles on the frame.
                    // for (Rect rect : rects) {
                    //     Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
                    // }

                    // frame.copyTo(output);

                    // //Convert it to a frame that can be sent to the dashboard. Unfortunately, we can only send a GRAY frame.
                    // Imgproc.cvtColor(output, output, Imgproc.COLOR_BGR2GRAY);

                    //Send it.
                    outputStream.putFrame(source);
                } catch (Exception e) {
                    System.out.println("Error, camera is probably not loaded yet.");
                }
            }
        });
        thread.start();
    }

    /**
     * Creates a frame where the closer it is to the color, the closer to white it is.
     * @param frame The frame to read.
     * @param lowerLim The lower color limit.
     * @param upperLim The upper color limit.
     * @return A new frame with the closer it is to the color, the closer to white it is.
     * */
    public static Mat getColor(Mat frame, Scalar lowerLim, Scalar upperLim) {
        Mat finalMask = frame.clone();
        Imgproc.cvtColor(finalMask, finalMask, Imgproc.COLOR_BGR2HSV);

        Imgproc.GaussianBlur(finalMask, finalMask, new Size(5,5), 0);

        Core.inRange(finalMask, lowerLim, upperLim, finalMask);

        return finalMask;
    }

    /**
     * Gets a list of colors in a frame. This is usually called after getColor() in order to get the contours of the white spots.
     * @param drawFrame The frame to read.
     * @return A list of contours in the frame.
     * */
    public static List<MatOfPoint> getContours(Mat drawFrame) {
        Mat threshold = drawFrame.clone();
        Imgproc.threshold(threshold, threshold, 1, 255, Imgproc.THRESH_BINARY_INV);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = threshold.clone();
        Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        contours.sort((c1, c2) -> (int) (Imgproc.contourArea(c1) - Imgproc.contourArea(c2)));

        return contours;
    }

    public Mat drawContours(Mat drawFrame, Mat frameFrom, Scalar color) {
        return drawContours(drawFrame, frameFrom, color, (int) Math.pow(50, 2), 640 * 480 - 50);
    }

    /**
     * Draw the contours of a frame.
     * @param drawFrame The frame to draw on.
     *                  This is usually the actual camera image.
     * @param frameFrom The frame to read.
     *                  This is usually the frame from getColor()
     * @param color The color to draw the contour outlines.
     * @param minSize The minimum size of the contour to draw
     *                (if the width or height is smaller than this, it will not be drawn)
     * @param maxSize The max size of the contour to draw. This is in area.
     * @return The frame with the contours drawn.
     * */
    public Mat drawContours(Mat drawFrame, Mat frameFrom, Scalar color, int minSize, int maxSize) {
        //Get the list of contours.
        List<MatOfPoint> contours = getContours(frameFrom);

        contours.removeIf(contour -> {
            Rect rect = Imgproc.boundingRect(contour);
            return rect.area() < minSize || rect.area() > maxSize;
        });

        for (MatOfPoint contour : contours) {
            //Get the bounds of the object.
            Rect bounds = Imgproc.boundingRect(contour);

            try {
                //Put contour into its own list
                List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
                contourList.add(contour);

                //Draw a shape around the object
                Imgproc.drawContours(drawFrame, contourList, -1, color, 3);
            } catch (Exception e) {
                System.out.println("Loading camera...");
                return drawFrame;
            }

            //Draw a rectangle surrounding the object
            Imgproc.rectangle(drawFrame, new Point(bounds.x, bounds.y), new Point(bounds.x + bounds.width, bounds.y + bounds.height), new Scalar(255, 0, 0));

            //Draw the dot in the center
            Point p1 = new Point(bounds.x + Math.floor(bounds.width / 2) - 1, bounds.y + Math.floor(bounds.height / 2) - 1);
            Point p2 = new Point(bounds.x + Math.floor(bounds.width / 2) + 1, bounds.y + Math.floor(bounds.height / 2) + 1);

            Imgproc.rectangle(drawFrame, p1, p2, new Scalar(255, 0, 238));
        }

        return drawFrame;
    }

    public static Mat resizeImage(Mat frame, int width, int height) {
        Mat newFrame = new Mat();
        Imgproc.resize(frame, newFrame, new Size(width, height));

        return newFrame;
    }


    /**
     * Copy a frame's size.
     * @param original The frame to copy.
     * @return A new frame with the same size as the original.
     * */
    private Mat copyMat(Mat original) {
        Mat mt = new Mat();
        mt.copySize(original);
        return mt;
    }

    
}