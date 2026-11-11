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
@TeleOp(name = "TeleopDriveBlue", group = "BIOBUZZ")
public class TeleopDriveBlue extends FTC30741Base {
    public Teleop teleop = new Teleop();

    @Override
    public void runOpMode() throws InterruptedException {
        while (opModeIsActive()) {
            teleop.teleopModeActive();
        }
    }
}