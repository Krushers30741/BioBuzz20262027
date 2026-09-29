package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * EXERCISE 1: Drive Forward and Stop
 *
 * Your job: make the robot drive straight forward for exactly 2 seconds, then stop.
 *
 * This is the simplest possible autonomous routine, but it teaches the most important idea
 * in autonomous programming: since there's no driver holding a controller, YOUR CODE has to
 * decide when to start and stop moving. Here, we'll use a timer.
 */
@Autonomous(name = "Exercise 1 - Drive Forward", group = "Exercises")
public class Exercise1_DriveForward extends FTC30741Base {

    @Override
    public void runOpMode() throws InterruptedException {
        // This sets up the drivetrain (same as TeleopDrive does).
        initOpMode(true, false);

        telemetry.addLine("Exercise 1 ready - press START");
        telemetry.update();
        waitForStart();

        // TODO 1: Figure out what time it will be when we should stop driving.
        //   Hint: now() tells you the current time in seconds since the OpMode started.
        //   We want to stop 2.0 seconds from right now.
        double stopTime = 0; // <-- replace 0 with the correct expression

        // TODO 2: Fill in the while loop's condition so it keeps looping UNTIL stopTime.
        //   Hint: it should keep looping as long as the OpMode is still active AND
        //   we haven't reached stopTime yet.
        while (/* TODO: your condition here */ false) {

            // TODO 3: Command the robot to drive straight forward.
            //   drive.driveRobotCentric(forward, strafe, turn) - what values make it go
            //   straight forward at half power, with no strafing and no turning?
            drive.driveRobotCentric(0, 0, 0); // <-- change these numbers
        }

        // TODO 4: Now that time is up, make sure the robot actually stops!
        //   What driveRobotCentric(...) call means "don't move at all"?

        telemetry.addLine("Exercise 1 complete!");
        telemetry.update();
    }
}
