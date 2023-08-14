package Modes.BehaviorManager.Todo.Value;

import Tools.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 模块：行为管理器
 * 类名：SearchValue
 * 用途：用于搜索值的操作类
 */
public class ShowValues {
    private final VBox box;
    private final String path;
    private final String list_name;
    private final ClassExpandTool controller;

    /**
     * 构造方法
     * @param box 垂直盒子容器
     * @param path 文件路径
     * @param list_name 列表名称
     */
    public ShowValues(VBox box, String path, String list_name) {
        this.box = box;
        this.path = path;
        this.list_name = list_name;

        // 准备DataController的基础数据
        String[] values = IOTool.read_file(path + File.separator + list_name);

        // 进行解密
        if (values == null) {
            values = new String[0];
        } else {
            for (int i=0; i<values.length; i++) {
                values[i] = EDTool.decrypt(values[i]);
            }
        }

        controller = new ClassExpandTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                "Todo" + File.separator + "DataController.class");
        controller.initialize_class("Modes.BehaviorManager.Todo.DataController",
                new Class[]{String[].class}, new Object[]{values});    // 将controller实例化
    }

    /**
     * 入口方法，用于启动搜索操作
     */
    public void entrance() {
        start(get_done());
    }

    /**
     * 启动搜索操作
     * @param is_done 指示计划表是否已完成
     */
    @SuppressWarnings("unchecked")
    private void start(boolean is_done) {
        // 清空box
        box.getChildren().clear();

        HBox hBox = new HBox();
        Button return_menu = WinTool.createButton(0, 0, 120, 40, 18, "返回首页");
        return_menu.setOnAction(actionEvent -> {
            // 保存程序
            save_data((List<String[]>) controller.invoke_method("getValues", new Class[0], new Object[0]));

            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "List" + File.separator + "ShowLists.class");
            Class<?> searcher = tool.get_class("Modes.BehaviorManager.Todo.List.ShowLists");
            tool.invoke_method(searcher, "entrance",
                    new Class[]{VBox.class, String.class}, new Object[]{box, path}, new Class[0], new Object[0]);
        });

        Button save = WinTool.createButton(0, 0, 120, 40, 20, "保存");
        save.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "Value" + File.separator + "SaveValue.class");
            Class<?> searcher = tool.get_class("Modes.BehaviorManager.Todo.Value.SaveValue");
            tool.invoke_method(searcher, "entrance",
                    new Class[]{ClassExpandTool.class, String.class, String.class}, new Object[]{controller, path, list_name});
        });

        hBox.getChildren().addAll(return_menu, save);

        box.getChildren().addAll(hBox, WinTool.createLabel(0, 0, 0, 10, 0, ""));
        add_label_to_box(list_name, Color.BLUE, get_done(), -1);    // 添加列表名，index为-1，用于在末尾添加
        box.getChildren().add(WinTool.createLabel(0, 0, 0, 10, 0, ""));    // 在列表名和信息之间增加空行

        List<String[]> values = (List<String[]>) controller.invoke_method("getValues", new Class[0], new Object[0]);
        for (int i=0; i<values.size(); i++) {
            String[] temp = values.get(i);
            add_label_to_box(temp[0], ColorTool.english_to_color(temp[1]), temp[1].equals("GREEN"), i);
        }

        // 当is_done为true时，弹出弹窗，问使用者是否转入finish中
        if (is_done) {
            Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                    "移动计划表", "本计划表已经完成", "是否将这一个计划表转移至完成区？");

            if (type.get() == ButtonType.OK) {
                ClassTool finish_tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                        "Todo" + File.separator + "List" + File.separator + "ListFinish.class");
                Class<?> changer = finish_tool.get_class("Modes.BehaviorManager.Todo.List.ListFinish");
                String return_value = (String) finish_tool.invoke_method(changer, "entrance",
                        new Class[]{ClassExpandTool.class}, new Object[]{controller},
                        new Class[]{String.class, String.class}, new Object[]{path, list_name});

                if (!(return_value == null)) {
                    WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "已将计划表转移至完成区",
                            "转以后计划表名：" + return_value);

                    ClassTool search_tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                            "Todo" + File.separator + "List" + File.separator + "ShowLists.class");
                    Class<?> searcher = search_tool.get_class("Modes.BehaviorManager.Todo.List.ShowLists");
                    search_tool.invoke_method(searcher, "entrance",
                            new Class[]{VBox.class, String.class}, new Object[]{box, path}, new Class[0], new Object[0]);
                } else {
                    WinTool.createAlert(Alert.AlertType.ERROR, "失败", "转移失败", "");
                }
            }
        }
    }

    /**
     * 将标签添加到垂直盒子容器中
     * @param label_value 标签值
     * @param color 颜色
     * @param is_done 标记是否已完成
     * @param index 索引值
     */
    private void add_label_to_box(String label_value, Color color, boolean is_done, int index) {
        HBox hBox = new HBox();

        Label label = create_label(label_value, color);
        label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                label.setTextFill(new_value ? Color.PURPLE : color));

        // 注册label在右击时弹出的ContextMenu
        ContextMenu menu = new ContextMenu();

        MenuItem create = new MenuItem("创建内容");
        MenuItem delete = new MenuItem("删除内容");
        MenuItem change = new MenuItem("更改信息");

        create.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "Value" + File.separator + "CreateValue.class");
            Class<?> creator = tool.get_class("Modes.BehaviorManager.Todo.Value.CreateValue");
            tool.invoke_method(creator, "entrance",
                    new Class[]{ClassExpandTool.class, int.class}, new Object[]{controller, index}, new Class[0], new Object[0]);

            start(get_done());    // 刷新
        });
        delete.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "Value" + File.separator + "RemoveValue.class");
            Class<?> deleter = tool.get_class("Modes.BehaviorManager.Todo.Value.RemoveValue");
            tool.invoke_method(deleter, "entrance",
                    new Class[]{ClassExpandTool.class, int.class}, new Object[]{controller, index});

            start(get_done());   // 刷新
        });
        change.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                    "Todo" + File.separator + "Value" + File.separator + "SetValueData.class");
            Class<?> changer = tool.get_class("Modes.BehaviorManager.Todo.Value.SetValueData");
            tool.invoke_method(changer, "entrance",
                    new Class[]{ClassExpandTool.class, int.class}, new Object[]{controller, index}, new Class[0], new Object[0]);

            start(get_done());   // 刷新
        });

        // 选择性添加控件，tile和item的添加是不一样的
        if (index == -1) {
            menu.getItems().add(create);
        } else {
            menu.getItems().addAll(create, delete, change);
        }
        label.setContextMenu(menu);

        hBox.getChildren().add(label);

        // 复选框的加载
        if (color != Color.GREEN) {
            if (!is_done) {
                CheckBox checkBox = WinTool.createCheckBox(0, 0, 120, 30, 18, "完成");
                checkBox.selectedProperty().addListener((observableValue, old_type, new_type) -> {
                    if (new_type) {
                        ClassTool tool = new ClassTool("Modes" + File.separator + "BehaviorManager" + File.separator +
                                "Todo" + File.separator + "Value" + File.separator + "ValueFinish.class");
                        Class<?> changer = tool.get_class("Modes.BehaviorManager.Todo.Value.ValueFinish");
                        tool.invoke_method(changer, "entrance",
                                new Class[]{ClassExpandTool.class, int.class}, new Object[]{controller, index}, new Class[0], new Object[0]);
                    }
                    start(get_done());   // 刷新
                });
                hBox.getChildren().add(checkBox);
            }
        }

        box.getChildren().add(hBox);
    }

    /**
     * 创建带有颜色的标签
     * @param data 标签内容
     * @param color 颜色
     * @return 创建的标签对象
     */
    private Label create_label(String data, Color color) {    // 不使用WinTool.createLabel原因是因为这个create_label不设置宽
        Label label = new Label(data + "  ");     // 添加两格的空格用于在后面添加的SelectButton进行区分
        label.setMaxHeight(30);
        label.setFont(Font.font(25));    // 将font数值设置为固定的25
        label.setTextFill(color);
        return label;
    }

    /**
     * 检查是否所有值的第二个属性都为GREEN
     * @return 如果是则返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    private boolean get_done() {
        List<String[]> values = (List<String[]>) controller.invoke_method("getValues", new Class[0], new Object[0]);

        boolean is_done = values.size() != 0;      // 当values是空的时，is_done仍然为false;
        if (is_done) {
            for (String[] temp : values) {
                if (!temp[1].equals("GREEN")) {
                    is_done = false;
                    break;
                }
            }
        }
        return is_done;
    }

    /**
     * 保存数据的方法，被动保存，没有Alert提示
     * @param values 值列表
     */
    private void save_data(List<String[]> values) {
        List<String> write_in_list = new ArrayList<>();

        for (String[] strings:values) {
            write_in_list.add(EDTool.encrypt(strings[0] + "\0" + strings[1]));
        }

        IOTool.override_file(path + File.separator + list_name, write_in_list);
    }
}
