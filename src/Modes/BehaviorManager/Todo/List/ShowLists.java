package Modes.BehaviorManager.Todo.List;

import Modes.BehaviorManager.NowDoing.ShowItems;
import Modes.BehaviorManager.Todo.Finish.SearchFinishList;
import Modes.BehaviorManager.Todo.Value.ShowValues;
import Tools.WinTool;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

    /**
     * @param box 用于显示搜索结果的VBox。
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
        // 加载所有列表
        File file = new File(path);
        String[] list = file.list();

        // 清空VBox
        box.getChildren().clear();

        // 绘制按钮
        HBox hBox = new HBox();
        Button create = WinTool.createButton(0, 0, 120, 40, 15, "创建新计划表");
        Button delete = WinTool.createButton(0, 0, 120, 40, 15, "删除计划表");
        Button change = WinTool.createButton(0, 0, 120, 40, 15, "更改计划表名");
        Button see_finish = WinTool.createButton(0, 0, 120, 40, 12, "已完成的计划表");
        Button now_doing = WinTool.createButton(0, 0, 120, 40, 16, "正在做");

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
            String path_for_finish = path.substring(0, path.length()-5) + "finish";

            SearchFinishList searcher = new SearchFinishList(box, path_for_finish);
            searcher.entrance();
        });
        now_doing.setOnAction(actionEvent -> {
            ShowItems clazz = new ShowItems(path.substring(0, path.length()-5) + "now_doing");
            clazz.entrance();
        });

        hBox.getChildren().addAll(create, delete, change, see_finish, now_doing);
        box.getChildren().add(hBox);

        // 绘制现有列表
        if (list != null) {
            for (String s : list) {
                addLabelToBox(s);
            }
        }
    }

    /**
     * 将指定列表名称的标签添加到VBox中。
     *
     * @param list_name 要显示的列表的名称。
     */
    private void addLabelToBox(String list_name) {
        Label label = WinTool.createLabel(0, 0, 530, 30, 25, "> " + list_name, Color.BLUE);
        label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                label.setTextFill(new_value ? Color.PURPLE : Color.BLUE));
        label.setOnMousePressed(mouseEvent -> {
            ShowValues clazz = new ShowValues(box, path, list_name);
            clazz.entrance();
        });
        box.getChildren().add(label);
    }
}
