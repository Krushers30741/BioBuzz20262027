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
        initOpMode(false, true, false, false, false, false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive()) {
           // Spin up, feed the balls (pausing/kicking on speed dips), then stop - see ShotController.
            if(shooter!=null) {
                new ShotController(shooter, conveyor, vision).run(SHOOT_SECONDS, this::opModeIsActive);
            }
            if(intake!=null) {
                intake.setPower(0.5);
            }

           drive2.driveTicks(drive2.squares(2), 0, 0.75, 0, this::opModeIsActive);
           drive2.driveTicks(drive2.squares(4), -0.75, 0, 0, this::opModeIsActive);

            if(intake!=null) {
                intake.setPower(0.0);
            }
            telemetry.update();
        }


        drive2.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();


    }

}
