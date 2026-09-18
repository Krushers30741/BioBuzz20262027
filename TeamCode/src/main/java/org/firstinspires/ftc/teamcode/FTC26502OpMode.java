package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import com.acmerobotics.roadrunner.Pose2d;

/***
 * Base class for all FTC26502 OpModes — BIOBUZZ (2026-2027) season.
 *
 * <p>Provides initialization for drivetrain, odometry, sensors, and vision.
 * Subclasses choose which subsystems to enable via {@link #initOpMode(boolean, boolean, boolean, boolean, boolean)}.
 * Implements {@link Clock} by exposing {@link #now()}.</p>
 *
 * <p><b>TODO (once robot mechanism is decided):</b> add fields + init flags here for
 * whatever picks up Pollen/Nectar and whatever scores into the Hive/Flower, the same
 * way {@code useShooter}/{@code useIntake} worked last season. Keep the same pattern:
 * one flag per subsystem, one line in {@link #initOpMode} to build it.</p>
 */
public abstract class FTC26502OpMode extends LinearOpMode implements Clock {

    protected MecanumDrive drive;
    protected SensorSystem sensors;
    protected VisionSystem vision;

    protected Intake intake;
    protected GoBildaPinpointDriver odo;
    protected boolean blueAlliance;

    /**
     * Initializes the selected robot subsystems and sets alliance color.
     *
     * <p>Only the subsystems specified by the boolean flags will be created and initialized.
     * Call this from your OpMode's init phase.</p>
     *
     * @param useDrive     whether to initialize the mecanum drive with a default start pose
     * @param useOdo       whether to initialize GoBilda Pinpoint odometry
     * @param useSensors   whether to initialize the sensor system
     * @param useVision    whether to initialize the vision system (AprilTag detection)
     * @param blueAlliance whether the robot is on the blue alliance
     */
    public void initOpMode(boolean useDrive, boolean useOdo, boolean useIntake,
                            boolean useSensors, boolean useVision,
                            boolean blueAlliance) {
        if (useDrive) {
            Pose2d startPose = new Pose2d(-56, 56, Math.toRadians(-35));
            drive = new MecanumDrive(hardwareMap, startPose);
        }

        if (useSensors) {
            sensors = new SensorSystem(hardwareMap, telemetry);
        }

        if (useVision) {
            vision = new VisionSystem(hardwareMap, telemetry, blueAlliance);
        }

        if (useIntake) {
            intake = new Intake(hardwareMap);
        }

        if (useOdo) {
            odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
            odo.setOffsets(-82.0, -10.0, DistanceUnit.MM);
            odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
            odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                    GoBildaPinpointDriver.EncoderDirection.FORWARD);
        }

        this.blueAlliance = blueAlliance;

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
