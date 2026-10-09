package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamServer;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

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

    // The tag the Exercises aim at. These are the real BIOBUZZ tags from our kit (36h11 family).
    // Official Hive tag IDs: red 0-7, blue 38-45. Change this to aim at a different tag.
    public static final int PRACTICE_TAG_ID = 39;
    // Official BIOBUZZ tags are 3.25 in. squares - MEASURE the black square on our kit tags
    // and change this if it is different, or range readings will be wrong.
    public static final double PRACTICE_TAG_SIZE_INCHES = 3.25;

    // Camera resolution - the one our camera is known to work at (640x360 is NOT supported in
    // YUY2 on this camera) and the one the lens calibration was done at. Don't change without
    // recalibrating.
    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 480;
    private static final double CALIBRATION_WIDTH = 1280.0;

    // TEMPORARY DEBUG SWITCH: set to false to run the camera with NO AprilTag detector attached
    // (exactly like the Camera Frame Capture utility). If the stream and camera FPS work with it
    // false but not true, the AprilTag detector is what's stopping the camera. Set back to true
    // when done.
    private static final boolean ATTACH_APRILTAG = true;

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    public VisionSystem(HardwareMap hardwareMap) {
        final double SCALE = CAMERA_WIDTH / CALIBRATION_WIDTH;
        // Start from the SDK's official library for this season's field tags.
        AprilTagLibrary official = AprilTagGameDatabase.getCurrentGameTagLibrary();

        // The SDK's library already has the BIOBUZZ tags - trying to add one again throws
        // "attempting to add a tag that already exists" - so use it as is.
        // Rename each tag to include its ID (e.g. "BLUE AUDIENCE 38") so the label drawn on the
        // camera stream shows exactly which tag it is. Same tags and sizes, just a clearer name.
        AprilTagLibrary.Builder libraryBuilder = new AprilTagLibrary.Builder();
        for (AprilTagMetadata tag : official.getAllTags()) {
            libraryBuilder.addTag(new AprilTagMetadata(
                    tag.id, tag.name + " " + tag.id, tag.tagsize, tag.distanceUnit));
        }
        AprilTagLibrary library = libraryBuilder.build();

        // Lens calibration values from our 3DF Zephyr calibration (calibration.xml).
        // Measured at 1280x720; scaled by SCALE for the resolution we actually run at.
        // Only valid for a 16:9 resolution - a different shape (like 640x480) needs recalibrating.
        aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(library)
                .setLensIntrinsics(
                        902.450747597 * SCALE, 902.450747597 * SCALE,
                        633.887792793 * SCALE, 361.131399436 * SCALE)
                .build();

        // TODO: make sure your robot configuration (Driver Station app -> Configure Robot) has
        //   a webcam named exactly "Webcam 1", or change the name below to match.
        VisionPortal.Builder portalBuilder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                // Same stream format the Camera Frame Capture utility uses (it works with this camera).
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG);  //was YUY2
        if (ATTACH_APRILTAG) {
            portalBuilder.addProcessor(aprilTag);
        }
        visionPortal = portalBuilder.build();

        // Lower decimation = the detector looks at more detail, so it can find small tags (ours are
        // 3.25 in.) from farther away. Default is 3; 2 is a good balance of range and speed.
        aprilTag.setDecimation(2);

        // Show the camera on the Driver Station ("Camera Stream" in the ... menu) and on FTC
        // Dashboard - same as the Camera Frame Capture utility does. Without this, the stream
        // doesn't appear for OpModes that use VisionSystem.
        CameraStreamServer.getInstance().setSource(visionPortal);
        FtcDashboard.getInstance().startCameraStream(visionPortal, 30);
    }

    /** Every AprilTag the camera can currently see that it also recognizes (has size info for). */
    public List<AprilTagDetection> getDetections() {
        return aprilTag.getDetections();
    }

    /**
     * Looks for one specific tag ID among everything currently visible.
     *
     * <p>SDK 12.0 note: {@link #getDetections()} can hand back either a plain single-tag
     * detection or a "cluster" detection (several tags treated as one group) - only the single
     * kind ({@link AprilTagSingleDetection}) has an .id field, so we check which kind each one
     * is before looking at its ID.</p>
     *
     * @param tagId the tag ID to look for (e.g. {@link #PRACTICE_TAG_ID})
     * @return that tag's detection (with id and range/bearing/yaw filled in), or null if it
     *         isn't visible right now
     */
    public AprilTagSingleDetection findTag(int tagId) {
        for (AprilTagDetection detection : getDetections()) {
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection single = (AprilTagSingleDetection) detection;
                if (single.metadata != null && single.id == tagId) {
                    return single;
                }
            }
        }
        return null;
    }

    /** Camera status, e.g. STREAMING. Anything else (or an error) means the camera isn't delivering images. */
    public String getCameraState() {
        return String.valueOf(visionPortal.getCameraState());
    }

    /** Frames per second the camera is delivering. Near 0 means images aren't arriving. */
    public float getFps() {
        return visionPortal.getFps();
    }

    /**
     * Debug text describing every detection the camera reports: its type, ID, and whether it has
     * size info (metadata) and a measured pose (range/bearing). Shows exactly why findTag() would
     * or wouldn't return a tag.
     */
    public String getSeenIds() {
        StringBuilder sb = new StringBuilder();
        for (AprilTagDetection d : getDetections()) {
            sb.append(d.getClass().getSimpleName()).append('[');
            if (d instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection single = (AprilTagSingleDetection) d;
                sb.append("id=").append(single.id).append(' ');
                sb.append("metadata=").append(single.metadata != null ? "yes" : "NO").append(' ');
            }
            sb.append("pose=").append(d.ftcPose != null ? "yes" : "NO").append("] ");
        }
        return sb.length() == 0 ? "none" : sb.toString().trim();
    }

    /** Call when the OpMode ends, so the camera and stream are released for the next OpMode. */
    public void close() {
        CameraStreamServer.getInstance().setSource(null);
        FtcDashboard.getInstance().stopCameraStream();
        visionPortal.close();
    }
}
