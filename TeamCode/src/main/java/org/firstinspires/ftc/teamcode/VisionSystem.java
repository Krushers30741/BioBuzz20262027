package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

/**
 * Wraps AprilTag detection for the season. Carried over from last year's VisionSystem —
 * the general VisionPortal/AprilTagProcessor setup below is not game-specific and needs
 * no changes.
 *
 * <p><b>TODO (BIOBUZZ specifics to fill in once field details are confirmed):</b></p>
 * <ul>
 *     <li>BIOBUZZ's AprilTags on the HIVE are 3.25 in. (last year's DECODE tags were 6.5 in.) —
 *     double check your tag library / camera calibration matches this.</li>
 *     <li>{@link #checkTag()} below is a placeholder that just returns the first detection.
 *     Replace {@code TAG_ID_PLACEHOLDER} with whatever HIVE/scoring tag ID(s) you actually
 *     want to track once you know your robot's aiming strategy.</li>
 * </ul>
 */
public class VisionSystem {

    private final Telemetry telemetry;

    // Set this to your actual camera mount relative to the robot center
    private final Position cameraPosition = new Position(
            DistanceUnit.INCH,
            0,   // X: right (+), left (-)
            0,   // Y: forward (+), back (-)
            11.5 / 2 + 1,   // Z: up (+), down (-)
            0
    );

    // Orientation of the camera relative to robot (Yaw, Pitch, Roll)
    private final YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(
            AngleUnit.DEGREES,
            0,   // Yaw
            -90, // Pitch
            0,   // Roll
            0
    );

    protected boolean blueAlliance;

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    // TODO: replace with the real HIVE/scoring AprilTag ID(s) for BIOBUZZ once decided.
    private static final int TAG_ID_PLACEHOLDER = -1;

    public VisionSystem(HardwareMap hw, Telemetry telemetry, boolean blueAlliance) {
        this.telemetry = telemetry;
        this.blueAlliance = blueAlliance;
        aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .setCameraPose(cameraPosition, cameraOrientation)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hw.get(WebcamName.class, "Webcam 1"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
    }

    public void reportTagIds() {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addData("# AprilTags Detected", detections.size());
        if (detections.isEmpty()) {
            telemetry.addLine("No tags in view.");
        } else {
            for (AprilTagDetection d : detections) {
                String name = (d.metadata != null && d.metadata.name != null) ? d.metadata.name : "Unknown";
                telemetry.addData("Tag", String.format("ID %d  Name %s", d.id, name));
            }
        }
        telemetry.update();
    }

    public void updateTelemetry() {
        telemetryAprilTag();
        telemetry.update();
    }

    public List<AprilTagDetection> getDetections() {
        return aprilTag.getDetections();
    }

    public void close() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null && detection.robotPose != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
                        detection.robotPose.getPosition().x,
                        detection.robotPose.getPosition().y,
                        detection.robotPose.getPosition().z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
                        detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                        detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                        detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)",
                        detection.center.x, detection.center.y));
            }
        }

        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
    }

    private AprilTagDetection findDetectionById(int id) {
        List<AprilTagDetection> detections = aprilTag.getDetections();
        for (AprilTagDetection d : detections) {
            if (d.id == id) return d;
        }
        return null;
    }

    /**
     * Placeholder for "is the tag I care about visible right now" — last year this checked
     * for the shooting-goal tag (ID 20/24 depending on alliance). Update TAG_ID_PLACEHOLDER
     * once you know which tag(s) matter for your BIOBUZZ aiming/scoring strategy.
     */
    public AprilTagDetection checkTag() {
        return findDetectionById(TAG_ID_PLACEHOLDER);
    }

}
