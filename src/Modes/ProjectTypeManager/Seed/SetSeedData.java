package Modes.ProjectTypeManager.Seed;

import Tools.ClassTool;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.File;

/**
 * 更改种子号用
 */
public class SetSeedData {
    private final Group group;
    private final String[] seed_values;
    private final String path;

    public SetSeedData(Group group, String[] seed_values, String path) {
        this.group = group;
        this.seed_values = seed_values;
        this.path = path;
    }

    public void entrance() {
        this.draw_controls();
    }

    public void draw_controls() {
        group.getChildren().clear();

        Label title = WinTool.createLabel(0, 0, 200, 30, 25, "更改种子号", Color.BLUE);

        Label label = WinTool.createLabel(0, 40, 80, 30, 18, "种子号：");
        TextField seedInput = WinTool.createTextField(80, 40, 160, 30, 15);

        Button confirm = WinTool.createButton(110, 120, 60, 30, 15, "确定");
        Button return_menu = WinTool.createButton(180, 120, 60, 30, 15, "返回");

        confirm.setOnAction(actionEvent -> save_data(seedInput.getText()));
        return_menu.setOnAction(actionEvent -> {
            // 返回
            ClassTool tool = new ClassTool("Modes" + File.separator + "ProjectTypeManager" + File.separator +
                    "Seed" + File.separator + "ShowSeed.class");
            Class<?> clazz = tool.get_class("Modes.ProjectTypeManager.Seed.ShowSeed");
            tool.invoke_method(clazz, "draw_controls",
                    new Class[]{String.class}, new Object[]{path}, new Class[]{Group.class}, new Object[]{group});
        });

        group.getChildren().addAll(title, label, seedInput, confirm, return_menu);
    }

    /**
     * 将新的种子号保存到文件中。
     * 如果数据保存成功，则弹出成功提示框，要不然弹出失败提示框
     *
     * @param seed_num 用户输入的新的种子号。
     */
    private void save_data(String seed_num) {
        seed_values[0] = seed_num;

        String[] read_data = IOTool.read_file(path);
        read_data[1] = EDTool.encrypt(seed_values[0]) + "\0" + seed_values[1] + "\0" + EDTool.encrypt(seed_values[2]);

        if (IOTool.override_file(path, read_data)) {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "保存成功", "");
        } else {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "保存失败", "请重新尝试");
        }
    }
}
