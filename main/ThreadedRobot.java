package main;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import threads.LightSensorThread;
import threads.LightData;

public class ThreadedRobot {

    public static void main(String[] args) {

        // ---------- LIGHT SENSOR ----------
        EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
        SampleProvider lightMode = lightSensor.getColorIDMode();
        float[] lightSample = new float[lightMode.sampleSize()];

        LightData lightData = new LightData();
        LightSensorThread lightThread =
                new LightSensorThread(lightMode, lightSample, lightData);

        lightThread.start();

        // ---------- ULTRASONIC SENSOR ----------
        EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
        SampleProvider distMode = usSensor.getDistanceMode();
        float[] distSample = new float[distMode.sampleSize()];

        // ---------- MOTORS ----------
        EV3LargeRegulatedMotor leftMotor =
                new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor =
                new EV3LargeRegulatedMotor(MotorPort.A);

        leftMotor.setSpeed(200);
        rightMotor.setSpeed(200);

        // ---------- MAIN LOOP ----------
        while (!Button.ESCAPE.isDown()) {

            // --- Read distance ---
            distMode.fetchSample(distSample, 0);
            float distance = distSample[0];

            // --- Read processed light data ---
            float light = lightData.filtered;
            boolean isBlack = lightData.isBlack;

            // --- Display ---
            LCD.drawString("Dist: " + distance + "   ", 0, 0);
            LCD.drawString("Light: " + light + "   ", 0, 1);

            // ---------- BEHAVIOR ----------
            if (distance < 0.15f) {
                avoidObstacle(leftMotor, rightMotor);
            }
            else if (isBlack) {
                // go straight
                setSpeed(leftMotor, rightMotor, 200);
                leftMotor.forward();
                rightMotor.forward();
            }
            else {
                // simple correction (turn right slightly)
                leftMotor.setSpeed(100);
                rightMotor.setSpeed(200);
                leftMotor.forward();
                rightMotor.forward();
            }

            Delay.msDelay(50);
        }

        // ---------- CLEANUP ----------
        lightThread.stopThread();

        leftMotor.stop();
        rightMotor.stop();

        lightSensor.close();
        usSensor.close();
        leftMotor.close();
        rightMotor.close();
    }

    // ---------- METHODS ----------

    static void avoidObstacle(EV3LargeRegulatedMotor left,
                              EV3LargeRegulatedMotor right) {

        left.stop(true);
        right.stop();

        setSpeed(left, right, 150);

        // reverse
        left.backward();
        right.backward();
        Delay.msDelay(400);

        // turn
        left.forward();
        right.backward();
        Delay.msDelay(450);
    }

    static void setSpeed(EV3LargeRegulatedMotor left,
                         EV3LargeRegulatedMotor right,
                         int speed) {
        left.setSpeed(speed);
        right.setSpeed(speed);
    }
}
