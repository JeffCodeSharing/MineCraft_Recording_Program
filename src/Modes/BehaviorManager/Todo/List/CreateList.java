package Modes.BehaviorManager.Todo.List;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

/**
 * 创建计划表
 */
public class CreateList extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final String path;

    /**
     * 构造一个CreateList对象，输入指定路径。
     *
     * @param path 创建计划表的路径
     */
    public CreateList(String path) {
        this.path = path;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField name_field = WinTool.createTextField(65, 40, 200, 30, 15);

        Button confirm = WinTool.createButton(95, 140, 80, 40, 18, "确定");
        Button cancel = WinTool.createButton(185, 140, 80, 40, 18, "取消");

        confirm.setOnAction(actionEvent -> afterConfirm(name_field.getText()));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 30, 20, "创建新计划表", Color.BLUE),
                WinTool.createLabel(10, 40, 55, 30, 15, "列表名:"), name_field,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建新计划表");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(300);
        stage.setHeight(250);
        stage.showAndWait();
    }

    /**
     * “确定”按钮调用的方法。
     *
     * @param field_data 文本框中的输入数据
     */
    private void afterConfirm(String field_data) {
        File file = new File(path, field_data);
        try {
            if (file.exists()) {
                throw new RuntimeException();
            }

            file.createNewFile();
            global_stage.close();

            WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "创建成功！！", "计划表名：" + field_data);
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "创建失败", "请重新尝试\n可能是有重复名的计划表");
        }
    }
}
