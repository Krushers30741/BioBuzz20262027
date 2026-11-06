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
    public static double SHOOTER_SPEED = 655;
    // The wheels must stay at speed this long (ms) before the conveyor feeds a ball. Gives each
    // shot a settled, full-speed start instead of firing the instant speed is touched.
    public static double SETTLE_MS = 150;
    // When a ball pulls the slower wheel DIP_TRIGGER_PERCENT or more below SHOOTER_SPEED, the
    // shooter target is raised by KICK_PERCENT until the wheels are back up to SHOOTER_SPEED, then
    // it drops back to normal. Set KICK_PERCENT to 0 to turn the kick off.
    public static double KICK_PERCENT = 5;
    public static double DIP_TRIGGER_PERCENT = 3;
    // true = after each ball dips the speed, the conveyor STOPS until the wheels are back at
    // SHOOTER_SPEED and settled for SETTLE_MS, then feeds the next ball. false = conveyor runs
    // nonstop. Flip it live in Dashboard (TeleopDrive) to compare the two.
    public static boolean PAUSE_AFTER_EACH_BALL = true;
    // Safety: if the speed has not recovered this long (ms) after a dip, feed the next ball anyway
    // so the conveyor can never stay stopped.
    public static double PAUSE_TIMEOUT_MS = 500;

    // FTC teleop periods are 2:00 (120s). These fire a one-time rumble + LED color change as
    // warnings, instead of checking a driver's own watch/count. Live-tunable.
    public static double WARNING_SECONDS_LEFT = 20.0;
    public static double FINAL_WARNING_SECONDS_LEFT = 5.0;
    private static final double TELEOP_DURATION_SECONDS = 120.0;
    private boolean warningGiven = false;
    private boolean finalWarningGiven = false;
    private boolean shooterRunning = false;      // left bumper pressed, waiting to feed / feeding
    private boolean conveyorFeeding = false;     // conveyor is running right now (shooter at speed)
    private final ElapsedTime settleTimer = new ElapsedTime();
    private boolean wasAtSpeed = false;
    // Shot log (shown in Panels): how far the speed fell on the last dip and how long it took back.
    private int dipCount = 0;
    private double lastDipLowSpeed = 0;
    private double lastRecoveryMs = 0;
    private double currentDipLow = 0;
    private boolean inDip = false;
    private boolean conveyorPaused = false;      // conveyor stopped, waiting for speed to recover
    private final ElapsedTime dipTimer = new ElapsedTime();
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
            intake.setPower(0.75);
            conveyor.setDirectionForward();
            conveyor.setPower(1.0);
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
            wasAtSpeed = false;
            inDip = false;
            conveyorPaused = false;
            dipCount = 0;
        }
        // Checked every loop.
        if (shooterRunning) {
            boolean atSpeed = shooter.atSpeed();
            double lowest = Math.min(shooter.getLeftShooterMotorVelocity(),
                    shooter.getRightShooterMotorVelocity());

            if (atSpeed && !wasAtSpeed) {
                settleTimer.reset();            // just got to speed - start the settle clock
            }
            wasAtSpeed = atSpeed;

            if (!conveyorFeeding) {
                // Start feeding once at speed and settled. After that the conveyor runs nonstop.
                if (atSpeed && settleTimer.milliseconds() >= SETTLE_MS) {
                    conveyorFeeding = true;
                    conveyor.setPower(1.0);
                }
            } else {
                // A ball pulled the speed down: kick the target up a few percent until the wheels
                // are back at SHOOTER_SPEED, then go back to the normal target.
                if (!inDip && lowest < SHOOTER_SPEED * (1 - DIP_TRIGGER_PERCENT / 100.0)) {
                    inDip = true;
                    dipCount++;
                    dipTimer.reset();
                    currentDipLow = lowest;
                    if (PAUSE_AFTER_EACH_BALL) {
                        conveyor.setPower(0);       // hold the next ball until speed is back
                        conveyorPaused = true;
                    }
                    if (KICK_PERCENT > 0) {
                        shooter.startVelocitySpinUp(SHOOTER_SPEED * (1 + KICK_PERCENT / 100.0));
                    }
                }
                if (inDip) {
                    currentDipLow = Math.min(currentDipLow, lowest);
                    if (lowest >= SHOOTER_SPEED) {
                        inDip = false;
                        lastDipLowSpeed = currentDipLow;
                        lastRecoveryMs = dipTimer.milliseconds();
                        shooter.startVelocitySpinUp(SHOOTER_SPEED);
                        settleTimer.reset();        // settle clock for the next ball
                    }
                }
                // Resume feeding once recovered and settled, if pause mode was switched off, or
                // if the speed has not come back within PAUSE_TIMEOUT_MS.
                boolean recovered = !inDip && atSpeed && settleTimer.milliseconds() >= SETTLE_MS;
                boolean timedOut = dipTimer.milliseconds() >= PAUSE_TIMEOUT_MS;
                if (conveyorPaused && (!PAUSE_AFTER_EACH_BALL || recovered || timedOut)) {
                    conveyor.setPower(1.0);
                    conveyorPaused = false;
                }
            }
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
            inDip = false;
            conveyorPaused = false;
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
            panelsTelemetry.addData("conveyor feeding", conveyorFeeding);
            panelsTelemetry.addData("kick active", inDip);
            panelsTelemetry.addData("conveyor paused", conveyorPaused);
            panelsTelemetry.addData("shots (speed dips)", dipCount);
            panelsTelemetry.addData("last dip lowest speed", lastDipLowSpeed);
            panelsTelemetry.addData("last recovery (ms)", lastRecoveryMs);
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
