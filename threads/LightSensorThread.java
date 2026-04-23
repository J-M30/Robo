package threads;

import data.LightData;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LightSensorThread extends Thread {

    private SampleProvider mode;
    private float[] sample;
    private LightData data;

    private float[] buffer = new float[5];
    private int index = 0;
    private int count = 0;

    private volatile boolean running = true;

    public LightSensorThread(SampleProvider mode, float[] sample, LightData data) {
        this.mode = mode;
        this.sample = sample;
        this.data = data;
    }

    public void run() {
        while (running) {
            mode.fetchSample(sample, 0);
            float value = sample[0];
            data.raw = value;

            buffer[index] = value;
            index = (index + 1) % buffer.length;

            if (count < buffer.length) count++;

            float sum = 0;
            for (int i = 0; i < count; i++) {
                sum += buffer[i];
            }

            data.filtered = sum / count;

            Delay.msDelay(10);
        }
    }

    public void stopThread() {
        running = false;
    }
}
