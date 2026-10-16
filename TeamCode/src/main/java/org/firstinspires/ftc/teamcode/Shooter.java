package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Intake subsystem — placeholder while the team decides on the BIOBUZZ scoring mechanism.
 * Right now it just hardware-maps a single motor and can spin it at a given power. Add real
 * behavior here (sensor checks, sequencing, etc.) once the mechanism design is settled.
 */
public class Shooter {

    private final DcMotorEx rightShooterMotor;

    private final DcMotorEx leftShooterMotor;

    public Shooter(HardwareMap hardwareMap) {

        leftShooterMotor = hardwareMap.get(DcMotorEx.class, "leftShooterMotor");

        rightShooterMotor = hardwareMap.get(DcMotorEx.class, "rightShooterMotor");

        leftShooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightShooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPower(double power) {
        leftShooterMotor.setPower(power);
        rightShooterMotor.setPower(power);
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
