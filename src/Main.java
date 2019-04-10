import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class Main
{
    public static void main(String[] args)
    {
        Robot robot = new Robot();
        robot.MoveForwardAB(900);
        robot.MoveBackwardD(900);
        robot.TurnClockwiseAB(90);
        Button.waitForAnyPress();
    }
}