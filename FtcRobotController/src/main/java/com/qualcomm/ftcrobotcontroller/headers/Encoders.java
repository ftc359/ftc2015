package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.DcMotor;

public final class Encoders {
    private Encoders(){}

    public static void resetEncoders(DcMotor... motors){
        for(DcMotor motor : motors)
            motor.setMode(com.qualcomm.robotcore.hardware.DcMotorController.RunMode.RESET_ENCODERS);
    }

    public static boolean encodersReset(DcMotor... motors){
        for(DcMotor motor : motors)
            if(motor.getCurrentPosition() != 0) return false;

        return true;
    }

    public static void runEncoders(DcMotor... motors){
        for(DcMotor motor : motors)
            if(motor.getMode() == com.qualcomm.robotcore.hardware.DcMotorController.RunMode.RESET_ENCODERS)
                motor.setMode(com.qualcomm.robotcore.hardware.DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public static boolean positionReached(double position, DcMotor... motors){
        for(DcMotor motor : motors)
            if(Math.abs(motor.getCurrentPosition()) < Math.abs(position)) return false;

        return true;
    }
}