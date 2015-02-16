package ca.team4976.sub;
import ca.team4976.io.Controller;
import ca.team4976.io.Input;
import ca.team4976.io.Output;

public class GripperV2 {
    //Determines if the solenoids are extended
    public boolean gripperExtended, kickerExtended;

    double leftTrigger, rightTrigger, gripperMotorSpeed;
    boolean leftBumper, rightBumper;

    boolean secondaryControllerActive;

    boolean containerIsReady;

    boolean xMode, aMode, doSwitch;


    /**
     * Initializes the gripper subsystem, called in robotInit();
     */
    public GripperV2() {
        gripperExtended = false;
        kickerExtended = false;
        containerIsReady = false;

        leftTrigger = 0.0;
        rightTrigger = 0.0;
        leftBumper = false;
        rightBumper = false;

        gripperMotorSpeed = 0.0;
        xMode = false;
        aMode = false;
        doSwitch = false;
    }

    public void update(Elevator elevator) {

        containerIsReady = Input.Digital.GRIPPER_LASER.get();

        //Update triggers and bumpers for the second controller
        leftTrigger = Controller.Secondary.Trigger.LEFT.value();
        rightTrigger = Controller.Secondary.Trigger.RIGHT.value();
        leftBumper = Controller.Secondary.Button.LEFT_BUMPER.isDown();
        rightBumper = Controller.Secondary.Button.RIGHT_BUMPER.isDown();

        //If the Start button is down on either remote, reset the gripper
        if (Controller.Primary.Button.START.isDownOnce() || Controller.Secondary.Button.START.isDownOnce()) {
            resetGripper();
        }
        if (Controller.Primary.Button.A.isDownOnce()){
            secondaryControllerActive = false;

            if (aMode) {
                aMode = false;
                xMode = false;
                gripperExtended = false;
                kickerExtended = false;
                gripperMotorSpeed = 0.0;
                doSwitch = true;
            }
            else {
                aMode = true;
                xMode = false;
                gripperExtended = true;
                kickerExtended = true;
                gripperMotorSpeed = 1.0;
                doSwitch = true;

            }

        }
        if (Controller.Primary.Button.X.isDownOnce()){
            secondaryControllerActive = false;

            if (xMode) {
                aMode = false;
                xMode = false;
                gripperExtended = false;
                kickerExtended = false;
                gripperMotorSpeed = 0.0;
                doSwitch = true;

            }
            else {
                aMode = false;
                xMode = true;
                gripperExtended = true;
                kickerExtended = false;
                gripperMotorSpeed = 1.0;
                doSwitch = true;

            }
        }
        if (Controller.Primary.Button.Y.isDownOnce()){
            secondaryControllerActive = false;
            if (gripperMotorSpeed != 1.0) {
                gripperMotorSpeed = 1.0;
            }
            else {
                gripperMotorSpeed = 0;
            }
        }
        if (Controller.Primary.Button.B.isDownOnce()){
            secondaryControllerActive = false;
            if (gripperMotorSpeed != -1.0) {
                gripperMotorSpeed = -1.0;
            }
            else {
                gripperMotorSpeed = 0;
            }
        }


////////////////////////////////////////////////////////////////////////////////
        // Secondary Controls
        ////////////////////////////////////////////////////////////////////////////////

        if (leftTrigger > 0){
            secondaryControllerActive = true;
            Output.Motor.GRIPPER_LEFT.set(leftTrigger * -1);
        }
        else if (leftBumper) {
            secondaryControllerActive = true;
            Output.Motor.GRIPPER_LEFT.set(1.0);
        }
        else if (secondaryControllerActive) {
            Output.Motor.GRIPPER_LEFT.set(0);
        }

        if (rightTrigger > 0){
            secondaryControllerActive = true;
            Output.Motor.GRIPPER_RIGHT.set(rightTrigger);
        }
        else if (rightBumper) {
            secondaryControllerActive = true;
            Output.Motor.GRIPPER_RIGHT.set(-1.0);
        }
        else if (secondaryControllerActive) {
            Output.Motor.GRIPPER_RIGHT.set(0);
        }

        if (Controller.Secondary.Button.X.isDownOnce()) {
            secondaryControllerActive = true;
            gripperExtended = !gripperExtended;
        }
        else if (Controller.Secondary.Button.A.isDownOnce()) {
            secondaryControllerActive = true;
            kickerExtended = !kickerExtended;
        }

        // If the elevator is in the process of lifting the container
        // out of the gripper, reset the gripper
        if (elevator.getCurrentLevel() <= 1 && elevator.getDesiredLevel() >= 1 && containerIsReady)
            resetGripper();

        if (!secondaryControllerActive) {
            //System.out.println("Primary controller active.");
            if (!containerIsReady) {
                //System.out.println("Container is not ready");
                if (xMode) {
                    Output.Motor.GRIPPER_LEFT.set(gripperMotorSpeed);
                    Output.Motor.GRIPPER_RIGHT.set(-gripperMotorSpeed);
                    System.out.println("Gripper (X): " + gripperExtended);
                    Output.PneumaticSolenoid.GRIPPER_PNEUMATIC.set(gripperExtended);
                    
                    if (elevator.getCurrentLevel() >= 1 && doSwitch) {
                        System.out.println("Kicker (X): " + kickerExtended);
                        Output.PneumaticSolenoid.GRIPPER_KICKER.set(false);
                        elevator.elevatorToLevel(0);
                        doSwitch = false;
                    }
                    else if (doSwitch) {
                        elevator.elevatorToLevel(1);
                        
                    }
                } else if (aMode) {
                    Output.Motor.GRIPPER_LEFT.set(gripperMotorSpeed);
                    Output.Motor.GRIPPER_RIGHT.set(-gripperMotorSpeed);
                    System.out.println("Gripper (A): " + gripperExtended);
                    Output.PneumaticSolenoid.GRIPPER_PNEUMATIC.set(gripperExtended);
                    if (elevator.getCurrentLevel() >= 1 && doSwitch) {
                        System.out.println("Kicker (A): " + kickerExtended);
                        Output.PneumaticSolenoid.GRIPPER_KICKER.set(true);
                        elevator.elevatorToLevel(0);
                        doSwitch = false;
                    }
                    else if (doSwitch) {
                        elevator.elevatorToLevel(1);
                        
                    }
                } else {
                    Output.Motor.GRIPPER_LEFT.set(0);
                    Output.Motor.GRIPPER_RIGHT.set(0);
                    Output.PneumaticSolenoid.GRIPPER_PNEUMATIC.set(false);
                    if (elevator.getCurrentLevel() >= 1 && doSwitch) {
                        Output.PneumaticSolenoid.GRIPPER_KICKER.set(false);
                        elevator.elevatorToLevel(0);
                        doSwitch = false;
                    }
                    else if (doSwitch) {
                        elevator.elevatorToLevel(1);
                        
                    }
                }
            }
            else {
                Output.Motor.GRIPPER_LEFT.set(0);
                Output.Motor.GRIPPER_RIGHT.set(0);
            }
        }
        else {
            Output.PneumaticSolenoid.GRIPPER_PNEUMATIC.set(gripperExtended);
            Output.PneumaticSolenoid.GRIPPER_KICKER.set(kickerExtended);
        }
    }

    /**
     * Resets the gripper
     */
    public void resetGripper(){
        gripperExtended = false;
        kickerExtended = false;
        secondaryControllerActive = false;
        containerIsReady = false;
        aMode = false;
        xMode = false;
        gripperMotorSpeed = 0.0;
    }
}