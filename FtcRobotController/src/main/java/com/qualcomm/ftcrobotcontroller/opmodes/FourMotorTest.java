package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.headers.Joystick;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class FourMotorTest extends OpMode {
    private DcMotor motor1, motor2, motor3, motor4;

    public void init(){
        motor1 = hardwareMap.dcMotor.get("test1");
        motor2 = hardwareMap.dcMotor.get("test2");
        motor3 = hardwareMap.dcMotor.get("test3");
        motor4 = hardwareMap.dcMotor.get("test4");
        motor1.setDirection(DcMotor.Direction.REVERSE);
        motor2.setDirection(DcMotor.Direction.REVERSE);
    }

    public void loop(){
        motor2.setPower(Joystick.exponential(1.00, gamepad1.right_stick_y, 0.10));
        motor4.setPower(Joystick.exponential(1.00, gamepad1.right_stick_y, 0.10));
        motor1.setPower(Joystick.exponential(1.00, gamepad1.left_stick_y, 0.10));
        motor3.setPower(Joystick.exponential(1.00, gamepad1.left_stick_y, 0.10));

        telemetry.addData("motorTest", "Power: " + gamepad1.right_stick_y);
        telemetry.addData("motorTest2", "Power: " + gamepad1.left_stick_y);
    }
}
