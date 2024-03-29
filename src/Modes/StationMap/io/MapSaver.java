package Modes.StationMap.io;

import Modes.StationMap.LineData;
import Tools.WinTool;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MapSaver {
    public static void save(List<LineData> data, String path) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(
                    new FileOutputStream(path + File.separator + "map" + File.separator + "map_data")
            );
            output.writeObject(data);
            output.close();

            WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "已成功保存", "");
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "保存失败", "请重新尝试");
        }
    }
}
