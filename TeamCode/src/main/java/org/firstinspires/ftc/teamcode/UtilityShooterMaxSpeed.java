package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * UTILITY: finds the fastest the shooter wheels can really spin.
 *
 * Hold A to run the shooter at full power. Watch "max seen" - once it stops climbing, that is
 * the real ceiling in ticks/second. Set SHOOTER_SPEED in TeleopDrive to about 85% of it, so the
 * motors can actually reach the target. Press B to stop and clear the max.
 *
 * Run it with a ball-free shooter and keep hands clear of the wheels.
 */
@TeleOp(name = "Utility - Shooter Max Speed", group = "Utility")
public class UtilityShooterMaxSpeed extends FTC30741Base {

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(false, false, true, false, false);
        telemetry.addLine("Hold A = shooter full power, B = stop and reset max");
        telemetry.update();
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

            telemetry.addData("left velocity", left);
            telemetry.addData("right velocity", right);
            telemetry.addData("max seen (left)", maxLeft);
            telemetry.addData("max seen (right)", maxRight);
            telemetry.addData("suggested SHOOTER_SPEED (85% of lower max)",
                    0.85 * Math.min(maxLeft, maxRight));
            double lowerMax = Math.min(maxLeft, maxRight);
            if (lowerMax > 0) {
                double f = 32767.0 / lowerMax;
                telemetry.addData("suggested VELOCITY_F (Shooter config)", f);
                telemetry.addData("suggested VELOCITY_P (10% of F)", 0.1 * f);
                telemetry.addLine("VELOCITY_I = 0, VELOCITY_D = 0. Enter in Dashboard -> Shooter.");
            }
            telemetry.update();
        }
        shooter.setPower(0);
    }
}
