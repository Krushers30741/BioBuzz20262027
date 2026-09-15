package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.SleepAction;

public class SleepStep implements Step{

    private SleepAction sleeper;

    public SleepStep(double sec) {
        this.sleeper = new SleepAction(sec);
    }

    @Override
    public Status runStep(TelemetryPacket packet) {
        while ( sleeper.run(packet) );
        return Status.SUCCESS;
    }

}
