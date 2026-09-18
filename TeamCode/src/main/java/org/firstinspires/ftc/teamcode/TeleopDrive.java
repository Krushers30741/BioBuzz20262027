package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * BIOBUZZ TeleOp — field-centric mecanum drive.
 *
 * <p>Starts with drive only. Once an intake motor named "Intake" exists in your robot
 * configuration, change {@code initOpMode(true, false)} below to {@code initOpMode(true, true)}
 * to turn on the right-bumper intake control.</p>
 *
 * Controls (gamepad 1):
 *   left stick Y   - drive forward/back (field-relative — always "away from you")
 *   left stick X   - strafe left/right (field-relative)
 *   right stick X  - turn
 *   left bumper    - hold for slow/precision mode
 *   right bumper   - run intake (once enabled — see above)
 *   options        - reset "forward" to whichever way the robot is currently facing
 */
@TeleOp(name = "TeleopDrive", group = "BIOBUZZ")
public class TeleopDrive extends FTC30741Base {

    // Driving at full power all the time makes fine positioning (lining up on the Hive,
    // Flowers, etc.) hard. Holding the left bumper scales everything down for precision.
    private static final double SLOW_MODE_SCALE = 0.4;

    // FTC teleop periods are 2:00 (120s). These fire a one-time rumble + LED color change as
    // warnings, instead of checking a driver's own watch/count.
    private static final double TELEOP_DURATION_SECONDS = 120.0;
    private static final double WARNING_SECONDS_LEFT = 20.0;
    private static final double FINAL_WARNING_SECONDS_LEFT = 5.0;

    private boolean warningGiven = false;
    private boolean finalWarningGiven = false;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false);

        telemetry.addLine("Ready - press START");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.options) {
                drive.resetHeading();
            }

            double slowModeScale = gamepad1.left_bumper ? SLOW_MODE_SCALE : 1.0;
            double forward = -gamepad1.left_stick_y * slowModeScale;
            double strafe = gamepad1.left_stick_x * slowModeScale;
            double turn = gamepad1.right_stick_x * slowModeScale;
            drive.driveFieldCentric(forward, strafe, turn);

            if (intake != null) {
                intake.setPower(gamepad1.right_bumper ? 1.0 : 0.0);
            }

            checkEndgameWarnings();

            telemetry.addData("heading (deg)", drive.getHeadingDegrees());
            telemetry.addData("slow mode", slowModeScale < 1.0 ? "ON" : "off");
            telemetry.addData("time left (s)", "%.0f", TELEOP_DURATION_SECONDS - now());
            telemetry.update();
        }
    }

    /**
     * Warns the driver with a controller rumble + light bar color as the 2:00 teleop clock
     * runs low, so they don't have to watch a stopwatch to know when endgame starts.
     */
    private void checkEndgameWarnings() {
        double secondsLeft = TELEOP_DURATION_SECONDS - now();

        if (!warningGiven && secondsLeft <= WARNING_SECONDS_LEFT) {
            warningGiven = true;
            gamepad1.rumbleBlips(2);
            gamepad1.setLedColor(1, 1, 0, Gamepad.LED_DURATION_CONTINUOUS); // yellow
        }

        if (!finalWarningGiven && secondsLeft <= FINAL_WARNING_SECONDS_LEFT) {
            finalWarningGiven = true;
            gamepad1.rumble(1.0, 1.0, 800);
            gamepad1.setLedColor(1, 0, 0, Gamepad.LED_DURATION_CONTINUOUS); // red
        }
    }
}
