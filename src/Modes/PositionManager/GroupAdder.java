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
    private final List<List<String[]>> group_values;
    private final String group_dir;
    private final List<String> group_names;
    private int y_count;

    /**
     * 使用指定的参数构造 GroupAdder 对象。
     * @param box 需要添加的VBox容器
     * @param group_values 组值列表。
     * @param group_names 组名列表。
     * @param group_dir 组的目录。
     */
    public GroupAdder(Pane box,
                      List<List<String[]>> group_values, List<String> group_names,
                      String group_dir) {
        this.box = box;
        this.group_values = group_values;
        this.group_names = group_names;
        this.group_dir = group_dir;
        this.y_count = 0;
    }

    /**
     * 向 PositionManager 添加组。
     * @param group_value 要添加的组的值。
     * @param title_str 组的标题。
     */
    public void add(List<String[]> group_value, String title_str) {
        final int group_type = group_names.size();      // 在调用本方法之前，group_names 已经添加信息了

        Button delete_group = WinTool.createButton(300, 70+y_count, 90, 30, 15, "删除本组");
        delete_group.setOnAction(actionEvent -> {
            RemoveGroup remover = new RemoveGroup();
            remover.entrance(new File(group_dir, title_str).getPath());

            update(true);
        });

        Button create_item = WinTool.createButton(390, 70+y_count, 90, 30, 15, "创建坐标");
        create_item.setOnAction(actionEvent -> {
            CreatePosition creator = new CreatePosition(box, group_values, group_names, group_value, group_dir, title_str);
            creator.entrance();
        });

        Button change = WinTool.createButton(480, 70+y_count, 110, 30, 14, "更改坐标信息");
        change.setOnAction(actionEvent -> {
            SetPositionData setter = new SetPositionData(box, group_values, group_names, group_value, group_dir, title_str);
            setter.entrance();
        });

        box.getChildren().addAll(
                WinTool.createLabel(0, 70+y_count, 320, 35, 20, group_type + "组: " + title_str, Color.BLUE),
                delete_group, create_item, change
        );
        y_count += 35;

        int i = 1;
        for (String[] values : group_value) {
            String add_str = title_str + i + "  X:" + values[0] + "  Y:" + values[1] + "  Z:" + values[2] + "  备注:" + values[3];
            Label label = WinTool.createLabelWithNoWidth(0, 70+y_count, 25, 18, add_str, ColorTool.englishToColor(values[4]));
            box.getChildren().addAll(label);

            i++;
            y_count += 25;
        }

        // 每一组之间的空隙
        y_count += 30;
    }

    public void add(List<String[]> group_value, String title_str, int group_type) {
        Button delete_group = WinTool.createButton(300, 70+y_count, 90, 30, 15, "删除本组");
        delete_group.setOnAction(actionEvent -> {
            RemoveGroup remover = new RemoveGroup();
            remover.entrance(new File(group_dir, title_str).getPath());

            update(true);
        });

        Button create_item = WinTool.createButton(390, 70+y_count, 90, 30, 15, "创建坐标");
        create_item.setOnAction(actionEvent -> {
            CreatePosition creator = new CreatePosition(box, group_values, group_names, group_value, group_dir, title_str);
            creator.entrance();
        });

        Button change = WinTool.createButton(480, 70+y_count, 110, 30, 14, "更改坐标信息");
        change.setOnAction(actionEvent -> {
            SetPositionData setter = new SetPositionData(box, group_values, group_names, group_value, group_dir, title_str);
            setter.entrance();
        });

        box.getChildren().addAll(
                WinTool.createLabel(0, 70+y_count, 320, 35, 20, group_type + "组: " + title_str, Color.BLUE),
                delete_group, create_item, change
        );
        y_count += 35;

        int i = 1;
        for (String[] values : group_value) {
            String add_str = title_str + i + "  X:" + values[0] + "  Y:" + values[1] + "  Z:" + values[2] + "  备注:" + values[3];
            Label label = WinTool.createLabelWithNoWidth(0, 70+y_count, 25, 18, add_str, ColorTool.englishToColor(values[4]));
            box.getChildren().addAll(label);

            i++;
            y_count += 25;
        }

        // 每一组之间的空隙
        y_count += 30;
    }

    /**
     * 更新 Vbox 中的组信息，用于删除一个组后的信息统一
     */
    public void update(boolean readFiles) {
        box.getChildren().remove(1, box.getChildren().size());

        GroupAdder adder = new GroupAdder(box, group_values, group_names, group_dir);
        if (readFiles) {
            group_values.clear();
            group_names.clear();

            String[] list = new File(group_dir).list();
            for (int i = 0; i < list.length; i++) {
                String s = list[i];
                try {
                    List<String> temp = IOTool.readFileAsArrayList(new File(group_dir, s).getPath());
                    List<String[]> value_group = new ArrayList<>();

                    for (String value : temp) {
                        String[] add_array = EDTool.decrypt(value).split("\0");
                        value_group.add(add_array);
                    }

                    group_values.add(value_group);
                } catch (Exception e) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试或删除项目重新尝试");
                }

                group_names.add(s);
                adder.add(group_values.get(i), group_names.get(i));
            }
        } else {
            for (int i=0; i<group_values.size(); i++) {
                adder.add(group_values.get(i), group_names.get(i), (i+1));
            }
        }
    }
}
