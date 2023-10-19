package Update;

import Tools.WinTool;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class UpdateManager implements Runnable {
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
                        Platform.runLater(() ->
                                WinTool.createAlertWithNoWait(Alert.AlertType.INFORMATION, "升级", "有新版本升级", "将在5秒后升级"));

                        // 更新线程休眠五秒
                        try {
                            Thread.sleep(5000);
                        } catch (Exception ignored) {}

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
                Platform.runLater(() ->
                        WinTool.createAlertWithNoWait(Alert.AlertType.INFORMATION, "更新完成", "请重启程序", "5秒后程序自动关闭"));

                try {
                    Thread.sleep(5000);
                } catch (Exception ignored) {}

                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() ->
                    WinTool.createAlertWithNoWait(Alert.AlertType.ERROR, "错误", "在更新中发生错误", "如果不影响使用，请忽略"));
        }
    }
}
