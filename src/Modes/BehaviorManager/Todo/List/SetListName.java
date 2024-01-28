package Modes.BehaviorManager.Todo.List;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

/**
 * 一个类，允许用户更改待办事项列表的名称。
 */
public class SetListName extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final String[] lists_name;
    private final String path;

    /**
     * 构造一个 ChangeListName 实例，使用给定的路径和列表名称。
     * @param path 列表的路径
     * @param lists_name 列表名称
     */
    public SetListName(String path, String[] lists_name) {
        this.path = path;
        this.lists_name = lists_name;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    /**
     * 添加UI控件，是主页，跳转主页时调用这一个函数
     */
    @Override
    public void drawControls(Group group) {
        global_stage.setHeight(480);  // 设置全局舞台的高度
        global_stage.setWidth(400);

        group.getChildren().clear();  // 清除组内的所有节点

        ListView<String> listView = WinTool.createListView(10, 40, 320, 300, lists_name);

        Button open = WinTool.createButton(160, 370, 80, 40, 18, "打开");
        Button cancel = WinTool.createButton(250, 370, 80, 40, 18, "取消");

        open.setOnAction(actionEvent -> showDetails(group, listView.getSelectionModel().getSelectedItem()));
        cancel.setOnAction(actionEvent -> global_stage.close());  // 取消按钮的事件处理程序

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 30, 25, "选择项目", Color.BLUE),
                listView,
                open, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("更改计划表名");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * 显示更改名称的详细信息界面，用户可以修改信息
     * @param group 组对象，用于添加控件
     * @param old_name 旧列表名称
     */
    private void showDetails(Group group, String old_name) {
        if (!(old_name == null) && !old_name.equals("")) {  // 检查 old_name 是否不为 null 或空
            global_stage.setHeight(310);  // 设置全局舞台的高度
            global_stage.setWidth(380);

            group.getChildren().clear();  // 清除组内的所有节点

            TextField name_field = WinTool.createTextField(90, 40, 260, 30, 15, old_name, "");  // 用于输入新名称的文本框

            Button confirm = WinTool.createButton(180, 210, 80, 40, 18, "确认");  // 确认按钮
            Button return_menu = WinTool.createButton(270, 210, 80, 40, 18, "返回");  // 返回按钮

            confirm.setOnAction(actionEvent -> afterConfirm(old_name, name_field.getText()));  // 确认按钮的事件处理程序
            return_menu.setOnAction(actionEvent -> drawControls(group));  // 返回按钮的事件处理程序

            group.getChildren().addAll(
                    WinTool.createLabel(0, 0, 100, 30, 25, "详细信息", Color.BLUE),
                    WinTool.createLabel(10, 40, 80, 30, 15, "列表名称:"), name_field,
                    confirm, return_menu
            );
        }
    }

    /**
     * 在确认更改名称后执行的操作。
     * @param old_name 旧列表名称
     * @param new_name 新列表名称
     */
    private void afterConfirm(String old_name, String new_name) {
        if (!(new_name == null) && !new_name.equals("")) {  // 检查 new_name 是否不为 null 或空
            File old_file = new File(path, old_name);
            File new_file = new File(path, new_name);
            if (old_file.renameTo(new_file)) {
                global_stage.close();  // 关闭全局舞台
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "无法更改名称", "请重试");  // 如果重命名失败，则显示错误提示
            }
        }
    }
}
