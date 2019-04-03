import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class UltrasonicSensor
{
    private static EV3UltrasonicSensor UltrasonicSensor = new EV3UltrasonicSensor(SensorPort.S1);
    public void UltrasonicSensor()
    {
        final SampleProvider sp = UltrasonicSensor.getDistanceMode();
        float distanceValueCentimeter = 0;

        final int iteration_threshhold = 50;
        for(int i = 0; i < iteration_threshhold; i++)
        {
            float[] sample = new float[sp.sampleSize()];
            sp.fetchSample(sample, 0);
            distanceValueCentimeter = sample[0] * 100;

            System.out.println( distanceValueCentimeter);

            Delay.msDelay(200);
        }
        Button.waitForAnyPress();
    }
}
