package src;

import data.LightData;
import threads.*;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;



public class ThreadedRobot {

    public static void main(String[] args) {

        EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
        SampleProvider lightMode = lightSensor.getRedMode();
        float[] lightSample = new float[lightMode.sampleSize()];

        LightData lightData = new LightData();
        LightSensorThread lightThread = new LightSensorThread(lightMode, lightSample, lightData);
        lightThread.start();

        EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
        SampleProvider distMode = usSensor.getDistanceMode();
        float[] distSample = new float[distMode.sampleSize()];

        UltrasonicThread ultraRunnable = new UltrasonicThread(distMode, distSample);
        Thread ultraThread = new Thread(ultraRunnable);
        ultraThread.start();

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        // ---------- CALIBRATION ----------
        LCD.drawString("WHITE", 0, 0);
        Button.waitForAnyPress();
        Delay.msDelay(500);
        float white = lightData.filtered;

        LCD.clear();
        LCD.drawString("BLACK", 0, 0);
        Button.waitForAnyPress();
        Delay.msDelay(500);
        float black = lightData.filtered;

        float target = (white + black) / 2;

        int baseSpeed = 120;
        float Kp = 400;

        // recovery variables
        boolean lost = false;
        int lastTurn = 1; // 1 = right, -1 = left

        while (!Button.ESCAPE.isDown()) {

            float light = lightData.filtered;
            float distance = ultraRunnable.getDistance();

            LCD.drawString("L:" + light + "   ", 0, 1);
            LCD.drawString("D:" + distance + "   ", 0, 2);

            if (distance < 0.15f) {
                avoidObstacle(leftMotor, rightMotor);
                continue;
            }

            float error = light - target;

            // ---------- LOST LINE DETECTION ----------
            if (Math.abs(error) > 0.2f) {
                lost = true;
            }

            if (lost) {
                // spin in last known direction
                if (lastTurn > 0) {
                    leftMotor.setSpeed(120);
                    rightMotor.setSpeed(120);
                    leftMotor.forward();
                    rightMotor.backward();
                } else {
                    leftMotor.setSpeed(120);
                    rightMotor.setSpeed(120);
                    leftMotor.backward();
                    rightMotor.forward();
                }

                // check if line found again
                if (Math.abs(error) < 0.05f) {
                    lost = false;
                }

                Delay.msDelay(50);
                continue;
            }

            int correction = (int)(error * Kp);

            int leftSpeed = baseSpeed - correction;
            int rightSpeed = baseSpeed + correction;

            // remember last turn direction
            if (correction > 0) lastTurn = 1;
            if (correction < 0) lastTurn = -1;

            leftSpeed = Math.max(50, Math.min(300, leftSpeed));
            rightSpeed = Math.max(50, Math.min(300, rightSpeed));

            leftMotor.setSpeed(leftSpeed);
            rightMotor.setSpeed(rightSpeed);

            leftMotor.forward();
            rightMotor.forward();

            Delay.msDelay(50);
        }

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

    static void avoidObstacle(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right) {
        left.stop(true);
        right.stop();

        left.setSpeed(150);
        right.setSpeed(150);

        left.backward();
        right.backward();
        Delay.msDelay(400);

        left.forward();
        right.backward();
        Delay.msDelay(450);
    }
}