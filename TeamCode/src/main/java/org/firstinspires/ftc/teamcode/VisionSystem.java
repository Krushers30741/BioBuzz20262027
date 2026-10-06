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
    public static final int PRACTICE_TAG_ID = 38;
    // Official BIOBUZZ tags are 3.25 in. squares - MEASURE the black square on our kit tags
    // and change this if it is different, or range readings will be wrong.
    public static final double PRACTICE_TAG_SIZE_INCHES = 3.25;

    // Camera resolution. The lens calibration below was done at 1280x720. Running at 640x360
    // (exactly half, same 16:9 shape) keeps the same field of view, so the calibration numbers
    // are simply scaled by CAMERA_WIDTH / 1280. 1280x720 was too heavy for the Control Hub
    // (camera FPS read 0), so we run at half size. Keep the 16:9 shape if you change this.
    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 360;
    private static final double CALIBRATION_WIDTH = 1280.0;

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    public VisionSystem(HardwareMap hardwareMap) {
        final double SCALE = CAMERA_WIDTH / CALIBRATION_WIDTH;
        // Start from the SDK's official library for this season's field tags.
        AprilTagLibrary official = AprilTagGameDatabase.getCurrentGameTagLibrary();

        // Our kit has tags 30-45. Add any of them the official library doesn't already know
        // about (so we never register the same ID twice).
        AprilTagLibrary.Builder libraryBuilder = new AprilTagLibrary.Builder().addLibrary(official);

        AprilTagLibrary library = libraryBuilder.build();

        // Lens calibration values from our 3DF Zephyr calibration (calibration.xml).
        // Measured at 1280x720; scaled by SCALE for the resolution we actually run at.
        // Only valid for a 16:9 resolution - a different shape (like 640x480) needs recalibrating.
        aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(library)
                .setLensIntrinsics(
                        925.919086365 * SCALE, 925.919086365 * SCALE,
                        656.336036235 * SCALE, 369.441035915 * SCALE)
                .build();

        // TODO: make sure your robot configuration (Driver Station app -> Configure Robot) has
        //   a webcam named exactly "Webcam 1", or change the name below to match.
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                // Same stream format the Camera Frame Capture utility uses (it works with this camera).
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(aprilTag)
                .build();

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

    /** IDs of every tag currently seen, for debugging (includes tags we have no size info for). */
    public String getSeenIds() {
        StringBuilder sb = new StringBuilder();
        for (AprilTagDetection d : getDetections()) {
            if (d instanceof AprilTagSingleDetection) {
                sb.append(((AprilTagSingleDetection) d).id).append(' ');
            }
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
