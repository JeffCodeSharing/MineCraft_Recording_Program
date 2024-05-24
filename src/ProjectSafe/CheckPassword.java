package ProjectSafe;

import Interface.AbstractWindow;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

/**
 * 在使用项目之前先尽心密码操作
 * 返回值: true --> 密码正确, false --> 用户没有输入
 */
public class CheckPassword extends Application implements AbstractWindow<Boolean> {
    private boolean runChecker = true;
    private final Stage global_stage = new Stage();
    private String project_name;
    private String password;

    /**
     * 构造方法，初始化了密码值及是否进行密码操作
     *
     * @param path 要确认密码的项目的路径
     */
    public CheckPassword(String path) {
        if (path != null) {
            try {
                File projectFile = new File(path);
                this.project_name = projectFile.getName();

                JSONObject jsonData = JSONObject.parseObject(String.join("", IOTool.readFile(new File(path, "checkItem.json"))));
                this.password = EDTool.decrypt(jsonData.getString("password"));
            } catch (Exception e) {
                WinTool.createAlert(Alert.AlertType.ERROR, "密码读取错误", "本项目不能正常打开", "");
                runChecker = false;
            }
        } else {
            runChecker = false;    // 上一次关闭的时候没有打开项目，直接跳过
        }
    }

    @Override
    public Boolean entrance() {
        if (runChecker) {    // 如果在调用构造函数的时候就已经发生错误了，就不执行，直接返回"false"
            start(global_stage);
        }
        return runChecker;
    }

    @Override
    public void drawControls(Group group) {
        TextField password_plaintext = WinTool.createTextField(10, 40, 220, 30, 15);
        password_plaintext.setVisible(false);
        PasswordField password = WinTool.createPasswordField(10, 40, 220, 30, 15);

        password_plaintext.textProperty().addListener((observableValue, old_value, new_value) -> password.setText(new_value));
        password.textProperty().addListener((observableValue, old_value, new_value) -> password_plaintext.setText(new_value));

        ImageView view = WinTool.createImageView(230, 40, 30, 30,
                System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png");
        view.setOnMousePressed(mouseEvent -> imageClick(view, password_plaintext, password));

        Button confirm = WinTool.createButton(100, 150, 80, 40, 16, "确定");
        Button cancel = WinTool.createButton(190, 150, 80, 40, 16, "取消");

        confirm.setOnAction(actionEvent -> afterConfirm(password.getText()));
        cancel.setOnAction(actionEvent -> {
            runChecker = false;
            global_stage.close();
        });

        group.getChildren().addAll(
                WinTool.createLabel(10, 10, 390, 30, 17, "输入" + project_name + "的密码："),
                password_plaintext, password, view,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        if (!password.isEmpty()) {
            Group group = new Group();
            Scene scene = new Scene(group);

            drawControls(group);

            stage.setTitle("输入密码");
            stage.setWidth(300);
            stage.setHeight(250);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(windowEvent -> {
                runChecker = false;
                stage.close();
            });
            stage.showAndWait();
        }
    }

    private void afterConfirm(String input) {
        if (input.equals(password)) {
            global_stage.close();
        } else {
            WinTool.createAlert(Alert.AlertType.INFORMATION, "错误", "密码错误", "请重试");
        }
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
}
