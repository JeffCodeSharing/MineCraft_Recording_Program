package Modes.StationMap;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @implNote 因为javafx.scene.paint.Color不能被序列化，所以改用String来储存
 */
public class LineData implements Serializable {
    private String color;
    private final Map<String, Integer[]> stations;      // 第一项为站名，第二项为xz轴的坐标

    public LineData(LinkedHashMap<String, Integer[]> stations, String color) {
        this.stations = stations;
        this.color = color;
    }

    public Integer[] get(String stationName) {
        return stations.get(stationName);
    }

    public boolean setStationName(String oldName, String newName) {
        Integer[] value = stations.get(oldName);
        try {
            stations.remove(oldName);
            // 如果不存在才put
            if (stations.get(newName) == null) {
                stations.put(newName, value);
                return true;
            } else {
                // 当做错误处理
                throw new RuntimeException();
            }
        } catch (Exception e) {
            stations.put(oldName, value);
            return false;
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addStation(String stationName, Integer[] position) {
        stations.put(stationName, position);
    }

    public Map<String, Integer[]> getStations() {
        return stations;
    }
}
