package Modes.ProjectManager;

import Interface.AbstractWindow;
import Tools.EDTool;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CreateProject 类负责在 ProjectManager 中创建项目。
 */
public class CreateProject extends Application implements AbstractWindow {
    private final Stage global_stage = new Stage();
    private final JSONObject writeData = new JSONObject();
    private String path = null;

    @Override
    public String[] entrance() {
        start(global_stage);
        return new String[]{path};
    }

    @Override
    public void drawControls(Group group) {
        // 其他可选项放在最前面，用于预加载
        TextField seed_in = WinTool.createTextField(90, 245, 200, 30, 15);
        TextField password_in = WinTool.createTextField(90, 285, 200, 30, 15);

        // 文件路径和确认
        Label warning = WinTool.createLabel(100, 50, 400, 30, 15, "", Color.ORANGE);
        Label create_path = WinTool.createLabel(100, 165, 400, 30, 15, "");
        TextField project_name = WinTool.createTextField(160, 75, 200, 30, 15, "untitled", "");
        TextField project_path = WinTool.createTextField(160, 135, 390, 30, 15,
                "C:" + File.separator + "Users" + File.separator
                        + System.getenv("USERNAME") + File.separator + "Desktop" + File.separator, "");

        project_name.textProperty().addListener((observableValue, aBoolean, t1) -> updateLabels(warning, create_path, project_path, project_name));
        project_path.textProperty().addListener((observableValue, aBoolean, t1) -> updateLabels(warning, create_path, project_path, project_name));

        Button search_dir = WinTool.createButton(550, 135, 30, 30, 12, "...");
        search_dir.setOnAction(actionEvent -> chooseDirectory(project_path));
        Button create_button = WinTool.createButton(380, 480, 80, 40, 15, "创建");
        create_button.setOnAction(actionEvent -> {
            if (createProject(create_path.getText()) &&
                    otherEventWriteIn(seed_in, password_in) &&
                    writeData()) {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "创建成功", "项目创建成功！", "项目路径：" + path);
            } else {
                WinTool.createAlert(Alert.AlertType.WARNING, "警告", "该项目已存在或路径不正确！", "");
            }
        });
        Button cancel_button = WinTool.createButton(480, 480, 80, 40, 15, "取消");
        cancel_button.setOnAction(actionEvent -> global_stage.close());

        updateLabels(warning, create_path, project_path, project_name);

        // 添加控件
        group.getChildren().addAll(
                warning,
                WinTool.createLabel(80, 80, 80, 20, 15, "项目名称："), project_name,
                WinTool.createLabel(80, 140, 80, 20, 15, "项目路径："), create_path,
                project_path, search_dir,

                WinTool.createLabel(10, 210, 100, 35, 20, "其他可选项", Color.BLUE),
                WinTool.createLabel(200, 210, 200, 30, 16, "输入的空格会在记录时消除", Color.ORANGE),
                WinTool.createLabel(30, 245, 80, 30, 15, "种子号："), seed_in,
                WinTool.createLabel(30, 285, 80, 30, 15, "密码："), password_in,
                create_button, cancel_button
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        // 绘制控件
        drawControls(group);

        stage.setTitle("创建项目");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.showAndWait();
    }

    /**
     * 用户选择目录后更新 textField 的文本
     *
     * @param textField 需要更新的 textField
     */
    private void chooseDirectory(TextField textField) {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            File file = chooser.showDialog(new Stage());
            textField.setText(file.getPath());
        } catch (Exception ignored) {
        }
    }

    /**
     * 创建项目的目录
     *
     * @return 如果项目创建成功则返回 true，如果项目已存在则返回 false
     */
    private boolean createProject(String create_path) {
        File project_dir = new File(create_path);

        if (project_dir.exists()) {
            return false;
        }

        try {
            if (!project_dir.mkdirs()) {
                return false;
            }

            File check_file = new File(create_path, "checkItem.json");
            if (!check_file.createNewFile()) {
                return false;
            }

            // 注意CreateTime是大写的Create
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            writeData.put("CreateTime", EDTool.encrypt(formatter.format(new Date())));

            File position_dir = new File(create_path, "positions");
            if (!position_dir.mkdirs()) {
                return false;
            }

            File behavior_dir = new File(create_path, "behavior");
            File todo_finish_dir = new File(behavior_dir, "finish");
            File todo_doing_dir = new File(behavior_dir, "doing");
            File now_doing_file = new File(behavior_dir, "now_doing.json");
            File map_dir = new File(create_path, "map");
            File map_file = new File(map_dir.getPath(), "map_data");
            File backup_dir = new File(create_path, "backup");
            if (!behavior_dir.mkdirs() || !todo_finish_dir.mkdirs() || !todo_doing_dir.mkdirs() || !map_dir.mkdirs() || !backup_dir.mkdirs()
                || !now_doing_file.createNewFile() || !map_file.createNewFile()) {
                return false;
            }

            JSONObject nowDoingData = new JSONObject();
            nowDoingData.put("data", new JSONArray());
            IOTool.overrideFile(now_doing_file.getPath(), new String[]{nowDoingData.toJSONString()});

            path = project_dir.getPath();     // 将path所记录的路径进行规范化
            global_stage.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据 textField 的文本更新 label 的数据
     *
     * @param warning       用于显示警告的 label
     * @param create_path   用于显示项目路径的 label
     * @param project_path  用于输入项目路径的 textField
     * @param project_name  用于输入项目名称的 textField
     */
    private void updateLabels(Label warning, Label create_path, TextField project_path, TextField project_name) {
        File file = new File(project_path.getText(), project_name.getText());
        warning.setText(file.exists() ? "文件夹已存在" : "");
        create_path.setText(file.getPath());
    }

    /**
     * 将其他可选项写入项目文件
     *
     * @param textFields 要写入项目文件的 textField
     * @return 如果写入成功则返回 true，否则返回 false
     */
    private boolean otherEventWriteIn(TextField... textFields) {
        try {
            // index 0 -> 种子号
            String seed = textFields[0].getText();
            seed = (seed == null) ? "" : seed.replace(" ", "");
            writeData.put("seed", EDTool.encrypt(seed));
            writeData.put("seedCanChange", false);

            // index 1 -> 密码
            String password = textFields[1].getText();
            if (password == null) {
                writeData.put("password", null);
            } else {
                password = password.replace(" ", "");
                writeData.put("password", EDTool.encrypt(password));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean writeData() {
        try {
            return IOTool.overrideFile(new File(path, "checkItem.json").getPath(), new String[]{writeData.toJSONString()});
        } catch (Exception e) {
            return false;
        }
    }
}
