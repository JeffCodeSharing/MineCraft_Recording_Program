package Modes.BehaviorManager.Todo.List;

import Modes.BehaviorManager.Todo.DataController;
import Modes.BehaviorManager.Todo.ED.Encryption;
import Tools.IOTool;
import Tools.JsonTool;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

/**
 * ListFinish类用于将已完成的计划表转移到已完成的列表中。
 */
public class ListFinish {
    private final JSONObject values;

    /**
     * ListFinish类的构造函数。
     *
     * @param controller DataController对象(使用ClassExpandTool的反射来完成)，用于获取已完成的计划表中的数据
     */
    public ListFinish(DataController controller) {
        // 获取数据+加密数据
        values = Encryption.encrypt(controller.getJsonValues());
    }

    /**
     * 将未完成计划表转移到已完成列表的入口方法。
     *
     * @param path      doing文件夹 的路径
     * @param list_name 计划表的文件名
     * @return 如果转移成功，返回已完成列表中的新文件名；否则返回null
     */
    public String entrance(String path, String list_name) {
        // 定义起始文件路径和移动后的文件路径
        String oldPath = new File(path, list_name+".json").getPath();

        String newPathHead = new File(new File(path).getParent(), "finish").getPath();
        String newName = list_name;

        // 检查finish文件夹中是否存在和转移文件同名的文件
        int i = 1;
        while (new File(newPathHead, newName+".json").exists()) {
            newName = list_name + "-" + i;
            i++;
        }

        String endPath = new File(newPathHead, newName +".json").getPath();

        // 保存计划表
        if (!JsonTool.writeJson(values, oldPath)) {    // 保存计划表，要不然转移了以前的计划表
            return null;
        }

        // 将文件从start_path转移到end_path
        return IOTool.moveFile(oldPath, endPath) ? newName : null;
    }
}
