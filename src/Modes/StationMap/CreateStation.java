package Modes.StationMap;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateStation extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final List<LineData> data;
    private int lineIndex;
    private String typeBoxDefault = "坐标创建";
    private static String goingType = null;       // 记录自定义走向的

    /**
     * @param data      所有线路的信息
     * @param lineName  创建站点归属的线路的列表序号
     */
    public CreateStation(List<LineData> data, String lineName) {
        this.data = data;

        for (int i=0; i<this.data.size(); i++) {
            // 比较线路名
            if (this.data.get(i).getLineName().equals(lineName)) {
                this.lineIndex = i;
                break;
            }
        }
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

            // 将这一个Pane放在CheckBox前加载的原因是因为CheckBox中要调用
            Pane pane = new Pane();

            CheckBox routeDirection = WinTool.createCheckBox(10, 140, 200, 40, 16, "自定义线路走向");
            routeDirection.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue) {      // 如果被勾选
                    addDirections(pane, fieldX.getText(), fieldZ.getText());
                } else {
                    pane.getChildren().clear();
                    goingType = null;
                }
            });

            Button confirm = WinTool.createButton(280, 320, 80, 40, 20, "确定");
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
                    routeDirection,
                    WinTool.createScrollPane(0, 180, 400, 140, pane),
                    confirm
            );
        } else {
            TextField nameField = WinTool.createTextField(90, 80, 150, 30, 16);

            // 将这一个Pane放在CheckBox前加载的原因是因为CheckBox中要调用
            Pane pane = new Pane();

            CheckBox routeDirection = WinTool.createCheckBox(10, 140, 200, 40, 16, "自定义线路走向");
            routeDirection.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue) {      // 如果被勾选
                    addDirections(pane, nameField.getText());
                } else {
                    pane.getChildren().clear();
                    goingType = null;
                }
            });

            Button confirm = WinTool.createButton(280, 320, 80, 40, 20, "确定");
            confirm.setOnAction(actionEvent -> {
                boolean success = create(nameField.getText());
                if (success) {
                    global_stage.close();
                }
            });

            group.getChildren().addAll(
                    WinTool.createLabel(10, 80, 80, 30, 16, "站点名称:"), nameField,
                    routeDirection,
                    WinTool.createScrollPane(0, 180, 400, 140, pane),
                    confirm
            );
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

    private boolean create(String strX, String strZ, String stationName) {
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
            for (LineData lineData : data) {
                for (LineData.StationData stationData : lineData.getStations()) {
                    String compareName = stationData.getName();
                    if (stationName.equals(compareName)) {
                        canContinue = false;
                        break;
                    }

                    Integer[] comparePosition = stationData.getPosition();
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

            LineData lineData = data.get(lineIndex);
            Integer[] nowPos = new Integer[]{x, z};

            if (!lineData.getStations().isEmpty()) {
                Integer[] lastPos = lineData.getStations().get(lineData.getStations().size() - 1).getPosition();
                lineData.getDirections().add((goingType != null) ? goingTypeToArray() :
                        defaultDirection(lastPos, nowPos));
            }
            lineData.addStation(stationName, nowPos);

            return true;
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "输入数据有错误", "");
            return false;
        }
    }

    private boolean create(String stationName) {
        try {
            for (LineData lineData : data) {
                for (LineData.StationData stationData : lineData.getStations()) {
                    String compareName = stationData.getName();
                    if (stationName.equals(compareName)) {
                        LineData addLine = data.get(lineIndex);
                        if (!addLine.getStations().isEmpty()) {
                            // 如果不是添加第一个站，就添加Direction
                            addLine.getDirections().add(
                                    (goingType != null) ? goingTypeToArray() : defaultDirection(
                                            addLine.getStations().get(addLine.getStations().size()-1).getPosition(),
                                            stationData.getPosition()
                                    )
                            );
                        }
                        addLine.addStation(stationName, stationData.getPosition());
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

    private void addDirections(Pane pane, String xStr, String zStr) {
        try {
            int createX = Integer.parseInt(xStr);
            int createZ = Integer.parseInt(zStr);

            ToggleGroup buttonGroup = new ToggleGroup();

            // 判断能够有哪种自定义路线
            List<LineData.StationData> stations = data.get(lineIndex).getStations();
            List<String> possibleDirection = getPossibleDirection(stations.get(stations.size() - 1).getPosition(),
                    new Integer[]{createX, createZ});

            int xCount = 0;
            for (String s:possibleDirection) {
                RadioButton rb = WinTool.createRadioButton(xCount, 30, 120, 50,
                        new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "MapImgs" + File.separator + s + ".png"));
                rb.setOnAction(actionEvent -> goingType = s);
                rb.setToggleGroup(buttonGroup);
                pane.getChildren().add(rb);
                xCount += 130;
            }
        } catch (Exception ignored) {
            // 如果xStr和zStr转换错误或线路内原先没有站点，那就什么都不显示
        }
    }

    private void addDirections(Pane pane, String stationName) {
        try {
            List<LineData.StationData> stations = data.get(lineIndex).getStations();
            LineData.StationData lastStation = stations.get(stations.size()-1);
            for (LineData lineData : data) {
                for (LineData.StationData nowStation : lineData.getStations()) {
                    String compareName = nowStation.getName();
                    if (stationName.equals(compareName)) {
                        ToggleGroup buttonGroup = new ToggleGroup();

                        // 判断能够有哪种自定义路线
                        List<String> possibleDirection = getPossibleDirection(lastStation.getPosition(),
                                nowStation.getPosition());

                        int xCount = 0;
                        for (String s : possibleDirection) {
                            RadioButton rb = WinTool.createRadioButton(xCount, 30, 120, 50,
                                    new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "MapImgs" + File.separator + s + ".png"));
                            rb.setOnAction(actionEvent -> goingType = s);
                            rb.setToggleGroup(buttonGroup);
                            pane.getChildren().add(rb);
                            xCount += 130;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // 如果站点没有找到或线路内原先没有站点，那就什么都不显示
        }
    }

    private List<String> getPossibleDirection(Integer[] lastPos, Integer[] createPos) {
        // 将有可能的方向简称存储到一个List中
        List<String> possibleDirection = new ArrayList<>();

        int lastX = lastPos[0];
        int lastZ = lastPos[1];
        int createX = createPos[0];
        int createZ = createPos[1];
        int xDistance = Math.abs(lastX - createX);
        int zDistance = Math.abs(lastZ - createZ);

        /*
        准则：如果任意一个坐标轴的偏移量小于等于30，允许直接用直线连接，否则不允许使用直线连接
        */
        if (xDistance <= 30) {      // 允许上下直接走向
            if (lastZ > createZ) {
                possibleDirection.add("U");
            } else {
                possibleDirection.add("D");
            }
        }

        if (zDistance <= 30) {      // 允许左右直接走向
            if (lastX > createX) {
                possibleDirection.add("L");
            } else {
                possibleDirection.add("R");
            }
        }

        if ((xDistance != 0) || (zDistance != 0)) {      // 如果不是走完全直线，允许拐弯
            if (lastX > createX) {
                if (lastZ > createZ) {
                    possibleDirection.add("UL");
                    possibleDirection.add("LU");
                } else {
                    possibleDirection.add("LD");
                    possibleDirection.add("DL");
                }
            } else {
                if (lastZ > createZ) {
                    possibleDirection.add("UR");
                    possibleDirection.add("RU");
                } else {
                    possibleDirection.add("RD");
                    possibleDirection.add("DR");
                }
            }
        }

        return possibleDirection;
    }

    private Integer[] defaultDirection(Integer[] lastPos, Integer[] nowPos) {
        int xDistance = Math.abs(lastPos[0]-nowPos[0]);
        int zDistance = Math.abs(lastPos[1]-nowPos[1]);

        if (xDistance == 0) {    // 只竖向走
            if (lastPos[1] > nowPos[1]) {
                return new Integer[]{-1, -1, 1, 0};
            } else {
                return new Integer[]{-1, -1, 0, 1};
            }
        } else if (zDistance == 0) {     // 只横向走
            if (lastPos[0] > nowPos[0]) {
                return new Integer[]{1, 0, -1, -1};
            } else {
                return new Integer[]{0, 1, -1, -1};
            }
        } else {
            if (xDistance >= zDistance) {    // x相对距离比z相对距离大 或 xz轴距离相等，竖向进站
                if (lastPos[0] > nowPos[0]){
                    if (lastPos[1] > nowPos[1]) {
                        return new Integer[]{1, -1, -1, 0};
                    } else {
                        return new Integer[]{1, -1, 0, -1};
                    }
                } else {
                    if (lastPos[1] > nowPos[1]) {
                        return new Integer[]{-1, 1, -1, 0};
                    } else {
                        return new Integer[]{-1, 1, 0, -1};
                    }
                }
            } else {                        // z相对距离比x相对距离大，横向进站
                if (lastPos[1] > nowPos[1]) {
                    if (lastPos[0] > nowPos[0]) {
                        return new Integer[]{-1, 0, 1, -1};
                    } else {
                        return new Integer[]{0, -1, 1, -1};
                    }
                } else {
                    if (lastPos[0] > nowPos[0]) {
                        return new Integer[]{-1, 0, -1, 1};
                    } else {
                        return new Integer[]{0, -1, -1, 1};
                    }
                }
            }
        }
    }

    private Integer[] goingTypeToArray() {
        return switch (goingType) {
            case "L" -> new Integer[]{0, 1, -1, -1};
            case "R" -> new Integer[]{1, 0, -1, -1};
            case "U" -> new Integer[]{-1, -1, 1, 0};
            case "D" -> new Integer[]{-1, -1, 0, 1};
            case "LU" -> new Integer[]{1, -1, -1, 0};
            case "LD" -> new Integer[]{1, -1, 0, -1};
            case "RU" -> new Integer[]{-1, 1, -1, 0};
            case "RD" -> new Integer[]{-1, 1, 0, -1};
            case "UL" -> new Integer[]{-1, 0, 1, -1};
            case "UR" -> new Integer[]{0, -1, 1, -1};
            case "DL" -> new Integer[]{-1, 0, -1, 1};
            case "DR" -> new Integer[]{0, -1, -1, 1};
            default -> null;
        };
    }
}
