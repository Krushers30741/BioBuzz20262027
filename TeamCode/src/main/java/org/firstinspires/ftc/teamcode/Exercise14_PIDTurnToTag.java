package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

/**
 * EXERCISE 14: PID Turn to the Tag
 *
 * Exercise 6 turned toward the tag at a fixed speed (Math.signum), so the robot can hunt back and
 * forth near the goal. Now use the PIDController you finished in Exercise 13 so the robot turns
 * fast when it's far off and gently when it's close.
 *
 * This is a TeleOp on purpose, so you can try it again and again while tuning:
 *   - Hold gamepad 1 A: the robot turns toward the tag using PID.
 *   - Let go of A: the robot stops.
 *   - Change kP / kI / kD in FTC Dashboard (Configuration -> Exercise14_PIDTurnToTag) between tries.
 *
 * The error is how far the tag is from straight ahead. ftcPose.bearing is POSITIVE when the tag is
 * to the LEFT, and a POSITIVE turn spins the robot RIGHT - so (like Exercise 6) the signs are
 * opposite, and we aim for a bearing of 0:
 *      error = 0 - bearing
 *
 * Camera numbers arrive a few frames late, so expect to use SMALLER gains than in Exercise 13.
 */
@Config
@TeleOp(name = "Exercise 14 - PID Turn to Tag", group = "Exercises")
public class Exercise14_PIDTurnToTag extends FTC30741Base {

    // Dashboard can change these live.
    public static double kP = 0.012;          // power per degree of error (20 degrees off -> 0.24)
    public static double kI = 0.0;
    public static double kD = 0.0;
    public static double kStatic = 0.05;      // a small extra push so the robot can start moving at all
    public static double maxPower = 0.4;      // never turn faster than this
    public static double toleranceDegrees = 2.0;

    @Override
    public void runOpMode() throws InterruptedException {
          initOpMode(true, false,false,false,false);
        initVision();

        PIDController pid = new PIDController(kP, kI, kD);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Exercise 14 ready - press START, then HOLD A to turn toward tag");
        telemetry.update();
        waitForStart();

        boolean wasHolding = false;

        while (opModeIsActive()) {
            boolean holding = gamepad1.a;
            AprilTagSingleDetection detection = vision.findTag(VisionSystem.PRACTICE_TAG_ID);

            double turn = 0;
            double error = 0;

            if (holding && detection != null) {
                if (!wasHolding) {
                    pid.reset();   // a fresh turn starts with a clean slate
                }
                pid.setGains(kP, kI, kD);

                // TODO 1: the error is how far from "straight ahead" the tag is.
                //   Remember: error = 0 - bearing  (use detection.ftcPose.bearing).
                error = 0;

                // TODO 2: ask the PID controller for a turn power: pid.update(error).
                turn = 0;

                // TODO 3 (static friction): motors need a minimum push before the robot moves at
                //   all. If Math.abs(error) is bigger than toleranceDegrees, add kStatic in the
                //   SAME direction as turn:  turn += kStatic * Math.signum(error).
                //   (Use the sign of error, not turn, so it still works if turn is tiny.)

                // TODO 4: limit the turn to between -maxPower and +maxPower, and set it to 0 when
                //   Math.abs(error) <= toleranceDegrees (close enough - stop fighting).
            }

            drive.driveRobotCentric(0, 0, turn);
            wasHolding = holding;

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("error (deg)", error);
            packet.put("turn power", turn);
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("holding A", holding);
            telemetry.addData("tag visible", detection != null);
            telemetry.addData("error (deg)", error);
            telemetry.addData("turn power", turn);
            telemetry.update();
        }

        drive.driveRobotCentric(0, 0, 0);
        vision.close();
    }
}
