package Modes.ProjectTypeManager.Seed;

import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * 更改种子号用
 */
public class SetSeedData {
    private final Group group;
    private final String path;

    public SetSeedData(Group group, String path) {
        this.group = group;
        this.path = path;
    }

    public void entrance() {
        this.drawControls();
    }

    public void drawControls() {
        group.getChildren().clear();

        Label title = WinTool.createLabel(0, 0, 200, 30, 25, "更改种子号", Color.BLUE);

        Label label = WinTool.createLabel(0, 40, 80, 30, 18, "种子号：");
        TextField seedInput = WinTool.createTextField(80, 40, 160, 30, 15);

        Button confirm = WinTool.createButton(110, 120, 60, 30, 15, "确定");
        Button return_menu = WinTool.createButton(180, 120, 60, 30, 15, "返回");

        confirm.setOnAction(actionEvent -> saveData(seedInput.getText()));
        return_menu.setOnAction(actionEvent -> {
            // 返回
            ShowSeed clazz = new ShowSeed(path);
            clazz.drawControls(group);
        });

        group.getChildren().addAll(title, label, seedInput, confirm, return_menu);
    }

    /**
     * 将新的种子号保存到文件中。
     * 如果数据保存成功，则弹出成功提示框，要不然弹出失败提示框
     *
     * @param seedNum 用户输入的新的种子号。
     */
    private void saveData(String seedNum) {

        JSONObject jsonData = JSONObject.parseObject(String.join("", IOTool.readFile(path)));
        jsonData.replace("seed", EDTool.encrypt(seedNum));

        boolean success = IOTool.overrideFile(path, new String[]{jsonData.toJSONString()});
        if (!success) {
            WinTool.createAlert(Alert.AlertType.ERROR, "失败", "保存失败", "请重新尝试");
        } else {
            ShowSeed clazz = new ShowSeed(path);
            clazz.drawControls(group);
        }
    }
}
