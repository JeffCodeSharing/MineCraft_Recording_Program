package Modes.LogManager.Data;

import Tools.ClassTool;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据搜索类，用于搜索特定日期的数据。
 */
public class ShowData {
    private final VBox box;

    /**
     * 构造函数，初始化 SearchData 类的实例。
     *
     * @param box 显示搜索结果的 VBox 对象。
     */
    public ShowData(VBox box) {
        this.box = box;
    }

    /**
     * 搜索特定日期的数据。
     *
     * @param project_path 项目的路径。
     * @param date 用户选择的日期。
     * @implNote 该方法为外部调用的入口。
     */
    public void entrance(String project_path, String date) {
        if (!(date == null)) {
            if (!date.equals("")) {
                start(project_path + File.separator + date);
            }
        }
    }

    /**
     * 开始搜索数据的过程。
     *
     * @param date_path 日期的路径。
     * @implNote 该方法为私有入口，外部无法调用。
     */
    private void start(String date_path) {
        box.getChildren().clear();
        List<TextField> fields = new ArrayList<>();
        List<TextArea> areas = new ArrayList<>();

        // 创建工具
        HBox hbox = new HBox();
        Label create_point_label = WinTool.createLabel(0, 0, 80, 30, 15, "事件点名：");
        TextField point = WinTool.createTextField(0, 0, 350, 30, 15);
        Button create_button = WinTool.createButton(0, 0, 60, 30, 15, "创建");
        create_button.setOnAction(actionEvent -> {
            create_point(point.getText(), "", fields, areas);
            point.setText("");    // 清空TextField
        });

        Button delete = WinTool.createButton(0, 0, 60, 30, 15, "删除");
        delete.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "LogManager" + File.separator +
                    "Data" + File.separator + "RemoveData.class");
            Class<?> deleter = tool.get_class("Modes.LogManager.Data.RemoveData");
            tool.invoke_method(deleter, "entrance",
                    new Class[]{VBox.class, List.class, List.class}, new Object[]{box, fields, areas},
                    new Class[0], new Object[0]);
        });

        Button save = WinTool.createButton(0, 0, 60, 30, 15, "保存");
        save.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "LogManager" + File.separator +
                    "Data" + File.separator + "SaveData.class");
            Class<?> saver = tool.get_class("Modes.LogManager.Data.SaveData");
            tool.invoke_method(saver, "entrance",
                    new Class[]{String.class, List.class, List.class}, new Object[]{date_path, fields, areas});
        });

        hbox.getChildren().addAll(create_point_label, point, create_button, delete, save);

        box.getChildren().addAll(hbox, WinTool.createLabel(0, 0, 0, 30, 0, ""));

        // 创建控件
        String[] temp_array = IOTool.read_file(date_path + File.separator + "simple_data");
        if (temp_array == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试");
        } else {
            for (int i = 0; i < temp_array.length; i += 2) {
                String field_data = EDTool.decrypt(temp_array[i]);
                String area_data = EDTool.decrypt(temp_array[i+1]).replace("\0", "\n");
                create_point(field_data, area_data, fields, areas);
            }
        }
    }

    /**
     * 创建事件点，并添加到界面中。
     *
     * @param field_value TextField 在文件中的储存值。
     * @param area_value TextArea 在文件中的储存值。
     * @param fields TextField 对象的列表。
     * @param areas TextArea 对象的列表。
     */
    private void create_point(String field_value, String area_value, List<TextField> fields, List<TextArea> areas) {
        int num = fields.size() + 1;

        Label label1 = WinTool.createLabel(0, 0, 100, 40, 20, "事件点" + num, Color.BLUE);
        Label label2 = WinTool.createLabel(0, 0, 100, 30, 15, "纲要：");
        TextField textfield = WinTool.createTextField(0, 0, 500, 30, 15, field_value, "");
        Label label3 = WinTool.createLabel(0, 0, 100, 30, 15, "内容");
        TextArea textArea = WinTool.createTextArea(0, 0, 500, 200, 15, true, area_value);
        Label label4 = WinTool.createLabel(0, 0, 100, 40, 20, "");

        fields.add(textfield);
        areas.add(textArea);
        box.getChildren().addAll(label1, label2, textfield, label3, textArea, label4);
    }
}

