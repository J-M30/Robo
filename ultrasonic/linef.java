package ultrasonic;



import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;   // allows the sensor to return the samples or data
                                        // e.g., for getting distance data from sonic sensor etc
                                        import lejos.utility.Delay;

public class linef {
    public static void main(String[] args) {


        EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
        SampleProvider colorSample = lightSensor.getColorIDMode();
        float[] colorSampleArray = new float[colorSample.sampleSize()];


        // Creating an instance of US sensor at port 2


        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);


        leftMotor.setSpeed(360);  
        rightMotor.setSpeed(360);


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
            // read sensors
            distance.fetchSample(sample, 0);
            float distanceInMeters = sample[0];


            lightSensor.fetchSample(colorSampleArray, 0);
            float colorID = colorSampleArray[0];


           
           
            // Display the distance on the LCD screen
            LCD.clear();
            LCD.drawString("Dist: " + distanceInMeters + " meters", 0, 0);
            LCD.drawString("Color: " + colorID, 0, 1);


           //Refresh display every 100 ms
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (distanceInMeters < 0.15){
                // Stop the motors if an obstacle is closer than 15 cm
               leftMotor.stop(true);   // true = immediate return
                rightMotor.stop();


                // Move backward for a short duration
                leftMotor.setSpeed(200);
                rightMotor.setSpeed(200);
                leftMotor.backward();
                rightMotor.backward();
                Delay.msDelay(500); // Move backward for 500 ms


                // Turn right for a short duration
                leftMotor.setSpeed(200);
                rightMotor.setSpeed(200);
                leftMotor.forward();
                rightMotor.backward();
                Delay.msDelay(500); // Turn for 500 ms
            }
            else {
                if (colorID == 1) { // If the color detected is black (you may need to adjust this threshold based on your sensor readings)
                    leftMotor.setSpeed(200);
                    rightMotor.setSpeed(200);
                    leftMotor.forward();
                    rightMotor.forward();
                } else {
                    leftMotor.setSpeed(100);
                    rightMotor.setSpeed(100);
                    leftMotor.forward();
                    rightMotor.backward();
                    Delay.msDelay(500);
                    if (colorID == 1){
                        leftMotor.setSpeed(200);
                        rightMotor.setSpeed(200);
                        leftMotor.forward();
                        rightMotor.forward();
                    } else{
                        leftMotor.setSpeed(100);
                        rightMotor.setSpeed(100);
                        leftMotor.backward();
                        rightMotor.forward();
                        Delay.msDelay(1000);
                    }
                }
               
            }
            Delay.msDelay(50);
        }


        // Close US sensor
        ultrasonicSensor.close();
        leftMotor.close();
        rightMotor.close();
        lightSensor.close();


    }

}
