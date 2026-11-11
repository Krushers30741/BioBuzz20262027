package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Teleop  extends FTC30741Base {
    public static double SLOW_MODE_SCALE = 0.4;
    private ShotController shot;
    private boolean reverseWasActive = false;
    private TelemetryManager panelsTelemetry;
    private double loopMs = 0;
    private double maxLoopMs = 0;
    private final ElapsedTime loopTimer = new ElapsedTime();
    public static double WARNING_SECONDS_LEFT = 20.0;
    public static double FINAL_WARNING_SECONDS_LEFT = 5.0;
    private static final double TELEOP_DURATION_SECONDS = 120.0;
    private boolean warningGiven = false;
    private boolean finalWarningGiven = false;
    public void teleopModeActive(){
        loopMs = loopTimer.milliseconds();   // time since the start of the previous pass
        loopTimer.reset();
        maxLoopMs = Math.max(maxLoopMs, loopMs);
        engageDrive();
        runIntake();
        reverseIntake();
        shootBalls();
        stopShooting();
        showTelemetry();
        checkEndgameWarnings();
    }

    public void engageDrive() {
        if (gamepad1.options) {
            drive.resetHeading();
        }

        double slowModeScale = gamepad1.left_bumper ? SLOW_MODE_SCALE : 1.0;
        double forward = gamepad1.left_stick_y * slowModeScale;
        double strafe = gamepad1.left_stick_x * slowModeScale;
        double turn = gamepad1.right_stick_x * slowModeScale;
        drive.driveRobotCentricHeadingHold(forward, strafe, turn);
    }

    @Override
    public void runOpMode() throws InterruptedException {

    }

    public void runIntake() {
        if (intake != null && gamepad1.right_bumper) {
            intake.setDirectionForward();
            intake.setPower(0.75);
            conveyor.setDirectionForward();
            conveyor.setPower(1.0);
        }
    }

    public void reverseIntake() {
        boolean active = gamepad1.right_trigger > 0;
        if (intake != null && active) {
            intake.setDirectionReverse();
            intake.setPower(0.75);
            conveyor.setDirectionReverse();
            conveyor.setPower(1.0);
            //shooter.setReverseDirection();
            //shooter.setPower(1.0);
            shot.cancel();      // the reverse control owns the conveyor now
        } else if (intake != null && reverseWasActive) {
            // Trigger just released: stop everything it was running, once.
            intake.setPower(0);
            conveyor.setPower(0);
            shooter.setPower(0);
        }
        reverseWasActive = active;
    }

    public void shootBalls() {
        if (shooter == null) {
            return;
        }
        // Left bumper (one press): start the shooter. The conveyor runs BY ITSELF whenever both
        // wheels are up to speed - no second press. ShotController does the rest every loop.
        if (gamepad1.leftBumperWasPressed()) {
            if (intake != null) {
                intake.setPower(0);
            }
            shot.start();
        }
        shot.update();
    }

    public void stopShooting() {
        if (shooter != null && gamepad1.left_trigger_pressed) {
            shot.stop();
            if (intake != null) {
                intake.setPower(0);
            }
        }
    }

    public void showTelemetry() {
        panelsTelemetry.addData("heading (deg)", drive.getHeadingDegrees());
        panelsTelemetry.addData("slow mode", gamepad1.left_bumper ? "ON" : "off");
        if (shooter != null) {
            panelsTelemetry.addData("shooter target", shooter.getTargetVelocity());
            panelsTelemetry.addData("shooter L velocity", shooter.getLeftShooterMotorVelocity());
            panelsTelemetry.addData("shooter R velocity", shooter.getRightShooterMotorVelocity());
            panelsTelemetry.addData("shooter at speed", shooter.atSpeed());
            panelsTelemetry.addData("conveyor feeding", shot.isFeeding());
            panelsTelemetry.addData("kick active", shot.isKickActive());
            panelsTelemetry.addData("conveyor paused", shot.isPaused());
            panelsTelemetry.addData("shots (speed dips)", shot.getDipCount());
            panelsTelemetry.addData("last dip lowest speed", shot.getLastDipLowSpeed());
            panelsTelemetry.addData("last recovery (ms)", shot.getLastRecoveryMs());
            panelsTelemetry.addData("shooter PIDF", shooter.getVelocityPidf());
            panelsTelemetry.addData("battery (V)", drive.getBatteryVoltage());
        }
    }
    private void checkEndgameWarnings() {
        double secondsLeft = TELEOP_DURATION_SECONDS - now();

        if (!warningGiven && secondsLeft <= WARNING_SECONDS_LEFT) {
            warningGiven = true;
            gamepad1.rumbleBlips(2);
            gamepad1.setLedColor(1, 1, 0, Gamepad.LED_DURATION_CONTINUOUS); // yellow
        }

        if (!finalWarningGiven && secondsLeft <= FINAL_WARNING_SECONDS_LEFT) {
            finalWarningGiven = true;
            gamepad1.rumble(1.0, 1.0, 800);
            gamepad1.setLedColor(1, 0, 0, Gamepad.LED_DURATION_CONTINUOUS); // red
        }
    }
}

