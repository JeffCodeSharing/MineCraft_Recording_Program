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

public class SetItemData extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final List<String> data;
    private final int selectIndex;

    public SetItemData(List<String> data, int selectIndex) {
        this.data = data;
        this.selectIndex = selectIndex;
    }

    public Void entrance() {
        if (selectIndex != -1) {    // 如果用户选择的情况下，才执行
            start(global_stage);
        }
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField field = WinTool.createTextField(110, 40, 270, 30, 15, data.get(selectIndex), "");

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
     * @param setData 修改后的任务内容
     */
    private void afterConfirm(String setData) {
        data.set(selectIndex, setData);
        global_stage.close();
    }
}
