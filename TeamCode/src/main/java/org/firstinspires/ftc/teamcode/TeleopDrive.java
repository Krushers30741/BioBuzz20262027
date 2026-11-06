package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * BIOBUZZ TeleOp — field-centric mecanum drive.
 *
 * <p>Starts with drive only. Once an intake motor named "Intake" exists in your robot
 * configuration, change {@code initOpMode(true, false)} below to {@code initOpMode(true, true)}
 * to turn on the right-bumper intake control.</p>
 *
 * <p>Telemetry goes to the Driver Station, FTC Dashboard, and Panels (panels.bylazar.com) all
 * at once. Panels dashboard: {@code http://192.168.43.1:8001} (Control Hub) while connected to
 * the robot's WiFi. FTC Dashboard: {@code http://192.168.43.1:8080/dash}. The fields below are
 * marked {@code @Config} (FTC Dashboard) and {@code @Configurable} (Panels), so either
 * dashboard can tune them live while the robot runs, no redeploy needed.</p>
 * <p>
 * Controls (gamepad 1):
 * left stick Y   - drive forward/back (field-relative — always "away from you")
 * left stick X   - strafe left/right (field-relative)
 * right stick X  - turn
 * left bumper    - hold for slow/precision mode
 * right bumper   - run intake (once enabled — see above)
 * options        - reset "forward" to whichever way the robot is currently facing
 */
@Config
@TeleOp(name = "TeleopDrive", group = "BIOBUZZ")
public class TeleopDrive extends FTC30741Base {

    // Driving at full power all the time makes fine positioning (lining up on the Hive,
    // Flowers, etc.) hard. Holding the left bumper scales everything down for precision.
    // Live-tunable from FTC Dashboard or Panels.
    public static double SLOW_MODE_SCALE = 0.4;
    public static double SHOOTER_SPEED = 655;
    public static double STRAFE_GAIN = 1.3;

    // FTC teleop periods are 2:00 (120s). These fire a one-time rumble + LED color change as
    // warnings, instead of checking a driver's own watch/count. Live-tunable.
    public static double WARNING_SECONDS_LEFT = 20.0;
    public static double FINAL_WARNING_SECONDS_LEFT = 5.0;
    private static final double TELEOP_DURATION_SECONDS = 120.0;
    private boolean warningGiven = false;
    private boolean finalWarningGiven = false;
    private boolean shooterRunning = false;      // left bumper pressed, waiting to feed / feeding
    private boolean conveyorFeeding = false;     // conveyor is running right now (shooter at speed)
    private boolean reverseWasActive = false;    // right trigger held last loop

    private TelemetryManager panelsTelemetry;

    @Override
    public void runOpMode() throws InterruptedException {
        // Mirror telemetry to Panels, FTC Dashboard, and the Driver Station all at once.
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        initOpMode(true, true, true, true, false);

        panelsTelemetry.debug("Ready - press START");
        panelsTelemetry.update(telemetry);
        waitForStart();

        while (opModeIsActive()) {
            engageDrive();
            runIntake();
            reverseIntake();
            shootArtifacts();
            stopShooting();
            panelsTelemotry();
            checkEndgameWarnings();

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
    public void runIntake(){
        if (intake != null && gamepad1.right_bumper) {
            intake.setPower(gamepad1.right_bumper ? 0.75 : 0.0);
            conveyor.setDirectionForward();
            conveyor.setPower(gamepad1.right_bumper ? 1.0 : 0.0);
        }
    }
    public void reverseIntake(){
        boolean active = gamepad1.right_trigger > 0;
        if (intake != null && active) {
            intake.setPower(0.75);
            conveyor.setDirectionReverse();
            conveyor.setPower(1.0);
            shooter.setReverseDirection();
            shooter.setPower(1.0);
            shooterRunning = false;
            conveyorFeeding = false;
        } else if (intake != null && reverseWasActive) {
            // Trigger just released: stop everything it was running, once.
            intake.setPower(0);
            conveyor.setPower(0);
            shooter.setPower(0);
        }
        reverseWasActive = active;
    }

    public void shootArtifacts(){
        if (shooter == null) {
            return;
        }
        // Left bumper (one press): start the shooter. The conveyor runs BY ITSELF whenever both
        // wheels are up to speed - no second press.
        if (gamepad1.leftBumperWasPressed()) {
            shooter.setForwardDirection();
            shooter.startVelocitySpinUp(SHOOTER_SPEED);
            if (intake != null) {
                intake.setPower(0);
            }
            conveyor.setDirectionForward();
            conveyor.setPower(0.0);
            shooterRunning = true;
            conveyorFeeding = false;
        }
        // Checked every loop: feed ONLY while both wheels are up to speed. If the speed drops
        // (a ball pulls it down), the conveyor stops until the wheels recover, then resumes.
        if (shooterRunning) {
            conveyorFeeding = shooter.atSpeed();
            conveyor.setPower(conveyorFeeding ? 1.0 : 0.0);
        }
    }

    public void stopShooting(){
        if (shooter != null && gamepad1.left_trigger_pressed){
            shooter.setPower(0);
            conveyor.setPower(0);
            if (intake != null) {
                intake.setPower(0);
            }
            shooterRunning = false;
            conveyorFeeding = false;
        }
    }
    public void engageDrive(){
        if (gamepad1.options) {
            drive.resetHeading();
        }

        // Use a local value - the old line overwrote SLOW_MODE_SCALE itself, so after the first
        // loop slow mode was gone for good.
        double slowModeScale = gamepad1.left_bumper ? SLOW_MODE_SCALE : 1.0;
        double forward = gamepad1.left_stick_y * slowModeScale;
        double strafe = gamepad1.left_stick_x * slowModeScale;
        double turn = gamepad1.right_stick_x * slowModeScale;
        // drive.driveFieldCentric(forward, strafe, turn);
        drive.driveRobotCentricHeadingHold(forward, strafe, turn);
    }

    public void panelsTelemotry(){
        panelsTelemetry.addData("heading (deg)", drive.getHeadingDegrees());
        panelsTelemetry.addData("slow mode", gamepad1.left_bumper ? "ON" : "off");
        if (shooter != null) {
            panelsTelemetry.addData("shooter target", shooter.getTargetVelocity());
            panelsTelemetry.addData("shooter L velocity", shooter.getLeftShooterMotorVelocity());
            panelsTelemetry.addData("shooter R velocity", shooter.getRightShooterMotorVelocity());
            panelsTelemetry.addData("shooter at speed", shooter.atSpeed());
            panelsTelemetry.addData("conveyor feeding", conveyorFeeding);
        }
        panelsTelemetry.addData("time left (s)", TELEOP_DURATION_SECONDS - now());
        panelsTelemetry.addData("raw left_stick_x", gamepad1.left_stick_x);
        panelsTelemetry.addData("raw left_stick_y", gamepad1.left_stick_y);
        panelsTelemetry.addData("raw right_stick_x", gamepad1.right_stick_x);
        double[] wheelVolocities = drive.getWheelVelocities();
        double[] wheelCurrents = drive.getWheelCurrents();
        panelsTelemetry.addData("lf speed (ticks/secs)", wheelVolocities[0]);
        panelsTelemetry.addData("rf speed (ticks/secs)", wheelVolocities[1]);
        panelsTelemetry.addData("lb speed (ticks/secs)", wheelVolocities[2]);
        panelsTelemetry.addData("rb speed (ticks/secs)", wheelVolocities[3]);
        panelsTelemetry.addData("lf current (a)", wheelCurrents[0]);
        panelsTelemetry.addData("rf current (a)", wheelCurrents[1]);
        panelsTelemetry.addData("lb current (a)", wheelCurrents[2]);
        panelsTelemetry.addData("rb current (a)", wheelCurrents[3]);
        panelsTelemetry.update(telemetry);
    }
}
