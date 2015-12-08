package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import android.os.SystemClock;

import com.qualcomm.ftcrobotcontroller.headers.Joystick;

public class CompetitionTeleOp extends OpMode{
    DcMotor frontLeft, frontRight, backLeft, backRight, hooks, tapeMeasure; //Initialize DcMotor objects
    Servo climbers, stopper; //Initialize Servo objects

    //Values to be carried between functions
    //int G1ButtonArray = 0; //Bit array used to work in sync with isReleased to determine if a button is being held
    double stopperPosition = 0.75;
    long timeStarted = 0;

    //Constants
    static double THRESHOLD = 0.10;
    static double DRIVEMOTORS_MAX = 1.00;
    static double HOOKS_MAX = 0.20;
    static double TAPEMEASURE_MAX = 1.00;

    private enum climbersPosition{
        OPEN(0.00), CLOSE(1.00);

        private final double position;
        private climbersPosition(final double pos){
            position = pos;
        }
        public double getPosition(){
            return position;
        }
    }



    //Assign the locations of the motors to our DcMotors
    @Override
    public void init(){
        //Initialize drive motors
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        //Initialize secondary motors
        hooks = hardwareMap.dcMotor.get("hooks");
        tapeMeasure = hardwareMap.dcMotor.get("tapeMeasure");

        //Initialize servos
        climbers = hardwareMap.servo.get("climbers");
        stopper = hardwareMap.servo.get("stopper");

        //Set the direction of motors
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start(){
        climbers.setPosition(climbersPosition.CLOSE.getPosition());
        stopper.setPosition(stopperPosition);
    }

    //Actual TeleOp
    @Override
    public void loop(){
        //Map the drive motors to gamepad 1's joysticks
        double leftMotors = Joystick.exponential(DRIVEMOTORS_MAX, gamepad1.left_stick_y, THRESHOLD);
        double rightMotors = Joystick.exponential(DRIVEMOTORS_MAX, gamepad1.right_stick_y, THRESHOLD);

        frontLeft.setPower(leftMotors);
        backLeft.setPower(leftMotors);
        frontRight.setPower(rightMotors);
        backRight.setPower(rightMotors);

        //Map a servo to gamepad 1's bumper buttons
        if(gamepad1.right_bumper)
            climbers.setPosition(climbersPosition.OPEN.getPosition());
        else if(gamepad1.left_bumper)
            climbers.setPosition(climbersPosition.CLOSE.getPosition());

        //Map the secondary motors to gamepad 2's joysticks
        tapeMeasure.setPower(Joystick.exponential(TAPEMEASURE_MAX, gamepad2.right_stick_y, THRESHOLD));
        hooks.setPower(Joystick.exponential(HOOKS_MAX, gamepad2.left_stick_y, THRESHOLD));

        //Map a servo to gamepad 2's bumper buttons
        long timeSinceStart = SystemClock.elapsedRealtime() - timeStarted;
        if(gamepad2.right_bumper) {
            if(timeStarted == 0)
                timeStarted = SystemClock.elapsedRealtime();
            if(timeSinceStart % 10 < 1)
                if(timeSinceStart > 2000)
                    stopperPosition -= (stopperPosition % 0.05 == 0.00)?0.05:stopperPosition % 0.05;
                else
                    stopperPosition -= 0.01;
        }
        else if(gamepad2.left_bumper) {
            if(timeStarted == 0)
                timeStarted = SystemClock.elapsedRealtime();
            if(timeSinceStart % 10 < 1)
                if(timeSinceStart > 2000)
                    stopperPosition += 0.05 - stopperPosition % 0.05;
            else
                stopperPosition += 0.01;
        }
        else
            timeStarted = 0;
        stopperPosition = Range.clip(stopperPosition, 0.00D, 0.95D);
        stopper.setPosition(stopperPosition);

        //Telemetry
        telemetry.addData("driveMotors", String.format("Left Side: %1.2f\tRight Side: %1.2f", leftMotors, rightMotors));
        telemetry.addData("hooks", String.format("Hooks: %1.2f", hooks.getPower()));
        telemetry.addData("tapeMeasure", String.format("Tape Measure: %1.2f", tapeMeasure.getPower()));
        telemetry.addData("servos", String.format("Climbers: %1.2f\tStopper: %1.2f", climbers.getPosition(), stopper.getPosition()));
    }
}
