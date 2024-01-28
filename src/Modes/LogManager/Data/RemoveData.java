package Modes.LogManager.Data;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * 删除数据类。
 */
public class RemoveData extends Application implements AbstractWindow {
    private final VBox box;
    private final Stage global_stage = new Stage();
    private final List<TextField> fields;
    private final List<TextArea> areas;
    private ListView<String> listView;

    /**
     * 使用指定的VBox、TextField列表和TextArea列表构造DeleteData对象。
     *
     * @param box 要显示删除结果的VBox。
     * @param fields 用于存储文本框的列表。
     * @param areas 用于存储文本区域的列表。
     */
    public RemoveData(VBox box, List<TextField> fields, List<TextArea> areas) {
        this.box = box;
        this.fields = fields;
        this.areas = areas;
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return null;
    }

    @Override
    public void drawControls(Group group) {
        listView = WinTool.createListView(10, 50, 400, 350);

        for (int i=0, j=1; i<fields.size(); i++, j++) {
            String insert = "事件点" + j + " " + fields.get(i).getText();
            listView.getItems().add(insert);
        }

        Button delete = WinTool.createButton(280, 420, 60, 30, 15, "删除");
        Button cancel = WinTool.createButton(350, 420, 60, 30, 15, "关闭");
        delete.setOnAction(actionEvent -> delete_item());
        cancel.setOnAction(actionEvent -> global_stage.close());

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 100, 40, 20, "删除项目", Color.BLUE),
                listView,
                delete, cancel
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("删除信息");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * @implNote 这是一个用户确认删除后的扩展代码块
     */
    private void delete_item() {
        int index = listView.getSelectionModel().getSelectedIndex();
        if (!(index == -1)) {
            Optional<ButtonType> type = WinTool.createAlert(Alert.AlertType.CONFIRMATION,
                    "删除事件点", "您是否要删除这一个事件点", "删除后事件点将不复存在，\n删除前请先确保没有在使用本项目");

            if (type.get() == ButtonType.OK) {
                // 更新列表
                fields.remove(index);
                areas.remove(index);

                // 更新vbox
                box.getChildren().remove(index * 6 + 2, index * 6 + 8);
                for (int i = 2, j = 1; i < box.getChildren().size(); i += 6, j++) {
                    Label label = WinTool.createLabel(0, 0, 100, 40, 20, "事件点" + j, Color.BLUE);
                    box.getChildren().set(i, label);
                }
            }

            global_stage.close();
            WinTool.createAlert(Alert.AlertType.INFORMATION, "完成", "已成功删除此事件点！", "");
        }
    }
}
