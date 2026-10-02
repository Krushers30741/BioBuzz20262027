package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.lang.annotation.Target;

/**
 * EXERCISE 7: Drive to a Set Distance from the Tag
 *
 * Your job: make the robot drive forward or backward, using the camera, until it's exactly
 * TARGET_RANGE_INCHES away from the practice tag - then stop.
 *
 * Same feedback-loop idea as Exercise 6, but using a different piece of ftcPose this time:
 * range instead of bearing. Range is always a positive number - the straight-line distance
 * (in inches) from the camera to the tag. If range is bigger than your target, you're too far
 * away and need to drive forward. If it's smaller, you're too close and need to back up.
 *
 * This exercise assumes the robot starts out already roughly facing the tag (it only drives
 * forward/backward, it doesn't turn) - Exercise 8 combines this with Exercise 6's turning.
 */
@Autonomous(name = "Exercise 7 - Drive to Range", group = "Exercises")
public class Exercise7_DriveToRange extends FTC30741Base {

    // How far away from the tag the robot should end up, in inches. Ask your coach what a
    // reasonable distance is - it might depend on where a shooter would eventually go.
    private static final double TARGET_RANGE_INCHES = 24.0;

    // How close to TARGET_RANGE_INCHES is good enough.
    private static final double RANGE_TOLERANCE_INCHES = 1.0;

    // How hard to drive while correcting. Keep this small for the same reason as Exercise 6.
    private static final double DRIVE_POWER = 0.2;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);
        initVision();

        telemetry.addLine("Exercise 7 ready - press START");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 5.0;
        AprilTagDetection detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

        // TODO 1: Loop while: OpMode active, AND not timed out, AND (tag not visible yet OR
        //   its range is still outside tolerance of TARGET_RANGE_INCHES).
        //   Hint: Math.abs(detection.ftcPose.range - TARGET_RANGE_INCHES) > RANGE_TOLERANCE_INCHES
        while (opModeIsActive() && giveUpTime < 30 && Math.abs(detection.ftcPose.range - TARGET_RANGE_INCHES) > RANGE_TOLERANCE_INCHES ) {

            detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

            if (detection != null) {
                // TODO 2: Drive forward or backward to close the distance.
                //   Hint: Math.signum(detection.ftcPose.range - TARGET_RANGE_INCHES) gives you
                double forward= Math.signum(detection.ftcPose.range - TARGET_RANGE_INCHES) * DRIVE_POWER;
                //   +1 if you're too far away, -1 if you're too close. Multiply that by
                //   DRIVE_POWER and pass it as the forward argument below.
                drive.driveRobotCentric(forward, 0, 0);
            } else {
                drive.driveRobotCentric(0, 0, 0);
            }

            telemetry.addData("tag visible", detection != null);
            if (detection != null) {
                telemetry.addData("range (in)", detection.ftcPose.range);
            }
            telemetry.update();
        }

        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Exercise 7 complete!");
        telemetry.update();
    }
}
