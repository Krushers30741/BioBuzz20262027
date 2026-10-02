package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

/**
 * EXERCISE 8 (CAPSTONE): Align and Get Ready to Shoot
 *
 * Your job: combine Exercise 6 (turning to face the tag) and Exercise 7 (driving to a set
 * distance) into ONE routine that does both AT THE SAME TIME, in a single loop - so the robot
 * ends up both facing the tag AND at the right distance from it. This is the actual skill the
 * team needs: getting the robot lined up on a target before it shoots.
 *
 * Note: this exercise stops once the robot is aimed and at distance, and prints "READY TO
 * SHOOT" to telemetry - it doesn't actually fire anything, since the shooter mechanism is a
 * separate subsystem the team hasn't wired up yet. Once that exists, firing would just be one
 * more step added after this loop finishes.
 *
 * Hint: you already wrote the turn-correction logic in Exercise 6 and the drive-correction
 * logic in Exercise 7. This exercise is mostly about combining both into the same
 * driveRobotCentric(...) call each time through the loop, instead of picking just one.
 */
@Autonomous(name = "Exercise 8 - Align and Ready", group = "Exercises")
public class Exercise8_AlignAndReady extends FTC30741Base {

    private static final double BEARING_TOLERANCE_DEGREES = 3.0;
    private static final double TARGET_RANGE_INCHES = 24.0;
    private static final double RANGE_TOLERANCE_INCHES = 1.0;
    private static final double TURN_POWER = 0.2;
    private static final double DRIVE_POWER = 0.2;
    private static double turnPower = 0.0;
    private static double forwardPower = 0.0;
    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);
        initVision();

        telemetry.addLine("Exercise 8 ready - press START");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 8.0;
        AprilTagDetection detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

        // TODO 1: Figure out the loop condition. You want to keep correcting until BOTH the
        //   bearing AND the range are within tolerance at the same time (or you time out, or
        //   the OpMode stops). Re-use the conditions from Exercise 6 and Exercise 7 - just
        //   combine them with && / || as needed.
        while (
                ((detection.ftcPose.range==TARGET_RANGE_INCHES)
                        &&(BEARING_TOLERANCE_DEGREES==detection.ftcPose.bearing))||giveUpTime<30) {

            detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

            if (detection != null) {
                // TODO 2: Compute a turn power AND a forward power this time, the same way you
                //   did in Exercises 6 and 7 - then pass BOTH into ONE driveRobotCentric call.
                //   Hint: driveRobotCentric(forwardPower, 0, turnPower)
                double forward= Math.signum
                        (detection.ftcPose.range - TARGET_RANGE_INCHES) * DRIVE_POWER;
                drive.driveRobotCentric(forward, 0, 0);
            } else {
                drive.driveRobotCentric(0, 0, 0);
            }

            telemetry.addData("tag visible", detection != null);
            if (detection != null) {
                telemetry.addData("bearing", detection.ftcPose.bearing);
                telemetry.addData("range (in)", detection.ftcPose.range);
            }
            telemetry.update();
        }

        // TODO 3: Stop the robot.

        telemetry.addLine("READY TO SHOOT");
        telemetry.update();
    }
}
