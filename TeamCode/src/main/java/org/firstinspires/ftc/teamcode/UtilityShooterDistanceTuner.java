package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * UTILITY: fills in ShotController.SHOOTER_TABLE (wheel speed for each distance).
 *
 * Park facing the Hive so the camera sees our alliance's tags, then:
 *   X / B          - measure to BLUE / RED tags
 *   right bumper   - hold: intake + conveyor, to load balls
 *   left bumper    - press once: shoot (same spin-up and feeding as TeleopDrive)
 *   left trigger   - stop shooter, conveyor and intake
 *   D-pad up/down  - speed +25 / -25 ticks/second
 *   D-pad right/left - speed +5 / -5 ticks/second
 *
 * When most shots score, write down {distance, speed} from the screen. That is one table row.
 * Back up about a foot and repeat. Full steps: SHOOTER-DISTANCE-TUNING.md in TeamCode.
 *
 * This OpMode always shoots at the speed on screen (distance mode is switched off while it runs),
 * so the table doesn't affect what you're testing.
 */
@TeleOp(name = "Utility - Shooter Distance Tuner", group = "Utility")
public class UtilityShooterDistanceTuner extends FTC30741Base {

    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        initOpMode(false, false, true, true, true, false);
        initVision();
        ShotController shot = new ShotController(shooter, conveyor, vision);

        // Shoot at the speed on screen, not from the table, while tuning.
        boolean distanceModeWas = ShotController.USE_DISTANCE_SPEED;
        ShotController.USE_DISTANCE_SPEED = false;

        panels.debug("Shooter Distance Tuner - press START");
        panels.update(telemetry);
        waitForStart();

        while (opModeIsActive()) {
            // --- Which alliance's tags to measure to ---
            if (gamepad1.xWasPressed()) ShotController.ALLIANCE = "blue";
            if (gamepad1.bWasPressed()) ShotController.ALLIANCE = "red";

            // --- Change the speed (one step per press) ---
            if (gamepad1.dpadUpWasPressed()) ShotController.SHOOTER_SPEED += 25;
            if (gamepad1.dpadDownWasPressed()) ShotController.SHOOTER_SPEED -= 25;
            if (gamepad1.dpadRightWasPressed()) ShotController.SHOOTER_SPEED += 5;
            if (gamepad1.dpadLeftWasPressed()) ShotController.SHOOTER_SPEED -= 5;
            if (ShotController.SHOOTER_SPEED < 0) ShotController.SHOOTER_SPEED = 0;

            // --- Load balls ---
            if (!shot.isRunning()) {
                if (gamepad1.right_bumper) {
                    intake.setDirectionForward();
                    intake.setPower(0.75);
                    conveyor.setDirectionForward();
                    conveyor.setPower(1.0);
                } else {
                    intake.setPower(0);
                    conveyor.setPower(0);
                }
            }

            // --- Shoot / stop ---
            if (gamepad1.leftBumperWasPressed()) {
                intake.setPower(0);
                shot.start();   // picks up a new SHOOTER_SPEED each time you press it
            }
            if (gamepad1.left_trigger > 0.5) {
                shot.stop();
                intake.setPower(0);
            }
            shot.update();

            // --- Screen ---
            double distance = vision.getCurrentDistance(ShotController.ALLIANCE);
            panels.addData("alliance tags", ShotController.ALLIANCE + "  (X = blue, B = red)");
            panels.addData("DISTANCE (in)", distance > 0
                    ? String.format("%.1f", distance) : "no tag seen");
            panels.addData("SPEED (ticks/sec)", ShotController.SHOOTER_SPEED);
            panels.addData("shooter L velocity", shooter.getLeftShooterMotorVelocity());
            panels.addData("shooter R velocity", shooter.getRightShooterMotorVelocity());
            panels.addData("shooter at speed", shooter.atSpeed());
            panels.addData("shots (speed dips)", shot.getDipCount());
            panels.addData("write down", "{DISTANCE, SPEED} when most shots score");
            panels.update(telemetry);
        }

        shot.stop();
        intake.setPower(0);
        ShotController.USE_DISTANCE_SPEED = distanceModeWas;
        vision.close();
    }
}
