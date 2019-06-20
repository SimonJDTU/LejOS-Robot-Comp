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
     * Moves the robot a given distance forward.
     * The method is blocking and will return once the robot have moved the given distance.
     * @param distance The amount of cm the robot should move.
     */
    public void MoveDistanceForwardAB(int distance)
    {
        Motor.A.setSpeed(600);
        Motor.B.setSpeed(600);
        int rotation = (int) (distance * 35.34);
        System.out.println("Moved " + distance + " forward");
        Motor.A.rotate(rotation, true);
        Motor.B.rotate(rotation, false);
    }

    /**
     * Moves the robot a given distance forward with a given motor speed
     * The method is blocking and will return once the robot have moved the given distance.
     * @param distance The amount of cm the robot should move.
     * @param speed The speed at which the motors should run at.
     */
    public void MoveDistanceForwardAB(int distance, int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);
        int rotation = (int) (distance * 35.34);
        Motor.A.rotate(rotation, true);
        Motor.B.rotate(rotation, false);
    }

    /**
     * Moves the robot a given distance backwards.
     * The method is blocking and will return once the robot have moved the given distance.
     * @param distance The amount of cm the robot should move.
     */
    public void MoveDistanceBackwardsAB(int distance)
    {
        Motor.A.setSpeed(600);
        Motor.B.setSpeed(600);
        int rotation = (int) (distance * 35.34);
        Motor.A.rotate(-rotation, true);
        Motor.B.rotate(-rotation, false);
    }

    /**
     * Turns the robot a given number of degrees clockwise.
     * @param degrees The amount of degrees the robot should turn
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
     * Turns the robot a given number of degrees clockwise.
     * @param degrees The amount of degrees the robot turn
     * @param speed The speed the motors turn at
     */
    public void TurnClockwiseAB(int degrees, int speed)
    {

        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);
        int  angle = (int) (degrees * 5.05);
        Motor.A.rotate(-angle, true);
        Motor.B.rotate(angle, false);
    }

    /**
     * Turns the robot a given number of degrees counterclockwise.
     * @param degrees The amount of degrees the robot should turn
     * @param speed The speed the motors turn at
     */
    public void TurnCounterclockwiseAB(int degrees, int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);
        System.out.println("Turned " + degrees + " degrees");
        int  angle = (int) (degrees * 5.077);
        Motor.B.rotate(-angle, true);
        Motor.A.rotate(angle, false);
    }

    /**
     * Turns the robot a given number of degrees counterclockwise.
     * @param degrees The amount of degrees the robot should turn
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

    /**
     * Starts the motor D at max speed and turns them to run backwards to capture balls.
     */
    public void CaptureBallsD()
    {
        Motor.D.setSpeed(Motor.D.getMaxSpeed());
        Motor.D.backward();
    }

    /**
     * Starts the motor C at max speed moving the plate from the front of the robot to the back
     * making space for balls.
     * @param amount The amount of balls you make space for. Should always be 3.
     */
    public void StoreBallsC(int amount)
    {
        Motor.C.setSpeed(Motor.C.getMaxSpeed());
        Motor.C.rotate(-amount*4300,false);
    }

    /**
     * Starts the motor C at max speed moving the plate from the back of the robot to the front
     * shooting out balls out of the robot.
     * @param amount The amount of balls you shoot out. Should always be 3.
     */
    public void ThrowBallsC(int amount)
    {
        Motor.C.setSpeed(Motor.C.getMaxSpeed());
        Motor.C.rotate(amount*4300, true);
    }

    /**
     * Starts the motor D at max speed and turns it forward to release balls.
     */
    public void ReleaseBallsD()
    {
        Motor.D.setSpeed(Motor.D.getMaxSpeed());
        Motor.D.forward();
    }

    /**
     * Stops the motor D which Stops the front wheels from moving
     */
    public void StopRobotD()
    {
        Motor.D.stop(true);
    }

}