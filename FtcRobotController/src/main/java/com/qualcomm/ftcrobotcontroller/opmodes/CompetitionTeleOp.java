package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import com.qualcomm.ftcrobotcontroller.headers.ServoV2;
import com.qualcomm.ftcrobotcontroller.headers.GamepadV2;
import com.qualcomm.ftcrobotcontroller.headers.Joystick;
import com.qualcomm.ftcrobotcontroller.headers.Timer;
/*
public class CompetitionTeleOp extends OpMode{
    DcMotor frontLeft, frontRight, backLeft, backRight, tapeMeasure, hooks; //Initialize DcMotor objects
    Servo climbers, stopper; //Initialize Servo objects

    //Values to be carried between functions
    //int G1ButtonArray = 0; //Bit array used to work in sync with isReleased to determine if a button is being held
    double stopperPosition = 0.16;
    long timeStarted = 0;

    //Constants
    final static double THRESHOLD = 0.10;
    final static double DRIVEMOTORS_MAX = 1.00;
    final static double HOOKS_MAX = 0.40;
    final static double TAPEMEASURE_MAX = 1.00;

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
        stopper.setPosition(0.75);
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
            if(timeSinceStart % 5 < 1) {
                if (timeSinceStart > 2000)
                    stopperPosition -= (stopperPosition % 0.05 == 0.00) ? 0.05 : stopperPosition % 0.05;
                else
                    stopperPosition -= 0.01;
            }
        }
        else if(gamepad2.left_bumper) {
            if(timeStarted == 0)
                timeStarted = SystemClock.elapsedRealtime();
            if(timeSinceStart % 5 < 1) {
                if (timeSinceStart > 2000)
                    stopperPosition += 0.05 - stopperPosition % 0.05;
                else
                    stopperPosition += 0.01;
            }
        }
        else
            timeStarted = 0;
        stopperPosition = Range.clip(stopperPosition, 0.16D, 0.95D);
        stopper.setPosition(stopperPosition);
        

        //Telemetry
        telemetry.addData("driveMotors", String.format("Left Side: %1.2f\tRight Side: %1.2f", leftMotors, rightMotors));
        telemetry.addData("hooks", String.format("Hooks: %1.2f", hooks.getPower()));
        telemetry.addData("tapeMeasure", String.format("Tape Measure: %1.2f", tapeMeasure.getPower()));
        telemetry.addData("servos", String.format("Climbers: %1.2f\tStopper: %1.2f", climbers.getPosition(), stopper.getPosition()));
    }
}
*/

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
    AIMER_MIN = 0.00D, AIMER_MAX = 1.00D, AIMER_INIT = 0.50D,
    STOPPER_MIN = 0.00D, STOPPER_MAX = 1.00D, STOPPER_INIT = 0.50D,
    FILTER_MIN = 0.00D, FILTER_MAX = 1.00D, FILTER_INIT = 0.50D;

    //Misc
    private long timeStarted = 0;
    private double aimerPosition = AIMER_INIT;
    
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

    /*@Override
    public void start(){
        try {
            //Initialize servos to their starting values
            //All CR Servos should start at equilibrium so that they don't move
            dumper.setPosition(CR_EQUILIBRIUM);
            tilter.setPosition(CR_EQUILIBRIUM);
            pusher.setPosition(CR_EQUILIBRIUM);

            //All 180 Servos should start at preset values
            aimer.setPosition(AIMER_INIT);
            stopper.setPosition(STOPPER_INIT);
            filter.setPosition(FILTER_INIT);
            //Set their change rates
            aimer.setChangeRate(1);
        }
        catch(IllegalArgumentException e){
            telemetry.addData("error", "@Start");
        }
    }*/

    boolean released1 = false, released2 = false;

    @Override
    public void loop(){
        joy1.update(gamepad1);
        joy2.update(gamepad2);
        //Map the drive motors to gamepad 1's joysticks
        double leftMotors = Joystick.exponential(DRIVE_MAX, gamepad1.left_stick_y, THRESHOLD);
        double rightMotors = Joystick.exponential(DRIVE_MAX, gamepad1.right_stick_y, THRESHOLD);

        frontLeft.setPower(leftMotors);
        backLeft.setPower(leftMotors);
        frontRight.setPower(rightMotors);
        backRight.setPower(rightMotors);

        //Map the dumper and filter servos to gamepad 1's buttons
        double dumperPower = (gamepad1.left_trigger > gamepad1.right_trigger)? -gamepad1.left_trigger : gamepad1.right_trigger;
        dumper.setPosition(Joystick.exponential(1.00D, CR_EQUILIBRIUM + dumperPower / 2, THRESHOLD));
        if(gamepad1.left_bumper)
            filter.setPosition(FILTER_MIN);
        else if(gamepad1.right_bumper)
            filter.setPosition(FILTER_MAX);

        //Map the secondary motors to gamepad 2's joysticks and buttons
        tapeMeasure.setPower(Joystick.exponential(TAPEMEASURE_MAX, gamepad2.right_stick_y, THRESHOLD));
        lift.setPower(Joystick.exponential(LIFT_MAX, gamepad2.left_stick_y, THRESHOLD));
        double intakePower = (gamepad2.left_trigger > gamepad2.right_trigger)? -gamepad2.left_trigger : gamepad2.right_trigger;
        intake.setPower(Joystick.exponential(INTAKE_MAX, intakePower, THRESHOLD));
        
        //Map the remaining servos to gamepad 2's buttons
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
        stopper.setPosition(aimerPosition);
        
        if(gamepad2.a && stopper.getPosition() == STOPPER_MIN)
            stopper.setPosition(STOPPER_MAX);
        else if(gamepad2.a && stopper.getPosition() == STOPPER_MAX)
            stopper.setPosition(STOPPER_MIN);

        if(gamepad1.x)
            pusher.setPosition(1.00F);
        else if(gamepad1.y)
            pusher.setPosition(0.00F);
        else
            pusher.setPosition(CR_EQUILIBRIUM);

        if(gamepad2.x)
            tilter.setPosition(0.75F);
        else if(gamepad2.y)
            tilter.setPosition(0.25F);
        else
            tilter.setPosition(CR_EQUILIBRIUM);

        if(joy1.a_isReleased(true))
            released1 = true;
        if(joy1.a_isReleased(false))
            released2 = true;

        //Telemetry
        telemetry.addData("drive", String.format("Left: %.2f Right %.2f", leftMotors, rightMotors));
        telemetry.addData("secondary", String.format("Tape: %.2f Lift: %.2f Intake %.2f", tapeMeasure.getPower(), lift.getPower(), intake.getPower()));
        telemetry.addData("aimer", aimer.getActualPosition());
        telemetry.addData("test", joy1.toString());
        telemetry.addData("test1", released1 + " " + released2 + " " + Joystick.button.a.getValue());
    }

    @Override
    public void stop(){
        dumper.setPosition(CR_EQUILIBRIUM);
        tilter.setPosition(CR_EQUILIBRIUM);
        pusher.setPosition(CR_EQUILIBRIUM);
    }
}