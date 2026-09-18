package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Base class for all FTC30741 OpModes — BIOBUZZ (2026-2027) season.
 *
 * <p>Every OpMode fills out the same "form": which subsystems does it actually need?
 * {@link #initOpMode(boolean, boolean)} builds only the ones you ask for and wires them up —
 * the real behavior for each subsystem lives in its own class ({@link MecanumDrive},
 * {@link Intake}), not here. Call this from your OpMode's runOpMode() before waitForStart().</p>
 */
public abstract class FTC30741Base extends LinearOpMode implements Clock {

    protected MecanumDrive drive;
    protected Intake intake;

    /**
     * @param useDrive  whether to build the drivetrain (see {@link MecanumDrive})
     * @param useIntake whether to build the intake (see {@link Intake})
     */
    public void initOpMode(boolean useDrive, boolean useIntake) {
        if (useDrive) {
            drive = new MecanumDrive(hardwareMap);
        }

        if (useIntake) {
            intake = new Intake(hardwareMap);
        }

        telemetry.addLine("Robot Init Complete");
        telemetry.update();
    }

    /**
     * Returns the current runtime of this OpMode in seconds.
     *
     * <p>Implements {@link Clock#now()} using {@link #getRuntime()}.</p>
     *
     * @return current runtime in seconds
     */
    public double now() {
        return this.getRuntime();
    }
}
