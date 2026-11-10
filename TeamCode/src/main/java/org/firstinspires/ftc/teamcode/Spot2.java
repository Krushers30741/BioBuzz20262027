package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

@Config
@Autonomous(name = "Spot2 - shoot all four Pollen", group = "Autos")
public class Spot2 extends FTC30741Base {
public static int EXPOSURE = 6;
public static int GAIN = 25;
// Total seconds for the shot, spin-up included. Tune live in Dashboard under Spot2.
public static double SHOOT_SECONDS = 12.0;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true, false);
        initVision();
        vision.setManualExposure(EXPOSURE, GAIN);   // exposure ms, gain - robot is still when it reads the tag

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive() && now() < giveUpTime) {
            while (!vision.hasSpot2() && now() < giveUpTime - 15) {
                telemetry.addLine("Tag Not Found");
            }
            if (vision.hasSpot2()) {
                shooter.setFloat();
                // Spin up, feed the balls (pausing/kicking on speed dips), then stop - see ShotController.
                new ShotController(shooter, conveyor).run(SHOOT_SECONDS, this::opModeIsActive);
            }

            drive.driveTicks(drive.squares(0.75), -0.75, 0, 0);
            drive.driveTicks(drive.squares(3), 0,-0.75 , 0);
            drive.driveRobotCentric(0, 0, 0);

            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 2 complete!");
        telemetry.update();

        // Release the camera and its stream so the next OpMode can use them.
        vision.close();
    }
}
