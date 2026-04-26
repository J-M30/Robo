package roboworking;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LightSensorThread extends Thread{
    private SampleProvider light;
    private float[] lightSample;

    private volatile boolean running = true;
    
public LightSensorThread(SampleProvider light, float[] lightSample){
        this.light=light;
        this.lightSample=lightSample; 
}  

        private volatile float color;
        private volatile boolean rightLight;
        private volatile boolean wrongLight1;
        private volatile boolean wrongLight2;

@Override
 public void run(){
        while (running){

            light.fetchSample(lightSample, 0);
            float color= (lightSample[0]*100); 
            rightLight = color > 1 && color < 3;
            wrongLight1 = color <1;
            wrongLight2 = color >3;
            
            
            try{
                if(wrongLight1 || wrongLight2){
                    Thread.sleep(100);
                } else{
                    Thread.sleep(100);
                }  
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        } 
    }

    public float getColor(){
        return color;
    }
    public void stopThis(){
        running=false;
    }   
       
}
        
    
