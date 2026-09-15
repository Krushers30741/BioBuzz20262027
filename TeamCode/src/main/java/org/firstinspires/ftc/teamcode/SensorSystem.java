package org.firstinspires.ftc.teamcode;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

/**
 * Wraps the indicator light + color/distance sensors. Carried over from last year's
 * SensorSystem, with the game-piece color check updated for this year.
 *
 * <p><b>Changed from last year:</b> DECODE's game pieces were purple/green "artifacts" —
 * this year's are yellow POLLEN and alliance-colored (red/blue) NECTAR, so the color
 * thresholds below are placeholders that need to be tuned against your actual color
 * sensor and real game pieces once you have them in hand.</p>
 */
public class SensorSystem {

    protected final Servo light;
    protected final ColorSensor color;
    protected final DistanceSensor fl;
    protected final DistanceSensor fr;
    protected boolean canShoot;
    protected double timer;

    public static final double LIGHTRED = 0.3;
    public static final double LIGHTYELLOW = 0.388;
    public static final double LIGHTGREEN = 0.477;
    public static final double LIGHTBLUE = 0.611;
    public static final double LIGHTPURPLE = 0.722;

    protected SensorSystem(HardwareMap hw, Telemetry telemetry) {
        light = hw.get(Servo.class, "light");
        color = hw.get(ColorSensor.class, "color");
        fl = hw.get(DistanceSensor.class, "flDistance");
        fr = hw.get(DistanceSensor.class, "frDistance");
        light.setPosition(0);
    }

    public boolean setLight(double lightValue) {
        light.setPosition(lightValue);
        return false;
    }

    // TODO: tune these thresholds against real POLLEN (yellow) and NECTAR (red/blue) once you have them.
    public boolean isPollen() {
        return color.red() > 1500 && color.green() > 1500;
    }

    public boolean isRedNectar() {
        return color.red() > 1500;
    }

    public boolean isBlueNectar() {
        return color.blue() > 1500;
    }

    public void updateIndicatorLights(double currentTime) {
        light.getPosition();
        if (isPollen()) {
            setLight(LIGHTYELLOW);
            timer = currentTime;
        } else if (isRedNectar()) {
            setLight(LIGHTRED);
            timer = currentTime;
        } else if (isBlueNectar()) {
            setLight(LIGHTBLUE);
            timer = currentTime;
        }
    }

    public void setCanShoot(boolean canShoot) {
        this.canShoot = canShoot;
    }
}
