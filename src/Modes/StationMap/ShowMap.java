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

public class ShowMap {
    private Canvas map;
    private GraphicsContext context;
    private final List<LineData> data;
    // 每一个站点经过的线路，用于换乘的站点用。Key为站的x和z轴，用"/"分割，Value是线路从横向来和从纵向来的个数（记录先横后纵）
    private final HashMap<String, Integer[]> linePassed = new HashMap<>();
    private final String path;
    private String nowUsingLine;

    /**
     * @param path 这个path是项目的根目录
     */
    public ShowMap(String path) {
        this.path = path;

        ArrayList<LineData> data_temp;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(path + File.separator + "map" + File.separator + "map_data")
            );

            data_temp = (ArrayList<LineData>) inputStream.readObject();

            inputStream.close();
        } catch (Exception ignored) {
            data_temp = new ArrayList<>();
        }
        data = data_temp;
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
            if (this.nowUsingLine != null) {
                CreateStation creator = new CreateStation(data, this.nowUsingLine);
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
        // 刷新PassedTrend
        updateLineTrend();

        // 清空画布
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, 630, 630);

        // 添加站点
        linePassed.forEach((s, passedTrend) -> {
            // stationType的内容规则”x/z“，x和z指代两个坐标，所以使用"/"来进行划分
            String[] stationPosTemp = s.split("/");
            double[] stationPos = new double[]{Double.parseDouble(stationPosTemp[0]), Double.parseDouble(stationPosTemp[1])};

            context.setLineWidth(2);
            context.setStroke(Color.BLACK);

            context.strokeArc(stationPos[0] - ((double) passedTrend[1]) * 2.5 - 2.5, stationPos[1] - ((double) passedTrend[0]) * 2.5 - 2.5,
                    5, 5, 90, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] - ((double) passedTrend[1]) * 2.5, stationPos[1] - ((double) passedTrend[0]) * 2.5 - 2.5,
                   stationPos[0] + ((double) passedTrend[1]) * 2.5, stationPos[1] - ((double) passedTrend[0]) * 2.5 - 2.5);
            context.strokeArc(stationPos[0] + ((double) passedTrend[1]) * 2.5-2.5, stationPos[1] - ((double) passedTrend[0]) * 2.5-2.5,
                    5, 5, 0, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] + ((double) passedTrend[1]) * 2.5 + 2.5, stationPos[1] - ((double) passedTrend[0]) * 2.5,
                    stationPos[0] + ((double) passedTrend[1]) * 2.5 + 2.5, stationPos[1] + ((double) passedTrend[0]) * 2.5);
            context.strokeArc(stationPos[0] + ((double) passedTrend[1]) * 2.5-2.5, stationPos[1] + ((double) passedTrend[0]) * 2.5-2.5,
                    5, 5, 270, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] + ((double) passedTrend[1]) * 2.5, stationPos[1] + ((double) passedTrend[0]) * 2.5 + 2.5,
                    stationPos[0] - ((double) passedTrend[1]) * 2.5, stationPos[1] + ((double) passedTrend[0]) * 2.5 + 2.5);
            context.strokeArc(stationPos[0] - ((double) passedTrend[1]) * 2.5 - 2.5, stationPos[1] + ((double) passedTrend[0]) * 2.5-2.5,
                    5, 5, 180, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] - ((double) passedTrend[1]) * 2.5-2.5, stationPos[1] + ((double) passedTrend[0]) * 2.5,
                    stationPos[0] - ((double) passedTrend[1]) * 2.5-2.5, stationPos[1] - ((double) passedTrend[0]) * 2.5);
        });

        // 划线连接站点
        for (LineData lineData : data) {
            List<LineData.StationData> stations = lineData.getStations();
            for (int i=0; i< stations.size(); i++) {
                // 获取lastPos和nowPos
                Integer[] lastPos = (i == 0) ? new Integer[2] : stations.get(i-1).getPosition();
                Integer[] nowPos = stations.get(i).getPosition();

                // 获取前后站的PassedTrend
                Integer[] lastTrend = linePassed.get(lastPos[0] + "/" + lastPos[1]);
                Integer[] nowTrend = linePassed.get(nowPos[0] + "/" + nowPos[1]);

                // 如果 i!=1，那么就不是第一个站，就执行
                if (i != 0) {
                    // -1: 不往那个方向, 0: 往那个方向，但是是在拐弯后的, 1: 往那个方向，并且在拐弯前的
                    int leftSide = -1;
                    int rightSide = -1;
                    int upSide = -1;
                    int downSide = -1;

                    int xDistance = Math.abs(lastPos[0] - nowPos[0]);
                    int zDistance = Math.abs(lastPos[1] - nowPos[1]);

                    if (Objects.equals(lastPos[0], nowPos[0])) {    // x轴相等，考虑Z轴
                        if (lastPos[1] > nowPos[1]) upSide = 1;
                        else downSide = 1;
                    } else if (Objects.equals(lastPos[1], nowPos[1])) {    // Z轴相等，考虑X轴
                        if (lastPos[0] > nowPos[0]) leftSide = 1;
                        else rightSide = 1;
                    } else {   // X轴和Z轴都不相等，考虑拐弯情况
                        if (xDistance > zDistance) {
                            if (lastPos[0] > nowPos[0]) leftSide = 1;
                            else rightSide = 1;

                            if (lastPos[1] > nowPos[1]) upSide = 0;
                            else downSide = 0;
                        } else if (zDistance > xDistance) {
                            if (lastPos[1] > nowPos[1]) upSide = 1;
                            else downSide = 1;

                            if (lastPos[0] > nowPos[0]) leftSide = 0;
                            else rightSide = 0;
                        } else {
                            if (lastPos[0] > nowPos[0]) leftSide = 1;
                            else rightSide = 1;

                            if (lastPos[1] > nowPos[1]) upSide = 0;
                            else downSide = 0;
                        }
                    }

                    // 对应不同side划线
                    context.setLineWidth(5);
                    context.setStroke(ColorTool.engToColor(lineData.getColor()));

                    // 先向左
                    if (leftSide == 1) {
                        // 没有其他操作
                        if ((upSide == -1) && (downSide == -1)) {
                            context.strokeLine(lastPos[0]-5-lastTrend[1]*2.5, lastPos[1],
                                    nowPos[0]+5+nowTrend[1]*2.5, nowPos[1]);
                        } else {     // 还有其他操作
                            context.strokeLine(lastPos[0]-5-lastTrend[1]*2.5, lastPos[1],
                                    nowPos[0]+5, lastPos[1]);
                        }
                    } else if (leftSide == 0) {     // 后向左
                        context.strokeLine(lastPos[0], nowPos[1],
                                nowPos[0]+5+lastTrend[1]*2.5, nowPos[1]);
                    }

                    // 先向右
                    if (rightSide == 1) {
                        // 没有其他操作
                        if ((upSide == -1) && (downSide == -1)) {
                            context.strokeLine(lastPos[0]+5+lastTrend[1]*2.5, lastPos[1],
                                    nowPos[0]-5-nowTrend[1]*2.5, nowPos[1]);
                        } else {      // 还有其他操作
                            context.strokeLine(lastPos[0]+5+lastTrend[1]*2.5, lastPos[1],
                                    nowPos[0]-5, lastPos[1]);
                        }
                    } else if (rightSide == 0) {     // 后向右
                        context.strokeLine(lastPos[0], nowPos[1],
                                nowPos[0]-5-nowTrend[1]*2.5, nowPos[1]);
                    }

                    // 先向上
                    if (upSide == 1) {
                        // 没有其他操作
                        if ((leftSide == -1) && (rightSide == -1)) {
                            context.strokeLine(lastPos[0], lastPos[1]-5-lastTrend[0]*2.5,
                                    nowPos[0], nowPos[1]+5+nowTrend[0]*2.5);
                        } else {    // 还有其他操作
                            context.strokeLine(lastPos[0], lastPos[1]-5-lastTrend[0]*2.5,
                                    lastPos[0], nowPos[1]+5);
                        }
                    } else if (upSide == 0) {      // 后向上
                        context.strokeLine(nowPos[0], lastPos[1],
                                nowPos[0], nowPos[1]+5+nowTrend[0]*2.5);
                    }

                    // 先向下
                    if (downSide == 1) {
                        // 没有其他操作
                        if ((leftSide == -1) && (rightSide == -1)) {
                            context.strokeLine(lastPos[0], lastPos[1]+5+lastTrend[0]*2.5,
                                    nowPos[0], nowPos[1]-5-nowTrend[0]*2.5);
                        } else {    // 还有其他操作
                            context.strokeLine(lastPos[0], lastPos[1]+5+lastTrend[0]*2.5,
                                    lastPos[0], nowPos[1]-5);
                        }
                    } else if (downSide == 0) {      // 后向下
                        context.strokeLine(nowPos[0], lastPos[1],
                                nowPos[0], nowPos[1]-5-nowTrend[0]*2.5);
                    }
                }
            }
        }
    }

    /**
     * @implNote 设置成static的原因是这里的函数在CreateStation中也要调用，获得线路的走向
     * @return true:横向, false:纵向
     */
    private boolean getStationTrend(Integer[] lastPos, Integer[] nowPos) {
        int xDistance = Math.abs(lastPos[0]-nowPos[0]);
        int zDistance = Math.abs(lastPos[1]-nowPos[1]);

        // 如果xz轴一样，竖着进站，如果不一样，短的进站
        if (xDistance == zDistance) {
            return false;
        } else if (xDistance == 0) {
            return false;
        } else if (zDistance == 0) {
            return true;
        } else {
            return xDistance < zDistance;
        }
    }

    /**
     * @implNote 与getStationTrend功能类似，不过get
     * @return true: 横向出站, false: 纵向出站
     */
    private boolean getStationForward(Integer[] nowPos, Integer[] nextPos) {
        int xDistance = Math.abs(nowPos[0] - nextPos[0]);
        int zDistance = Math.abs(nowPos[1] - nextPos[1]);

        if (xDistance == zDistance) {     // xz轴相对距离都相等
            return true;
        } else if (xDistance == 0) {     // x坐标相等
            return false;
        } else if (zDistance == 0) {     // z坐标相等
            return true;
        } else {
             return xDistance > zDistance;
        }
    }

    public void updateLineTrend() {
        // 清空linePassed
        linePassed.clear();

        // 设置LinePassed
        for (LineData lineData : data) {
            List<LineData.StationData> stations = lineData.getStations();
            for (int i = 0; i< stations.size(); i++) {
                Integer[] lastPos = (i == 0) ? new Integer[2] : stations.get(i-1).getPosition();
                Integer[] nowPos = stations.get(i).getPosition();
                String key = nowPos[0] + "/" + nowPos[1];
                int trendTemp = -1;    // -1:没有赋值, 0: 东西走向, 1：南北走向

                if (i != 0) {    // 如果不是第一个站，考虑进进站问题
                    boolean trend = getStationTrend(new Integer[]{lastPos[0], lastPos[1]}, nowPos);
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

                if (stations.size() != i+1) {      // 如果不是最后一个站，考虑出站问题
                    // 判断trend（这一个trend指的是出站的方向），true:东西方向，false：南北方向
                    Integer[] passedTrend = linePassed.get(key);
                    Integer[] nextPos = stations.get(i+1).getPosition();
                    boolean trend = getStationForward(nowPos, nextPos);

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
            }
        }
    }
}
