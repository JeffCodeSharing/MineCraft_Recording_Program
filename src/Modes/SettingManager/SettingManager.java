package Modes.SettingManager;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingManager extends Application implements AbstractWindow {
    @Override
    public String[] entrance() {
        start(new Stage());
        return null;
    }

    @Override
    public void draw_controls(Group group) {
        VBox pane_box = new VBox();
        ScrollPane scrollPane = WinTool.createScrollPane(0, 0, 400, 400, pane_box);

        // todo 填充settings信息

        group.getChildren().addAll(scrollPane);
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        draw_controls(group);

        stage.setTitle("设置");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
