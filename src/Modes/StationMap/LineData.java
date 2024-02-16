package Modes.StationMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @implNote 因为javafx.scene.paint.Color不能被序列化，所以改用String来储存
 */
public class LineData implements Serializable {
    private String color;
    private final Map<Integer, Integer[]> stations;      // 第一项为站的编号，第二项为xz轴的坐标

    public LineData(HashMap<Integer, Integer[]> stations, String color) {
        this.stations = stations;
        this.color = color;
    }

    public Integer[] get(Integer stationType) {
        return stations.get(stationType);
    }

    public boolean setStationType(Integer old_type, Integer new_type) {
        Integer[] value = stations.get(old_type);
        try {
            stations.remove(old_type);
            stations.put(new_type, value);
            return true;
        } catch (Exception e) {
            stations.put(old_type, value);
            return false;
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addStation(Integer type, Integer[] position) {
        stations.put(type, position);
    }

    public void setStationPosition(Integer station_type, Integer[] position) {
        stations.replace(station_type, position);
    }

    public Map<Integer, Integer[]> getStations() {
        return stations;
    }
}
