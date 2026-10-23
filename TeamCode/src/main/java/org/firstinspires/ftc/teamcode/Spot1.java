package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Config
@Autonomous(name = "Spot1 - shoot all four Pollen", group = "Autos")
public class Spot1 extends FTC30741Base {

public static double tick1 =100;
    public static double tick2 =100;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true,false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive()) {
           // shooter.setForwardDirection();
            //shooter.startVelocitySpinUp(2000);
          //  conveyor.setDirectionForward();
           // conveyor.setPower(0.5);
            //wait(6.0);
            //conveyor.setPower(0);
           // shooter.setPower(0);
          // intake.setPower(0.5);
            drive.driveTicks(tick1, 0, -1, 0);
            drive.driveTicks(tick2, -1, 0, 0);
           // drive.driveTicks(2320, 0, 1, 0);
          //  drive.driveTicks(4060, 1, 0, 0);
           // intake.setPower(0);
            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();


    }
}
