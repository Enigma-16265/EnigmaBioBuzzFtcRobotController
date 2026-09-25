package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.enums.BallTypes;

public class FusionShooterTest extends OpMode {
    public FusionShooter fusionShooter;
    private Gamepad gamepad1;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    @Override
    public void init() {
        fusionShooter = new FusionShooter(hardwareMap, telemetry, gamepad1);
    }

    @Override
    public void loop() {
        fusionShooter.runShooterPID();

        if(gamepad1.leftBumperWasPressed())
            fusionShooter.adjustHood(-0.05);
        if(gamepad1.rightBumperWasPressed())
            fusionShooter.adjustHood(0.05);
        if(gamepad1.a)
            fusionShooter.setTargetType(BallTypes.NECTAR);
        if(gamepad1.b)
            fusionShooter.setTargetType(BallTypes.POLLEN);
    }
}
