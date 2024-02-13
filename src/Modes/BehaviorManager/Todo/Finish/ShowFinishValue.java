package Modes.BehaviorManager.Todo.Finish;

import Modes.BehaviorManager.Todo.List.ShowLists;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;

public class ShowFinishValue {
    private final Pane box;
    private final String path;
    private final String list_name;
    private final String[] file_values;
    private int y_count;

    /**
     * 构造函数
     * @param box VBox对象，用于展示UI界面
     * @param path 已完成任务列表所在路径
     * @param list_name 当前列表名称
     */
    public ShowFinishValue(Pane box, String path, String list_name) {
        this.box = box;
        this.path = path;
        this.list_name = list_name;
        this.y_count = 0;

        String[] temp = IOTool.readFile(path + File.separator + list_name);
        if (temp == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试");
            this.file_values = new String[0];
        } else {
            this.file_values = temp;

            Arrays.setAll(file_values, i -> EDTool.decrypt(file_values[i]));
        }
    }

    /**
     * 入口方法，开始展示已完成任务的详细内容
     */
    public void entrance() {
        box.getChildren().clear();

        // 创建返回首页和返回上级按钮，并设置点击事件
        Button return_menu = WinTool.createButton(0, 0, 120, 40, 18, "返回首页");
        return_menu.setOnAction(actionEvent -> {
            ShowLists clazz = new ShowLists(box, path.substring(0, path.length()-6) + "doing");
            clazz.entrance();
        });

        Button return_last = WinTool.createButton(120, 0, 120, 40, 20, "返回上级");
        return_last.setOnAction(actionEvent -> {
            ShowFinishList searcher = new ShowFinishList(box, path);
            searcher.entrance();
        });

        box.getChildren().addAll(return_menu, return_last,
                WinTool.createLabel(0, 50, -1, 30, 25, list_name, Color.BLUE));

        // 遍历任务内容并展示
        for (String s : file_values) {
            box.getChildren().addAll(WinTool.createLabel(0, 90+y_count, -1, 30, 25, s, Color.GREEN));
            y_count += 30;
        }
    }
}
