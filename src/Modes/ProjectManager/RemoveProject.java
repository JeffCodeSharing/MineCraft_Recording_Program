package Modes.ProjectManager;

import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * RemoveProject 类负责在 ProjectManager 中删除项目。
 */
public class RemoveProject {

    /**
     * 进入方法
     *
     * @param path 待删除项目的路径
     */
    public boolean entrance(String path) {
        if (path.equals("none")) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "没有打开项目", "");
        } else {
            Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                    "删除项目", "您是否要删除这一个项目", "删除后项目将不复存在，\n删除前请先确保没有在使用本文件夹");

            if (type.get() == ButtonType.OK) {
                if (IOTool.removeDirectory(path)) {     // 删除项目
                    WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "项目删除成功", "");
                    return true;
                } else {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "项目无法删除", "");
                }
            }
        }
        return false;
    }
}
