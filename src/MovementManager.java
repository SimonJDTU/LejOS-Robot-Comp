import org.opencv.core.Point;

import java.util.ArrayList;

/*MovementMananger crates instances of ComputerVision and Robot. The class uses points given by ComputerVision
to calculate distances and angles to turn, where it afterwards sends the commands to the robot. */
class MovementManager {

    private final Point smallGoalDot = new Point(560, 240);
    private final Point smallGoal = new Point(639, 240);
    private Client client;
    private IComputerVision cv = new ComputerVision();
    private int ballCaught = 0;
    private ArrayList<SafetyCorner> securePoints = new ArrayList<>();

    private int PORT_CONNECTION = 5000;
    private String IP_C0NNECTION = "192.168.137.239";

    MovementManager() {
        this.client = new Client();
        this.client.startConnection(IP_C0NNECTION, PORT_CONNECTION);
        SafetyCorner upperLeftSafetyCorner = new SafetyCorner(new Point(95, 95));
        SafetyCorner topMidSafetyCorner = new SafetyCorner(new Point(320, 95));
        SafetyCorner upperRightSafetyCorner = new SafetyCorner(new Point(545, 95));
        SafetyCorner rightMidSafetyCorner = new SafetyCorner(new Point(545, 240));
        SafetyCorner lowerRightSafetyCorner = new SafetyCorner(new Point(545, 385));
        SafetyCorner botMidSafetyCorner = new SafetyCorner(new Point(320, 385));
        SafetyCorner lowerLeftSafetyCorner = new SafetyCorner(new Point(95, 385));
        SafetyCorner leftMidSafetyCorner = new SafetyCorner(new Point(95, 240));

        upperLeftSafetyCorner.setLeft(topMidSafetyCorner);
        upperLeftSafetyCorner.setRight(leftMidSafetyCorner);

        topMidSafetyCorner.setLeft(upperRightSafetyCorner);
        topMidSafetyCorner.setRight(upperLeftSafetyCorner);

        upperRightSafetyCorner.setLeft(rightMidSafetyCorner);
        upperRightSafetyCorner.setRight(topMidSafetyCorner);

        rightMidSafetyCorner.setLeft(lowerRightSafetyCorner);
        rightMidSafetyCorner.setRight(upperRightSafetyCorner);

        lowerRightSafetyCorner.setLeft(botMidSafetyCorner);
        lowerRightSafetyCorner.setRight(rightMidSafetyCorner);

        botMidSafetyCorner.setLeft(lowerLeftSafetyCorner);
        botMidSafetyCorner.setRight(lowerRightSafetyCorner);

        lowerLeftSafetyCorner.setLeft(leftMidSafetyCorner);
        lowerLeftSafetyCorner.setRight(botMidSafetyCorner);

        leftMidSafetyCorner.setLeft(upperLeftSafetyCorner);
        leftMidSafetyCorner.setRight(lowerLeftSafetyCorner);

        securePoints.add(upperLeftSafetyCorner);
        securePoints.add(topMidSafetyCorner);
        securePoints.add(upperRightSafetyCorner);
        securePoints.add(rightMidSafetyCorner);
        securePoints.add(lowerRightSafetyCorner);
        securePoints.add(botMidSafetyCorner);
        securePoints.add(lowerLeftSafetyCorner);
        securePoints.add(leftMidSafetyCorner);

    }

