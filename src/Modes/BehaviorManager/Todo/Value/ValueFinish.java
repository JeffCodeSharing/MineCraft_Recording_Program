package Modes.BehaviorManager.Todo.Value;

import Modes.BehaviorManager.Todo.DataController;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ValueFinish {
    public static void entrance(DataController.DataPack value) {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "完成？", "你真的确定完成了吗", "完成后将无法再调整为未完成！！！");

        if (type.get() == ButtonType.OK) {
            value.setDone(true);
        }
    }
}
