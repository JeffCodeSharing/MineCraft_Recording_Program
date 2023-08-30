package Modes.PositionManager.Group;

import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.Optional;

/**
 * 本Class用于删除坐标组
 */
public class RemoveGroup {
    /**
     * DeleteGroup入口，供调用者使用。
     *
     * @param box          坐标组显示的VBox
     * @param delete_path  要删除的坐标组所在的文件路径
     * @param title_value  坐标组的对应的index
     */
    public void entrance(VBox box, String delete_path, String title_value) {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "删除项目", "您是否要删除这一个坐标组", "删除后坐标组中的数据将不复存在");

        if (type.get() == ButtonType.OK) {
            // 删除文件
            try {
                if (!new File(delete_path).delete()) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "删除失败", "");
            }
        }
    }
}
