package Modes.BehaviorManager.Todo.List;

import Modes.BehaviorManager.NowDoing.ShowItems;
import Modes.BehaviorManager.Todo.Finish.ShowFinishList;
import Modes.BehaviorManager.Todo.Value.ShowValues;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;

/**
 * SearchList类提供了搜索和显示待办列表的功能。
 * 它包括创建、删除和更改待办列表的方法，以及查看已完成列表和当前活动列表的方法。
 */
public class ShowLists {
    private final Pane box;
    private final String path;
    private int y_count;

    /**
     * @param box  用于显示搜索结果的VBox。
     * @param path 包含待办列表的文件夹的路径。
     */
    public ShowLists(Pane box, String path) {
        this.box = box;
        this.path = path;
    }

    /**
     * 加载所有列表并显示。
     */
    public void entrance() {
        // 加载所有计划表
        String[] list = new File(path).list();

        // 清空Pane
        box.getChildren().clear();
        y_count = 0;

        // 绘制按钮
        Button create = WinTool.createButton(0, 0, 120, 40, 15, "创建新计划表");
        Button delete = WinTool.createButton(120, 0, 120, 40, 15, "删除计划表");
        Button change = WinTool.createButton(240, 0, 120, 40, 15, "更改计划表名");
        Button see_finish = WinTool.createButton(360, 0, 120, 40, 12, "已完成的计划表");
        Button now_doing = WinTool.createButton(480, 0, 120, 40, 16, "正在做");

        create.setOnAction(actionEvent -> {
            CreateList creator = new CreateList(path);
            creator.entrance();

            entrance();   // 重新加载
        });
        delete.setOnAction(actionEvent -> {
            RemoveList remover = new RemoveList(path, list);
            remover.entrance();

            entrance();   // 重新加载
        });
        change.setOnAction(actionEvent -> {
            SetListName setter = new SetListName(path, list);
            setter.entrance();

            entrance();   // 重新加载
        });
        see_finish.setOnAction(actionEvent -> {
            String finishPath = new File(new File(path).getParent(), "finish").getPath();

            ShowFinishList searcher = new ShowFinishList(box, finishPath);
            searcher.entrance();
        });
        now_doing.setOnAction(actionEvent -> {
            ShowItems clazz = new ShowItems(
                    new File(new File(path).getParent(), "now_doing").getPath());
            clazz.entrance();
        });

        box.getChildren().addAll(create, delete, change, see_finish, now_doing);

        // 绘制现有列表
        if (list != null) {
            for (String s : list) {
                // 去除.json后缀
                int dotIndex = s.lastIndexOf(".");
                if (dotIndex == -1) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "项目损坏", "");
                    return;
                } else {
                    String fileName = s.substring(0, dotIndex);
                    addLabelToBox(fileName);                      // 添加Label
                }
            }
        }
    }

    /**
     * 将指定列表名称的标签添加到VBox中。
     *
     * @param listName 要显示的列表的名称。
     */
    private void addLabelToBox(String listName) {
        Label label = WinTool.createLabel(0, 40+y_count, -1, 30, 25,
                "> " + listName, Color.BLUE);
        label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                label.setTextFill(new_value ? Color.PURPLE : Color.BLUE));
        label.setOnMousePressed(mouseEvent -> {
            ShowValues clazz = new ShowValues(box, path, listName);
            clazz.entrance();
        });
        box.getChildren().add(label);

        y_count += 30;
    }
}
