
package org.firstinspires.ftc.teamcode.vision;

public class AprilTagData {

    public enum Cluster {
        RED_WALL(new int[]{30, 31, 32, 33}),
        RED_AUDIENCE(new int[]{34, 35, 36, 37}),
        BLUE_AUDIENCE(new int[]{38, 39, 40, 41}),
        BLUE_WALL(new int[]{42, 43, 44, 45});

        private final int[] tagIds;

        Cluster(int[] tagIds) {
            this.tagIds = tagIds;
        }

        public boolean contains(int id) {
            for (int tagId : tagIds) {
                if (tagId == id) {
                    return true;
                }
            }
            return false;
        }

        public String getTagIds() {
            return java.util.Arrays.toString(tagIds);
        }
    }
}
