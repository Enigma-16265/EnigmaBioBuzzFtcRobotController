package org.firstinspires.ftc.teamcode.tuners;

import com.arcrobotics.ftclib.controller.PIDController;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
@TeleOp
public class ShooterPIDTuner extends OpMode {
    private TelemetryManager telemetryM;
    private PIDController pollenController, nectarController;
    private DcMotorEx pollenShooter, nectarShooter;
    private Servo pollenHood, nectarHood;
    public int pollenTargetSpeed = 800, nectarTargetSpeed = 1100;
    public static double pP = 0.0, pI = 0.0, pD = 0.0;
    public static double nP = 0.0, nI = 0.0, nD = 0.0;
    public static double pollenHoodPos = 0.5, nectarHoodPos = 0.5;
    public static int shooterOn = 0;

    public boolean isShooterOn() {
        return shooterOn == 1;
    }

    public void runPollenShooterPID() {
        double currentVelocity = pollenShooter.getVelocity();

        pollenController.setPID(pP, pI, pD);
        double shooterPid = pollenController.calculate(currentVelocity, pollenTargetSpeed);

        shooterPid = Math.max(shooterPid, -0.2);
        pollenShooter.setPower(shooterPid);
    }
    public void runNectarShooterPID() {
        double currentVelocity = nectarShooter.getVelocity();

        nectarController.setPID(nP, nI, nD);
        double shooterPid = nectarController.calculate(currentVelocity, nectarTargetSpeed);

        shooterPid = Math.max(shooterPid, -0.2);
        nectarShooter.setPower(shooterPid);
    }

    public void setHoodPoses() {
        pollenHood.setPosition(pollenHoodPos);
        nectarHood.setPosition(nectarHoodPos);
    }

    @Override
    public void init() {
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);

        pollenShooter = hardwareMap.get(DcMotorEx.class, "pollenShooter");
        nectarShooter = hardwareMap.get(DcMotorEx.class, "nectarShooter");

        pollenHood = hardwareMap.get(Servo.class, "pollenHood");
        nectarHood = hardwareMap.get(Servo.class, "nectarHood");
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        if(isShooterOn()) {
            runPollenShooterPID();
            runNectarShooterPID();
        }

        telemetryM.addData("pollen hood", pollenHood.getPosition());
        telemetryM.addData("nectar hood", nectarHood.getPosition());
        telemetryM.addData("pollen shooter vel", pollenShooter.getVelocity());
        telemetryM.addData("nectar shooter vel", nectarShooter.getVelocity());
    }
}
