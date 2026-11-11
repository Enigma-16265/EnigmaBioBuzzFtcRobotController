package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp
public class Stupidtest extends OpMode {

    @Override
    public void init() {

    }
    @Override
    public void loop() {
        telemetry.addLine("sloth is very awesome");
        telemetry.update();
    }
}
