package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class RemoveValue {
    /**
     * @param value 触发删除操作的DataPack，删除的时候要在它的 父DataPack 中进行操作
     */
    public static void remove(DataController.DataPack value) {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "删除信息", "您是否要删除这一个信息", "删除后信息将不复存在，其下的信息也将不复存在");

        if (type.get() == ButtonType.OK) {
            boolean success = value.getParent().getChildren().remove(value);
            if (!success) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "删除失败", "请刷新后重新尝试");
            }
        }
    }
}
