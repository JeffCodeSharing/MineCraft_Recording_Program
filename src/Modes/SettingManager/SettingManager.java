package Modes.SettingManager;

import Interface.AbstractWindow;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingManager extends Application implements AbstractWindow {
    @Override
    public String[] entrance() {
        start(new Stage());
        return null;
    }

    @Override
    public void draw_controls(Group group) {

    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        draw_controls(group);

        stage.setTitle("设置");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(400);
        stage.showAndWait();
    }
}
