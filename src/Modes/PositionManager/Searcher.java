package Modes.PositionManager;

import Modes.PositionManager.Group.CreateGroup;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

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
    public void entrance(Pane box, String path) {
        box.getChildren().clear();
        start(box, new File(path, "positions").getPath());
    }

    /**
     * @implNote 实际的入口点，仅可由同一类中的方法调用
     */
    private void start(Pane box, String path) {      // 传入path已经到了positions路径下了
        Button create_group = WinTool.createButton(0, 0, 130, 40, 20, "创建坐标组");
        create_group.setOnAction(actionEvent -> {
            CreateGroup creator = new CreateGroup(box, item_num, group_value, group_name, path);
            creator.entrance();
        });

        box.getChildren().addAll(create_group);

        addGroup(path, box);
    }

    /**
     * 用于在用户将记录类型调整为“坐标”时的最初显示
     *
     * @param dir 目录的文件类
     */
    private void addGroup(String dir, Pane box) {
        GroupAdder adder = new GroupAdder(box, group_value, group_name, dir);

        try {
            String[] list = new File(dir).list();

            for (int i=0; i<list.length; i++) {
                try {
                    String s = list[i];

                    String[] temp = IOTool.readFile(new File(dir, s).getPath());
                    List<String[]> piece_value = new ArrayList<>();

                    for (String value : temp) {
                        String[] add_array = EDTool.decrypt(value).split("\0");
                        piece_value.add(add_array);
                    }

                    item_num.add(piece_value.size());
                    group_value.add(piece_value);
                    group_name.add(s);

                    // 执行GroupAdder中的add的操作
                    adder.add(group_value.get(i), group_name.get(i));
                } catch (Exception e) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试或删除项目重新尝试");
                }
            }
        } catch (Exception ignored) {
            // 这里的报错忽略忽略的是当用户没有打开项目时出现的检索错误
        }
    }
}
