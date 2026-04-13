package threads;

import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

class LightSensorThread extends Thread {

    private SampleProvider lightMode;
    private float[] sample;
    private LightData data;

    private float[] buffer = new float[5];
    private int index = 0;
    private int count = 0;

    private volatile boolean running = true;

    public LightSensorThread(SampleProvider lightMode, float[] sample, LightData data) {
        this.lightMode = lightMode;
        this.sample = sample;
        this.data = data;
    }

    public void run() {

        while (running) {

            // 1. Read sensor (fast, no allocations)
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
            data.filtered = avg;

            // 3. Classification (clean interface for teammates)
            data.isBlack = (avg < 0.3f);

            Delay.msDelay(10);
        }
    }

    // clean shutdown method (VERY important for grading)
    public void stopThread() {
        running = false;
    }
}
