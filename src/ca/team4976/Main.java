package ca.team4976;

import ca.team4976.io.Input;
import ca.team4976.io.Output;
import ca.team4976.sub.DriveTrain;
import ca.team4976.sub.Elevator;
import ca.team4976.sub.Gripper;
import ca.team4976.sub.Rake;
import edu.wpi.first.wpilibj.*;

public class Main extends IterativeRobot {

    Rake rake;
    Elevator elevator;
    Gripper gripper;
    DriveTrain drive;

    public void robotInit() {
        rake = new Rake();
        elevator = new Elevator();
        gripper = new Gripper();
        drive = new DriveTrain();
    }

    public void teleopInit() {

    }

    public void autonomousInit() {

    }

    public void teleopPeriodic() {
        rake.update();
        int[] values = elevator.update();
        gripper.update(values);
        drive.teleopArcadeDrive();
    }

    public void autonomousPeriodic() {

    }

}
