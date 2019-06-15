import org.opencv.core.Point;

import java.util.ArrayList;

/*MovementMananger crates instances of ComputerVision and Robot. The class uses points given by ComputerVision
to calculate distances and angles to turn, where it afterwards sends the commands to the robot. */
public class MovementManager {

    private IComputerVision cv = new ComputerVision();
    private Client client;
    private int PORT_CONNECTION=5000;
    private String IP_C0NNECTION="192.168.43.181";

    MovementManager() {
        this.client = new Client(IP_C0NNECTION, PORT_CONNECTION);
    }

    //TODO: set up waiting system, instead of taking images.
    void run() {
        int ballCaught = 0;
        Point closetsBall;
        ArrayList<Point>  robotLocation;

        cv.run();
        do{
            if (ballCaught>=3) {
                delieverToGoal();
                ballCaught=0;
            } else {
                //TODO: Make it so it recalculates the angle and distance close to the ball
                closetsBall = getClosestBall();
                robotLocation = cv.getRobotLocation();
                turnDegrees(calcAngle(robotLocation,closetsBall));
                moveDistance(calcDistance(robotLocation,closetsBall));
                ballCaught++;
            }
        }while (!(numberOfBalls()==0));
        this.cv.setProgramRunning(false);
        System.out.println("Program ended");
    }

    private void moveDistance(double distance) {
        String command;
        command = "1-" + (int) ((distance - 30) / 3.844);
        //command = "1-30";
        client.sendMessage(command);
    }

    private void turnDegrees(double[] degrees) {
        String command;
        if (degrees[0] == 1) {
            //turn left
            command = "3-" + (int) degrees[1];
        } else {
            command = "2-" + (int) degrees[1];
        }
        client.sendMessage(command);
    }

    //returns a double array which contains the degrees of turn to match the vectors and whether it's positive or negative
    private double[] calcAngle(ArrayList<Point> robotPoint, Point goal){
        double[] directions = new double[2];
        if(goal.x > 10 && goal.y > 10){
            Point robotVector = new Point(robotPoint.get(1).x - robotPoint.get(0).x, robotPoint.get(1).y - robotPoint.get(0).y);
            Point bigGoalVector = new Point(goal.x - robotPoint.get(0).x, goal.y - robotPoint.get(0).y);

            double dotProduct = (robotVector.x*bigGoalVector.x)+(robotVector.y*bigGoalVector.y);
            double magnitudeOfA = Math.sqrt(Math.pow(robotVector.x,2)+Math.pow(robotVector.y,2));
            double magnitudeOfB = Math.sqrt(Math.pow(bigGoalVector.x,2)+Math.pow(bigGoalVector.y,2));
            double goalAngle = Math.toDegrees(Math.acos(dotProduct/(magnitudeOfA*magnitudeOfB)));
            directions[0] = goalAngle;

            //if result > 0 robot turns left, else it turns right
            double result = ((goal.x - robotPoint.get(0).x) * (robotPoint.get(1).y - robotPoint.get(0).y)) - ((goal.y - robotPoint.get(0).y) * (robotPoint.get(1).x - robotPoint.get(0).x));
            if(result > 0){
                directions[1] = 1;
            }else{
                directions[1] = 0;
            }
        }
        return directions;
    }

    private int numberOfBalls(){
        return cv.getBallsLocation().size();
    }

    private Point getClosestBall(){
        //it is assumes that the back of the robot is the center.
        ArrayList<Point> robotPoint=cv.getRobotLocation();
        Point returnPoint = new Point(340,240);
        double minDist = 1000000;
        for(Point ball : cv.getBallsLocation()){
            double distance = Math.sqrt(Math.pow(ball.x-robotPoint.get(0).x, 2) + Math.pow(ball.y - robotPoint.get(0).y, 2));
            if(distance < minDist){
                minDist = distance;
                returnPoint=ball;
            }
        }
        return returnPoint;
    }

    private double calcDistance(ArrayList<Point> robotPoint, Point goal){
        return Math.sqrt(Math.pow(goal.x - robotPoint.get(0).x, 2) + Math.pow(goal.y - robotPoint.get(0).y, 2));
    }

    //TODO: calculate the closets goal and take action accordingly
    //TODO: add so that we go to a point near the goal and turn to deliever the balls.
    public void delieverToGoal(){
        //Point DeliveryPoint = new Point(0, 480.0/2.0);
        double[] directions=calcAngle(cv.getRobotLocation(), cv.getGoalsLocation());
        //goal.x += 70;
    }
}
