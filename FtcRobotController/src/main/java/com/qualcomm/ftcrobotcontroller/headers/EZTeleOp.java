package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.ftcrobotcontroller.headers.ProgramReader;
import com.qualcomm.ftcrobotcontroller.headers.Joystick;

import java.util.List;

public class EZTeleOp extends OpMode{
    private List <DcMotor> motorList; //List for our motors
    private List<Servo> servoList; //List for our servos

    private ProgramReader programReader = new ProgramReader("default"); //Initialize our XML reader

    int boolArray = 0; //Uses bitmath to turn an integer in a giant array of booleans

    @Override
    public void init() {
        //Initialize each motor, configure its settings, and add it to the list
        for(int iterator = 0; iterator < programReader.motorDataList.size(); iterator++) {
            motorList.add(hardwareMap.dcMotor.get(programReader.motorDataList.get(iterator).getName()));
            motorList.get(iterator).setDirection(programReader.motorDataList.get(iterator).getDirection());
        }

        //Initialize each servo, configure its settings, and add it to the list
        for(int iterator = 0; iterator < programReader.servoDataList.size(); iterator++) {
            servoList.add(hardwareMap.servo.get(programReader.servoDataList.get(iterator).getName()));
            servoList.get(iterator).setDirection(programReader.servoDataList.get(iterator).getDirection());
            servoList.get(iterator).scaleRange(
                    programReader.servoDataList.get(iterator).getMinPosition(),
                    programReader.servoDataList.get(iterator).getMaxPosition()
            );
            servoList.get(iterator).setPosition(programReader.servoDataList.get(iterator).getStartingValue());
        }
    }

    @Override
    public void loop() {
        //Joysticks

        //Buttons


        //Dpad
    }

    public Gamepad gamepad(int index){
        if(index == 1) return gamepad1;
        if(index == 2) return gamepad2;
        return null;
    }
}
