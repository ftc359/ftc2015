package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.Gamepad;

public final class Joystick{
    private static final double JOYSTICK_MAX_VALUE = 1.00;

    private Joystick(){}

    public static boolean isReleased(Gamepad gamepad, button button, int bitfield){
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
        if(((bitfield & button.getValue()) != button.getValue() && bool) || (((bitfield & button.getValue()) == button.getValue()) && !bool)){
            bitfield ^= button.getValue();
            return !((bitfield & button.getValue()) == button.getValue());
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
        a(0), b(1), x(2), y(3), left_bumper(4), right_bumper(5), left_stick_button(6), right_stick_button(7), left_trigger(8), right_trigger(9);
        private final int value;
        private button(final int newValue){
            value = newValue;
        }
        public int getValue(){
            return 1 << value;
        }
    }
}