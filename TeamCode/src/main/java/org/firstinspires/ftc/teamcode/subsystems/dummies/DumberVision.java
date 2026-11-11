
package org.firstinspires.ftc.teamcode.subsystems.dummies;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.vision.AprilTagData.Cluster;
import org.firstinspires.ftc.teamcode.vision.AprilTagData.TagPosition;

import java.util.List;

public class DumberVision {

    private Limelight3A limelight;
    private Follower follower;
    private Alliances alliance;

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

    private final Pose redAudienceCellPose = new Pose(58, 57, 0);
    private final Pose redWallCellPose = new Pose(58, 84, 0);
    private final Pose blueAudienceCellPose = new Pose(84, 57, 0);
    private final Pose blueWallCellPose = new Pose(84, 84, 0);

    public DumberVision(HardwareMap hardwareMap, Follower follower, Alliances alliance) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        this.follower = follower;
        this.alliance = alliance;
    }

    public void startLimelight() {
        limelight.start();
    }

    private Cluster selectedCluster() {
        return clusters[selectedClusterIndex];
    }

    public void selectPreviousCluster() {
        selectedClusterIndex =
                (selectedClusterIndex - 1 + clusters.length) % clusters.length;
    }

    public void selectNextCluster() {
        selectedClusterIndex =
                (selectedClusterIndex + 1) % clusters.length;
    }

    public void toggleTracking() {
        tracking = !tracking;
    }

    public void stopTracking() {
        tracking = false;
    }

    public boolean isTracking() {
        return tracking;
    }

    public void updateControls(Gamepad gamepad) {

        if (gamepad.dpadLeftWasPressed()) {
            selectPreviousCluster();
        }

        if (gamepad.dpadRightWasPressed()) {
            selectNextCluster();
        }

        if (gamepad.xWasPressed()) {
            toggleTracking();
        }

        if (gamepad.yWasPressed()) {
            stopTracking();
        }
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

        Pose targetCluster = new Pose(0, 0, 0);

        if (selectedCluster() == Cluster.RED_AUDIENCE) {
            targetCluster = redAudienceCellPose;
        }

        if (selectedCluster() == Cluster.RED_WALL) {
            targetCluster = redWallCellPose;
        }

        if (selectedCluster() == Cluster.BLUE_AUDIENCE) {
            targetCluster = blueAudienceCellPose;
        }

        if (selectedCluster() == Cluster.BLUE_WALL) {
            targetCluster = blueWallCellPose;
        }

        double targetHeadingRadians = Math.atan2(
                targetCluster.y() - follower.pose().y(),
                targetCluster.x() - follower.pose().x()
        );

        return targetHeadingRadians - follower.pose().heading();
    }

    public double getVisionTurn() {

        if (visibleTagCount == 0) {
            estimatedCenterTx = Math.toDegrees(getTargetHeading());
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

    public void update() {
        updateVision();
    }

    public void getTelemetry(Telemetry t) {

        t.addData("Selected cell", selectedCluster());

        t.addData("Tracking", tracking ? "ON" : "OFF");

        t.addData("Visible tags", visibleTagCount);

        t.addData("Detected tags", visibleTagIds);

        t.addData("Average tag tx", averageTx);

        t.addData("Estimated center tx", estimatedCenterTx);

        t.addData("Vision turn", getVisionTurn());

        if (visibleTagCount == 0) {
            t.addLine("Selected cell not visible");
        } else if (Math.abs(estimatedCenterTx) < DEADZONE) {
            t.addLine("Centered on estimated cell center");
        } else {
            t.addLine("Centering on estimated cell center");
        }

        t.addLine();

        t.addData("Follower Pose", follower.pose());

        t.addData("Target Radians", getTargetHeading());

        t.addData("Target degrees",
                Math.toDegrees(getTargetHeading()));

        t.addData("Field Relative Target Degrees",
                Math.toDegrees(getTargetHeading() + follower.pose().heading()));

        t.addData("Heading", follower.pose().heading());
    }

    public Cluster getSelectedCluster() {
        return selectedCluster();
    }

    public double getEstimatedCenterTx() {
        return estimatedCenterTx;
    }

    public int getVisibleTagCount() {
        return visibleTagCount;
    }

    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }
}
