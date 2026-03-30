package src;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class HelloWorld
{
    public static void main(String[] args)
    {
        // String message1 = "This is my 1st LEGO code.";
        // String message2 = "Make me autonomous";
        // String message3 = "Press any button to Stop.";
        LCD.clear();
        LCD.drawString("Welcome", 0, 0);
        
        Delay.msDelay(1000);
       
        // TextWrap(message2);
        LCD.drawString("This is my 1st LEGO code.", 0, 1);
       
        Delay.msDelay(2000);
        LCD.drawString("Make me autonomous", 0, 2);
        
        // TextWrap(message3);
        LCD.drawString("Press any button to Stop.", 0,4);
        
       
        // Wait for a button press to exit
        Button.waitForAnyPress();
    }


}