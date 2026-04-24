package light;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;
<<<<<<< Updated upstream
=======
import lejos.utility.Delay;
>>>>>>> Stashed changes

public class kkk
{
    public static void main(String[] args)
    {
        EV3ColorSensor colorSensor  = new EV3ColorSensor(SensorPort.S2);
        SampleProvider light        = colorSensor.getAmbientMode();
<<<<<<< Updated upstream
        

=======
>>>>>>> Stashed changes
        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        
        // Create an array to hold the sensor data
        float[] sample = new float[light.sampleSize()];

<<<<<<< Updated upstream
        float lower = 0.04f;
        float upper = 0.07f;

        rightMotor.setSpeed(200);
        leftMotor.setSpeed(200);
        rightMotor.forward();
        leftMotor.forward();
=======
>>>>>>> Stashed changes
        
        // Continuously display the light intensity until a button is pressed
        while (!Button.ESCAPE.isDown())                 // Exit if the ESCAPE button is pressed
        {
<<<<<<< Updated upstream
            
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
            
            
=======
            // Get the current light intensity reading from the sensor
            light.fetchSample(sample, 0);               // 0 is the index where data will be stored
            
            // Display the light intensity value on the LCD screen
            LCD.clear();
            LCD.drawString( (int)(sample[0] * 100) + "%", 0, 0);            
>>>>>>> Stashed changes
            try 
            {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

<<<<<<< Updated upstream
            
=======
            if (0 < (sample[0]*100) && (sample[0]*100) <2 ){
                leftMotor.setSpeed(100);
                rightMotor.setSpeed(100);
                leftMotor.forward();
                rightMotor.forward();
            } else {
                while (0 > (sample[0]*100) || (sample[0]*100) >2 ){
                leftMotor.setSpeed(100);
                rightMotor.setSpeed(100);
                leftMotor.backward();
                rightMotor.forward();
                Delay.msDelay(100);
                }
                
            }
>>>>>>> Stashed changes
              
        }
        colorSensor.close();
        leftMotor.close();
        rightMotor.close();
    }
}