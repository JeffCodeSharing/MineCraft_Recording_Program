package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Modes.BehaviorManager.Todo.ED.Encryption;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.Alert;

import java.io.File;

public class SaveValue {
    /**
     * @param controller 控制信息的DataController
     * @param path       绝对路径，到达储存计划表的文件夹
     * @param listName   计划表的名称
     * @param showAlert  是否显示提示框（在用户手动点击保存，和退回首页的过程中的被动保存不一样）
     */
    public static void save(DataController controller, String path, String listName,
                            boolean showAlert) {
        File file = new File(path, listName+".json");
        JSONObject jsonData = controller.getJsonValues();
        JSONObject encryptData = Encryption.encrypt(jsonData);
        String writeData = encryptData.toJSONString();

        boolean success = IOTool.overrideFile(file.getPath(), new String[]{writeData});
        if (showAlert) {
            if (success) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "保存成功", "");
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "写入文件错误", "请重新尝试或检查项目是否被占用");
            }
        }
    }
}
