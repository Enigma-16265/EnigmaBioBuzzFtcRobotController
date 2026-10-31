package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.controllers.Controller;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * This class handles everything related to the shooter, including a potential turret.
 */
public class Shooter {
    private PIDController pollenController, nectarController;
    private int pollenTargetSpeed = 800, nectarTargetSpeed = 1100;
    private final double pP = 0.0, pI = 0.0, pD = 0.0;
    private final double nP = 0.0, nI = 0.0, nD = 0.0;
    private DcMotorEx pollenShooter, nectarShooter;
    public Shooter(HardwareMap hardwareMap) {
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);

        pollenShooter = hardwareMap.get(DcMotorEx.class, "pollenShooter");
        nectarShooter = hardwareMap.get(DcMotorEx.class, "nectarShooter");
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
