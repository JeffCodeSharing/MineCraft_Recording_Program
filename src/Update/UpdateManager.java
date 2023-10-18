package Update;

import Tools.WinTool;
import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UpdateManager implements Runnable {
    private final Stage stage;

    public UpdateManager(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void run() {
        try {
            boolean has_been_updated = false;
            boolean sent_alert = true;
            while (true) {
                UpdateChecker checker = new UpdateChecker();
                boolean can_update = checker.check();

                if (can_update) {
                    if (sent_alert) {
                        PlatformImpl.runAndWait(() ->
                                WinTool.createAlert(Alert.AlertType.INFORMATION, "升级", "有新版本升级", "请升级"));
                        sent_alert = false;
                    }

                    Updater updater = new Updater(checker.getUpdateVersion(), checker.getRootPath());
                    boolean update_success = updater.update();

                    if (!update_success) {
                        break;
                    } else {
                        has_been_updated = true;
                    }
                } else {
                    break;
                }
            }

            if (has_been_updated) {
                PlatformImpl.runAndWait(() ->
                        WinTool.createAlert(Alert.AlertType.INFORMATION, "更新完成", "请重启程序", "程序自动关闭"));

                System.exit(0);
            }
        } catch (Exception e) {
            PlatformImpl.runAndWait(() ->
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "在更新中发生错误", "如果不影响使用，请忽略"));
        }
    }
}
