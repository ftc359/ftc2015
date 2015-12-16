package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.concurrent.TimeUnit;

public class PIDController {
    private final DcMotor motor;

    private double kP, kI, kD;

    private final long sampleRate;

    private double speed = 0.0; //setpoint
    private final long MAX_COUNTS_PER_MINUTE;

    private volatile boolean on = false;

    private long lastTime;
    private long lastEncoderCount;
    private double errorSum;
    private double lastError;

    //Constructor
    public PIDController(DcMotor motor, double kP, double kI, double kD, long sampleRate, long MAX_COUNTS_PER_MINUTE) {
        this.motor = motor;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.sampleRate = sampleRate;
        this.MAX_COUNTS_PER_MINUTE = MAX_COUNTS_PER_MINUTE;
    }

    public PIDController(DcMotor motor, double kP, double kI, double kD, long sampleRate) {
        this(motor, kP, kI, kD, sampleRate, 150 /* rpm */ * 1120 /* ticks per rotation */);
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

    public void setSpeed(long countsPerSecond){
        this.speed = (speed > 1.0)?1.0:(speed < -1.0)?-1.0:countsPerSecond;
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
                lastTime = System.currentTimeMillis();
                lastEncoderCount = motor.getCurrentPosition();
                lastError = 0;
                errorSum = 0;

                while(on){
                    try{
                        Thread.sleep(getSampleRate());
                    }
                    catch(InterruptedException e){
                        this.interrupt();
                    }
                    synchronized (this) {
                        //Calculate current speed
                        double period = TimeUnit.MILLISECONDS.toSeconds(lastTime - System.currentTimeMillis())/60.0;
                        lastTime = System.currentTimeMillis();
                        double speed = ((lastEncoderCount-motor.getCurrentPosition())/period)/MAX_COUNTS_PER_MINUTE;
                        lastEncoderCount = motor.getCurrentPosition();

                        //Calculate error
                        double error = getSpeed() - speed;
                        errorSum += error;

                        //Calculate PID
                        double proportional = kP * error / TimeUnit.MILLISECONDS.toSeconds(sampleRate)/60.0;
                        double integral = kI * errorSum / TimeUnit.MILLISECONDS.toSeconds(sampleRate)/60.0;
                        double derivative = kD * (error - lastError) / TimeUnit.MILLISECONDS.toSeconds(sampleRate)/60.0;
                        lastError = error;

                        motor.setPower(proportional + integral + derivative);
                    }
                }
            }
        }).start();
    }

    public void turnOff() {
        this.on = false;
    }
}
