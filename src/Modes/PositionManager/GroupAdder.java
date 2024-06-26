package Modes.PositionManager;

import Modes.PositionManager.Event.GroupEvent;
import Modes.PositionManager.Event.PositionEvent;
import Modes.PositionManager.Group.RemoveGroup;
import Modes.PositionManager.Position.CreatePosition;
import Modes.PositionManager.Position.SetPositionData;
import Tools.ColorTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.List;

/**
 * GroupAdder 类负责向 VBox 添加组（包括组中的信息）。
 */
// 注意：本class中，group_values和group_value是不一样的
public class GroupAdder {
    private final Pane box;
    private final List<GroupEvent> group_values;
    private final String group_dir;
    private int y_count;

    /**
     * 使用指定的参数构造 GroupAdder 对象。
     * @param box 需要添加的VBox容器
     * @param group_values 组值列表。
     * @param group_dir 组的目录。
     */
    public GroupAdder(Pane box,
                      List<GroupEvent> group_values, String group_dir) {
        this.box = box;
        this.group_values = group_values;
        this.group_dir = group_dir;
        this.y_count = 0;
    }

    /**
     * 向 PositionManager 添加组。
     * @param group_value 要添加的组的值。
     */
    public void add(GroupEvent group_value) {
        final int groupIndex = group_values.size();      // 在调用本方法之前，group_values 已经添加信息了
        String fileName = group_value.getGroupName();

        // 获取到要显示的标题名字
        int dotIndex = fileName.lastIndexOf(".");
        String titleStr = fileName.substring(0, dotIndex);

        Button delete_group = WinTool.createButton(300, 70+y_count, 90, 30, 15, "删除本组");
        delete_group.setOnAction(actionEvent -> {
            RemoveGroup remover = new RemoveGroup();
            remover.entrance(new File(group_dir, fileName).getPath());

            update(true);
        });

        Button create_item = WinTool.createButton(390, 70+y_count, 90, 30, 15, "创建坐标");
        create_item.setOnAction(actionEvent -> {
            CreatePosition creator = new CreatePosition(box, group_values, group_value, group_dir, fileName);
            creator.entrance();
        });

        Button change = WinTool.createButton(480, 70+y_count, 110, 30, 14, "更改坐标信息");
        change.setOnAction(actionEvent -> {
            SetPositionData setter = new SetPositionData(box, group_values, group_value, group_dir, fileName);
            setter.entrance();
        });

        box.getChildren().addAll(
                WinTool.createLabel(0, 70+y_count, 320, 35, 20, groupIndex + "组: " + titleStr, Color.BLUE),
                delete_group, create_item, change
        );
        y_count += 35;

        int i = 1;
        for (PositionEvent data : group_value.getPositions()) {
            String add_str = titleStr + i + "  X:" + data.getX() + "  Y:" + data.getY() + "  Z:" + data.getZ() + "  备注:" + data.getNote();
            Label label = WinTool.createLabel(0, 70+y_count, -1, 25, 18, add_str, ColorTool.engToColor(data.getColor()));
            box.getChildren().addAll(label);

            i++;
            y_count += 25;
        }

        // 每一组之间的空隙
        y_count += 30;
    }

    public void add(GroupEvent group_value, int group_type) {
        String fileName = group_value.getGroupName();

        // 获取titleStr
        int dotIndex = fileName.lastIndexOf(".");
        String titleStr = fileName.substring(0, dotIndex);

        Button delete_group = WinTool.createButton(300, 70+y_count, 90, 30, 15, "删除本组");
        delete_group.setOnAction(actionEvent -> {
            RemoveGroup remover = new RemoveGroup();
            remover.entrance(new File(group_dir, fileName).getPath());

            update(true);
        });

        Button create_item = WinTool.createButton(390, 70+y_count, 90, 30, 15, "创建坐标");
        create_item.setOnAction(actionEvent -> {
            CreatePosition creator = new CreatePosition(box, group_values, group_value, group_dir, fileName);
            creator.entrance();
        });

        Button change = WinTool.createButton(480, 70+y_count, 110, 30, 14, "更改坐标信息");
        change.setOnAction(actionEvent -> {
            SetPositionData setter = new SetPositionData(box, group_values, group_value, group_dir, fileName);
            setter.entrance();
        });

        box.getChildren().addAll(
                WinTool.createLabel(0, 70+y_count, 320, 35, 20, group_type + "组: " + titleStr, Color.BLUE),
                delete_group, create_item, change
        );
        y_count += 35;

        int i = 1;
        for (PositionEvent data : group_value.getPositions()) {
            String add_str = titleStr + i + "  X:" + data.getX() + "  Y:" + data.getY() + "  Z:" + data.getZ() + "  备注:" + data.getNote();
            Label label = WinTool.createLabel(0, 70+y_count, -1, 25, 18, add_str, ColorTool.engToColor(data.getColor()));
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

        GroupAdder adder = new GroupAdder(box, group_values, group_dir);
        if (readFiles) {
            group_values.clear();

            String[] list = new File(group_dir).list();
            for (int i = 0; i < list.length; i++) {
                String fileName = list[i];
                try {
                    JSONObject jsonData = JsonTool.readJson(new File(group_dir, fileName));
                    JSONArray jsonArray = jsonData.getJSONArray("data");
                    GroupEvent groupData = new GroupEvent(fileName);

                    for (int j=0; j<jsonArray.size(); j++) {
                        JSONObject positionData = jsonArray.getJSONObject(j);
                        groupData.add(new PositionEvent(positionData));
                    }

                    group_values.add(groupData);
                } catch (Exception e) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试或删除项目重新尝试");
                }

                adder.add(group_values.get(i));
            }
        } else {
            for (int i=0; i<group_values.size(); i++) {
                adder.add(group_values.get(i), i+1);
            }
        }
    }
}
