package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.controllers.Controller;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This class handles everything related to the shooter, including a potential turret.
 */
public class Shooter {
    private PIDController pollenController, nectarController;
    private final int pollenTargetSpeed = 800, nectarTargetSpeed = 1100;
    private final double pP = 0.0, pI = 0.0, pD = 0.0;
    private final double nP = 0.0, nI = 0.0, nD = 0.0;
    private final double POLLEN_GATE_OPEN_POS = 0.6;
    private final double POLLEN_GATE_CLOSE_POS = 0.4;
    private final double NECTAR_GATE_OPEN_POS = 0.6;
    private final double NECTAR_GATE_CLOSE_POS = 0.4;
    private DcMotorEx pollenShooter, nectarShooter;
    private DcMotor intakeLeft, intakeRight;
    private Servo pollenGate, nectarGate;
    private Servo pollenHood, nectarHood;
    public Shooter(HardwareMap hardwareMap) {
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);

        pollenShooter = hardwareMap.get(DcMotorEx.class, "pollenShooter");
        nectarShooter = hardwareMap.get(DcMotorEx.class, "nectarShooter");

        intakeLeft = hardwareMap.get(DcMotor.class, "intakeLeft");
        intakeRight = hardwareMap.get(DcMotor.class, "intakeRight");

        pollenGate = hardwareMap.get(Servo.class, "pollenGate");
        nectarGate = hardwareMap.get(Servo.class, "nectarGate");
        pollenHood = hardwareMap.get(Servo.class, "pollenHood");
        nectarHood = hardwareMap.get(Servo.class, "nectarHood");
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

    public Command runShooters = Command.build()
            .setExecute(this::runShooterPIDs);

    public Command openGates = Command.build()
            .setStart(() -> {
                pollenGate.setPosition(POLLEN_GATE_OPEN_POS);
                nectarGate.setPosition(NECTAR_GATE_OPEN_POS);
            })
            .setDone(() -> true);
    public Command closeGates = Command.build()
            .setStart(() -> {
                pollenGate.setPosition(POLLEN_GATE_OPEN_POS);
                nectarGate.setPosition(NECTAR_GATE_OPEN_POS);
            })
            .setDone(() -> true);

    public void setPollenHood(double toSet) {pollenHood.setPosition(toSet);}
    public void setNectarHood(double toSet) {nectarHood.setPosition(toSet);}
    public double getPollenHoodPos() {return pollenHood.getPosition();}
    public double getNectarHoodPos() {return nectarHood.getPosition();}


    public void runShooterPIDs() {
        runPollenShooterPID();
        runNectarShooterPID();
    }

    public double getPollenShooterVelocity() {
        return pollenShooter.getVelocity();
    }
    public double getNectarShooterVelocity() {
        return nectarShooter.getVelocity();
    }
}
