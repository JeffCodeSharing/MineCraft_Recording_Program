package Modes.BehaviorManager.Todo.Finish;

import Modes.BehaviorManager.Todo.List.ShowLists;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;

public class ShowFinishList {
    private final Pane box;
    private final String path;
    private static int y_count;

    /**
     * @param box  添加信息的Pane
     * @param path 一个绝对路径，到达finishList的存放地点（不包括指定的文件名）
     */
    public ShowFinishList(Pane box, String path) {
        this.box = box;
        this.path = path;
    }

    public void entrance() {
        // 初始化操作
        box.getChildren().clear();
        String[] listNames = new File(path).list();

        // 对listNames进行去后缀操作
        try {
            for (int i = 0; i < listNames.length; i++) {
                String name = listNames[i];
                int dotIndex = name.lastIndexOf(".");
                listNames[i] = name.substring(0, dotIndex);
            }
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "项目损坏", "");
        }

        // 添加控件
        Button returnMenu = WinTool.createButton(0,0, 120, 40, 16, "返回首页");
        returnMenu.setOnAction(event -> {
            ShowLists showLists = new ShowLists(box, new File(new File(path).getParent(), "doing").getPath());
            showLists.entrance();
        });

        Button removeList = WinTool.createButton(120, 0, 120, 40, 16, "删除计划表");
        removeList.setOnAction(event -> {
            RemoveFinishList remover = new RemoveFinishList(path, listNames);
            remover.entrance();

            // 刷新
            entrance();
        });

        box.getChildren().addAll(
                returnMenu, removeList
        );

        y_count = 60;
        for (String name:listNames) {
            addLabelToBox(name);
        }
    }

    private void addLabelToBox(String listName) {
        Label label = WinTool.createLabel(0, y_count, -1, 30, 25,
                "> " + listName, Color.BLUE);
        label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                label.setTextFill(new_value ? Color.PURPLE : Color.BLUE));
        label.setOnMousePressed(mouseEvent -> {
            ShowFinishValues clazz = new ShowFinishValues(box, path, listName);
            clazz.entrance();
        });
        box.getChildren().add(label);

        y_count += 30;
    }
}
