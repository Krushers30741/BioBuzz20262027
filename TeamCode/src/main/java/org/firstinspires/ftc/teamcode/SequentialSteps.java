package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import java.util.Arrays;
import java.util.List;

public class SequentialSteps implements Action {
    private final List<Step> steps;
    private int i = 0;

    public SequentialSteps(Step... steps) {
        this.steps = Arrays.asList(steps);
    }

    @Override public boolean run(TelemetryPacket packet) {
        while (i < steps.size()) {
            Step.Status s = steps.get(i).runStep(packet);
            if (s == Step.Status.RUNNING) return true;
            if (s == Step.Status.FAILURE) {
                packet.put("Step failed:", steps.get(i).getClass().toString());
                return false; // abort sequence
            }
            // else SUCCESS for this step; advance to next and loop
            i++;
        }
        return false; // all done
    }

}
