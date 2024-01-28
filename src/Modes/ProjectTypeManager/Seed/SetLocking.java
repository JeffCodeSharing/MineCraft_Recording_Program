package Modes.ProjectTypeManager.Seed;

import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.File;

public class SetLocking {
    private final boolean to_lock;
    private final Group group;
    private final String path; // 已经到check_item

    public SetLocking(Group group, String path, boolean to_lock) {
        this.group = group;
        this.path = path;
        this.to_lock = to_lock;
    }

    /**
     * 入口方法
     *
     * @return 返回 null
     */
    public String[] entrance() {
        drawControls();
        return null;
    }

    /**
     * 绘制控件
     */
    public void drawControls() {
        group.getChildren().clear();

        if (to_lock) {
            // 预处理warning的label
            Label warning = WinTool.createLabel(10, 110, 280, 30, 15, "", Color.ORANGE);

            TextField password_plaintext = WinTool.createTextField(110, 40, 140, 30, 15);
            TextField again_plaintext = WinTool.createTextField(110, 80, 140, 30, 15);
            password_plaintext.setVisible(false);
            again_plaintext.setVisible(false);

            PasswordField password = WinTool.createPasswordField(110, 40, 140, 30, 15);
            PasswordField again = WinTool.createPasswordField(110, 80, 140, 30, 15);

            password_plaintext.textProperty().addListener((observableValue, old_value, new_value) -> password.setText(new_value));
            again_plaintext.textProperty().addListener((observable, old_value, new_value) -> again.setText(new_value));
            password.textProperty().addListener((observableValue, old_value, new_value) -> password_plaintext.setText(new_value));
            again.textProperty().addListener((observableValue, old_value, new_value) -> {
                // 同步数据
                again_plaintext.setText(new_value);

                // 数据判断
                if (!password.getText().equals(new_value)) {
                    warning.setText("密码输入不一致");
                } else {
                    warning.setText("");
                }
            });

            ImageView view1 = WinTool.createImageView(250, 40, 30, 30,
                    System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png");
            ImageView view2 = WinTool.createImageView(250, 80, 30, 30,
                    System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png");

            view1.setOnMousePressed(mouseEvent -> imageClick(view1, password_plaintext, password));
            view2.setOnMousePressed(mouseEvent -> imageClick(view2, again_plaintext, again));

            Button confirm = WinTool.createButton(120, 140, 60, 30, 15, "确定");
            Button return_menu = WinTool.createButton(190, 140, 60, 30, 15, "返回");

            confirm.setOnAction(actionEvent -> {
                if (password.getText().equals(again.getText())) {
                    lockConfirm(password.getText());
                } else {
                    WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "密码输入不一致", "请检查");
                }
            });
            return_menu.setOnAction(actionEvent -> {
                // 返回
                ShowSeed clazz = new ShowSeed(path);
                clazz.drawControls(group);
            });

            group.getChildren().addAll(
                    WinTool.createLabel(0, 0, 200, 30, 25, "锁定", Color.BLUE),
                    WinTool.createLabel(10, 40, 100, 30, 18, "解锁密码："),
                    password_plaintext, password, view1,
                    WinTool.createLabel(10, 80, 100, 30, 18, "再次输入："),
                    again_plaintext, again, view2,
                    warning,
                    confirm, return_menu
            );
        } else {
            TextField password_plaintext = WinTool.createTextField(110, 40, 140, 30, 15);
            password_plaintext.setVisible(false);
            PasswordField password = WinTool.createPasswordField(110, 40, 140, 30, 15);

            password_plaintext.textProperty().addListener((observableValue, old_value, new_value) -> password.setText(new_value));
            password.textProperty().addListener((observableValue, old_value, new_value) -> password_plaintext.setText(new_value));

            ImageView view = WinTool.createImageView(250, 40, 30, 30,
                    System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png");
            view.setOnMousePressed(mouseEvent -> imageClick(view, password_plaintext, password));

            Button unlock = WinTool.createButton(120, 140, 60, 30, 15, "解锁");
            Button return_menu = WinTool.createButton(190, 140, 60, 30, 15, "返回");

            unlock.setOnAction(actionEvent -> unlockConfirm(password.getText()));
            return_menu.setOnAction(actionEvent -> {
                ShowSeed clazz = new ShowSeed(path);
                clazz.drawControls(group);
            });

            group.getChildren().addAll(
                    WinTool.createLabel(0, 0, 280, 30, 20, "解锁", Color.BLUE),
                    WinTool.createLabel(10, 40, 100, 30, 18, "输入密码："),
                    password_plaintext, password, view,
                    unlock, return_menu
            );
        }
    }

    /**
     * 锁定确认
     *
     * @param password 解锁密码
     */
    private void lockConfirm(String password) {
        String[] file_data = IOTool.readFile(path);
        String[] seed_data = file_data[1].split("\0");

        seed_data[2] = EDTool.encrypt(password);
        file_data[1] = seed_data[0] + "\0TRUE\0" + seed_data[2];

        if (IOTool.overrideFile(path, file_data)) {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "锁定成功", "解锁密码：" + password);
        }

        // 返回主页
        ShowSeed clazz = new ShowSeed(path);
        clazz.drawControls(group);
    }

    /**
     * 解锁确认
     *
     * @param password 解锁密码
     */
    private void unlockConfirm(String password) {
        String[] file_data = IOTool.readFile(path);
        String[] seed_data = file_data[1].split("\0");

        if (password.equals(EDTool.decrypt(seed_data[2]))) {      // 解密后比较
            file_data[1] = seed_data[0] + "\0FALSE\0" + seed_data[2];
            if (IOTool.overrideFile(path, file_data)) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "成功！", "密码正确", "解锁成功！");

                ShowSeed clazz = new ShowSeed(path);
                clazz.drawControls(group);
            } else {
                WinTool.createAlert(Alert.AlertType.ERROR, "失败", "解锁失败", "请重新尝试，不是你的密码问题");
            }
        } else {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "失败", "密码错误", "请重新填写");
        }
    }

    /**
     * 图片点击事件
     * @param view      ImageView
     * @param plaintext 明文
     * @param ciphertext 密文
     */
    private void imageClick(ImageView view, TextField plaintext, PasswordField ciphertext) {
        if (plaintext.isVisible()) {
            view.setImage(new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png"));
        } else {
            view.setImage(new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "hide_password.png"));
        }
        plaintext.setVisible(!plaintext.isVisible());
        ciphertext.setVisible(!ciphertext.isVisible());
    }
}