package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.ftcrobotcontroller.headers.Joystick;

public class CRServo extends OpMode{
    private Servo crServo;


    public void init(){
        crServo = hardwareMap.servo.get("servo");
    }

    public void loop(){
        crServo.setPosition(Joystick.exponential(0.5, gamepad1.left_stick_y, 0.05) + 0.5);
    }
}
