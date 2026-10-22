package org.firstinspires.ftc.teamcode;


import android.graphics.Color;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ColorSensor {
    private final NormalizedColorSensor colorSensor;
    private final Telemetry telemetry;

    public ColorSensor(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        colorSensor.setGain(2.0f);
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }
    }

    public void getColor() {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        if (colors == null) {
            telemetry.addData("Detected Color", "Sensor data not found");
            telemetry.update();
            return;
        }

        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);

        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];
        String detectedcolor = "Unknown";

        if (hue > 20 && hue < 40) {
            detectedcolor = "Red";
        } else if (hue > 80 && hue < 100) {
            detectedcolor = "Yellow";

        } else if (hue > 190 && hue < 250) {
            detectedcolor = "Blue";
        }

        telemetry.addData("Detected Color", detectedcolor);
        telemetry.addData("hue (0-360)", "%.1f", hue);
        telemetry.addData("saturation (0-1)", "%.3f", saturation);
        telemetry.addData("value (0-1)", "%.3f", value);
        telemetry.addLine()
                .addData("R", "%.3f", colors.red)
                .addData("G", "%.3f", colors.green)
                .addData("B", "%.3f", colors.blue)
                .addData("A", "%.3f", colors.alpha);
        telemetry.update();
    }
}
