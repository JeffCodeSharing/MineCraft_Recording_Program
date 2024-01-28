package Modes.ModDownloader;

import Interface.AbstractWindow;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ModDownloader extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField download_path = WinTool.createTextField(110, 65, 235, 35, 16);
        Button choose_path = WinTool.createButton(345, 65, 35, 35, 13, "...");

        choose_path.setOnAction(actionEvent -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择下载文件夹");
            File file = chooser.showDialog(new Stage());

            if (file != null) {
                download_path.setText(file.getPath());
            }
        });

        Button download_button = WinTool.createButton(30, 120, 120, 40, 16, "下载");
        download_button.setOnAction(actionEvent -> {
            boolean success = downloadMod(download_path.getText());

            if (success) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功下载", "已下载",
                        "已下载到：" + System.getProperty("user.dir") + File.separator + "mrp_mod.jar");
                global_stage.close();
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "下载失败", "请检查网络配置");
            }
        });

        Button close_window = WinTool.createButton(170, 120, 120, 40, 16, "关闭窗口");
        close_window.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 300, 40, 20, "下载模组", Color.BLUE),
                WinTool.createLabel(10, 60, 100, 35, 16, "下载路径："), download_path, choose_path,
                download_button, close_window
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("下载模组");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(400);
        stage.setHeight(250);
        stage.showAndWait();
    }

    public boolean downloadMod(String download_path) {
        if ((download_path != null) && !download_path.equals("")) {
            download_path = download_path + File.separator + "mrp_mod.jar";
        } else {
            download_path = System.getProperty("user.dir") + File.separator + "mrp_mod.jar";
        }

        try {
            JSONObject jsonData = JsonTool.readJson(System.getProperty("user.dir") + File.separator + "data" +
                    File.separator + "information.json");

            URL url = new URL("https://jeffcodesharing.github.io/Files/mrp_mod/" + jsonData.getString("version") + "/mrp_mod.jar");
            URLConnection connection = url.openConnection();
            connection.connect();

            File output_file = new File(download_path);
            output_file.getParentFile().mkdirs();
            output_file.createNewFile();

            InputStream in = connection.getInputStream();
            OutputStream out = new FileOutputStream(output_file.getPath());

            int reading_byte;
            while ((reading_byte = in.read()) != -1) {
                out.write(reading_byte);
            }

            in.close();
            out.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
