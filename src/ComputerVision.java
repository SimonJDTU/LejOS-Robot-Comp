import nu.pattern.OpenCV;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_xfeatures2d;
import org.opencv.calib3d.Calib3d;
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

public class ComputerVision extends JPanel implements IComputerVision {

    private Point frontCenter = new Point(), backCenter = new Point(), lastPositionFront = new Point(), lastPositionBack = new Point(), goal2 = new Point();
    private ArrayList<Point> ballsLocation = new ArrayList<>();
    private ArrayList<Point> balls = new ArrayList<>();
    private static ArrayList<ArrayList<Point>> ballConsistency = new ArrayList<>();
    private Mat frame;
    private VideoCapture camera;
    private Point centerPointCross = new Point();

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
        Mat frontCircles = new Mat();
        Mat backcircles = new Mat();
        ArrayList<Point> corners;

            if (camera.read(frame)) {

                // Convert color for ball detection

                // Median Blur seams not to be importened for now. Umcomment and edit k size to use.
                //Imgproc.medianBlur(tempImage, tempImage, 11);

                // Normalize the image to increase and improve ball detection
                Imgproc.cvtColor(frame, cornerImage, COLOR_BGR2RGB);
                Imgproc.cvtColor(cornerImage, cornerImage, COLOR_RGB2GRAY);
                Imgproc.medianBlur(cornerImage, cornerImage, 7);

                inRange(cornerImage, new Scalar(10, 10, 10), new Scalar(60, 60, 60), cornerImage);
                // New Mat to detect colors
                Imgproc.cvtColor(frame, tempImage1, COLOR_BGR2HSV);
                Mat tempImage8 = tempImage1.clone();
                Core.normalize(tempImage, tempImage1, 10, 200, Core.NORM_MINMAX, CV_8UC1);

                //Detect corners and do homography warp
                try {
                    corners = detectCorners(cornerImage);
                    cornersOfTrack = new MatOfPoint2f(corners.get(0), corners.get(1), corners.get(2), corners.get(3));
                    cornersOfFrame = new MatOfPoint2f(new Point(0, 0), new Point(640, 0), new Point(0, 480), new Point(640, 480));
                    warpPerspective(frame, frame, Calib3d.findHomography(cornersOfTrack, cornersOfFrame, Calib3d.RANSAC, 4), frame.size());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("unlucky");
                }
                //????????????????? goal??

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
                    //System.out.println("no circle found");
                    frontCenter = lastPositionFront;
                }


                Imgproc.cvtColor(frame, tempImage5, COLOR_BGR2HSV);

                //Core.inRange(tempImage2,new Scalar(0,0,0),new Scalar(250,250,180),tempImage2);
                //Detect borders ???
                Imgproc.cvtColor(frame, tempImage1, COLOR_BGR2HSV);
                inRange(tempImage1, new Scalar(0, 220, 220), new Scalar(20, 255, 255), tempImage1);
                HighGui.imshow("hallo", tempImage1);


                ArrayList<Double> crossMedianX = new ArrayList<>();
                ArrayList<Double> crossMedianY = new ArrayList<>();

