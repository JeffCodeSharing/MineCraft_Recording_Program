package Modes.LogManager.Date;

import Tools.WinTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 搜索日期
 */
public class ShowDate {
    private final ListView<String> listView;
    private final boolean isUpdate;

    /**
     * 构造函数
     * @param listView 日志列表
     * @param isUpdate 是否更新
     */
    public ShowDate(ListView<String> listView, boolean isUpdate) {
        this.listView = listView;
        this.isUpdate = isUpdate;
    }

    /**
     * 进入搜索日期
     * @param path 日志路径
     * @param yearValue 年份
     * @param monthValue 月份
     */
    public void entrance(String path, String yearValue, String monthValue) {
        if ((yearValue == null || monthValue == null) && !isUpdate) {
            WinTool.createAlert(Alert.AlertType.WARNING, "提示", "年或月没有输入", "");
        } else if ((yearValue.equals("") || monthValue.equals("")) && !isUpdate) {
            WinTool.createAlert(Alert.AlertType.WARNING, "提示", "年或月没有输入", "");
        } else {
            try {
                int year = Integer.parseInt(yearValue);
                int month = Integer.parseInt(monthValue);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
                int current_year = Integer.parseInt(formatter.format(new Date()));
                if (year > current_year || year < 1 || month > 12 || month < 1) {
                    throw new RuntimeException();
                }

                // 更新列表
                String[] listInsert = get_month_file(year, month, path);

                listView.getItems().clear();
                listView.getItems().addAll(listInsert);
            } catch (Exception e) {
                if (!isUpdate) {
                    WinTool.createAlert(Alert.AlertType.ERROR, "错误", "查询数据错误", "请重新输入");
                }
            }
        }
    }

    /**
     * 获取月份的文件
     * @param year 年份
     * @param month 月份
     * @param path 日志路径
     * @return 文件列表
     */
    private String[] get_month_file(int year, int month, String path) {
        File file = new File(path);
        if (file.exists()) {
            List<String> returnList = new ArrayList<>(List.of(file.list()));
            String headType = year + "-" + month + "-";

            for (int i = returnList.size() - 1; i >= 0; i--) { // 从末尾进行删减，避免了删除时出现因为索引值变化而漏删的情况
                String temp = returnList.get(i);
                if (!temp.startsWith(headType)) {
                    returnList.remove(i);
                }
            }

            return returnList.toArray(new String[0]);
        } else {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "项目读取错误", "路径错误！");
            return null;
        }
    }
}
