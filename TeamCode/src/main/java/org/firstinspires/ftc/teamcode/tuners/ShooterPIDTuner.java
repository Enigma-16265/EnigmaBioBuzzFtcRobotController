package org.firstinspires.ftc.teamcode.tuners;

import com.arcrobotics.ftclib.controller.PIDController;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Configurable
@TeleOp
public class ShooterPIDTuner extends OpMode {
    private PIDController pollenController, nectarController;
    private DcMotorEx pollenShooter, nectarShooter;
    public int pollenTargetSpeed = 800, nectarTargetSpeed = 1100;
    public final double pP = 0.0, pI = 0.0, pD = 0.0;
    public final double nP = 0.0, nI = 0.0, nD = 0.0;

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

    @Override
    public void init() {
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);
    }

    @Override
    public void start() {}

    @Override
    public void loop() {
        runPollenShooterPID();
        runNectarShooterPID();
    }
}
