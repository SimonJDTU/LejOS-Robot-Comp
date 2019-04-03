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
        VideoCapture camera = new VideoCapture(4);
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
                    Imgproc.medianBlur(tempImage, tempImage, 5);

                    // Use HoughCircels to mark the balls
                    Mat circles = new Mat();
                    Imgproc.HoughCircles(tempImage, circles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows()/8, 25.0, 14.0, 15, 20);

                    for(int i = 0; i < circles.cols(); i++){
                        double[] c = circles.get(0, i);
                        Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                        Imgproc.circle(frame, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
                        int radius = (int) Math.round(c[2]);
                        Imgproc.circle(frame, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    }

                    HighGui.imshow("SHIET SON", frame);
                    HighGui.waitKey();

                    //BufferedImage image = t.MatToBufferedImage(circles);
                    //t.window(image, "asdfasdf", 0,0);
                    //frame.convertTo(tempImage, CV_8UC1, 1);
                    //t.window(image, "Original Image", 0, 0);
                    //saveImage(image);
                    //t.window(t.grayscale(image), "Processed Image", 40, 60);
                    //frame.convertTo(tempImage, CV_8UC1, 1.0/255, 0);
                    /*BufferedImage image2 = t.MatToBufferedImage(tempImage);
                    Mat two = new Mat();
                    System.out.println(frame.type());
                    System.out.println(tempImage.type());
                    System.out.println(CV_8UC1);
                    //tempImage.convertTo();
                    Imgproc.HoughCircles(frame, two, Imgproc.HOUGH_GRADIENT, 1, 200);*/

                    if(frame.empty()){
                        System.out.println("1");
                    }
                    if(frame.type() == CV_8UC1){
                        System.out.println("2");
                    }

                    //t.window(t.loadImage("ImageName"), "Image loaded", 0, 0);
                    //BufferedImage image2 = t.MatToBufferedImage(two);
                    //saveImage(image2);
                    //break;
                }

                counter++;
                if(counter==1){
                    break;
                }
            }
        }
        camera.release();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public ComputerVision() {
    }

    public ComputerVision(BufferedImage img) {
        image = img;
    }

    //Show image on window
    public void window(BufferedImage img, String text, int x, int y) {
        JFrame frame0 = new JFrame();
        frame0.getContentPane().add(new ComputerVision(img));
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame0.setTitle(text);
        frame0.setSize(img.getWidth(), img.getHeight() + 30);
        frame0.setLocation(x, y);
        frame0.setVisible(true);
    }

    //Load an image
    public BufferedImage loadImage(String file) {
        BufferedImage img;

        try {
            File input = new File(file);
            img = ImageIO.read(input);

            return img;
        } catch (Exception e) {
            System.out.println("erro");
        }

        return null;
    }
    //Save an image
    public static void saveImage(BufferedImage img) {
        try {
            File outputfile = new File("/home/dleh/A/new.png");
            ImageIO.write(img, "png", outputfile);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    //Grayscale filter
    public BufferedImage grayscale(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color c = new Color(img.getRGB(j, i));

                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);

                Color newColor =
                        new Color(
                                red + green + blue,
                                red + green + blue,
                                red + green + blue);

                img.setRGB(j, i, newColor.getRGB());
            }
        }

        return img;
    }

    public BufferedImage MatToBufferedImage(Mat frame) {
        //Mat() to BufferedImage

        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }
}
