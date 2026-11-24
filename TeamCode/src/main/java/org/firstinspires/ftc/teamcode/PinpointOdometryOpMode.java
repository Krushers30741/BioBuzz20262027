package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Pinpoint Odometry", group = "Odometry")
public class PinpointOdometryOpMode extends LinearOpMode {

    private GoBildaPinpointDriver odo;

    // No constructor here on purpose: the Driver Station can only open an OpMode that has none
    // (or one with no arguments). The old one took (HardwareMap, Telemetry), so it wouldn't open.

    @Override
    public void runOpMode() {
        odo = setUp(hardwareMap);

        telemetry.addLine("Pinpoint Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Pull the newest X, Y, and heading data from the board
            odo.update();

            Pose2D pos = odo.getPosition();
            double xInches = pos.getX(DistanceUnit.INCH);
            double yInches = pos.getY(DistanceUnit.INCH);
            double headingDeg = pos.getHeading(AngleUnit.DEGREES);

            telemetry.addData("Status", odo.getDeviceStatus());
            telemetry.addData("X (in)", "%.2f", xInches);
            telemetry.addData("Y (in)", "%.2f", yInches);
            telemetry.addData("Heading (deg)", "%.2f", headingDeg);
            telemetry.update();
        }
    }

    /**
     * Sets up the Pinpoint with our pod settings. Used by this OpMode and by
     * FTC30741Base.initOdometry(), so both use the same numbers. Keep the robot still while it runs.
     */
    public static GoBildaPinpointDriver setUp(HardwareMap hardwareMap) {
        // 1. Map the Pinpoint (plug into I2C Port 1, 2, or 3 — NOT Port 0)
        GoBildaPinpointDriver odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        // 2. Set pod offsets from the center of your robot (in millimeters)
        // xOffset: sideways distance of the forward pod (Left is +, Right is -)
        // yOffset: forward/back distance of the strafe pod (Forward is +, Back is -)
        odo.setOffsets(-84.0, -168.0, DistanceUnit.MM);

        // 3. Pick your exact goBILDA pod model (or change to goBILDA_SWINGARM_POD)
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        // 4. Set encoder directions (flip to REVERSED if X or Y counts backward)
        odo.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        // 5. Reset position and calibrate the internal IMU (keep robot still!)
        odo.resetPosAndIMU();
        return odo;
    }
}

