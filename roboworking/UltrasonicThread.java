package roboworking;

import lejos.robotics.SampleProvider;

public class UltrasonicThread implements Runnable {

    private SampleProvider ultrasonicSensor;
    private float[] sample; //store data

    private volatile boolean running = true;

public UltrasonicThread(SampleProvider sensor, float[] sample) {//Constructor
        this.ultrasonicSensor = sensor;
        this.sample = sample;
    }
    private volatile float distance;//latest distance measured
    private volatile boolean objDetected; //Object detection flag

@Override
public void run() {

    while (running) {   

        ultrasonicSensor.fetchSample(sample, 0); // Read sensor value to the sample array
        distance = sample[0];
        objDetected = distance < 0.15f;

        try { //Teachers example of "smart" delay
            if (objDetected) {
                Thread.sleep(100);
            } else {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
    public float getDistance() {
        return distance;
    }
    public void stop() {
        running = false;
    }
   
}
