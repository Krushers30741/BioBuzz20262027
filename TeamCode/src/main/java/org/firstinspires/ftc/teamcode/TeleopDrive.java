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
    public Teleop teleop = new Teleop();

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
            teleop.teleopModeActive();
        }
    }
}