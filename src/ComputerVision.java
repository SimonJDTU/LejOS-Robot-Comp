import nu.pattern.OpenCV;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

public class ComputerVision extends JPanel implements IComputerVision, Runnable {

    private Point frontCenter = new Point(), backCenter = new Point(), lastPositionFront = new Point(), lastPositionBack = new Point(), goal2 = new Point();
    private ArrayList<Point> ballsLocation = new ArrayList<>();
    private static ArrayList<ArrayList<Point>> ballConsistency = new ArrayList<>();
    private Mat frame;
    private VideoCapture camera;
    private boolean programRunning = true;

    private final int SET_FRAME_WIDTH = 640, SET_FRAME_HEIGHT = 480;

    ComputerVision() {
        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() throws InterruptedException {

        /* Loading core libary to get accesses to the camera.
        If not load correctly try:
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ComputerVision t = new ComputerVision();*/
        OpenCV.loadLocally();


        // Capturing from usb Camera
        // Camera has to be 142-143 from the ground.
        // USB CAM index 4 , own is 0
        camera = new VideoCapture(0);
        //camera.open("/dev/v41/by-id/usb-046d_Logitech_Webcam_C930e_DDCF656E-video-index0");

        // Set resolution
        // 720p(1280 - 720)   480p(640 - 480)
        camera.set(CV_CAP_PROP_FRAME_WIDTH, SET_FRAME_WIDTH);
        camera.set(CV_CAP_PROP_FRAME_HEIGHT, SET_FRAME_HEIGHT);

        // New Mat frame
        frame = new Mat();

        // Show the mat frame
        System.out.println(frame.type());
        camera.read(frame);
        Thread.sleep(2000);
        if (!camera.isOpened()) {
            System.out.println("Camera Error");
        }
        System.out.println("Initialization completed");
    }

    @Override
    public void run() {
        Mat tempImage = new Mat();
        Mat tempImage1 = new Mat();
        Mat tempImage2 = new Mat();
        Mat tempImage3 = new Mat();
        Mat combined = new Mat();
        Mat tempImage5 = new Mat();
        Mat matHomography;
        Mat cornerImage = new Mat();
        MatOfPoint2f cornersOfFrame, cornersOfTrack;
        Mat circles = new Mat();
        Mat fromtcircles = new Mat();
        Mat backcircles = new Mat();
        ArrayList<Point> corners = new ArrayList<>();


        if (camera.read(frame)) {

            do {

                // Convert color for ball detection

                // Median Blur seams not to be importened for now. Umcomment and edit k size to use.
                //Imgproc.medianBlur(tempImage, tempImage, 11);

                // Normalize the image to increase and improve ball detection

                // Use HoughCircels to mark the balls

                Imgproc.cvtColor(frame, cornerImage, COLOR_BGR2GRAY);
                Imgproc.medianBlur(cornerImage, cornerImage, 7);
                Core.normalize(cornerImage, cornerImage, 10, 200, Core.NORM_MINMAX, CV_8UC1);

                // New Mat to detect colors
                Imgproc.cvtColor(frame, tempImage1, COLOR_BGR2HSV);
                Mat tempImage8 = tempImage1.clone();
                Core.normalize(tempImage, tempImage1, 10, 200, Core.NORM_MINMAX, CV_8UC1);

                inRange(cornerImage, new Scalar(5, 5, 5), new Scalar(46, 46, 46), cornerImage);
                try {
                    corners = detectCorners(cornerImage);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("unlucky");
                }
                // SMALL
                inRange(tempImage1, new Scalar(35, 45, 225), new Scalar(60, 75, 255), tempImage3);

                //Homography warp
                cornersOfTrack = new MatOfPoint2f(corners.get(0), corners.get(1), corners.get(2), corners.get(3));
                cornersOfFrame = new MatOfPoint2f(new Point(0, 0), new Point(640, 0), new Point(0, 480), new Point(640, 480));
                matHomography = Calib3d.findHomography(cornersOfTrack, cornersOfFrame, Calib3d.RANSAC, 4); //Calib3d.RANSAC
                warpPerspective(frame, frame, matHomography, frame.size());
                tempImage = frame.clone();


                Imgproc.cvtColor(frame, tempImage, COLOR_BGR2GRAY);
                Core.normalize(tempImage, tempImage, 60, 200, Core.NORM_MINMAX, CV_8UC1);


                // HoughCircles to detect circles - to find TTBalls.
                Imgproc.HoughCircles(tempImage, circles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows() / 100, 50.0, 20.0, 4, 9);  // save values 50, 50, 25,10,23

                // Detect circels on Robot
                Imgproc.HoughCircles(tempImage, fromtcircles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows() / 100, 50.0, 19.0, 10, 15);  // save values 50, 50, 25,10,23
                Imgproc.HoughCircles(tempImage, backcircles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows() / 100, 50.0, 19.0, 15, 30);  // save values 50, 50, 25,10,23


                Imgproc.cvtColor(frame, tempImage5, COLOR_BGR2HSV);

                //Core.inRange(tempImage2,new Scalar(0,0,0),new Scalar(250,250,180),tempImage2);
                //borders
                inRange(tempImage1, new Scalar(0, 170, 170), new Scalar(190, 255, 255), tempImage2);

                // Goals
                // SMALL

                // BIG

                inRange(tempImage8, new Scalar(25, 80, 245), new Scalar(40, 100, 255), combined);
                HighGui.imshow("after", combined);
                // Detect Corners

                //Colorize circels
                for (int i = 0; i < circles.cols(); i++) {
                    double[] c = circles.get(0, i);
                    Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                    ballsLocation.add(center);

                }
                ballConsistency.add(0, ballsLocation);
                if (ballConsistency.size() >= 10) {
                    ballConsistency.remove(ballConsistency.size() - 1);
                }
                ArrayList<Point> balls = new ArrayList<>();

                for (ArrayList<Point> points : ballConsistency) {
                    for (int j = 0; j < points.size(); j++) {
                        if (!balls.contains(points.get(j))) {
                            balls.add(points.get(j));
                        }
                    }
                }
                for (int i = 0; i < balls.size(); i++) {
                    for (int j = 0; j < balls.size(); j++) {
                        if (i != j && (balls.get(i).x <= balls.get(j).x + 5 && balls.get(i).x >= balls.get(j).x - 5) && (balls.get(i).y <= balls.get(j).y + 5 && balls.get(i).y >= balls.get(j).y - 5)) {
                            balls.remove(i);
                            i--;
                            break;
                        }
                    }
                }
                //System.out.println("amount of balls: " + balls.size());
                for (Point ball : balls) {
                    circle(frame, ball, 1, new Scalar(255, 100, 100), 7, 8, 0);
                }

                try {

                    double[] c = backcircles.get(0, 0);
                    backCenter = new Point(Math.round(c[0]), Math.round(c[1]));
                    Imgproc.circle(frame, backCenter, 1, new Scalar(0, 100, 100), 3, 8, 0);
                    lastPositionBack = backCenter;
                } catch (NullPointerException e) {
                    backCenter = lastPositionBack;
                }
                try {
                    double[] c = backcircles.get(0, 0);
                    c = fromtcircles.get(0, 0);
                    frontCenter = new Point(Math.round(c[0]), Math.round(c[1]));
                    Imgproc.circle(frame, frontCenter, 1, new Scalar(0, 100, 100), 3, 8, 0);
                    lastPositionFront = frontCenter;
                } catch (NullPointerException e) {
                    //System.out.println("no circle found");
                    frontCenter = lastPositionFront;
                }

                ArrayList<Point> avgRobotFront = new ArrayList<Point>();
                final ArrayList<Point> avgRobotBack = new ArrayList<Point>();
                Point avgGoal2 = new Point();
                Point goal = new Point();
                //Point goal2 = new Point();
                ArrayList<Double> meanForGoal2x = new ArrayList<>();
                ArrayList<Double> meanForGoal2y = new ArrayList<>();
                for (int i = 0; i < tempImage.rows(); i++) {
                    for (int j = 0; j < tempImage.cols(); j++) {

                                /*if (tempImage3.get(i, j)[0] == 255) {
                                    combined.put(i, j, tempImage3.get(i, j)[0] );
                                    goal = new Point(j, i);

                                }*/

                        if (combined.get(i, j)[0] == 255) {
                            combined.put(i, j, combined.get(i, j)[0]);
                            goal2 = new Point(j, i);
                            meanForGoal2x.add(goal2.x);
                            meanForGoal2y.add(goal2.y);
                        }



                    }


                }

                try {
                    Collections.sort(meanForGoal2x);
                    Collections.sort(meanForGoal2y);
                    goal2.x = meanForGoal2x.get(meanForGoal2x.size() / 2);
                    goal2.y = meanForGoal2y.get(meanForGoal2y.size() / 2);
                } catch (IndexOutOfBoundsException e) {
                    //System.out.println("oops");
                }


                        /*Point robotVector = new Point(frontCenter.x - backCenter.x, frontCenter.y - backCenter.y);
                        Point bigGoalVector = new Point(goal2.x - frontCenter.x, goal2.y - frontCenter.y);
                        Imgproc.line(frame, goal2, backCenter,  new Scalar(250,0,0), 5);
                        Point a = robotVector;
                        Point b = bigGoalVector;
                        double dotProduct = (a.x*b.x)+(a.y*b.y);
                        double magnitudeOfA = Math.sqrt(Math.pow(a.x,2)+Math.pow(a.y,2));
                        double magnitudeOfB = Math.sqrt(Math.pow(b.x,2)+Math.pow(b.y,2));
                        Goalangle = Math.toDegrees(Math.acos(dotProduct/(magnitudeOfA*magnitudeOfB)));*/

                //getDirection();

                           /* Goalangle = Math.toDegrees(Math.cos((robotVector.x*bigGoalVector.x + robotVector.y*bigGoalVector.y)/
                                   (Math.sqrt(Math.pow(robotVector.x,2)+Math.pow(robotVector.y,2)))
                                           * Math.sqrt(Math.pow(bigGoalVector.x,2)*Math.pow(bigGoalVector.y,2))));*/



            /*if (frame.empty()) {
                System.out.println("1");
            }
            if (frame.type() == CV_8UC1) {
                System.out.println("2");
            }*/


                showGUI(cornerImage, combined);
            } while (programRunning);
            System.out.println("ComputerVision ended");
        }
    }

    public void drawOnImages(Point point1, Point point2, Scalar color) {

        Imgproc.line(frame, point1, point2, color, 5);

    }

    private void showGUI(Mat cornerImage, Mat combined){
        HighGui.imshow("SHIET SON", frame);
        HighGui.imshow("whatever2", cornerImage);
        HighGui.imshow("whatever4", combined);
        //HighGui.imshow("whatever", tempImage);
        //HighGui.imshow("whatever3", tempImage3);
        //HighGui.imshow("whatever5",tempImage5);
        HighGui.waitKey(1);
    }

    private ArrayList<Point> detectCorners(Mat cornerImage) throws IndexOutOfBoundsException {

        ArrayList<Double> medianX = new ArrayList<>();
        ArrayList<Double> medianY = new ArrayList<>();
        Point upperLeftCorner = new Point();
        Point upperRightCorner = new Point();
        Point lowerLeftCorner = new Point();
        Point lowerRightCorner = new Point();
        ArrayList<Point> corners = new ArrayList<>();

        //upper left corner
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                if (cornerImage.get(i, j)[0] == 255) {
                    upperLeftCorner = new Point(i, j);
                    medianX.add(upperLeftCorner.x);
                    medianY.add(upperLeftCorner.y);
                }
            }
        }
        Collections.sort(medianX);
        Collections.sort(medianY);
        upperLeftCorner.x = medianX.get(medianX.size() / 2);
        upperLeftCorner.y = medianY.get(medianY.size() / 2);
        corners.add(upperLeftCorner);
        medianX.clear();
        medianY.clear();

