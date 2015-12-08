package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class CRServo extends OpMode{
    private DcMotor motor;
    private Servo CRservo;

    public void init(){
        motor = hardwareMap.dcMotor.get("motor");
        CRservo = hardwareMap.servo.get("servo");
    }

    public void loop(){
        motor.setPower(gamepad1.right_stick_y);
        CRservo.setPosition(gamepad1.left_stick_y);
    }
}
