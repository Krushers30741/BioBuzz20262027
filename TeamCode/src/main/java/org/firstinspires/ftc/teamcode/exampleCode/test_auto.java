package org.firstinspires.ftc.teamcode.exampleCode;
//hey, its me, its jsfdnmssfs;andios3hjnr45mnmfdm67ejek
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.FTC30741Base;


@Autonomous(name = "test_auto", group = "Autos")
public class test_auto extends FTC30741Base {




    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true,false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive()) {
           drive.driveTicks(drive.squares(5), 0.6, 0, 0);
            drive.driveTicks(drive.squares(0.25), 0, 0, -0.6);
            drive.driveTicks(drive.squares(2), 0.6, 0, 0);
            telemetry.update();
        }


        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();


    }
}
