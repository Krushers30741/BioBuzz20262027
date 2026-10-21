package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "Test Color sensor", group = "TeleOp")
public class testColorSensor extends FTC30741Base {


    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode(false, false, false, false, true);
        initVision();

        telemetry.addLine("");
        telemetry.update();
        waitForStart();

        while (opModeIsActive() ) {
        colorSensor.getColor();
        }



        telemetry.addLine("Spot 1 complete!");
        telemetry.update();

        // Release the camera and its stream so the next OpMode can use them.
        vision.close();
    }
}
