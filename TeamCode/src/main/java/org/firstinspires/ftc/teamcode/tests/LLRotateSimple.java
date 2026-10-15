package org.firstinspires.ftc.teamcode.tests;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

enum LLRotationStates {
    IDLE,
    NO_TARGET,
    OFF_LEFT,
    OFF_RIGHT,
    CENTERED
}

@TeleOp
public class LLRotateSimple extends OpMode {
    private Limelight3A limelight;
    private Follower follower;
    private LLRotationStates llRotationState = LLRotationStates.IDLE;
    private LLResult latestResult;

    private void drive() {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);
        //follower.manual(powers);
        follower.update();
    }

    /*
    private void updateLLRotationState() {
        if(latestResult.getTx())
    }

     */

    @Override
    public void init() {
        //limelight = hardwareMap.get(Limelight3A.class, "limelight");
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void loop() {
        //latestResult = limelight.getLatestResult();

        drive();
    }
}
