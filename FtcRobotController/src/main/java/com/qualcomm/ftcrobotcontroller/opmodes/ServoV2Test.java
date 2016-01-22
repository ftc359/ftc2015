package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.ftcrobotcontroller.headers.ServoV2;
import com.qualcomm.ftcrobotcontroller.headers.Timer;

public class ServoV2Test extends OpMode{
    ServoV2 servo;
    Timer timer;

    @Override
    public void init(){
        servo = new ServoV2(hardwareMap.servo.get("test"));
    }

    @Override
    public void start(){
        timer = new Timer();
        servo.setChangeRate(1);
    }

    boolean first = true;

    @Override
    public void loop(){
        if(first) {
            servo.setPosition(0D);
            first = !timer.secondsPassed(1);
        }
        else
            servo.setPosition(1D);
        telemetry.addData("servoPos", servo.getActualPosition());
    }
}
