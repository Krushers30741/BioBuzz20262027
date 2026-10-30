package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;


@Autonomous(name = "Spot2 - shoot all four Pollen", group = "Autos")
public class Spot2 extends FTC30741Base {


    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true, false);
        initVision();
        vision.setManualExposure(6, 25);   // exposure ms, gain - robot is still when it reads the tag

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive() && now() < giveUpTime) {
            while (!vision.hasSpot2() && now() < giveUpTime - 15) {
                telemetry.addLine("Tag Not Found");
            }
            if (vision.hasSpot2()) {
                shooter.setForwardDirection();
                shooter.setFloat();
                shooter.startVelocitySpinUp(1000);
                shooter.waitUntilReady(5.0, this::opModeIsActive);
                conveyor.setDirectionForward();
                conveyor.setPower(1);
                wait(10.0);
                conveyor.setPower(0);
                shooter.setPower(0);
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
