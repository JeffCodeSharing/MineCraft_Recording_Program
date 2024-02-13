package Modes.StationMap;

import Tools.WinTool;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ShowMap {
    public void entrance(Pane box) {
        box.getChildren().clear();
        start(box);
    }

    private void start(Pane box) {
        Canvas map = WinTool.createCanvas(0, 0, 630, 630, Color.WHITE);

        HBox input_box = new HBox();
        input_box.setLayoutX(30);
        input_box.setLayoutY(660);

        TextField x_point = WinTool.createTextField(0, 0, 70, 30, 16);
        TextField z_point = WinTool.createTextField(0, 0, 70, 30, 16);
        input_box.getChildren().addAll(
                WinTool.createLabel(0, 0, 30, 30, 16, "x:"), x_point,
                WinTool.createLabel(0, 0, 30, 30, 16, "z:"), z_point
        );

        box.getChildren().addAll(
                map,
                input_box
        );
    }
}
