package ultrasonic;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;   // allows the sensor to return the samples or data
                                        // e.g., for getting distance data from sonic sensor etc
                                        import lejos.utility.Delay;

public class newUS {

    public static void main(String[] args) {
        // Creating an instance of US sensor at port 2

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        leftMotor.setSpeed(360);   
        rightMotor.setSpeed(360);
        leftMotor.forward();
        rightMotor.forward();
        Delay.msDelay(3000); 

        EV3UltrasonicSensor ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S1);
        
        // Get the distance sample provider
        SampleProvider distance = ultrasonicSensor.getDistanceMode();
        
        // Create a sample array to hold the distance value
        // even though sonic sensor gives distance as an o/p, but since other sensors, e.g., light sensor
        // can provide multiple values, therefore to keep consistency, I'm using sampleprovider
        float[] sample = new float[distance.sampleSize()];
    
        // Keep displaying the distance, until user presses a button
        while (!Button.ESCAPE.isDown())
        {
            // Get the curRent distnce reading from the US sensor
            distance.fetchSample(sample, 0);
            
            // Display the distance on the LCD screen
            LCD.clear();
            LCD.drawString("Dist: " + sample[0] + " meters", 0, 0);

           //Refresh display every 100 ms
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (distance.sampleSize() == 0.10){
               leftMotor.setSpeed(0);
                rightMotor.setSpeed(0);
                Delay.msDelay(3000);  
            }

        }

        

        // leftMotor.setSpeed(100);
        // rightMotor.setSpeed(100);        
        // leftMotor.rotateTo(360, true);
        // leftMotor.rotateTo(360,true);
        // leftMotor.setSpeed(360);   
        // rightMotor.setSpeed(360);
        // Close US sensor
        ultrasonicSensor.close();
        leftMotor.close();
        rightMotor.close();

    }
}