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
        initOpMode(false, true, false, false, false, false);
        initVision();
        vision.setManualExposure(EXPOSURE, GAIN);   // exposure ms, gain - robot is still when it reads the tag

        telemetry.addLine("");
        telemetry.update();
        waitForStart();
        double giveUpTime = now() + 30.0;

        if (opModeIsActive() && now() < giveUpTime) {
            // Wait up to 15 seconds for a tag, but quit right away if Stop is pressed.
            while (opModeIsActive() && !vision.hasSpot2() && now() < giveUpTime - 15) {
                telemetry.addLine("Tag Not Found");
                telemetry.update();
            }
            if (vision.hasSpot2()) {
                if(shooter!=null) {
                    shooter.setFloat();

                }

                // Spin up, feed the balls (pausing/kicking on speed dips), then stop - see ShotController.
                if(shooter!=null) {
                    new ShotController(shooter, conveyor, vision).run(SHOOT_SECONDS, this::opModeIsActive);
                }
            }

            drive2.driveTicks(drive2.squares(0.75), -0.75, 0, 0, this::opModeIsActive);
            drive2.driveTicks(drive2.squares(3), 0, -0.75, 0, this::opModeIsActive);
            drive2.driveRobotCentric(0, 0, 0);

            telemetry.update();
        }


        drive2.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 2 complete!");
        telemetry.update();

        // Release the camera and its stream so the next OpMode can use them.
        vision.close();
    }
}
