package Modes.PositionManager;

import Modes.PositionManager.Group.RemoveGroup;
import Modes.PositionManager.Position.CreatePosition;
import Modes.PositionManager.Position.SetPositionData;
import Tools.ColorTool;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * GroupAdder 类负责向 VBox 添加组（包括组中的信息）。
 */
// 注意：本class中，group_values和group_value是不一样的
public class GroupAdder {
    private final Pane box;
    private final List<Integer> item_num;
    private final List<List<String[]>> group_values;
    private final String group_dir;
    private final List<String> group_name;

    /**
     * 使用指定的参数构造 GroupAdder 对象。
     * @param box 需要添加的VBox容器
     * @param item_num 每个组中的项目数列表。
     * @param group_values 组值列表。
     * @param group_name 组名列表。
     * @param group_dir 组的目录。
     */
    public GroupAdder(Pane box, List<Integer> item_num,
                      List<List<String[]>> group_values, List<String> group_name,
                      String group_dir) {
        this.box = box;
        this.item_num = item_num;
        this.group_values = group_values;
        this.group_name = group_name;
        this.group_dir = group_dir;
    }

    /**
     * 向 PositionManager 添加组。
     * @param group_value 要添加的组的值。
     * @param title_str 组的标题。
     */
    public void add(List<String[]> group_value, String title_str) {
        final int group_type = group_name.size();      // 在调用本方法之前，group_name 已经添加信息了
        HBox hBox = new HBox();
        Label group_title = WinTool.createLabel(0, 0, 320, 35, 20,
                group_type + "组: " + title_str, Color.BLUE);

        Button delete_group = WinTool.createButton(0, 0, 90, 30, 15, "删除本组");
        delete_group.setOnAction(actionEvent -> {
            RemoveGroup remover = new RemoveGroup();
            remover.entrance(group_dir + File.separator + title_str);

            update();
        });

        Button create_position = WinTool.createButton(0, 0, 90, 30, 15, "创建坐标");
        create_position.setOnAction(actionEvent -> {
            CreatePosition creator = new CreatePosition(box, item_num, group_value, group_type, title_str, group_dir, title_str);
            creator.entrance();
        });

        Button change = WinTool.createButton(0, 0, 110, 30, 14, "更改坐标信息");
        change.setOnAction(actionEvent -> {
            SetPositionData setter = new SetPositionData(box, item_num, group_value, group_type, group_dir, title_str);
            setter.entrance();
        });

        hBox.getChildren().addAll(
                group_title, delete_group, create_position, change
        );
        box.getChildren().add(hBox);

        int i = 1;
        for (String[] values : group_value) {
            String add_str = title_str + i + "  X:" + values[0] + "  Y:" + values[1] + "  Z:" + values[2] + "  备注:" + values[3];
            Label label = WinTool.createLabelWithNoWidth(0, 0, 25, 18, add_str, ColorTool.englishToColor(values[4]));
            box.getChildren().add(label);

            i++;
        }
        box.getChildren().add(WinTool.createLabel(0, 0, 0, 40, 0, ""));
    }

    /**
     * 更新 Vbox 中的组信息，用于删除一个组后的信息统一
     */
    private void update() {
        box.getChildren().remove(2, box.getChildren().size());    // 清除除了前两排以外的所有控件
        item_num.clear();
        group_values.clear();
        group_name.clear();

        GroupAdder adder = new GroupAdder(box, item_num, group_values, group_name, group_dir);
        File dir_file = new File(group_dir);

        String[] list = dir_file.list();
        for (int i=0; i<list.length; i++) {
            String s = list[i];
            try {
                List<String> temp = IOTool.readFileAsArrayList(group_dir + File.separator + s);
                List<String[]> value_group = new ArrayList<>();

                for (String value : temp) {
                    String[] add_array = EDTool.decrypt(value).split("\0");
                    value_group.add(add_array);
                }

                item_num.add(value_group.size());
                group_values.add(value_group);
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试或删除项目重新尝试");
            }

            group_name.add(s);
            adder.add(group_values.get(i), group_name.get(i));
        }
    }
}
