import org.opencv.core.Point;

import java.util.ArrayList;

/*MovementMananger crates instances of ComputerVision and Robot. The class uses points given by ComputerVision
to calculate distances and angles to turn, where it afterwards sends the commands to the robot. */
class MovementManager {

    private Client client;
    private ComputerVision cv = new ComputerVision();
    private int ballCaught = 0;
    private final Point smallGoalDot = new Point(560, 240);
    private final Point smallGoal = new Point(639, 240);

    private int PORT_CONNECTION = 5000;
    private String IP_C0NNECTION = "192.168.137.239";

    MovementManager() {
        this.client = new Client();
        this.client.startConnection(IP_C0NNECTION, PORT_CONNECTION);
    }

    void run() {
        Point closestBall, goalPoint;
        processImages();
        int pointOffset, noseOffset;

        do {
            if (ballCaught >= 3 || (numberOfBalls() == 0 && ballCaught > 0)) {
                System.out.println("Goal delivery hit");

                processImages();
                turnDegrees(angleToSmallGoal(smallGoalDot));

                processImages();
                moveDistance(distanceToSmallGoal(smallGoalDot), 0, 15);

                processImages();
                turnDegrees(angleToSmallGoal(smallGoalDot));

                processImages();
                moveDistance(distanceToSmallGoal(smallGoalDot), 0, 0);

                processImages();
                turnDegrees(turnToFaceSmallGoal());

                processImages();
                depositBalls();

                processImages();
                ballCaught = 0;

                processImages();

            } else {
                //TODO: Make it so it recalculates the angle and distance close to the ball
                processImages();
                closestBall = getClosestBall();
                if (cv.insideCircle(closestBall)) {
                    goalPoint = cv.circleRotation(closestBall);
                    pointOffset=0;
                    noseOffset=20;
                } else if (isCornerBall(closestBall)) {
                    goalPoint = cv.ballsCloseToEdge(closestBall);
                    pointOffset=0;
                    noseOffset=9;
                } else {
                    goalPoint = closestBall;
                    pointOffset=15;
                    noseOffset=0;
                }

                //while path is not clean
                while (!cv.cleanPath(closestBall)) {
                    //move to hardcoded safe spots
                    break;
                }
                processImages();
                turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint));

                if (calcDistance(cv.getRobotLocation(), goalPoint) >= 10) {
                    processImages();
                    turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint));
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation(), goalPoint), 0, pointOffset);
                }

                if (calcDistance(cv.getRobotLocation(), goalPoint) >= 10) {
                    processImages();
                    turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint));
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation(), goalPoint), 0, pointOffset);
                }

                processImages();
                turnDegrees(calcAngle(cv.getRobotLocation(), closestBall));

                processImages();
                moveDistance(calcDistance(cv.getRobotLocation(), closestBall), noseOffset, 0);
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                moveBackwards();
                ballCaught++;
                processImages();
            }
    } while(!(numberOfBalls() ==0&&ballCaught==0));
        client.sendMessage(".");
        System.out.println("Program ended");
}

    private void moveBackwards() {
        client.sendMessage("6-20");
        waitForRobot();
    }

    private void depositBalls() {
        client.sendMessage("4");
        waitForRobot();
    }

    private void waitForRobot() {
        while (true) {
            String message = client.readMessage();
            if (message != null) {
                break;
            }
        }
    }

    private void processImages() {
        for (int i = 0; i < 10; i++) {
            cv.run();
        }
    }

    private void moveDistance(double distance, double noseOffset, double offset) {
        System.out.println("****************");
        //System.out.println("Driving Length: " + distance);
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
        return cv.getBallsLocation().size();
    }

    private Point getClosestBall() {
        //it is assumes that the back of the robot is the center.
        ArrayList<Point> robotPoint = cv.getRobotLocation();
        Point returnPoint = new Point(340, 240);
        double minDist = 1000000;
        for (Point ball : cv.getBallsLocation()) {
            double distance = Math.sqrt(Math.pow(ball.x - robotPoint.get(0).x, 2) + Math.pow(ball.y - robotPoint.get(0).y, 2));
            if (distance < minDist) {
                minDist = distance;
                returnPoint = ball;
            }
        }
        return returnPoint;
    }

    private boolean isCornerBall(Point goal) {
        Point tempPoint = cv.ballsCloseToEdge(goal);
        System.out.println("temppoit nfor ball :" + tempPoint);
        return tempPoint != null;
    }

    private double calcDistance(ArrayList<Point> robotPoint, Point goal) {
        return Math.sqrt(Math.pow(goal.x - robotPoint.get(0).x, 2) + Math.pow(goal.y - robotPoint.get(0).y, 2));
    }

    private double distanceToSmallGoal(Point goal) {
        return calcDistance(cv.getRobotLocation(), goal);
    }

    private double[] angleToSmallGoal(Point goal) {
        return calcAngle(cv.getRobotLocation(), goal);
    }

    private double[] turnToFaceSmallGoal() {
        return calcAngle(cv.getRobotLocation(), this.smallGoal);

    }

}