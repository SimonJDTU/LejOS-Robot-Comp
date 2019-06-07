import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.*;

import java.nio.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Scalar;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

public class ComputerVision extends JPanel{
    BufferedImage image;

    public static void main(String[] args) throws InterruptedException {

        // Loading core libary to get accesses to the camera
        OpenCV.loadLocally();
        // If not load correctly try:
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //ComputerVision t = new ComputerVision();


        // Capturing from usb Camera
        // USB CAM index 4 , own is 0
        VideoCapture camera = new VideoCapture(1);
        //camera.open("/dev/v41/by-id/usb-046d_Logitech_Webcam_C930e_DDCF656E-video-index0");

        // Set resulution
        //1280 - 720        //640 - 480
        camera.set(CV_CAP_PROP_FRAME_WIDTH,640);
        camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);

        // New Mat frame
        Mat frame = new Mat();

        // Show the mat frame
        System.out.println(frame.type());
        camera.read(frame);

        if(!camera.isOpened()){
            System.out.println("Error");
        }
        else
            {
            while(true)
            {

                if (camera.read(frame))
                {

                    // New Picture
                    Mat tempImage = new Mat();
                    Mat tempImage2 = new Mat();
                    Mat tempImage3 = new Mat();
                    Mat tempImage4 = new Mat();
                    Mat tempImage5 = new Mat();
                    Mat tempImage6 = new Mat();
                    Mat combined = new Mat();

                    // Convert color
                    Imgproc.cvtColor(frame, tempImage,Imgproc.COLOR_BGR2GRAY);
                    Imgproc.medianBlur(tempImage, tempImage, 5);
                    Core.normalize(tempImage,tempImage,10,200,Core.NORM_MINMAX, CV_8UC1);


                    // Use HoughCircels to mark the balls
                    Mat circles = new Mat();

                    Imgproc.HoughCircles(tempImage, circles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows()/100, 50.0, 19.0, 4, 9);  // save values 50, 50, 25,10,23

                    //New Mat to detect colors
                    Imgproc.cvtColor(frame, tempImage2, COLOR_BGR2HSV);
                    Imgproc.cvtColor(frame, tempImage3, COLOR_BGR2HSV);
                    Imgproc.cvtColor(frame, tempImage4, COLOR_BGR2HSV);
                    Imgproc.cvtColor(frame, tempImage5, COLOR_BGR2HSV);
                    Imgproc.cvtColor(frame, tempImage6, COLOR_BGR2HSV);
                    //Core.inRange(tempImage2,new Scalar(0,0,0),new Scalar(250,250,180),tempImage2);
                    //borders
                    inRange(tempImage2,new Scalar(0,170,170) ,new Scalar(190,255,255),tempImage2);

                    //Goals
                    inRange(tempImage3,new Scalar(30,40,240) ,new Scalar(45,60,255),tempImage3);
                    

                    inRange(tempImage4,new Scalar(85,20,230) ,new Scalar(100,40,255),tempImage4);

                    //Robot
                    Core.inRange(tempImage5,new Scalar(120,100,150),new Scalar(135,125,180),tempImage5);


                    //System.out.println();
                    for(int i = 0; i < circles.cols(); i++){
                        double[] c = circles.get(0, i);
                        Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                        Imgproc.circle(frame, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
                        int radius = (int) Math.round(c[2]);
                        Imgproc.circle(frame, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    }

                    Point robot = new Point();
                    Point goal = new Point();
                    combined = tempImage2;
                    //A.convertTo(A, CvType.CV_64FC3);


                    for (int i = 0; i<tempImage.rows(); i++) {
                        for(int j=0; j<tempImage.cols(); j++){
                           // double[] pixels = tempImage5.get(i,j);
                            //double[] somePixes = tempImage3.get(i,j);
                           // pixels[0] = pixels[0];
                            //somePixes[0] = somePixes[0];
                            if(tempImage2.get(i,j)[0] == 255){
                                combined.put(i,j,tempImage2.get(i,j)[0]-1);
                            }

                            if (tempImage3.get(i,j)[0] == 255){
                                combined.put(i,j,tempImage3.get(i,j)[0]-2);
                                goal = new Point(i,j);
                            }

                            if(tempImage4.get(i,j)[0] == 255){
                                combined.put(i,j,tempImage4.get(i,j)[0]-3);
                            }
                            if(tempImage5.get(i,j)[0]==255){
                                combined.put(i,j,tempImage5.get(i,j)[0]-4);
                                robot = new Point(i,j);
                            }
                            if(tempImage6.get(i,j)[0] == 255){
                                combined.put(i,j,tempImage6.get(i,j)[0]-5);
                            }





                            //System.out.println(dis);

                        }

                    }
                    double dis;

                    dis=Math.sqrt((goal.x-robot.x)*(goal.x-robot.x) + (goal.y-robot.y)*(goal.y-robot.y));

                    System.out.println(dis);


                    //1
                    //2
                    //4
                    //8
                    //16
                    //32
                    //64
                    //128
                    //HighGui.imshow("SHIET SON", frame);
                    //HighGui.imshow("whatever", tempImage);
                    //HighGui.imshow("whatever2", tempImage2);
                    //HighGui.imshow("whatever3", tempImage3);
                    HighGui.imshow("whatever4", combined);
                    //HighGui.imshow("whatever5",tempImage5);
                    HighGui.waitKey(1);


                    if(frame.empty())
                    {
                        System.out.println("1");
                    }
                    if(frame.type() == CV_8UC1)
                    {
                        System.out.println("2");
                    }


                    //System.out.println("Circles:  " + circles.cols());
                }


            }

        }
        System.out.println("out");
        camera.release();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public ComputerVision()
    {
    }
}
