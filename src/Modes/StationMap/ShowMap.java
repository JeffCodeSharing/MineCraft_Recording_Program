package Modes.StationMap;

import Tools.ColorTool;
import Tools.WinTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ShowMap {
    private Canvas map;
    private GraphicsContext context;
    private final LinkedHashMap<String, LineData> data;
    // 每一个站点经过的线路，用于换乘的站点用。Key为站的x和z轴，用"/"分割，Value是线路从横向来和从纵向来的个数（记录先横后纵）
    private final HashMap<String, Integer[]> linePassed = new HashMap<>();
    private final String path;
    private String nowUsingLine;

    /**
     * @param path 这个path是项目的根目录
     */
    public ShowMap(String path) {
        this.path = path;

        LinkedHashMap<String, LineData> data_temp;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(path + File.separator + "map" + File.separator + "map_data")
            );

            data_temp = (LinkedHashMap<String, LineData>) inputStream.readObject();

            inputStream.close();
        } catch (Exception ignored) {
            data_temp = new LinkedHashMap<>();
        }
        this.data = data_temp;

        Integer[] lastStationPos = new Integer[2];
        for (Map.Entry<String, LineData> entry:this.data.entrySet()) {
            LineData lineData = entry.getValue();
            Set<Map.Entry<String, Integer[]>> stationSet = lineData.getStations().entrySet();
            List<Map.Entry<String, Integer[]>> stationList = new ArrayList<>(stationSet);
            for (int i=0; i< stationList.size(); i++) {
                Integer[] stationPosition = stationList.get(i).getValue();
                String key = stationPosition[0] + "/" + stationPosition[1];
                int trendTemp = -1;    // -1:没有赋值, 0: 东西走向, 1：南北走向

                if (!(i == 0)) {    // 如果不是第一个站，考虑进进站问题
                    boolean trend = getStationTrend(new Integer[]{lastStationPos[0], lastStationPos[1]}, stationPosition);
                    Integer[] passedTrend = linePassed.get(key);
                    trendTemp = trend ? 0 : 1;
                    if (passedTrend == null) {
                        if (trend) {
                            linePassed.put(key, new Integer[]{1, 0});
                        } else {
                            linePassed.put(key, new Integer[]{0, 1});
                        }
                    } else {
                        if (trend) {
                            passedTrend[0] += 1;
                        } else {
                            passedTrend[1] += 1;
                        }
                    }
                }

                if (stationList.size() != i+1) {      // 如果不是最后一个站，考虑出站问题
                    // 判断trend（这一个trend指的是出站的方向），true:东西方向，false：南北方向
                    Integer[] passedTrend = linePassed.get(key);
                    Integer[] nextStationPos = stationList.get(i+1).getValue();
                    boolean trend = getStationForward(stationPosition, nextStationPos);

                    if ((trend ? 0 : 1) != trendTemp) {
                        if (passedTrend == null) {
                            if (trend) {
                                linePassed.put(key, new Integer[]{1, 0});
                            } else {
                                linePassed.put(key, new Integer[]{0, 1});
                            }
                        } else {
                            if (trend) {
                                passedTrend[0] += 1;
                            } else {
                                passedTrend[1] += 1;
                            }
                        }
                    }
                } else {       // 如果stationList的内容只有一个
                    // 如果这个站点没有被创建过，则创建
                    linePassed.computeIfAbsent(key, k -> new Integer[]{0, 0});
                }

                lastStationPos = stationPosition;
            }
        }
    }

    public void entrance(Pane box) {
        box.getChildren().clear();
        start(box);
    }

    private void start(Pane box) {
        Button map_output = WinTool.createButton(0, 0, 120, 40, 20, "导出地图");
        map_output.setOnAction(actionEvent -> MapOutput.output(map));

        Button save_map = WinTool.createButton(120, 0, 120, 40, 20, "保存地图");
        save_map.setOnAction(actionEvent -> MapSaver.save(data, path));

        map = WinTool.createCanvas(0, 80, 630, 630, Color.WHITE);
        context = map.getGraphicsContext2D();

        drawPoints();

        Button choose_line = WinTool.createButton(0, 45, 120, 30, 16, "选择线路");
        Button create_station = WinTool.createButton(120, 45, 120, 30, 16, "创建站点");
        Button create_line = WinTool.createButton(240, 45, 120, 30, 16, "创建线路");

        choose_line.setOnAction(actionEvent -> {
            LineChooser chooser = new LineChooser(data);
            this.nowUsingLine = chooser.entrance()[0];
        });
        create_station.setOnAction(actionEvent -> {
            if (nowUsingLine == null) {
                LineChooser chooser = new LineChooser(data);
                this.nowUsingLine = chooser.entrance()[0];
            }

            // 如果上一步用户直接点击叉叉关掉的话，下方代码不执行
            if (nowUsingLine != null) {
                CreateStation creator = new CreateStation(data, linePassed, nowUsingLine);
                creator.entrance();
                drawPoints();
            }
        });
        create_line.setOnAction(actionEvent -> {
            CreateLine creator = new CreateLine(data);
            creator.entrance();
        });

        box.getChildren().addAll(
                map_output, save_map,
                map,
                choose_line, create_station, create_line
        );
    }

    private void drawPoints() {
        // 清空画布
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, 630, 630);

        // 添加站点
        linePassed.forEach((s, passedTrend) -> {
            // stationType的内容规则”x/z“，x和z指代两个坐标，所以使用"/"来进行划分
            String[] stationPositionTemp = s.split("/");
            double[] stationPosition = new double[]{Double.parseDouble(stationPositionTemp[0]), Double.parseDouble(stationPositionTemp[1])};

            context.setLineWidth(2);
            context.setStroke(Color.BLACK);

            context.strokeArc(stationPosition[0] - ((double) passedTrend[1]) * 2.5 - 2.5, stationPosition[1] - ((double) passedTrend[0]) * 2.5 - 2.5,
                    5, 5, 90, 90, ArcType.OPEN);
            context.strokeLine(stationPosition[0] - ((double) passedTrend[1]) * 2.5, stationPosition[1] - ((double) passedTrend[0]) * 2.5 - 2.5,
                   stationPosition[0] + ((double) passedTrend[1]) * 2.5, stationPosition[1] - ((double) passedTrend[0]) * 2.5 - 2.5);
            context.strokeArc(stationPosition[0] + ((double) passedTrend[1]) * 2.5-2.5, stationPosition[1] - ((double) passedTrend[0]) * 2.5-2.5,
                    5, 5, 0, 90, ArcType.OPEN);
            context.strokeLine(stationPosition[0] + ((double) passedTrend[1]) * 2.5 + 2.5, stationPosition[1] - ((double) passedTrend[0]) * 2.5,
                    stationPosition[0] + ((double) passedTrend[1]) * 2.5 + 2.5, stationPosition[1] + ((double) passedTrend[0]) * 2.5);
            context.strokeArc(stationPosition[0] + ((double) passedTrend[1]) * 2.5-2.5, stationPosition[1] + ((double) passedTrend[0]) * 2.5-2.5,
                    5, 5, 270, 90, ArcType.OPEN);
            context.strokeLine(stationPosition[0] + ((double) passedTrend[1]) * 2.5, stationPosition[1] + ((double) passedTrend[0]) * 2.5 + 2.5,
                    stationPosition[0] - ((double) passedTrend[1]) * 2.5, stationPosition[1] + ((double) passedTrend[0]) * 2.5 + 2.5);
            context.strokeArc(stationPosition[0] - ((double) passedTrend[1]) * 2.5 - 2.5, stationPosition[1] + ((double) passedTrend[0]) * 2.5-2.5,
                    5, 5, 180, 90, ArcType.OPEN);
            context.strokeLine(stationPosition[0] - ((double) passedTrend[1]) * 2.5-2.5, stationPosition[1] + ((double) passedTrend[0]) * 2.5,
                    stationPosition[0] - ((double) passedTrend[1]) * 2.5-2.5, stationPosition[1] - ((double) passedTrend[0]) * 2.5);
        });

        // 划线连接站点
        data.forEach((lineName, lineData) -> {
            Map<String, Integer[]> stations = lineData.getStations();
            final AtomicBoolean isFirstStation = new AtomicBoolean(true);
            final AtomicReferenceArray<Integer> last_position = new AtomicReferenceArray<>(2);

            // 因为存储时使用的是LinkedHashMap，所以直接使用遍历
            stations.forEach((stationName, stationPosition) -> {
                if (!isFirstStation.get()) {
                    // -1: 不往那个方向, 0: 往那个方向，但是是在拐弯后的, 1: 往那个方向，并且在拐弯前的
                    int leftSide = -1;
                    int rightSide = -1;
                    int upSide = -1;
                    int downSide = -1;

                    if (Objects.equals(last_position.get(0), stationPosition[0])) {    // x轴相等，考虑Z轴
                        if (last_position.get(1) > stationPosition[1]) upSide = 1;
                        else downSide = 1;
                    } else if (Objects.equals(last_position.get(1), stationPosition[1])) {    // Z轴相等，考虑X轴
                        if (last_position.get(0) > stationPosition[0]) leftSide = 1;
                        else rightSide = 1;
                    } else {   // X轴和Z轴都不相等，考虑拐弯情况
                        if (Math.abs(last_position.get(0) - stationPosition[0]) > Math.abs(last_position.get(1) - stationPosition[1])) {
                            if (last_position.get(0) > stationPosition[0]) leftSide = 1;
                            else rightSide = 1;

                            if (last_position.get(1) > stationPosition[1]) upSide = 0;
                            else downSide = 0;
                        } else if (Math.abs(last_position.get(1) - stationPosition[1]) > Math.abs(last_position.get(0) - stationPosition[0])) {
                            if (last_position.get(1) > stationPosition[1]) upSide = 1;
                            else downSide = 1;

                            if (last_position.get(0) > stationPosition[0]) leftSide = 0;
                            else rightSide = 0;
                        } else {
                            if (last_position.get(0) > stationPosition[0]) leftSide = 1;
                            else rightSide = 1;

                            if (last_position.get(1) > stationPosition[1]) upSide = 0;
                            else downSide = 0;
                        }
                    }

                    // 对应不同side划线
                    context.setLineWidth(5);
                    context.setStroke(ColorTool.engToColor(lineData.getColor()));

                    // 这里的所有划线都考虑了同时包含转弯的情况
                    if (leftSide == 1) context.strokeLine(last_position.get(0)-5, last_position.get(1), stationPosition[0]+5, last_position.get(1));
                    else if (leftSide == 0) context.strokeLine(last_position.get(0), stationPosition[1], stationPosition[0]+5, stationPosition[1]);

                    if (rightSide == 1) context.strokeLine(last_position.get(0)+5, last_position.get(1), stationPosition[0]-5, last_position.get(1));
                    else if (rightSide == 0) context.strokeLine(last_position.get(0), stationPosition[1], stationPosition[0]-5, stationPosition[1]);

                    if (upSide == 1) context.strokeLine(last_position.get(0), last_position.get(1)-5, last_position.get(0), stationPosition[1]+5);
                    else if (upSide == 0) context.strokeLine(stationPosition[0], last_position.get(1), stationPosition[0], stationPosition[1]+5);

                    if (downSide == 1) context.strokeLine(last_position.get(0), last_position.get(1)+5, last_position.get(0), stationPosition[1]-5);
                    else if (downSide == 0) context.strokeLine(stationPosition[0], last_position.get(1), stationPosition[0], stationPosition[1]-5);
                }

                last_position.set(0, stationPosition[0]);
                last_position.set(1, stationPosition[1]);

                isFirstStation.set(false);
            });
        });
    }

    /**
     * @implNote 设置成static的原因是这里的函数在CreateStation中也要调用，获得线路的走向
     * @return true:横向, false:纵向
     */
    public static boolean getStationTrend(Integer[] lastPos, Integer[] nowPos) {
        int xDistance = Math.abs(lastPos[0]-nowPos[0]);
        int zDistance = Math.abs(lastPos[1]-nowPos[1]);
        // 如果xz轴一样，竖着进站，如果不一样，短的进站
        if (xDistance == zDistance) {
            return false;
        } else return xDistance < zDistance;
    }

    /**
     * @implNote 与getStationTrend功能类似，不过get
     * @return true: 横向出站, false: 纵向出站
     */
    public static boolean getStationForward(Integer[] nowPos, Integer[] nextPos) {
        int xDistance = Math.abs(nowPos[0] - nextPos[0]);
        int zDistance = Math.abs(nowPos[1] - nextPos[1]);

        if (xDistance == 0) {    // x坐标相等
            return false;
        } else if (zDistance == 0) {     // z坐标相等
            return true;
        } else {
            if (xDistance == zDistance) {     // xz轴相对居里都相等
                return true;
            } else return xDistance > zDistance;
        }
    }
}
