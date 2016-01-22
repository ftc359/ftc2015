package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.concurrent.TimeUnit;

public class PIDController {
    public final DcMotor motor;

    private double kP, kI, kD;

    private final long sampleRate;

    private double speed = 0.0; //setpoint
    private final double maxCountsPerSecond;

    private volatile boolean on = false;

    public long lastTime;
    public long lastEncoderCount;
    public double errorSum;
    public double lastError;

    //Constructor
    public PIDController(DcMotor motor, double kP, double kI, double kD, long sampleRate, double maxCountsPerSecond) {
        this.motor = motor;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.sampleRate = sampleRate;
        this.maxCountsPerSecond = maxCountsPerSecond;
    }

    public PIDController(DcMotor motor, double kP, double kI, double kD, long sampleRate) {
        this(motor, kP, kI, kD, sampleRate, 105 /* rpm */ * 1680 /* ticks per rotation */ / 60.0);
    }

    //Getters and Setters
    public double getP() {
        return kP;
    }

    public void setP(double kP) {
        this.kP = kP;
    }

    public double getI() {
        return kI;
    }

    public void setI(double kI) {
        this.kI = kI;
    }

    public double getD() {
        return kD;
    }

    public void setD(double kD) {
        this.kD = kD;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public boolean isOn() {
        return on;
    }

    public void setSpeed(double speed){
        if(this.speed == speed)
            return;
        this.speed = (speed > 1.0)?1.0:(speed < -1.0)?-1.0:speed;
        this.turnOff();
        this.turnOn();
    }

    public double getSpeed(){
        return this.speed;
    }

    public DcMotor getMotor(){
        return motor;
    }

    public void turnOn() {
        this.on = true;
        getMotor().setPower(speed);

        (new Thread(){
            @Override
            public void run() {
                synchronized (this) {
                    motor.setPower(speed);
                    lastTime = System.currentTimeMillis();
                    lastEncoderCount = motor.getCurrentPosition();
                    lastError = 0;
                    errorSum = 0;
                }

                while(on){
                    while(System.currentTimeMillis() < lastTime + getSampleRate()){}
                    synchronized (this) {
                        //Calculate current speed
                        long period = (System.currentTimeMillis() - lastTime);
                        lastTime = System.currentTimeMillis();
                        double speed = ((motor.getCurrentPosition() - lastEncoderCount) / period)/(maxCountsPerSecond / 1000);
                        lastEncoderCount = motor.getCurrentPosition();

                        //Calculate error
                        double error = getSpeed() - speed;
                        errorSum += error * period;

                        //Calculate PID
                        double proportional = kP * error;
                        double integral = kI * errorSum;
                        double derivative = kD * (error - lastError) / period;
                        lastError = error;
                        double PID = speed + (proportional + integral + derivative);
                        if(Math.abs(PID) > 1.0) PID = Math.signum(PID);
                        motor.setPower(PID);
                    }
                }
            }
        }).start();
    }

    public void turnOff() {
        this.on = false;
    }
}
