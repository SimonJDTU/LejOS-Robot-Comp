import lejos.hardware.Button;
import lejos.utility.Delay;

public class Main
{
    public static void main(String[] args)
    {
        //Test to make the robot move forward, backward, turn 90 degrees Clockwise and 90 degrees Counterclockwise
/*

        Robot Robot = new Robot();
        Robot.MoveForwardAB(200);
        Delay.msDelay(1000);
        Robot.MoveBackwardAB(200);
        Delay.msDelay(1000);
        Robot.TurnClockwiseAB(90);
        Delay.msDelay(1000);
        Robot.TurnCounterclockwiseAB(1000, 200);
*/




/*

        //Test the communication with the robot and the pc.
        //Remember to make sure the Robot is connected to the PC and set the proper IP.
        //The default port we use for the Server object is 5000.
        //When you want to end the connection you should send the string "Over".
        Server Server = new Server(5000);
*/


/*Robot robot = new Robot();
robot.CaptureBallsD();
Delay.msDelay(5000);
robot.ReleaseBallsD();
Delay.msDelay(5000);
robot.StopReleaseBalls();*/

        Robot robot = new Robot();
        robot.CaptureBallsD();
        robot.MoveDistanceForwardAB(50);
        robot.TurnCounterclockwiseAB(90);
        robot.MoveDistanceForwardAB(50);
        robot.TurnCounterclockwiseAB(90);
        robot.MoveDistanceForwardAB(50);
        robot.TurnCounterclockwiseAB(90);
        robot.MoveDistanceForwardAB(50);
        robot.TurnCounterclockwiseAB(90);

/*        Robot robot = new Robot();
        robot.MoveDistanceForwardAB(200);*/






    }

}