                try {
                    for (int i = 100; i < tempImage1.cols() - 100; i++) {
                        for (int j = 100; j < tempImage1.rows() - 100; j++) {
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
                    circle(frame, centerPointCross, 70, new Scalar(255, 100, 100), 7, 8, 0);
                }catch(IndexOutOfBoundsException e){

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
                if (ballConsistency.size() >= 10) {
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
                //System.out.println("amount of balls: " + balls.size());
                for (Point ball : balls) {
                    circle(frame, ball, 1, new Scalar(255, 100, 100), 7, 8, 0);
                }

                //HighGui.imshow("HSV", tempImage5);
                showGUI(cornerImage);
            } else {
                System.out.println("No picture taken");
            }
            System.out.println("ComputerVision ended");
        }

    public void drawOnImages(Point point1, Point point2, Scalar color) {

        Imgproc.line(frame, point1, point2, color, 5);

    }

    private void showGUI(Mat cornerImage){
        Imgproc.line(frame, new Point(540, 240), new Point(540, 240), new Scalar(0, 0, 255), 5);
        Imgproc.line(frame, new Point(100, 240), new Point(100, 240), new Scalar(255, 0, 0), 5);
        HighGui.imshow("SHIET SON", frame);
        //HighGui.imshow("whatever2", cornerImage);
        //hGui.imshow("whatever", tempImage);
        //HighGui.imshow("whatever3", tempImage3);
        //HighGui.imshow("whatever5",tempImage5);
        HighGui.waitKey(1);
    }

    public boolean cleanPath(Point goal){

            double a , b , t , c, S;
            double x0 , x1 , y0 , y1;
            double k , h , r;
            //double x , y, R;

        // Roberts vector
         x0=getRobotLocation().get(0).x;
         y0=getRobotLocation().get(0).y;

        // Ball Vector
        x1=goal.x;
        y1=goal.y;

        //Circel center and radius
            h = centerPointCross.x;
            k = centerPointCross.y;
            r = 70;


            //R = Math.sqrt(Math.pow(x-h,2)+ Math.pow(y-k,2));

            a = Math.pow(x1-x0,2) + Math.pow(y1-y0,2);

            b = 2*(x1-x0)*(x0-h)+2*(y1-y0)*(y0-k);

            c = Math.pow(x0-h,2)+Math.pow(y0-k,2)-Math.pow(r,2);

            S = Math.pow(b, 2)-4*a*c;

            if(S < 0)
            {
                System.out.println("--------------------");
                System.out.println("--Free togo Robert--");
                System.out.println("--------------------");
                return true;
            }
                System.out.println("--------------------");
                System.out.println("--Dont You fucking--");
                System.out.println("----Drive Robert----");
                System.out.println("--------------------");
                return false;


            //Circels possition

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

        Scalar color = new Scalar(0,0,250);

        drawOnImages(upperLeftCorner,upperRightCorner,color);
        drawOnImages(upperRightCorner,lowerRightCorner,color);
        drawOnImages(lowerRightCorner,lowerLeftCorner,color);
        drawOnImages(lowerLeftCorner,upperLeftCorner,color);

        return corners;

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

    public void setProgramRunning(Boolean bool){
    }

    public Point ballsCloseToEdge(Point currentBall){
        Point closeToEdge = new Point();
        int safetyDistance = 100;
        if(currentBall.x < 100 && currentBall.y < 100){
            closeToEdge.x = currentBall.x+safetyDistance;
            closeToEdge.y = currentBall.y+safetyDistance;
        } else if(currentBall.x > 560 && currentBall.y < 100){
            closeToEdge.x = currentBall.x-safetyDistance;
            closeToEdge.y = currentBall.y+safetyDistance;
        } else if(currentBall.x > 560 && closeToEdge.y > 380){
            closeToEdge.x = currentBall.x-safetyDistance;
            closeToEdge.y = currentBall.y-safetyDistance;
        }else if (currentBall.x < 100 && currentBall.y > 380){
            closeToEdge.x = currentBall.x+safetyDistance;
            closeToEdge.y = currentBall.y-safetyDistance;
        } else if(currentBall.y > 440){
            closeToEdge.x = currentBall.x;
            closeToEdge.y = currentBall.y-safetyDistance;
            return closeToEdge;
        }else if(currentBall.y < 40){
            closeToEdge.x = currentBall.x;
            closeToEdge.y = currentBall.y+safetyDistance;
            return closeToEdge;
        }else if(currentBall.x < 40){
            closeToEdge.x = currentBall.x+safetyDistance;
            closeToEdge.y = currentBall.y;
            return  closeToEdge;
        }else if(currentBall.x > 600){
            closeToEdge.x = currentBall.x-safetyDistance;
            closeToEdge.y = currentBall.y;
            return  closeToEdge;
        }
        return null;
    }

}
