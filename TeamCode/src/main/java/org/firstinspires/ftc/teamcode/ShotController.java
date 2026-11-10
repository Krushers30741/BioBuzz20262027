package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.function.BooleanSupplier;

/**
 * Spins up the shooter and feeds the balls, shared by TeleopDrive, Spot1 and Spot2.
 *
 * <p>Use it like this:
 * <pre>
 *   shot = new ShotController(shooter, conveyor);
 *   shot.start();          // begin spinning up
 *   shot.update();         // call EVERY loop - checks the wheels and runs the conveyor
 *   shot.stop();           // stop shooter and conveyor
 * </pre>
 * An autonomous can instead call {@code shot.run(seconds, this::opModeIsActive)}, which does
 * start / update-until-time-is-up / stop in one call.</p>
 *
 * <p>How it feeds: the conveyor stays off until BOTH wheels are at speed and have stayed there for
 * SETTLE_MS. Then it runs. When a ball pulls the slower wheel DIP_TRIGGER_PERCENT or more below
 * SHOOTER_SPEED, the target is raised by KICK_PERCENT until the wheels are back at SHOOTER_SPEED;
 * with PAUSE_AFTER_EACH_BALL the conveyor also stops until the speed is back and settled.</p>
 *
 * <p>All the numbers below are {@code @Config}: tune them live in FTC Dashboard under
 * ShotController. They reset when the app restarts or code is redeployed.</p>
 */
@Config
public class ShotController {

    // Target wheel speed in ticks/second.
    public static double SHOOTER_SPEED = 745;
    // The wheels must stay at speed this long (ms) before the conveyor feeds a ball.
    public static double SETTLE_MS = 150;
    // Raise the target by KICK_PERCENT when a ball drags the speed down by DIP_TRIGGER_PERCENT.
    // Set KICK_PERCENT to 0 to turn the kick off.
    public static double KICK_PERCENT = 5;
    public static double DIP_TRIGGER_PERCENT = 3;
    // true = conveyor STOPS after each ball until the speed is back and settled. false = nonstop.
    public static boolean PAUSE_AFTER_EACH_BALL = true;
    // Safety: if the speed has not recovered this long (ms) after a dip, feed the next ball anyway.
    public static double PAUSE_TIMEOUT_MS = 500;
    // Safety: if the wheels have not reached speed this long (ms) after start(), feed anyway so a
    // shot can never wait forever on a wheel that will not get up to speed.
    public static double SPINUP_TIMEOUT_MS = 3000;

    private final Shooter shooter;
    private final Conveyor conveyor;

    private boolean running = false;       // start() called, not stopped yet
    private boolean feeding = false;       // conveyor has been started for this shot
    private boolean paused = false;        // conveyor stopped, waiting for speed to recover
    private boolean wasAtSpeed = false;
    private boolean inDip = false;
    private int dipCount = 0;
    private double lowestInDip = 0;
    private double lastDipLowSpeed = 0;
    private double lastRecoveryMs = 0;
    private final ElapsedTime settleTimer = new ElapsedTime();
    private final ElapsedTime dipTimer = new ElapsedTime();
    private final ElapsedTime spinUpTimer = new ElapsedTime();

    public ShotController(Shooter shooter, Conveyor conveyor) {
        this.shooter = shooter;
        this.conveyor = conveyor;
    }

    /** Begin spinning up. The conveyor stays off until the wheels are at speed. */
    public void start() {
        shooter.setForwardDirection();
        shooter.startVelocitySpinUp(SHOOTER_SPEED);
        conveyor.setDirectionForward();
        conveyor.setPower(0.0);
        running = true;
        feeding = false;
        paused = false;
        wasAtSpeed = false;
        inDip = false;
        dipCount = 0;
        spinUpTimer.reset();
    }

    /** Call every loop. Does nothing unless start() has been called. */
    public void update() {
        if (!running) {
            return;
        }
        boolean atSpeed = shooter.atSpeed();
        double lowest = Math.min(shooter.getLeftShooterMotorVelocity(),
                shooter.getRightShooterMotorVelocity());

        if (atSpeed && !wasAtSpeed) {
            settleTimer.reset();            // just got to speed - start the settle clock
        }
        wasAtSpeed = atSpeed;

        if (!feeding) {
            // Start feeding once at speed and settled (or the spin-up safety timeout passes).
            boolean ready = atSpeed && settleTimer.milliseconds() >= SETTLE_MS;
            if (ready || spinUpTimer.milliseconds() >= SPINUP_TIMEOUT_MS) {
                feeding = true;
                conveyor.setPower(1.0);
            }
            return;
        }

        // A ball pulled the speed down: kick the target up a few percent until the wheels are
        // back at SHOOTER_SPEED, then go back to the normal target.
        if (!inDip && lowest < SHOOTER_SPEED * (1 - DIP_TRIGGER_PERCENT / 100.0)) {
            inDip = true;
            dipCount++;
            dipTimer.reset();
            lowestInDip = lowest;
            if (PAUSE_AFTER_EACH_BALL) {
                conveyor.setPower(0);       // hold the next ball until speed is back
                paused = true;
            }
            if (KICK_PERCENT > 0) {
                shooter.startVelocitySpinUp(SHOOTER_SPEED * (1 + KICK_PERCENT / 100.0));
            }
        }
        if (inDip) {
            lowestInDip = Math.min(lowestInDip, lowest);
            if (lowest >= SHOOTER_SPEED) {
                inDip = false;
                lastDipLowSpeed = lowestInDip;
                lastRecoveryMs = dipTimer.milliseconds();
                shooter.startVelocitySpinUp(SHOOTER_SPEED);
                settleTimer.reset();        // settle clock for the next ball
            }
        }
        // Resume feeding once recovered and settled, if pause mode was switched off, or if the
        // speed has not come back within PAUSE_TIMEOUT_MS.
        boolean recovered = !inDip && atSpeed && settleTimer.milliseconds() >= SETTLE_MS;
        boolean timedOut = dipTimer.milliseconds() >= PAUSE_TIMEOUT_MS;
        if (paused && (!PAUSE_AFTER_EACH_BALL || recovered || timedOut)) {
            conveyor.setPower(1.0);
            paused = false;
        }
    }

    /** Stop the shooter and the conveyor. */
    public void stop() {
        shooter.setPower(0);
        conveyor.setPower(0);
        cancel();
    }

    /**
     * Stop this controller's logic WITHOUT touching the motors. TeleopDrive uses it when another
     * control (reverse) takes over the conveyor.
     */
    public void cancel() {
        running = false;
        feeding = false;
        paused = false;
        inDip = false;
    }

    /**
     * Autonomous helper: start, keep updating until seconds have passed (or isActive turns
     * false), then stop. Pass {@code this::opModeIsActive} from your OpMode. The time includes
     * the spin-up.
     */
    public void run(double seconds, BooleanSupplier isActive) {
        long end = System.nanoTime() + (long) (seconds * 1e9);
        start();
        while (isActive.getAsBoolean() && System.nanoTime() < end) {
            update();
        }
        stop();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFeeding() {
        return feeding && !paused;      // conveyor is actually moving right now
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isKickActive() {
        return inDip;
    }

    public int getDipCount() {
        return dipCount;
    }

    public double getLastDipLowSpeed() {
        return lastDipLowSpeed;
    }

    public double getLastRecoveryMs() {
        return lastRecoveryMs;
    }
}
