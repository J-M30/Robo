package light;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class valonlukija
{
    public static void main(String[] args)
    {
        EV3ColorSensor colorSensor  = new EV3ColorSensor(SensorPort.S2);
        SampleProvider light        = colorSensor.getAmbientMode();

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        
        // Create an array to hold the sensor data
        float[] sample = new float[light.sampleSize()];

        
        // Continuously display the light intensity until a button is pressed
        while (!Button.ESCAPE.isDown())                 // Exit if the ESCAPE button is pressed
        {
            // Get the current light intensity reading from the sensor
            light.fetchSample(sample, 0);   // 0 is the index where data will be stored


            // Display the light intensity value on the LCD screen
            LCD.clear();
            LCD.drawString((int)(sample[0] * 100) + "%", 0, 0);  // Display as percentage

            
            try 
            {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            
              
        }
        colorSensor.close();
        leftMotor.close();
        rightMotor.close();
    }
}