package light;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class viivanseuraaja
{
    public static void main(String[] args)
    {
        EV3ColorSensor colorSensor  = new EV3ColorSensor(SensorPort.S2);
        SampleProvider light        = colorSensor.getAmbientMode();

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        
        // Create an array to hold the sensor data
        float[] sample = new float[light.sampleSize()];
        
        leftMotor.setSpeed(50);
        rightMotor.setSpeed(50);
        
        
        while (!Button.ESCAPE.isDown())                 
        {

            light.fetchSample(sample, 0);               
            
            // Display the light intensity value on the LCD screen
            LCD.clear();
            LCD.drawString( (int)(sample[0] * 100) + "%", 0, 0);            
            try 
            {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (1 < (sample[0]*100) && (sample[0]*100) <3 ){
                leftMotor.forward();
                rightMotor.forward();
            } else if (1 > (sample[0]*100)) {
                leftMotor.backward();
                rightMotor.stop();
            } else{
                leftMotor.forward();
                rightMotor.stop();
            }

              
        }
        colorSensor.close();
        leftMotor.close();
        rightMotor.close();
    }
}