package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

/**
 * Visualizes the same path AutoDrive.java drives on the real robot — no hardware needed.
 *
 * <p>To run it: open this file in Android Studio and click the green "Run" arrow next to
 * {@code main()} (or right-click the file and choose Run). A small window pops up showing
 * the robot driving the path on a virtual field.
 *
 * <p><b>Note:</b> the bot size/speed numbers below (track width, max velocity, max
 * acceleration) were carried over from last year's tuned {@code MecanumDrive.Params} in
 * TeamCode — they describe last year's chassis, not necessarily this year's BIOBUZZ robot.
 * Once the new chassis is built and those constants are re-tuned, update the numbers here
 * to match so this visualizer stays accurate.
 */
public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // maxVel (in/s), maxAccel (in/s^2), maxAngVel (rad/s), maxAngAccel (rad/s^2), track width (in)
                .setConstraints(30, 50, Math.PI, Math.PI, 13.3)
                .build();

        // Mirrors TeamCode's AutoDrive.java: drive forward `forwardInches` and stop.
        double forwardInches = 24;
        myBot.runAction(myBot.getDrive()
                .actionBuilder(new Pose2d(0, 0, 0))
                .lineToX(forwardInches)
                .build());

        // GRID is a generic field background that always exists. If MeepMeep adds a
        // BIOBUZZ-specific field image later, swap it in here.
        meepMeep.setBackground(MeepMeep.Background.GRID_GRAY)
                .setDarkMode(true)
                .addEntity(myBot)
                .start();
    }
}
