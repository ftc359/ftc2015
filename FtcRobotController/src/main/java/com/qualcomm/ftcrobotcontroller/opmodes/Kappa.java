package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.headers.Encoders;
import com.qualcomm.ftcrobotcontroller.headers.StateMachine;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Kappa extends OpMode {
    private DcMotor motor1, motor2, motor3, motor4;
    StateMachine sm = new StateMachine("drive1", "drive2");

    public Kappa(){}

    @Override
    public void init(){
        motor1 = hardwareMap.dcMotor.get("left");
        motor2 = hardwareMap.dcMotor.get("right");
        motor2.setDirection(DcMotor.Direction.REVERSE);
        motor3 = hardwareMap.dcMotor.get("extend1");
        motor4 = hardwareMap.dcMotor.get("extend2");
    }

    @Override
    public void loop() {
        if (sm.eventInactive("drive1")) {
            sm.queueEvent("drive1");
            Encoders.resetEncoders(motor1, motor2);
        }
        if(sm.eventActive("drive1") && Encoders.encodersReset(motor1, motor2)){
            sm.runEvent("drive1");
        }
    }
}
