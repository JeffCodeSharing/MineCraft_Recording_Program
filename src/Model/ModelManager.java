package Model;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ModelManager extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void draw_controls(Group group) {
        TextField download_path = WinTool.createTextField(110, 65, 235, 35, 16);
        Button choose_path = WinTool.createButton(345, 65, 35, 35, 13, "...");

        choose_path.setOnAction(actionEvent -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择下载文件夹");
            File file = chooser.showDialog(new Stage());

            if (file != null) {
                download_path.setText(file.getPath());
            }
        });

        Button download_button = WinTool.createButton(30, 120, 120, 40, 16, "下载");
        download_button.setOnAction(actionEvent -> {
            Downloader downloader = new Downloader();
            boolean success = downloader.entrance(download_path.getText());

            if (success) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功下载", "已下载",
                        "已下载到：" + System.getProperty("user.dir") + File.separator + "mrp_mod.jar");
                global_stage.close();
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "下载失败", "请检查网络配置");
            }
        });

        Button close_window = WinTool.createButton(170, 120, 120, 40, 16, "关闭窗口");
        close_window.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 300, 40, 20, "下载模组", Color.BLUE),
                WinTool.createLabel(10, 60, 100, 35, 16, "下载路径："), download_path, choose_path,
                download_button, close_window
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        draw_controls(group);

        stage.setTitle("下载模组");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(250);
        stage.showAndWait();
    }
}
