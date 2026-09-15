package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

public interface Step {
    enum Status { RUNNING, SUCCESS, FAILURE }
    Status runStep(TelemetryPacket packet);
}
