package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Modes.BehaviorManager.Todo.ED.Decryption;
import Modes.BehaviorManager.Todo.List.ListFinish;
import Modes.BehaviorManager.Todo.List.ShowLists;
import Tools.ColorTool;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ShowValues {
    private final Pane box;
    private final String path;
    private final String listName;
    private final DataController controller;
    private static int yCount;

    /**
     * @param box      显示控件的Pane
     * @param path     路径 存储的是绝对路径，路径到达计划表储存处，但是不到包含计划表
     * @param listName 计划表的名称
     */
    public ShowValues(Pane box, String path, String listName) {
        this.box = box;
        this.path = path;
        this.listName = listName;

        // 准备DataController的基础数据
        String readValues = String.join("", IOTool.readFile(new File(path, listName+".json").getPath()));
        // 进行解密
        JSONObject values = Decryption.decrypt(JSONObject.parseObject(readValues));
        // 赋值
        controller = new DataController(values);
    }

    public void entrance() {
        start(getDone());
    }

    private void start(boolean isDone) {
        // 初始化yCount
        yCount = 60;

        if (isDone) {
            Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                    "移动计划表", "本计划表已经完成", "是否将这一个计划表转移至完成区？");

            if (type.get() == ButtonType.OK) {
                ListFinish finisher = new ListFinish(controller);
                String return_value = finisher.entrance(path, listName);

                if (return_value != null) {
                    WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "已将计划表转移至完成区",
                            "转以后计划表名：" + return_value);

                    // 跳转到首页（显示计划表的页面）
                    ShowLists clazz = new ShowLists(box, path);
                    clazz.entrance();
                } else {
                    WinTool.createAlert(Alert.AlertType.ERROR, "失败", "转移失败", "");
                }
            }
        } else {
            // 重置
            box.getChildren().clear();

            Button return_menu = WinTool.createButton(0, 0, 120, 40, 18, "返回首页");
            return_menu.setOnAction(actionEvent -> {
                // 保存程序
                SaveValue.save(controller, path, listName, false);

                ShowLists clazz = new ShowLists(box, path);
                clazz.entrance();
            });

            Button save = WinTool.createButton(120, 0, 120, 40, 20, "保存");
            save.setOnAction(actionEvent ->
                    SaveValue.save(controller, path, listName, true));

            box.getChildren().addAll(return_menu, save);

            DataController.DataPack values = controller.getValues();
            addLabelToBox(values.getNote(), Color.BLUE, values, true);      // 添加标题

            // 添加信息  逐级添加，每增加一级，indent增加1
            updateScreen(values.getChildren(), 0);
        }
    }

    /**
     * @implNote 用于显示计划表内容的时候使用  递归使用
     */
    private void updateScreen(List<DataController.DataPack> children, int indent) {
        for (DataController.DataPack child : children) {
            String msg = "\t".repeat(indent) + "· " + child.getNote() + " " + child.getWay();
            addLabelToBox(msg, ColorTool.engToColor(child.getColor()), child, false);     // 添加项目

            // 递归
            updateScreen(child.getChildren(), indent+1);
        }
    }

    private boolean getDone() {
        return controller.getValues().isDone();
    }

    // 设置一个yCount值，初始值为60，是窗口上两个按钮占用的
    private void addLabelToBox(String labelValue, Color color, DataController.DataPack data, boolean isTitle) {
        HBox value_box = new HBox();
        value_box.setLayoutX(0);
        value_box.setLayoutY(yCount);

        Label label = WinTool.createLabel(0, 0, -1, 30, 25, labelValue, color);
        label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                label.setTextFill(new_value ? Color.PURPLE : color));

        // 注册label在右击时弹出的ContextMenu
        ContextMenu menu = new ContextMenu();

        MenuItem create = new MenuItem("创建内容");
        MenuItem delete = new MenuItem("删除内容");
        MenuItem change = new MenuItem("更改信息");

        create.setOnAction(actionEvent -> {
            CreateValue creator = new CreateValue(data);
            creator.entrance();

            start(false);    // 刷新（创建内容一定不可能完成）
        });
        delete.setOnAction(actionEvent -> {
            RemoveValue.remove(data);

            start(getDone());   // 刷新
        });
        change.setOnAction(actionEvent -> {
            SetValueData setter = new SetValueData(data);
            setter.entrance();

            start(getDone());   // 刷新
        });

        // 选择性添加控件，title和item的添加是不一样的
        if (isTitle) {
            menu.getItems().add(create);
        } else {
            menu.getItems().addAll(create, delete, change);
        }
        label.setContextMenu(menu);

        value_box.getChildren().add(label);

        // 复选框的加载
        if (color != Color.GREEN) {
            CheckBox checkBox = WinTool.createCheckBox(0, 0, 120, 30, 18, "完成");
            checkBox.selectedProperty().addListener((observableValue, old_type, new_type) -> {
                if (new_type) {
                    ValueFinish.entrance(data);
                }
                start(getDone());   // 刷新
            });
            value_box.getChildren().add(checkBox);
        }
        box.getChildren().add(value_box);

        yCount += 30;
    }
}
