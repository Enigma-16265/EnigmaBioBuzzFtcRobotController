package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.*;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.enums.Alliances;
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
                gamepad.right_stick_x,
                follower.pose().heading()
        );

        if(!follower.isBusy())
            ManualDrive.driveOrHold(follower, powers);
    }

    public double getHeadingToPoint(Alliances alliance) {
        Pose targetPose;
        if(alliance == Alliances.RED) {
            targetPose = new Pose(57.75, 71.75);
        }
        else {
            targetPose = new Pose(83.75, 71.75);
        }

        double heading = Math.atan2(
                targetPose.y() - follower.pose().y(),
                targetPose.x() - follower.pose().x()
        );

        return heading;
    }

    public void turnToHeadingInPlace(double heading) {
        Pose toTurnTo = new Pose(follower.pose().x(), follower.pose().y(), heading);
        follower.follow(Paths.line(follower.pose(), toTurnTo));
    }

    public void turnToHeadingInPlace(Vision v) {
        Pose toTurnTo = new Pose(follower.pose().x(), follower.pose().y(), v.getNeededHeading(follower));
        follower.follow(Paths.line(follower.pose(), toTurnTo));
    }

    public void periodic() {
        follower.update();
    }
}
