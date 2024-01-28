package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 模块：行为管理器
 * 类名：SaveValue
 * 用途：用于保存值的操作类
 */
public class SaveValue {

    /**
     * 入口方法，用于启动保存操作
     * @param controller 数据控制器(ClassExpandTool反射实现)
     * @param path 保存文件路径
     * @param list_name 列表名称
     */
    public void entrance(DataController controller, String path, String list_name) {
        List<String[]> values = controller.getValues();
        String list_path = path + File.separator + list_name;

        start(list_path, values);
    }

    /**
     * 启动保存操作
     * @param list_path 列表保存路径
     * @param values 值列表
     */
    private void start(String list_path, List<String[]> values) {
        List<String> write_in_list = new ArrayList<>();

        for (String[] strings:values) {
            write_in_list.add(EDTool.encrypt(strings[0] + "\0" + strings[1]));   // 加密后添加到ArrayList中
        }

        if (IOTool.overrideFile(list_path, write_in_list)) {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "保存成功", "");
        } else {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "写入文件错误", "请重新尝试或检查项目是否被占用");
        }
    }
}
