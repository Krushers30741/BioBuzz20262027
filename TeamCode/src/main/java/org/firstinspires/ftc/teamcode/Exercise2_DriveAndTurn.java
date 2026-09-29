package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * EXERCISE 2: Drive Forward, Then Turn
 *
 * Your job: make the robot drive forward for 1.5 seconds, then turn in place for 1 second,
 * then stop.
 *
 * You could write this the same way as Exercise 1 (a while loop with a timer), but that gets
 * repetitive fast once you need several movements in a row. Instead, the team's FTC30741Base
 * class now has a helper method built for exactly this:
 *
 *     driveForTime(forward, strafe, turn, seconds)
 *
 * It drives with the values you give it for the given number of seconds, then automatically
 * stops before returning control to your code. That means a whole autonomous routine can just
 * be a list of calls to driveForTime(...), one per movement - no manual timers needed.
 */
@Autonomous(name = "Exercise 2 - Drive and Turn", group = "Exercises")
public class Exercise2_DriveAndTurn extends FTC30741Base {

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);

        telemetry.addLine("Exercise 2 ready - press START");
        telemetry.update();
        waitForStart();

        // TODO 1: Drive straight forward for 1.5 seconds.
        //   Fill in driveForTime(forward, strafe, turn, seconds) with the right values.
        driveForTime(1, 0, 0, 1.5);

        // TODO 2: Turn in place for 1 second.
        //   (No forward or strafe motion this time - only turn should be nonzero.)
        driveForTime(0, 0, 1, 1);

        telemetry.addLine("Exercise 2 complete!");
        telemetry.update();
    }
}
