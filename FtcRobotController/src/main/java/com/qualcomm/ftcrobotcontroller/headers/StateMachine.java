package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.DcMotor;

public class StateMachine {
    private DcMotor[] motorArray;
    private String[] eventList;
    private State[] eventState;

    /**
     * Constructor for the StateMachine class that stores a list of event names and initializes the rest of the class to work properly.
     *
     * @param events a list or array of identifiers for the events
     */
    public StateMachine(String... events){
        this.eventList = new String[events.length];
        this.eventState = new State[events.length];
        System.arraycopy(events, 0, this.eventList, 0, events.length);
        for(int i = 0; i < events.length; i++) eventState[i] = State.INACTIVE;
    }

    /**
     *
     * @param event the event identifier
     * @return the array index of the specified event.
     * @throws IllegalArgumentException
     */
    private int eventIndex(String event) throws IllegalArgumentException{
        for(int i = 0; i < this.eventList.length; i++)
            if(this.eventList[i].equals(event)) return i;
        throw new IllegalArgumentException("No such event: " + event);
    }

    /**
     *
     * @param event the event identifier
     * @return the {@link com.qualcomm.ftcrobotcontroller.headers.StateMachine.State} of the specified event
     */
    private State getState(String event){
        return this.eventState[eventIndex(event)];
    }

    /**
     * Sets the event to run next loop. Must be called for runEvent to work.
     *
     * @param event the event identifier
     */
    public void queueEvent(String event){
        this.eventState[eventIndex(event)] = State.STANDBY;
    }

    /**
     * Put this as the condition to an if function to start the code inside (only executes once).
     *
     * @param event the event identifier
     * @return if the event was queued previously
     */
    public boolean runEvent(String event){
        if(getState(event) == State.STANDBY) {
            this.eventState[eventIndex(event)] = State.EXECUTING;
            return true;
        }
        return false;
    }

    /**
     * Sets the event to the finished state
     *
     * @param event the event identifier
     * @return if the event was running previously
     */
    public boolean finishEvent(String event){
        if(getState(event) == State.EXECUTING) {
            this.eventState[eventIndex(event)] = State.FINISHED;
            return true;
        }
        return false;
    }

    /**
     *
     * @param event the event identifier
     * @return if the event's state is inactive
     */
    public boolean eventInactive(String event){
        return (getState(event) == State.INACTIVE);
    }

    /**
     *
     * @param event the event identifier
     * @return if the event has been queued
     */
    public boolean eventActive(String event){
        return (getState(event) == State.STANDBY);
    }

    /**
     *
     * @param event the event identifier
     * @return if the event is running
     */
    public boolean eventRunning(String event){
        return (getState(event) == State.EXECUTING);
    }

    /**
     *
     * @param event the event identifier
     * @return if the event has finished running
     */
    public boolean eventFinished(String event){
        return (getState(event) == State.FINISHED);
    }

    public void setDriveMotors(DcMotor... motors) throws IllegalArgumentException{
        if(motors.length > 8) throw new IllegalArgumentException("Too many motors");
        if(motors.length%2 == 1) throw new IllegalArgumentException("Cannot have an odd number of drive motors");
        this.motorArray = new DcMotor[motors.length];
        System.arraycopy(motors, 0, this.motorArray, 0, motors.length);
    }

    public void move(String event, double power, double distance, direction dir){
        if(eventInactive(event)){
            for (DcMotor motor : this.motorArray)
                motor.setPower(0);
            Encoders.resetEncoders(this.motorArray);
        }
        if(eventActive(event) && Encoders.encodersReset(this.motorArray)){
            Encoders.runEncoders(this.motorArray);
            queueEvent(event);
        }
        if(runEvent(event))
            for (int i = 0; i < this.motorArray.length; i++)
                this.motorArray[i].setPower(power * (((dir.getValue() & (i + 1) % 2) != 0) ? -1 : 1));
        if(eventRunning(event) && Encoders.positionReached(distance, this.motorArray)){
            for (DcMotor motor : this.motorArray)
                motor.setPower(0);
            finishEvent(event);
        }
    }

    public boolean runToPosition(String event, double power, double position, double uncertainty, DcMotor... motors){
        if(eventInactive(event)){
            for (DcMotor motor : motors)
                motor.setPower(0);
            Encoders.resetEncoders(motors);
        }
        if(eventActive(event) && Encoders.encodersReset(motors)){
            Encoders.runEncoders(motors);
            queueEvent(event);
        }
        if(runEvent(event))
            for (DcMotor motor : motors)
                motor.setPower(power);
        if(eventRunning(event) && Encoders.positionReached(position, motors)){
            for (DcMotor motor : motors)
                motor.setPower(0);
            finishEvent(event);
            return true;
        }

        return false;
    }

    private enum State{
        INACTIVE,
        STANDBY,
        EXECUTING,
        FINISHED
    }

    public enum direction{
        FWD(0), LEFT(1), RIGHT(2), BWD(3);
        private final int value;
        private direction(final int newValue){
            value = newValue;
        }
        public int getValue(){
            return value;
        }
    }
}