package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.headers.Joystick;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TwoMotorTest extends OpMode {
    private DcMotor motor1, motor2;

    public void init(){
        motor1 = hardwareMap.dcMotor.get("test1");
        motor2 = hardwareMap.dcMotor.get("test2");
        motor1.setDirection(DcMotor.Direction.REVERSE);
    }

    public void loop(){
        motor1.setPower(Joystick.exponential(1.00, gamepad1.right_stick_y, 0.10));
        motor2.setPower(Joystick.exponential(1.00, gamepad1.left_stick_y, 0.10));

        telemetry.addData("motorTest", "Power: " + motor1.getPower());
        telemetry.addData("motorTest2", "Power: " + motor2.getPower());
    }
}
