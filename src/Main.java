import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;
import lejos.utility.Timer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

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

       // Server server = new Server(5000);
        Robot robot = new Robot();
        //robot.ReleaseBallsD();
        robot.ThrowBallsC(3);
        //robot.StoreBallsC(3);
        Button.waitForAnyPress();




/*        Robot robot = new Robot();
        robot.MoveDistanceForwardAB(200);*/






    }

}