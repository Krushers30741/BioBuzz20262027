package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class TimeOutAction implements Step {
    private final Step inner;
    private final double timeoutSec;
    private final Clock clock;
    private double start;

    public TimeOutAction(Step inner, double timeoutSec, Clock clock) {
        this.inner = inner;
        this.timeoutSec = timeoutSec;
        this.clock = clock;
        start = -1.0;
    }

    @Override
    public Step.Status runStep(TelemetryPacket packet) {
        double now = clock.now();
        if (start < 0) {
            start = now;
        }
        Step.Status st = inner.runStep(packet);
        if (st == Status.SUCCESS) {
            return Status.SUCCESS;
        }
        if (now - start >= timeoutSec) {
            return Status.FAILURE;
        }
        return st;
    }

}
