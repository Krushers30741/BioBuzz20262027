package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * EXERCISE 4: Encoder-Based Precise Drive
 *
 * Every exercise so far used a TIMER to decide when to stop. That works, but it's not very
 * precise - if the battery is a little low, or the floor is a little different, the robot
 * will travel a different distance in the same amount of time.
 *
 * Instead, this exercise uses the drive motors' ENCODERS - sensors that count exactly how far
 * each wheel has actually turned - so the robot stops after traveling a specific distance, no
 * matter what.
 *
 * STEP A (do this first, in MecanumDrive.java):
 *   MecanumDrive already has a method called getWheelVelocities() that reads each wheel's
 *   current SPEED from its encoder. Look at how that method is written, then add a new method
 *   called getWheelPositions() that reads each wheel's current POSITION instead (how many
 *   total encoder ticks it has counted since the OpMode started). Hint: DcMotorEx has a
 *   method called getCurrentPosition() - same idea as getVelocity(), just a different method
 *   name and it returns ticks instead of ticks/second.
 *
 * STEP B (do this here, in this file):
 *   Use your new getWheelPositions() method to drive forward until the front-left wheel has
 *   traveled TARGET_TICKS, then stop.
 */
@Autonomous(name = "Exercise 4 - Encoder Drive", group = "Exercises")
public class Exercise4_EncoderDrive extends FTC30741Base {

    // How far to drive, measured in encoder ticks instead of seconds. Ask your coach what a
    // reasonable starting number is for your robot's motors - it depends on the gear ratio.
    private static final double TARGET_TICKS = 1000;
    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);
        // COACH FIX: removed a leftover drive.getWheelPositions() call that was here -
        // it read the encoders and threw the result away without using it, so it didn't
        // actually do anything. The real reads happen below, where the result is used.
        telemetry.addLine("Exercise 4 ready - press START");
        telemetry.update();
        waitForStart();

        // TODO 1: Loop while the front-left wheel's position (index 0 of the array returned
        //   by drive.getWheelPositions()) is still less than TARGET_TICKS.

        while (drive.getWheelPositions()[0]<TARGET_TICKS) {

            // TODO 2: Drive forward. (Same driveRobotCentric call as Exercise 1.)
            drive.driveRobotCentric(1, 0, 0);

            // This shows you the live tick count while it's driving - helpful for debugging.
            telemetry.addData("front-left ticks", drive.getWheelPositions()[0]);
            telemetry.update();
        }

        // TODO 3: Stop the robot.
        // COACH FIX: this line was commented out, so the robot never actually got a stop
        // command after the loop - it would just coast at full power into whatever came
        // next (or rely on the OpMode ending to cut the motors). Uncommented it.
        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Exercise 4 complete!");
        telemetry.update();
    }
}
