package Modes.StationMap;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

public class CreateStation extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final Map<String, LineData> data;
    private final Map<String, Integer[]> linePassed;
    private final String lineName;
    private String typeBoxDefault = "坐标创建";

    public CreateStation(Map<String, LineData> data, Map<String, Integer[]> linePassed, String lineName) {
        this.data = data;
        this.linePassed = linePassed;
        this.lineName = lineName;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        // 刷新清空
        group.getChildren().clear();

        ComboBox<String> createTypeBox = WinTool.createComboBox(90, 40, 150, 30, false, typeBoxDefault,
                "坐标创建", "站点名创建");
        createTypeBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            typeBoxDefault = newValue;
            drawControls(group);
        });

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 35, 30, "创建站点", Color.BLUE),
                WinTool.createLabel(10, 40, 80, 30, 16, "创建方式:"), createTypeBox
        );

        if (typeBoxDefault.equals("坐标创建")) {
            TextField fieldX = WinTool.createTextField(40, 80, 120, 30, 16);
            TextField fieldZ = WinTool.createTextField(200, 80, 120, 30, 16);
            TextField nameField = WinTool.createTextField(90, 110, 150, 30, 16);

            Button confirm = WinTool.createButton(200, 250, 80, 40, 20, "确定");
            confirm.setOnAction(actionEvent -> {
                boolean success = create(fieldX.getText(), fieldZ.getText(), nameField.getText());
                if (success) {
                    global_stage.close();
                }
            });

            group.getChildren().addAll(
                    WinTool.createLabel(10, 80, 30, 30, 16, "X:"), fieldX,
                    WinTool.createLabel(170, 80, 30, 30, 16, "Z:"), fieldZ,
                    WinTool.createLabel(10, 110, 80, 30, 16, "站点名称:"), nameField,
                    confirm
            );
        } else {
            TextField nameField = WinTool.createTextField(90, 80, 150, 30, 16);

            Button confirm = WinTool.createButton(200, 250, 80, 40, 20, "确定");
            confirm.setOnAction(actionEvent -> {
                boolean success = create(nameField.getText());
                if (success) {
                    global_stage.close();
                }
            });

            group.getChildren().addAll(
                    WinTool.createLabel(10, 80, 80, 30, 16, "站点名称:"), nameField,
                    confirm);
        }
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建车站");
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public boolean create(String strX, String strZ, String stationName) {
        try {
            int x = Integer.parseInt(strX);
            int z = Integer.parseInt(strZ);

            // 如果stationName为空，当做错误处理
            if ((stationName == null) || (stationName.equals(""))) {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "没有输入站点名", "如果是已创建的站点请使用第二种创建站点的方式");
                return false;
            }

            // 检查站点名唯一性和是否有重复的xz轴（如果有重复也算有错）
            boolean canContinue = true;
            for (Map.Entry<String, LineData> entry : data.entrySet()) {
                LineData lineData = entry.getValue();
                for (Map.Entry<String, Integer[]> stationEntry : lineData.getStations().entrySet()) {
                    String compareName = stationEntry.getKey();
                    if (stationName.equals(compareName)) {
                        canContinue = false;
                        break;
                    }

                    Integer[] comparePosition = stationEntry.getValue();
                    if ((comparePosition[0] == x) && (comparePosition[1] == z)) {
                        canContinue = false;
                        break;
                    }
                }
                if (!canContinue) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "失败", "站点名有重复或xz轴有重复", "请换一个站点名");
                    return false;
                }
            }

            LineData lineData = data.get(lineName);
            lineData.addStation(stationName, new Integer[]{x, z});

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "输入数据有错误", "");
            return false;
        }
    }

    public boolean create(String stationName) {
        try {
            for (Map.Entry<String, LineData> entry : data.entrySet()) {
                LineData lineData = entry.getValue();
                for (Map.Entry<String, Integer[]> stationEntry : lineData.getStations().entrySet()) {
                    String compareName = stationEntry.getKey();
                    if (stationName.equals(compareName)) {
                        data.get(lineName).addStation(stationName, stationEntry.getValue());
                        return true;
                    }
                }
            }

            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "输入的站点名不存在", "");
            return false;
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "未知的错误", "");
            return false;
        }
    }
}
