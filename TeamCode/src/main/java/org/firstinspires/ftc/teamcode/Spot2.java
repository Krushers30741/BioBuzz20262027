package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;


@Autonomous(name = "Spot2 - shoot all four Pollen", group = "Autos")
public class Spot2 extends FTC30741Base {


    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true, false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive() && now() < giveUpTime) {

            while (vision.getDetections().isEmpty() && now() < giveUpTime - 15) {
                telemetry.addLine("Tag Not Found");
            }
            if (!vision.getDetections().isEmpty()) {
                shooter.setForwardDirection();
                shooter.setFloat();
                shooter.startVelocitySpinUp(1000);
                while (!shooter.isReady()) {
                }
                conveyor.setDirectionForward();
                conveyor.setPower(1);
                wait(10.0);
                conveyor.setPower(0);
                shooter.setPower(0);
            }

            drive.driveTicks(drive.squares(0.5), 1, 0, 0);
            drive.driveTicks(drive.squares(3), 0, -1, 0);
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
