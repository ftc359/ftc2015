package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.ftcrobotcontroller.headers.PIDController;

public class PIDTest extends OpMode{
    DcMotor motor;
    PIDController PIDctrl = new PIDController(motor, 0.2, 0.1, 0.1, 250);

    @Override
    public void init(){
        motor = hardwareMap.dcMotor.get("test");
    }

    @Override
    public void loop(){
        PIDctrl.setSpeed(0.50);
        telemetry.addData("speed", PIDctrl.getSpeed());
        telemetry.addData("actualSpeed", motor.getPower());
    }
}
