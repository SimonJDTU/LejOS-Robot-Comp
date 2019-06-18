import org.opencv.core.Point;

import java.util.ArrayList;

/*MovementMananger crates instances of ComputerVision and Robot. The class uses points given by ComputerVision
to calculate distances and angles to turn, where it afterwards sends the commands to the robot. */
public class MovementManager {

    private Client client;
    private int PORT_CONNECTION = 5000;
    private String IP_C0NNECTION = "192.168.43.181";
    private ComputerVision cv = new ComputerVision();
    private boolean goToEdgeBall = true;
    private int ballCaught = 0;
    private final Point smallGoalDot = new Point(560, 240);
    private final Point bigGoalDot = new Point(80, 240);
    private final Point smallGoal = new Point(639, 240);
    private final Point bigGoal = new Point(1, 240);


    MovementManager() {
        this.client = new Client();
        this.client.startConnection(IP_C0NNECTION, PORT_CONNECTION);
    }

    //TODO: set up waiting system, instead of taking images.
    void run() {

        Point closetsBall;
        ArrayList<Point> robotLocation;
        for (int i = 0; i < 10; i++) {
            cv.run();
        }

        do {
            if (ballCaught >= 3 ||(numberOfBalls()==0 && ballCaught>0)) {
                System.out.println("Goal delivery hit");
                for (int i = 0; i < 10; i++) {
                    cv.run();
                }
                Point goal = closetsGoal();
                turnDegrees(angleToGoal(goal));
                moveDistance(distanceToGoal(goal), 0, 0);

                for (int i = 0; i < 10; i++) {
                    cv.run();
                }
                turnDegrees(turnToFaceGoal(goal));
                depositballs();
                ballCaught = 0;
            } else {
                //TODO: Make it so it recalculates the angle and distance close to the ball
                for (int i = 0; i < 10; i++) {
                    cv.run();
                }
                closetsBall = getClosestBall();
                cv.cleanPath(closetsBall);
                robotLocation = cv.getRobotLocation();
                if (cv.ballsCloseToEdge(closetsBall) == null) {
                    turnDegrees(calcAngle(robotLocation, closetsBall));
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    if (calcDistance(robotLocation, closetsBall) >= 20) {
                        moveDistance(calcDistance(robotLocation, closetsBall), 0, 15);
                        for (int i = 0; i < 10; i++) {
                            cv.run();
                        }
                    }

                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    closetsBall = getClosestBall();
                    robotLocation = ((ComputerVision) cv).getRobotLocation();
                    turnDegrees(calcAngle(robotLocation, closetsBall));
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    moveDistance(calcDistance(robotLocation, closetsBall), 15, 0);
                    client.sendMessage("6-20");
                    waitForRobot();

                    ballCaught++;
                } else {
                    turnDegrees(calcAngle(robotLocation, closetsBall));
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    if (calcDistance(robotLocation, closetsBall) >= 20) {
                        moveDistance(calcDistance(robotLocation, closetsBall), 0, 0);
                        for (int i = 0; i < 10; i++) {
                            cv.run();
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    closetsBall = getClosestBall();
                    robotLocation = ((ComputerVision) cv).getRobotLocation();
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    turnDegrees(calcAngle(robotLocation, closetsBall));
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }
                    moveDistance(calcDistance(robotLocation, closetsBall), 13, 0);
                    for (int i = 0; i < 10; i++) {
                        cv.run();
                    }

                    ballCaught++;
                }
            }
        } while (!(numberOfBalls() == 0 && ballCaught==0));
        System.out.println("Program ended");
    }

    private void depositballs() {
        client.sendMessage("4");
        waitForRobot();
    }

    private Point closetsGoal() {

        double returnSmall = calcDistance(((ComputerVision) cv).getRobotLocation(), new Point(540, 240));
        double returnBig = calcDistance(((ComputerVision) cv).getRobotLocation(), new Point(100, 240));
        if (returnBig > returnSmall) {
            return smallGoalDot;
        }
        return bigGoalDot;
    }

    private void waitForRobot() {
        while (true) {
            String message = client.readMessage();
            if (message != null) {
                break;
            }
        }
    }

    private void moveDistance(double distance, double noseOffset, double offset) {
        System.out.println("****************");
        System.out.println("Driving Length: " + distance);
        String command;
        command = "1-" + (int) ((distance / 3.844) - noseOffset - offset);
        client.sendMessage(command);
        waitForRobot();
    }

    private void turnDegrees(double[] degrees) {
        System.out.println("****************");
        System.out.println("Turning degrees: " + degrees[0]);
        String command;
        if (degrees[1] == 1) {
            //turn left
            command = "3-" + (int) degrees[0];
        } else {
            command = "2-" + (int) degrees[0];
        }
        client.sendMessage(command);
        waitForRobot();
    }

    //returns a double array which contains the degrees of turn to match the vectors and whether it's positive or negative
    private double[] calcAngle(ArrayList<Point> robotPoint, Point goal) {
        double[] directions = new double[2];
        if (goal.x > 10 && goal.y > 10) {
            Point robotVector = new Point(robotPoint.get(1).x - robotPoint.get(0).x, robotPoint.get(1).y - robotPoint.get(0).y);
            Point bigGoalVector = new Point(goal.x - robotPoint.get(0).x, goal.y - robotPoint.get(0).y);

            double dotProduct = (robotVector.x * bigGoalVector.x) + (robotVector.y * bigGoalVector.y);
            double magnitudeOfA = Math.sqrt(Math.pow(robotVector.x, 2) + Math.pow(robotVector.y, 2));
            double magnitudeOfB = Math.sqrt(Math.pow(bigGoalVector.x, 2) + Math.pow(bigGoalVector.y, 2));
            double goalAngle = Math.toDegrees(Math.acos(dotProduct / (magnitudeOfA * magnitudeOfB)));
            directions[0] = goalAngle;

            //if result > 0 robot turns left, else it turns right
            double result = ((goal.x - robotPoint.get(0).x) * (robotPoint.get(1).y - robotPoint.get(0).y)) - ((goal.y - robotPoint.get(0).y) * (robotPoint.get(1).x - robotPoint.get(0).x));
            if (result > 0) {
                directions[1] = 1;
            } else {
                directions[1] = 0;
            }
        }
        return directions;
    }

    private int numberOfBalls() {
        return ((ComputerVision) cv).getBallsLocation().size();
    }

    private Point getClosestBall() {
        //it is assumes that the back of the robot is the center.
        ArrayList<Point> robotPoint = ((ComputerVision) cv).getRobotLocation();
        Point returnPoint = new Point(340, 240);
        double minDist = 1000000;
        for (Point ball : ((ComputerVision) cv).getBallsLocation()) {
            double distance = Math.sqrt(Math.pow(ball.x - robotPoint.get(0).x, 2) + Math.pow(ball.y - robotPoint.get(0).y, 2));
            if (distance < minDist) {
                minDist = distance;
                returnPoint = ball;
            }
        }
        Point edgeBall = ((ComputerVision) cv).ballsCloseToEdge(returnPoint);
        if (edgeBall != null && goToEdgeBall) {
            goToEdgeBall = false;
            return edgeBall;
        } else {
            goToEdgeBall = true;
            return returnPoint;
        }

    }

    private double calcDistance(ArrayList<Point> robotPoint, Point goal) {
        return Math.sqrt(Math.pow(goal.x - robotPoint.get(0).x, 2) + Math.pow(goal.y - robotPoint.get(0).y, 2));
    }

    public double distanceToGoal(Point goal) {
        return calcDistance(cv.getRobotLocation(), goal);
    }

    public double[] angleToGoal(Point goal) {
        return calcAngle(cv.getRobotLocation(), goal);
    }

    public double[] turnToFaceGoal(Point goal) {
        if(goal.equals(this.smallGoalDot)){
            return calcAngle(cv.getRobotLocation(), this.smallGoal);
        }
        return calcAngle(cv.getRobotLocation(), this.bigGoal);
    }

}