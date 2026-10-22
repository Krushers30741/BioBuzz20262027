package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime; // Added for non-blocking timing

public class Shooter {

    private final DcMotorEx rightShooterMotor;
    private final DcMotorEx leftShooterMotor;

    // Asynchronous tracking variables
    private final ElapsedTime timer = new ElapsedTime();
    private double targetVelocity = 0;
    private int waitCounter = 0;
    private boolean isSpinningUp = false;

    public Shooter(HardwareMap hardwareMap) {
        leftShooterMotor = hardwareMap.get(DcMotorEx.class, "leftShooterMotor");
        rightShooterMotor = hardwareMap.get(DcMotorEx.class, "rightShooterMotor");

        leftShooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightShooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // Call this ONCE to initiate the spin-up sequence
    public void startVelocitySpinUp(double velocity) {
        this.targetVelocity = velocity;
        leftShooterMotor.setVelocity(velocity);
        rightShooterMotor.setVelocity(velocity);

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
        if (getLeftShooterMotorVelocity() >= targetVelocity && getRightShooterMotorVelocity() >= targetVelocity) {
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

    public void setPower(double power) {
        leftShooterMotor.setPower(power);
        rightShooterMotor.setPower(power);
    }

    public double getLeftShooterMotorVelocity() {
        return leftShooterMotor.getVelocity();
    }

    public double getRightShooterMotorVelocity() {
        return rightShooterMotor.getVelocity();
    }

    public void setForwardDirection(){
        leftShooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightShooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setReverseDirection(){
        leftShooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightShooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void setFloat(){
        leftShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightShooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }
}
