package Modes.StationMap;

import Modes.StationMap.io.MapOutput;
import Modes.StationMap.io.MapSaver;
import Tools.ColorTool;
import Tools.WinTool;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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
    private Canvas showingMap;
    private GraphicsContext showingContext;
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

        map = WinTool.createCanvas(0, 0, -1, -1, Color.WHITE);
        context = map.getGraphicsContext2D();
        showingMap = WinTool.createCanvas(0, 80, 630, 630, Color.WHITE);
        showingContext = showingMap.getGraphicsContext2D();

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
                showingMap,
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

        // 获取到X和Z轴的最大最小值
        int minX = 0;
        int maxX = 0;
        int minZ = 0;
        int maxZ = 0;
        for (LineData lineData : data) {
            for (LineData.StationData stationData : lineData.getStations()) {
                Integer[] value = stationData.getPosition();
                if (value[0] > maxX) maxX = value[0];
                else if (value[0] < minX) minX = value[0];

                if (value[1] > maxZ) maxZ = value[1];
                else if (value[0] < minZ) minZ = value[1];
            }
        }

        // 处理4个极限值并清空画布
        // moveX与moveZ用于在绘制的时候进行偏移，正常的各偏移20px
        int moveX = (minX<0) ? Math.abs(minX)+20 : 20;
        int moveZ = (minZ<0) ? Math.abs(minZ)+20 : 20;

        // 清空画布（+100是为了保证正常绘制，是大部分情况）
        map.setWidth(maxX+moveX+100);
        map.setHeight(maxZ+moveZ+100);
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, maxX+moveX+100, maxZ+moveZ+100);

        // 添加站点
        linePassed.forEach((s, passedTrend) -> {
            // stationType的内容规则”x/z“，x和z指代两个坐标，所以使用"/"来进行划分
            String[] stationPosTemp = s.split("/");
            double[] stationPos = new double[]{Double.parseDouble(stationPosTemp[0]), Double.parseDouble(stationPosTemp[1])};
            double[] usingPassedTrend = new double[]{(double) Math.max(passedTrend[0], passedTrend[1]), (double) Math.max(passedTrend[2], passedTrend[3])};

            context.setLineWidth(2);
            context.setStroke(Color.BLACK);

            context.strokeArc(stationPos[0]-usingPassedTrend[1]*2.5-2.5+moveX, stationPos[1]-usingPassedTrend[0]*2.5-2.5+moveZ,
                    5, 5, 90, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0]-usingPassedTrend[1]*2.5+moveX, stationPos[1]-usingPassedTrend[0]*2.5-2.5+moveZ,
                   stationPos[0]+usingPassedTrend[1]*2.5+moveX, stationPos[1]-usingPassedTrend[0]*2.5-2.5+moveZ);
            context.strokeArc(stationPos[0]+usingPassedTrend[1]*2.5-2.5+moveX, stationPos[1]-usingPassedTrend[0]*2.5-2.5+moveZ,
                    5, 5, 0, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0]+usingPassedTrend[1]*2.5+2.5+moveX, stationPos[1]-usingPassedTrend[0]*2.5+moveZ,
                    stationPos[0]+usingPassedTrend[1]*2.5+2.5+moveX, stationPos[1]+usingPassedTrend[0]*2.5+moveZ);
            context.strokeArc(stationPos[0]+usingPassedTrend[1]*2.5-2.5+moveX, stationPos[1]+usingPassedTrend[0]*2.5-2.5+moveZ,
                    5, 5, 270, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0]+usingPassedTrend[1]*2.5+moveX, stationPos[1]+usingPassedTrend[0]*2.5+2.5+moveZ,
                    stationPos[0]-usingPassedTrend[1]*2.5+moveX, stationPos[1]+usingPassedTrend[0]*2.5+2.5+moveZ);
            context.strokeArc(stationPos[0]-usingPassedTrend[1]*2.5-2.5+moveX, stationPos[1]+usingPassedTrend[0]*2.5-2.5+moveZ,
                    5, 5, 180, 90, ArcType.OPEN);
            context.strokeLine(stationPos[0]-usingPassedTrend[1]*2.5-2.5+moveX, stationPos[1]+usingPassedTrend[0]*2.5+moveZ,
                    stationPos[0]-usingPassedTrend[1]*2.5-2.5+moveX, stationPos[1]-usingPassedTrend[0]*2.5+moveZ);
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

                    // 获取前后站的PassedTrend
                    Integer[] lastTrendAll = linePassed.get(lastTrendKey);
                    Integer[] nowTrendAll = linePassed.get(nowTrendKey);

                    // 获取drawTemp（这里的Temp是要使用的，用于绘制重线的时候不会重叠）
                    Integer[] lastDrawTemp = drawTemp.get(lastTrendKey);
                    Integer[] nowDrawTemp = drawTemp.get(nowTrendKey);

                    // 加工前后站的PassedTrend
                    double[] lastTrend = new double[]
                            {(double) Math.max(lastTrendAll[0], lastTrendAll[1]),
                                    (double) Math.max(lastTrendAll[2], lastTrendAll[3])};
                    double[] nowTrend = new double[]
                            {(double) Math.max(nowTrendAll[0], nowTrendAll[1]),
                                    (double) Math.max(nowTrendAll[2], nowTrendAll[3])};

                    // 确定绘制方向
                    // -1: 不往那个方向, 0: 往那个方向，但是是在拐弯后的, 1: 往那个方向，并且在拐弯前的
                    Integer[] direction = lineData.getDirections().get(i-1);
                    int leftSide = -1;
                    int rightSide = -1;
                    int upSide = -1;
                    int downSide = -1;

                    /*
                      对4个Side进行赋值
                      赋值规则：
                        1. direction中有值为1时，横向与纵向分别直接赋值（出发口和出发方向一次是相同的）
                        direction[0]==1 => leftSide=1;
                        direction[1]==1 => rightSide=1;
                        direction[2]==1 => upSide=1;
                        direction[3]==1 => downSide=1;

                        2. direction中有值为1时，横向与纵向分别直接赋值（进站口和出发二次方向是相反的）
                        direction[0]==0 => rightSide=0;
                        direction[1]==0 => leftSide=0;
                        direction[2]==0 => downSide=0;
                        direction[3]==0 => upSide=0;

                        3. direction中有值为0和1的正好是一对相反方向的，direction为0的不赋值（防止覆盖）
                        e.g.
                        direction[0]==1, direction[1]==0 => leftSide=1
                     */
                    if (direction[0] == 1) leftSide = 1;
                    else if (direction[1] == 1) rightSide = 1;
                    else if (direction[2] == 1) upSide = 1;
                    else downSide = 1;

                    if (direction[0] == 0 && direction[1] != 1) rightSide = 0;
                    else if (direction[1] == 0 && direction[0] != 1) leftSide = 0;
                    else if (direction[2] == 0 && direction[3] != 1) downSide = 0;
                    else if (direction[3] == 0 && direction[2] != 1) upSide = 0;

                    // 对应不同side划线
                    context.setLineWidth(5);
                    context.setStroke(ColorTool.engToColor(lineData.getColor()));

                    // 绘制路线
                    // +2.5的目的是为了抵消线的宽度，让线尽量看起来在中间
                    // 先向左
                    if (leftSide == 1) {
                        // 没有其他操作
                        if ((upSide == -1) && (downSide == -1)) {
                            context.strokeLine(lastPos[0]-5-lastTrend[1]*2.5+moveX,
                                    lastPos[1]-lastTrendAll[0]*2.5+2.5+(lastTrendAll[0]-lastDrawTemp[0])*5+moveZ,
                                    nowPos[0]+5+nowTrend[1]*2.5+moveX,
                                    nowPos[1]- nowTrendAll[1]*2.5+2.5+(nowTrendAll[1]-nowDrawTemp[1])*5+moveZ);
                        } else {     // 还有其他操作
                            context.strokeLine(lastPos[0]-5-lastTrend[1]*2.5+moveX,
                                    lastPos[1]- lastTrendAll[0]*2.5+2.5+(lastTrendAll[0]-lastDrawTemp[0])*5+moveZ,
                                    nowPos[0]- nowTrendAll[3]*2.5+2.5+(nowTrendAll[3]-nowDrawTemp[3])*5+moveX,
                                    lastPos[1]- lastTrendAll[0]*2.5+2.5+(lastTrendAll[0]-lastDrawTemp[0])*5+moveZ);
                        }
                    } else if (leftSide == 0) {     // 后向左
                        if (upSide == 1) {
                            context.strokeLine(lastPos[0]- lastTrendAll[2]*3+2.5+(lastTrendAll[2]-lastDrawTemp[2])*5+moveX,
                                    nowPos[1]- nowTrendAll[1]*2.5+2.5+(nowTrendAll[1]-nowDrawTemp[1])*5+moveZ,
                                    nowPos[0]+5+lastTrend[1]*2.5+moveX,
                                    nowPos[1]- nowTrendAll[1]*2.5+2.5+(nowTrendAll[1]-nowDrawTemp[1])*5+moveZ);
                        } else {
                            context.strokeLine(lastPos[0]- lastTrendAll[3]*2.5+2.5+(lastTrendAll[3]-lastDrawTemp[3])*5+moveX,
                                    nowPos[1]- nowTrendAll[1]*2.5+2.5+(nowTrendAll[1]-nowDrawTemp[1])*5+moveZ,
                                    nowPos[0]+5+lastTrend[1]*2.5+moveX,
                                    nowPos[1]- nowTrendAll[1]*2.5+2.5+(nowTrendAll[1]-nowDrawTemp[1])*5+moveZ);
                        }
                    }

                    // 先向右
                    if (rightSide == 1) {
                        // 没有其他操作
                        if ((upSide == -1) && (downSide == -1)) {
                            context.strokeLine(lastPos[0]+5+lastTrend[1]*2.5+moveX,
                                    lastPos[1]- lastTrendAll[1]*2.5+2.5+(lastTrendAll[1]-lastDrawTemp[1])*5+moveZ,
                                    nowPos[0]-5-nowTrend[1]*2.5+moveX,
                                    nowPos[1]- nowTrendAll[0]*2.5+2.5+(nowTrendAll[0]-nowDrawTemp[0])*5+moveZ);
                        } else {      // 还有其他操作
                            context.strokeLine(lastPos[0]+5+lastTrend[1]*2.5+moveX,
                                    lastPos[1]- lastTrendAll[1]*2.5+2.5+(lastTrendAll[1]-lastDrawTemp[1])*5+moveZ,
                                    nowPos[0]-5- nowTrendAll[2]*2.5+2.5+(nowTrendAll[2]-nowDrawTemp[2])*5+moveX,
                                    lastPos[1]- lastTrendAll[1]*2.5+2.5+(lastTrendAll[1]-lastDrawTemp[1])*5+moveZ);
                        }
                    } else if (rightSide == 0) {     // 后向右
                        if (upSide == 1) {
                            context.strokeLine(lastPos[0]- lastTrendAll[2]*2.5+2.5+(lastTrendAll[2]-lastDrawTemp[2])*5+moveX,
                                    nowPos[1]- nowTrendAll[0]*2.5+2.5+(nowTrendAll[0]-nowDrawTemp[0])*5+moveZ,
                                    nowPos[0]-5-nowTrend[1]*2.5+moveX,
                                    nowPos[1]- nowTrendAll[0]*2.5+2.5+(nowTrendAll[0]-nowDrawTemp[0])*5+moveZ);
                        } else {
                            context.strokeLine(lastPos[0]- lastTrendAll[3]*2.5+2.5+(lastTrendAll[3]-lastDrawTemp[3])*5+moveX,
                                    nowPos[1]- nowTrendAll[0]*2.5+2.5+(nowTrendAll[0]-nowDrawTemp[0])*5+moveZ,
                                    nowPos[0]-5-nowTrend[1]*2.5+moveX,
                                    nowPos[1]- nowTrendAll[0]*2.5+2.5+(nowTrendAll[0]-nowDrawTemp[0])*5+moveZ);
                        }
                    }

                    // 先向上
                    if (upSide == 1) {
                        // 没有其他操作
                        if ((leftSide == -1) && (rightSide == -1)) {
                            context.strokeLine(lastPos[0]- lastTrendAll[2]*2.5+2.5+(lastTrendAll[2]-lastDrawTemp[2])*5+moveX,
                                    lastPos[1]-5-lastTrend[0]*2.5+moveZ,
                                    nowPos[0]- nowTrendAll[3]*2.5+2.5+(nowTrendAll[3]-nowDrawTemp[3])*5+moveX,
                                    nowPos[1]+5+nowTrend[0]*2.5+moveZ);
                        } else {    // 还有其他操作
                            context.strokeLine(lastPos[0]-lastTrendAll[2]*2.5+2.5+(lastTrendAll[2]-lastDrawTemp[2])*5+moveX,
                                    lastPos[1]-5-lastTrend[0]*2.5+moveZ,
                                    lastPos[0]-lastTrendAll[2]*2.5+2.5+(lastTrendAll[2]-lastDrawTemp[2])*5+moveX,
                                    nowPos[1]-lastTrendAll[0]*2.5+2.5+(lastTrendAll[0]-lastDrawTemp[0])*5+moveZ);
                        }
                    } else if (upSide == 0) {      // 后向上
                        if (leftSide == 1) {
                            context.strokeLine(nowPos[0]- nowTrendAll[3]*2.5+2.5+(nowTrendAll[3]-nowDrawTemp[3])*5+moveX,
                                    lastPos[1]- lastTrendAll[0]*2.5+2.5+(lastTrendAll[0]-lastDrawTemp[0])*5+moveZ,
                                    nowPos[0]- nowTrendAll[3]*2.5+2.5+(nowTrendAll[3]-nowDrawTemp[3])*5+moveX,
                                    nowPos[1]+5+nowTrend[0]*2.5+moveZ);
                        } else {
                            context.strokeLine(nowPos[0]- nowTrendAll[3]*2.5+2.5+(nowTrendAll[3]-nowDrawTemp[3])*5+moveX,
                                    lastPos[1]- lastTrendAll[1]*2.5+2.5+(lastTrendAll[1]-lastDrawTemp[1])*5+moveZ,
                                    nowPos[0]- nowTrendAll[3]*2.5+2.5+(nowTrendAll[3]-nowDrawTemp[3])*5+moveX,
                                    nowPos[1]+5+nowTrend[0]*2.5+moveZ);
                        }
                    }

                    // 先向下
                    if (downSide == 1) {
                        // 没有其他操作
                        if ((leftSide == -1) && (rightSide == -1)) {
                            context.strokeLine(lastPos[0]- lastTrendAll[3]*2.5+2.5+(lastTrendAll[3]-lastDrawTemp[3])*5+moveX,
                                    lastPos[1]+5+lastTrend[0]*2.5+moveZ,
                                    nowPos[0]- nowTrendAll[2]*2.5+2.5+(nowTrendAll[2]-nowDrawTemp[2])*5+moveX,
                                    nowPos[1]-5-nowTrend[0]*2.5+moveZ);
                        } else {    // 还有其他操作
                            context.strokeLine(lastPos[0]- lastTrendAll[3]*2.5+2.5+(lastTrendAll[3]-lastDrawTemp[3])*5+moveX,
                                    lastPos[1]+5+lastTrend[0]*2.5+moveZ,
                                    lastPos[0]- lastTrendAll[3]*2.5+2.5+(lastTrendAll[3]-lastDrawTemp[3])*5+moveX,
                                    nowPos[1]-5- nowTrendAll[0]*2.5+2.5+(nowTrendAll[0]-nowDrawTemp[0])*5+moveZ);
                        }
                    } else if (downSide == 0) {      // 后向下
                        if (leftSide == 1) {
                            context.strokeLine(nowPos[0]- nowTrendAll[2]*2.5+2.5+(nowTrendAll[2]-nowDrawTemp[2])*5+moveX,
                                    lastPos[1]- lastTrendAll[0]*2.5+2.5+(lastTrendAll[0]-lastDrawTemp[0])*5+moveZ,
                                    nowPos[0]-nowTrendAll[2]*2.5+2.5+(nowTrendAll[2]-nowDrawTemp[2])*5+moveX,
                                    nowPos[1]-5-nowTrend[0]*2.5+moveZ);
                        } else {
                            context.strokeLine(nowPos[0]- nowTrendAll[2]*2.5+2.5+(nowTrendAll[2]-nowDrawTemp[2])*5+moveX,
                                    lastPos[1]- lastTrendAll[1]*2.5+2.5+(lastTrendAll[1]-lastDrawTemp[1])*5+moveZ,
                                    nowPos[0]- nowTrendAll[2]*2.5+2.5+(nowTrendAll[2]-nowDrawTemp[2])*5+moveX,
                                    nowPos[1]-5-nowTrend[0]*2.5+moveZ);
                        }
                    }

                    // 减少All中的次数
                    if (leftSide == 1) {
                        lastDrawTemp[0] -= 1;
                        if (upSide == 0) {
                            nowDrawTemp[3] -= 1;
                        } else if (downSide == 0) {
                            nowDrawTemp[2] -= 1;
                        } else {
                            nowDrawTemp[1] -= 1;
                        }
                    } else if (rightSide == 1) {
                        lastDrawTemp[1] -= 1;
                        if (upSide == 0) {
                            nowDrawTemp[3] -= 1;
                        } else if (downSide == 0) {
                            nowDrawTemp[2] -= 1;
                        } else {
                            nowDrawTemp[0] -= 1;
                        }
                    } else if (upSide == 1) {
                        lastDrawTemp[2] -= 1;
                        if (leftSide == 0) {
                            nowDrawTemp[1] -= 1;
                        } else if (rightSide == 0) {
                            nowDrawTemp[0] -= 1;
                        } else {
                            nowDrawTemp[3] -= 1;
                        }
                    } else {
                        lastDrawTemp[3] -= 1;
                        if (leftSide == 0) {
                            nowDrawTemp[1] -= 1;
                        } else if (rightSide == 0) {
                            nowDrawTemp[0] -= 1;
                        } else {
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
                        position[0]-textBounds.getWidth()/2+moveX, position[1]+trend[0]*2.5+20+moveZ
                );
            }
        }

        // 将map中的内容缩小
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage image = map.snapshot(sp, null);

        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        if (image.getWidth()>630 || image.getHeight()>630) {
            if (image.getWidth()>image.getHeight()) {
                view.setFitWidth(630);
            } else {
                view.setFitHeight(630);
            }
        }

        showingContext.fillRect(0, 0, 630, 630);
        showingContext.drawImage(view.snapshot(new SnapshotParameters(), null), 0, 0);
    }

    public void updateLineTrend() {
        // 清空linePassed
        linePassed.clear();

        // 设置LinePassed
        for (LineData lineData : data) {
            List<LineData.StationData> stations = lineData.getStations();
            for (int i = 0; i< stations.size(); i++) {
                Integer[] nowPos = stations.get(i).getPosition();
                String nowKey = nowPos[0] + "/" + nowPos[1];

                if (i != 0) {    // 如果不是第一个站，正常加载进出站
                    Integer[] lastPos = stations.get(i - 1).getPosition();
                    String lastKey = lastPos[0] + "/" + lastPos[1];
                    Integer[] tempTrend = lineData.getDirections().get(i - 1);
                    Integer[] nowTrend = linePassed.get(nowKey);
                    Integer[] lastTrend = linePassed.get(lastKey);

                    // 添加出站的（即LastTrend）
                    if (tempTrend[0] == 1) {
                        lastTrend[0] += 1;
                    } else if (tempTrend[1] == 1) {
                        lastTrend[1] += 1;
                    } else if (tempTrend[2] == 1) {
                        lastTrend[2] += 1;
                    } else {
                        lastTrend[3] += 1;
                    }

                    // 添加进站的（即NowTrend）
                    if (nowTrend == null) {
                        if (tempTrend[0] == 0) {
                            linePassed.put(nowKey, new Integer[]{1, 0, 0, 0});
                        } else if (tempTrend[1] == 0) {
                            linePassed.put(nowKey, new Integer[]{0, 1, 0, 0});
                        } else if (tempTrend[2] == 0) {
                            linePassed.put(nowKey, new Integer[]{0, 0, 1, 0});
                        } else {
                            linePassed.put(nowKey, new Integer[]{0, 0, 0, 1});
                        }
                    } else {
                        if (tempTrend[0] == 0) {
                            nowTrend[0] += 1;
                        } else if (tempTrend[1] == 0) {
                            nowTrend[1] += 1;
                        } else if (tempTrend[2] == 0) {
                            nowTrend[2] += 1;
                        } else {
                            nowTrend[3] += 1;
                        }
                    }
                } else {     // 如果是第一个站，就创建那个站的Trend记录
                    // 如果这个站点没有被创建过，则创建（防止其他线路也只有一个站点且是同一个站点）
                    linePassed.computeIfAbsent(nowKey, k -> new Integer[]{0, 0, 0, 0});
                }

                if (stations.size() == 1) {       // 如果线路只有1个站点
                    // 如果这个站点没有被创建过，则创建（防止其他线路也只有一个站点且是同一个站点）
                    linePassed.computeIfAbsent(nowKey, k -> new Integer[]{0, 0, 0, 0});
                }
            }
        }
    }
}
