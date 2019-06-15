import org.opencv.core.Point;

import java.util.ArrayList;

public interface IComputerVision {

    void run();
    Point getGoalsLocation();
    ArrayList<Point> getRobotLocation();
    ArrayList<Point> getBallsLocation();
    void setProgramRunning(Boolean bool);

}
