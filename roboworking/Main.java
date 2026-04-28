package roboworking;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


public class Main {

    public static void Main(String[] args) {

        // ---------- LIGHT SENSOR ----------
        EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
        SampleProvider light = lightSensor.getAmbientMode();
        float[] lightSample = new float[light.sampleSize()];

        LightSensorThread ls = new LightSensorThread(light, lightSample); // ls thread starts
        Thread l = new Thread(ls);
        l.start();

        // ---------- ULTRASONIC SENSOR ----------
        EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
        SampleProvider distMode = usSensor.getDistanceMode();
        float[] distSample = new float[distMode.sampleSize()];


        UltrasonicThread usThread = new UltrasonicThread(distMode, distSample); // Us thread start
        Thread t = new Thread(usThread);
        t.start();

        float distanceInMeters = usThread.getDistance(); //Get distrance from thread

        float color = ls.getColor();

        // ---------- MOTORS ----------
        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        leftMotor.setSpeed(50); 
        rightMotor.setSpeed(50);

        // ---------- MAIN LOOP ----------
        while (!Button.ESCAPE.isDown()) {

            // --- Read distance ---
            distMode.fetchSample(distSample, 0);
            float distance = distSample[0];

            light.fetchSample(lightSample, 0);
            float color1= (lightSample[0]*100); 

            // --- Display ---
            LCD.drawString("Dist: " + distance, 0, 0);
            LCD.drawString("Light:" + (int)(lightSample[0] * 100) + "%", 0, 0);    

            // ---------- BEHAVIOR ----------
            if (distance < 0.15f) {
                LCD.drawString("Vaistetaan: " + distance + "   ", 0, 0);
                avoidObstacle(leftMotor, rightMotor);
            }else{
                if(color1 <= 3){ 
                    leftMotor.forward();
                    rightMotor.forward();
                }else{ 
                    LCD.drawString("Kaannytaan: " + (int)(lightSample[0] * 100) + "%", 0, 0); 
                    leftMotor.forward();
                    rightMotor.stop();
                }
            } 

            Delay.msDelay(50);
        }

        // ---------- CLEANUP ----------

        leftMotor.stop();
        rightMotor.stop();

        lightSensor.close();
        usSensor.close();
        leftMotor.close();
        rightMotor.close();
    }

    // ---------- METHODS ----------

    static void avoidObstacle(EV3LargeRegulatedMotor left,EV3LargeRegulatedMotor right) {

        left.stop(true);
        right.stop();

        left.setSpeed(100);
        right.setSpeed(100);

        // reverse
        left.backward();
        right.backward();
        Delay.msDelay(400);

        // turn
        left.forward();
        right.stop();
        Delay.msDelay(600);

        

    }

}