package org.firstinspires.ftc.teamcode.subsystems.dummies;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 *  This class should contain everything related to the intake. This obviously includes the intake itself,
 *  but also anything related to a flower intake, as well as transfer.
 */
public class DummyIntake {
    private final double INTAKE_POWER = 0.8;
    private final double FEED_POWER = 0.6;
    private final double OUTTAKE_POWER = -0.4;
    public DummyIntake(HardwareMap hardwareMap) {

    }

    public Command setFeeding = Command.build()
            .setStart(() -> {
                setPower(FEED_POWER);
            })
            .setDone(() -> true);

    public Command stopIntake = Command.build()
            .setStart(() -> {
                setPower(0);
            })
            .setDone(() -> true);

    private void setPower(double power) {

    }

    public void runIntake(Gamepad gamepad) {

    }
}
