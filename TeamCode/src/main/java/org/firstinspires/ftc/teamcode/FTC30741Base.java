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
    protected Shooter shooter;
    protected VisionSystem vision;

    /**
     * @param useDrive  whether to build the drivetrain (see {@link MecanumDrive})
     * @param useIntake whether to build the intake (see {@link Intake}
     * @param useShooter whether to build the shooter (see {@link Shooter})
     */
    public void initOpMode(boolean useDrive, boolean useIntake, boolean useShooter) {
        if (useDrive) {
            drive = new MecanumDrive(hardwareMap);
        }

        if (useIntake) {
            intake = new Intake(hardwareMap);
        }

        if (useShooter) {
            shooter = new Shooter(hardwareMap);
        }

        telemetry.addLine("Robot Init Complete");
        telemetry.update();
    }

    /**
     * Sets up the webcam and AprilTag detector (see {@link VisionSystem}). Call this once, in
     * addition to {@link #initOpMode(boolean, boolean)}, in any OpMode that needs to see
     * AprilTags. Kept separate from initOpMode so OpModes that don't use the camera don't pay
     * for starting it up.
     */
    protected void initVision() {
        vision = new VisionSystem(hardwareMap);
    }

    protected void driveForTime(double forward, double strafe, double turn, double seconds) {
        double endTime = now() + seconds;
        while (opModeIsActive() && now() < endTime) {
            drive.driveRobotCentric(forward, strafe, turn);
        }
        drive.driveRobotCentric(0, 0, 0);
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

    protected void wait(double seconds) {
        double endTime = now() + seconds;
        while (opModeIsActive() && now() < endTime) {
        }
    }

}
