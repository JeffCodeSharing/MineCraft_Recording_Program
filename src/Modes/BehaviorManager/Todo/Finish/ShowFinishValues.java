package Modes.BehaviorManager.Todo.Finish;

import Modes.BehaviorManager.Todo.ED.Decryption;
import Modes.BehaviorManager.Todo.List.ShowLists;
import Tools.IOTool;
import Tools.WinTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;

public class ShowFinishValues {
    private final Pane box;
    private final String path;
    private final String listName;
    private final JSONObject data;
    private int y_count;

    /**
     * 构造函数
     * @param box VBox对象，用于展示UI界面
     * @param path 已完成任务列表所在路径
     * @param listName 当前列表名称
     */
    public ShowFinishValues(Pane box, String path, String listName) {
        this.box = box;
        this.path = path;
        this.listName = listName;

        String[] temp = IOTool.readFile(new File(path, listName+".json").getPath());
        if (temp == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取文件错误", "请重新尝试");
            this.data = null;
        } else {
            String jsonString = String.join("", temp);
            JSONObject jsonData = JSONObject.parseObject(jsonString);

            this.data = Decryption.decrypt(jsonData);
        }
    }

    /**
     * 入口方法，开始展示已完成任务的详细内容
     */
    public void entrance() {
        box.getChildren().clear();

        Button return_menu = WinTool.createButton(0, 0, 120, 40, 18, "返回首页");
        return_menu.setOnAction(actionEvent -> {
            ShowLists clazz = new ShowLists(box, new File(new File(path).getParent(), "doing").getPath());
            clazz.entrance();
        });
        box.getChildren().addAll(return_menu);

        if (data != null) {
            Button return_last = WinTool.createButton(120, 0, 120, 40, 20, "返回上级");
            return_last.setOnAction(actionEvent -> {
                ShowFinishList searcher = new ShowFinishList(box, path);
                searcher.entrance();
            });

            box.getChildren().addAll(return_last,
                    WinTool.createLabel(0, 50, -1, 30, 25, listName, Color.BLUE));

            // 遍历任务内容并展示
            this.y_count = 80;
            showData(data.getJSONArray("children"), 0);
        }
    }

    public void showData(JSONArray children, int indent) {
        for (Object childTemp : children) {
            JSONObject child = (JSONObject) childTemp;
            box.getChildren().add(WinTool.createLabel(0, y_count, -1, 30, 25,
                    "\t".repeat(indent) + "· " + child.getString("name"), Color.GREEN));
            y_count += 30;

            showData(child.getJSONArray("children"), ++indent);
        }
    }
}