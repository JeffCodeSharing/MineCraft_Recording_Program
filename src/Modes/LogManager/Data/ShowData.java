package Modes.LogManager.Data;

import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据搜索类，用于搜索特定日期的数据。
 */
public class ShowData {
    private final Pane box;
    private final List<TextField> fields;
    private final List<TextArea> areas;
    private final boolean isUpdate;
    private final boolean can_start;      // 为了防止出现用户没有选择日期就查询的情况
    private final String datePath;

    /**
     * 构造函数，初始化 SearchData 类的实例。
     *
     * @param box 显示搜索结果的 VBox 对象。
     */
    public ShowData(Pane box, String projectPath, String date) {
        this.box = box;
        this.fields = new ArrayList<>();
        this.areas = new ArrayList<>();
        this.isUpdate = false;
        this.can_start = !((date == null) || date.equals(""));
        this.datePath = new File(projectPath, date).getPath();
    }

    public ShowData(Pane box, List<TextField> fields, List<TextArea> areas) {
        this.box = box;
        this.fields = fields;
        this.areas = areas;
        this.isUpdate = true;
        this.can_start = true;
        this.datePath = "";
    }

    /**
     * 搜索特定日期的数据。
     *
     * @implNote 该方法为外部调用的入口。
     */
    public void entrance() {
        if (can_start) {
            start();
        }
    }

    /**
     * 开始搜索数据的过程。
     *
     * @implNote 该方法为私有入口，外部无法调用。
     */
    private void start() {
        box.getChildren().clear();

        // 创建工具
        TextField eventPoint = WinTool.createTextField(80, 0, 350, 30, 15);
        Button create_button = WinTool.createButton(430, 0, 60, 30, 15, "创建");
        create_button.setOnAction(actionEvent -> {
            createPoint(eventPoint.getText(), "", fields, areas);
            eventPoint.setText("");    // 清空TextField
        });

        Button delete = WinTool.createButton(490, 0, 60, 30, 15, "删除");
        delete.setOnAction(actionEvent -> {
            RemoveData remover = new RemoveData(box, fields, areas);
            remover.entrance();
        });

        Button save = WinTool.createButton(550, 0, 60, 30, 15, "保存");
        save.setOnAction(actionEvent -> {
            SaveData saver = new SaveData();
            saver.entrance(datePath, fields, areas);
        });

        box.getChildren().addAll(
                WinTool.createLabel(0, 0, 80, 30, 15, "事件点名："),
                eventPoint, create_button, delete, save
        );

        // 创建控件
        if (isUpdate) {
            for (int i = 0; i < fields.size(); i++) {
                String fieldData = fields.get(i).getText();
                String areaData = areas.get(i).getText();
                createPoint(fieldData, areaData, i);
            }
        } else {
            String[] temp_array = IOTool.readFile(new File(datePath, "simple_data").getPath());
            if (temp_array == null) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试");
            } else {
                for (int i = 0; i < temp_array.length; i += 2) {
                    String fieldData = EDTool.decrypt(temp_array[i]);
                    String areaData = EDTool.decrypt(temp_array[i + 1]).replace("\0", "\n");
                    createPoint(fieldData, areaData, fields, areas);
                }
            }
        }
    }

    /**
     * 创建事件点，并添加到界面中。
     *
     * @param fieldValue TextField 在文件中的储存值。
     * @param areaValue TextArea 在文件中的储存值。
     * @param fields TextField 对象的列表。
     * @param areas TextArea 对象的列表。
     */
    private void createPoint(String fieldValue, String areaValue, List<TextField> fields, List<TextArea> areas) {
        int posType = fields.size();

        TextField textfield = WinTool.createTextField(0, posType*360+60+70, 500, 30, 15, fieldValue, "");
        TextArea textArea = WinTool.createTextArea(0, posType*360+60+130, 500, 200, 15, true, areaValue);

        fields.add(textfield);
        areas.add(textArea);
        box.getChildren().addAll(
                WinTool.createLabel(0, posType*360+60, 100, 40, 20, "事件点" + (posType + 1), Color.BLUE),
                WinTool.createLabel(0, posType*360+60+40, 100, 30, 15, "纲要"), textfield,
                WinTool.createLabel(0, posType*360+60+100, 100, 30, 15, "内容"), textArea
        );
    }

    /**
     * 创建事件点，并添加到界面中（更新时）
     *
     * @param fieldValue 创建的TextField中的值。
     * @param areaValue 创建的TextArea中的值。
     */
    private void createPoint(String fieldValue, String areaValue, int posType) {
        TextField textfield = WinTool.createTextField(0, posType*360+60+70, 500, 30, 15, fieldValue, "");
        TextArea textArea = WinTool.createTextArea(0, posType*360+60+130, 500, 200, 15, true, areaValue);

        box.getChildren().addAll(
                WinTool.createLabel(0, posType*360+60, 100, 40, 20, "事件点" + (posType + 1), Color.BLUE),
                WinTool.createLabel(0, posType*360+60+40, 100, 30, 15, "纲要"), textfield,
                WinTool.createLabel(0, posType*360+60+100, 100, 30, 15, "内容"), textArea
        );
    }
}

