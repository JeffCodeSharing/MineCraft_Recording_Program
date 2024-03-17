package Modes.StationMap;

import java.io.Serializable;
import java.util.List;

/**
 * @implNote 因为javafx.scene.paint.Color不能被序列化，所以改用String来储存
 */
public class LineData implements Serializable {
    private final String lineName;
    private String color;
    private final List<StationData> stations;      // 第一项为站名，第二项为xz轴的坐标
    private final List<Integer[]> directions;      // 站点之间线路的走向

    public LineData(String lineName, List<StationData> stations, List<Integer[]> directions, String color) {
        this.lineName = lineName;
        this.stations = stations;
        this.directions = directions;
        this.color = color;
    }

    public Integer[] getPosition(String stationName) {
        for (StationData station:this.stations) {
            if (station.getName().equals(stationName)) {
                return station.getPosition();
            }
        }
        return null;
    }

    public String getColor() {
        return this.color;
    }

    public String getLineName() {
        return this.lineName;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addStation(String stationName, Integer[] position) {
        this.stations.add(new StationData(stationName, position));
    }

    public List<StationData> getStations() {
        return stations;
    }

    public List<Integer[]> getDirections() {
        return directions;
    }

    /**
     * @implNote 用作记录每一个Station的数据，辅助LineData
     */
    public static class StationData implements Serializable {
        private String name;
        private final Integer[] position;     // 位置不准改变

        public StationData(String name, Integer[] position) {
            this.name = name;
            this.position = position;
        }

        public String getName() {
            return this.name;
        }

        public Integer[] getPosition() {
            return this.position;
        }

        public void setName(String newName) {
            this.name = newName;
        }
    }
}
