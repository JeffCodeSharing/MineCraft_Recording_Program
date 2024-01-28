package Modes.ProjectTypeManager.GameBackup;

import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ExportBackup {
    private final String path;   // 已经到了backup文件夹了

    public ExportBackup(String path) {
        this.path = path;
    }

    public void entrance() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("导出备份");

        // 复制
        File file = chooser.showDialog(new Stage());
        if (file != null) {
            // 复制
            if (IOTool.saveAsDirectory(path, file.getPath())) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "保存成功！！", "");
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "失败", "请重新尝试");
            }
        }
    }
}
