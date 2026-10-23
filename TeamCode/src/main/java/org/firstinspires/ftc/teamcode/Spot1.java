package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@Autonomous(name = "Spot1 - shoot all four Pollen", group = "Autos")
public class Spot1 extends FTC30741Base {




    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true,false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive()) {
           shooter.setForwardDirection();
           shooter.startVelocitySpinUp(2000);
           conveyor.setDirectionForward();
           while(!shooter.isReady()){}
           conveyor.setPower(1);
           wait(6.0);
           conveyor.setPower(0);
           shooter.setPower(0);
           intake.setPower(0.5);
           drive.driveTicks(drive.squares(2), 0, 1, 0);
           drive.driveTicks(drive.squares(3.5), 1, 0, 0);
           intake.setPower(0);
            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();


    }
}
