package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.List;

import static com.pedropathing.api.Paths.line;

@TeleOp
public class LLAlignmentTest extends OpMode {

    private Follower follower;
    private Limelight3A limelight;

    private boolean redAlliance = true;

    private final int[] redTags = {30, 31, 32, 33, 34, 35, 36, 37};

    private final int[] blueTags = {38, 39, 40, 41, 42, 43, 44, 45};

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

        limelight = hardwareMap.get(
                Limelight3A.class, "limelight");

        limelight.pipelineSwitch(0);
    }

    @Override
    public void start() {
        limelight.start();
    }

    private boolean hasTag(int id, int[] tags) {
        for (int tag : tags) {
            if (tag == id) return true;
        }
        return false;
    }

    private void aimAtTag() {
        LLResult result = limelight.getLatestResult();

        if (result == null || !result.isValid()) return;

        List<LLResultTypes.FiducialResult> tags =
                result.getFiducialResults();

        double closestTx = Double.MAX_VALUE;
        boolean found = false;

        int[] allianceTags = redAlliance ? redTags : blueTags;

        for (LLResultTypes.FiducialResult tag : tags) {
            if (hasTag(tag.getFiducialId(), allianceTags)) {
                double tx = tag.getTargetXDegrees();

                if (Math.abs(tx) < Math.abs(closestTx)) {
                    closestTx = tx;
                    found = true;
                }
            }
        }

        if (found) {
            double heading = follower.pose().heading()
                    - Math.toRadians(closestTx);

            Pose start = follower.pose();

            Pose end = new Pose(
                    start.x() + Math.cos(heading),
                    start.x() + Math.sin(heading),
                    heading
            );

            follower.follow(
                    line(start, end).linear(start, end)
            );
        }
    }

    @Override
    public void loop() {
        if (gamepad1.dpadLeftWasPressed()) {
            redAlliance = true;
        }

        if (gamepad1.dpadRightWasPressed()) {
            redAlliance = false;
        }

        if (gamepad1.xWasPressed()) {
            aimAtTag();
        }

        follower.update();

        telemetry.addData(
                "Alliance", redAlliance ? "Red" : "Blue");

        telemetry.addData(
                "Heading",
                Math.toDegrees(follower.pose().heading()));

        telemetry.update();
    }

    @Override
    public void stop() {
        limelight.stop();
    }
}