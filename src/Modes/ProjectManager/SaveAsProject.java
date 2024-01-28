package Modes.ProjectManager;

import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * SaveAsProject 类用于在 ProjectManager 中另存为项目。
 */
public class SaveAsProject {

    /**
     * 调用者可以使用的入口方法。
     *
     * @param original_path 原始项目的路径
     */
    public void entrance(String original_path) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("另存为");
        File file = chooser.showDialog(new Stage());

        if (file != null) {
            if (IOTool.saveAsDirectory(original_path, file.getPath())) {      // 拷贝文件
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "拷贝项目成功", "路径：" + file.getPath());
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "拷贝失败",
                        "请检查拷贝文件夹是否在被使用\n拷贝文件夹是否超过权限");
            }
        }
    }
}
