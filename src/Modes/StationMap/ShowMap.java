package Modes.StationMap;

import Tools.ColorTool;
import Tools.WinTool;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class ShowMap {
    private Canvas map;
    private GraphicsContext context;
    private final List<LineData> data;
    // 每一个站点经过的线路，用于换乘的站点用。Key为站的x和z轴，用"/"分割，Value是线路分别按照左，右，上，下的进出站路线数量
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

        // 临时绘制站点信息储存并初始化
        // Value的Integer[]共4个 分别对应左右上下进站的车辆
        // 使用DeepCopy的模式，使drawTemp的更改不会影响linePassed的数据
        HashMap<String, Integer[]> drawTemp = new HashMap<>();
        for (Map.Entry<String, Integer[]> entry : linePassed.entrySet()) {
            String key = entry.getKey();
            Integer[] value = entry.getValue();
            Integer[] copyValue = Arrays.copyOf(value, value.length);
            drawTemp.put(key, copyValue);
        }

        // 清空画布
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, 630, 630);

        // 添加站点
        linePassed.forEach((s, passedTrend) -> {
            // stationType的内容规则”x/z“，x和z指代两个坐标，所以使用"/"来进行划分
            String[] stationPosTemp = s.split("/");
            double[] stationPos = new double[]{Double.parseDouble(stationPosTemp[0]), Double.parseDouble(stationPosTemp[1])};
            double[] usingPassedTrend = new double[]{(double) Math.max(passedTrend[0], passedTrend[1]), (double) Math.max(passedTrend[2], passedTrend[3])};

            context.setLineWidth(2);
            context.setStroke(Color.BLACK);

            context.strokeArc(stationPos[0] - (usingPassedTrend[1]) * 2.5 - 2.5, stationPos[1] - (usingPassedTrend[0]) * 2.5 - 2.5,
                    5, 5, 90, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] - (usingPassedTrend[1]) * 2.5, stationPos[1] - (usingPassedTrend[0]) * 2.5 - 2.5,
                   stationPos[0] + (usingPassedTrend[1]) * 2.5, stationPos[1] - (usingPassedTrend[0]) * 2.5 - 2.5);
            context.strokeArc(stationPos[0] + (usingPassedTrend[1]) * 2.5-2.5, stationPos[1] - (usingPassedTrend[0]) * 2.5-2.5,
                    5, 5, 0, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] + (usingPassedTrend[1]) * 2.5 + 2.5, stationPos[1] - (usingPassedTrend[0]) * 2.5,
                    stationPos[0] + (usingPassedTrend[1]) * 2.5 + 2.5, stationPos[1] + (usingPassedTrend[0]) * 2.5);
            context.strokeArc(stationPos[0] + (usingPassedTrend[1]) * 2.5-2.5, stationPos[1] + (usingPassedTrend[0]) * 2.5-2.5,
                    5, 5, 270, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] + (usingPassedTrend[1]) * 2.5, stationPos[1] + (usingPassedTrend[0]) * 2.5 + 2.5,
                    stationPos[0] - (usingPassedTrend[1]) * 2.5, stationPos[1] + (usingPassedTrend[0]) * 2.5 + 2.5);
            context.strokeArc(stationPos[0] - (usingPassedTrend[1]) * 2.5 - 2.5, stationPos[1] + (usingPassedTrend[0]) * 2.5-2.5,
                    5, 5, 180, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0] - (usingPassedTrend[1]) * 2.5-2.5, stationPos[1] + (usingPassedTrend[0]) * 2.5,
                    stationPos[0] - (usingPassedTrend[1]) * 2.5-2.5, stationPos[1] - (usingPassedTrend[0]) * 2.5);
        });

        // 划线连接站点
        for (LineData lineData : data) {
            List<LineData.StationData> stations = lineData.getStations();
            for (int i=0; i< stations.size(); i++) {
                // 如果不是第一个站，就执行
                if (i != 0) {
                    // 获取lastPos和nowPos
                    Integer[] lastPos = stations.get(i-1).getPosition();
                    Integer[] nowPos = stations.get(i).getPosition();

                    // 获取坐标key
                    String lastTrendKey = lastPos[0] + "/" + lastPos[1];
                    String nowTrendKey = nowPos[0] + "/" + nowPos[1];

                    // 获取前后站的PassedTrend（这里的Temp是没加工过的数据，但是不代表不用）
                    Integer[] lastTrendTemp = linePassed.get(lastTrendKey);
                    Integer[] nowTrendTemp = linePassed.get(nowTrendKey);

                    // 获取drawTemp（这里的Temp是要使用的，用于绘制重线的时候不会重叠）
                    Integer[] lastDrawTemp = drawTemp.get(lastTrendKey);
                    Integer[] nowDrawTemp = drawTemp.get(nowTrendKey);

                    // 加工前后站的PassedTrend
                    double[] lastTrend = new double[]
                            {(double) Math.max(lastTrendTemp[0], lastTrendTemp[1]),
                                    (double) Math.max(lastTrendTemp[2], lastTrendTemp[3])};
                    double[] nowTrend = new double[]
                            {(double) Math.max(nowTrendTemp[0], nowTrendTemp[1]),
                                    (double) Math.max(nowTrendTemp[2], nowTrendTemp[3])};

                    // 确定绘制方向
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

                    // 绘制路线
                    // +2.5的目的是为了抵消线的宽度，让线尽量看起来在中间
                    // 先向左
                    if (leftSide == 1) {
                        // 没有其他操作
                        if ((upSide == -1) && (downSide == -1)) {
                            context.strokeLine(lastPos[0]-5-lastTrend[1]*2.5,
                                    lastPos[1]-lastTrendTemp[0]*2.5+2.5+(lastTrendTemp[0]-lastDrawTemp[0])*5,
                                    nowPos[0]+5+nowTrend[1]*2.5,
                                    nowPos[1]-nowTrendTemp[1]*2.5+2.5+(nowTrendTemp[1]-nowDrawTemp[1])*5);
                        } else {     // 还有其他操作
                            context.strokeLine(lastPos[0]-5-lastTrend[1]*2.5,
                                    lastPos[1]-lastTrendTemp[0]*2.5+2.5+(lastTrendTemp[0]-lastDrawTemp[0])*5,
                                    nowPos[0]-nowTrendTemp[3]*2.5+2.5+(nowTrendTemp[3]-nowDrawTemp[3])*5,
                                    lastPos[1]-lastTrendTemp[0]*2.5+2.5+(lastTrendTemp[0]-lastDrawTemp[0])*5);
                        }
                    } else if (leftSide == 0) {     // 后向左
                        if (upSide == 1) {
                            context.strokeLine(lastPos[0]-lastTrendTemp[2]*3+2.5+(lastTrendTemp[2]-lastDrawTemp[2])*5,
                                    nowPos[1]-nowTrendTemp[1]*2.5+2.5+(nowTrendTemp[1]-nowDrawTemp[1])*5,
                                    nowPos[0]+5+lastTrend[1]*2.5,
                                    nowPos[1]-nowTrendTemp[1]*2.5+2.5+(nowTrendTemp[1]-nowDrawTemp[1])*5);
                        } else {
                            context.strokeLine(lastPos[0]-lastTrendTemp[3]*2.5+2.5+(lastTrendTemp[3]-lastDrawTemp[3])*5,
                                    nowPos[1]-nowTrendTemp[1]*2.5+2.5+(nowTrendTemp[1]-nowDrawTemp[1])*5,
                                    nowPos[0]+5+lastTrend[1]*2.5,
                                    nowPos[1]-nowTrendTemp[1]*2.5+2.5+(nowTrendTemp[1]-nowDrawTemp[1])*5);
                        }
                    }

                    // 先向右
                    if (rightSide == 1) {
                        // 没有其他操作
                        if ((upSide == -1) && (downSide == -1)) {
                            context.strokeLine(lastPos[0]+5+lastTrend[1]*2.5,
                                    lastPos[1]-lastTrendTemp[1]*2.5+2.5+(lastTrendTemp[1]-lastDrawTemp[1])*5,
                                    nowPos[0]-5-nowTrend[1]*2.5,
                                    nowPos[1]-nowTrendTemp[0]*2.5+2.5+(nowTrendTemp[0]-nowDrawTemp[0])*5);
                        } else {      // 还有其他操作
                            context.strokeLine(lastPos[0]+5+lastTrend[1]*2.5,
                                    lastPos[1]-lastTrendTemp[1]*2.5+2.5+(lastTrendTemp[1]-lastDrawTemp[1])*5,
                                    nowPos[0]-5-nowTrendTemp[2]*2.5+2.5+(nowTrendTemp[2]-nowDrawTemp[2])*5,
                                    lastPos[1]-lastTrendTemp[1]*2.5+2.5+(lastTrendTemp[1]-lastDrawTemp[1])*5);
                        }
                    } else if (rightSide == 0) {     // 后向右
                        if (upSide == 1) {
                            context.strokeLine(lastPos[0]-lastTrendTemp[2]*2.5+2.5+(lastTrendTemp[2]-lastDrawTemp[2])*5,
                                    nowPos[1]-nowTrendTemp[0]*2.5+2.5+(nowTrendTemp[0]-nowDrawTemp[0])*5,
                                    nowPos[0]-5-nowTrend[1]*2.5,
                                    nowPos[1]-nowTrendTemp[0]*2.5+2.5+(nowTrendTemp[0]-nowDrawTemp[0])*5);
                        } else {
                            context.strokeLine(lastPos[0]-lastTrendTemp[3]*2.5+2.5+(lastTrendTemp[3]-lastDrawTemp[3])*5,
                                    nowPos[1]-nowTrendTemp[0]*2.5+2.5+(nowTrendTemp[0]-nowDrawTemp[0])*5,
                                    nowPos[0]-5-nowTrend[1]*2.5,
                                    nowPos[1]-nowTrendTemp[0]*2.5+2.5+(nowTrendTemp[0]-nowDrawTemp[0])*5);
                        }
                    }

                    // 先向上
                    if (upSide == 1) {
                        // 没有其他操作
                        if ((leftSide == -1) && (rightSide == -1)) {
                            context.strokeLine(lastPos[0]-lastTrendTemp[2]*2.5+2.5+(lastTrendTemp[2]-lastDrawTemp[2])*5,
                                    lastPos[1]-5-lastTrend[0]*2.5,
                                    nowPos[0]-nowTrendTemp[3]*2.5+2.5+(nowTrendTemp[3]-nowDrawTemp[3])*5,
                                    nowPos[1]+5+nowTrend[0]*2.5);
                        } else {    // 还有其他操作
                            context.strokeLine(lastPos[0]-lastTrendTemp[2]*2.5+2.5+(lastTrendTemp[2]-lastDrawTemp[2])*5,
                                    lastPos[1]-5-lastTrend[0]*2.5,
                                    lastPos[0]-lastTrendTemp[2]*2.5+2.5+(lastTrendTemp[2]-lastDrawTemp[2])*5,
                                    nowPos[1]-lastTrendTemp[0]*2.5+2.5+(lastTrendTemp[0]-lastDrawTemp[0])*5);
                        }
                    } else if (upSide == 0) {      // 后向上
                        if (leftSide == 1) {
                            context.strokeLine(nowPos[0]-nowTrendTemp[3]*2.5+2.5+(nowTrendTemp[3]-nowDrawTemp[3])*5,
                                    lastPos[1]-lastTrendTemp[0]*2.5+2.5+(lastTrendTemp[0]-lastDrawTemp[0])*5,
                                    nowPos[0]-nowTrendTemp[3]*2.5+2.5+(nowTrendTemp[3]-nowDrawTemp[3])*5,
                                    nowPos[1]+5+nowTrend[0]*2.5);
                        } else {
                            context.strokeLine(nowPos[0]-nowTrendTemp[3]*2.5+2.5+(nowTrendTemp[3]-nowDrawTemp[3])*5,
                                    lastPos[1]-lastTrendTemp[1]*2.5+2.5+(lastTrendTemp[1]-lastDrawTemp[1])*5,
                                    nowPos[0]-nowTrendTemp[3]*2.5+2.5+(nowTrendTemp[3]-nowDrawTemp[3])*5,
                                    nowPos[1]+5+nowTrend[0]*2.5);
                        }
                    }

                    // 先向下
                    if (downSide == 1) {
                        // 没有其他操作
                        if ((leftSide == -1) && (rightSide == -1)) {
                            context.strokeLine(lastPos[0]-lastTrendTemp[3]*2.5+2.5+(lastTrendTemp[3]-lastDrawTemp[3])*5,
                                    lastPos[1]+5+lastTrend[0]*2.5,
                                    nowPos[0]-nowTrendTemp[2]*2.5+2.5+(nowTrendTemp[2]-nowDrawTemp[2])*5,
                                    nowPos[1]-5-nowTrend[0]*2.5);
                        } else {    // 还有其他操作
                            context.strokeLine(lastPos[0]-lastTrendTemp[3]*2.5+2.5+(lastTrendTemp[3]-lastDrawTemp[3])*5,
                                    lastPos[1]+5+lastTrend[0]*2.5,
                                    lastPos[0]-lastTrendTemp[3]*2.5+2.5+(lastTrendTemp[3]-lastDrawTemp[3])*5,
                                    nowPos[1]-5-nowTrendTemp[0]*2.5+2.5+(nowTrendTemp[0]-nowDrawTemp[0])*5);
                        }
                    } else if (downSide == 0) {      // 后向下
                        if (leftSide == 1) {
                            context.strokeLine(nowPos[0]-nowTrendTemp[2]*2.5+2.5+(nowTrendTemp[2]-nowDrawTemp[2])*5,
                                    lastPos[1]-lastTrendTemp[0]*2.5+2.5+(lastTrendTemp[0]-lastDrawTemp[0])*5,
                                    nowPos[0]-nowTrendTemp[2]*2.5+2.5+(nowTrendTemp[2]-nowDrawTemp[2])*5,
                                    nowPos[1]-5-nowTrend[0]*2.5);
                        } else {
                            context.strokeLine(nowPos[0]-nowTrendTemp[2]*2.5+2.5+(nowTrendTemp[2]-nowDrawTemp[2])*5,
                                    lastPos[1]-lastTrendTemp[1]*2.5+2.5+(lastTrendTemp[1]-lastDrawTemp[1])*5,
                                    nowPos[0]-nowTrendTemp[2]*2.5+2.5+(nowTrendTemp[2]-nowDrawTemp[2])*5,
                                    nowPos[1]-nowTrend[0]*2.5);
                        }
                    }

                    // 减少All中的次数
                    if (leftSide == 1) {
                        if (upSide == 0) {
                            lastDrawTemp[0] -= 1;
                            nowDrawTemp[3] -= 1;
                        } else if (downSide == 0) {
                            lastDrawTemp[0] -= 1;
                            nowDrawTemp[2] -= 1;
                        } else {
                            lastDrawTemp[0] -= 1;
                            nowDrawTemp[1] -= 1;
                        }
                    } else if (rightSide == 1) {
                        if (upSide == 0) {
                            lastDrawTemp[1] -= 1;
                            nowDrawTemp[3] -= 1;
                        } else if (downSide == 0) {
                            lastDrawTemp[1] -= 1;
                            nowDrawTemp[2] -= 1;
                        } else {
                            lastDrawTemp[1] -= 1;
                            nowDrawTemp[0] -= 1;
                        }
                    } else if (upSide == 1) {
                        if (leftSide == 0) {
                            lastDrawTemp[2] -= 1;
                            nowDrawTemp[1] -= 1;
                        } else if (rightSide == 0) {
                            lastDrawTemp[2] -= 1;
                            nowDrawTemp[0] -= 1;
                        } else {
                            lastDrawTemp[2] -= 1;
                            nowDrawTemp[3] -= 1;
                        }
                    } else {
                        if (leftSide == 0) {
                            lastDrawTemp[3] -= 1;
                            nowDrawTemp[1] -= 1;
                        } else if (rightSide == 0) {
                            lastDrawTemp[3] -= 1;
                            nowDrawTemp[0] -= 1;
                        } else {
                            lastDrawTemp[3] -= 1;
                            nowDrawTemp[2] -= 1;
                        }
                    }
                }
            }
        }

        // 写上站台名字
        final int FONT_SIZE = 16;
        Font font = Font.font(FONT_SIZE);
        context.setFont(font);
        context.setFill(Color.BLACK);
        for (LineData lineData : data) {
            for (LineData.StationData stationData : lineData.getStations()) {
                Integer[] position = stationData.getPosition();
                String name = stationData.getName();
                Integer[] trendTemp = linePassed.get(position[0] + "/" + position[1]);
                double[] trend = new double[]
                        {(double) Math.max(trendTemp[0], trendTemp[1]),
                                (double) Math.max(trendTemp[2], trendTemp[3])};

                Text testText = new Text(name);
                testText.setFont(Font.font(FONT_SIZE));
                Bounds textBounds = testText.getLayoutBounds();

                context.fillText(
                        name,
                        position[0]-textBounds.getWidth()/2, position[1]+trend[0]*2.5+20
                );
            }
        }
    }

    /**
     * @implNote 获得线路的进站方向
     * @return 0: 左向; 1: 右向; 2: 上向; 3: 下向
     */
    private int getStationTrend(Integer[] lastPos, Integer[] nowPos) {
        int xDistance = Math.abs(lastPos[0]-nowPos[0]);
        int zDistance = Math.abs(lastPos[1]-nowPos[1]);

        if (xDistance == zDistance) {       // xz轴距离相等，竖向进站
            if (lastPos[1] > nowPos[1]) {   // 上一个站在现在这个站的下方
                return 3;
            } else {
                return 2;
            }
        } else if (xDistance == 0) {        // 如果是仅纵向运动
            if (lastPos[1] > nowPos[1]) {   // 上一个站在现在这个站的下方
                return 3;
            } else {
                return 2;
            }
        } else if (zDistance == 0) {        // 如果仅横向运动
            if (lastPos[0] > nowPos[0]) {   // 上一个站在现在这个站的右侧
                return 1;
            } else{
                return 0;
            }
        } else {
            if (xDistance > zDistance) {    // x相对距离比z相对距离大，竖向进站
                if (lastPos[1] > nowPos[1]) {    // 上一个站在现在这个站的下方
                    return 3;
                } else {
                    return 2;
                }
            } else {                        // z相对距离比x相对距离大，横向进站
                if (lastPos[0] > nowPos[0]) {    // 上一个站在这一个站的右侧
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     * @implNote 与getStationTrend功能类似，不过是出站方向的
     * @return 0: 左向; 1: 右向; 2: 上向; 3: 下向
     */
    private int getStationForward(Integer[] nowPos, Integer[] nextPos) {
        int xDistance = Math.abs(nowPos[0] - nextPos[0]);
        int zDistance = Math.abs(nowPos[1] - nextPos[1]);

        if (xDistance == zDistance) {       // xz轴相对距离相等，横向出站
            if (nextPos[0] > nowPos[0]) {   // 下一个站在现在这一个站的右侧
                return 1;
            } else {
                return 0;
            }
        } else if (xDistance == 0) {        // x坐标相等，竖向出站
            if (nextPos[1] > nowPos[1]) {   // 下一个站在现在这一个站的下方
                return 3;
            } else {
                return 2;
            }
        } else if (zDistance == 0) {        // z坐标相等，横向出站
            if (nextPos[0] > nowPos[0]) {   // 下一个站在这一个站的右侧
                return 1;
            } else {
                return 0;
            }
        } else {
             if (xDistance > zDistance) {   // x相对距离比z相对距离大，横向出站
                 if (nextPos[0] > nowPos[0]) {    // 下一个站在这一个站的右侧
                     return 1;
                 } else {
                     return 0;
                 }
             } else {                      // z相对距离比x相对距离大，纵向出站
                 if (nextPos[1] > nowPos[1]) {    // 下一个站在这一个站的下侧
                     return 3;
                 } else {
                     return 2;
                 }
             }
        }
    }

    public void updateLineTrend() {
        // 清空linePassed
        linePassed.clear();

        // 设置LinePassed
        for (LineData lineData : data) {
            List<LineData.StationData> stations = lineData.getStations();
            for (int i = 0; i< stations.size(); i++) {
                Integer[] nowPos = stations.get(i).getPosition();
                String key = nowPos[0] + "/" + nowPos[1];

                if (i != 0) {    // 如果不是第一个站，考虑进进站问题
                    Integer[] lastPos = stations.get(i-1).getPosition();
                    int trend = getStationTrend(new Integer[]{lastPos[0], lastPos[1]}, nowPos);
                    Integer[] passedTrend = linePassed.get(key);

                    if (passedTrend == null) {
                        if (trend == 0) {
                            linePassed.put(key, new Integer[]{1, 0, 0, 0});
                        } else if (trend == 1) {
                            linePassed.put(key, new Integer[]{0, 1, 0, 0});
                        } else if (trend == 2) {
                            linePassed.put(key, new Integer[]{0, 0, 1, 0});
                        } else {
                            linePassed.put(key, new Integer[]{0, 0, 0, 1});
                        }
                    } else {
                        passedTrend[trend] += 1;
                    }
                }

                if (stations.size() != i+1) {      // 如果不是最后一个站，考虑出站问题
                    Integer[] nextPos = stations.get(i+1).getPosition();
                    int trend = getStationForward(nowPos, nextPos);
                    Integer[] passedTrend = linePassed.get(key);

                    if (passedTrend == null) {
                        if (trend == 0) {
                            linePassed.put(key, new Integer[]{1, 0, 0, 0});
                        } else if (trend == 1) {
                            linePassed.put(key, new Integer[]{0, 1, 0, 0});
                        } else if (trend == 2) {
                            linePassed.put(key, new Integer[]{0, 0, 1, 0});
                        } else {
                            linePassed.put(key, new Integer[]{0, 0, 0, 1});
                        }
                    } else {
                        passedTrend[trend] += 1;
                    }
                }

                if (stations.size() == 1) {       // 如果线路只有1个站点
                    // 如果这个站点没有被创建过，则创建（防止其他线路也只有一个站点且是同一个站点）
                    linePassed.computeIfAbsent(key, k -> new Integer[]{0, 0, 0, 0});
                }
            }
        }
    }
}
