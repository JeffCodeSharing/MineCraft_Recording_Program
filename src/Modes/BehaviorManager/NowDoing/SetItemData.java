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

/**
 * 显示正在做的任务的详细内容并提供修改的窗口类。
 */
public class SetItemData extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final List<String> list;
    private final int index;

    /**
     * 创建一个ShowItemData对象，用于显示正在做的任务的详细内容并提供修改功能。
     *
     * @param list  正在做的任务的列表，在保存和打开详细信息时使用
     * @param index 当前用户选择的任务的索引
     */
    public SetItemData(List<String> list, int index) {
        this.list = list;
        this.index = index;
    }

    /**
     * 入口方法，在该方法中启动窗口并等待窗口关闭。
     *
     * @return 返回null
     */
    @Override
    public String[] entrance() {
        if (index != -1) {    // 如果用户选择的情况下，才执行
            start(global_stage);
        }
        return null;
    }

    /**
     * 绘制窗口组的控件。
     *
     * @param group 用于添加控件的 JavaFX Group 对象
     */
    @Override
    public void drawControls(Group group) {
        TextField field = WinTool.createTextField(110, 40, 270, 30, 15, list.get(index), "");

        Button confirm = WinTool.createButton(200, 160, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(290, 160, 80, 40, 16, "关闭");

        confirm.setOnAction(actionEvent -> afterConfirm(field.getText()));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 380, 30, 25, "更改内容", Color.BLUE),
                WinTool.createLabel(10, 40, 100, 30, 15, "概述："), field,
                confirm, cancel
        );
    }

    /**
     * 启动窗口并显示。
     *
     * @param stage JavaFX Stage 对象
     */
    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("细则");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(250);
        stage.showAndWait();
    }

    /**
     * 在点击"确定"按钮后的处理逻辑。
     *
     * @param set_data 修改后的任务内容
     */
    private void afterConfirm(String set_data) {
        list.set(index, set_data);
        global_stage.close();
    }
}
