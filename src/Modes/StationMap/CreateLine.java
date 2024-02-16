package Modes.StationMap;

import Interface.AbstractWindow;
import Tools.ColorTool;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class CreateLine extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final Map<String, LineData> lines;

    public CreateLine(Map<String, LineData> lines) {
        this.lines = lines;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField line_name = WinTool.createTextField(110, 50, 150, 30, 16);
        TextField color_field = WinTool.createTextField(110, 90, 100, 30, 16);
        Rectangle color_rect = WinTool.createRectangle(220, 95, 20, 20, Color.WHITE);
        Button color_choose = WinTool.createButton(260, 90, 100, 30, 16, "选择颜色");
        Button confirm = WinTool.createButton(250, 250, 100, 30, 16, "确定");

        color_field.textProperty().addListener((observableValue, oldValue, newValue) ->
                setRectColor(color_rect, color_field)
        );
        color_choose.setOnAction(actionEvent -> {
            Color color = choose_color((Color) color_rect.getFill());
            if (color != null) {    // 用户没有选择颜色的情况下关闭窗口
                String webCode = ColorTool.colorToWebCode(color);
                color_field.setText(webCode);
            }
        });

        confirm.setOnAction(actionEvent -> {
            String lineName = line_name.getText();
            String lineColor = color_field.getText();
            // 检测lineName以及lineColor是否都已经填写并且填写正确
            if ((lineName != null) && (!lineName.equals("")) &&
                    (ColorTool.engToColor(lineColor) != null)) {
                lines.put(lineName, new LineData(new HashMap<>(), lineColor));
                global_stage.close();
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "创建线路成功", "线路名称" + lineName);
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "创建线路失败", "检查空是否都填写正确");
            }
        });

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 35, 30, "创建新线路", Color.BLUE),
                WinTool.createLabel(10, 50, 100, 30, 16, "线路名称:"), line_name,
                WinTool.createLabel(10, 90, 100, 30, 16, "线路颜色:"), color_field, color_rect, color_choose,
                WinTool.createLabel(10, 130, 300, 60, 16, "线路颜色建议使用“选择颜色获取”\n若手动填写，请在16进制颜色前加“#”号", Color.ORANGE),
                confirm
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建新线路");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(350);
        stage.showAndWait();
    }

    private Color choose_color(Color original_color) {
        ColorChooser chooser = new ColorChooser(original_color);
        return chooser.entrance();
    }

    /**
     * @implNote 这一个方法是用于更改Rectangle组件的颜色的
     */
    private void setRectColor(Rectangle rect, TextField field) {
        Color color = ColorTool.engToColor(field.getText());
        if (color != null) {
            rect.setFill(color);
        }
    }
}

class ColorChooser extends Application {
    private final Stage global_stage = new Stage();
    private final Color original_color;
    private static Color return_color = null;

    public ColorChooser(Color original_color) {
        this.original_color = original_color;
    }

    public Color entrance() {
        start(global_stage);
        return return_color;
    }

    public void drawControls(Group group) {
        ColorPicker picker = WinTool.createColorPicker(0, 0, 120, 30, original_color);

        Button confirm = WinTool.createButton(40, 40, 80, 30, 16, "确定");
        confirm.setOnAction(actionEvent -> {
            return_color = picker.getValue();
            global_stage.close();
        });

        group.getChildren().addAll(picker, confirm);
    }

    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("选择颜色");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(180);
        stage.setHeight(120);
        stage.showAndWait();
    }
}
