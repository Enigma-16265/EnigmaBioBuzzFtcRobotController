package org.firstinspires.ftc.teamcode.tests;

import androidx.annotation.NonNull;

import com.arcrobotics.ftclib.controller.PIDController;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.components.Shooter;
import org.firstinspires.ftc.teamcode.enums.BallColors;
import org.firstinspires.ftc.teamcode.enums.BallTypes;

public class FusionShooter { //this will need to be expanded upon if we do use. eg, auto waiting for x milliseconds for hood to adjust before feeding
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private TelemetryManager telemetryM;
    private Gamepad gamepad1;
    private PIDController shooterController;
    //private DualColorSensor dualColorSensor;
    public static double sP = 0.0, sI = 0.0, sD = 0.0;
    private int pollenSpeed = 2000, nectarSpeed = 2500;
    private int targetSpeed = pollenSpeed;
    private boolean autoDetect = false;
    private DcMotorEx primaryShooter;
    private DcMotorEx secondaryShooter;
    //private Servo hoodLeft, hoodRight;
    private BallTypes targetBallType = BallTypes.POLLEN;
    //public double pollenHoodPos = 0.4, nectarHoodPos = 0.6;


    public FusionShooter(@NonNull HardwareMap hardwareMap, Telemetry telemetry, TelemetryManager telemetryM, Gamepad gamepad1) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.telemetryM = telemetryM;
        this.gamepad1 = gamepad1;

        //shooterController = new PIDController(sP, sI, sD);

        //dualColorSensor = new DualColorSensor(hardwareMap);

        primaryShooter = hardwareMap.get(DcMotorEx.class, "primaryShooter");
        secondaryShooter = hardwareMap.get(DcMotorEx.class, "secondaryShooter");
        //hoodLeft = hardwareMap.get(Servo.class, "hoodLeft");
        //hoodRight = hardwareMap.get(Servo.class, "hoodRight");

        primaryShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        //hoodRight.setDirection(Servo.Direction.REVERSE);
    }

    private void setShooterPower(double power) {
        primaryShooter.setPower(power);
        secondaryShooter.setPower(power);
    }

    public void toggleAutoDetect() {
        autoDetect = !autoDetect;
    }

    public void setTargetType(BallTypes type) {
        targetBallType = type;
        //updateHoodPose();
    }
    public void updateTargetSpeed() {
        if(targetBallType == BallTypes.POLLEN)
            targetSpeed = pollenSpeed;
        if(targetBallType == BallTypes.NECTAR)
            targetSpeed = nectarSpeed;
    }
    public void adjustSpeed(int toAdjust) {
        if(targetBallType == BallTypes.POLLEN) {
            pollenSpeed += toAdjust;
            updateTargetSpeed();
        }
        if(targetBallType == BallTypes.NECTAR) {
            nectarSpeed += toAdjust;
            updateTargetSpeed();
        }
    }
    /*
    public void autoUpdateBallType() {
        if(dualColorSensor.lastSeen == BallColors.YELLOW) {
            targetBallType = BallTypes.POLLEN;
        }
        else {
            targetBallType = BallTypes.NECTAR;
        }
    }

     */

    /*
    public void updateHoodPose() {
        if(targetBallType == BallTypes.POLLEN)
            setHoodPos(pollenHoodPos);
        if(targetBallType == BallTypes.NECTAR)
            setHoodPos(nectarHoodPos);
    }


    private void setHoodPos(double toSet) {
        hoodLeft.setPosition(toSet);
        hoodRight.setPosition(toSet);
    }



    public void adjustHood(double toAdjust) {
        setHoodPos(hoodLeft.getPosition() + toAdjust);
    }

     */

    public BallTypes getTargetBallType() {
        return targetBallType;
    }

    public void addTelemetry() {
        telemetry.addData("Target Ball Type", targetBallType);
        telemetry.addData("target speed", targetSpeed);
        telemetry.addData("current speed 1", primaryShooter.getVelocity());
        telemetry.addData("current speed 2", secondaryShooter.getVelocity());
        telemetry.addData("auto detect", autoDetect);
        //telemetry.addData("last guess", dualColorSensor.lastSeen);

        /*
        telemetryM.addData("Target Ball Type", targetBallType);
        telemetryM.addData("target speed", targetSpeed);
        telemetryM.addData("current speed 1", primaryShooter.getVelocity());
        telemetryM.addData("current speed 2", secondaryShooter.getVelocity());
        telemetryM.addData("auto detect", autoDetect);

         */
    }

    /*
    public void runShooterPID() {
        double currentVelocity = primaryShooter.getVelocity();

        shooterController.setPID(sP, sI, sD);
        double shooterPid = shooterController.calculate(currentVelocity, targetSpeed);

        if(shooterPid < -0.5) {
            shooterPid = -0.5;
        }
        setShooterPower(shooterPid);
    }

     */
    public void runShooterPID() {
        primaryShooter.setVelocity(targetSpeed);
        secondaryShooter.setVelocity(targetSpeed);

        //dualColorSensor.updateRGB();
        //autoUpdateBallType();
    }
}
