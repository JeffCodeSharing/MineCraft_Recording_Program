package Tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonTool {
    public static JSONObject readJson(String path) {
        String[] temp = IOTool.readFile(path);

        if (temp != null) {
            return JSON.parseObject(String.join("", temp));
        } else {
            return null;
        }
    }

    public static void writeJson(JSONObject jsonObject, String path) {
        System.out.println(jsonObject.containsKey("path"));
        String jsonString = JSONObject.toJSONString(jsonObject, SerializerFeature.WriteMapNullValue);
        System.out.println(jsonString);
        IOTool.overrideFile(path, new String[]{jsonString});
    }
}
