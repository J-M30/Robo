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

import data.LightData;
import threads.LightSensorThread;
import threads.UltrasonicThread;

public class robotworkpls {

    // ---- CONSTANTS (easy to tune) ----
    static final int BASE_SPEED = 150;
    static final int TURN_FAST = 180;
    static final int TURN_SLOW = 100;

    static final float OBSTACLE_DISTANCE = 0.15f;
    static final float BLACK_THRESHOLD = 30f; // adjust after testing

    public static void main(String[] args) {

        // ---------- LIGHT SENSOR ----------
        EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
        SampleProvider lightMode = lightSensor.getRedMode();
        float[] lightSample = new float[lightMode.sampleSize()];

        LightData lightData = new LightData();
        LightSensorThread lightThread =
                new LightSensorThread(lightMode, lightSample, lightData);
        lightThread.start();

        // ---------- ULTRASONIC SENSOR ----------
        EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
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

        leftMotor.setSpeed(BASE_SPEED);
        rightMotor.setSpeed(BASE_SPEED);

        // ---------- MAIN LOOP ----------
        while (!Button.ESCAPE.isDown()) {

            float light = lightData.filtered * 100; // convert to %
            float distance = ultraRunnable.getDistance();

            // --- Display ---
            LCD.drawString("Dist: " + distance + "   ", 0, 0);
            LCD.drawString("Light: " + (int)light + "   ", 0, 1);

            // ---------- BEHAVIOR ----------
            if (distance < OBSTACLE_DISTANCE) {
                avoidObstacle(leftMotor, rightMotor);
            }
            else {
                followLine(leftMotor, rightMotor, light);
            }

            Delay.msDelay(30);
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

    // ---------- LINE FOLLOWING ----------
    static void followLine(EV3LargeRegulatedMotor left,
                           EV3LargeRegulatedMotor right,
                           float light) {

        if (light < BLACK_THRESHOLD) {
            // ON LINE → straight
            left.setSpeed(BASE_SPEED);
            right.setSpeed(BASE_SPEED);
        } else {
            // OFF LINE → smooth LEFT turn
            left.setSpeed(TURN_SLOW);
            right.setSpeed(TURN_FAST);
        }

        left.forward();
        right.forward();
    }

    // ---------- OBSTACLE AVOID ----------
    static void avoidObstacle(EV3LargeRegulatedMotor left,
                              EV3LargeRegulatedMotor right) {

        left.stop(true);
        right.stop();

        left.setSpeed(150);
        right.setSpeed(150);

        // reverse
        left.backward();
        right.backward();
        Delay.msDelay(300);

        // turn LEFT (smooth, not spin)
        left.setSpeed(100);
        right.setSpeed(180);

        left.forward();
        right.forward();
        Delay.msDelay(500);
    }
}
