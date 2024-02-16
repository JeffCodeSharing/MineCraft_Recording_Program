package Modes.StationMap;

import Tools.WinTool;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.Map;

public class CreateStation {
    public static void create(GraphicsContext context, Map<String, LineData> linesData, String lineType, TextField field_x, TextField field_z, Color color) {
        try {
            int x = Integer.parseInt(field_x.getText());
            int z = Integer.parseInt(field_z.getText());
            LineData lineData = linesData.get(lineType);
            int station_order = lineData.getStations().size();
            Integer[] lastPosition = lineData.get(station_order-1);

            lineData.addStation(station_order, new Integer[]{x, z});

            context.setFill(color);
            context.setStroke(color);
            context.setLineWidth(5);
            context.fillOval(x-5, z-5, 10, 10);
            context.strokeLine(x, z, lastPosition[0], lastPosition[1]);
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "输入数据有错误或还未选择线路", "");
        }
    }
}
