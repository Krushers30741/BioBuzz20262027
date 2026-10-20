package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;


@Autonomous(name = "Spot1 - shoot all four Pollen", group = "Autos")
public class Spot2 extends FTC30741Base {


    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false,false,false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        while (opModeIsActive() && now() < giveUpTime) {


if(vision.findTag(38) == null && vision.findTag(39) == null && vision.findTag(40) == null && vision.findTag(41) == null) {
    telemetry.addLine("Tag Not Found");}else{
    shooter.setForwardDirection();
    shooter.setFloat();
    wait(1000);
    shooter.setPower(.25)
    conveyor.setDirectionForward();
    conveyor.setPower(1);
    wait(1000);
    conveyor.setPower(0);
    shooter.setPower(0);
    driveForTime(1,0,0,.25);
    driveForTime(0,1,0,2);
    drive.driveRobotCentric(0,0,0);
    }


            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 2 complete!");
        telemetry.update();

        // Release the camera and its stream so the next OpMode can use them.
        vision.close();
    }
}
