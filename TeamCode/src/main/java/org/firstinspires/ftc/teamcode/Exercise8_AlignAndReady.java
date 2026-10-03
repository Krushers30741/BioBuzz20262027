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
    // COACH FIX: removed two unused fields (turnPower, forwardPower) that were declared
    // here but never read - the real turn/forward values are computed as local variables
    // inside the loop below, same as Exercises 6 and 7 do.

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
        //
        // COACH FIX: this condition had the three bugs below - this is exactly the "common
        // mistake" called out in the coach guide, so it's worth walking through:
        //  1) It used == to compare range/bearing to the target. Camera readings always
        //     have a little noise, so they are essentially never EXACTLY equal to a target
        //     number - this condition was basically always false. Switched to the same
        //     Math.abs(...) > tolerance checks from Exercises 6 and 7.
        //  2) It also compared bearing directly to BEARING_TOLERANCE_DEGREES (3.0) instead
        //     of checking whether bearing's distance from 0 was within tolerance - fixed by
        //     reusing the Math.abs(bearing) > tolerance pattern from Exercise 6.
        //  3) The big one: it OR'd "both aligned" against giveUpTime<30 (which, like
        //     Exercises 6/7, was always true since giveUpTime is only ever a few seconds
        //     ahead). The loop needs to keep going while opModeIsActive() AND we haven't
        //     timed out AND we still need to correct SOMETHING - and "still need to correct
        //     something" is an OR of "bearing still off" and "range still off" (this is the
        //     De Morgan's-law swap from the coach guide: "not (bearing-ok AND range-ok)" is
        //     the same as "(bearing NOT ok) OR (range NOT ok)"). Also added a
        //     detection == null check, since detection.ftcPose on a null detection (tag not
        //     visible yet) would crash the OpMode with a NullPointerException.
        while (opModeIsActive() && now() < giveUpTime &&
                (detection == null
                        || Math.abs(detection.ftcPose.bearing) > BEARING_TOLERANCE_DEGREES
                        || Math.abs(detection.ftcPose.range - TARGET_RANGE_INCHES) > RANGE_TOLERANCE_INCHES)) {

            detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

            if (detection != null) {
                // TODO 2: Compute a turn power AND a forward power this time, the same way you
                //   did in Exercises 6 and 7 - then pass BOTH into ONE driveRobotCentric call.
                //   Hint: driveRobotCentric(forwardPower, 0, turnPower)
                // COACH FIX: only the forward (range) correction was here - the turn
                // (bearing) correction from Exercise 6 was missing entirely, so the robot
                // would drive to the right distance but never actually turn to face the
                // tag. Added the turn calculation back and passed both into one call.
                double turn = Math.signum(detection.ftcPose.bearing) * TURN_POWER;
                double forward = Math.signum(detection.ftcPose.range - TARGET_RANGE_INCHES) * DRIVE_POWER;
                drive.driveRobotCentric(forward, 0, turn);
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
        // COACH FIX: there was no code here at all, so the robot would coast at whatever
        // power it last had right into the "READY TO SHOOT" message. Added the stop call.
        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("READY TO SHOOT");
        telemetry.update();
    }
}
