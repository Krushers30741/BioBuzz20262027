package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * UTILITY: finds the fastest the shooter wheels can really spin.
 *
 * Hold A to run the shooter at full power. Watch "max seen" - once it stops climbing, that is
 * the real ceiling in ticks/second. Set SHOOTER_SPEED in TeleopDrive to about 85% of it, so the
 * motors can actually reach the target. Press B to stop and clear the max.
 *
 * The numbers show on the Driver Station, FTC Dashboard and Panels, same as TeleopDrive.
 * Run it with a ball-free shooter and keep hands clear of the wheels.
 */
@TeleOp(name = "Utility - Shooter Max Speed", group = "Utility")
public class UtilityShooterMaxSpeed extends FTC30741Base {

    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        initOpMode(false, false,false, true, false, false);
        panels.debug("Hold A = shooter full power, B = stop and reset max");
        panels.update(telemetry);
        waitForStart();

        double maxLeft = 0;
        double maxRight = 0;

        while (opModeIsActive()) {
            if (gamepad1.a) {
                shooter.setForwardDirection();
                shooter.setPower(1.0);
            } else {
                shooter.setPower(0);
            }
            if (gamepad1.b) {
                maxLeft = 0;
                maxRight = 0;
            }

            double left = shooter.getLeftShooterMotorVelocity();
            double right = shooter.getRightShooterMotorVelocity();
            maxLeft = Math.max(maxLeft, left);
            maxRight = Math.max(maxRight, right);

            panels.addData("left velocity", left);
            panels.addData("right velocity", right);
            panels.addData("max seen (left)", maxLeft);
            panels.addData("max seen (right)", maxRight);
            panels.addData("suggested SHOOTER_SPEED (85% of lower max)",
                    0.85 * Math.min(maxLeft, maxRight));
            double lowerMax = Math.min(maxLeft, maxRight);
            if (lowerMax > 0) {
                double f = 32767.0 / lowerMax;
                panels.addData("suggested VELOCITY_F (Shooter config)", f);
                panels.addData("suggested VELOCITY_P (10% of F)", 0.1 * f);
                panels.addData("note", "VELOCITY_I = 0, VELOCITY_D = 0. Enter in Dashboard -> Shooter.");
            }
            panels.update(telemetry);
        }
        shooter.setPower(0);
    }
}
