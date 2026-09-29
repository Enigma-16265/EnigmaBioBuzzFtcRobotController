package org.firstinspires.ftc.teamcode.tests;

import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.enums.BallTypes;

@TeleOp
public class FusionShooterTest extends OpMode {
    public FusionShooter fusionShooter;
    //private Gamepad gamepad1;
    private TelemetryManager telemetryM;

    @Override
    public void init() {
        fusionShooter = new FusionShooter(hardwareMap, telemetry, telemetryM, gamepad1);
    }

    @Override
    public void loop() {
        fusionShooter.runShooterPID();

        if(gamepad1.leftBumperWasPressed())
            fusionShooter.adjustSpeed(-100);
        if(gamepad1.rightBumperWasPressed())
            fusionShooter.adjustSpeed(100);
        if(gamepad1.aWasPressed())
            fusionShooter.setTargetType(BallTypes.NECTAR);
        if(gamepad1.bWasPressed())
            fusionShooter.setTargetType(BallTypes.POLLEN);
        if(gamepad1.yWasPressed())
            fusionShooter.toggleAutoDetect();

        fusionShooter.addTelemetry();
        telemetry.update();
        //telemetryM.update();
    }
}
