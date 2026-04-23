package viimeinenYritys;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


public class Robot {

    public static void main(String[] args) {

        // ---------- LIGHT SENSOR ----------
        EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);

        SampleProvider lightMode = lightSensor.getColorIDMode();
        float[] lightSample = new float[lightMode.sampleSize()];

        lightData3 lightData = new lightData3();

        lightSensorThread3 lightThread = new lightSensorThread3(lightMode, lightSample, lightData);

        lightThread.start();


        // ---------- ULTRASONIC SENSOR ----------
        EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);

        SampleProvider distMode = usSensor.getDistanceMode();
        float[] distSample = new float[distMode.sampleSize()];

        UST2 ultraRunnable = new UST2(distMode, distSample);

        Thread ultraThread = new Thread(ultraRunnable);
        ultraThread.start();


        // ---------- MOTORS ----------
        EV3LargeRegulatedMotor leftMotor =new EV3LargeRegulatedMotor(MotorPort.B);

        EV3LargeRegulatedMotor rightMotor =new EV3LargeRegulatedMotor(MotorPort.A);

        leftMotor.setSpeed(200);
        rightMotor.setSpeed(200);


        // ---------- MAIN LOOP ----------
        while (!Button.ESCAPE.isDown()) {

            // --- Light data (from thread) ---
            float light = lightData.filtered;
            boolean isBlack = lightData.isBlack;

            // --- Ultrasonic data (from thread) ---
            float distance = ultraRunnable.getDistance();

            // --- Display ---
            LCD.drawString("Dist: " + distance + "   ", 0, 0);
            LCD.drawString("Light: " + light + "   ", 0, 1);

            // ---------- BEHAVIOR ----------
            if (distance < 0.15f) {
                avoidObstacle(leftMotor, rightMotor);
            }
            else if (isBlack) {
                setSpeed(leftMotor, rightMotor, 200);
                leftMotor.forward();
                rightMotor.forward();
            }
            else {
                // correction turn
                leftMotor.setSpeed(100);
                rightMotor.setSpeed(200);
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

    static void setSpeed(EV3LargeRegulatedMotor left,
                         EV3LargeRegulatedMotor right,
                         int speed) {
        left.setSpeed(speed);
        right.setSpeed(speed);
    }
}