package Modes.BehaviorManager.Todo.Finish;

import Modes.BehaviorManager.Todo.List.ShowLists;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;

public class SearchFinishValue {
    private final VBox box;
    private final String path;
    private final String list_name;
    private final String[] file_values;

    /**
     * 构造函数
     * @param box VBox对象，用于展示UI界面
     * @param path 已完成任务列表所在路径
     * @param list_name 当前列表名称
     */
    public SearchFinishValue(VBox box, String path, String list_name) {
        this.box = box;
        this.path = path;
        this.list_name = list_name;

        String[] temp = IOTool.readFile(path + File.separator + list_name);
        if (temp == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试");
            this.file_values = new String[0];
        } else {
            this.file_values = temp;

            for (int i=0; i<file_values.length; i++) {
                file_values[i] = EDTool.decrypt(file_values[i]);
            }
        }
    }

    /**
     * 入口方法，开始展示已完成任务的详细内容
     */
    public void entrance() {
        box.getChildren().clear();

        // 创建返回首页和返回上级按钮，并设置点击事件
        HBox hBox = new HBox();
        Button return_menu = WinTool.createButton(0, 0, 120, 40, 18, "返回首页");
        return_menu.setOnAction(actionEvent -> {
            ShowLists clazz = new ShowLists(box, path.substring(0, path.length()-6) + "doing");
            clazz.entrance();
        });

        Button return_last = WinTool.createButton(0, 0, 120, 40, 20, "返回上级");
        return_last.setOnAction(actionEvent -> {
            SearchFinishList searcher = new SearchFinishList(box, path);
            searcher.entrance();
        });

        hBox.getChildren().addAll(return_menu, return_last);

        // 创建列表名标签
        Label title = WinTool.createLabelWithNoWidth(0, 0, 30, 25, list_name, Color.BLUE);

        // 向界面添加返回按钮、列表名和间距标签
        box.getChildren().addAll(hBox, WinTool.createLabel(0, 0, 0, 10, 0, ""),
                title, WinTool.createLabel(0, 0, 0, 10, 0, ""));    // 在列表名和信息之间增加空行

        // 遍历任务内容并展示
        for (String s : file_values) {
            Label label = WinTool.createLabel(0, 0, 530, 30, 25, s, Color.GREEN);
            box.getChildren().add(label);
        }
    }
}
