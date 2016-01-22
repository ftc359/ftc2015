package com.qualcomm.ftcrobotcontroller.headers;

public class Timer {
    private long timeStarted = 0L;

    public Timer(){
        timeStarted = System.currentTimeMillis();
    }

    public long getMillisPassed(){
        return timeStarted - System.currentTimeMillis();
    }

    public long getSecondsPassed(){
        return (timeStarted - System.currentTimeMillis()) / 1000L;
    }

    public void resetTimer(){
        timeStarted = System.currentTimeMillis();
    }

    public boolean millisPassed(long millis){
        return getMillisPassed() >= millis;
    }

    public boolean secondsPassed(long seconds){
        return getSecondsPassed() >= seconds;
    }
}
