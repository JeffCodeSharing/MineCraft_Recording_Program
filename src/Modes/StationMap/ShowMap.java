package Modes.StationMap;

import Tools.ColorTool;
import Tools.WinTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ShowMap {
    private Canvas map;
    private GraphicsContext context;
    private final Map<String, LineData> data;
    private final String path;
    private String nowUsingLine;

    /**
     * @param path 这个path是项目的根目录
     */
    public ShowMap(String path) {
        this.path = path;

        Map<String, LineData> data_temp;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(path + File.separator + "map" + File.separator + "map_data")
            );

            data_temp = (HashMap<String, LineData>) inputStream.readObject();

            inputStream.close();
        } catch (Exception ignored) {
            data_temp = new HashMap<>();
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "加载地图失败", "");
        }
        this.data = data_temp;
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

        TextField x_point = WinTool.createTextField(30, 45, 70, 30, 16);
        TextField z_point = WinTool.createTextField(150, 45, 70, 30, 16);
        Button choose_line = WinTool.createButton(240, 45, 120, 30, 16, "选择线路");
        Button create_station = WinTool.createButton(360, 45, 120, 30, 16, "创建站点");
        Button create_line = WinTool.createButton(480, 45, 120, 30, 16, "创建线路");

        choose_line.setOnAction(actionEvent -> {
            LineChooser chooser = new LineChooser(data);
            this.nowUsingLine = chooser.entrance()[0];
        });
        create_station.setOnAction(actionEvent ->
                CreateStation.create(context, data, nowUsingLine, x_point, z_point, ColorTool.engToColor(data.get(nowUsingLine).getColor()))
        );
        create_line.setOnAction(actionEvent -> {
            CreateLine creator = new CreateLine(data);
            creator.entrance();
        });

        box.getChildren().addAll(
                map_output, save_map,
                map,
                WinTool.createLabel(0, 45, 40, 30, 25, "x:"), x_point,
                WinTool.createLabel(120, 45, 40, 30, 25, "z:"), z_point,
                choose_line, create_station, create_line
        );
    }

    private void drawPoints() {
        data.forEach((lineName, lineData) -> {
            Map<Integer, Integer[]> stations = lineData.getStations();
            AtomicReferenceArray<Integer> last_position = null;

            for (int i=0; i<stations.size(); i++) {
                Integer[] stationPosition = stations.get(i);

                context.setFill(ColorTool.engToColor(lineData.getColor()));
                context.fillOval(stationPosition[0]-5, stationPosition[1]-5, 10, 10);

                if (last_position != null) {
                    context.setLineWidth(5);
                    context.setStroke(ColorTool.engToColor(lineData.getColor()));
                    context.strokeLine(last_position.get(0), last_position.get(1), stationPosition[0], stationPosition[1]);
                }
                last_position = new AtomicReferenceArray<>(stationPosition);
            }
        });
    }
}
