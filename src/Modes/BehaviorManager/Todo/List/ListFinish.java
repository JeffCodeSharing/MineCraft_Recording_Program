package Modes.BehaviorManager.Todo.List;

import Modes.BehaviorManager.Todo.DataController;
import Tools.EDTool;
import Tools.IOTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ListFinish类用于将已完成的计划表转移到已完成的列表中。
 */
public class ListFinish {
    private final List<String> values = new ArrayList<>();

    /**
     * ListFinish类的构造函数。
     *
     * @param controller DataController对象(使用ClassExpandTool的反射来完成)，用于获取已完成的计划表中的数据
     */
    public ListFinish(DataController controller) {
        List<String[]> controller_values = controller.getValues();
        // 添加保存内容
        for (String[] temp : controller_values) {
            // 将每个计划项转换为只有文字的格式（没有颜色标记，还要加密）
            values.add(EDTool.encrypt(temp[0]));
        }
    }

    /**
     * 将未完成计划表转移到已完成列表的入口方法。
     *
     * @param path      计划表的路径
     * @param list_name 计划表的文件名
     * @return 如果转移成功，返回已完成列表中的新文件名；否则返回null
     */
    public String entrance(String path, String list_name) {
        // 保存计划表
        if (!IOTool.overrideFile(new File(path, list_name).getPath(), values)) {    // 保存计划表，要不然转移了以前的计划表
            return null;
        }

        // 转移文件
        String start_path = new File(path, list_name).getPath();
        String end_name_head = path.substring(0, path.length() - 5) + "finish";
        String end_name = list_name;
        int i = 1;

        // 检查finish文件夹中是否存在和转移文件同名的文件
        while (new File(end_name_head, end_name).exists()) {
            end_name = list_name + "-" + i;
            i++;
        }

        String end_path = new File(end_name_head, end_name).getPath();

        // 将文件从start_path转移到end_path
        return IOTool.moveFile(start_path, end_path) ? end_name : null;
    }
}
