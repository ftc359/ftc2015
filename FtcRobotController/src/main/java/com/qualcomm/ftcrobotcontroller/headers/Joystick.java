package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.Gamepad;

public final class Joystick{
    private static final double JOYSTICK_MAX_VALUE = 1.00;

    private Joystick(){}

    public static boolean isReleased(Gamepad gamepad, button button, int boolArray){
        boolean bool = false;
        switch(button){
            case a:
                bool = gamepad.a;
                break;
            case b:
                bool = gamepad.b;
                break;
            case x:
                bool = gamepad.x;
                break;
            case y:
                bool = gamepad.y;
                break;
            case left_bumper:
                bool = gamepad.left_bumper;
                break;
            case right_bumper:
                bool = gamepad.right_bumper;
                break;
            case left_stick_button:
                bool = gamepad.left_stick_button;
                break;
            case right_stick_button:
                bool = gamepad.right_stick_button;
                break;
            case left_trigger:
                bool = (gamepad.left_trigger > 0.0);
                break;
            case right_trigger:
                bool = (gamepad.right_trigger > 0.0);
                break;
        }
        if(((boolArray & button.getValue()) != button.getValue() && bool) || (((boolArray & button.getValue()) == button.getValue()) && !bool)){
            boolArray ^= button.getValue();
            return !((boolArray & button.getValue()) == button.getValue());
        }
        return false;
    }

    public static double exponential(double maxPower, double joystickCurrent, double deadzone){
        if(joystickCurrent == 0) return 0;
        double polarity = joystickCurrent/Math.abs(joystickCurrent);
        return polarity*(maxPower-Math.sqrt(((Math.abs(joystickCurrent)-JOYSTICK_MAX_VALUE)*(maxPower*maxPower))/(deadzone-JOYSTICK_MAX_VALUE)));
    }

    public static double linear(double maxPower, double joystickCurrent, double deadzone){
        if(joystickCurrent == 0) return 0;
        double polarity = joystickCurrent/Math.abs(joystickCurrent);
        return polarity*(maxPower-((Math.abs(joystickCurrent)-JOYSTICK_MAX_VALUE)*maxPower)/(deadzone-JOYSTICK_MAX_VALUE));
    }

    public enum button{
        a(512), b(256), x(128), y(64), left_bumper(32), right_bumper(16), left_stick_button(8), right_stick_button(4), left_trigger(2), right_trigger(1);
        private final int value;
        private button(final int newValue){
            value = newValue;
        }
        public int getValue(){
            return value;
        }
    }
}