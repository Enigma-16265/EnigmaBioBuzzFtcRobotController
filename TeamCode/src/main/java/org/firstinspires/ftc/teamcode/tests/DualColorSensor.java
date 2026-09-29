package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.teamcode.enums.BallColors;


@TeleOp
public class DualColorSensor {
    private HardwareMap hardwareMap;
    private RevColorSensorV3 colorSensor1;
    public BallColors currentColorGuess = BallColors.UNKNOWN;
    public BallColors lastSeen = BallColors.UNKNOWN;
    private float hue;

    NormalizedRGBA colors;

    float r, g, b;


    private boolean xBetween(double x, double lowValue, double highValue) {
        return (x >= lowValue && x <= highValue);
    }

    private BallColors colorDetection() {
        if((hue < 50 || hue > 330) && hue != 0) {
            return BallColors.RED;
        }
        if(hue < 270 && hue > 170) {
            return BallColors.BLUE;
        }
        if(hue < 120 && hue > 80) {
            return BallColors.YELLOW;
        }
        else {
            return BallColors.UNKNOWN;
        }



        /*
        if (r > g * 1.4 && r > b * 1.4) {
            return BallColors.RED;
        }
        else if (b > r * 1.4 && b > g * 1.2) {
            return BallColors.BLUE;
        }
        else if (r > b * 1.3 && g > b * 1.3) {
            return BallColors.YELLOW;
        }
        else {
            return BallColors.UNKNOWN;
        }

         */


    }

//    private void telemetry() {
//        telemetry.addData("current guess", currentColorGuess);
//        telemetry.addData("last seen", lastSeen);
//        telemetry.addData("hue", hue);
//
//        telemetry.update();
//    }

    public void updateRGB() {
        colors = colorSensor1.getNormalizedColors();

        hue = (float) JavaUtil.colorToHue(colors.toColor());


        r = colors.red;
        g = colors.green;
        b = colors.blue;

        currentColorGuess = colorDetection();
        if(currentColorGuess != BallColors.UNKNOWN) {
            lastSeen = currentColorGuess;
        }
    }

    public DualColorSensor(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        colorSensor1 = hardwareMap.get(RevColorSensorV3.class, "colorSensor1");
    }
}