    void run() {
        Point closestBall, goalPoint;
        processImages();
        int pointOffset, noseOffset;
        boolean freeball;

        do {
            if (ballCaught >= 4 || (numberOfBalls() == 0 && ballCaught > 0)) {
                System.out.println("Goal delivery hit");

                //while path is not clean
                if (!cv.cleanPath(cv.getRobotLocation().get(0), smallGoalDot)) {
                    SafetyCorner securePoint = closestGoodPoint();
                    turnDegrees(calcAngle(cv.getRobotLocation(), securePoint.location),true);
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation().get(0), securePoint.location), 0, 0,true);
                    processImages();
                    driveRoute(securePoint, smallGoalDot);
                }

                processImages();
                turnDegrees(angleToSmallGoal(smallGoalDot),true);

                processImages();
                moveDistance(distanceToSmallGoal(smallGoalDot), 0, 15,true);

                processImages();
                turnDegrees(angleToSmallGoal(smallGoalDot),false);

                processImages();
                moveDistance(distanceToSmallGoal(smallGoalDot), 0, 0,false);

                processImages();
                turnDegrees(turnToFaceSmallGoal(),false);

                processImages();
                depositBalls();

                processImages();
                ballCaught = 0;

                processImages();

            } else {
                processImages();
                closestBall = getClosestBall();
                if (cv.insideCircle(closestBall)) {
                    goalPoint = cv.circleRotation(closestBall);
                    pointOffset = 0;
                    noseOffset = 17;
                    freeball = false;
                } else if (isCornerBall(closestBall)) {
                    goalPoint = cv.ballsCloseToEdge(closestBall);
                    pointOffset = 0;
                    noseOffset = 13;
                    freeball = false;
                } else {
                    goalPoint = closestBall;
                    pointOffset = 10;
                    noseOffset = 0;
                    freeball = true;
                }

                //while path is not clean
                if (!cv.cleanPath(cv.getRobotLocation().get(0), goalPoint)) {
                    SafetyCorner securePoint = closestGoodPoint();
                    turnDegrees(calcAngle(cv.getRobotLocation(), securePoint.location),true);
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation().get(0), securePoint.location), 0, 0, true);
                    processImages();
                    driveRoute(securePoint, goalPoint);
                }

                processImages();
                turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);

                processImages();
                if(freeball == false){
                    if (calcDistance(cv.getRobotLocation().get(0), goalPoint) >= 20) {
                        turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);
                        processImages();
                        moveDistance(calcDistance(cv.getRobotLocation().get(0), goalPoint), 0, pointOffset, true);
                    }
                }else{
                    if(calcDistance(cv.getRobotLocation().get(0), goalPoint) >= 20) {
                        turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);
                        processImages();
                        moveDistance(calcDistance(cv.getRobotLocation().get(0), goalPoint), 0, 25,true);
                    }else{
                        turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);
                    }
                }

                processImages();
                if(freeball == false){
                if (calcDistance(cv.getRobotLocation().get(0), goalPoint) >= 20) {
                    turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation().get(0), goalPoint), 0, pointOffset,true);
                }
                }else{
                    if(calcDistance(cv.getRobotLocation().get(0), goalPoint) >= 40) {
                        turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);
                        processImages();
                        moveDistance(calcDistance(cv.getRobotLocation().get(0), goalPoint), 0, 25,true);
                    }else{
                        turnDegrees(calcAngle(cv.getRobotLocation(), goalPoint),true);
                    }
                }
                processImages();
                turnDegrees(calcAngle(cv.getRobotLocation(), closestBall),false);

                //last nut
                processImages();
                moveDistance(calcDistance(cv.getRobotLocation().get(0), closestBall), noseOffset, 0,false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moveBackwards();
                ballCaught++;
                processImages();
            }
        } while (!(numberOfBalls() == 0 && ballCaught == 0));
        client.sendMessage(".");
        System.out.println("Program ended");
    }

    private void driveRoute(SafetyCorner closestGoodPoint, Point goal) {

        SafetyCorner rightPointResult = closestGoodPoint.right, leftPointResult = closestGoodPoint.left;
        boolean rightValid = true, leftValid = true;

        if (!cv.cleanPath(closestGoodPoint.location, leftPointResult.location) && !cv.cleanPath(closestGoodPoint.location, rightPointResult.location)) {
            double rightDistance, leftDistance;
            rightDistance = calcDistance(closestGoodPoint.location, rightPointResult.location);
            leftDistance = calcDistance(closestGoodPoint.location, leftPointResult.location);

            if (rightDistance > leftDistance) {
                processImages();
                turnDegrees(calcAngle(cv.getRobotLocation(), rightPointResult.location),true);
                processImages();
                moveDistance(calcDistance(cv.getRobotLocation().get(0), rightPointResult.location), 0, 0,true);
                closestGoodPoint = rightPointResult;
                rightPointResult = closestGoodPoint.right;
                leftPointResult = closestGoodPoint.left;
            } else {
                processImages();
                turnDegrees(calcAngle(cv.getRobotLocation(), leftPointResult.location),true);
                processImages();
                moveDistance(calcDistance(cv.getRobotLocation().get(0), leftPointResult.location), 0, 0,true);
                closestGoodPoint = leftPointResult;
                rightPointResult = closestGoodPoint.right;
                leftPointResult = closestGoodPoint.left;
            }

        } else if (cv.insideCircle(rightPointResult.location)) {
            processImages();
            turnDegrees(calcAngle(cv.getRobotLocation(), leftPointResult.location),true);
            processImages();
            moveDistance(calcDistance(cv.getRobotLocation().get(0), leftPointResult.location), 0, 0,true);
            closestGoodPoint = leftPointResult;
            rightPointResult = closestGoodPoint.right;
            leftPointResult = closestGoodPoint.left;

        } else if (cv.insideCircle(leftPointResult.location)) {
            processImages();
            turnDegrees(calcAngle(cv.getRobotLocation(), rightPointResult.location),true);
            processImages();
            moveDistance(calcDistance(cv.getRobotLocation().get(0), rightPointResult.location), 0, 0,true);
            closestGoodPoint = rightPointResult;
            rightPointResult = closestGoodPoint.right;
            leftPointResult = closestGoodPoint.left;
        }

        if (!(cv.cleanPath(closestGoodPoint.location, rightPointResult.location))) {
            rightValid = false;
        }
        if (!(cv.cleanPath(closestGoodPoint.location, leftPointResult.location))) {
            leftValid = false;
        }

        int i = 0, j = 0;
        if(rightValid) {
            System.out.println("Checking right path");
            while (i++ < 7) {
                if (cv.cleanPath(rightPointResult.location, goal)) {
                    break;
                }
                if (!(cv.cleanPath(rightPointResult.location, rightPointResult.right.location))) {
                    rightValid = false;
                }
                rightPointResult = rightPointResult.right;
            }
        }
        if(leftValid) {
            System.out.println("Checking left path");
            while (j++ < 7) {
                if (cv.cleanPath(leftPointResult.location, goal)) {
                    break;
                }
                if (!(cv.cleanPath(leftPointResult.location, leftPointResult.left.location))) {
                    leftValid = false;
                }
                leftPointResult = leftPointResult.left;
            }
        }


        if (!(cv.cleanPath(closestGoodPoint.location, goal))) {
            SafetyCorner nextPoint = closestGoodPoint;
            if (i <= j && rightValid||rightValid && !leftValid) {
                for (int k = 0; k < i; k++) {
                    System.out.println("Driving right: " + i);
                    nextPoint = nextPoint.right;
                    processImages();
                    turnDegrees(calcAngle(cv.getRobotLocation(), nextPoint.location),true);
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation().get(0), nextPoint.location), 0, 0,true);
                }
            } else if (j <= i && leftValid||leftValid && !rightValid ) {
                for (int k = 0; k < j; k++) {
                    System.out.println("Driving left: " + j);
                    nextPoint = nextPoint.left;
                    processImages();
                    turnDegrees(calcAngle(cv.getRobotLocation(), nextPoint.location),true);
                    processImages();
                    moveDistance(calcDistance(cv.getRobotLocation().get(0), nextPoint.location), 0, 0,true);
                }
            } else {
                System.out.println("Couldn't find path in driveRoute");
            }
        }
    }

    private SafetyCorner closestGoodPoint() {
        SafetyCorner returnPoint = null;
        ArrayList<Point> robot = cv.getRobotLocation();
        double dist = 100000;

        for (SafetyCorner point : securePoints) {
            if (cv.cleanPath(robot.get(0), point.location)) {
                if (calcDistance(robot.get(0), point.location) < dist) {
                    returnPoint = point;
                    dist = calcDistance(robot.get(0), point.location);
                }
            }
        }
        if (!(returnPoint == null)) {
            return returnPoint;
        }
        dist = 100000;
        for (SafetyCorner point : securePoints) {
            if (calcDistance(robot.get(0), point.location) < dist) {
                returnPoint = point;
                dist = calcDistance(robot.get(0), point.location);
            }
        }
        return returnPoint;
    }

    private void moveBackwards() {
        client.sendMessage("6-16");
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
        for (int i = 0; i < 5; i++) {
            cv.run();
        }
    }

    private void moveDistance(double distance, double noseOffset, double offset, boolean fast) {
        System.out.println("****************");
        System.out.println("Driving Length: " + distance);
        String command;

        if(fast){
            command = "1-" + (int) ((distance / 3.844) - noseOffset - offset) +"-800";
            client.sendMessage(command);
            waitForRobot();
        }else{
            command = "1-" + (int) ((distance / 3.844) - noseOffset - offset) +"-500";
            client.sendMessage(command);
            waitForRobot();
        }
    }

    private void turnDegrees(double[] degrees, boolean fast) {
        System.out.println("****************");
        System.out.println("Turning degrees: " + degrees[0]);
        String command;

        if(fast){
            if (degrees[1] == 1) {
                //turn left
                command = "3-" + (int) degrees[0]+ "-500";
            } else {
                command = "2-" + (int) degrees[0]+ "-500";
            }
            client.sendMessage(command);
            waitForRobot();
        }else{
            if (degrees[1] == 1) {
                //turn left
                command = "3-" + (int) degrees[0]+ "-200";
            } else {
                command = "2-" + (int) degrees[0]+ "-200";
            }
            client.sendMessage(command);
            waitForRobot();
        }

    }

    //returns a double array which contains the degrees of turn to match the vectors and whether it's positive or negative
    private double[] calcAngle(ArrayList<Point> robotPoint, Point goal) {
        double[] directions = new double[2];
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
        return directions;
    }

    private int numberOfBalls() {
        return cv.getBallsLocation().size();
    }

    private Point getClosestBall() {

        //it is assumes that the back of the robot is the center.
        ArrayList<Point> robotPoint = cv.getRobotLocation();
        Point returnPoint = null;
        while (returnPoint == null) {
            processImages();
            double minDist = 1000000;
            for (Point ball : cv.getBallsLocation()) {
                double distance = Math.sqrt(Math.pow(ball.x - robotPoint.get(0).x, 2) + Math.pow(ball.y - robotPoint.get(0).y, 2));
                if (distance < minDist) {
                    minDist = distance;
                    returnPoint = ball;
                }
            }
            System.out.println("No balls found in getClosetsBall");
        }
        return returnPoint;
    }

    private boolean isCornerBall(Point goal) {
        return cv.ballsCloseToEdge(goal) != null;
    }

    private double calcDistance(Point robotPoint, Point goal) {
        return Math.sqrt(Math.pow(goal.x - robotPoint.x, 2) + Math.pow(goal.y - robotPoint.y, 2));
    }

    private double distanceToSmallGoal(Point goal) {
        return calcDistance(cv.getRobotLocation().get(0), goal);
    }

    private double[] angleToSmallGoal(Point goal) {
        return calcAngle(cv.getRobotLocation(), goal);
    }

    private double[] turnToFaceSmallGoal() {
        return calcAngle(cv.getRobotLocation(), this.smallGoal);

    }

    private class SafetyCorner {
        Point location;
        SafetyCorner left = null;
        SafetyCorner right = null;

        private SafetyCorner(Point location) {
            this.location = location;
        }

        private void setLeft(SafetyCorner left) {
            this.left = left;
        }

        private void setRight(SafetyCorner right) {
            this.right = right;
        }

    }
}