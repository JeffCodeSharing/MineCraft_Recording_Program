package Update;

import Tools.IOTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Updater extends Application {
    private final String UPDATE_VERSION;
    private final String FILE_URL;
    private final String SAVE_PATH;

    public Updater(String version) {
        this.UPDATE_VERSION = version;
        this.FILE_URL = "https://update.codeforfree.kesug.com/mc_recording_program/" + version + "/sources.zip";
        this.SAVE_PATH = System.getProperty("user.dir") + File.separator + "cache";
    }

    public void update() {
        init_cache();
        start(new Stage());
    }

    @Override
    public void start(Stage stage) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        VBox root = new VBox(progressBar);
        Scene scene = new Scene(root, 300, 100);

        stage.setScene(scene);
        stage.setTitle("下载进度");
        stage.show();

        Task<Void> downloadTask = createDownloadTask();
        Thread downloadThread = new Thread(downloadTask);
        downloadThread.start();

        // 更改信息
        JSONObject jsonObject = JsonTool.read_json(System.getProperty("user.dir") + File.separator + "data" +
                File.separator + "information.json");
        jsonObject.replace("version", UPDATE_VERSION);
    }

    private void init_cache() {
        String cache_path = System.getProperty("user.dir") + File.separator + "cache";
        File cache_file = new File(cache_path);

        // 删除cache文件夹
        if (cache_file.exists()) {
            IOTool.remove_directory(cache_path);
        }

        // 创建cache文件夹
        cache_file.mkdirs();
    }

    private Task<Void> createDownloadTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                URL url = new URL(FILE_URL);
                URLConnection connection = url.openConnection();
                int fileSize = connection.getContentLength();

                try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                     FileOutputStream out = new FileOutputStream(SAVE_PATH)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    int totalBytesRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        double progress = (double) totalBytesRead / fileSize;
                        updateProgress(progress, 1);
                    }
                } catch (Exception e) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "升级出现错误", "错误原因：" + e.getMessage());
                }

                return null;
            }
        };
    }
}
