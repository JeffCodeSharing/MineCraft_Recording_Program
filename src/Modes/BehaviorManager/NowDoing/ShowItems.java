package Modes.BehaviorManager.NowDoing;

import Interface.AbstractWindow;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理正在做的任务的窗口类。
 */
public class ShowItems extends Application implements AbstractWindow {
    private final List<String> file_data;
    private final String path;     // 已包含到now_doing文件
    private final Stage global_stage = new Stage();
    private ListView<String> listView;

    /**
     * 创建一个NowDoing对象，用于管理正在做的任务。
     *
     * @param path 用于保存正在做的任务的文件路径
     */
    public ShowItems(String path) {
        this.path = path;

        List<String> temp = IOTool.readFileAsArrayList(path);
        if (temp == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "读取错误", "读取文件错误", "请重新尝试");
            file_data = new ArrayList<>();
        } else {
            file_data = temp;

            // 解密处理
            file_data.replaceAll(EDTool::decrypt);
        }
    }

    /**
     * 窗口的入口方法，在该方法中启动窗口并等待窗口关闭。
     *
     * @return 返回null
     */
    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    /**
     * 绘制窗口组的控件。
     *
     * @implNote 是“正在做”窗口的主页
     * @param group 用于添加控件的 JavaFX Group 对象
     */
    @Override
    public void drawControls(Group group) {
        listView = WinTool.createListView(0, 40, 480, 300);
        updateListView();

        Button create = WinTool.createButton(210, 360, 80, 40, 16, "创建");
        Button finish = WinTool.createButton(300, 360, 80, 40, 16, "完成");
        Button change = WinTool.createButton(390, 360, 80, 40, 16, "更改");
        create.setOnAction(actionEvent -> {
            CreateItem creator = new CreateItem(file_data);
            creator.entrance();

            updateListView();   // 刷新
        });
        finish.setOnAction(actionEvent -> finishItem(listView.getSelectionModel().getSelectedIndex()));
        change.setOnAction(actionEvent -> {
            SetItemData setter = new SetItemData(file_data, listView.getSelectionModel().getSelectedIndex());
            setter.entrance();

            updateListView();   // 刷新
        });

        Button save = WinTool.createButton(300, 410, 80, 40, 16, "保存");
        Button cancel = WinTool.createButton(390, 410, 80, 40, 16, "关闭");
        save.setOnAction(actionEvent -> saveValues(true));     // 弹出提示框，主动保存
        cancel.setOnAction(actionEvent -> {
            saveValues(false);     // 不弹出提示框，被动保存
            global_stage.close();
        });

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 480, 30, 25, "正在做", Color.BLUE),
                listView,
                create, finish, change,
                save, cancel
        );
    }

    /**
     * 启动窗口并显示。
     *
     * @param stage JavaFX Stage 对象
     */
    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("正在做");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(540);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * 保存任务列表到文件。
     *
     * @param type true --> 弹出提示框; false --> 不弹出提示框
     */
    private void saveValues(boolean type) {
        // 加密处理
        file_data.replaceAll(EDTool::encrypt);

        boolean save_type = IOTool.overrideFile(path, file_data);
        if (type) {
            if (save_type) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功！", "保存成功", "");
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "保存失败", "请重新尝试");
            }
        }
    }

    /**
     * 完成任务。
     *
     * @param index 用户点击的索引
     */
    private void finishItem(int index) {
        if (index != -1) {
            file_data.remove(index);
            updateListView();
        }
    }

    /**
     * 更新任务列表视图。
     */
    private void updateListView() {
        listView.getItems().clear();
        for (int i=0; i<file_data.size(); i++) {
            String s = file_data.get(i);
            listView.getItems().add((i+1) + ": " + s);
        }
    }
}
