package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

/**
 * EXERCISE 5: See the Tag
 *
 * Your job: print live information about the practice AprilTag to telemetry, any time the
 * camera can see it - the tag's ID, how far away it is (range), and which way the robot needs
 * to turn to face it (bearing).
 *
 * This is the vision equivalent of Exercise 1's timer loop: it's the simplest possible thing you
 * can do with the camera, but it teaches the one idea everything else in this exercise set
 * builds on - every loop, you ask the camera "do you see the tag right now?", and it hands you
 * back either real numbers or nothing at all.
 *
 * This one is a TeleOp on purpose, not an Autonomous - that way you can press START, then walk
 * the tag around in front of the camera and watch the numbers change live, instead of waiting
 * through a 30-second autonomous window each time you want to test something.
 */
@TeleOp(name = "Exercise 5 - See the Tag", group = "Exercises")
public class Exercise5_SeeTheTag extends FTC30741Base {

    @Override
    public void runOpMode() throws InterruptedException {
        // This sets up the camera and AprilTag detector (see VisionSystem.java).
        initVision();

        telemetry.addLine("Exercise 5 ready - press START, then hold the practice tag up to the camera");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // TODO 1: Ask the vision system whether it currently sees the practice tag.
            //   Hint: vision.findTag(int tagId) - the tag ID you want is
            //   VisionSystem.PRACTICE_TAG_ID. Store the result in a variable.
            AprilTagDetection detection = null; // <-- replace null with the real call

            // TODO 2: If detection is NOT null, the tag is visible - show its info.
            //   If it IS null, the tag isn't visible right now - say so instead.
            //   Hint: detection.id, detection.ftcPose.range, detection.ftcPose.bearing,
            //   and detection.ftcPose.yaw are all available once you know detection isn't null.
            telemetry.addLine("TODO 2: show tag info here");

            telemetry.update();
        }
    }
}
