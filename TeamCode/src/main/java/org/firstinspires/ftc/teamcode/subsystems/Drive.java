package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.teleop.OpModeStorage;

/**
 * "It drive"
 */
public class Drive {
    private Follower follower;
    public Drive(HardwareMap hardwareMap) {
        follower = Constants.create(hardwareMap);
        follower.setPose(OpModeStorage.autonomousEndPose);
        follower.update();
    }

    public void drive(Gamepad gamepad) {
        double turn = 0.0;
//        double turn = tracking
//                ? getVisionTurn()
//                : -gamepad.right_stick_x;

        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad.left_stick_y,
                -gamepad.left_stick_x,
                turn,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);
    }


    public void periodic() {
        follower.update();
    }
}
