package Modes.LogManager.Data;

import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.ArrayList;
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
        String write_path = date_path + File.separator + "simple_data";

        List<String> list = new ArrayList<>();
        for (int i=0; i<fields.size(); i++){
            String field_encode = EDTool.encrypt(fields.get(i).getText());
            String area_encode = EDTool.encrypt(areas.get(i).getText()).replace("\n", "\0");

            list.add(field_encode);
            list.add(area_encode);
        }

        if (!IOTool.overrideFile(write_path, list)) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "保存失败", "");
        } else {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "保存成功！", "");
        }
    }
}

