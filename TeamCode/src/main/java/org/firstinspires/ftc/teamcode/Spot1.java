package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Config

@Autonomous(name = "Spot1 - shoot all four Pollen", group = "Autos")
public class Spot1 extends FTC30741Base {


// Total seconds for the shot, spin-up included. Tune live in Dashboard under Spot1.
public static double SHOOT_SECONDS = 7.0;

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, false, true, true, true, false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive()) {
           // Spin up, feed the balls (pausing/kicking on speed dips), then stop - see ShotController.
           new ShotController(shooter, conveyor).run(SHOOT_SECONDS, this::opModeIsActive);
           intake.setPower(0.5);
           drive.driveTicks(drive.squares(2), 0, 0.75, 0);
           drive.driveTicks(drive.squares(4), -0.75, 0, 0);
           intake.setPower(0);
            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();


    }

}
