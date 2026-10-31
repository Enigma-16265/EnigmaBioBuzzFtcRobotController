
package org.firstinspires.ftc.teamcode.vision;

import java.util.Arrays;

public class AprilTagData {

    public enum TagPosition {
        LEFTEST,
        LEFT,
        RIGHT,
        RIGHTEST
    }

    public enum Cluster {

        RED_WALL(
                new int[]{30, 31, 32, 33},
                new TagPosition[]{
                        TagPosition.LEFTEST,
                        TagPosition.LEFT,
                        TagPosition.RIGHT,
                        TagPosition.RIGHTEST
                }
        ),

        RED_AUDIENCE(
                new int[]{34, 35, 36, 37},
                new TagPosition[]{
                        TagPosition.LEFTEST,
                        TagPosition.LEFT,
                        TagPosition.RIGHT,
                        TagPosition.RIGHTEST
                }
        ),

        BLUE_AUDIENCE(
                new int[]{38, 39, 40, 41},
                new TagPosition[]{
                        TagPosition.LEFTEST,
                        TagPosition.LEFT,
                        TagPosition.RIGHT,
                        TagPosition.RIGHTEST
                }
        ),

        BLUE_WALL(
                new int[]{42, 43, 44, 45},
                new TagPosition[]{
                        TagPosition.LEFTEST,
                        TagPosition.LEFT,
                        TagPosition.RIGHT,
                        TagPosition.RIGHTEST
                }
        );

        private final int[] tagIds;
        private final TagPosition[] positions;

        Cluster(int[] tagIds, TagPosition[] positions) {
            this.tagIds = tagIds;
            this.positions = positions;
        }

        public boolean contains(int id) {
            return indexOf(id) >= 0;
        }

        public int indexOf(int id) {
            for (int i = 0; i < tagIds.length; i++) {
                if (tagIds[i] == id) {
                    return i;
                }
            }
            return -1;
        }

        public TagPosition getPosition(int id) {
            int index = indexOf(id);
            return index >= 0 ? positions[index] : null;
        }

        public int getTagIdAt(int index) {
            return tagIds[index];
        }

        public int size() {
            return tagIds.length;
        }

        public String getTagIds() {
            return Arrays.toString(tagIds);
        }
    }
}
