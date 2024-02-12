package Modes.PositionManager.Position;

import Interface.AbstractWindow;
import Modes.PositionManager.Group.SaveGroup;
import Modes.PositionManager.GroupAdder;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * 创建位置数据的窗口类，实现了AbstractWindow接口。
 */
public class CreatePosition extends Application implements AbstractWindow {
    private final Pane box;
    private final List<List<String[]>> group_values;
    private final List<String> group_names;
    private final List<String[]> group_value;
    private final Stage global_stage = new Stage();
    private final String group_path;

    /**
     * CreatePosition类的构造方法。
     *
     * @param box        存放位置数据的VBox对象
     * @param group_value 存储位置数据的列表
     * @param group_dir  分组数据保存的目录
     * @param group_name 分组名称
     */
    public CreatePosition(Pane box,
                          List<List<String[]>> group_values, List<String> group_names,
                          List<String[]> group_value,
                          String group_dir, String group_name) {
        this.box = box;
        this.group_values = group_values;
        this.group_names = group_names;
        this.group_value = group_value;
        this.group_path = new File(group_dir, group_name).getPath();
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
        group_value.add(new String[]{x, y, z, notes, "BLACK"});

        GroupAdder updater = new GroupAdder(box, group_values, group_names, new File(group_path).getParent());
        updater.update(false);

        global_stage.close();

        SaveGroup saver = new SaveGroup();
        saver.entrance(group_path, group_value);
    }
}
