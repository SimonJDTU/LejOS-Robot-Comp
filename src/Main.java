import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class Main
{
    public static void main(String[] args)
    {

        Robot robot = new Robot();


        // Catching Balls.
        //robot.CatchBalls(1);
        int count = 0;
        while(count < 4)
        {
            if (count % 2 == 0)
            {
                robot.CatchBalls(3);
            }
            else
            {
                robot.ReleaseBalls(3);
            }

            Delay.msDelay(5000);
            count++;
        }
        System.out.println("Færdig med programmet, tryk på en knap for at afslutte");
        Button.waitForAnyPress();


    }
}