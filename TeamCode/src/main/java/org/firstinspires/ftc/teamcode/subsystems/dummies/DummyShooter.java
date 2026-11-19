package org.firstinspires.ftc.teamcode.subsystems.dummies;

import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.subsystems.Vision;

/**
 * This class handles everything related to the shooter, including a potential turret.
 */
public class DummyShooter {
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
    public DummyShooter(HardwareMap hardwareMap) {
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);
    }

    public void runPollenShooterPID() {

    }
    public void runNectarShooterPID() {

    }

    public Command runShooters = Command.build()
            .setExecute(this::runShooterPIDs);

    public Command openGates = Command.build()
            .setStart(() -> {
                runNectarShooterPID();
            })
            .setDone(() -> true);
    public Command closeGates = Command.build()
            .setStart(() -> {
                runNectarShooterPID();
            })
            .setDone(() -> true);

    public void setPollenHood(double toSet) {}
    public void setNectarHood(double toSet) {}
    public double getPollenHoodPos() {return 0.777;}
    public double getNectarHoodPos() {return 0.16265;}


    public void runShooterPIDs() {
        runPollenShooterPID();
        runNectarShooterPID();
    }

    public void setShootersBasedOnDistance(Vision v) {

    }

    public double getPollenShooterVelocity() {
        return 2468;
    }
    public double getNectarShooterVelocity() {
        return 680;
    }
}
