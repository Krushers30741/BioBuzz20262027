package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * Drives the robot around. Owns the four drive motors and the Control Hub's built-in IMU,
 * and does field-centric mecanum driving — pushing the stick "forward" always drives away
 * from the driver, even if the robot has spun around mid-match.
 */
public class MecanumDrive {

    private final HardwareMap hardwareMap;
    private final DcMotorEx leftFront, leftBack, rightBack, rightFront;
    private final IMU imu;

    public MecanumDrive(HardwareMap hw) {
        this.hardwareMap=hw;
        // TODO: make sure your robot configuration (Driver Station app -> Configure Robot)
        //   has motors with these exact names, or change the names below to match.
        leftFront = hw.get(DcMotorEx.class, "leftFront");
        leftBack = hw.get(DcMotorEx.class, "leftBack");
        rightBack = hw.get(DcMotorEx.class, "rightBack");
        rightFront = hw.get(DcMotorEx.class, "rightFront");

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
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
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
    public void driveRobotCentric(double forward, double strafe, double turn) {


        double frontLeftPower = forward + strafe + turn;
        double frontRightPower = forward + strafe - turn;
        double backLeftPower = forward - strafe + turn;
        double backRightPower = forward - strafe - turn;

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
     * Tells the IMU "whatever direction the robot is facing right now counts as forward."
     * Bind this to a button so a driver can re-zero the heading if the robot got bumped or
     * turned by hand between autonomous and teleop, or between matches.
     */
    public void resetHeading() {
        imu.resetYaw();
    }

    /** Current heading in degrees — mainly for telemetry/debugging. */
    public double getHeadingDegrees() {
        return Math.toDegrees(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
    }
    public double[] getWheelVelocities(){
       return new double[]{
                leftFront.getVelocity(),
                rightFront.getVelocity(),
                leftBack.getVelocity(),
                rightBack.getVelocity()
        };
        }
    public double[] getWheelCurrents(){
        return new double[]{
                leftFront.getCurrent(CurrentUnit.AMPS),
                rightFront.getCurrent(CurrentUnit.AMPS),
                leftBack.getCurrent(CurrentUnit.AMPS),
                rightBack.getCurrent(CurrentUnit.AMPS)
        };
    }
    public double getBatteryVoltage(){
        double minVoltage = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor :hardwareMap.voltageSensor){
            double v = sensor.getVoltage();
            if (v>0&&v<minVoltage){
                minVoltage = v;
            }
        }
        return minVoltage;
    }
    }

