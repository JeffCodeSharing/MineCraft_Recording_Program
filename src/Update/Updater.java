package Update;

import Tools.IOTool;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater {
    private final String UPDATE_VERSION;
    private final String ROOT_PATH;
    private final String CACHE_PATH;
    private final String BIN_PATH;
    private static boolean update_success = true;
    private static boolean run_finish = false;

    public Updater(String version, String root_path) {
        this.UPDATE_VERSION = version;
        this.ROOT_PATH = root_path + "/" + version + "/";
        this.CACHE_PATH = System.getProperty("user.dir") + File.separator + "cache" + File.separator;
        this.BIN_PATH = System.getProperty("user.dir") + File.separator + "bin" + File.separator;
    }

    public boolean update() {
        initCache();
        Platform.runLater(this::start);

        while (true) {
            try {
                Thread.sleep(5);
            } catch (Exception ignored) {}
            if (run_finish) {
                return update_success;
            }
        }
    }

    public void start() {
        String json_path = CACHE_PATH + "/update_items.json";
        String source_path = CACHE_PATH + "/sources.zip";
        try {
            // 下载文件
            downloadFile(ROOT_PATH + "update_items.json", json_path);
            downloadFile(ROOT_PATH + "sources.zip", source_path);

            // 解压文件
            unpackZip(source_path);

            // 将解压后的信息归位到程序中位置
            JSONObject change_log = JsonTool.readJson(
                    System.getProperty("user.dir") + File.separator + "cache" + File.separator + "update_items.json"
            );

            coverFiles(BIN_PATH, "bin", change_log);
            coverFiles(System.getProperty("user.dir") + File.separator, "root", change_log);

            // 更改版本信息
            String information_path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "information.json";
            JSONObject jsonObject = JsonTool.readJson(information_path);
            jsonObject.replace("version", UPDATE_VERSION);
            JsonTool.write_json(jsonObject, information_path);
        } catch (Exception e) {
            e.printStackTrace();
            update_success = false;
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "更新失败", "若不影响使用请忽略");
        }

        run_finish = true;
    }

    private void initCache() {
        String cache_path = System.getProperty("user.dir") + File.separator + "cache";
        File cache_file = new File(cache_path);

        // 删除cache文件夹
        if (cache_file.exists()) {
            IOTool.removeDirectory(cache_path);
        }

        // 创建cache文件夹
        cache_file.mkdirs();
    }

    private void downloadFile(String download_path, String save_path) throws Exception {
        URL url = new URL(download_path);
        URLConnection connection = url.openConnection();

        // 打开连接
        connection.connect();

        // 获取输入流
        InputStream input = connection.getInputStream();
        OutputStream output = new FileOutputStream(save_path);

        // 读取输入流中的数据，并写入输出流中
        int reading_byte;
        while ((reading_byte = input.read()) != -1) {
            output.write(reading_byte);
        }

        // 关闭流
        input.close();
        output.close();
    }

    private void unpackZip(String zip_path) throws Exception {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip_path))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String destinationPath = System.getProperty("user.dir") + File.separator + "cache" + File.separator + "unpack_data";
                File newFile = new File(destinationPath, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    // 创建文件夹
                    newFile.mkdirs();
                } else {
                    // 创建文件
                    newFile.getParentFile().mkdirs();
                    if (newFile.getName().contains(".")) {    // 如果文件名中有"."号，就创建文件
                        newFile.createNewFile();
                    }

                    // 写入文件内容
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    private void coverFiles(String root_path, String type, JSONObject change_log) throws Exception {
        JSONObject jsonObject = change_log.getJSONObject(type);

        JSONArray create_array = jsonObject.getJSONArray("Create");
        JSONArray remove_array = jsonObject.getJSONArray("Remove");
        JSONArray set_array = jsonObject.getJSONArray("Set");

        String unpack_root = CACHE_PATH + "unpack_data" + File.separator + type + File.separator;

        // 按照json列表中查看
        if (create_array != null) {
            for (int i=0; i<create_array.size(); i++) {
                // 以"."号的package包来寻址
                String package_path = create_array.getString(i).replace(".", "/") + ".class";
                File cache_file = new File(unpack_root, package_path);
                File lib_file = new File(root_path, package_path);

                lib_file.getParentFile().mkdirs();
                lib_file.createNewFile();

                if (IOTool.copyFile(cache_file.getPath(), lib_file.getPath())) {
                    throw new RuntimeException("Copy File Failed");
                }
            }
        }

        if (remove_array != null) {
            for (int i=0; i<remove_array.size(); i++) {
                // 以"."号的package包来寻址
                String package_path = remove_array.getString(i).replace(".", "/") + ".class";
                File lib_file = new File(root_path, package_path);

                if (IOTool.removeFile(lib_file.getPath())) {
                    throw new RuntimeException("Remove File Failed");
                }
            }
        }

        if (set_array != null) {
            for (int i=0; i<set_array.size(); i++) {
                String package_path = set_array.getString(i).replace(".", "/") + ".class";
                File lib_file = new File(root_path, package_path);

                if (IOTool.removeFile(lib_file.getPath())) {
                    throw new RuntimeException("Remove File Failed");
                }

                lib_file.createNewFile();

                File cache_file = new File(unpack_root, package_path);
                if (IOTool.copyFile(cache_file.getPath(), lib_file.getPath())) {
                    throw new RuntimeException("Copy Data Failed");
                }
            }
        }

        // 遍历文件夹，并且删除空文件夹
        IOTool.removeEmptyDir(root_path);
    }
}
