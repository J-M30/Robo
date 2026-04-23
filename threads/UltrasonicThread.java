package threads;

import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


public class UltrasonicThread implements Runnable {

    private SampleProvider mode;
    private float[] sample;

    private volatile boolean running = true;
    private volatile float distance = 1.0f;

    // filtering buffer (same design as LightSensorThread)
    private float[] buffer = new float[5];
    private int index = 0;
    private int count = 0;

    public UltrasonicThread(SampleProvider mode, float[] sample) {
        this.mode = mode;
        this.sample = sample;
    }

    public void run() {
        while (running) {

            mode.fetchSample(sample, 0);
            float value = sample[0];

            // ---------- VALIDATION ----------
            // ignore unrealistic values
            if (value < 0.02f || value > 2.5f) {
                Delay.msDelay(30);
                continue;
            }

            // ---------- FILTERING (moving average) ----------
            buffer[index] = value;
            index = (index + 1) % buffer.length;

            if (count < buffer.length) count++;

            float sum = 0;
            for (int i = 0; i < count; i++) {
                sum += buffer[i];
            }

            distance = sum / count;

            Delay.msDelay(30);
        }
    }

    public float getDistance() {
        return distance;
    }

    public void stop() {
        running = false;
    }

}

