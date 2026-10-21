package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

/**
 * Intake subsystem — placeholder while the team decides on the BIOBUZZ scoring mechanism.
 * Right now it just hardware-maps a single motor and can spin it at a given power. Add real
 * behavior here (sensor checks, sequencing, etc.) once the mechanism design is settled.
 */
public class colorSensor {

    private final NormalizedColorSensor colorSensor;
    private NormalizedRGBA colors;

    public colorSensor(HardwareMap hardwareMap) {
        // TODO: make sure your hardware config has a motor with this name (or change it below)
        //   see https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/index.html
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSen0sor"); //Expansion Hub 0
        colorSensor.setGain(2.0f);
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }
        colors = colorSensor.getNormalizedColors();
    }

    public void getColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);
        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];
        String detectedcolor = "Unknown";
        if (hue > 340 || hue < 20) {
            detectedcolor = "Red";
        } else if (hue > 30 && hue < 70) {
            detectedcolor = "yellow";
        } else if (hue > 10 && hue < 150) {
            detectedcolor = "green";
        } else if (hue > 190 && hue < 250) {
            detectedcolor = "blue";

        }
        telemetry.addData("Detected Color", detectedcolor);
        telemetry.addData("hue (0-360)", "%.1f", hue);
        telemetry.addData("saturation (0-1)", "%.3f", saturation);
        telemetry.addData("value (0-1)", "%.3f", value);
        telemetry.addLine()
                .addData("R","%.3f", colors.red)
                .addData("G","%.3f", colors.green)
                .addData("B","%.3f", colors.blue)
                .addData("A","%.3f", colors.alpha);
                telemetry.update();
    }
}
