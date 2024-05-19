package Modes.PositionManager.Group;

import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.Alert;

import java.util.List;

/**
 * SaveGroup是一个用于保存分组数据的类。
 */
public class SaveGroup {

    /**
     * 保存分组数据的入口方法。
     *
     * @param group_path    分组保存的路径
     * @param group_value   需要保存的分组数据
     */
    public void entrance(String group_path, List<String[]> group_value) {
        boolean success = start(group_path, group_value);
        if (!success) {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "保存失败", "");
        }
    }

    /**
     * 启动保存分组数据的操作。
     *
     * @param path        分组数据保存的路径
     * @param group_value 需要保存的分组数据
     */
    private boolean start(String path, List<String[]> group_value) {
        JSONObject jsonData = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonData.put("data", jsonArray);

        for (String[] data_piece:group_value) {
            JSONObject positionData = new JSONObject();
            positionData.put("x", data_piece[0]);
            positionData.put("y", data_piece[1]);
            positionData.put("z", data_piece[2]);
            positionData.put("note", data_piece[3]);
            positionData.put("color", data_piece[4]);
            jsonArray.add(positionData);
        }

        return JsonTool.writeJson(jsonData, path);
    }
}
