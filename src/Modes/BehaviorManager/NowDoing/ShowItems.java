package Modes.BehaviorManager.NowDoing;

import Interface.AbstractWindow;
import Tools.EDTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

public class ShowItems extends Application implements AbstractWindow<Void> {
    private final String path;
    private final List<String> data = new ArrayList<>();
    private final Stage global_stage = new Stage();
    private ListView<String> listView;

    /**
     * @param path 一个绝对路径，路径直到NowDoing的储存文件
     */
    public ShowItems(String path) {
        this.path = path;

        // 初始化data
        try {
            JSONObject jsonObject = JsonTool.readJson(this.path);
            for (Object tempObj : jsonObject.getJSONArray("data")) {
                String childData = (String) tempObj;
                data.add(EDTool.decrypt(childData));
            }
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "文件读取错误", "请重试");
        }
    }

    @Override
    public Void entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        listView = WinTool.createListView(0, 40, 480, 300);
        updateListView();

        Button create = WinTool.createButton(210, 360, 80, 40, 16, "创建");
        Button finish = WinTool.createButton(300, 360, 80, 40, 16, "完成");
        Button change = WinTool.createButton(390, 360, 80, 40, 16, "更改");
        create.setOnAction(actionEvent -> {
            CreateItem creator = new CreateItem(data);
            creator.entrance();

            updateListView();   // 刷新
        });
        finish.setOnAction(actionEvent -> finishItem(listView.getSelectionModel().getSelectedIndex()));
        change.setOnAction(actionEvent -> {
            SetItemData setter = new SetItemData(data, listView.getSelectionModel().getSelectedIndex());
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
     * @param showAlert true --> 弹出提示框; false --> 不弹出提示框
     */
    private void saveValues(boolean showAlert) {
        // 加密处理
        JSONObject encryptJson = new JSONObject();
        JSONArray dataArray = new JSONArray();
        encryptJson.put("data", dataArray);

        for (String s:data) {
            dataArray.add(EDTool.encrypt(s));
        }

        boolean success = JsonTool.writeJson(encryptJson, path);
        if (showAlert) {
            if (success) {
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
            data.remove(index);
            updateListView();
        }
    }

    /**
     * 更新任务列表视图。
     */
    private void updateListView() {
        listView.getItems().clear();
        for (int i=0; i<data.size(); i++) {
            String s = data.get(i);
            listView.getItems().add((i+1) + ": " + s);
        }
    }
}
