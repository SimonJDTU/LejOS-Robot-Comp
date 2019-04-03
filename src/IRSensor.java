import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;

import java.util.ArrayList;

public class IRSensor
{
    public void Sense()
    {
        float[] DataFromSensor = new float[1];
        int offset = 0;
        ArrayList<String> Modes = new ArrayList<String>();

        EV3IRSensor IRSensor = new EV3IRSensor(SensorPort.S1);
        IRSensor.setCurrentMode(0);
        IRSensor.fetchSample(DataFromSensor, offset);
        Modes = IRSensor.getAvailableModes();


        System.out.println("Sample Size: " + IRSensor.sampleSize());


        for(int i= 0; i < 500; i++)
        {
            IRSensor.fetchSample(DataFromSensor, offset);
            System.out.println(DataFromSensor[0]);

            Delay.msDelay(1000);
            LCD.clear();
        }
        Button.waitForAnyPress();
    }
}
