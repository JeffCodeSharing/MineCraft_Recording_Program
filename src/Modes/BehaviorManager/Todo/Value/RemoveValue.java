package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * 模块：行为管理器
 * 类名：DeleteValue
 * 用途：用于删除值的操作类
 */
public class RemoveValue {

    /**
     * 运行删除操作
     * @param controller 数据控制器(ClassExpandTool反射实现)
     * @param index 索引值
     */
    public void entrance(DataController controller, int index) {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "删除信息", "您是否要删除这一个信息", "删除后信息将不复存在，其下的信息也将不复存在");

        if (type.get() == ButtonType.OK) {
            controller.remove(index);
        }
    }
}
