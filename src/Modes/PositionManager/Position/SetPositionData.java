package Modes.PositionManager.Position;

import Interface.AbstractWindow;
import Modes.PositionManager.Group.SaveGroup;
import Tools.ColorTool;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * 更改坐标组中坐标的信息
 */
public class SetPositionData extends Application implements AbstractWindow {
    private final VBox box;
    private final List<Integer> item_num;
    private final List<String[]> group_value;
    private final int group_type;
    private final String group_name;
    private final String group_dir;
    private final Stage global_stage = new Stage();

    /**
     * ChangePositionData类的构造方法。
     *
     * @param box_in          存放位置数据的VBox对象
     * @param item_num_in     每个组的项目数列表
     * @param group_value_in  存储位置数据的列表
     * @param group_type_in   组类型
     * @param group_dir_in    分组数据保存的目录
     * @param group_name_in   分组名称
     */
    public SetPositionData(VBox box_in, List<Integer> item_num_in, List<String[]> group_value_in, int group_type_in,
                           String group_dir_in, String group_name_in) {
        box = box_in;
        item_num = item_num_in;
        group_value = group_value_in;
        group_type = group_type_in;
        group_name = group_name_in;
        group_dir = group_dir_in;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    /**
     * 绘制用户点击进入后的首页。
     *
     * @param group 要绘制控件的Group对象
     */
    @Override
    public void drawControls(Group group) {
        group.getChildren().clear();

        String[] listView_in = new String[group_value.size()];
        for (int i = 0; i < listView_in.length; i++) {
            String[] temp = group_value.get(i);

            String array_in = "第" + (i + 1) + "项  X:" + temp[0] + "  Y:" + temp[1] + "  Z:" + temp[2] + "  备注:" + temp[3] + "  颜色:" + temp[4];
            listView_in[i] = array_in;
        }

        ListView<String> listView = WinTool.createListView(20, 45, 340, 280, listView_in);

        Button confirm = WinTool.createButton(190, 350, 80, 40, 16, "选择");
        Button cancel = WinTool.createButton(280, 350, 80, 40, 16, "关闭");

        confirm.setOnAction(actionEvent -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                showPositionDetails(group, index);
            }
        });
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 300, 30, 20, "选择坐标", Color.BLUE),
                listView,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("更改坐标信息");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(420);
        stage.setHeight(450);
        stage.showAndWait();
    }

    /**
     * 显示位置详细信息页面。
     *
     * @param group 要绘制控件的Group对象
     * @param index 选中的位置索引
     */
    private void showPositionDetails(Group group, int index) {
        group.getChildren().clear();
        String[] values = group_value.get(index);

        TextField x_field = WinTool.createTextField(60, 55, 100, 30, 15, values[0], "");
        TextField y_field = WinTool.createTextField(60, 95, 100, 30, 15, values[1], "");
        TextField z_field = WinTool.createTextField(60, 135, 100, 30, 15, values[2], "");
        TextField notes_field = WinTool.createTextField(60, 175, 300, 30, 15, values[3], "");
        ComboBox<String> color_box = WinTool.createComboBox(60, 215, 100, 30, false,
                ColorTool.englishToChinese(values[4]), ColorTool.getColorsName());

        Button change = WinTool.createButton(120, 300, 70, 35, 15, "更改");
        Button delete = WinTool.createButton(200, 300, 70, 35, 15, "删除");
        Button return_menu = WinTool.createButton(280, 300, 70, 35, 15, "返回 ");

        change.setOnAction(actionEvent -> saveEvents(
                x_field.getText(), y_field.getText(), z_field.getText(), notes_field.getText(),
                ColorTool.chineseToEnglish(color_box.getSelectionModel().getSelectedItem()), index
        ));
        delete.setOnAction(actionEvent -> {
            RemovePosition remover = new RemovePosition(box, index, group_type, item_num, group_value, group_dir, group_name);
            remover.entrance();

            drawControls(group);     // 回到首页
        });
        return_menu.setOnAction(actionEvent -> drawControls(group));

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 60, 40, 25, "细则", Color.BLUE),
                WinTool.createLabel(20, 55, 40, 30, 15, "X :"),
                WinTool.createLabel(20, 95, 40, 30, 15, "Y :"),
                WinTool.createLabel(20, 135, 40, 30, 15, "Z :"),
                WinTool.createLabel(20, 175, 40, 30, 15, "备注:"),
                WinTool.createLabel(20, 215, 40, 30, 15, "颜色:"),
                x_field, y_field, z_field, notes_field, color_box,
                change, delete, return_menu
        );
    }

    /**
     * 保存更改后的位置信息。
     *
     * @param x          X 坐标的字符串值
     * @param y          Y 坐标的字符串值
     * @param z          Z 坐标的字符串值
     * @param notes      备注的字符串值
     * @param color      颜色的字符串值
     * @param index      选中的位置索引
     */
    private void saveEvents(String x, String y, String z, String notes, String color, int index) {
        int controls_num = 2 + 1 + index;
        for (int i = 0; i < group_type - 1; i++) {
            controls_num = controls_num + item_num.get(i) + 2;
        }

        group_value.set(index, new String[]{x, y, z, notes, color});

        String[] values = group_value.get(index);
        String add_value = group_name + (index + 1) + "  x:" + values[0] + " y:" + values[1] + " z:" + values[2] + " 备注:" + values[3];
        box.getChildren().set(controls_num, WinTool.createLabel(0, 0, 630, 25, 18, add_value,
                ColorTool.englishToColor(values[4])));

        WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "更改成功！！", "");

        SaveGroup saver = new SaveGroup();
        saver.entrance(group_dir + File.separator + group_name, group_value);
    }
}
