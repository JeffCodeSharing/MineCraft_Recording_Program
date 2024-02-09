package Modes.PositionManager.Position;

import Interface.AbstractWindow;
import Modes.PositionManager.Group.SaveGroup;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * 创建位置数据的窗口类，实现了AbstractWindow接口。
 */
public class CreatePosition extends Application implements AbstractWindow {
    private final Pane box;
    private final List<Integer> item_num;
    private final List<String[]> group_value;
    private final int group_type;
    private final String title_str;
    private final Stage global_stage = new Stage();
    private final String group_path;

    /**
     * CreatePosition类的构造方法。
     *
     * @param box        存放位置数据的VBox对象
     * @param item_num   每个组的项目数列表
     * @param group_value 存储位置数据的列表
     * @param group_type 组类型
     * @param title_str  分组名称的字符串
     * @param group_dir  分组数据保存的目录
     * @param group_name 分组名称
     */
    public CreatePosition(Pane box, List<Integer> item_num, List<String[]> group_value, int group_type, String title_str,
                          String group_dir, String group_name) {
        this.box = box;
        this.item_num = item_num;
        this.group_value = group_value;
        this.group_type = group_type;
        this.title_str = title_str;
        this.group_path = group_dir + File.separator + group_name;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    /**
     * 在窗口中绘制控件
     *
     * @param group 要绘制控件的Group对象
     */
    @Override
    public void drawControls(Group group) {
        TextField x_in = WinTool.createTextField(45, 10, 90, 30, 15);
        TextField y_in = WinTool.createTextField(45, 50, 90, 30, 15);
        TextField z_in = WinTool.createTextField(45, 90, 90, 30, 15);
        TextField notes_in = WinTool.createTextField(45, 130, 120, 30, 15);

        Button confirm = WinTool.createButton(120, 200, 70, 35, 15, "确定");
        Button cancel = WinTool.createButton(200, 200, 70, 35, 15, "取消");

        confirm.setOnAction(actionEvent -> afterConfirm(
                x_in.getText(), y_in.getText(), z_in.getText(), notes_in.getText()
        ));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(20, 10, 25, 30, 15, "X:"),
                WinTool.createLabel(20, 50, 25, 30, 15, "Y:"),
                WinTool.createLabel(20, 90, 25, 30, 15, "Z:"),
                WinTool.createLabel(10, 130, 35, 30, 15, "备注:"),
                x_in, y_in, z_in, notes_in,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建坐标");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(300);
        stage.showAndWait();
    }

    /**
     * 创建位置后的操作。
     *
     * @param x     X 坐标的字符串值
     * @param y     Y 坐标的字符串值
     * @param z     Z 坐标的字符串值
     * @param notes 备注的字符串值
     */
    private void afterConfirm(String x, String y, String z, String notes) {
        int before_control_num = 2;
        for (int i = 0; i < group_type; i++) {
            before_control_num = before_control_num + item_num.get(i) + 2;
        }

        // 创建控件
        String add_str = title_str + (item_num.get(group_type - 1) + 1) + "  x:" + x + " y:" + y + " z:" + z + " 备注:" + notes;
        box.getChildren().add(before_control_num - 1, WinTool.createLabel(20, 0, 610, 25,
                18, add_str, Color.BLACK));

        // 更改统计信息
        item_num.set(group_type - 1, item_num.get(group_type - 1) + 1);
        group_value.add(new String[]{x, y, z, notes, "BLACK"});

        global_stage.close();

        SaveGroup saver = new SaveGroup();
        saver.entrance(group_path, group_value);
    }
}
