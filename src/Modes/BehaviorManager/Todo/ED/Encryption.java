package Modes.BehaviorManager.Todo.ED;

import Tools.EDTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * This class in order to encrypt the data
 */
public class Encryption {
    public static JSONObject encrypt(JSONObject tempData) {
        JSONObject finalData = new JSONObject();

        // 加密当前级名称
        String clearMsg = tempData.getString("name");
        String secretMsg = EDTool.encrypt(clearMsg);
        finalData.put("name", secretMsg);

        // 复制颜色属性
        finalData.put("color", tempData.get("color"));

        JSONArray clearChildren = tempData.getJSONArray("children");
        JSONArray secretChildren = new JSONArray();
        for (Object childTemp: clearChildren) {
            JSONObject child = (JSONObject) childTemp;

            // 递归
            secretChildren.add(encrypt(child));
        }
        finalData.put("children", secretChildren);

        return finalData;
    }
}
