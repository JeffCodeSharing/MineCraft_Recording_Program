package Update;

import Constant.SSLConstant;
import Tools.JsonTool;
import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private String update_version = "";
    private String root_path = "";

    public boolean check() {
        JSONObject jsonObject = JsonTool.readJson(System.getProperty("user.dir") + File.separator + "data" +
                File.separator + "information.json");
        root_path = jsonObject.getString("root_path");
        String now_version = jsonObject.getString("version");

        for (int i=0; i<2; i++) {
            update_version = getUpdateVersion(i, now_version);
            String path = root_path + "/" + update_version + "/update_items.json";
            try {
                // 获取默认的 SSLContext
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, SSLConstant.TRUST_MANAGER, new java.security.SecureRandom());

                // 设置默认的 SSLSocketFactory
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    if (reader.readLine() != null) {
                        return true;
                    }
                    reader.close();

                    return false;
                }

                connection.disconnect();
            } catch (Exception e) {
                break;
            }
        }

        return false;
    }

    /**
     * @param change_level 0: 最低版本号; 1: 中间版本号; 2: 最高版本号
     * @param version      输入版本号
     * @return 将生成的返回
     */
    private static String getUpdateVersion(int change_level, String version) {
        String[] version_split = version.split("\\.");
        if (change_level == 0) {
            int temp = Integer.parseInt(version_split[2]);
            version_split[2] = String.valueOf(temp + 1);
        } else if (change_level == 1) {
            int temp = Integer.parseInt(version_split[1]);
            version_split[1] = String.valueOf(temp + 1);
        } else if (change_level == 2) {
            int temp = Integer.parseInt(version_split[0]);
            version_split[0] = String.valueOf(temp + 1);
        }

        return String.join(".", version_split);
    }

    public String getUpdateVersion() {
        return update_version;
    }

    public String getRootPath() {
        return root_path;
    }
}
