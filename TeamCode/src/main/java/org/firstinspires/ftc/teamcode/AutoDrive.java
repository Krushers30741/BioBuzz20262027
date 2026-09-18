package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Bare-bones drive-only autonomous for BIOBUZZ. Drives forward a short distance and stops —
 * a starting point to confirm odometry/localization work before any scoring logic exists.
 * Replace the trajectory below (and add scoring Actions) once a mechanism is built.
 */
@Config
@Autonomous(name = "AutoDrive", group = "BIOBUZZ")
public class AutoDrive extends FTC26502OpMode {

    public static double forwardInches = 24;

    @Override
    public void runOpMode() throws InterruptedException {
        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, dashboardTelemetry);

        initOpMode(true, false, true,false, false, false);

        Pose2d startPose = new Pose2d(0, 0, 0);
        drive = new MecanumDrive(hardwareMap, startPose);

        Action driveForward = drive.actionBuilder(startPose)
                .lineToX(forwardInches)
                .build();

        telemetry.addLine("Init complete.");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(driveForward);
    }
}
