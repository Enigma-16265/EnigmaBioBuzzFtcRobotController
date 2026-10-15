package org.firstinspires.ftc.teamcode.tests;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.ivy.pedro.PedroCommands.*;

import android.transition.Scene;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.List;

enum LLRotationStates {
    IDLE,
    LOST,
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
    private List<LLResultTypes.FiducialResult> fiducialResults;
    private double minLeft = -3, minRight = 3;
    private final PoseFactory poseFactory = PoseFactory.degrees();

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


//    private void limelighty() {
//        for(LLResultTypes.FiducialResult fiducial : fiducialResults) {
//        }
//    }

    private void turnToTarget() {
        double robotHeading = follower.pose().heading();
        double newHeading =  robotHeading += Math.toRadians(latestResult.getTx() * -1);
        Pose poseToRotateTo = new Pose(follower.pose().x(), follower.pose().y(), newHeading);

        Command holdAtGoal = hold(follower, poseToRotateTo);

        Scheduler.schedule(holdAtGoal);
    }


    private void updateLLRotationState() {
        if(!fiducialResults.isEmpty()) {
            if(latestResult.getTx() < minLeft) {
                llRotationState = LLRotationStates.OFF_LEFT;
            }
            if(latestResult.getTx() > minRight) {
                llRotationState = LLRotationStates.OFF_RIGHT;
            }
            if (latestResult.getTx() < minRight && latestResult.getTx() > minLeft) {
                llRotationState = LLRotationStates.CENTERED;
            }
        }
        else {
            llRotationState = LLRotationStates.LOST;
        }
    }



    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(new Pose(0.0, 0.0, Math.toRadians(0)));

    }

    @Override
    public void loop() {
        fiducialResults = latestResult.getFiducialResults();
        latestResult = limelight.getLatestResult();
        drive();
        updateLLRotationState();

        if(gamepad1.xWasPressed()) {
            turnToTarget();
        }

        Scheduler.execute();
    }
}
