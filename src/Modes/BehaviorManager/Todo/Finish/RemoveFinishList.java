package Modes.BehaviorManager.Todo.Finish;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveFinishList extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final String[] listNames;
    private final String path;

    public RemoveFinishList(String path, String[] listNames) {
        this.listNames = listNames;
        this.path = path;
    }

    @Override
    public Void entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        AtomicBoolean select_all = new AtomicBoolean(false);

        // 提前声明
        ListView<String> listView = WinTool.createListView(10, 40, 340, 260, listNames);
        Button select_button = WinTool.createButton(10, 310, 80, 40, 18, "全选");

        // 一系列操作
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, old_value, new_value) ->
                select_all.set(false));

        select_button.setOnAction(actionEvent -> {     // ListView全选
            listView.getItems().forEach(item -> listView.getSelectionModel().select(item));
            select_all.set(true);
        });

        Button confirm = WinTool.createButton(180, 310, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(270, 310, 80, 40, 16, "取消");

        confirm.setOnAction(actionEvent -> afterConfirm(listView.getSelectionModel().getSelectedItem(), select_all.get()));
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 300, 40, 20, "删除计划表", Color.BLUE),
                listView, select_button,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("删除计划表");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(400);
        stage.showAndWait();
    }

    private void afterConfirm(String item, boolean select_all) {
        if (item != null) {
            if (select_all) {
                Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                        "删除列表", "您是否要删除所有", "删除后列表将不复存在");

                if (type.get() == ButtonType.OK) {
                    for (String s:listNames) {
                        File file = new File(path, s+".json");
                        if (!file.delete()) {
                            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "删除" + s + "失败", "请重新尝试");
                        }
                    }

                    global_stage.close();
                }
            } else {
                Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                        "删除列表", "您是否要删除这一个列表", "删除后列表将不复存在");

                if (type.get() == ButtonType.OK) {
                    File file = new File(path + File.separator + item);
                    if (file.delete()) {
                        global_stage.close();
                        WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "删除成功！", "");
                    } else {
                        WinTool.createAlert(Alert.AlertType.ERROR, "失败", "删除失败", "请重新尝试");
                    }
                }
            }
        }
    }
}
