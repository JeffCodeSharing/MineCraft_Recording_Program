package Modes.ProjectManager;

import ProjectSafe.CheckPassword;
import Tools.JsonTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * OpenProject 类负责在 ProjectManager 中打开项目。
 */
public class OpenProject {
    private String path = null;

    /**
     * 进入方法，用于打开项目。
     *
     * @return 返回打开项目的路径
     */
    public String entrance() {
        chooseDir();
        return path;
    }

    /**
     * 选择项目目录。
     */
    private void chooseDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择项目");

        try {
            File file = chooser.showDialog(new Stage());
            File check = new File(file.getPath(), "checkItem.json");
            if (check.exists()) {
                JSONObject fileData = JsonTool.readJson(check.getPath());
                if (fileData.getString("CreateTime") == null) {
                    throw new RuntimeException();
                } else {
                    // 密码检查
                    path = file.getPath();

                    CheckPassword checker = new CheckPassword(path);
                    boolean returnValue = checker.entrance();

                    if (returnValue) {
                        WinTool.createAlert(Alert.AlertType.INFORMATION, "选择成功", "已成功切换项目", "切换项目:" + path);
                    } else {
                        path = null;
                    }
                }
            } else {
                throw new RuntimeException();
            }
        } catch (NullPointerException ignored) {
            // 留空
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.WARNING, "错误", "打开项目出现错误", "");
        }
    }
}
