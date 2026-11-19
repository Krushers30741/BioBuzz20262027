package org.firstinspires.ftc.teamcode;

/*
 * ShooterSpeedTable
 * -----------------
 * Turns "how far away is the goal?" into "how fast should the flywheel spin?"
 *
 * How it works, in kid terms:
 *   We test-shoot from a few distances and write down the flywheel speed that
 *   scored best at each one. That list is our TABLE.
 *
 *   When the robot is at a distance we did NOT test (say 52 inches, but we only
 *   tested 48 and 60), we draw a straight line between the two closest test
 *   points and read the speed off that line. That is called LINEAR INTERPOLATION.
 *
 *   If the robot is closer than our closest test, or farther than our farthest
 *   test, we just use the speed from the end of the table. That is called
 *   CLAMPING. It keeps us from guessing wildly outside what we actually tested.
 *
 * Units: distance in INCHES, speed in TICKS PER SECOND (what DcMotorEx.setVelocity uses).
 *
 * This class has no FTC hardware in it, so it is easy to test and hard to break.
 */
public class ShooterSpeedTable {

    // Distances we tested, smallest to largest (inches).
    private final double[] distances;
    // Best flywheel speed at each of those distances (ticks per second).
    private final double[] speeds;

    /**
     * Build a table from rows of {distanceInches, speedTicksPerSecond}.
     * Rows must be in order from closest to farthest, with no repeated distances.
     *
     * Example:
     *   new ShooterSpeedTable(new double[][] {
     *       {24, 1200},
     *       {48, 1450},
     *       {72, 1700},
     *   });
     */
    public ShooterSpeedTable(double[][] rows) {
        if (rows == null || rows.length == 0) {
            throw new IllegalArgumentException("Shooter table needs at least one row.");
        }
        distances = new double[rows.length];
        speeds = new double[rows.length];

        for (int i = 0; i < rows.length; i++) {
            if (rows[i].length != 2) {
                throw new IllegalArgumentException("Row " + i + " needs exactly 2 numbers: {distance, speed}.");
            }
            distances[i] = rows[i][0];
            speeds[i] = rows[i][1];

            // Catch typos early: distances must keep getting bigger.
            if (i > 0 && distances[i] <= distances[i - 1]) {
                throw new IllegalArgumentException("Row " + i + " distance (" + distances[i]
                        + ") must be bigger than the row before it (" + distances[i - 1] + ").");
            }
        }
    }

    /** Returns the flywheel speed (ticks per second) for a distance in inches. */
    public double speedFor(double distanceInches) {
        int last = distances.length - 1;

        // CLAMP: closer than our closest test point -> use the first speed.
        if (distanceInches <= distances[0]) {
            return speeds[0];
        }
        // CLAMP: farther than our farthest test point -> use the last speed.
        if (distanceInches >= distances[last]) {
            return speeds[last];
        }

        // Find the two test points we are between.
        for (int i = 0; i < last; i++) {
            double nearDist = distances[i];
            double farDist = distances[i + 1];

            if (distanceInches <= farDist) {
                // How far along are we from nearDist to farDist? 0.0 = at near, 1.0 = at far.
                double fraction = (distanceInches - nearDist) / (farDist - nearDist);

                // Move that same fraction of the way from the near speed to the far speed.
                double nearSpeed = speeds[i];
                double farSpeed = speeds[i + 1];
                return nearSpeed + fraction * (farSpeed - nearSpeed);
            }
        }

        // We should never get here, but return the last speed just in case.
        return speeds[last];
    }

    /** Closest distance in the table (inches). Handy for telemetry. */
    public double minDistance() {
        return distances[0];
    }

    /** Farthest distance in the table (inches). Handy for telemetry. */
    public double maxDistance() {
        return distances[distances.length - 1];
    }
}
