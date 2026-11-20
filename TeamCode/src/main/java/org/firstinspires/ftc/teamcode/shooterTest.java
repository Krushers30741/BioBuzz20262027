package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Config
@TeleOp(name = "shooterTest", group = "BIOBUZZ")
public class shooterTest extends LinearOpMode {
    private TelemetryManager panelsTelemetry;
    public void runOpMode() throws InterruptedException {
        DcMotorEx leftShooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");//Expansion Hub 3
        leftShooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftShooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();
       while(opModeIsActive()) {

           if (gamepad1.rightBumperWasPressed()) {
               leftShooterMotor.setPower(1.0);
           }
           panelsTelemetry.addData("shooter velocity", leftShooterMotor.getVelocity()/28*60);
           panelsTelemetry.update(telemetry);
       }
    }
}