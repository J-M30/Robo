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

        int baseSpeed = 120;
        float Kp = 400;

        // recovery variables
        boolean lost = false;
int lastDirection = 1; // 1 = right, -1 = left

while (!Button.ESCAPE.isDown()) {

    float light = lightData.filtered;
    float distance = ultraRunnable.getDistance();

    LCD.drawString("L:" + light + "   ", 0, 1);
    LCD.drawString("D:" + distance + "   ", 0, 2);

    // ---------- OBSTACLE ----------
    if (distance < 0.15f) {
        avoidObstacle(leftMotor, rightMotor);
        continue;
    }

    // ---------- NORMALIZED LINE VALUE ----------
    float normalized = (light - black) / (white - black);
    normalized = Math.max(0, Math.min(1, normalized));

    float error = normalized - 0.5f;

    // ---------- LOST LINE DETECTION ----------
    if (Math.abs(error) > 0.35f) {
        lost = true;
    }

    // ---------- RECOVERY MODE ----------
    if (lost) {

        // rotate in last known direction
        leftMotor.setSpeed(120);
        rightMotor.setSpeed(120);

        if (lastDirection > 0) {
            leftMotor.forward();
            rightMotor.backward();
        } else {
            leftMotor.backward();
            rightMotor.forward();
        }

        // exit recovery when line is found again
        if (Math.abs(error) < 0.2f) {
            lost = false;
        }

        Delay.msDelay(40);
        continue;
    }

    // ---------- NORMAL LINE FOLLOWING ----------
    int correction = (int)(error * Kp);

    int leftSpeed = baseSpeed - correction;
    int rightSpeed = baseSpeed + correction;

    leftSpeed = Math.max(60, Math.min(250, leftSpeed));
    rightSpeed = Math.max(60, Math.min(250, rightSpeed));

    leftMotor.setSpeed(leftSpeed);
    rightMotor.setSpeed(rightSpeed);

    leftMotor.forward();
    rightMotor.forward();

    // remember direction of correction
    if (correction > 0) lastDirection = 1;
    else if (correction < 0) lastDirection = -1;

    Delay.msDelay(40);
    lightSensor.close();
    usSensor.close();
}

}
    private static void avoidObstacle(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right) {
        // simple obstacle avoidance: backup, turn, and move forward
        left.setSpeed(150);
        right.setSpeed(150);

        // backup
        left.backward();
        right.backward();
        Delay.msDelay(500);

        // turn
        left.forward();
        right.backward();
        Delay.msDelay(400);

        // move forward
        left.forward();
        right.forward();
        Delay.msDelay(500);
    }

}