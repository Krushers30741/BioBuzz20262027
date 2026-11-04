package org.firstinspires.ftc.teamcode.exampleCode;
//hey, its me, its jsfdnmssfs;andios3hjnr45mnmfdm67ejek
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.FTC30741Base;


@Autonomous(name = "Spot1 - shoot all four Pollen", group = "Autos")
public class figure8auto extends FTC30741Base {




    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(true, true, true, true,false);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        double giveUpTime = now() + 30.0;

        if (opModeIsActive()) {
            telemetry.update();
        }
        drive.driveTicks(drive.squares(5), 0.6, 0., 0);
        drive.driveTicks(drive.squares(1.5), 0, 0.6, 0);
        drive.driveTicks(drive.squares(4), 0, -0.6, 0);
        drive.driveTicks(drive.squares(1.5), 0, -0.6, 0);
        drive.driveTicks(drive.squares(4), 0.6, 0., 0);
        drive.driveTicks(drive.squares(1.5), 0, -0.6, 0);
        drive.driveRobotCentric(0, 0, 0);

        telemetry.addLine("Spot 1 complete!");
        telemetry.update();


    }
}
