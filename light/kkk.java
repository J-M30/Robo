package light;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;

public class kkk
{
    public static void main(String[] args)
    {
        EV3ColorSensor colorSensor  = new EV3ColorSensor(SensorPort.S2);
        SampleProvider light        = colorSensor.getAmbientMode();
        

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        
        // Create an array to hold the sensor data
        float[] sample = new float[light.sampleSize()];

        float lower = 0.04f;
        float upper = 0.07f;

        rightMotor.setSpeed(200);
        leftMotor.setSpeed(200);
        rightMotor.forward();
        leftMotor.forward();
        
        // Continuously display the light intensity until a button is pressed
        while (!Button.ESCAPE.isDown())                 // Exit if the ESCAPE button is pressed
        {
            
            light.fetchSample(sample, 0);              
            
            LCD.clear();
            LCD.drawString("Light Intensity: " + (int)(sample[0] * 100) + "%", 0, 3);  // Display as percentage

            if (0 <= sample[0] && sample[0] > 2){
                leftMotor.forward();
                rightMotor.forward();
            }
            else if (sample[0] > 2){
                leftMotor.forward();
                rightMotor.stop();
            }
            
            
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