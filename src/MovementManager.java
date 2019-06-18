import org.opencv.core.Point;

import java.util.ArrayList;

/*MovementMananger crates instances of ComputerVision and Robot. The class uses points given by ComputerVision
to calculate distances and angles to turn, where it afterwards sends the commands to the robot. */
public class MovementManager {

    private Client client;
    private int PORT_CONNECTION=5000;
    private String IP_C0NNECTION="192.168.137.208";
    private Runnable cv = new ComputerVision();
    private Thread thread = new Thread(cv);

    MovementManager() {
        this.client = new Client(IP_C0NNECTION, PORT_CONNECTION);
    }

    //TODO: set up waiting system, instead of taking images.
    void run() {
        int ballCaught = 0;
        Point closetsBall;
        ArrayList<Point>  robotLocation;

        thread.start();

        try {
            Thread.sleep(2500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        do{
            if (ballCaught>=3) {
                System.out.println("Goal delivery hit");
                turnDegrees(angleToGoal());
                moveDistance(distanceToGoal());
                try {
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                turnDegrees(turnToFaceGoal());
                client.sendMessage("4");
                try {
                    Thread.sleep(15000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                ballCaught=0;
            } else {
                //TODO: Make it so it recalculates the angle and distance close to the ball
                closetsBall = getClosestBall();
                robotLocation = ((ComputerVision)cv).getRobotLocation();
                turnDegrees(calcAngle(robotLocation,closetsBall));
                moveDistance(calcDistance(robotLocation,closetsBall));
                try {
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                ballCaught++;
            }
        }while (!(numberOfBalls()==0));
        ((ComputerVision)cv).setProgramRunning(false);
        System.out.println("Program ended");
    }

    private void moveDistance(double distance) {
        System.out.println("****************");
        System.out.println("Driving Length: "+distance);
        System.out.println("****************");
        String command;
        command = "1-" + (int) ((distance - 30) / 3.844);
        client.sendMessage(command);
    }

    private void turnDegrees(double[] degrees) {
        System.out.println("****************");
        System.out.println("Turning degrees: "+degrees[0]);
        System.out.println("****************");
        String command;
        if (degrees[1] == 1) {
            //turn left
            command = "3-" + (int) degrees[0];
        } else {
            command = "2-" + (int) degrees[0];
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
        return ((ComputerVision)cv).getBallsLocation().size();
    }

    private Point getClosestBall(){
        //it is assumes that the back of the robot is the center.
        ArrayList<Point> robotPoint=((ComputerVision)cv).getRobotLocation();
        Point returnPoint = new Point(340,240);
        double minDist = 1000000;
        for(Point ball : ((ComputerVision)cv).getBallsLocation()){
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

    public double distanceToGoal(){
        Point goalPoint = new Point();//((ComputerVision)cv).getGoalsLocation();
        goalPoint= new Point(640, 240);
        return calcDistance(((ComputerVision)cv).getRobotLocation(),goalPoint);
    }

    public double[] angleToGoal(){
        Point goalLocation = new Point(); //(ComputerVision)cv).getGoalsLocation();
        goalLocation= new Point(600, 240);
        return calcAngle(((ComputerVision)cv).getRobotLocation(),goalLocation);
    }

    public double[] turnToFaceGoal(){
        return calcAngle(((ComputerVision)cv).getRobotLocation(),((ComputerVision)cv).getGoalsLocation());
    }

}
