/**
 * 模块：行为管理器
 * 类名：ValueFinish
 * 用途：用于标记值为已完成的操作类
 */
package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

public class ValueFinish {
    private final DataController controller;
    private final int index;

    /**
     * 构造方法
     * @param controller 数据控制器对象(ClassExpandTool反射实现)
     * @param index 值的索引
     */
    public ValueFinish(DataController controller, int index) {
        this.controller = controller;
        this.index = index;
    }

    /**
     * 入口方法，用于启动完成操作
     */
    public void entrance() {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "完成？", "你真的确定完成了吗", "完成后将无法再调整为未完成！！！");

        if (type.get() == ButtonType.OK) {
            if (index == -1) {    // 当用户点击的是标题时
                List<String[]> values = controller.getValues();
                for (int i=0; i<values.size(); i++) {
                    String[] temp = values.get(i);
                    temp[1] = "GREEN";
                    values.set(i, temp);
                }
            } else {
                // 不在数组中直接进行修改是为了防止直接修改了DataController中记录的数据，避免了 不管成不成功都会更改颜色
                String[] values = controller.get(index);
                controller.set(index, values[0], "GREEN");
            }
        }
    }
}
