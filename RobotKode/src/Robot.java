import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

/*
Motor A er robotten venstre larvefod.
Motor B er robotten højre larvefod.
Motor C er robottens interne lager af bolde
Motor D er robotten to hjul til at indsamle og udskyde bolde.
 */
public class Robot
{
    /**
     * Makes the robot move forward using the motors at input A and B
     * @param speed The speed at which the two motors move
     */
    public void MoveForwardAB(int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);

        Motor.A.forward();
        Motor.B.forward();
    }

    /**
     * Moves the robot backwards using the motors at input A and B
     * @param speed The speed at which the two motors move
     */
    public void MoveBackwardAB(int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);

        Motor.A.backward();
        Motor.B.backward();
    }

    public void MoveDistanceForwardAB(int distance)
    {
        Motor.A.setSpeed(600);
        Motor.B.setSpeed(600);
        int rotation = (int) (distance * 35.34);
        System.out.println("Moved " + distance + " forward");
        Motor.A.rotate(rotation, true);
        Motor.B.rotate(rotation, false);
    }

    public void MoveDistanceForwardAB(int distance, int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);
        int rotation = (int) (distance * 35.34);
        Motor.A.rotate(rotation, true);
        Motor.B.rotate(rotation, true);
    }

    public void MoveDistanceBackwardsAB(int distance)
    {
        Motor.A.setSpeed(600);
        Motor.B.setSpeed(600);
        int rotation = (int) (distance * 35.34);
        Motor.A.rotate(-rotation, true);
        Motor.B.rotate(-rotation, false);
    }

    /**
     * Turns the robot clockwise using the motors at input A as the left motor and and B as the right motor.
     * The robot turns for a given duration in ms.
     * @param degrees The amount of degrees the robot should turn clockwise.
     */
    public void TurnClockwiseAB(int degrees)
    {

        Motor.A.setSpeed(200);
        Motor.B.setSpeed(200);
        int  angle = (int) (degrees * 5.05);
        //System.out.println("Angle is " + angle);
        Motor.A.rotate(-angle, true);
        Motor.B.rotate(angle, false);
    }

    /**
     * Turns the robot clockwise using the motors at input A as the left motor and and B as the right motor.
     * The robot turns for a given duration in ms.
     * @param degrees The amount of degrees the robot should turn counterclockwise
     */
    public void TurnCounterclockwiseAB(int degrees)
    {
        Motor.A.setSpeed(200);
        Motor.B.setSpeed(200);
        System.out.println("Turned " + degrees + " degrees");
        int  angle = (int) (degrees * 5.077);
        //System.out.println("Angle is " + angle);
        Motor.B.rotate(-angle, true);
        Motor.A.rotate(angle, false);
    }

    public void TurnClockwiseAB()
    {
        Motor.A.setSpeed(400);
        Motor.B.setSpeed(400);
        Motor.A.forward();
        Motor.B.backward();
    }

    public void TurnCounterclockwiseAB()
    {
        Motor.A.setSpeed(400);
        Motor.B.setSpeed(400);
        Motor.A.backward();
        Motor.B.forward();
    }

    public void StopTurningAB()
    {
        Motor.A.stop(true);
        Motor.B.stop(true);
    }


    /**
     *
     */
    public void CaptureBallsD()
    {
        Motor.D.setSpeed(Motor.D.getMaxSpeed());
        Motor.D.backward();
    }

    public void StoreBallsC(int amount)
    {
        Motor.C.setSpeed(Motor.C.getMaxSpeed());
        Motor.C.rotate(-amount*4300,false);
    }
    public void ThrowBallsC(int amount)
    {
        Motor.C.setSpeed(Motor.C.getMaxSpeed());
        Motor.C.rotate(amount*4300, true);
    }

    public void ReleaseBallsD()
    {
        Motor.D.setSpeed(Motor.D.getMaxSpeed());
        Motor.D.forward();
    }

    public void StopReleaseBalls()
    {
        Motor.D.stop();
    }

    public void StopRobotAB()
    {
        Motor.A.stop(true);
        Motor.B.stop(true);
    }
    public void StopRobotD()
    {
        Motor.D.stop(true);
    }

    public void playSound()
    {
        Sound.setVolume(100);
        Sound.beepSequenceUp();
    }
}