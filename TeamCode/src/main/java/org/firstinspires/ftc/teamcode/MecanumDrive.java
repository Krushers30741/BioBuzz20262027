package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import java.util.function.BooleanSupplier;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * Drives the robot around. Owns the four drive motors and the Control Hub's built-in IMU,
 * and does field-centric mecanum driving — pushing the stick "forward" always drives away
 * from the driver, even if the robot has spun around mid-match.
 */
@Config
public class MecanumDrive {

    // Back-right wheel runs slow while strafing, which turns the robot a bit. This boosts that
    // wheel's power in proportion to how hard you strafe. 1.0 = no change. Try 1.05, 1.1, ...
    // Tune live in FTC Dashboard (Configuration -> MecanumDrive), no redeploy needed.
    public static double RIGHT_BACK_STRAFE_TRIM = 1.0;

    // ---- Heading hold (keeps the robot pointed the same way while driving straight/strafing) ----
    // Turn it on/off live in FTC Dashboard (Configuration -> MecanumDrive).
    public static boolean HEADING_HOLD_ENABLED = false;
    // Turn power added per degree of heading error. 5 degrees off * 0.02 = 0.1 turn power.
    // Too small = still drifts. Too big = the robot wobbles side to side.
    public static double HEADING_KP = 0.02;
    // Damping: raise slightly (e.g. 0.001) only if it wobbles. Leave 0 to start.
    public static double HEADING_KD = 0.0;
    public static double HEADING_MAX_CORRECTION = 0.3;

    private boolean holdingHeading = false;
    private double targetHeadingDeg = 0.0;
    private double lastHeadingErrorDeg = 0.0;
    private long lastHoldNanos = 0;

    // Front-left wheel is the weak one when strafing LEFT only. Boosts it in proportion to the
    // stick. 1.0 = no change. Try 1.05, 1.1, ... (below 1.0 if it gets worse). Live in Dashboard.
    public static double LEFT_FRONT_STRAFE_LEFT_TRIM = 1.0;

    private final HardwareMap hardwareMap;
    private Telemetry telemetry;
    private final DcMotorEx leftFront, leftBack, rightBack, rightFront;
    private final IMU imu;

