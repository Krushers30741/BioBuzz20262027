package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@Config
@Configurable
@Autonomous(name = "test shooter", group = "BIOBUZZ")
public class test_shooter  extends LinearOpMode {








    @Override
    public void runOpMode() throws InterruptedException {
        // Mirror telemetry to Panels, FTC Dashboard, and the Driver Station all at once.


        DcMotorEx  LM = hardwareMap.get(DcMotorEx.class, "leftShooterMotor");

        DcMotorEx  RM = hardwareMap.get(DcMotorEx.class, "rightShooterMotor");

        DcMotorEx  CV = hardwareMap.get(DcMotorEx.class, "conveyor");

        LM.setDirection(DcMotorSimple.Direction.FORWARD);

        CV.setDirection(DcMotorSimple.Direction.FORWARD);

        RM.setDirection(DcMotorSimple.Direction.REVERSE);

        LM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LM.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        RM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RM.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        CV.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        CV.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



        waitForStart();

        while (opModeIsActive()) {

            LM.setPower(0.35);
            RM.setPower(0.35);
            CV.setPower(0.50);
        }
    }


}
