package Modes.PositionManager.Group;

import Tools.EDTool;
import Tools.WinTool;
import javafx.scene.control.Alert;

import java.io.FileWriter;
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
        try {
            start(group_path, group_value);
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "保存失败", "");
        }
    }

    /**
     * 启动保存分组数据的操作。
     *
     * @param path        分组数据保存的路径
     * @param group_value 需要保存的分组数据
     * @throws Exception 如果保存失败抛出异常
     */
    private void start(String path, List<String[]> group_value) throws Exception {
        FileWriter writer = new FileWriter(path);

        for (String[] strings : group_value) {
            StringBuilder builder = new StringBuilder();
            for (int i=0; i<strings.length; i++) {
                builder.append(strings[i]);

                if (i < strings.length-1) {
                    builder.append("\0");
                }
            }
            writer.append(EDTool.encrypt(builder.toString())).append("\n");
        }

        writer.close();
    }
}
