package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.ftcrobotcontroller.headers.PIDController;
import com.qualcomm.ftcrobotcontroller.headers.Encoders;

public class PIDTest extends OpMode{
    DcMotor motor;
    PIDController PIDctrl;

    @Override
    public void init(){
        motor = hardwareMap.dcMotor.get("test");
        PIDctrl = new PIDController(motor, 0.05, 0.02, 0.02, 250);
        Encoders.runEncoders(motor);
    }

    @Override
    public void loop() {
        PIDctrl.setSpeed(0.25);
        telemetry.addData("speed", PIDctrl.getSpeed());
        telemetry.addData("actualSpeed", motor.getPower());
        telemetry.addData("lastpos", PIDctrl.lastEncoderCount);
        telemetry.addData("lasttime", PIDctrl.lastTime);
        telemetry.addData("lasterror", PIDctrl.lastError);
        telemetry.addData("errorsum", PIDctrl.errorSum);
    }

    @Override
    public void stop(){
        PIDctrl.turnOff();
    }
}
