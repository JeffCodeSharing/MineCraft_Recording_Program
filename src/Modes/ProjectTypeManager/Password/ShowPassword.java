package Modes.ProjectTypeManager.Password;

import Interface.AbstractWindow;
import Tools.ClassTool;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

public class ShowPassword extends Application implements AbstractWindow {
    private final String path;
    private String password;
    private final Stage global_stage = new Stage();

    /**
     * 构造方法，初始化密码，以及确定 project_opening
     *
     * @param path 此时的项目路径
     */
    public ShowPassword(String path) {
        if (path.equals("none")) {
            this.path = "";    // 将path留空
        } else {
            this.path = path;

            // 密码获取
            String[] file_data = IOTool.read_file(path + File.separator + "check_item");
            if (file_data == null) {
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取密码错误", "请重新尝试");
                password = "未填写";
            } else {
                password = file_data[2].equals("") ? "未填写" : EDTool.decrypt(file_data[2]);
            }
        }
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    /**
     * 主页
     *
     * @param group 用于添加控件的 JavaFX Group 对象
     */
    @Override
    public void draw_controls(Group group) {
        group.getChildren().clear();

        Label password_label = WinTool.createLabel(10, 40, 240, 30, 18,
                "密码：" + (password.equals("未填写") ? password : "*".repeat(password.length())));
        ImageView view = WinTool.createImageView(250, 40, 30, 30,
                System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png");
        view.setOnMousePressed(mouseEvent -> image_click(view, password_label));

        Button change = WinTool.createButton(10, 110, 100, 40, 15, "更改密码");
        change.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "ProjectTypeManager" + File.separator +
                    "Password" + File.separator + "SetPassword.class");
            Class<?> setter = tool.get_class("Modes.ProjectTypeManager.Password.SetPassword");
            tool.invoke_method(setter, "entrance",
                    new Class[]{Group.class, String.class}, new Object[]{group, path}, new Class[0], new Object[0]);
        });

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 300, 30, 22, "密码", Color.BLUE),
                password_label, view,
                change
        );
    }

    @Override
    public void start(Stage stage) {
        if (path.equals("")) {    // 如果项目已经打开
            WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "你还没有打开项目", "请打开后重试");
        } else {
            // 密码确认
            ClassTool tool = new ClassTool("ProjectSafe" + File.separator + "CheckPassword.class");
            Class<?> checker = tool.get_class("ProjectSafe.CheckPassword");
            String return_value = ((String[]) tool.invoke_method(checker, "entrance",
                    new Class[]{String.class}, new Object[]{path}, new Class[0], new Object[0]))[0];

            if (return_value.equals("true")) {
                Group group = new Group();
                Scene scene = new Scene(group);

                draw_controls(group);

                stage.setTitle("更改密码");
                stage.setScene(scene);
                stage.setHeight(220);
                stage.setWidth(300);
                stage.setResizable(false);
                stage.showAndWait();
            }
        }
    }

    private void image_click(ImageView view, Label label) {
        if (!password.equals("未填写")) {
            String image_url = view.getImage().getUrl();
            // 下面这一个是show_password.png图标所在的路径，和上面这一个做比较
            String show_image_url = System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png";

            if (image_url.equals(show_image_url)) {     // 密码栏是隐藏状态
                view.setImage(new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "hide_password.png"));
                label.setText("密码：" + password);
            } else {
                view.setImage(new Image(System.getProperty("user.dir") + File.separator + "data" + File.separator + "show_password.png"));
                label.setText("密码：" + "*".repeat(password.length()));
            }
        }
    }
}
