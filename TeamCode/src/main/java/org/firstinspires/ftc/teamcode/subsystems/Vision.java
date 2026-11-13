
package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.vision.AprilTagData;

import java.util.ArrayList;
import java.util.List;

public class Vision {

    private Limelight3A limelight;

    private LLResult latestResult;
    private List<LLResultTypes.FiducialResult> fiducialResults;

    private Alliances alliance;

    private AprilTagData.Cluster wallCluster;
    private AprilTagData.Cluster audienceCluster;
    private AprilTagData.Cluster selectedCluster;

    private int visibleTagCount = 0;
    private int[] visibleNumberIds = new int[0];
    private String visibleTagIds = "None";

    private int wallTagCount = 0;
    private int audienceTagCount = 0;

    private double averageTx = 0.0;
    private double estimatedCenterTx = 0.0;

    private static final double DEADZONE = 0.5;

    private static final double LEFTEST_OFFSET = -7.5;
    private static final double LEFT_OFFSET = -4.0;
    private static final double RIGHT_OFFSET = 4.0;
    private static final double RIGHTEST_OFFSET = 7.5;

    public Vision(HardwareMap hardwareMap, Alliances alliance) {

        limelight = hardwareMap.get(
                Limelight3A.class,
                "limelight"
        );

        setAlliance(alliance);
    }

    public void setAlliance(Alliances alliance) {

        this.alliance = alliance;

        if (alliance == Alliances.RED) {

            wallCluster = AprilTagData.Cluster.RED_WALL;
            audienceCluster = AprilTagData.Cluster.RED_AUDIENCE;

        } else if (alliance == Alliances.BLUE) {

            wallCluster = AprilTagData.Cluster.BLUE_WALL;
            audienceCluster = AprilTagData.Cluster.BLUE_AUDIENCE;

        } else {

            wallCluster = null;
            audienceCluster = null;
        }

        selectedCluster = null;
    }

    public void startVision() {
        limelight.start();
    }

    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }

    public LLStatus getStatus() {
        return limelight.getStatus();
    }

    public boolean isLimelightNull() {
        return limelight == null;
    }

    public double getTx() {
        return latestResult != null
                ? latestResult.getTx()
                : 0.0;
    }

    public int[] getVisibleNumberIds() {
        return visibleNumberIds;
    }

    public int getVisibleTagCount() {
        return visibleTagCount;
    }

    public String getVisibleTagIds() {
        return visibleTagIds;
    }

    public double getAverageTx() {
        return averageTx;
    }

    public double getEstimatedCenterTx() {
        return estimatedCenterTx;
    }

    public AprilTagData.Cluster getSelectedCluster() {
        return selectedCluster;
    }

    public int getWallTagCount() {
        return wallTagCount;
    }

    public int getAudienceTagCount() {
        return audienceTagCount;
    }

    private double getPositionOffset(
            AprilTagData.TagPosition position
    ) {
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

    private void selectBestCluster() {

        if (wallTagCount > audienceTagCount) {

            selectedCluster = wallCluster;

        } else if (audienceTagCount > wallTagCount) {

            selectedCluster = audienceCluster;

        } else if (wallTagCount == 0) {

            selectedCluster = null;
        }

        // If counts are tied and tags are visible,
        // retain the previous selection.
    }

    public void updateVision() {

        latestResult = limelight.getLatestResult();

        fiducialResults = latestResult != null
                ? latestResult.getFiducialResults()
                : null;

        visibleTagCount = 0;
        wallTagCount = 0;
        audienceTagCount = 0;

        averageTx = 0.0;
        estimatedCenterTx = 0.0;

        double totalTx = 0.0;
        double totalCenterEstimate = 0.0;

        StringBuilder ids = new StringBuilder();
        ArrayList<Integer> visibleIds = new ArrayList<>();

        if (fiducialResults != null) {

            for (LLResultTypes.FiducialResult tag : fiducialResults) {

                int id = tag.getFiducialId();

                // Record every visible tag
                visibleIds.add(id);

                // Count alliance-specific wall tags
                if (wallCluster != null && wallCluster.contains(id)) {
                    wallTagCount++;
                }

                // Count alliance-specific audience tags
                if (audienceCluster != null && audienceCluster.contains(id)) {
                    audienceTagCount++;
                }
            }
        }

        // Automatically choose the best-visible cluster
        selectBestCluster();

        // Calculate center using ONLY the selected cluster
        if (fiducialResults != null && selectedCluster != null) {

            for (LLResultTypes.FiducialResult tag : fiducialResults) {

                int id = tag.getFiducialId();

                if (!selectedCluster.contains(id)) {
                    continue;
                }

                AprilTagData.TagPosition position =
                        selectedCluster.getPosition(id);

                if (position == null) {
                    continue;
                }

                double tx = tag.getTargetXDegrees();

                double positionOffset =
                        getPositionOffset(position);

                double centerEstimate =
                        tx - positionOffset;

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

        visibleNumberIds = visibleIds.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public double getNeededHeading(Follower follower) {

        return follower.pose().heading()
                - Math.toRadians(estimatedCenterTx);
    }

    public boolean withinDeadzone() {

        return visibleTagCount > 0
                && Math.abs(estimatedCenterTx) < DEADZONE;
    }

    public boolean canSeeUprightAllianceCell(Alliances alliance) {

        if (alliance == null) {
            return false;
        }

        for (int id : visibleNumberIds) {

            if (alliance == Alliances.BLUE) {

                if (id >= 38 && id <= 45) {
                    return true;
                }

            } else if (alliance == Alliances.RED) {

                if (id >= 30 && id <= 37) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getFocusedCellName() {

        if (selectedCluster == null) {
            return "NONE";
        }

        switch (selectedCluster) {

            case RED_WALL:
            case BLUE_WALL:
                return "WALL";

            case RED_AUDIENCE:
            case BLUE_AUDIENCE:
                return "AUDIENCE";

            default:
                return "NONE";
        }
    }

    public double getFocusedCellCenterTx() {
        return selectedCluster != null && visibleTagCount > 0
                ? estimatedCenterTx
                : 0.0;
    }
}
