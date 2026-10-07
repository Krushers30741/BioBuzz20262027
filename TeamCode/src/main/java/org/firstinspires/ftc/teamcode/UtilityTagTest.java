package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamServer;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.Locale;

/**
 * DIAGNOSTIC ONLY. Camera + AprilTag detector set up exactly like UtilityCameraFrameCapture
 * (same resolution, YUY2 stream format, stream routing, and an INIT loop), but with the AprilTag
 * detector added and NO VisionSystem involved.
 *
 * Use it to find out whether the problem is the detector itself or how Exercise 5 / VisionSystem
 * starts the camera:
 *   - Stream live + FPS above 0 here  -> the problem is in VisionSystem / Exercise 5.
 *   - Stream black here too           -> the problem is the detector with this camera.
 *
 * Delete this file when the problem is solved.
 */
@TeleOp(name = "Utility: Tag Test", group = "Utility")
public class UtilityTagTest extends LinearOpMode {

    final int RESOLUTION_WIDTH = 1280;
    final int RESOLUTION_HEIGHT = 720;

    @Override
    public void runOpMode() {
        AprilTagProcessor aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .setLensIntrinsics(925.919086365, 925.919086365, 656.336036235, 369.441035915)
                .build();

        VisionPortal portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(aprilTag)
                .build();

        FtcDashboard.getInstance().startCameraStream(portal, 30);
        CameraStreamServer.getInstance().setSource(portal);

        // Same INIT loop style as the capture utility - view "Camera Stream" on the DS here.
        while (opModeInInit()) {
            telemetry.addLine("######## Tag Test ########");
            telemetry.addData(" > Camera state", portal.getCameraState());
            telemetry.addData(" > Camera FPS", portal.getFps());
            telemetry.addData(" > Tag IDs seen", describe(aprilTag));
            telemetry.update();
            sleep(20);
        }

        while (opModeIsActive()) {
            telemetry.addLine("######## Tag Test ########");
            telemetry.addData(" > Camera state", portal.getCameraState());
            telemetry.addData(" > Camera FPS", portal.getFps());
            telemetry.addData(" > Tag IDs seen", describe(aprilTag));
            telemetry.update();
        }

        CameraStreamServer.getInstance().setSource(null);
        FtcDashboard.getInstance().stopCameraStream();
        portal.close();
    }

    private String describe(AprilTagProcessor aprilTag) {
        StringBuilder sb = new StringBuilder();
        for (AprilTagDetection d : aprilTag.getDetections()) {
            if (d instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection s = (AprilTagSingleDetection) d;
                sb.append(String.format(Locale.US, "%d", s.id));
                if (s.ftcPose != null) {
                    sb.append(String.format(Locale.US, " (range %.1f in) ", s.ftcPose.range));
                } else {
                    sb.append(" (no pose) ");
                }
            }
        }
        return sb.length() == 0 ? "none" : sb.toString().trim();
    }
}
