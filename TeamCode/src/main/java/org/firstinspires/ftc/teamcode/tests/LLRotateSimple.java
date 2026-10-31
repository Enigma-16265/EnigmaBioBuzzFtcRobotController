
package org.firstinspires.ftc.teamcode.tests;

import com.pedropathing.api.PoseFactory;
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
import org.firstinspires.ftc.teamcode.vision.AprilTagData.Cluster;
import org.firstinspires.ftc.teamcode.vision.AprilTagData.TagPosition;

import java.util.List;

@TeleOp(name = "Limelight Cluster Center")
public class LLRotateSimple extends OpMode {

    private Limelight3A limelight;
    private Follower follower;

    private LLResult latestResult;
    private List<LLResultTypes.FiducialResult> fiducialResults;

    private final Cluster[] clusters = Cluster.values();
    private int selectedClusterIndex = 0;

    private boolean tracking = false;

    private int visibleTagCount = 0;
    private String visibleTagIds = "None";

    private double averageTx = 0.0;
    private double estimatedCenterTx = 0.0;
    
    private static final double DEADZONE = 0.5;
    private static final double TURN_GAIN = 0.035;
    private static final double MAX_TURN = 1.5;

    private static final double LEFTEST_OFFSET = -7.5;
    private static final double LEFT_OFFSET = -4.0;
    private static final double RIGHT_OFFSET = 4.0;
    private static final double RIGHTEST_OFFSET = 7.5;

    private Cluster selectedCluster() {
        return clusters[selectedClusterIndex];
    }
    private PoseFactory factory = PoseFactory.degrees();
    private Pose startPose = factory.of(7, 8.5, 0);
    private Pose redAudienceCellPose = factory.of(58,57,0);
    private Pose redWallCellPose = factory.of(58, 84, 0);
    private Pose blueAudienceCellPose = factory.of(84, 57, 0);
    private Pose blueWallCellPose = factory.of(84, 84, 0);


    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        limelight.start();
    }

    private double getPositionOffset(TagPosition position) {
        switch (position) {
            case LEFTEST:
                return LEFTEST_OFFSET;

            case LEFT:
                return LEFT_OFFSET;

            case RIGHT:
                return RIGHT_OFFSET;

            case RIGHTEST:
                return RIGHTEST_OFFSET;

            default:
                return 0.0;
        }
    }

    private void updateVision() {
        latestResult = limelight.getLatestResult();

        fiducialResults = latestResult != null
                ? latestResult.getFiducialResults()
                : null;

        visibleTagCount = 0;
        averageTx = 0.0;
        estimatedCenterTx = 0.0;

        double totalTx = 0.0;
        double totalCenterEstimate = 0.0;

        StringBuilder ids = new StringBuilder();

        if (fiducialResults != null) {
            for (LLResultTypes.FiducialResult tag : fiducialResults) {

                int id = tag.getFiducialId();

                if (!selectedCluster().contains(id)) {
                    continue;
                }

                TagPosition position =
                        selectedCluster().getPosition(id);

                if (position == null) {
                    continue;
                }

                double tx = tag.getTargetXDegrees();
                double positionOffset =
                        getPositionOffset(position);

                double centerEstimate = tx - positionOffset;

                totalTx += tx;
                totalCenterEstimate += centerEstimate;
                visibleTagCount++;

                if (ids.length() > 0) {
                    ids.append(", ");
                }

                ids.append(id)
                        .append(" (")
                        .append(position)
                        .append(")");
            }
        }

        if (visibleTagCount > 0) {
            averageTx = totalTx / visibleTagCount;

            estimatedCenterTx =
                    totalCenterEstimate / visibleTagCount;

            visibleTagIds = ids.toString();
        } else {
            visibleTagIds = "None";
        }
    }

    private double getTargetHeading() {
        Pose targetCluster = factory.of(0,0,0);
        if(selectedCluster() == Cluster.RED_AUDIENCE)
            targetCluster = redAudienceCellPose;
        if(selectedCluster() == Cluster.RED_WALL)
            targetCluster = redWallCellPose;
        if(selectedCluster() == Cluster.BLUE_AUDIENCE)
            targetCluster = blueAudienceCellPose;
        if(selectedCluster() == Cluster.BLUE_WALL)
            targetCluster = blueWallCellPose;

        double targetHeadingRadians = Math.atan2(
                targetCluster.y() - follower.pose().y(),
                targetCluster.x() - follower.pose().x()
        );

        return targetHeadingRadians - follower.pose().heading();
    }

    private double getVisionTurn() {
        if (visibleTagCount == 0) {
            estimatedCenterTx = Math.toDegrees(getTargetHeading());

            //return 0.0;
        }

        if (Math.abs(estimatedCenterTx) < DEADZONE) {
            return 0.0;
        }

        double turn = -estimatedCenterTx * TURN_GAIN;

        return Math.max(
                -MAX_TURN,
                Math.min(MAX_TURN, turn)
        );
    }

    private void drive() {
        double turn = tracking
                ? getVisionTurn()
                : -gamepad1.right_stick_x;

        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                turn,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);
    }

    private void updateTelemetry() {
        telemetry.addData("Selected cell", selectedCluster());
        //telemetry.addData("Cell tag IDs", selectedCluster().getTagIds());

        telemetry.addData("Tracking", tracking ? "ON" : "OFF");
        telemetry.addData("Visible tags", visibleTagCount);
        telemetry.addData("Detected tags", visibleTagIds);

        telemetry.addData("Average tag tx", averageTx);
        telemetry.addData("Estimated center tx", estimatedCenterTx);
        //telemetry.addData("Vision turn", getVisionTurn());

        if (visibleTagCount == 0) {
            telemetry.addLine("Selected cell not visible");
        }
        else if (Math.abs(estimatedCenterTx) < DEADZONE) {
            telemetry.addLine("Centered on estimated cell center");
        }
        else {
            telemetry.addLine("Centering on estimated cell center");
        }
        telemetry.addLine();
        telemetry.addData("Follower Pose", follower.pose());
        telemetry.addData("Target Radians", getTargetHeading());
        telemetry.addData("Target degrees", Math.toDegrees(getTargetHeading()));
        telemetry.addData("Field Relative Target Degrees", Math.toDegrees(getTargetHeading() + follower.pose().heading()));
        telemetry.addData("heading", follower.pose().heading());

        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.dpadLeftWasPressed()) {
            selectedClusterIndex = (selectedClusterIndex - 1 + clusters.length) % clusters.length;
        }

        if (gamepad1.dpadRightWasPressed()) {
            selectedClusterIndex = (selectedClusterIndex + 1) % clusters.length;
        }

        if (gamepad1.xWasPressed()) {
            tracking = !tracking;
        }

        if (gamepad1.yWasPressed()) {
            tracking = false;
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
