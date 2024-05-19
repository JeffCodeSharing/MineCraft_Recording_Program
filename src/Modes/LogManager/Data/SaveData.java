package Modes.LogManager.Data;

import Tools.EDTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.List;

/**
 * 数据保存类，用于将数据保存到文件中。
 */
public class SaveData {

    /**
     * 保存数据的过程。
     *
     * @param date_path 要保存数据的目录路径。
     * @param fields 包含要保存数据的 TextField 对象的列表。
     * @param areas 包含要保存数据的 TextArea 对象的列表。
     */
    public void entrance(String date_path, List<TextField> fields, List<TextArea> areas) {
        JSONObject writeData = new JSONObject();
        JSONArray list = new JSONArray();
        for (int i=0; i<fields.size(); i++){
            String encodeEvent = EDTool.encrypt(fields.get(i).getText());
            String encodeDetails = EDTool.encrypt(areas.get(i).getText());

            JSONObject eventData = new JSONObject();
            eventData.put("event", encodeEvent);
            eventData.put("details", encodeDetails);

            list.add(eventData);
        }
        writeData.put("data", list);

        if (!JsonTool.writeJson(writeData, new File(date_path, "basicData.json"))) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "保存失败", "");
        } else {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "保存成功！", "");
        }
    }
}