        //upper right corner
        for (int i = cornerImage.cols() - 50; i < cornerImage.cols(); i++) {
            for (int j = 0; j < 50; j++) {
                if (cornerImage.get(j, i)[0] == 255) {
                    upperRightCorner = new Point(i, j);
                    medianX.add(upperRightCorner.x);
                    medianY.add(upperRightCorner.y);

                }
            }
        }
        Collections.sort(medianX);
        Collections.sort(medianY);
        upperRightCorner.x = medianX.get(medianX.size() / 2);
        upperRightCorner.y = medianY.get(medianY.size() / 2);
        corners.add(upperRightCorner);
        medianX.clear();
        medianY.clear();

        //lower left corner
        for (int i = 0; i < 50; i++) {
            for (int j = cornerImage.rows() - 50; j < cornerImage.rows(); j++) {
                if (cornerImage.get(j, i)[0] == 255) {
                    lowerLeftCorner = new Point(i, j);
                    medianX.add(lowerLeftCorner.x);
                    medianY.add(lowerLeftCorner.y);
                }
            }
        }
        Collections.sort(medianX);
        Collections.sort(medianY);
        upperLeftCorner.x = medianX.get(medianX.size() / 2);
        upperLeftCorner.y = medianY.get(medianY.size() / 2);
        corners.add(lowerLeftCorner);
        medianX.clear();
        medianY.clear();
        //lower right corner
        for (int i = cornerImage.cols() - 50; i < cornerImage.cols(); i++) {
            for (int j = cornerImage.rows() - 50; j < cornerImage.rows(); j++) {
                if (cornerImage.get(j, i)[0] == 255) {
                    lowerRightCorner = new Point(i, j);
                    medianX.add(lowerRightCorner.x);
                    medianY.add(lowerRightCorner.y);
                }
            }
        }
        Collections.sort(medianX);
        Collections.sort(medianY);
        lowerRightCorner.x = medianX.get(medianX.size() / 2);
        lowerRightCorner.y = medianY.get(medianY.size() / 2);
        corners.add(lowerRightCorner);

        Scalar color = new Scalar(0,0,250);
        drawOnImages(upperLeftCorner,upperRightCorner,color);
        drawOnImages(upperRightCorner,lowerRightCorner,color);
        drawOnImages(lowerRightCorner,lowerLeftCorner,color);
        drawOnImages(lowerLeftCorner,upperLeftCorner,color);

        return corners;

    }

    public ArrayList<Point> getBallsLocation() {
        return ballsLocation;
    }

    public ArrayList<Point> getRobotLocation() {
        ArrayList<Point> robotLocation = new ArrayList<>();
        robotLocation.add(backCenter);
        robotLocation.add(frontCenter);
        return robotLocation;
    }

    //TODO: return both goal locations
    public Point getGoalsLocation() {
        return goal2;
    }

    public void setProgramRunning(Boolean bool){
        this.programRunning=bool;
    }

}
