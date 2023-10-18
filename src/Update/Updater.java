package Update;

import Tools.IOTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater extends Application {
    private final String UPDATE_VERSION;
    private final String ROOT_PATH;
    private final String SAVE_PATH;
    private static boolean update_success = true;

    public Updater(String version, String root_path) {
        this.UPDATE_VERSION = version;
        this.ROOT_PATH = root_path + "/" + version + "/";
        this.SAVE_PATH = System.getProperty("user.dir") + File.separator + "cache";
    }

    public boolean update() {
        init_cache();
        PlatformImpl.runAndWait(() -> start(new Stage()));

        return update_success;
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

        String json_path = SAVE_PATH + "/update_items.json";
        String source_path = SAVE_PATH + "/sources.zip";
        try {
            // 下载文件
            download_file(ROOT_PATH + "update_items.json", json_path);
            download_file(ROOT_PATH + "sources.zip", source_path);

            // 解压文件
            unpack_zip(source_path);

            // 更改版本信息
            String information_path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "information.json";
            JSONObject jsonObject = JsonTool.read_json(information_path);
            jsonObject.replace("version", UPDATE_VERSION);
            JsonTool.write_json(jsonObject, information_path);
        } catch (Exception e) {
            update_success = false;

            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "更新失败", "请重新尝试");
        }

        stage.close();
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
        URL url = new URL(download_path);
        URLConnection connection = url.openConnection();

        // 打开连接
        connection.connect();

        // 获取输入流
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // 创建输出流，用于保存下载的代码文件
        FileWriter writer = new FileWriter(save_path);

        // 读取输入流中的数据，并写入输出流中
        String line;
        while ((line = reader.readLine()) != null) {
            writer.append(line).append("\n");
        }

        // 关闭流
        writer.close();
        reader.close();
        inputStream.close();
    }

    private void unpack_zip(String zip_path) throws Exception {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip_path))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String destinationPath = System.getProperty("user.dir") + File.separator + "cache" + File.separator + "unpack_data";
                File newFile = new File(destinationPath, zipEntry.getName());

                // 创建父目录
                newFile.getParentFile().mkdirs();

                // 写入文件内容
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                zipEntry = zis.getNextEntry();
            }
        }
    }
}
