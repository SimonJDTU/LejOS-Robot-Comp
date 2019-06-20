import org.opencv.core.Point;

import java.util.ArrayList;

public interface IComputerVision {

    void run();
    ArrayList<Point> getRobotLocation();
    ArrayList<Point> getBallsLocation();
    boolean cleanPath(Point startPoint, Point endPoint);
    Point ballsCloseToEdge(Point currentBall);
    boolean insideCircle(Point goal);
    public Point circleRotation(Point goal);

}
