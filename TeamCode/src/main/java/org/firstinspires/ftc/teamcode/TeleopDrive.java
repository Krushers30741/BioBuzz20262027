package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * BIOBUZZ TeleOp — robot-centric mecanum drive with heading hold, intake and shooter.
 *
 * <p>Telemetry goes to the Driver Station, FTC Dashboard, and Panels all at once. Panels:
 * {@code http://192.168.43.1:8001}. FTC Dashboard: {@code http://192.168.43.1:8080/dash}. The
 * {@code public static} fields below are marked {@code @Config}, so they can be tuned live in
 * Dashboard while the robot runs (they reset when the app restarts or code is redeployed).</p>
 *
 * <p>Controls (gamepad 1):
 * left stick     - drive forward/back and strafe
 * right stick X  - turn
 * left bumper    - press once: spin up the shooter; the conveyor feeds by itself when at speed.
 *                  (While held it also puts the drive in slow mode.)
 * left trigger   - stop shooter, conveyor and intake
 * right bumper   - hold: run intake and conveyor
 * right trigger  - hold: reverse intake, conveyor and shooter (clear a jam)
 * options        - reset the heading
 */
@Config
@TeleOp(name = "TeleopDrive", group = "BIOBUZZ")
public class TeleopDrive extends FTC30741Base {

    // Driving at full power all the time makes fine positioning (lining up on the Hive,
    // Flowers, etc.) hard. Holding the left bumper scales everything down for precision.
    // Live-tunable from FTC Dashboard or Panels.
    public static double SLOW_MODE_SCALE = 0.4;
    // The shooter numbers (SHOOTER_SPEED, SETTLE_MS, KICK_PERCENT, pause settings) now live in
    // ShotController, shared with Spot1 and Spot2 - tune them in Dashboard under ShotController.

    // FTC teleop periods are 2:00 (120s). These fire a one-time rumble + LED color change as
    // warnings, instead of checking a driver's own watch/count. Live-tunable.
    public static double WARNING_SECONDS_LEFT = 20.0;
    public static double FINAL_WARNING_SECONDS_LEFT = 5.0;
    private static final double TELEOP_DURATION_SECONDS = 120.0;
    private boolean warningGiven = false;
    private boolean finalWarningGiven = false;
    private ShotController shot;                 // shooter spin-up and feeding, shared with the autos
    private boolean reverseWasActive = false;    // right trigger held last loop
    // Loop timing (shown in Panels): how long one pass of the main loop takes.
    private final ElapsedTime loopTimer = new ElapsedTime();
    private double loopMs = 0;
    private double maxLoopMs = 0;

    private TelemetryManager panelsTelemetry;

    @Override
    public void runOpMode() throws InterruptedException {
        // Mirror telemetry to Panels, FTC Dashboard, and the Driver Station all at once.
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        initOpMode(true, true, true, true, false);
        shot = new ShotController(shooter, conveyor);

        panelsTelemetry.debug("Ready - press START");
        panelsTelemetry.update(telemetry);
        waitForStart();

        loopTimer.reset();
        while (opModeIsActive()) {
            loopMs = loopTimer.milliseconds();   // time since the start of the previous pass
            loopTimer.reset();
            maxLoopMs = Math.max(maxLoopMs, loopMs);
            engageDrive();
            runIntake();
            reverseIntake();
            shootArtifacts();
            stopShooting();
            showTelemetry();
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
            intake.setDirectionForward();
            intake.setPower(0.75);
            conveyor.setDirectionForward();
            conveyor.setPower(1.0);
        }
    }
    public void reverseIntake(){
        boolean active = gamepad1.right_trigger > 0;
        if (intake != null && active) {
            intake.setDirectionReverse();
            intake.setPower(0.75);
            conveyor.setDirectionReverse();
            conveyor.setPower(1.0);
            //shooter.setReverseDirection();
            //shooter.setPower(1.0);
            shot.cancel();      // the reverse control owns the conveyor now
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
        // wheels are up to speed - no second press. ShotController does the rest every loop.
        if (gamepad1.leftBumperWasPressed()) {
            if (intake != null) {
                intake.setPower(0);
            }
            shot.start();
        }
        shot.update();
    }

    public void stopShooting(){
        if (shooter != null && gamepad1.left_trigger_pressed){
            shot.stop();
            if (intake != null) {
                intake.setPower(0);
            }
        }
    }
    public void engageDrive(){
        if (gamepad1.options) {
            drive.resetHeading();
        }

        double slowModeScale = gamepad1.left_bumper ? SLOW_MODE_SCALE : 1.0;
        double forward = gamepad1.left_stick_y * slowModeScale;
        double strafe = gamepad1.left_stick_x * slowModeScale;
        double turn = gamepad1.right_stick_x * slowModeScale;
        drive.driveRobotCentricHeadingHold(forward, strafe, turn);
    }

    public void showTelemetry(){
        panelsTelemetry.addData("heading (deg)", drive.getHeadingDegrees());
        panelsTelemetry.addData("slow mode", gamepad1.left_bumper ? "ON" : "off");
        if (shooter != null) {
            panelsTelemetry.addData("shooter target", shooter.getTargetVelocity());
            panelsTelemetry.addData("shooter L velocity", shooter.getLeftShooterMotorVelocity());
            panelsTelemetry.addData("shooter R velocity", shooter.getRightShooterMotorVelocity());
            panelsTelemetry.addData("shooter at speed", shooter.atSpeed());
            panelsTelemetry.addData("conveyor feeding", shot.isFeeding());
            panelsTelemetry.addData("kick active", shot.isKickActive());
            panelsTelemetry.addData("conveyor paused", shot.isPaused());
            panelsTelemetry.addData("shots (speed dips)", shot.getDipCount());
            panelsTelemetry.addData("last dip lowest speed", shot.getLastDipLowSpeed());
            panelsTelemetry.addData("last recovery (ms)", shot.getLastRecoveryMs());
            panelsTelemetry.addData("shooter PIDF", shooter.getVelocityPidf());
            panelsTelemetry.addData("battery (V)", drive.getBatteryVoltage());
        }
        panelsTelemetry.addData("loop time (ms)", loopMs);
        panelsTelemetry.addData("max loop time (ms)", maxLoopMs);
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
