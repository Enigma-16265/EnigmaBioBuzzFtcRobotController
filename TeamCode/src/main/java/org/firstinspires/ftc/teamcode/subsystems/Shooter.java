package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This class handles everything related to the shooter, including a potential turret.
 */
public class Shooter {
    private PIDController pollenController, nectarController;
    private int pollenTargetSpeed = 800, nectarTargetSpeed = 1100;
    private final double pP = 0.01, pI = 0.0, pD = 0.0;
    private final double nP = 0.0, nI = 0.0, nD = 0.0;
    private final double POLLEN_GATE_OPEN_POS = 0.6;
    private final double POLLEN_GATE_CLOSE_POS = 0.4;
    private final double NECTAR_GATE_OPEN_POS = 0.6;
    private final double NECTAR_GATE_CLOSE_POS = 0.4;
    private final int[] pollenShooterSpeeds = {1200, 1250, 1300, 1350};
    private final double[] pollenHoodPoses = {0.5, 0.53, 0.56, 0.59}; //important tweak values
    private final int[] nectarShooterSpeeds = {1300, 1350, 1400, 1450};
    private final double[] nectarHoodPoses = {0.5, 0.53, 0.56, 0.59};
    private final double[] distanceThresholds = {44, 47, 50, 53, 56};
    private DcMotorEx pollenShooter, nectarShooter;
    private Servo pollenGate, nectarGate;
    private Servo pollenHood, nectarHood;
    public Shooter(HardwareMap hardwareMap) {
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);

        pollenShooter = hardwareMap.get(DcMotorEx.class, "pollenShooter");
        //nectarShooter = hardwareMap.get(DcMotorEx.class, "nectarShooter");

        //intakeLeft = hardwareMap.get(DcMotor.class, "intakeLeft");
        //intakeRight = hardwareMap.get(DcMotor.class, "intakeRight");

        pollenGate = hardwareMap.get(Servo.class, "pollenGate");
        //nectarGate = hardwareMap.get(Servo.class, "nectarGate");
        pollenHood = hardwareMap.get(Servo.class, "pollenHood");
        //nectarHood = hardwareMap.get(Servo.class, "nectarHood");
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

    private int desiredPollenSpeedBasedOnDistance(Vision v) {
        double distance = v.getDistanceToFiducial();
        double index = getDistanceIndex(distance);

        if(index == -1) {
            return pollenTargetSpeed;
        }

        return pollenShooterSpeeds[getDistanceIndex(index)];
    }

    private double desiredPollenHoodPosBasedOnDistance(Vision v) {
        double distance = v.getDistanceToFiducial();
        double index = getDistanceIndex(distance);

        if(index == -1) {
            return pollenHood.getPosition();
        }

        return pollenHoodPoses[getDistanceIndex(index)];
    }

    private int desiredNectarSpeedBasedOnDistance(Vision v) {
        double distance = v.getDistanceToFiducial();
        double index = getDistanceIndex(distance);

        if(index == -1) {
            return nectarTargetSpeed;
        }

        return nectarShooterSpeeds[getDistanceIndex(index)];
    }

    private double desiredNectarHoodPosBasedOnDistance(Vision v) {
        double distance = v.getDistanceToFiducial();
        double index = getDistanceIndex(distance);

        if(index == -1) {
            return nectarHood.getPosition();
        }

        return nectarHoodPoses[getDistanceIndex(index)];
    }

    private int getDistanceIndex(double distance) {
        if(isBetweenXY(distance, distanceThresholds[0], distanceThresholds[1]))
            return 0;
        if(isBetweenXY(distance, distanceThresholds[1], distanceThresholds[2]))
            return 1;
        if(isBetweenXY(distance, distanceThresholds[2], distanceThresholds[3]))
            return 2;
        if(isBetweenXY(distance, distanceThresholds[3], distanceThresholds[4]))
            return 3;

        return -1;
   }

    private boolean isBetweenXY(double value, double min, double max) {
        return (value >= min && value <= max);
    }
//
//    private double getDistanceToGoalOdo(Follower follower, Alliances alliance) {
//        return Math.sqrt(
//                Math.pow()
//        )
//    }

    public void setShootersBasedOnDistance(Vision v) {
        pollenTargetSpeed = desiredPollenSpeedBasedOnDistance(v);
        pollenHood.setPosition(desiredPollenHoodPosBasedOnDistance(v));
    }


    public void runShooterPIDs() {
        runPollenShooterPID();
        //runNectarShooterPID();
    }

    public double getPollenShooterVelocity() {
        return pollenShooter.getVelocity();
    }
    public double getNectarShooterVelocity() {
        return nectarShooter.getVelocity();
    }
}
