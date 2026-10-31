package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 *  This class should contain everything related to the intake. This obviously includes the intake itself,
 *  but also anything related to a flower intake, as well as transfer.
 */
public class Intake {
    private DcMotor intakeLeft, intakeRight;
    private double INTAKE_POWER = 0.8;
    private double OUTTAKE_POWER = -0.4;
    public Intake(HardwareMap hardwareMap) {
        intakeLeft = hardwareMap.get(DcMotor.class, "intakeLeft");
        intakeRight = hardwareMap.get(DcMotor.class, "intakeRight");
    }

    private void setPower(double power) {
        intakeLeft.setPower(power);
        intakeRight.setPower(power);
    }

    public void runIntake(Gamepad gamepad) {
        if(gamepad.right_trigger_pressed)
            setPower(INTAKE_POWER);
        else if(gamepad.left_trigger_pressed)
            setPower(OUTTAKE_POWER);
    }
}
