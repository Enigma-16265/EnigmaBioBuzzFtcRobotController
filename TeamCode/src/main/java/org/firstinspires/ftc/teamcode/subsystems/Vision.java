package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.enums.Alliances;
import org.firstinspires.ftc.teamcode.vision.AprilTagData;

import java.util.ArrayList;
import java.util.List;

public class Vision {
    private Gamepad gamepad1;
    private Alliances alliance;
    private Limelight3A limelight;
    private LLResult latestResult;
    private List<LLResultTypes.FiducialResult> fiducialResults;

    private final AprilTagData.Cluster[] clusters = AprilTagData.Cluster.values();
    private int selectedClusterIndex = 0;

    private boolean tracking = false;

    private int visibleTagCount = 0;
    private int[] visibleNumberIds;
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
    public Vision(HardwareMap hardwareMap, Alliances alliance) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        this.alliance = alliance;
    }
    private AprilTagData.Cluster selectedCluster() {
        return clusters[selectedClusterIndex];
    }
    public boolean isLimelightNull() {
        return limelight == null;
    }
    public double getTx() {
        return limelight.getLatestResult().getTx();
    }
    public int[] getVisibleNumberIds() {
        return visibleNumberIds;
    }
    private double getPositionOffset(AprilTagData.TagPosition position) {
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

    public void stop() {
        limelight.stop();
    }

    public double getNeededHeading(Follower follower) {
        return follower.pose().heading() - estimatedCenterTx;
    }

    public boolean withinDeadzone() {
        if(limelight.getLatestResult().getTx() != 0)
            return false;
        return Math.abs(estimatedCenterTx) < DEADZONE;
    }

    public boolean canSeeUprightAllianceCell(Alliances alliance) {
        if (fiducialResults == null || alliance == null) {
            //return false;
            throw new RuntimeException("freak you!!");
        }

//        AprilTagData.Cluster[] allianceClusters;
//
//        if (alliance == Alliances.BLUE) {
//            allianceClusters = new AprilTagData.Cluster[]{
//                    AprilTagData.Cluster.BLUE_AUDIENCE,
//                    AprilTagData.Cluster.BLUE_WALL
//            };
//        } else {
//            allianceClusters = new AprilTagData.Cluster[]{
//                    AprilTagData.Cluster.RED_AUDIENCE,
//                    AprilTagData.Cluster.RED_WALL
//            };
//        }
//
//        for (LLResultTypes.FiducialResult tag : fiducialResults) {
//            int id = tag.getFiducialId();
//
//            for (AprilTagData.Cluster cluster : allianceClusters) {
//                if (cluster.contains(id)) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
        if(alliance == Alliances.BLUE) {
            for(int i : visibleNumberIds) {
                if(i >= 38 && i <= 45)
                    return true;
            }
        }
        else {
            for(int i : visibleNumberIds) {
                if(i >= 30 && i <= 37)
                    return true;
            }
        }

        return false;
    }

    public void updateVision() {
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
        ArrayList<Integer> visibleIds = new ArrayList<>();

        if (fiducialResults != null) {
            for (LLResultTypes.FiducialResult tag : fiducialResults) {

                int id = tag.getFiducialId();

                if (!selectedCluster().contains(id)) {
                    continue;
                }

                AprilTagData.TagPosition position =
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

                visibleIds.add(id);

                if (ids.length() > 0) {
                    ids.append(", ");
                }

                ids.append(id)
                        .append(" (")
                        .append(position)
                        .append(")");
            }
        }

        // Update visible tag averages
        if (visibleTagCount > 0) {
            averageTx = totalTx / visibleTagCount;

            estimatedCenterTx =
                    totalCenterEstimate / visibleTagCount;

            visibleTagIds = ids.toString();
        } else {
            visibleTagIds = "None";
        }

        // Update visible tag ID array every run
        visibleNumberIds = visibleIds.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}
