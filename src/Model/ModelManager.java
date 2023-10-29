package Model;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ModelManager extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void draw_controls(Group group) {
        Button download_button = WinTool.createButton(30, 80, 80, 40, 16, "下载");
        download_button.setOnAction(actionEvent -> {
            Downloader downloader = new Downloader();
            downloader.entrance();
        });

        Button close_window = WinTool.createButton(130, 80, 80, 40, 16, "关闭窗口");
        close_window.setOnAction(actionEvent -> {
            global_stage.close();
        });

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 300, 40, 20, "下载模组", Color.BLUE),
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
        stage.setHeight(400);
        stage.showAndWait();
    }
}
