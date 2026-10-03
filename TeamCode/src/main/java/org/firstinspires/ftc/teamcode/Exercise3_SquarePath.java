package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * EXERCISE 3: Drive a Square Path
 *
 * Your job: make the robot drive in a square and end up back close to where it started -
 * without ever turning! You'll do this purely by strafing, using driveForTime(...) from
 * Exercise 2.
 *
 * A square has 4 equal sides. Think about which direction the robot needs to move for each
 * side:
 *   Side 1: forward
 *   Side 2: strafe right
 *   Side 3: backward
 *   Side 4: strafe left
 *
 * Since mecanum wheels let the robot strafe, it never has to rotate to trace this whole
 * shape - it stays facing the same direction the entire time.
 */
@Autonomous(name = "Exercise 3 - Square Path", group = "Exercises")
public class Exercise3_SquarePath extends FTC30741Base {

    // Feel free to change this while testing - it controls how long (and therefore how far)
    // each side of the square is.
    private static final double SECONDS_PER_SIDE = 1.0;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);

        telemetry.addLine("Exercise 3 ready - press START");
        telemetry.update();
        waitForStart();

        // COACH NOTE: all four sides below had 0.5 hard-coded in instead of
        // SECONDS_PER_SIDE, so the constant you declared above was never actually used.
        // The square still closes (all 4 sides were equally 0.5s, and the directions are
        // all correct), but now that it uses SECONDS_PER_SIDE, you can make the square
        // bigger or smaller by changing one number up top instead of four numbers below.

        // TODO 1: Side 1 - drive forward for SECONDS_PER_SIDE.
        driveForTime(1, 0, 0, SECONDS_PER_SIDE);

        // TODO 2: Side 2 - strafe right for SECONDS_PER_SIDE.
        driveForTime(0, 1, 0, SECONDS_PER_SIDE);

        // TODO 3: Side 3 - drive backward for SECONDS_PER_SIDE.
        driveForTime(-1, 0, 0, SECONDS_PER_SIDE);

        // TODO 4: Side 4 - strafe left for SECONDS_PER_SIDE.
        driveForTime(0, -1, 0, SECONDS_PER_SIDE);

        telemetry.addLine("Exercise 3 complete! Did the robot end up back near start?");
        telemetry.update();
    }
}
//Negative number = left
