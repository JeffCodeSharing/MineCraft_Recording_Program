package Modes.BehaviorManager.Todo.Finish;

import Tools.ClassTool;
import Tools.WinTool;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;

/**
 * SearchFinishList类用于展示已完成任务列表
 */
public class SearchFinishList {
    private final VBox box;
    private final String path;     // 已包含到finish文件夹的路径

    /**
     * 构造函数
     * @param box VBox对象，用于展示UI界面
     * @param path 已完成任务列表所在路径
     */
    public SearchFinishList(VBox box, String path) {
        this.box = box;
        this.path = path;
    }

    /**
     * 入口方法，开始展示已完成任务列表
     * 也用于刷新
     */
    public void entrance() {
        // 刷新计划表
        String[] list = new File(path).list();
        list = (list == null) ? new String[0] : list;

        box.getChildren().clear();

        HBox hBox = new HBox();

        Button return_to_menu = WinTool.createButton(0, 0, 120, 40, 16, "返回首页");
        return_to_menu.setOnAction(actionEvent -> {
            String doing_path = path.substring(0, path.length()-6) + "doing";

            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "List" + File.separator + "ShowLists.class");
            Class<?> searcher = tool.get_class("Modes.BehaviorManager.Todo.List.ShowLists");
            tool.invoke_method(searcher, "entrance",
                    new Class[]{VBox.class, String.class}, new Object[]{box, doing_path}, new Class[0], new Object[0]);
        });

        Button remove_list = WinTool.createButton(0, 0, 120, 40, 15, "删除计划表");

        final String[] finalList = list;
        remove_list.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "Finish" + File.separator + "RemoveFinishList.class");
            Class<?> searcher = tool.get_class("Modes.BehaviorManager.Todo.Finish.RemoveFinishList");
            tool.invoke_method(searcher, "entrance",
                    new Class[]{String[].class, String.class}, new Object[]{finalList, path}, new Class[0], new Object[0]);

            // 刷新
            entrance();
        });

        hBox.getChildren().addAll(return_to_menu, remove_list);

        box.getChildren().addAll(
                hBox, WinTool.createLabel(0, 0, 0, 10, 0, "")
        );

        // 绘制列表
        for (String s : list) {
            add_label_to_box(s);
        }
    }

    /** 私有方法，将任务名称添加到界面中
     * @param value 传入要打开的计划表
     */
    private void add_label_to_box(String value) {
        Label label = WinTool.createLabel(0, 0, 530, 30, 25, "> " + value, Color.BLUE);
        label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                label.setTextFill(new_value ? Color.PURPLE : Color.BLUE));
        label.setOnMousePressed(mouseEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "Finish" + File.separator + "SearchFinishValue.class");
            Class<?> searcher = tool.get_class("Modes.BehaviorManager.Todo.Finish.SearchFinishValue");
            tool.invoke_method(searcher, "entrance",
                    new Class[]{VBox.class, String.class, String.class}, new Object[]{box, path, value},
                    new Class[0], new Object[0]);
        });
        box.getChildren().add(label);
    }
}
