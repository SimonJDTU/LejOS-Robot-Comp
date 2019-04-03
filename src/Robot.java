import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Robot
{
    private static EV3UltrasonicSensor UltrasonicSensor = new EV3UltrasonicSensor(SensorPort.S1);
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

    /**
     * Turns the robot clockwise using the motors at input A as the left motor and and B as the right motor.
     * The robot turns for a set number of degrees.
     * 90 degrees corresponds to an angle of 468,75
     * 180 degrees corresponds to an angle of 937,5
     * 270 degrees corresponds to an angle of 1406,25
     * 360 degrees corresponds to an angle of 1875
     * @param degrees The amount of degrees that the robot rotates
     */
    public void TurnClockwiseAB(int degrees)
    {

        int  angle = (int) (degrees * 5.2083333333333333333333333333333);
        System.out.println("Angle is " + angle);
        Motor.A.rotate(angle, true);
        Motor.B.rotate(-angle, false);
    }

    /**
     * Turns the robot counterclockwise using the motors at input A as the left motor and and B as the right motor.
     * The robot turns for a set number of degrees.
     * 90 degrees corresponds to an angle of 468,75
     * 180 degrees corresponds to an angle of 937,5
     * 270 degrees corresponds to an angle of 1406,25
     * 360 degrees corresponds to an angle of 1875
     * @param degrees The amount of degrees that the robot rotates
     */
    public void TurnCounterclockwiseAB(int degrees)
    {
        int  angle = (int) (degrees * 5.2083333333333333333333333333333);
        System.out.println("Angle is " + angle);
        Motor.A.rotate(-angle, true);
        Motor.B.rotate(angle, false);
    }

    /**
     * Uses the Ultrasonic Sensor to calculate the distance to the nearest object.
     * Returns infinite if it is either out of range or closer than 3cm otherwise returns in cm
     */
    public float DistanceToObstacleUS()
    {
        final SampleProvider SP = UltrasonicSensor.getDistanceMode();
        float Distance = 0;
        float[] Sample = new float[SP.sampleSize()];
        SP.fetchSample(Sample, 0);
        Distance = Sample[0] * 100;
        return Distance;
    }

    /**
     * Method to capture the balls with the Motor D
     * Makes the wheels spin inwards to capture the balls from the front of the robot
     * Starts the motor and makes it turn at a certain speed given with the speed parameter
     * @param speed The speed at which the motor D turns. Can be 0-900
     */
    public void CatchBalls(int speed)
    {
        Motor.C.setSpeed(speed);
        Motor.D.setSpeed(speed);
        Motor.C.backward();
        Motor.D.backward();
    }

    /**
     * Method to capture the balls with the Motor D
     * Makes the wheels spin outwards to release the balls from the front of the robot
     * Starts the motor and makes it turn at a certain speed given with the speed parameter
     * @param speed The speed at which the motor D turns. Can be 0-900
     */
    public void ReleaseBalls(int speed)
    {
        Motor.C.setSpeed(speed);
        Motor.D.setSpeed(speed);
        Motor.C.forward();
        Motor.D.forward();
    }

    /**
     * Stops the motor D and motor C which results in stopping the release of balls from the robot.
     * Motor D handles the wheels in front of the robot that can push and pull the balls.
     * Motor C handles the storage of the balls in the robot.
     */
    public void StopReleaseBalls()
    {
        Motor.D.stop();
        Motor.C.stop();
    }

    /**
     * Stops the robots motors A and B which is used to move it forward and backwards.
     * This stops the robot.
     */
    public void StopRobotAB()
    {
        Motor.A.stop();
        Motor.B.stop();
    }

    public void MoveForwardAvoidObstacles()
    {
        float DistanceToObstacle;
        for(int i = 0; i < 1000; i++)
        {
            MoveForwardAB(100);
            Delay.msDelay(50);
            DistanceToObstacle = DistanceToObstacleUS();
            if (DistanceToObstacle < 10)
            {
                StopRobotAB();
                TurnClockwiseAB(400);
            }
        }
    }

    /**
     * Used to debug the storage of balls mechanism
     * Moves the motor used in the ball storage mechanism forward which pushes out the balls
     * @param degrees Moves motor used in the ball storage mechanism a certain amount of degrees
     */
    public void MoveForwardC(int degrees)
    {
        Motor.C.rotate(degrees, false);
    }
    /**
     * Used to debug the storage of balls mechanism
     * Moves the motor used in the ball storage mechanism backwards which pulls in the balls
     * @param degrees Moves motor used in the ball storage mechanism a certain amount of degrees
     */
    public void MoveBackwardC(int degrees)
    {
        Motor.C.rotate(-degrees, false);
    }
}
