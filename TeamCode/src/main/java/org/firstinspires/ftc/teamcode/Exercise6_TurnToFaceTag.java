package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

/**
 * EXERCISE 6: Turn to Face the Tag
 *
 * Your job: make the robot spin in place, using the camera, until it's aimed directly at the
 * practice tag - then stop.
 *
 * This is a different kind of loop than anything in the driving exercises. Exercises 1-4 all
 * knew ahead of time how long or how far to drive. This one doesn't - it has to keep checking
 * the camera and CORRECTING itself, over and over, until the error (how far off-target it is)
 * gets small enough. That's called a feedback loop, and it's how basically all real robot
 * aiming and alignment works.
 *
 * ftcPose.bearing tells you how many degrees off-target the tag is:
 *   - bearing near 0     -> the tag is straight ahead, you're aimed correctly
 *   - bearing positive/negative -> the tag is off to one side (test it yourself to see which
 *     sign means which direction on your robot!)
 */
@Autonomous(name = "Exercise 6 - Turn to Face Tag", group = "Exercises")
public class Exercise6_TurnToFaceTag extends FTC30741Base {

    // How close to "dead on" is good enough? Smaller = more precise, but can jitter/hunt more.
    private static final double BEARING_TOLERANCE_DEGREES = 3.0;

    // How hard to turn while correcting. Keep this small - a slow, controlled turn is much
    // easier to stop accurately than a fast one.
    private static final double TURN_POWER = 0.2;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);
        initVision();

        telemetry.addLine("Exercise 6 ready - press START");
        telemetry.update();
        waitForStart();

        // Autonomous only lasts 30 seconds total - don't let searching for a tag that never
        // shows up eat the whole thing. Give up after this many seconds either way.
        double giveUpTime = now() + 5.0;

        AprilTagDetection detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

        // TODO 1: Loop while ALL of these are true:
        //   - the OpMode is still active
        //   - we haven't hit giveUpTime yet
        //   - EITHER we don't see the tag yet, OR its bearing is still outside tolerance
        //     (Hint: Math.abs(detection.ftcPose.bearing) > BEARING_TOLERANCE_DEGREES)
        while ((giveUpTime<30) &&
                ((detection == null) || (Math.abs(detection.ftcPose.bearing) > BEARING_TOLERANCE_DEGREES))) {

            detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

            if (detection != null) {
                // TODO 2: Turn toward the tag. You want a small, constant-speed turn in
                //   whichever direction reduces the bearing toward 0.
                //   Hint: Math.signum(detection.ftcPose.bearing) gives you -1, 0, or 1.
                //   Multiply that by TURN_POWER and pass it as the turn argument below.
                //   Test it - if the robot turns the WRONG way, flip the sign!

                double turn = Math.signum(detection.ftcPose.bearing) * TURN_POWER;

                drive.driveRobotCentric(0, 0, turn);
            } else {
                // Tag isn't visible right now - stay still instead of spinning blindly and
                // possibly turning further away from it.
                drive.driveRobotCentric(0, 0, 0);
            }

            telemetry.addData("tag visible", detection != null);
            if (detection != null) {
                telemetry.addData("bearing", detection.ftcPose.bearing);
            }
            telemetry.update();
        }

        // TODO 3: Stop the robot.
        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Exercise 6 complete!");
        telemetry.update();
    }
}
