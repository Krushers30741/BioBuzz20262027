package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


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




            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();

        // Release the camera and its stream so the next OpMode can use them.
        vision.close();
    }
}
