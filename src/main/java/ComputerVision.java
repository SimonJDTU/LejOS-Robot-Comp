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

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.*;

import java.nio.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static org.opencv.core.CvType.CV_8UC1;

public class ComputerVision extends JPanel{
    BufferedImage image;

    public static void main(String[] args) throws InterruptedException {

        // Loading core libary to get accesses to the camera
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ComputerVision t = new ComputerVision();

        // Capturing from usb Camera
        // USB CAM index 4 , own i 0
        VideoCapture camera = new VideoCapture(0);
        //camera.open("/dev/v41/by-id/usb-046d_Logitech_Webcam_C930e_DDCF656E-video-index0");

        // New Mat frame
        Mat frame = new Mat();

        // Show the mat frame
        System.out.println(frame.type());
        camera.read(frame);

        if(!camera.isOpened()){
            System.out.println("Error");
        }
        else {
            int counter=0;
            while(true){

                if (camera.read(frame)){

                    //BufferedImage image = t.MatToBufferedImage(frame);

                    // New Picture
                    Mat tempImage = new Mat();

                    // Convert color
                    Imgproc.cvtColor(frame, tempImage,Imgproc.COLOR_BGR2GRAY);
                    Imgproc.medianBlur(tempImage, tempImage, 15);
                    //Imgproc.GaussianBlur(tempImage, tempImage, new Size(45,45),2,2);
                    Core.normalize(tempImage,tempImage,50,200,Core.NORM_MINMAX, CV_8UC1);


                    // Use HoughCircels to mark the balls
                    Mat circles = new Mat();
                    Imgproc.HoughCircles(tempImage, circles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows()/50, 80.0, 25.0, 10, 23);  // save values 50, 50, 25,10,23

                    for(int i = 0; i < circles.cols(); i++){
                        double[] c = circles.get(0, i);
                        Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                        Imgproc.circle(frame, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
                        int radius = (int) Math.round(c[2]);
                        Imgproc.circle(frame, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    }

                    HighGui.imshow("SHIET SON", frame);
                    HighGui.imshow("whatever", tempImage);
                    HighGui.waitKey(1);


                    if(frame.empty()){
                        System.out.println("1");
                    }
                    if(frame.type() == CV_8UC1){
                        System.out.println("2");
                    }


                    System.out.println("out1+ " + circles.cols());
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

    public ComputerVision() {
    }
}
