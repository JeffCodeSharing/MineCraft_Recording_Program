package Modes.PositionManager.Group;

import Interface.AbstractWindow;
import Modes.PositionManager.Event.GroupEvent;
import Modes.PositionManager.GroupAdder;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * CreateGroup类实现了AbstractWindow接口和Application类，用于创建坐标组的窗口。
 */
public class CreateGroup extends Application implements AbstractWindow<Void> {
    private final Stage global_stage = new Stage();
    private final Pane box;
    private final String create_dir;
    private final List<GroupEvent> group_values;

    /**
     * CreateGroup类的构造函数，初始化创建坐标组所需的参数。
     *
     * @param box       坐标组显示的VBox
     * @param group_values  存储坐标组数据的列表的列表
     * @param create_dir       坐标组所在文件夹的路径
     */
    public CreateGroup(Pane box, List<GroupEvent> group_values, String create_dir) {
        this.box = box;
        this.group_values = group_values;
        this.create_dir = create_dir;
    }

    @Override
    public Void entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField field = WinTool.createTextField(60, 40, 210, 30, 15);

        Button confirm = WinTool.createButton(100, 110, 80, 40, 15, "确定");
        confirm.setOnAction(actionEvent -> {
            if (createFile(field.getText())) {
                global_stage.close();

                // 因为用户输入的组不包括".json"后缀
                group_values.add(new GroupEvent(field.getText()+".json"));

                // 更新
                GroupAdder adder = new GroupAdder(box, group_values, create_dir);
                adder.update(false);
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "创建失败", "创建失败", "原因可能：\n记录文件创建失败或组名已经存在");
            }
        });
        Button cancel = WinTool.createButton(190, 110, 80, 40, 15, "取消");
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 30, 20, "创建新坐标组", Color.BLUE),
                WinTool.createLabel(10, 40, 60, 30, 17,  "组名："), field,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建坐标组");
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(200);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * 在文件系统中创建相应的记录文件。
     *
     * @param name 要插入的组的名称
     * @return 插入成功返回true，否则返回false
     */
    private boolean createFile(String name) {
        File createFile = new File(create_dir, name+".json");

        // 检索是否存在一样名字的文件
        if (createFile.exists()) {
            return false;
        }

        // 创建文件
        try {
            createFile.createNewFile();
            JSONObject initMsg = new JSONObject();
            initMsg.put("data", new JSONArray());
            JsonTool.writeJson(initMsg, createFile);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
