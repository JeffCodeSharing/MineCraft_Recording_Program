package Update;

import Tools.JsonTool;
import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

public class CheckUpdate {
    public static boolean check() {
        JSONObject jsonObject = JsonTool.read_json(System.getProperty("user.dir") + File.separator + "data" +
                File.separator + "information.json");
        String root_path = jsonObject.getString("root_path");
        String version = jsonObject.getString("version");

        // 创建一个信任所有证书的 TrustManager
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        for (int i=0; i<2; i++) {
            String path = root_path + "/" + get_update_version(i, version) + "/update_item.json";
            try {
                // 获取默认的 SSLContext
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // 设置默认的 SSLSocketFactory
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return true;
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
    private static String get_update_version(int change_level, String version) {
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

        return version_split[0] + "." + version_split[1] + "." + version_split[2];
    }
}
