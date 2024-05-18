package Modes.ProjectTypeManager.Seed;

import Interface.AbstractWindow;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 * ShowSeed类负责显示种子号并允许用户更改种子号。
 * 并且也是管理Seed的模块，承担主页的工作
 * 该类扩展了JavaFX的Application类，并实现了AbstractWindow接口。
 */
public class ShowSeed extends Application implements AbstractWindow<Void> {
    private String seed;
    private final String path;    // 已包含到CheckItem.json
    private final JSONObject jsonData;
    private boolean canChange;


    /**
     * ShowSeed类的构造函数。
     * @param path 存放种子文件的目录路径。
     */
    public ShowSeed(String path) {
        this.path = path;

        JSONObject readValues;
        try {
            readValues = JSONObject.parseObject(String.join("", IOTool.readFile(path)));
            this.seed = EDTool.decrypt(readValues.getString("seed"));
            this.canChange = readValues.getBooleanValue("seedCanChange");

            if (seed.equals("")) {
                seed = "未填写";
            }
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取错误", "请重新尝试");
            readValues = new JSONObject();
            seed = "未填写";
            canChange = false;
        }
        this.jsonData = readValues;
    }

    /**
     * ShowSeed类的入口方法。
     * 启动JavaFX应用程序并显示种子窗口。
     *
     * @return null
     */
    @Override
    public Void entrance() {
        start(new Stage());
        return null;
    }

    /**
     * 绘制种子窗口的主页UI控件
     *
     * @param group 添加控件的JavaFX Group对象。
     */
    @Override
    public void drawControls(Group group) {
        group.getChildren().clear();

        Label seed_label = WinTool.createLabel(10, 10, 300, 30, 20, "种子号：" + seed);

        // 菜单加载
        ContextMenu menu = new ContextMenu();

        MenuItem copy = new MenuItem("复制种子号");
        copy.setOnAction(actionEvent -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString((seed.equals("未填写") ? "" : seed));
            clipboard.setContent(content);
        });

        menu.getItems().addAll(copy);
        seed_label.setContextMenu(menu);

        Button changeButton = WinTool.createButton(10, 70, 100, 40, 15, "更改种子号");
        changeButton.setOnAction(actionEvent -> {
            if (canChange) {
                SetSeedData setter = new SetSeedData(group, path);
                setter.entrance();
            } else {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "禁止", "修改种子号的行为被禁止", "请解锁之后再进行更改");
            }
        });

        group.getChildren().addAll(seed_label, changeButton);

        if (canChange) {
            Button lockButton = WinTool.createButton(120, 70, 100, 40, 18, "锁定");
            lockButton.setOnAction(actionEvent -> {
                canChange = false;
                saveChange();
                drawControls(group);
            });
            group.getChildren().add(lockButton);
        } else {
            Button unlockButton = WinTool.createButton(120, 70, 100, 40, 18, "解锁");
            unlockButton.setOnAction(actionEvent -> {
                canChange = true;
                saveChange();
                drawControls(group);
            });
            group.getChildren().add(unlockButton);
        }
    }

    /**
     * 启动JavaFX应用程序并显示种子窗口。
     *
     * @param stage 表示窗口的JavaFX Stage对象。
     */
    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setScene(scene);
        stage.setTitle("种子号");
        stage.setHeight(220);
        stage.setWidth(300);
        stage.setResizable(false);
        stage.showAndWait();
    }

    private void saveChange() {
        try {
            jsonData.replace("seedCanChange", canChange);
            IOTool.overrideFile(path, new String[]{jsonData.toJSONString()});
        } catch (Exception ignored) {}
    }
}
