package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.DcMotor;

public class PIDController {
    private DcMotor motor;

    private double kP, kI, kD;

    public final long sampleRate;
    private long lastTime;

    private long speed = 0; //setpoint

    private volatile boolean on = false;

    //Constructor
    public PIDController(DcMotor motor, double kP, double kI, double kD, long sampleRate) {
        this.motor = motor;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.sampleRate = sampleRate;
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
        this.speed = countsPerSecond;
    }

    public long getSpeed(){
        return this.speed;
    }

    public void turnOn() {
        this.on = true;
        (new Thread(){
            @Override
            public void run() {
                lastTime = System.currentTimeMillis();

                while(on){
                    try{
                        Thread.sleep(getSampleRate());
                    }
                    catch(InterruptedException e){
                        this.interrupt();
                    }
                    synchronized (this) {
                        long period = lastTime - System.currentTimeMillis();
                        lastTime = System.currentTimeMillis();


                    }
                }
            }
        }).start();
    }

    public void turnOff() {
        this.on = false;
    }

    //Main methods
}
