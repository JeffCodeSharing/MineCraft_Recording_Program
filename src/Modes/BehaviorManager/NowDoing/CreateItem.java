package Modes.BehaviorManager.NowDoing;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class CreateItem extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final List<String> data;

    public CreateItem(List<String> data) {
        this.data = data;
    }

    public Void entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField field = WinTool.createTextField(110, 40, 260, 30, 15);

        Button confirm = WinTool.createButton(200, 160, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(290, 160, 80, 40, 16, "关闭");

        confirm.setOnAction(actionEvent -> afterConfirm(field.getText()));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 380, 30, 25, "创建内容", Color.BLUE),
                WinTool.createLabel(10, 40, 100, 30, 15, "概述："), field,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(250);
        stage.showAndWait();
    }

    /**
     * 在点击确定按钮后执行的操作。
     *
     * @param create_data 要创建的内容
     */
    private void afterConfirm(String create_data) {
        data.add(create_data);
        global_stage.close();
    }
}
