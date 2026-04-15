package main;

import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LightSensorThread2 extends Thread {

    private SampleProvider lightMode;
    private float[] sample;
    private LightData2 data;

    private float[] buffer = new float[5];
    private int index = 0;
    private int count = 0;

    private volatile boolean running = true;

    public LightSensorThread2(SampleProvider lightMode, float[] sample, LightData2 data) {
        this.lightMode = lightMode;
        this.sample = sample;
        this.data = data;
    }

    public void run() {

        while (running) {

            // 1. Read sensor
            lightMode.fetchSample(sample, 0);
            float value = sample[0];
            data.raw = value;

            // 2. Moving average filter
            buffer[index] = value;
            index = (index + 1) % buffer.length;

            if (count < buffer.length) count++;

            float sum = 0;
            for (int i = 0; i < count; i++) {
                sum += buffer[i];
            }

            float avg = sum / count;

            // 3. Store processed values (ONLY here)
            data.filtered = avg;
            data.isBlack = avg < 0.3f;

            Delay.msDelay(10);
        }
    }

    public void stopThread() {
        running = false;
    }
}