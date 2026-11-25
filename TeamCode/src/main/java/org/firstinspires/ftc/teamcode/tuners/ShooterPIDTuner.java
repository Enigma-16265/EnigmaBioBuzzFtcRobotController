package org.firstinspires.ftc.teamcode.tuners;

import com.arcrobotics.ftclib.controller.PIDController;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.panels.Panels;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Configurable
@TeleOp
public class ShooterPIDTuner extends OpMode {
    private TelemetryManager telemetryM;
    private PIDController pollenController, nectarController;
    private DcMotorEx pollenShooter, nectarShooter;
    private Servo pollenHood, nectarHood;
    public static int pollenTargetSpeed = 800, nectarTargetSpeed = 1250;
    public static double pP = 0.008, pI = 0.0, pD = 0.0;
    public static double nP = 0.01, nI = 0.0, nD = 0.0;
    public static double pollenHoodPos = 0.5, nectarHoodPos = 0.62;
    public static boolean shooterOn = false;

    public boolean isShooterOn() {
        return shooterOn;// == 1;
    }

    public void runPollenShooterPID() {
        double currentVelocity = pollenShooter.getVelocity();

        pollenController.setPID(pP, pI, pD);
        double shooterPid = pollenController.calculate(currentVelocity, pollenTargetSpeed);

        shooterPid = Math.max(shooterPid, -0.2);
        pollenShooter.setPower(shooterPid);
        nectarShooter.setPower(shooterPid);
    }
    public void runNectarShooterPID() {
        double currentVelocity = nectarShooter.getVelocity();

        nectarController.setPID(nP, nI, nD);
        double shooterPid = nectarController.calculate(currentVelocity, nectarTargetSpeed);

        shooterPid = Math.max(shooterPid, -0.2);
        nectarShooter.setPower(shooterPid);
    }

//    public void setHoodPoses() {
//        pollenHood.setPosition(pollenHoodPos);
//        nectarHood.setPosition(nectarHoodPos);
//    }

    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        pollenController = new PIDController(pP, pI, pD);
        nectarController = new PIDController(nP, nI, nD);

        pollenShooter = hardwareMap.get(DcMotorEx.class, "pollenShooter");
        nectarShooter = hardwareMap.get(DcMotorEx.class, "nectarShooter");

        pollenShooter.setDirection(DcMotorSimple.Direction.REVERSE);

        pollenHood = hardwareMap.get(Servo.class, "pollenHood");
        //nectarHood = hardwareMap.get(Servo.class, "nectarHood");
    }

    @Override
    public void start() {

    }

    @Override
    public void init_loop() {
        pollenShooter.setPower(0.3);
        nectarShooter.setPower(0.3);
    }

    @Override
    public void loop() {
        if(isShooterOn()) {
            runPollenShooterPID();
            //runNectarShooterPID();
        }
        pollenHood.setPosition(pollenHoodPos);



        telemetry.addData("shooter vel", pollenShooter.getVelocity());
        telemetry.addData("targret vel", pollenTargetSpeed);
        telemetry.addData("hood pos", pollenHood.getPosition());
        telemetryM.addData("shooter vel", pollenShooter.getVelocity());
        telemetryM.addData("targret vel", pollenTargetSpeed);
        telemetryM.addData("power", pollenShooter.getPower());


//        telemetry.addData("nectar shooter vel", nectarShooter.getVelocity());
//        telemetry.addData("nectar targret vel", nectarTargetSpeed);
//        telemetryM.addData("nectar shooter vel", nectarShooter.getVelocity());
//        telemetryM.addData("nectar targret vel", nectarTargetSpeed);
//        telemetryM.addData("nectar power", nectarShooter.getPower());

        telemetry.update();
        telemetryM.update();
    }
}
