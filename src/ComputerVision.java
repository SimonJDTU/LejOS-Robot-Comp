import nu.pattern.OpenCV;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

public class ComputerVision extends JPanel implements IComputerVision {

    private final boolean RUN_INFINITE = false;
    private final Point topLeft = new Point(73,43);
    private final Point topRight = new Point(593,39);
    private final Point botLeft = new Point(71,427);
    private final Point botRight = new Point(599,427);

    private Point frontCenter = new Point(), backCenter = new Point(), lastPositionFront = new Point(), lastPositionBack = new Point(), goal2 = new Point();
    private ArrayList<Point> ballsLocation = new ArrayList<>();
    private ArrayList<Point> balls = new ArrayList<>();
    private static ArrayList<Point> goodCorners = new ArrayList<>();
    private static ArrayList<ArrayList<Point>> ballConsistency = new ArrayList<>();
    private Mat frame;
    private VideoCapture camera;
    private Point centerPointCross = new Point();
    private Point returnPoint = new Point();
    private final int crossRadius = 90;

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
        Mat tempImage3;
        Mat tempImage5 = new Mat();
        Mat cornerImage = new Mat();
        MatOfPoint2f cornersOfFrame, cornersOfTrack;
        Mat circles = new Mat();
        Mat frontCircles = new Mat();
        Mat backcircles = new Mat();
        ArrayList<Point> corners;
        do {
            if (camera.read(frame)) {
                // Convert color for ball detection

                // Median Blur seams not to be importened for now. Umcomment and edit k size to use.
                //Imgproc.medianBlur(tempImage, tempImage, 11);

                // Normalize the image to increase and improve ball detection
                Imgproc.cvtColor(frame, cornerImage, COLOR_BGR2RGB);
                Imgproc.cvtColor(cornerImage, cornerImage, COLOR_RGB2GRAY);
                tempImage3 = cornerImage;
                HighGui.imshow("hmm", tempImage3);
                Imgproc.medianBlur(cornerImage, cornerImage, 7);

                inRange(cornerImage, new Scalar(0, 0, 0), new Scalar(70, 70, 70), cornerImage);
                // New Mat to detect colors
                Imgproc.cvtColor(frame, tempImage1, COLOR_BGR2HSV);
                Core.normalize(tempImage, tempImage1, 10, 200, Core.NORM_MINMAX, CV_8UC1);

                //Detect corners and do homography warp
                cornersOfTrack = new MatOfPoint2f(topLeft, topRight, botLeft, botRight);
                cornersOfFrame = new MatOfPoint2f(new Point(0, 0), new Point(640, 0), new Point(0, 480), new Point(640, 480));
                if(!RUN_INFINITE) {
                    warpPerspective(frame, frame, Calib3d.findHomography(cornersOfTrack, cornersOfFrame, Calib3d.RANSAC, 4), frame.size());
                }else{
                    System.out.println(MouseInfo.getPointerInfo().getLocation());
                }
                /*
                try {
                    corners = detectCorners(cornerImage);
                    if (corners.size() == 4) {
                        goodCorners = corners;
                        cornersOfTrack = new MatOfPoint2f(corners.get(0), corners.get(1), corners.get(2), corners.get(3));
                        cornersOfFrame = new MatOfPoint2f(new Point(0, 0), new Point(640, 0), new Point(0, 480), new Point(640, 480));
                        warpPerspective(frame, frame, Calib3d.findHomography(cornersOfTrack, cornersOfFrame, Calib3d.RANSAC, 4), frame.size());
                    }

                } catch (IndexOutOfBoundsException e) {
                    try{
                        corners = goodCorners;
                        cornersOfTrack = new MatOfPoint2f(corners.get(0), corners.get(1), corners.get(2), corners.get(3));
                        cornersOfFrame = new MatOfPoint2f(new Point(0, 0), new Point(640, 0), new Point(0, 480), new Point(640, 480));
                        warpPerspective(frame, frame, Calib3d.findHomography(cornersOfTrack, cornersOfFrame, Calib3d.RANSAC, 4), frame.size());
                        System.out.println("Lost Corner(s)");
                    }catch(IndexOutOfBoundsException ex){
                        ex.printStackTrace();
                    }
                }*/



                tempImage = frame.clone();

                Imgproc.cvtColor(frame, tempImage, COLOR_BGR2GRAY);
                Core.normalize(tempImage, tempImage, 60, 200, Core.NORM_MINMAX, CV_8UC1);

                // Detect balls
                Imgproc.HoughCircles(tempImage, circles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows() / 100, 50.0, 20.0, 4, 9);  // save values 50, 50, 25,10,23

                // Detect Robot
                Imgproc.HoughCircles(tempImage, frontCircles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows() / 100, 50.0, 19.0, 10, 15);  // save values 50, 50, 25,10,23
                Imgproc.HoughCircles(tempImage, backcircles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows() / 100, 50.0, 19.0, 15, 30);  // save values 50, 50, 25,10,23
                try {
                    double[] c = backcircles.get(0, 0);
                    backCenter = new Point(Math.round(c[0]), Math.round(c[1]));
                    Imgproc.circle(frame, backCenter, 1, new Scalar(0, 100, 100), 3, 8, 0);
                    lastPositionBack = backCenter;
                } catch (NullPointerException e) {
                    backCenter = lastPositionBack;
                }

                try {
                    double[] c;
                    c = frontCircles.get(0, 0);
                    frontCenter = new Point(Math.round(c[0]), Math.round(c[1]));
                    Imgproc.circle(frame, frontCenter, 1, new Scalar(0, 100, 100), 3, 8, 0);
                    lastPositionFront = frontCenter;
                } catch (NullPointerException e) {
                    frontCenter = lastPositionFront;
                }


                Imgproc.cvtColor(frame, tempImage5, COLOR_BGR2HSV);

                //Core.inRange(tempImage2,new Scalar(0,0,0),new Scalar(250,250,180),tempImage2);

                //Detect cross ???
                Imgproc.cvtColor(frame, tempImage1, COLOR_BGR2HSV);
                inRange(tempImage1, new Scalar(0, 220, 220), new Scalar(20, 255, 255), tempImage1);
                HighGui.imshow("HSV", tempImage1);


                ArrayList<Double> crossMedianX = new ArrayList<>();
                ArrayList<Double> crossMedianY = new ArrayList<>();

                try {
                    for (int i = 25; i < tempImage1.cols() - 25; i++) {
                        for (int j = 25; j < tempImage1.rows() - 25; j++) {
                            if (tempImage1.get(j, i)[0] == 255) {
                                centerPointCross = new Point(i, j);
                                crossMedianX.add(centerPointCross.x);
                                crossMedianY.add(centerPointCross.y);

                            }
                        }
                    }
                    Collections.sort(crossMedianX);
                    Collections.sort(crossMedianY);
                    centerPointCross.x = crossMedianX.get(crossMedianX.size() / 2);
                    centerPointCross.y = crossMedianY.get(crossMedianY.size() / 2);
                    crossMedianX.clear();
                    crossMedianY.clear();
                    Imgproc.line(frame, centerPointCross, centerPointCross, new Scalar(255, 255, 255), 5);
                    Imgproc.circle(frame, centerPointCross, crossRadius, new Scalar(255, 100, 100), 7, 8, 0);

                    //Cross goalDot
                    Imgproc.circle(frame, returnPoint, 1, new Scalar(80, 43, 229), 7, 8, 0);

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }


                //inRange(tempImage8, new Scalar(25, 80, 245), new Scalar(40, 100, 255), combined);

                //Colorize circles
                ballsLocation.clear();
                balls.clear();

                for (int i = 0; i < circles.cols(); i++) {
                    double[] c = circles.get(0, i);
                    Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                    ballsLocation.add(center);
                }

                ballConsistency.add(0, ballsLocation);
                if (ballConsistency.size() >= 5) {
                    ballConsistency.remove(ballConsistency.size() - 1);
                }

                for (ArrayList<Point> points : ballConsistency) {
                    for (Point point : points) {
                        if (!balls.contains(point)) {
                            balls.add(point);
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
                for (Point ball : balls) {
                    circle(frame, ball, 1, new Scalar(255, 100, 100), 7, 8, 0);
                }

                HighGui.imshow("HSV", tempImage5);
                showGUI();
            } else {
                System.out.println("No picture taken");
            }
        } while (RUN_INFINITE);
    }

    private void drawOnImages(Point point1, Point point2, Scalar color) {

        Imgproc.line(frame, point1, point2, color, 5);

    }

    private void showGUI() {

        //draw help corners
        if(RUN_INFINITE){
            Imgproc.line(frame, topLeft, topLeft, new Scalar(255, 0, 255), 5);
            Imgproc.line(frame, topRight, topRight, new Scalar(255, 0, 255), 5);
            Imgproc.line(frame, botLeft, botLeft, new Scalar(255, 0, 255), 5);
            Imgproc.line(frame, botRight, botRight, new Scalar(255, 0, 255), 5);
        } else {
            //drawing ballCloseToEdge
            Imgproc.line(frame, new Point(50, 50), new Point(50, 430), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(50, 430), new Point(590, 430), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(590, 430), new Point(590, 50), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(590, 50), new Point(50, 50), new Scalar(0, 255, 0), 5);

            //safety points
            Imgproc.line(frame, new Point(95, 95), new Point(95, 95), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(320, 95), new Point(320, 95), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(545, 95), new Point(545, 95), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(545, 240), new Point(545, 240), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(545, 385), new Point(545, 385), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(320, 385), new Point(320, 385), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(95, 385), new Point(95, 385), new Scalar(0, 255, 0), 5);
            Imgproc.line(frame, new Point(95, 240), new Point(95, 240), new Scalar(0, 255, 0), 5);

            //goal
            Imgproc.line(frame, new Point(540, 240), new Point(540, 240), new Scalar(0, 0, 255), 5);
        }





        HighGui.imshow("SHIET SON", frame);
        //HighGui.imshow("whatever2", cornerImage);
        //hGui.imshow("whatever", tempImage);
        //HighGui.imshow("whatever3", tempImage3);
        //HighGui.imshow("whatever5",tempImage5);
        HighGui.waitKey(1);
    }

    public boolean cleanPath(Point startPoint, Point endPoint) {

        double a, b, c, S;
        double x0, x1, y0, y1;
        double k, h, r;

        // Roberts vector
        x0 = startPoint.x;
        y0 = startPoint.y;

        // Ball Vector
        x1 = endPoint.x;
        y1 = endPoint.y;

        //Circel center and radius
        h = centerPointCross.x;
        k = centerPointCross.y;
        r = crossRadius;

        a = Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2);

        b = 2 * (x1 - x0) * (x0 - h) + 2 * (y1 - y0) * (y0 - k);

        c = Math.pow(x0 - h, 2) + Math.pow(y0 - k, 2) - Math.pow(r, 2);

        S = Math.pow(b, 2) - 4 * a * c;

        if (S < 0) {
            System.out.println("CLEANPATH: x=" + endPoint.x + " y=" + endPoint.y +" : TRUE");
            return true;
        }
        System.out.println("CLEANPATH: x=" + endPoint.x + " y=" + endPoint.y +" : FALSE");
        return false;
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
                    upperLeftCorner = new Point(j, i);
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
        lowerLeftCorner.x = medianX.get(medianX.size() / 2);
        lowerLeftCorner.y = medianY.get(medianY.size() / 2);
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

        Scalar color = new Scalar(0, 0, 250);

        drawOnImages(upperLeftCorner, upperRightCorner, color);
        drawOnImages(upperRightCorner, lowerRightCorner, color);
        drawOnImages(lowerRightCorner, lowerLeftCorner, color);
        drawOnImages(lowerLeftCorner, upperLeftCorner, color);

        return corners;

    }

    public Point circleRotation(Point goal) {
        double x0, x1, y0, y1;
        final int factor = 8;

        x0 = centerPointCross.x;
        y0 = centerPointCross.y;

        x1 = goal.x;
        y1 = goal.y;

        returnPoint = new Point((x0 - (x0 - x1) * factor), (y0 - (y0 - y1) * factor));
        double vectorLength = Math.sqrt(Math.pow(returnPoint.x - centerPointCross.x, 2) + Math.pow(returnPoint.y - centerPointCross.y, 2));

        if (vectorLength > 150) {
            Point vectorDisc = new Point(x1 - x0, y1 - y0);
            double vectorDiscLength = Math.sqrt(Math.pow(vectorDisc.x, 2) + Math.pow(vectorDisc.y, 2));
            double vectorFactor = 150 / vectorDiscLength;
            vectorDisc.x *= vectorFactor;
            vectorDisc.y *= vectorFactor;
            returnPoint = new Point(vectorDisc.x += centerPointCross.x, vectorDisc.y += centerPointCross.y);
            return returnPoint;
        }
        return returnPoint;

    }

    public boolean insideCircle(Point goal) {
        double x0, x1, y0, y1, d;
        x0 = centerPointCross.x;
        y0 = centerPointCross.y;
        x1 = goal.x;
        y1 = goal.y;
        d = Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
        if (d <= crossRadius) {
            return true;
        }
        return false;
    }

    public ArrayList<Point> getBallsLocation() {
        return balls;
    }

    public ArrayList<Point> getRobotLocation() {
        ArrayList<Point> robotLocation = new ArrayList<>();
        robotLocation.add(backCenter);
        robotLocation.add(frontCenter);
        return robotLocation;
    }

    public Point ballsCloseToEdge(Point currentBall) {
        Point closeToEdge = new Point();
        int safetyDistance = 100;
        int detectionDistance = 50;
        int ySmallFromEdge = detectionDistance;
        int xBigFromEdge = 640-detectionDistance;
        int yBigFromEdge = 480-detectionDistance;

        //top left corner
        if (currentBall.x < detectionDistance && currentBall.y < ySmallFromEdge) {
            closeToEdge.x = currentBall.x + safetyDistance;
            closeToEdge.y = currentBall.y + safetyDistance;
            return closeToEdge;
            //top right corner
        } else if (currentBall.x > xBigFromEdge && currentBall.y < ySmallFromEdge) {
            closeToEdge.x = currentBall.x - safetyDistance;
            closeToEdge.y = currentBall.y + safetyDistance;
            return closeToEdge;
            //bottom right corner
        } else if (currentBall.x > xBigFromEdge && currentBall.y > yBigFromEdge) {
            closeToEdge.x = currentBall.x - safetyDistance;
            closeToEdge.y = currentBall.y - safetyDistance;
            return closeToEdge;
            //bottom left corner
        } else if (currentBall.x < detectionDistance && currentBall.y > yBigFromEdge) {
            closeToEdge.x = currentBall.x + safetyDistance;
            closeToEdge.y = currentBall.y - safetyDistance;
            return closeToEdge;
            //bottom
        } else if (currentBall.y > yBigFromEdge) {
            closeToEdge.x = currentBall.x;
            closeToEdge.y = currentBall.y - safetyDistance;
            return closeToEdge;
            //top
        } else if (currentBall.y < ySmallFromEdge) {
            closeToEdge.x = currentBall.x;
            closeToEdge.y = currentBall.y + safetyDistance;
            return closeToEdge;
            //left
        } else if (currentBall.x < detectionDistance) {
            closeToEdge.x = currentBall.x + safetyDistance;
            closeToEdge.y = currentBall.y;
            return closeToEdge;

        } else if (currentBall.x > xBigFromEdge) {
            closeToEdge.x = currentBall.x - safetyDistance;
            closeToEdge.y = currentBall.y;
            return closeToEdge;
        }
        return null;
    }

}
