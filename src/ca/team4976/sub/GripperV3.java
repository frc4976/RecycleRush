package ca.team4976.sub;
import ca.team4976.io.Controller;
import ca.team4976.io.Input;
import ca.team4976.io.Output;

public class GripperV3 {

    boolean xState, aState;

    boolean leftBumper, rightBumper;
    double  leftTrigger, rightTrigger;

    boolean gripperState, kickerState;

    public GripperV3(){
        xState = false;
        aState = false;

        gripperState = false;
        kickerState = false;


    }

    public void update(Elevator elevator){
        leftBumper = Controller.Secondary.Button.LEFT_BUMPER.isDown();
        rightBumper = Controller.Secondary.Button.RIGHT_BUMPER.isDown();
        leftTrigger = Controller.Secondary.Trigger.LEFT.value();
        rightTrigger = Controller.Secondary.Trigger.RIGHT.value();
        //Primary Controls
        if(Controller.Primary.Button.X.isDownOnce()){
            //pickup an upright container
            xState = !xState;
            System.out.println("X state is " + xState);
            gripperState = !gripperState;
            kickerState = false;
            pickupContainer(elevator, xState, gripperState, kickerState);
        }
        else if(Controller.Primary.Button.A.isDownOnce()){
            //pick up a fallen over container
            aState = !aState;
            gripperState = !gripperState;
            kickerState = !kickerState;
            pickupContainer(elevator, aState, kickerState, gripperState);
        }
        //Secondary Controls
        if(leftTrigger != 0){
            gripperLeftMotors(leftTrigger * - 1);
        }
        else if (leftBumper){
            gripperLeftMotors(1.0);
        }
        else {
            gripperLeftMotors(0);
        }
        if(rightTrigger != 0){
            gripperRightMotors(rightTrigger);
        }
        else if(rightBumper){
            gripperRightMotors(-1.0);
        }
        else{
            gripperRightMotors(0.0);
        }
    }
    //Reset function called in main when START button is pressed
    public void reset(Elevator elevator){
        gripperMotors(0.0, 0.0);
        toggleGripper(false);
        if(elevator.withinThreshold(1)){
            toggleKicker(false);
        }
        else{
            elevator.elevatorToLevel(1);
            if(elevator.withinThreshold(1)){
                toggleKicker(false);
                elevator.elevatorToLevel(0);
            }
        }
    }
    //Simplifies output to the gripper pneumatic
    private void toggleGripper(boolean state){
        Output.PneumaticSolenoid.GRIPPER_PNEUMATIC.set(state);
    }
    //Simplifies output to the kicker pneumatic
    private void toggleKicker(boolean state){
        Output.PneumaticSolenoid.GRIPPER_KICKER.set(state);
    }
    //Simplifies output to the left Gripper Motor
    private void gripperLeftMotors(double speed){
        Output.Motor.GRIPPER_LEFT.set(speed);
    }
    //Simplifies output to the Right Gripper motor
    private void gripperRightMotors(double speed){
        Output.Motor.GRIPPER_RIGHT.set(speed);
    }
    //Sets speed value to both motors simultaneously
    private void gripperMotors(double speedLeft,double speedRight){
        System.out.println("Running the gripper left motor at " + speedLeft + " Running the gripper right motor at " + speedRight);
        Output.Motor.GRIPPER_LEFT.set(speedLeft);
        Output.Motor.GRIPPER_RIGHT.set(speedRight);
    }
    //Picks up a container
    private void pickupContainer(Elevator elevator, boolean state, boolean iKickerState, boolean iGripperState) {
        System.out.println("Picking up container");
        System.out.println("The state is " + state);
        System.out.println("The gripper state is " + iGripperState);
        System.out.println("The kicker state is " + iKickerState);
        if (state) {
            toggleGripper(iGripperState);
            gripperMotors(1.0, -1.0);
            if (elevator.withinThreshold(1)) {

                System.out.println(kickerState);
                toggleKicker(iKickerState);
                if(Input.Digital.GRIPPER_LASER.get()){
                    gripperMotors(0.0,0.0);
                }
            } else {
                elevator.elevatorToLevel(1);
                if (elevator.withinThreshold(1)) {

                    toggleKicker(iKickerState);
                    elevator.elevatorToLevel(0);
                    if(Input.Digital.GRIPPER_LASER.get()){
                        gripperMotors(0.0,0.0);
                    }
                }
            }
        } else {
            System.out.println("resetting gripper");
            reset(elevator);
        }
    }
}
