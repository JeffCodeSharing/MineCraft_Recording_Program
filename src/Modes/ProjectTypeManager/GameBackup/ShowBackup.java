package Modes.ProjectTypeManager.GameBackup;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

public class ShowBackup extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final String path;    // 已经包含到了backup文件夹
    private final boolean is_backup;

    /**
     * 初始化项目的路径
     *
     * @param path 项目的路径
     */
    public ShowBackup(String path) {
        if (path.equals("none")) {
            this.path = "";
        } else {
            this.path = path + File.separator + "backup";
        }

        is_backup = !Arrays.equals(new File(this.path).list(), new String[0]);
    }

    @Override
    public Void entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        Label backup_label = WinTool.createLabel(10, 40, 230, 30, 16,
                "备份：" + (is_backup ? "已备份" : "未备份"));

        Button output_backup = WinTool.createButton(30, 100, 100, 40, 15, "导出备份");
        output_backup.setOnAction(actionEvent -> {
            ExportBackup exporter = new ExportBackup(path);
            exporter.entrance();
        });

        Button override_backup = WinTool.createButton(140, 100, 100, 40, 15, "更新备份");
        override_backup.setOnAction(actionEvent -> {
            ImportBackup importer = new ImportBackup(path);
            importer.entrance();
        });

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 300, 30, 20, "游戏备份", Color.BLUE),
                backup_label,
                output_backup, override_backup
        );
    }

    @Override
    public void start(Stage stage) {
        if (path.equals("")) {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "你还没有打开项目", "请打开后重试");
        } else {
            Group group = new Group();
            Scene scene = new Scene(group);

            drawControls(group);

            stage.setTitle("游戏备份");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setWidth(300);
            stage.setHeight(200);
            stage.showAndWait();
        }
    }
}
