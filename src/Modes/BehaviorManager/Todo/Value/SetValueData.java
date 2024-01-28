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

/**
 * 模块：行为管理器
 * 类名：ChangeValueData
 * 用途：用于修改值的数据界面
 */
public class SetValueData extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final DataController controller;
    private final int index;

    public SetValueData(DataController controller, int index) {
        this.controller = controller;
        this.index = index;
    }

    /**
     * 入口方法，用于启动界面
     * @return null
     */
    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    /**
     * 绘制界面控件
     * @param group 添加控件的Group
     */
    @Override
    public void drawControls(Group group) {
        String[] value_data = controller.get(index);
        String[] text_data = value_data[0].split("· ");   // 用点号作切分点

        // 进一步切割（分出 notes 和 way），分层讨论，以免在用户更改的是第一层时出错
        text_data = text_data[(text_data.length == 1) ? 0 : 1].split(" ");
        if (text_data.length == 1) {    // 防止用户没有输入
            text_data = new String[]{text_data[0], ""};
        }

        Label information = WinTool.createLabel(20, 130, 350, 60, 18,
                "提示：在填写过程中不能使用 tab 键和 空格键！！", Color.ORANGE);
        information.setWrapText(true);

        TextField notes_field = WinTool.createTextField(100, 50, 260, 30, 16, text_data[0], "");
        TextField way_field = WinTool.createTextField(120, 100, 240, 30, 16, text_data[1], "");

        // 监听文本框内容改变事件，去除其中的tab和空格
        notes_field.textProperty().addListener((observableValue, s, t1) -> {
            String temp = notes_field.getText();
            temp = temp.replace("\t", "");
            temp = temp.replace(" ", "");

            notes_field.setText(temp);
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
        confirm.setOnAction(actionEvent -> afterConfirm(notes_field.getText(), way_field.getText()));
        // 取消按钮点击事件
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 300, 30, 25, "创建信息"),
                WinTool.createLabel(20, 50, 80, 30, 16, "信息："), notes_field,
                WinTool.createLabel(20, 100, 100, 30, 16, "实现方法："), way_field,
                information,
                confirm, cancel
        );
    }

    /**
     * 启动JavaFX应用程序
     * @param stage 舞台对象
     */
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
     * @param notes_value "信息内容"的TextField中的内容
     * @param ways_value "实现方法"的TextField中的内容
     */
    private void afterConfirm(String notes_value, String ways_value) {
        String[] value_data = controller.get(index);
        String[] text_data = value_data[0].split("· ");   // 用点号作切分点（切分出"\t"和文字内容）

        String set_value = text_data[0] + "· " + notes_value + " " + ways_value;
        // value_data[1]指的是文字的Color
        controller.set(index, set_value, value_data[1]);

        global_stage.close();
        WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "更改成功！", "");
    }
}
