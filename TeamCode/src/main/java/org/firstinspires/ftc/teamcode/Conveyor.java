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
public class Conveyor {

    private final DcMotorEx conveyor;

    public Conveyor(HardwareMap hardwareMap) {
        // TODO: make sure your hardware config has a motor with this name (or change it below)
        //   see https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/index.html
        conveyor = hardwareMap.get(DcMotorEx.class, "conveyor");
    }

    public void setPower(double power) {
        conveyor.setPower(power);
    }

    public void setDirection() {
        conveyor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void setDirectionReverse() {
        conveyor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void brake() {
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}