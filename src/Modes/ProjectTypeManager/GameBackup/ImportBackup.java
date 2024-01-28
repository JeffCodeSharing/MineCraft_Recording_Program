package Modes.ProjectTypeManager.GameBackup;

import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImportBackup {
    private final String path;    // 已经到backup了

    public ImportBackup(String path) {
        this.path = path;
    }

    public void entrance() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择备份");

        try {
            // 清理backup文件夹
            if (!IOTool.removeDirectory(path)) {
                throw new RuntimeException();
            }

            if (!new File(path).mkdirs()) {
                throw new RuntimeException();
            }

            // 复制
            File file = chooser.showDialog(new Stage());
            if (file != null) {
                File create_dir = new File(path + File.separator + file.getName());

                // 创建保存文件夹
                if (!create_dir.mkdirs()) {
                    throw new RuntimeException();
                }

                // 复制 save_as_directory复制的只是指定目录下的文件及文件夹，所以才需要创建文件在先
                if (IOTool.saveAsDirectory(file.getPath(), create_dir.getPath())) {
                    WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "保存成功！！", "");
                } else {
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "失败", "请重新尝试");
        }
    }
}

