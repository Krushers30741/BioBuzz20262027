package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime; // Added for non-blocking timing

import java.util.function.BooleanSupplier;

@Config
public class Shooter {

    // Velocity-control tuning, live in FTC Dashboard (Configuration -> Shooter).
    // Leave VELOCITY_F at 0 to keep the hub's built-in values. To tune: set F to
    // 32767 / (max ticks per second from the Utility - Shooter Max Speed test), then raise P a
    // little at a time (about 10% of F is a common start) until the speed snaps back quickly after
    // a ball without oscillating. The new values apply the next time the shooter spins up.
    public static double VELOCITY_P = 0;
    public static double VELOCITY_I = 0;
    public static double VELOCITY_D = 0;
    public static double VELOCITY_F = 0;


    // One flywheel motor, named "shooter" in the robot configuration (Expansion Hub 3).
    // (The old two-wheel shooter used leftShooterMotor and rightShooterMotor.)
    private final DcMotorEx shooterMotor;


    // Asynchronous tracking variables
    // "At speed" means the wheel reaches this fraction of the target. A motor in velocity mode
    // hovers around its target and often sits just under it, so 100% can never be met.
    private static final double READY_FRACTION = 0.97;

    private final ElapsedTime timer = new ElapsedTime();
    private double targetVelocity = 0;
    private int waitCounter = 0;
    private boolean isSpinningUp = false;

    public Shooter(HardwareMap hardwareMap) {
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");//Expansion Hub 3
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setForwardDirection();   // shoot direction, same as the shooterTest OpMode
    }

    // Call this ONCE to initiate the spin-up sequence
    public void startVelocitySpinUp(double velocity) {

        this.targetVelocity = velocity;
        if (VELOCITY_F > 0) {
            PIDFCoefficients pidf = new PIDFCoefficients(VELOCITY_P, VELOCITY_I, VELOCITY_D, VELOCITY_F);
            shooterMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        }
        shooterMotor.setVelocity(velocity);

        this.waitCounter = 0;
        this.timer.reset();
        this.isSpinningUp = true;
    }

    // Call this inside your main OpMode loop continuously.
    // It returns true when the motors are at speed OR it has timed out (max 2 seconds).
    public boolean isReady() {
        if (!isSpinningUp) {
            return false;
        }

        // Check if we hit target velocity
        if (atSpeed()) {
            isSpinningUp = false;
            return true;
        }

        // Handle the 500ms intervals non-blockingly
        if (timer.milliseconds() >= 500) {
            waitCounter++;
            timer.reset();
            if (waitCounter >= 4) { // 4 * 500ms = 2 seconds max timeout
                isSpinningUp = false;
                return true;
            }
        }

        return false;
    }

    /**
     * True whenever the wheel is at (97% of) the target speed. Unlike isReady() this can be
     * asked every loop - it does not use up its answer and it has no timeout, so true really
     * means "up to speed".
     */
    public boolean atSpeed() {
        if (targetVelocity <= 0) {
            return false;
        }
        double needed = targetVelocity * READY_FRACTION;
        return getShooterMotorVelocity() >= needed;
    }

    /** The hub's velocity-control values currently in use on the shooter motor (debug). */
    public String getVelocityPidf() {
        PIDFCoefficients c = shooterMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        return String.format("P=%.2f I=%.2f D=%.2f F=%.2f", c.p, c.i, c.d, c.f);
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    /**
     * Blocks until the shooter is at speed, but never forever: gives up after timeoutSeconds, or
     * as soon as isActive turns false (pass {@code this::opModeIsActive} from your OpMode).
     * Call startVelocitySpinUp(...) first. Returns true if the shooter became ready.
     */
    public boolean waitUntilReady(double timeoutSeconds, BooleanSupplier isActive) {
        long end = System.nanoTime() + (long) (timeoutSeconds * 1e9);
        while (isActive.getAsBoolean() && System.nanoTime() < end) {
            // isReady() reports true only once per spin-up, so use its answer right away.
            if (isReady()) {
                return true;
            }
        }
        return false;
    }

    public void setPower(double power) {
        shooterMotor.setPower(power);
    }

    /** Flywheel speed in ticks/second (28 ticks per motor turn, so RPM = this / 28 * 60). */
    public double getShooterMotorVelocity() {
        return shooterMotor.getVelocity();
    }

    // "Forward" = the direction that shoots. shooterTest showed REVERSE shoots on this motor.
    // If the wheel spins backwards on the robot, swap REVERSE and FORWARD in these two methods.
    public void setForwardDirection(){
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setReverseDirection(){
        shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void setFloat(){
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }
}
