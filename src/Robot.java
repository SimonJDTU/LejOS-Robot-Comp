import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
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
     * The robot turns for a given duration in ms.
     * @param ms The amount of time the robot should turn for in ms
     * @param speed The speed at which the robot turns
     */
    public void TurnClockwiseAB(int ms, int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);
        Motor.A.forward();
        Motor.B.backward();

        Delay.msDelay(ms);

        Motor.A.stop(true);
        Motor.B.stop(true);
    }

    /**
     * Turns the robot clockwise using the motors at input A as the left motor and and B as the right motor.
     * The robot turns for a given duration in ms.
     * @param duration The amount of time the robot should turn for in ms
     * @param speed The speed at which the robot turns
     */
    public void TurnCounterclockwiseAB(int duration, int speed)
    {
        Motor.A.setSpeed(speed);
        Motor.B.setSpeed(speed);
        Motor.A.backward();
        Motor.B.forward();

        Delay.msDelay(duration);

        Motor.A.stop(true);
        Motor.B.stop(true);
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
     *
     */
    public void CaptureBalls(int speed)
    {
        Motor.D.setSpeed(300);
        Motor.D.backward();
    }

    public void ReleaseBalls(int speed)
    {
        Motor.C.setSpeed(speed);
        Motor.D.setSpeed(speed);
        Motor.C.forward();
        Motor.D.forward();
    }

    public void StopReleaseBalls()
    {
        Motor.D.stop();
        Motor.C.stop();
    }

    public void StopCaptureBalls()
    {
        Motor.C.stop();
    }

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
                TurnClockwiseAB(200, 900);
            }
        }
    }
    public void MoveForwardC(int degrees)
    {
        Motor.C.rotate(degrees, false);
    }
    public void MoveBackwardC(int degrees)
    {
        Motor.C.rotate(-degrees, false);
    }
}
