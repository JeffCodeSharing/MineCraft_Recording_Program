package Modes.PositionManager.Group;

import Interface.AbstractWindow;
import Modes.PositionManager.GroupAdder;
import Tools.WinTool;
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
import java.util.ArrayList;
import java.util.List;

/**
 * CreateGroup类实现了AbstractWindow接口和Application类，用于创建坐标组的窗口。
 */
public class CreateGroup extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final Pane box;
    private final String create_dir;
    private final List<Integer> item_num;
    private final List<List<String[]>> group_value;
    private final List<String> group_name;

    /**
     * CreateGroup类的构造函数，初始化创建坐标组所需的参数。
     *
     * @param box       坐标组显示的VBox
     * @param item_num        记录每个坐标组中已有的项目数
     * @param group_value  存储坐标组数据的列表的列表
     * @param group_name   存储坐标组名的列表
     * @param create_dir       坐标组所在文件夹的路径
     */
    public CreateGroup(Pane box, List<Integer> item_num, List<List<String[]>> group_value, List<String> group_name, String create_dir) {
        this.box = box;
        this.item_num = item_num;
        this.group_value = group_value;
        this.group_name = group_name;
        this.create_dir = create_dir;
    }

    @Override
    public String[] entrance() {
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

                group_value.add(new ArrayList<>());
                group_name.add(field.getText());

                // 更新
                GroupAdder adder = new GroupAdder(box, item_num, group_value, group_name, create_dir);
                adder.update(false);

                item_num.add(0);
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
        File createFile = new File(create_dir, name);

        // 检索是否存在一样名字的文件
        if (createFile.exists()) {
            return false;
        }

        // 创建文件
        try {
            createFile.createNewFile();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
