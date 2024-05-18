package Tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.File;

public class JsonTool {
    public static JSONObject readJson(String path) {
        String[] temp = IOTool.readFile(path);

        if (temp != null) {
            return JSON.parseObject(String.join("", temp));
        } else {
            return null;
        }
    }

    public static JSONObject readJson(File file) {
        return readJson(file.getPath());
    }

    public static boolean writeJson(JSONObject jsonObject, String path) {
        String jsonString = JSONObject.toJSONString(jsonObject, SerializerFeature.WriteMapNullValue);
        return IOTool.overrideFile(path, new String[]{jsonString});
    }

    public static boolean writeJson(JSONObject jsonObject, File file) {
        return writeJson(jsonObject, file.getPath());
    }
}
