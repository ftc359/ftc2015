package com.qualcomm.ftcrobotcontroller.headers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import java.util.List;

class ProgramReader {
    public List <DcMotorData> motorDataList;
    public List <ServoData> servoDataList;


    public ProgramReader(String path){

    }





    public class DcMotorData{
        private String name;
        private DcMotor.Direction direction;
        private double defaultValue;

        public DcMotorData(String name, DcMotor.Direction direction, double defaultValue){
            this.name = name;
            this.direction = direction;
            this.defaultValue = defaultValue;
        }

        public String getName(){
            return this.name;
        }

        public DcMotor.Direction getDirection(){
            return this.direction;
        }

        public double getDefaultValue(){
            return this.defaultValue;
        }
    }

    public class ServoData{
        private String name;
        private Servo.Direction direction;
        private double minPosition;
        private double maxPosition;
        private double startingValue;

        public ServoData(String name, Servo.Direction direction, double minPosition, double maxPosition, double startingValue){
            this.name = name;
            this.direction = direction;
            this.minPosition = minPosition;
            this.maxPosition = maxPosition;
            this.startingValue = startingValue;
        }

        public String getName(){
            return this.name;
        }

        public Servo.Direction getDirection(){
            return this.direction;
        }

        public double getMinPosition(){
            return this.minPosition;
        }

        public double getMaxPosition(){
            return this.maxPosition;
        }

        public double getStartingValue(){
            return this.startingValue;
        }
    }

    public interface Event{
        Gamepad getGamepad();
    }

    public enum EventType{
        JOYSTICK, BUTTON, DPAD
    }

    public enum JoystickEventType{
        EXPONENTIAL, LINEAR, STATIC
    }

    public enum ButtonEventType{
        A, B, X, Y, BUMPER, STICK_BUTTON, TRIGGER
    }

    public enum DpadEventType{
        UP, DOWN, LEFT, RIGHT
    }
}