package Modes.PositionManager;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

/**
 * @implNote 这一个文件是用来定位在ScrollBar上的坐标组的位置
 */
public class PositioningGroup extends Application implements AbstractWindow {
    private final ScrollPane pane;
    private final List<String> group_name;
    private final List<Integer> item_num;
    private final Stage global_stage = new Stage();
    public PositioningGroup(ScrollPane pane, List<String> group_name, List<Integer> item_num) {
        this.pane = pane;
        this.group_name = group_name;
        this.item_num = item_num;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        // 准备ListView<String>的传入数据
        String[] add_values = new String[group_name.size()];
        for (int i=0; i<add_values.length; i++) {
            add_values[i] = "组" + (i+1) + "  " + group_name.get(i);
        }

        ListView<String> listView = WinTool.createListView(10, 30, 350, 250, add_values);

        Button confirm = WinTool.createButton(190, 310, 80, 40, 15, "确定");
        Button cancel = WinTool.createButton(280, 310, 80, 40, 15, "关闭");

        confirm.setOnAction(actionEvent -> afterPress(listView.getSelectionModel().getSelectedIndex()));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 30, 20, "定位组名：", Color.BLUE),
                listView, confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("定位坐标组");
        stage.setWidth(420);
        stage.setHeight(420);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * 该方法在确认按钮被按下时被调用。它计算所选坐标组在滚动条上的位置，并相应地设置滚动面板的垂直值。
     *
     * @param index 用户选择定位的坐标所对应的值
     */

    private void afterPress(int index) {
        if (index != -1) {
            double set_value = 0;
            set_value += 40;    // 第一个HBox控件
            set_value += 30;    // label的隔行
            for (int i = 0; i < index; i++) {
                set_value += 35;  // 组标题
                set_value += 25 * item_num.get(i);  // 组下的坐标
                set_value += 40;   // label的隔行
            }

            // 除以ScrollPane的长度
            set_value /= (pane.getContent().getBoundsInLocal().getHeight() - (item_num.size() * 45));   // 后面的减法是修正
            pane.setVvalue(set_value);
        }
    }
}
