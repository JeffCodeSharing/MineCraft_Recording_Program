package Modes.LogManager.Date;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class CreateDate extends Application implements AbstractWindow<Void> {
    private String date = "";
    private final Stage global_stage = new Stage();
    private final String path;
    private final ListView<String> listView;
    private final String year_value;
    private final String month_value;

    public CreateDate(String path, ListView<String> listView, String year_value, String month_value) {
        this.path = path;
        this.listView = listView;
        this.year_value = year_value;
        this.month_value = month_value;
    }

    @Override
    public Void entrance() {
        start(global_stage);
        createDate();
        return null;
    }

    @Override
    public void drawControls(Group group) {
        TextField year_field = WinTool.createTextField(50, 20, 120, 30, 15);
        TextField month_field = WinTool.createTextField(50, 70, 120, 30, 15);
        TextField day_field = WinTool.createTextField(50, 120, 120, 30, 15);

        Button today = WinTool.createButton(120, 160, 60, 30, 15, "今日");
        today.setOnAction(actionEvent -> {
            LocalDate currentDate = LocalDate.now();

            year_field.setText(String.valueOf(currentDate.getYear()));
            day_field.setText(String.valueOf(currentDate.getDayOfMonth()));
            month_field.setText(String.valueOf(currentDate.getMonthValue()));
        });

        Button confirm = WinTool.createButton(120, 200, 60, 35, 15, "确定");
        Button cancel = WinTool.createButton(200, 200, 60, 35, 15, "取消");

        confirm.setOnAction(actionEvent -> {
            if (checkDate(year_field, month_field, day_field)) {
                global_stage.close();
            }
        });
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(20, 20, 30, 30, 15, "年："), year_field,
                WinTool.createLabel(20, 70, 30, 30, 15, "月："), month_field,
                WinTool.createLabel(20, 120, 30, 30, 15, "日："), day_field,
                today,
                confirm, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("创建日志");
        stage.setWidth(300);
        stage.setHeight(300);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(windowEvent -> {
            date = "";
            stage.close();
        });
        stage.showAndWait();
    }

    /**
     * 检查用户输入的日期是否有效。
     *
     * @param year  年份的 TextField
     * @param month 月份的 TextField
     * @param day   日的 TextField
     * @return 返回窗口是否应该关闭的检查项
     */
    private boolean checkDate(TextField year, TextField month, TextField day) {
        try {
            int year_int = Integer.parseInt(year.getText());
            int month_int = Integer.parseInt(month.getText());
            int day_int = Integer.parseInt(day.getText());

            Integer[] day_of_months = DaysOfMonths(Integer.parseInt(year.getText()));

            LocalDate currentDate = LocalDate.now();
            int[] date_int = {
                    currentDate.getYear(),
                    currentDate.getMonthValue(),
                    currentDate.getDayOfMonth()
            };

            // 检查
            if (year_int > date_int[0] || year_int < 1) {
                WinTool.createAlert(Alert.AlertType.WARNING, "提示", "无法创建日志于：" + year_int + "年", "");
                return false;
            } else if (year_int == date_int[0]) {
                if (month_int > date_int[1] || month_int < 1) {
                    WinTool.createAlert(Alert.AlertType.WARNING, "提示", "无法创建日志于：" + month_int + "月", "");
                    return false;
                } else if (month_int == date_int[1]) {
                    if (day_int > date_int[2] || day_int < 1) {
                        WinTool.createAlert(Alert.AlertType.WARNING, "提示", "无法创建日志于：" + day_int + "日", "");
                        return false;
                    }
                } else {
                    int day_of_month = day_of_months[month_int-1];
                    if (day_int > day_of_month || day_int < 1) {
                        WinTool.createAlert(Alert.AlertType.WARNING, "提示", "无法创建日志于：" + day_int + "日", "");
                        return false;
                    }
                }
            } else {
                if (month_int > 12 || month_int < 1) {
                    WinTool.createAlert(Alert.AlertType.WARNING, "提示", "无法创建日志于：" + month_int + "月", "");
                    return false;
                } else {
                    int day_of_month = day_of_months[month_int-1];
                    if (day_int > day_of_month || day_int < 1) {
                        WinTool.createAlert(Alert.AlertType.WARNING, "提示", "无法创建日志于：" + day_int + "日", "");
                        return false;
                    }
                }
            }

            // 如果用户输入的day是一位数的，将day扩充到两位数
            date = year.getText() + "-" + month.getText() + (day.getText().length() == 1 ? "-0" : "-") + day.getText();
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.WARNING, "提示", "输入不完整", "请修改");
            return false;
        }
        return true;
    }

    /**
     * 根据年份返回每个月的天数。
     *
     * @param year 年份
     * @return 返回每个月的天数的数组，例如：index 0 -> 31 (一月)
     */
    private Integer[] DaysOfMonths(int year) {
        int second_month = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
        return new Integer[]{31, second_month, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};    // 每个月份的天数
    }

    /**
     * 创建日期并生成日志
     */
    private void createDate() {
        if (!date.equals("")) {
            File create_file = new File(path + File.separator + date);
            File create_data = new File(create_file.getPath() + File.separator+ "simple_data");
            if (!create_file.exists()) {
                try {
                    if (create_file.mkdir()) {
                        if (!create_data.createNewFile()) {
                            throw new RuntimeException();
                        }

                        // 刷新listView
                        ShowDate showDate = new ShowDate(listView, true);
                        showDate.entrance(path, year_value, month_value);

                        WinTool.createAlert(Alert.AlertType.INFORMATION, "成功", "创建日志成功！",
                                "创建日志于：" + date);
                    } else {
                        throw new RuntimeException();
                    }
                } catch (Exception e) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "生成失败", "创建日志失败");
                }
            } else {
                WinTool.createAlert(Alert.AlertType.WARNING, "提示", "生成失败", "日志已存在");
            }
        }
    }
}
