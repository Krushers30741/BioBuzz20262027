package org.firstinspires.ftc.teamcode;

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamServer;
import com.acmerobotics.dashboard.FtcDashboard;


import java.util.Locale;

@TeleOp(name = "Utility: Camera Frame Capture", group = "Utility")
public class UtilityCameraFrameCapture extends LinearOpMode
{
    /*
     * EDIT THESE PARAMETERS AS NEEDED
     */
    final boolean USING_WEBCAM = true;
    final BuiltinCameraDirection INTERNAL_CAM_DIR = BuiltinCameraDirection.BACK;

    // Change these from 640x480 to 320x240 to see if the feed wakes up
    final int RESOLUTION_WIDTH = 1280;
    final int RESOLUTION_HEIGHT = 720;
   // final int RESOLUTION_WIDTH = 640;
    //final int RESOLUTION_HEIGHT = 480;

    // Internal state
    boolean lastX;
    int frameCount;
    long capReqTime;

    @Override
    public void runOpMode()
    {
        VisionPortal portal;

        if (USING_WEBCAM)
        {
            portal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                    .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                    .build();

        }
        else
        {
            portal = new VisionPortal.Builder()
                    .setCamera(INTERNAL_CAM_DIR)
                    .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                    .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                    .build();
        }
        // Route the stream directly to FTC Dashboard (30 Max FPS)
        FtcDashboard.getInstance().startCameraStream(portal, 30);
        CameraStreamServer.getInstance().setSource(portal);
        // ====================================================================
        // PHASE 1: INIT LOOP (Allows you to view "Camera Stream" on DS)
        // ====================================================================
        while (opModeInInit())
        {
            telemetry.addLine("######## Camera Capture Utility ########");
            telemetry.addLine(String.format(Locale.US, " > Resolution: %dx%d", RESOLUTION_WIDTH, RESOLUTION_HEIGHT));
            telemetry.addLine(" > STATUS: INITIALIZED (Ready for Stream)");
            telemetry.addLine(" > Press PLAY on DS to start frame capture mode");
            telemetry.addData(" > Camera Status", portal.getCameraState());
            telemetry.update();

            sleep(20); // Small delay to preserve CPU resources
        }

        // ====================================================================
        // PHASE 2: ACTIVE LOOP (Runs after you click the Play button)
        // ====================================================================
        while (opModeIsActive())
        {
            boolean x = gamepad1.x;

            if (x && !lastX)
            {
                portal.saveNextFrameRaw(String.format(Locale.US, "CameraFrameCapture-%06d", frameCount++));
                capReqTime = System.currentTimeMillis();
            }

            lastX = x;

            telemetry.addLine("######## Camera Capture Utility ########");
            telemetry.addLine(String.format(Locale.US, " > Resolution: %dx%d", RESOLUTION_WIDTH, RESOLUTION_HEIGHT));
            telemetry.addLine(" > Press X (or Square) to capture a frame");
            telemetry.addData(" > Camera Status", portal.getCameraState());

            if (capReqTime != 0)
            {
                telemetry.addLine("\nCaptured Frame!");
            }

            if (capReqTime != 0 && System.currentTimeMillis() - capReqTime > 1000)
            {
                capReqTime = 0;
            }

            telemetry.update();
        }
        CameraStreamServer.getInstance().setSource(null);
        FtcDashboard.getInstance().stopCameraStream();
        // Clean up when stopped
        portal.close();
    }
}
