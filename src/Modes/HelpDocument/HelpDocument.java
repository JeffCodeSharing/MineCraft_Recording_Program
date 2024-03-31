package Modes.HelpDocument;

import Tools.WinTool;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.File;

public class HelpDocument {
    public static void entrance() {
        File helpFile = new File(
                System.getProperty("user.dir") + File.separator +
                        "data" + File.separator + "HelpDocument" + File.separator + "help.html"
                );

        try {
            Desktop.getDesktop().browse(helpFile.toURI());
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "不能打开帮助文档", "请重新尝试");
        }
    }
}
