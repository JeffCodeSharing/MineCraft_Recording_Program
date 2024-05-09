package Modes.BehaviorManager.Todo.List;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

/**
 * 删除计划表的类。
 */
public class RemoveList extends Application implements AbstractWindow {
    private final String path;
    private final String[] planList;
    private final Stage global_stage = new Stage();

    /**
     * 构造一个DeleteList对象，指定路径和计划表列表。
     *
     * @param path   删除计划表的路径
     * @param list   计划表列表
     */
    public RemoveList(String path, String[] list) {
        this.path = path;

        // 处理planList中的数据，将.json后缀去除，然后赋值
        if (list == null || list.length == 0) {
            this.planList = new String[0];
        } else {
            try {
                for (int i = 0; i < list.length; i++) {
                    int dotIndex = list[i].lastIndexOf(".");
                    list[i] = list[i].substring(0, dotIndex);
                }
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "项目损坏", "");
            }
            this.planList = list;
        }
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        ListView<String> listView = WinTool.createListView(20, 40, 400, 350, planList);

        Button confirm = WinTool.createButton(250, 400, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(340, 400, 80, 40, 16, "取消");

        confirm.setOnAction(actionEvent -> afterConfirm(listView.getSelectionModel().getSelectedItem()));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 120, 30, 18, "删除计划表", Color.BLUE),
                listView,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("删除计划表");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.showAndWait();
    }

    /**
     * ”确定“按钮的调用方法。
     * 删除选中的计划表名
     *
     * @param choice 选中的计划表名称
     */
    private void afterConfirm(String choice) {
        if ((choice != null) && (!choice.equals(""))) {
            File file = new File(path, choice +".json");

            if (!file.delete()) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "删除失败", "请重新尝试");
            } else {
                global_stage.close();
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "删除成功", "");
            }
        }
    }
}
