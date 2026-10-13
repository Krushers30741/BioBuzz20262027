package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * EXERCISE 13: PID Playground
 *
 * Before we use PID on the real robot, practice on a pretend one. This OpMode simulates a
 * motor-driven arm or turret in code: it has a position, a speed, a bit of friction, and a small
 * delay (like a camera has). Nothing on the robot moves, so there's nothing to break.
 *
 * Your job (two parts):
 *  1) Finish PIDController.java (4 TODOs - P, I, D, and adding them up).
 *  2) Tune it. Open FTC Dashboard in a browser (the Control Hub's Wi-Fi, then
 *     http://192.168.43.1:8080/dash), find "Exercise13_PIDPlayground" under Configuration, and
 *     change kP, kI and kD WHILE this OpMode runs. Watch the graph: "target" vs "position".
 *
 * Gamepad 1 (in INIT or after START): A = target 90   B = target 0   X = target -90   Y = reset
 *
 * Tuning order:
 *   1. kI = 0, kD = 0. Raise kP until it gets there fast but overshoots a little.
 *   2. Raise kD until the overshoot goes away.
 *   3. Only add a tiny kI if it settles a little short of the target.
 */
@Config
@TeleOp(name = "Exercise 13 - PID Playground", group = "Exercises")
public class Exercise13_PIDPlayground extends LinearOpMode {

    // Dashboard can change these live. They must be public static (and not final).
    public static double kP = 0.02;
    public static double kI = 0.0;
    public static double kD = 0.0;

    // The pretend system. Change these to make it harder.
    public static double POWER_TO_ACCEL = 900.0;   // how hard a full-power push accelerates it
    public static double FRICTION = 4.0;           // how quickly it slows down by itself
    public static int DELAY_LOOPS = 3;             // how late the "sensor" is (like camera lag)

    private double position = 0;   // degrees
    private double velocity = 0;   // degrees per second
    private double target = 0;
    private final java.util.ArrayDeque<Double> pending = new java.util.ArrayDeque<>();

    @Override
    public void runOpMode() {
        PIDController pid = new PIDController(kP, kI, kD);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        boolean lastA = false, lastB = false, lastX = false, lastY = false;
        long lastNanos = System.nanoTime();

        telemetry.addLine("Exercise 13 ready. Open Dashboard to tune; A/B/X set the target.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a && !lastA) target = 90;
            if (gamepad1.b && !lastB) target = 0;
            if (gamepad1.x && !lastX) target = -90;
            if (gamepad1.y && !lastY) { position = 0; velocity = 0; target = 0; pid.reset(); pending.clear(); }
            lastA = gamepad1.a; lastB = gamepad1.b; lastX = gamepad1.x; lastY = gamepad1.y;

            long now = System.nanoTime();
            double dt = Math.min((now - lastNanos) / 1e9, 0.05);
            lastNanos = now;

            // Read the gains from Dashboard each loop, so changes take effect immediately.
            pid.setGains(kP, kI, kD);

            double error = target - position;
            double power = Math.max(-1.0, Math.min(1.0, pid.update(error)));

            // The "motor" doesn't act on this power until DELAY_LOOPS loops later.
            pending.addLast(power);
            double appliedPower = pending.size() > DELAY_LOOPS ? pending.removeFirst() : 0;

            // Simple physics: push makes it speed up, friction slows it down.
            velocity += (POWER_TO_ACCEL * appliedPower - FRICTION * velocity) * dt;
            position += velocity * dt;

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("target", target);
            packet.put("position", position);
            packet.put("error", error);
            packet.put("power", power);
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("target (deg)", target);
            telemetry.addData("position (deg)", position);
            telemetry.addData("error (deg)", error);
            telemetry.addData("power", power);
            telemetry.update();

            sleep(20);
        }
    }
}
