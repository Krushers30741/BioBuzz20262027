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
 * DIAGNOSTIC ONLY - delete when the camera problem is solved.
 *
 * Tries several camera setups so we can find one that works, without redeploying between tests.
 * Press a gamepad1 button (in INIT or after START) to switch setups; the camera restarts each time.
 *
 *   A = NO AprilTag detector, 1280x720, YUY2   (same as the capture utility - should be live)
 *   B = AprilTag detector,    1280x720, YUY2
 *   C = AprilTag detector,     640x480, YUY2
 *   D = AprilTag detector,    1280x720, MJPEG
 *
 * It starts on A. For each setup: open Camera Stream, wave a hand in front of the lens, and note
 * whether it is live or black and what the camera FPS says.
 */
@TeleOp(name = "Utility: Tag Test", group = "Utility")
public class UtilityTagTest extends LinearOpMode {

    private VisionPortal portal;
    private AprilTagProcessor aprilTag;   // null when the setup has no detector
    private String current = "";

    @Override
    public void runOpMode() {
        startSetup('A');

        boolean lastA = false, lastB = false, lastX = false, lastY = false;

        while (!isStopRequested()) {
            boolean a = gamepad1.a, b = gamepad1.b, x = gamepad1.x, y = gamepad1.y;
            if (a && !lastA) startSetup('A');
            if (b && !lastB) startSetup('B');
            if (x && !lastX) startSetup('C');   // X button = setup C
            if (y && !lastY) startSetup('D');
            lastA = a; lastB = b; lastX = x; lastY = y;

            telemetry.addLine("Buttons: A=no detector | B=detector 1280x720 | X=detector 640x480 | Y=detector MJPEG");
            telemetry.addData("Current setup", current);
            telemetry.addData("Camera state", portal.getCameraState());
            telemetry.addData("Camera FPS", portal.getFps());
            telemetry.addData("Tag IDs seen", describe());
            telemetry.update();
            sleep(20);
        }

        stopCurrent();
    }

    private void startSetup(char which) {
        stopCurrent();

        int w = 1280, h = 720;
        VisionPortal.StreamFormat format = VisionPortal.StreamFormat.YUY2;
        boolean detector = true;
        switch (which) {
            case 'A': detector = false; current = "A: no detector, 1280x720, YUY2"; break;
            case 'B': current = "B: detector, 1280x720, YUY2"; break;
            case 'C': w = 640; h = 480; current = "C: detector, 640x480, YUY2"; break;
            case 'D': format = VisionPortal.StreamFormat.MJPEG; current = "D: detector, 1280x720, MJPEG"; break;
        }

        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(w, h))
                .setStreamFormat(format);

        if (detector) {
            aprilTag = new AprilTagProcessor.Builder()
                    .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                    .build();
            builder.addProcessor(aprilTag);
        }

        portal = builder.build();
        FtcDashboard.getInstance().startCameraStream(portal, 30);
        CameraStreamServer.getInstance().setSource(portal);
    }

    private void stopCurrent() {
        if (portal != null) {
            CameraStreamServer.getInstance().setSource(null);
            FtcDashboard.getInstance().stopCameraStream();
            portal.close();
            portal = null;
        }
        aprilTag = null;
    }

    private String describe() {
        if (aprilTag == null) return "(no detector in this setup)";
        StringBuilder sb = new StringBuilder();
        for (AprilTagDetection d : aprilTag.getDetections()) {
            if (d instanceof AprilTagSingleDetection) {
                sb.append(String.format(Locale.US, "%d ", ((AprilTagSingleDetection) d).id));
            }
        }
        return sb.length() == 0 ? "none" : sb.toString().trim();
    }
}
