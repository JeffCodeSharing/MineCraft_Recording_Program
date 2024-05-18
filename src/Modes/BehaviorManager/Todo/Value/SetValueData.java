package Modes.BehaviorManager.Todo.Value;

import Interface.AbstractWindow;
import Modes.BehaviorManager.Todo.DataController;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SetValueData extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final DataController.DataPack value;

    /**
     * @param value 触发了更改操作的DataPack
     */
    public SetValueData(DataController.DataPack value) {
        this.value = value;
    }

    public Void entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        Label information = WinTool.createLabel(20, 130, 350, 60, 18,
                "提示：在填写过程中不能使用 tab 键和 空格键！！", Color.ORANGE);
        information.setWrapText(true);

        TextField note_field = WinTool.createTextField(100, 50, 260, 30, 16,
                value.getNote(), "");
        TextField way_field = WinTool.createTextField(120, 100, 240, 30, 16,
                value.getWay(), "");

        // 监听文本框内容改变事件，去除其中的tab和空格
        note_field.textProperty().addListener((observableValue, s, t1) -> {
            String temp = note_field.getText();
            temp = temp.replace("\t", "");
            temp = temp.replace(" ", "");

            note_field.setText(temp);
        });

        way_field.textProperty().addListener((observableValue, s, t1) -> {
            String temp = way_field.getText();
            temp = temp.replace("\t", "");
            temp = temp.replace(" ", "");

            way_field.setText(temp);
        });

        Button confirm = WinTool.createButton(200, 200, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(290, 200, 80, 40, 16, "取消");

        // 确认按钮点击事件
        confirm.setOnAction(actionEvent -> afterConfirm(note_field.getText(), way_field.getText()));
        // 取消按钮点击事件
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 300, 30, 25, "创建信息"),
                WinTool.createLabel(20, 50, 80, 30, 16, "信息："), note_field,
                WinTool.createLabel(20, 100, 100, 30, 16, "实现方法："), way_field,
                information,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("更改信息");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(350);
        stage.showAndWait();
    }

    /**
     * 确认按钮点击事件处理
     * @param noteValue "信息内容"的TextField中的内容
     * @param wayValue "实现方法"的TextField中的内容
     */
    private void afterConfirm(String noteValue, String wayValue) {
        value.setNote(noteValue);
        value.setWay(wayValue);

        global_stage.close();
    }
}
