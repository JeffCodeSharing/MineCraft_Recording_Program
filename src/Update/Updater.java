package Update;

import Tools.IOTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Updater extends Application {
    private final String UPDATE_VERSION;
    private final String ROOT_PATH;
    private final String SAVE_PATH;

    public Updater(String version, String root_path) {
        this.UPDATE_VERSION = version;
        this.ROOT_PATH = root_path + "/" + version + "/";
        this.SAVE_PATH = System.getProperty("user.dir") + File.separator + "cache";
    }

    public void update() {
        init_cache();
        start(new Stage());
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        group.getChildren().addAll(progressBar);

        stage.setScene(scene);
        stage.setTitle("下载进度");
        stage.show();

        try {
            download_file(ROOT_PATH + "update_items.json", SAVE_PATH + "/update_items.json");
            download_file(ROOT_PATH + "sources.zip", SAVE_PATH + "/sources.zip");
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "更新失败", "请重新尝试");
        }

        // 更改信息
        String json_path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "information.json";
        JSONObject jsonObject = JsonTool.read_json(json_path);
        jsonObject.replace("version", UPDATE_VERSION);
        JsonTool.write_json(jsonObject, json_path);
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

    private void download_file(String download_path, String save_path) throws Exception {
        URLConnection connection = new URL(download_path).openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        File file = new File(save_path);
        file.createNewFile();

        List<String> list = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        IOTool.override_file(save_path, list);
    }
}
