package Model;

import Tools.JsonTool;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Downloader {
    public boolean entrance(String download_path) {
        if (download_path != null) {
            if (download_path.equals("")) {
                download_path = System.getProperty("user.dir") + File.separator + "mrp_mod.jar";
            } else {
                download_path = download_path + File.separator + "mrp_mod.jar";
            }
        } else {
            download_path = System.getProperty("user.dir") + File.separator + "mrp_mod.jar";
        }

        try {
            JSONObject jsonData = JsonTool.read_json(System.getProperty("user.dir") + File.separator + "data" +
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
