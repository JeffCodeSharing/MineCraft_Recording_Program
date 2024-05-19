package Modes.PositionManager.Event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupEvent {
    private final String groupName;
    private final List<PositionEvent> positions = new ArrayList<>();

    /**
     * @param groupName 这一个groupName包括".json"后缀
     */
    public GroupEvent(String groupName) {
        this.groupName = groupName;
    }

    public void add(PositionEvent event) {
        positions.add(event);
    }

    public PositionEvent get(int i) {
        return positions.get(i);
    }

    public void remove(int index) {
        positions.remove(index);
    }

    public int size() {
        return positions.size();
    }

    public String getGroupName() {
        return groupName;
    }

    public List<PositionEvent> getPositions() {
        return positions;
    }

    public JSONObject getJsonData() {
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        data.put("data", jsonArray);

        for (PositionEvent pos:positions) {
            JSONObject positionData = new JSONObject();
            positionData.put("x", pos.getX());
            positionData.put("y", pos.getY());
            positionData.put("z", pos.getZ());
            positionData.put("note", pos.getNote());
            positionData.put("color", pos.getColor());
            jsonArray.add(positionData);
        }

        return data;
    }
}
