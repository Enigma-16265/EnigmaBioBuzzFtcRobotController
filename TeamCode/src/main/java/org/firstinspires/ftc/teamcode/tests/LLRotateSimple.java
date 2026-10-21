
package org.firstinspires.ftc.teamcode.tests;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.vision.AprilTagData;

import java.util.List;

@TeleOp(name = "Limelight Cluster Center")
public class LLRotateSimple extends OpMode {

    private Limelight3A limelight;
    private Follower follower;

    private LLResult latestResult;
    private List<LLResultTypes.FiducialResult> fiducialResults;

    private final AprilTagData.Cluster[] clusters =
            AprilTagData.Cluster.values();

    private int selectedClusterIndex = 0;

    private boolean tracking = false;

    private int visibleTagCount = 0;
    private String visibleTagIds = "None";
    private double averageTx = 0.0;

    private static final double DEADZONE = 1.5;
    private static final double TURN_GAIN = 0.035;
    private static final double MAX_TURN = 0.5;

    private AprilTagData.Cluster selectedCluster() {
        return clusters[selectedClusterIndex];
    }

    @Override
    public void init() {
        limelight = hardwareMap.get(
                Limelight3A.class, "limelight");

        follower = Constants.create(hardwareMap);
        follower.setPose(new Pose(0.0, 0.0, 0.0));
    }

    @Override
    public void start() {
        limelight.start();
    }

    private void updateVision() {
        latestResult = limelight.getLatestResult();

        fiducialResults = latestResult != null
                ? latestResult.getFiducialResults()
                : null;

        double totalTx = 0.0;
        visibleTagCount = 0;

        StringBuilder ids = new StringBuilder();

        if (fiducialResults != null) {
            for (LLResultTypes.FiducialResult tag : fiducialResults) {
                int id = tag.getFiducialId();

                if (!selectedCluster().contains(id)) {
                    continue;
                }

                totalTx += tag.getTargetXDegrees();
                visibleTagCount++;

                if (ids.length() > 0) {
                    ids.append(", ");
                }

                ids.append(id);
            }
        }

        if (visibleTagCount > 0) {
            averageTx = totalTx / visibleTagCount;
            visibleTagIds = ids.toString();
        } else {
            averageTx = 0.0;
            visibleTagIds = "None";
        }
    }

    private double getVisionTurn() {
        if (visibleTagCount == 0) {
            return 0.0;
        }

        if (Math.abs(averageTx) < DEADZONE) {
            return 0.0;
        }

        double turn = -averageTx * TURN_GAIN;

        return Math.max(-MAX_TURN, Math.min(MAX_TURN, turn));
    }

    private void drive() {
        double turn = tracking
                ? getVisionTurn()
                : -gamepad1.right_stick_x;

        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x, //idk why but I need to flip this
                turn,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);
    }

    private void updateTelemetry() {
        telemetry.addData("Selected cell", selectedCluster());
        telemetry.addData("Cell's tag IDs",
                selectedCluster().getTagIds());

        telemetry.addData("Tracking", tracking ? "ON" : "OFF");
        telemetry.addData("Visible tags in cell", visibleTagCount);
        telemetry.addData("Detected tag IDs", visibleTagIds);
        telemetry.addData("Average tx (degrees)", averageTx);
        //telemetry.addData("Turn power", getVisionTurn());

        if (visibleTagCount == 0) {
            telemetry.addLine("Selected cell not visible");
        } else if (Math.abs(averageTx) < DEADZONE) {
            telemetry.addLine("Centered on selected cell");
        } else {
            telemetry.addLine("Centering on selected cell");
        }

        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.dpadLeftWasPressed()) {
            selectedClusterIndex =
                    (selectedClusterIndex - 1 + clusters.length)
                            % clusters.length;
        }

        if (gamepad1.dpadRightWasPressed()) {
            selectedClusterIndex =
                    (selectedClusterIndex + 1) % clusters.length;
        }

        if (gamepad1.xWasPressed()) {
            tracking = !tracking;
        }

        updateVision();
        drive();
        follower.update();
        updateTelemetry();
    }

    @Override
    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }
}
