package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@Autonomous(name = "Spot1 - shoot all four Pollen", group = "Autos")
public class Spot1 extends FTC30741Base {


    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false, true, true,false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        while (opModeIsActive() && now() < giveUpTime) {
            shooter.setForwardDirection();
            shooter.setVelocity(2000);
            conveyor.setPower(0.5);
            wait(6.0);
            conveyor.setPower(0);
            shooter.setPower(0);
            intake.setPower(0.5);
            drive.driveTicks(2320, 0, 1, 0);
            drive.driveTicks(4060, 1, 0, 0);
            intake.setPower(0);
            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();

        // Release the camera and its stream so the next OpMode can use them.
        vision.close();
    }
}
