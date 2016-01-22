package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

public class ServoV2{
    private final Servo servo;
    protected double position;
    protected int changeRate = 0;
    protected ServoUpdater updater = null;
    public final static double MIN_POSITION = Servo.MIN_POSITION;
    public final static double MAX_POSITION = Servo.MAX_POSITION;

    public ServoV2(Servo servo){
        this.servo = servo;
    }

    public ServoV2(ServoController controller, int portNumber) {
        this(controller, portNumber, Servo.Direction.FORWARD);
    }

    public ServoV2(ServoController controller, int portNumber, Servo.Direction direction) {
        this.servo = new Servo(controller, portNumber, direction);
    }

    public String getDeviceName() {
        return "Servo";
    }

    public String getConnectionInfo() {
        return servo.getConnectionInfo();
    }

    public int getVersion() {
        return 2;
    }

    public void close() {
    }

    public ServoController getController() {
        return servo.getController();
    }

    public void setDirection(Servo.Direction direction) {
        servo.setDirection(direction);
    }

    public Servo.Direction getDirection() {
        return servo.getDirection();
    }

    public int getPortNumber() {
        return servo.getPortNumber();
    }

    public void setPosition(double position) {
        this.position = Range.clip(position, 0.00D, 1.00D);
        if(changeRate == 0)
            servo.setPosition(this.position);
    }

    public double getPosition(){
        return position;
    }

    public double getActualPosition() {
        return servo.getPosition();
    }

    public void scaleRange(double min, double max) throws IllegalArgumentException {
        servo.scaleRange(min, max);
    }

    public void setChangeRate(int changeRate){
        if(changeRate == 0) {
            updater.on = false;
        }
        else if(this.changeRate == 0){
            updater = new ServoUpdater();
            updater.start();
        }
        this.changeRate = changeRate;
    }

    public int getChangeRate(){
        return changeRate;
    }

    private class ServoUpdater extends Thread{
        public boolean on = true;

        public void run() {
            while (on) {
                while(Math.abs(position - servo.getPosition()) > 0) {
                    servo.setPosition(Range.clip(servo.getPosition() + (Math.signum(changeRate) / 100D), MIN_POSITION, MAX_POSITION));
                    try {
                        this.wait(Math.abs(changeRate * 10L));
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
