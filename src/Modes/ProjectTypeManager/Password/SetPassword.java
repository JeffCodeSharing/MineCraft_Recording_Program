package Modes.ProjectTypeManager.Password;

import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.File;

/**
 * 用于更改密码
 */
public class SetPassword {
    private final Group group;
    private String return_password;
    private final String path;

    /**
     * 构造函数，初始化Group，Group来自ShowPassword的传入
     */
    public SetPassword(Group group, String path) {
        this.group = group;
        this.path = path;
    }

    public String entrance() {
        drawControls();
        return null;
    }

    private void drawControls() {
        group.getChildren().clear();

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
                return_password = password.getText();

                if (writePassword(return_password)) {
                    WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "密码设置成功", "密码是" + return_password);
                    toMenu();    // 返回主页
                } else {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "密码设置失败", "请重新尝试");
                }
            } else {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "密码输入不一致", "请检查");
            }
        });
        return_menu.setOnAction(actionEvent -> toMenu());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 300, 30, 22, "更改密码", Color.BLUE),
                WinTool.createLabel(10, 40, 100, 30, 18, "解锁密码："),
                password_plaintext, password, view1,
                WinTool.createLabel(10, 80, 100, 30, 18, "再次输入："),
                again_plaintext, again, view2,
                warning,
                confirm, return_menu
        );
    }

    private void imageClick(ImageView view, TextField plaintext, PasswordField ciphertext) {
        if (plaintext.isVisible()) {
            view.setImage(new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png"));
        } else {
            view.setImage(new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "hide_password.png"));
        }
        plaintext.setVisible(!plaintext.isVisible());
        ciphertext.setVisible(!ciphertext.isVisible());
    }

    private void toMenu() {
        ShowPassword clazz = new ShowPassword(path);
        clazz.drawControls(group);
    }

    private boolean writePassword(String password) {
        try {
            String filePath = path + File.separator + "checkItem.json";
            JSONObject fileData = JSONObject.parseObject(String.join("", IOTool.readFile(filePath)));

            fileData.replace("password", EDTool.encrypt(password));
            return IOTool.overrideFile(filePath, new String[]{fileData.toJSONString()});
        } catch (Exception e) {
            return false;
        }
    }
}