    public MecanumDrive(HardwareMap hw, Telemetry telemetry) {
        this.hardwareMap = hw;
        this.telemetry = telemetry;
        // TODO: make sure your robot configuration (Driver Station app -> Configure Robot)
        //   has motors with these exact names, or change the names below to match.
        leftFront = hw.get(DcMotorEx.class, "leftFront"); //Expansion Hub 1
        leftBack = hw.get(DcMotorEx.class, "leftBack"); //Expansion Hub 3
        rightBack = hw.get(DcMotorEx.class, "rightBack");//Control Hub 0
        rightFront = hw.get(DcMotorEx.class, "rightFront");//Control Hub 1

        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBack.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotorEx motor : new DcMotorEx[]{leftFront, leftBack, rightBack, rightFront}) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        // Built-in Control Hub IMU — used for field-centric drive below.
        // TODO: confirm LogoFacingDirection/UsbFacingDirection match how the hub is actually
        //   mounted on the robot: https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html
        imu = hw.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));
    }

    /**
     * Drives the robot relative to the FIELD instead of relative to the robot's current
     * heading. Reads the IMU heading and rotates the stick input by it before mixing into
     * the four wheel powers.
     *
     * @param forward how much to drive forward/back (-1 to 1; pass -gamepad.left_stick_y)
     * @param strafe  how much to strafe left/right (-1 to 1; pass gamepad.left_stick_x)
     * @param turn    how much to rotate (-1 to 1; pass gamepad.right_stick_x)
     */
    public void driveFieldCentric(double forward, double strafe, double turn) {
        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        // Rotate the stick vector by -heading so "forward" means "away from the driver"
        // instead of "whichever way the robot's front currently points."
        double rotatedForward = forward * Math.cos(-heading) - strafe * Math.sin(-heading);
        double rotatedStrafe = forward * Math.sin(-heading) + strafe * Math.cos(-heading);

        double frontLeftPower = rotatedForward + rotatedStrafe + turn;
        double frontRightPower = rotatedForward + rotatedStrafe - turn;
        double backLeftPower = rotatedForward - rotatedStrafe + turn;
        double backRightPower = rotatedForward - rotatedStrafe - turn;

        // Scale all four down together (never up) so pushing two sticks at once can't push
        // any wheel's requested power over 1.0 and distort the direction the robot travels.
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        leftFront.setPower(frontLeftPower / max);
        rightFront.setPower(frontRightPower / max);
        leftBack.setPower(backLeftPower / max);
        rightBack.setPower(backRightPower / max);
    }

    /**
     * Drives the robot relative to the FIELD instead of relative to the robot's current
     * heading. Reads the IMU heading and rotates the stick input by it before mixing into
     * the four wheel powers.
     *
     * @param forward how much to drive forward/back (-1 to 1; pass -gamepad.left_stick_y)
     * @param strafe  how much to strafe left/right (-1 to 1; pass gamepad.left_stick_x)
     * @param turn    how much to rotate (-1 to 1; pass gamepad.right_stick_x)
     */
    /**
     * Like driveRobotCentric, but when you are driving without turning, it uses the IMU to keep
     * the robot pointed the same way (a PD controller on heading error). If you ask for a turn,
     * or the robot is not translating, the hold lets go and re-locks on the new heading later.
     * Does nothing extra unless HEADING_HOLD_ENABLED is true.
     */
    public void driveRobotCentricHeadingHold(double forward, double strafe, double turn) {
        boolean translating = Math.abs(forward) > 0.05 || Math.abs(strafe) > 0.05;
        if (!HEADING_HOLD_ENABLED || Math.abs(turn) > 0.05 || !translating) {
            holdingHeading = false;
            driveRobotCentric(forward, strafe, turn);
            return;
        }

        double heading = getHeadingDegrees();
        long now = System.nanoTime();
        if (!holdingHeading) {
            holdingHeading = true;
            targetHeadingDeg = heading;
            lastHeadingErrorDeg = 0;
            lastHoldNanos = now;
        }

        // Error in degrees, wrapped to -180..180 so it never takes the long way around.
        double error = targetHeadingDeg - heading;
        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        double dt = (now - lastHoldNanos) / 1e9;
        double derivative = dt > 0 ? (error - lastHeadingErrorDeg) / dt : 0;
        lastHeadingErrorDeg = error;
        lastHoldNanos = now;

        // IMU yaw is positive when the robot turns LEFT, but a positive turn power spins it
        // RIGHT, so the correction is the opposite sign of the error.
        double correction = -(HEADING_KP * error + HEADING_KD * derivative);
        correction = Math.max(-HEADING_MAX_CORRECTION, Math.min(HEADING_MAX_CORRECTION, correction));

        driveRobotCentric(forward, strafe, correction);
    }

    public void driveRobotCentric(double forward, double strafe, double turn) {
        forward = -forward;
        //strafe = -strafe;
        double frontLeftPower = forward + strafe + turn;
        double frontRightPower = forward - strafe - turn;
        double backLeftPower = forward - strafe + turn;
        double backRightPower = forward + strafe - turn;

        // Scale all four down together (never up) so pushing two sticks at once can't push
        // any wheel's requested power over 1.0 and distort the direction the robot travels.
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        double lfTrim = strafe < 0
                ? 1.0 + (LEFT_FRONT_STRAFE_LEFT_TRIM - 1.0) * Math.min(1.0, Math.abs(strafe))
                : 1.0;
        leftFront.setPower(Math.max(-1.0, Math.min(1.0, frontLeftPower / max * lfTrim)));
        rightFront.setPower(frontRightPower / max);
        leftBack.setPower(backLeftPower / max);
        double trim = 1.0 + (RIGHT_BACK_STRAFE_TRIM - 1.0) * Math.min(1.0, Math.abs(strafe));
        rightBack.setPower(Math.max(-1.0, Math.min(1.0, backRightPower / max * trim)));
    }

    /**
     * Tells the IMU "whatever direction the robot is facing right now counts as forward."
     * Bind this to a button so a driver can re-zero the heading if the robot got bumped or
     * turned by hand between autonomous and teleop, or between matches.
     */
    public void resetHeading() {
        imu.resetYaw();
    }

    /**
     * Current heading in degrees — mainly for telemetry/debugging.
     */
    public double getHeadingDegrees() {
        return Math.toDegrees(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
    }

    public double[] getWheelVelocities() {
        return new double[]{
                leftFront.getVelocity(),
                rightFront.getVelocity(),
                leftBack.getVelocity(),
                rightBack.getVelocity()
        };
    }

    public double[] getWheelPositions() {
        return new double[]{
                leftFront.getCurrentPosition(),
                rightFront.getCurrentPosition(),
                leftBack.getCurrentPosition(),
                rightBack.getCurrentPosition()
        };
    }

    public double[] getWheelCurrents() {
        return new double[]{
                leftFront.getCurrent(CurrentUnit.AMPS),
                rightFront.getCurrent(CurrentUnit.AMPS),
                leftBack.getCurrent(CurrentUnit.AMPS),
                rightBack.getCurrent(CurrentUnit.AMPS)
        };
    }

    public double getBatteryVoltage() {
        double minVoltage = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double v = sensor.getVoltage();
            if (v > 0 && v < minVoltage) {
                minVoltage = v;
            }
        }
        return minVoltage;
    }

    /**
     * Old signature: kept so existing code compiles. It cannot see the Stop button, so prefer
     * the version below that takes isActive.
     */
    public void driveTicks(double ticks, double forward, double strafe, double turn) {
        driveTicks(ticks, forward, strafe, turn, () -> true);
    }

    /**
     * Drives until every wheel has travelled at least {@code ticks} (in either direction), or
     * until isActive turns false (pass {@code this::opModeIsActive} from your OpMode).
     */
    public void driveTicks(double ticks, double forward, double strafe, double turn, BooleanSupplier isActive) {
        // Wheel positions do not start at zero, so remember where each wheel began.
        double[] start = getWheelPositions();
        while (isActive.getAsBoolean() && !wheelsTravelled(start, ticks)) {
            driveRobotCentricHeadingHold(-forward, strafe, turn);
            // telemetry can be null if whoever built this MecanumDrive didn't pass one in.
            if (telemetry != null) {
                telemetry.addData("front-left ticks", getWheelPositions()[0] - start[0]);
                telemetry.update();
            }
        }
        holdingHeading = false;   // next move locks onto its own starting heading
        driveRobotCentric(0, 0, 0);
    }

    // True once EVERY wheel has moved at least ticks from its start, forward or backward.
    private boolean wheelsTravelled(double[] start, double ticks) {
        double[] now = getWheelPositions();
        for (int i = 0; i < 4; i++) {
            if (Math.abs(now[i] - start[i]) < ticks) {
                return false;
            }
        }
        return true;
    }

    public double squares(double numOfSquares) {
        return 1100 * numOfSquares;
    }
}

