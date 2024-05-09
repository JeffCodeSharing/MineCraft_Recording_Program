package Modes.BehaviorManager.Todo.ED;

import Tools.EDTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * This class in order to decrypt the data
 */
public class Decryption {
    public static JSONObject decrypt(JSONObject tempData) {
        JSONObject finalData = new JSONObject();

        // 解密当前级名称
        String secretMsg = tempData.getString("name");
        String clearMsg = EDTool.decrypt(secretMsg);
        finalData.put("name", clearMsg);

        // 复制颜色属性
        finalData.put("color", tempData.get("color"));

        JSONArray secretChildren = tempData.getJSONArray("children");
        JSONArray clearChildren = new JSONArray();
        for (Object childTemp : secretChildren) {
            JSONObject child = (JSONObject) childTemp;

            // 递归
            clearChildren.add(decrypt(child));
        }
        finalData.put("children", clearChildren);

        return finalData;
    }
}
