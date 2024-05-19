package Modes.PositionManager.Group;

import Modes.PositionManager.Event.GroupEvent;
import Tools.JsonTool;
import Tools.WinTool;
import javafx.scene.control.Alert;

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
    public void entrance(String group_path, GroupEvent group_value) {
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
    private boolean start(String path, GroupEvent group_value) {
        return JsonTool.writeJson(group_value.getJsonData(), path);
    }
}
