package Modes.PositionManager;

import Tools.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责管理PositionManager中的方法
 */
public class Searcher {
    private final List<String> group_name = new ArrayList<>();
    private final List<List<String[]>> group_value = new ArrayList<>();
    private final List<Integer> item_num = new ArrayList<>();

    /**
     * @param box 添加控件的VBox
     * @implNote 调用者可以使用此入口点
     */
    public void entrance(ScrollPane pane, VBox box, String path) {
        start(pane, box, path + File.separator + "positions");
    }

    /**
     * @implNote 实际的入口点，仅可由同一类中的方法调用
     */
    private void start(ScrollPane pane, VBox box, String path) {      // 传入path已经到了positions路径下了
        box.getChildren().clear();

        HBox hBox = new HBox();
        Button create_group = WinTool.createButton(0, 0, 130, 40, 20, "创建坐标组");
        create_group.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "PositionManager" + File.separator +
                    "Group" + File.separator + "CreateGroup.class");
            Class<?> deleter = tool.get_class("Modes.PositionManager.Group.CreateGroup");
            tool.invoke_method(deleter, "entrance",
                    new Class[]{VBox.class, List.class, List.class, List.class, String.class},
                    new Object[]{box, item_num, group_value, group_name, path},
                    new Class[0], new Object[0]);
        });

        Button positioning_group = WinTool.createButton(0, 0, 130, 40, 20, "定位坐标组");
        positioning_group.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "PositionManager" + File.separator +
                    "PositioningGroup.class");
            Class<?> deleter = tool.get_class("Modes.PositionManager.PositioningGroup");
            tool.invoke_method(deleter, "entrance",
                    new Class[]{ScrollPane.class, List.class, List.class},
                    new Object[]{pane, group_name, item_num},
                    new Class[0], new Object[0]);
        });

        hBox.getChildren().addAll(create_group, positioning_group);

        box.getChildren().addAll(hBox,
                WinTool.createLabel(0, 0, 0, 30, 0, ""));

        add_group(path, box);
    }

    /**
     * 用于在用户将记录类型调整为“坐标”时的最初显示
     *
     * @param dir 目录的文件类
     */
    private void add_group(String dir, VBox box) {
        ClassExpandTool adder = new ClassExpandTool("Modes" + File.separator + "PositionManager" + File.separator + "GroupAdder.class");
        adder.initialize_class("Modes.PositionManager.GroupAdder",
                new Class[]{VBox.class, List.class, List.class, List.class, String.class},
                new Object[]{box, item_num, group_value, group_name, dir}
        );     // 初始化class

        String[] list = new File(dir).list();
        for (int i = 0; i < list.length; i++) {
            String s = list[i];
            try {
                String[] temp = IOTool.read_file(dir + File.separator + s);
                List<String[]> value_group = new ArrayList<>();

                for (String value : temp) {
                    String[] add_array = EDTool.decrypt(value).split("\0");
                    value_group.add(add_array);
                }

                item_num.add(value_group.size());
                group_value.add(value_group);
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试或删除项目重新尝试");
            }

            group_name.add(s);

            // 执行GroupAdder中的add的操作
            adder.invoke_method("add",
                    new Class[]{List.class, String.class}, new Object[]{group_value.get(i), group_name.get(i)}
            );
        }
    }
}
