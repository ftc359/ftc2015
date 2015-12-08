package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.headers.Joystick;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class OneMotorTest extends OpMode {
    private DcMotor motor;

    public void init(){
        motor = hardwareMap.dcMotor.get("test");
    }

    public void loop(){
        motor.setPower(Joystick.exponential(1.00, gamepad1.right_stick_y, 0.10));

        telemetry.addData("motorTest", "Power: " + motor.getPower());
    }
}
