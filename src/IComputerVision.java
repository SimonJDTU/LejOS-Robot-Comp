import org.opencv.core.Point;

import java.util.ArrayList;

public interface IComputerVision {

    void run();
    ArrayList<Point> getRobotLocation();
    ArrayList<Point> getBallsLocation();

}
