package Modes.StationMap;

import Tools.WinTool;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

public class MapOutput {
    public static void output(Canvas map) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // 创建一个快照参数对象
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);

            // 获取Canvas的快照
            WritableImage writableImage = map.snapshot(sp, null);

            // 创建文件输出流并将快照保存为图像文件
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "导出图片成功", "导出路径：" + file.getPath());
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "导出图片错误", "请重试");
            }
        }
    }
}
