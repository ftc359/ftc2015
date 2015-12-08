package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.headers.Joystick;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class motorTest extends OpMode {
    DcMotor test1, test2, test3, test4;

    public motorTest(){}

    public void init(){
        test1 = hardwareMap.dcMotor.get("test1");
        test2 = hardwareMap.dcMotor.get("test2");
        test3 = hardwareMap.dcMotor.get("test3");
        test4 = hardwareMap.dcMotor.get("test4");
        test3.setDirection(DcMotor.Direction.REVERSE);
        test4.setDirection(DcMotor.Direction.REVERSE);
    }

    public void loop(){
        float joyY1 = (float) Joystick.exponential(1.00, gamepad1.right_stick_y, 0.10);
        float joyY2 = (float) Joystick.exponential(1.00, gamepad1.left_stick_y, 0.10);

        test1.setPower(joyY1);
        test2.setPower(joyY1);
        test3.setPower(joyY2);
        test4.setPower(joyY2);

        telemetry.addData("motor1_2", "Motors 1 and 2: " + String.format("%d", test1.getCurrentPosition()));
        telemetry.addData("motor3_4", "Motors 3 and 4: " + String.format("%d", test3.getCurrentPosition()));
        telemetry.addData("y1", String.format("%1.2f", joyY1));
        telemetry.addData("y2", String.format("%1.2f", joyY2));
    }

    public void stop(){}
}
