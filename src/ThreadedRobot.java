package src;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import threads.LightData;
import threads.LightSensorThread;
import threads.UltrasonicThread;

public class ThreadedRobot {

    public static void main(String[] args) {

        // ---------- LIGHT SENSOR ----------
        EV3ColorSensor lightSensor =
                new EV3ColorSensor(SensorPort.S2);

        // use RedMode (not ColorID)
        SampleProvider lightMode = lightSensor.getRedMode();
        float[] lightSample = new float[lightMode.sampleSize()];

        LightData lightData = new LightData();

        LightSensorThread lightThread =
                new LightSensorThread(lightMode, lightSample, lightData);

        lightThread.start();


        // ---------- ULTRASONIC SENSOR ----------
        EV3UltrasonicSensor usSensor =
                new EV3UltrasonicSensor(SensorPort.S1);

        SampleProvider distMode = usSensor.getDistanceMode();
        float[] distSample = new float[distMode.sampleSize()];

        UltrasonicThread ultraRunnable =
                new UltrasonicThread(distMode, distSample);

        Thread ultraThread = new Thread(ultraRunnable);
        ultraThread.start();


        // ---------- MOTORS ----------
        EV3LargeRegulatedMotor leftMotor =
                new EV3LargeRegulatedMotor(MotorPort.B);

        EV3LargeRegulatedMotor rightMotor =
                new EV3LargeRegulatedMotor(MotorPort.A);

        leftMotor.setSpeed(150);
        rightMotor.setSpeed(150);


        // ---------- MAIN LOOP ----------
        while (!Button.ESCAPE.isDown()) {

            float light = lightData.filtered;
            boolean isBlack = lightData.isBlack;

            float distance = ultraRunnable.getDistance();

            // --- Display ---
            LCD.drawString("Dist: " + distance + "   ", 0, 0);
            LCD.drawString("Light: " + light + "   ", 0, 1);

            // ---------- BEHAVIOR ----------
            if (distance < 0.15f) {
                avoidObstacle(leftMotor, rightMotor);
            }
            else {
                if (isBlack) {
                    // on line → go straight
                    leftMotor.setSpeed(150);
                    rightMotor.setSpeed(150);
                } else {
                    // off line → turn LEFT to find it
                    leftMotor.setSpeed(200);
                    rightMotor.setSpeed(80);
                }

                leftMotor.forward();
                rightMotor.forward();
            }

            Delay.msDelay(50);
        }

        // ---------- CLEANUP ----------
        lightThread.stopThread();

        ultraRunnable.stop();
        ultraThread.interrupt();

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

        left.setSpeed(150);
        right.setSpeed(150);

        // reverse
        left.backward();
        right.backward();
        Delay.msDelay(400);

        // turn
        left.forward();
        right.backward();
        Delay.msDelay(450);
    }
}
