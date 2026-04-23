package light;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class nya
{
    public static void main(String[] args)
    {
        EV3ColorSensor colorSensor  = new EV3ColorSensor(SensorPort.S2);
        SampleProvider light        = colorSensor.getAmbientMode();

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

        float[] sample = new float[light.sampleSize()];
        float target = 38;
        double gain=0.6;
        float f=(float)gain;
        float color=sample[0];

        while(!Button.ESCAPE.isDown()){

            LCD.clear();
            LCD.drawString("Light Intensity: " + color, 0, 0);

            float correction =  color - target;
            float turnPower = (float)(correction*gain);
            leftMotor.setSpeed(turnPower);
            rightMotor.setSpeed(turnPower);
        }

        colorSensor.close();
        leftMotor.close();
        rightMotor.close();
    }
}