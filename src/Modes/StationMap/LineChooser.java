package Modes.StationMap;

import Interface.AbstractWindow;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LineChooser extends Application implements AbstractWindow {
    private static String choose_line = null;
    private final Stage global_stage = new Stage();
    private final String[] lines;

    public LineChooser(List<LineData> data) {
        List<String> linesTemp = new ArrayList<>();
        for (LineData dataPiece:data) {
            linesTemp.add(dataPiece.getLineName());
        }
        this.lines = linesTemp.toArray(new String[0]);
    }

    @Override
    public String[] entrance() {
        start(global_stage);
        return new String[]{choose_line};
    }

    @Override
    public void drawControls(Group group) {
        ListView<String> lines_list = WinTool.createListView(10, 40, 230, 250, lines);
        Button confirm = WinTool.createButton(160, 310, 80, 30, 16, "确定");

        confirm.setOnAction(actionEvent -> {
            String select_item = lines_list.getSelectionModel().getSelectedItem();
            if ((select_item != null) && (!select_item.equals(""))) {
                choose_line = select_item;
                global_stage.close();
            }
        });

        group.getChildren().addAll(
                WinTool.createLabel(0, 0, 200, 35, 30, "选择线路", Color.BLUE),
                lines_list, confirm
        );
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("选择线路");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(280);
        stage.setHeight(400);
        stage.showAndWait();
    }
}
