package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

/**
 * Sets up the webcam and the AprilTag detector, and gives the rest of the code a simple way to
 * ask "do you currently see tag number X, and if so, how far away and which way is it?"
 *
 * <p>Mirrors how {@link MecanumDrive} owns the drive motors: this class owns the camera and the
 * vision pipeline, so OpModes don't have to deal with VisionPortal/AprilTagProcessor setup
 * directly.</p>
 */
public class VisionSystem {

    // Before the real BIOBUZZ field tags are confirmed/available, we're using one of FIRST's
    // own official test tags to practice with - no guessing about size or ID needed. Tag 585
    // ("Cousteau") is 6 inches, from the official FTC AprilTag testing PDF:
    // https://ftc-docs.firstinspires.org/en/latest/_downloads/9dee926dd59f7f35e84c2b816c793fea/FTCAprilTagSDK82SamplesExtended.pdf
    // (That PDF also has tags 583 "Nemo" and 584 "Jonah" at 4 inches, and 586 "Ariel" at 6
    // inches, in case more than one group wants to test with different tags at once.)
    public static final int PRACTICE_TAG_ID = 585;
    public static final double PRACTICE_TAG_SIZE_INCHES = 6.0;

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    public VisionSystem(HardwareMap hardwareMap) {
        // Start from the official library for this season's field tags (works once it's
        // available for BIOBUZZ), and add our own practice tag on top of it.
        AprilTagLibrary library = new AprilTagLibrary.Builder()
                .addLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .addTag(PRACTICE_TAG_ID, "Practice Tag", PRACTICE_TAG_SIZE_INCHES, DistanceUnit.INCH)
                .build();

        aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(library)
                .build();

        // TODO: make sure your robot configuration (Driver Station app -> Configure Robot) has
        //   a webcam named exactly "Webcam 1", or change the name below to match.
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }

    /** Every AprilTag the camera can currently see that it also recognizes (has size info for). */
    public List<AprilTagDetection> getDetections() {
        return aprilTag.getDetections();
    }

    /**
     * Looks for one specific tag ID among everything currently visible.
     *
     * @param tagId the tag ID to look for (e.g. {@link #PRACTICE_TAG_ID})
     * @return that tag's detection (with range/bearing/yaw filled in), or null if it isn't
     *         visible right now
     */
    public AprilTagDetection findTag(int tagId) {
        for (AprilTagDetection detection : getDetections()) {
            if (detection.metadata != null && detection.id == tagId) {
                return detection;
            }
        }
        return null;
    }
}
