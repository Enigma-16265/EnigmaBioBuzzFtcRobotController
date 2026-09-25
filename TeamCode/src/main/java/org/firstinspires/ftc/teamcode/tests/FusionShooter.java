package org.firstinspires.ftc.teamcode.tests;

import androidx.annotation.NonNull;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.enums.BallTypes;

public class FusionShooter { //this will need to be expanded upon if we do use. eg, auto waiting for x milliseconds for hood to adjust before feeding
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad1;
    private PIDController shooterController;
    public static double sP, sI, sD;
    private int targetSpeed = 800;
    private DcMotorEx primaryShooter;
    private DcMotor secondaryShooter;
    private Servo hoodLeft, hoodRight;
    private BallTypes targetBallType = BallTypes.POLLEN;
    public double pollenHoodPos = 0.4, nectarHoodPos = 0.6;


    public FusionShooter(@NonNull HardwareMap hardwareMap, Telemetry telemetry, Gamepad gamepad1) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.gamepad1 = gamepad1;

        shooterController = new PIDController(sP, sI, sD);

        primaryShooter = hardwareMap.get(DcMotorEx.class, "primaryShooter");
        secondaryShooter = hardwareMap.get(DcMotor.class, "secondaryShooter");
        hoodLeft = hardwareMap.get(Servo.class, "hoodLeft");
        hoodRight = hardwareMap.get(Servo.class, "hoodRight");

        secondaryShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        hoodRight.setDirection(Servo.Direction.REVERSE);
    }

    private void setShooterPower(double power) {
        primaryShooter.setPower(power);
        secondaryShooter.setPower(power);
    }

    public void setTargetType(BallTypes type) {
        targetBallType = type;
        updateHoodPose();
    }

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

    public void runShooterPID() {
        double currentVelocity = primaryShooter.getVelocity();

        shooterController.setPID(sP, sI, sD);
        double shooterPid = shooterController.calculate(currentVelocity, targetSpeed);

        if(shooterPid < -0.5) {
            shooterPid = -0.5;
        }
        setShooterPower(shooterPid);
    }
}
