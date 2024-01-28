package Modes.LogManager.Date;

import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.File;
import java.util.Optional;

/**
 * 删除日期
 */
public class RemoveDate {

    /**
     * 删除日期
     * @param listView 日志列表
     * @param path 日志路径
     */
    public void entrance(ListView<String> listView, String path) {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "删除日志", "您是否要删除这一个日志", "删除后本日志将不复存在，\n删除前请先确保没有在使用本文件夹");

        if (type.get() == ButtonType.OK) {
            String deleteValue = listView.getSelectionModel().getSelectedItem();
            if (!(deleteValue == null)) {
                if (!deleteValue.equals("")) {
                    listView.getItems().remove(deleteValue);    // 更新listView

                    boolean returnValue = IOTool.removeDirectory(path + File.separator + deleteValue);
                    if (!returnValue) {
                        WinTool.createAlert(Alert.AlertType.ERROR, "失败", "删除失败", "文件夹内可能被更改或使用");
                    } else {
                        WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "删除成功", "");
                    }
                }
            }
        }
    }
}
