import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class Main
{
    public static void main(String[] args)
    {
        Robot robot = new Robot();
        robot.TurnCounterclockwiseAB(90);
        //Delay.msDelay(1000);
        //robot.TurnCounterclockwiseAB(1920);
        Button.waitForAnyPress();
    }
}