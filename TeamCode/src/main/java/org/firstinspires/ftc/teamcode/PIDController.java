package org.firstinspires.ftc.teamcode;

/**
 * A reusable PID controller. You will finish writing it in Exercise 13, then use it in
 * Exercise 14 (and anywhere else the robot needs to "aim" at something).
 *
 * The idea: every loop you tell it the ERROR (how far you are from where you want to be) and it
 * hands back a motor power. Three parts decide that power:
 *
 *   P (proportional): push in proportion to how far off you are.      kP * error
 *   I (integral):     push harder the longer you've been off.         kI * (error added up over time)
 *   D (derivative):   push against how fast the error is changing.    kD * (change in error / time)
 *
 *   power = P + I + D
 *
 * Error = target - where you are right now. Positive error means "go in the positive direction".
 */
public class PIDController {

    private double kP, kI, kD;

    // Stops the I part from growing without limit while the robot is stuck ("windup").
    private double integralLimit = 1.0;

    private double integral = 0;
    private double lastError = 0;
    private long lastNanos = 0;
    private boolean first = true;

    public PIDController(double kP, double kI, double kD) {
        setGains(kP, kI, kD);
    }

    /** Lets you change the gains while the OpMode is running (Dashboard tuning). */
    public void setGains(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public void setIntegralLimit(double limit) {
        this.integralLimit = Math.abs(limit);
    }

    /** Forget everything - call this when a new move starts. */
    public void reset() {
        integral = 0;
        lastError = 0;
        first = true;
    }

    /**
     * @param error target minus current position
     * @return the motor power this error asks for (NOT yet limited to -1..1)
     */
    public double update(double error) {
        long now = System.nanoTime();
        // dt = seconds since the last call. (Provided for you.)
        double dt = first ? 0 : (now - lastNanos) / 1e9;
        lastNanos = now;

        // TODO 1 (P): the proportional part is the error times kP.
        double pTerm = 0;

        // TODO 2 (I): add error * dt to integral, then keep integral between
        //   -integralLimit and +integralLimit (Math.max / Math.min).
        //   Then the integral part is kI * integral.
        double iTerm = 0;

        // TODO 3 (D): the derivative is how much the error changed, divided by dt:
        //   (error - lastError) / dt.  Only do this if dt > 0 (the first call has no dt).
        //   The derivative part is kD times that.
        double dTerm = 0;

        lastError = error;
        first = false;

        // TODO 4: add the three parts together and return the result.
        return 0;
    }
}
