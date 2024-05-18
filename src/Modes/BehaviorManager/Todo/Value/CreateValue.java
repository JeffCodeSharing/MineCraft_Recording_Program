package Modes.BehaviorManager.Todo.Value;

import Interface.AbstractWindow;
import Modes.BehaviorManager.Todo.DataController;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CreateValue extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final DataController.DataPack parent;

    public  CreateValue(DataController.DataPack parent) {
        this.parent = parent;
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

        TextField notes_field = WinTool.createTextField(100, 50, 260, 30, 16);
        TextField way_field = WinTool.createTextField(120, 100, 240,30, 16);

        // 监听文本框内容改变事件，去除其中的tab和空格
        notes_field.textProperty().addListener((observableValue, old_value, new_value) -> notes_field.setText(new_value.replace(" ", "")));
        way_field.textProperty().addListener((observableValue, old_value, new_value) -> way_field.setText(new_value.replace(" ", "")));

        Button confirm = WinTool.createButton(200, 200, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(290, 200, 80, 40, 16, "取消");

        // 确认按钮点击事件
        confirm.setOnAction(actionEvent -> afterConfirm(notes_field.getText(), way_field.getText()));
        // 取消按钮点击事件
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 300, 30, 25, "创建信息", Color.BLUE),
                WinTool.createLabel(20, 50, 80, 30, 16, "信息："), notes_field,
                WinTool.createLabel(20, 100, 100, 30, 16, "实现方法："), way_field,
                information,
                confirm, cancel);
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建内容");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(350);
        stage.showAndWait();
    }

    /**
     * 确认按钮点击事件处理
     * @param note 信息内容
     * @param way  实现方法
     */
    private void afterConfirm(String note, String way) {
        if (note != null) {
            if (!(note.equals(""))) {
                if (way == null) {   // 防止用户没有输入way输入框
                    way = "";
                }

                DataController.DataPack child = new DataController.DataPack(note, way, "BLACK", parent);
                parent.addChildren(child);
                child.setDone(false);

                global_stage.close();
            } else {
                WinTool.createAlert(Alert.AlertType.WARNING, "提示", "请填写”信息“一栏", "");
            }
        } else {
            WinTool.createAlert(Alert.AlertType.WARNING, "提示", "请填写“信息”一栏", "");
        }
    }
}
