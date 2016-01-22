package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import com.qualcomm.ftcrobotcontroller.headers.ServoV2;
import com.qualcomm.ftcrobotcontroller.headers.GamepadV2;
import com.qualcomm.ftcrobotcontroller.headers.Joystick;
import com.qualcomm.ftcrobotcontroller.headers.Timer;

public class CompetitionTeleOp extends OpMode{
    //INITIALIZE VARIABLES
    //Custom gamepads
    GamepadV2 joy1 = new GamepadV2(0.20F, (float) THRESHOLD);
    GamepadV2 joy2 = new GamepadV2(0.20F, (float) THRESHOLD);

    //Motors
    private DcMotor frontLeft, frontRight, backLeft, backRight, //Drive motors
    tapeMeasure, //Motor to wind and unwind the tape measure
    lift, //Motor to extend and retract the drawer slides
    intake; //Motor to move the surgical tubing

    //Servos
    private ServoV2 aimer, //180 Servo to adjust the slope of the tape measure
    stopper, //180 Servo to stop the tape measure from unwinding when hanging
    filter, //180 Servos to raise and lower the front debris filter
    dumper, //CR Servo to dump the climbers loaded before autonomous
    tilter, //CR Servo to rotate the bucket from side to side
    pusher; //CR Servo to push out the loaded blocks

    //Constants
    private final static double THRESHOLD = 0.10D,
    DRIVE_MAX = 1.00D,
    TAPEMEASURE_MAX = 0.80D,
    LIFT_MAX = 1.00D,
    INTAKE_MAX = 1.00D,

    CR_EQUILIBRIUM = 0.50D, //0.50 means that it will apply no power to itself, > 0.50 is in the CW direction and < 0.50 is in the CCW direction
    AIMER_MIN = 0.16D, AIMER_MAX = 1.00D,
    STOPPER_CLOSE = 0.00D, STOPPER_OPEN = 1.00D,
    DUMPER_UP = 0.00D, DUMPER_DOWN = 1.00D,
    FILTER_MIN = 0.00D, FILTER_MAX = 1.00D;

    //Misc
    private long timeStarted = 0;
    private double aimerPosition = AIMER_MIN;
    private boolean stopperMax = false, dumperMax = false;
    
    @Override
    public void init(){
        //Assign motors
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        tapeMeasure = hardwareMap.dcMotor.get("tapeMeasure");
        lift = hardwareMap.dcMotor.get("lift");
        intake = hardwareMap.dcMotor.get("intake");

        lift.setDirection(DcMotor.Direction.REVERSE);

        //Assign servos
        aimer = new ServoV2(hardwareMap.servo.get("aimer"));
        stopper = new ServoV2(hardwareMap.servo.get("stopper"));
        filter = new ServoV2(hardwareMap.servo.get("filter"));
        dumper = new ServoV2(hardwareMap.servo.get("dumper"));
        tilter = new ServoV2(hardwareMap.servo.get("tilter"));
        pusher = new ServoV2(hardwareMap.servo.get("pusher"));
    }

    @Override
    public void start(){
        try {
            //Initialize servos to their starting values
            //All CR Servos should start at equilibrium so that they don't move
            tilter.setPosition(CR_EQUILIBRIUM);
            pusher.setPosition(CR_EQUILIBRIUM);

            //All 180 Servos should start at preset values
            dumper.setPosition(DUMPER_DOWN);
            aimer.setPosition(AIMER_MIN);
            stopper.setPosition(STOPPER_OPEN);
            filter.setPosition(FILTER_MIN);
            //Set their change rates
            //aimer.setChangeRate(1);
        }
        catch(IllegalArgumentException e){
            telemetry.addData("error", "@Start");
        }
    }

    @Override
    public void loop(){
        joy1.update(gamepad1);
        joy2.update(gamepad2);
        //Map the drive motors to gamepad 1's joysticks
        double leftMotors = joy1.left_stick_y_exponential(DRIVE_MAX);
        double rightMotors = joy1.right_stick_y_exponential(DRIVE_MAX);

        frontLeft.setPower(leftMotors);
        backLeft.setPower(leftMotors);
        frontRight.setPower(rightMotors);
        backRight.setPower(rightMotors);

        //Map the dumper and filter servos to gamepad 1's buttons
        pusher.setPosition(joy1.triggers_exponential(0.50D) + CR_EQUILIBRIUM);
        if(joy1.left_bumper_isReleased(true))
            filter.setPosition(FILTER_MIN);
        else if(joy1.right_bumper_isReleased(true))
            filter.setPosition(FILTER_MAX);

        //Map the secondary motors to gamepad 2's joysticks and buttons
        tapeMeasure.setPower(joy2.right_stick_y_exponential(TAPEMEASURE_MAX));
        lift.setPower(joy2.left_stick_y_exponential(LIFT_MAX));
        intake.setPower(joy2.triggers_exponential(INTAKE_MAX));
        
        //Map the remaining servos to gamepad 2's buttons
        /*
        long timeSinceStart = System.currentTimeMillis() - timeStarted;
        if(gamepad2.right_bumper) {
            if(timeStarted == 0)
                timeStarted = System.currentTimeMillis();
            if(timeSinceStart % 5 < 1) {
                if (timeSinceStart > 2000)
                    aimerPosition -= (aimerPosition % 0.05 == 0.00) ? 0.05 : aimerPosition % 0.05;
                else
                    aimerPosition -= 0.01;
            }
        }
        else if(gamepad2.left_bumper) {
            if(timeStarted == 0)
                timeStarted = System.currentTimeMillis();
            if(timeSinceStart % 5 < 1) {
                if (timeSinceStart > 2000)
                    aimerPosition += 0.05 - aimerPosition % 0.05;
                else
                    aimerPosition += 0.01;
            }
        }
        else
            timeStarted = 0;
        aimerPosition = Range.clip(aimerPosition, AIMER_MIN, AIMER_MAX);
        */
        if(joy2.right_bumper_isReleased(true))
            aimerPosition += 0.025;
        if(joy2.left_bumper_isReleased(true))
            aimerPosition -= 0.025;
        aimerPosition = Range.clip(aimerPosition, AIMER_MIN, AIMER_MAX);
        aimer.setPosition(aimerPosition);

        if(joy2.a_isReleased(true)){
            if(stopperMax)
                stopper.setPosition(STOPPER_CLOSE);
            else
                stopper.setPosition(STOPPER_OPEN);
            stopperMax = !stopperMax;
        }

        if(joy1.a_isReleased(true)){
            if(dumperMax)
                dumper.setPosition(DUMPER_UP);
            else
                dumper.setPosition(DUMPER_DOWN);
            dumperMax = !dumperMax;
        }

        if(gamepad2.x)
            tilter.setPosition(0.75F);
        else if(gamepad2.y)
            tilter.setPosition(0.25F);
        else
            tilter.setPosition(CR_EQUILIBRIUM);

        //Telemetry
        telemetry.addData("drive", String.format("Left: %.2f Right %.2f", leftMotors, rightMotors));
        telemetry.addData("secondary", String.format("Tape: %.2f Lift: %.2f Intake %.2f", tapeMeasure.getPower(), lift.getPower(), intake.getPower()));
        telemetry.addData("servos", String.format("Aimer: %.2f, %.2f Stopper: %.2f Dumper: %.2f Filter: %.2f", aimer.getPosition(), aimer.getActualPosition(), stopper.getPosition(), dumper.getPosition(), filter.getPosition()));
    }

    @Override
    public void stop(){
        tilter.setPosition(CR_EQUILIBRIUM);
        pusher.setPosition(CR_EQUILIBRIUM);
    }
}