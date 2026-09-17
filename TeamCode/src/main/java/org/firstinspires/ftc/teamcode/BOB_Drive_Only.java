package org.firstinspires.ftc.teamcode;

/*
 * BOB_Drive_Only
 *
 * Mecanum drive and nothing else. No shooter, indexer, servos, camera,
 * or sensors - so this OpMode will run even if all of that hardware is
 * unplugged, as long as the four drive motors are in the configuration.
 *
 * Good for: checking motor directions, testing a fresh build, letting a
 * new driver get a feel for the robot, or isolating "is it the drivetrain
 * or is it everything else?"
 *
 * Controls (gamepad 1):
 *   left stick Y  - forward / back
 *   left stick X  - strafe left / right
 *   right stick X - turn
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "BOB Drive Only")
public class BOB_Drive_Only extends LinearOpMode {

  private DcMotor leftFront;
  private DcMotor rightFront;
  private DcMotor leftBack;
  private DcMotor rightBack;

  @Override
  public void runOpMode() {

    // Names must match the robot configuration exactly.
      leftFront = hardwareMap.get(DcMotor.class, "leftFront");
      rightFront = hardwareMap.get(DcMotor.class, "rightFront");
      leftBack = hardwareMap.get(DcMotor.class, "leftBack");
      rightBack = hardwareMap.get(DcMotor.class, "rightBack");

    // Same directions as BOB_A_Main.
      leftFront.setDirection(DcMotor.Direction.REVERSE);
      rightFront.setDirection(DcMotor.Direction.FORWARD);
      leftBack.setDirection(DcMotor.Direction.FORWARD);
      rightBack.setDirection(DcMotor.Direction.FORWARD);

      leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    // BRAKE makes the robot stop when the sticks are released instead of
    // coasting. Switch to FLOAT if the drivers prefer coasting.
      leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    telemetry.addData("Status", "Ready - press START");
    telemetry.update();

    waitForStart();

    while (opModeIsActive()) {

      // Read the sticks. Y is negated because pushing the stick forward
      // reports a negative value.
      double drive = -gamepad1.left_stick_y;
      double strafe = gamepad1.left_stick_x;
      double turn = gamepad1.right_stick_x;

      // Mecanum mixing - same math as BOB_A_Main.
      double frontLeftPower = drive + strafe + turn;
      double frontRightPower = drive - strafe - turn;
      double backLeftPower = drive - strafe + turn;
      double backRightPower = drive + strafe - turn;

      // Those four values can add up past 1.0 when you push two sticks at
      // once. The motors would just clip anything over 1.0, which bends the
      // direction the robot actually travels. Scaling all four down by the
      // largest keeps the ratios - and therefore the heading - correct.
      double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
      max = Math.max(max, Math.abs(backLeftPower));
      max = Math.max(max, Math.abs(backRightPower));
      if (max > 1.0) {
        frontLeftPower = frontLeftPower / max;
        frontRightPower = frontRightPower / max;
        backLeftPower = backLeftPower / max;
        backRightPower = backRightPower / max;
      }

        leftFront.setPower(frontLeftPower);
        rightFront.setPower(frontRightPower);
        leftBack.setPower(backLeftPower);
        rightBack.setPower(backRightPower);

      telemetry.addData("Sticks", "drive %.2f  strafe %.2f  turn %.2f", drive, strafe, turn);
      telemetry.addData("Front", "L %.2f  R %.2f", frontLeftPower, frontRightPower);
      telemetry.addData("Rear", "L %.2f  R %.2f", backLeftPower, backRightPower);
      telemetry.update();
    }
  }
}
