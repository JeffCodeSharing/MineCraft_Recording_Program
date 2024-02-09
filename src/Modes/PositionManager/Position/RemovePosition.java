package Modes.PositionManager.Position;

import Modes.PositionManager.Group.SaveGroup;
import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * DeletePosition 类负责从 指定坐标组中删除一个坐标
 */
public class RemovePosition {
    private final Pane box;
    private final int index;
    private final int group_index;
    private final List<Integer> item_num;
    private final List<String[]> group_value;
    private final String group_path;

    /**
     * 使用指定的参数构造 DeletePosition 对象。
     *
     * @param box         包含坐标的 VBox 容器。
     * @param index       要删除的位置的索引。
     * @param group_index 组的索引。
     * @param item_num    每个组中的项目数列表。
     * @param group_value 组数据列表。
     * @param group_dir   储存文件所在目录。
     * @param group_name  要删除的组的名称。
     */
    public RemovePosition(Pane box, int index, int group_index, List<Integer> item_num, List<String[]> group_value,
                          String group_dir, String group_name) {
        this.box = box;
        this.index = index;
        this.group_index = group_index;
        this.item_num = item_num;
        this.group_value = group_value;
        this.group_path = group_dir + File.separator + group_name;
    }

    /**
     * 开始删除位置的过程。
     */
    public void entrance() {
        start();
    }

    /**
     * 显示确认对话框以确认删除位置。
     * 如果确认删除，则删除位置并保存组。
     */
    private void start() {
        Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                "删除坐标", "您是否要删除这一个坐标", "删除后坐标将不复存在，\n删除后记得要保存组");

        if (type.get() == ButtonType.OK) {
            int controls_num = 2 + 1 + index;
            for (int i = 0; i < group_index - 1; i++) {
                controls_num = controls_num + item_num.get(i) + 2;
            }

            box.getChildren().remove(controls_num);
            group_value.remove(index);
            item_num.set(group_index - 1, item_num.get(group_index - 1) - 1);

            SaveGroup saver = new SaveGroup();
            saver.entrance(group_path, group_value);
        }
    }
}