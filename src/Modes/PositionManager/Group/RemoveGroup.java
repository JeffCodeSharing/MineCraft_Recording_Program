package Modes.PositionManager.Group;

import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 本Class用于删除坐标组
 */
public class RemoveGroup {
    private final List<Integer> item_num;
    private final List<List<String[]>> group_value;
    private final List<String> group_name;

    /**
     * DeleteGroup类的构造函数，初始化删除坐标组所需的参数。
     *
     * @param item_num   记录每个坐标组中已有的项目数
     * @param group_value 存储坐标组数据的列表的列表
     * @param group_name  存储坐标组名的列表
     */
    public RemoveGroup(List<Integer> item_num, List<List<String[]>> group_value, List<String> group_name) {
        this.item_num = item_num;
        this.group_value = group_value;
        this.group_name = group_name;
    }

    /**
     * DeleteGroup入口，供调用者使用。
     *
     * @param box          坐标组显示的VBox
     * @param delete_path  要删除的坐标组所在的文件路径
     * @param title_value  坐标组的对应的index
     */
    public void entrance(VBox box, String delete_path, String title_value) {
        // 获取title_value中的组数
        Scanner sc = new Scanner(title_value);
        sc.useDelimiter("\\D+");
        int pos = sc.nextInt() - 1;   // index要比组数小1

        start(box, new File(delete_path), pos);
    }

    /**
     * DeleteGroup的真正入口，供方法entrance调用。
     *
     * @param box          坐标组显示的VBox
     * @param delete_file  要删除的坐标组文件
     * @param pos          坐标组的位置
     */
    private void start(VBox box, File delete_file, int pos) {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "删除项目", "您是否要删除这一个坐标组", "删除后坐标组中的数据将不复存在");

        if (type.get() == ButtonType.OK) {
            int group_item_num = item_num.get(pos);
            int item_pos = 2;
            for (int i = 0; i < pos; i++) {
                item_pos = item_pos + 2 + item_num.get(i);
            }

            box.getChildren().remove(item_pos, item_pos + group_item_num);
            item_num.remove(pos);
            group_value.remove(pos);
            group_name.remove(pos);

            // 删除文件
            try {
                if (!delete_file.delete()) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "删除失败", "");
            }
        }
    }
}
