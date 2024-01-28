package Tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonTool {
    public static JSONObject readJson(String path) {
        String[] temp = IOTool.readFile(path);

        if (temp != null) {
            StringBuilder json_data = new StringBuilder();

            for (String s : temp) {
                json_data.append(s);
            }

            return JSON.parseObject(json_data.toString());
        } else {
            return null;
        }
    }

    public static void write_json(JSONObject jsonObject, String path) {
        String jsonString = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat);
        IOTool.overrideFile(path, new String[]{jsonString});
    }
}
