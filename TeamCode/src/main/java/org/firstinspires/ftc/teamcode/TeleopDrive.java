package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Bare-bones drive-only TeleOp for BIOBUZZ. No scoring subsystems yet — this exists so the
 * team can test-drive the chassis as soon as it's built. Add scoring controls here (or split
 * back out into a TeleopWithActions-style shared class, like last year) once a mechanism exists.
 */
@TeleOp(name = "TeleopDrive", group = "BIOBUZZ")
public class TeleopDrive extends FTC26502OpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false, true,false, false, false);

        waitForStart();
        while (opModeIsActive()) {
            drive.updatePoseEstimate();

            double fwd = -gamepad1.left_stick_y;
            double str = -gamepad1.left_stick_x;
            double turn = -gamepad1.right_stick_x;
            drive.setDrivePowers(fwd, str, turn);

            if (gamepad1.right_bumper) {
                intake.setPower(1.0);
            } else {
                intake.setPower(0.0);
            }

            telemetry.addData("heading (deg)", Math.toDegrees(drive.localizer.getPose().heading.toDouble()));
            telemetry.update();
        }
    }
}
