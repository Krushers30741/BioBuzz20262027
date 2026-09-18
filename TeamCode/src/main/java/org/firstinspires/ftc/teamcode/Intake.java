package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Intake subsystem — placeholder while the team decides on the BIOBUZZ scoring mechanism.
 * Right now it just hardware-maps a single motor and can spin it at a given power. Add real
 * behavior here (sensor checks, sequencing, etc.) once the mechanism design is settled.
 */
public class Intake {

    private final DcMotorEx intakeMotor;

    public Intake(HardwareMap hardwareMap) {
        // TODO: make sure your hardware config has a motor with this name (or change it below)
        //   see https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/index.html
        intakeMotor = hardwareMap.get(DcMotorEx.class, "Intake");
    }

    public void setPower(double power) {
        intakeMotor.setPower(power);
    }
}
