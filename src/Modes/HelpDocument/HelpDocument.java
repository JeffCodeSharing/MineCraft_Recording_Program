package Modes.HelpDocument;

import Interface.AbstractWindow;
import Tools.IOTool;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;

/**
 * HelpDocument类实现了AbstractWindow接口和Application类，用于展示帮助文档的窗口。
 */
public class HelpDocument extends Application implements AbstractWindow {
    private final VBox pane_box = new VBox();
    private String search_path;

    /**
     * HelpDocument类的构造函数，初始化 search_path
     */
    public HelpDocument() {
        search_path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "HelpDocument";
    }

    @Override
    public String[] entrance() {
        start(new Stage());
        return null;
    }

    @Override
    public void drawControls(Group group) {
        ScrollPane scrollPane = WinTool.createScrollPane(0, 0, 580, 560, pane_box);
        group.getChildren().add(scrollPane);

        showDetails(true);
    }

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group);

        drawControls(group);

        stage.setTitle("帮助文档");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.showAndWait();
    }

    private void showDetails(boolean is_menu) {
        // 绘制Label
        String[] list = new File(search_path).list();
        pane_box.getChildren().clear();

        // 返回按钮
        if (!is_menu) {
            HBox hBox = new HBox();
            Button return_menu = WinTool.createButton(0, 0, 100, 30, 15, "返回首页");
            return_menu.setOnAction(actionEvent -> {
                search_path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "HelpDocument";
                showDetails(true);
            });

            Button go_back = WinTool.createButton(0, 0, 100, 30, 15, "返回上一层");
            go_back.setOnAction(actionEvent -> {
                while (!search_path.endsWith(File.separator)) {
                    search_path = search_path.substring(0, search_path.length()-1);
                }

                showDetails(search_path.equals(System.getProperty("user.dir")
                        + File.separator + "data" + File.separator + "HelpDocument" + File.separator));   // 是否回到首页
            });

            hBox.getChildren().addAll(return_menu, go_back);
            pane_box.getChildren().add(hBox);
        }

        for (String s:list) {
            // 若是图片，就不加载进来
            if (!s.endsWith(".png") && !s.endsWith(".jpg") && !s.endsWith(".jpeg")) {
                Label label = createLabel("> " + s, Color.BLUE);
                label.setUnderline(true);
                label.hoverProperty().addListener((observableValue, old_value, new_value) ->
                        label.setTextFill(new_value ? Color.PURPLE : Color.BLUE));
                label.setOnMousePressed(mouseEvent -> openNextLayer(s));
                pane_box.getChildren().addAll(label);
            }
        }
    }

    private void showUnderlyingData() {
        try {
            String[] file_value = IOTool.readFile(search_path);
            file_value = (file_value == null) ? new String[0] : file_value;    // 防止返回null

            pane_box.getChildren().clear();

            // 返回按钮
            HBox hBox = new HBox();
            Button return_menu = WinTool.createButton(0, 0, 100, 30, 15, "返回首页");
            return_menu.setOnAction(actionEvent -> {
                search_path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "HelpDocument";
                showDetails(true);
            });

            Button go_back = WinTool.createButton(0, 0, 100, 30, 15, "返回上一层");
            go_back.setOnAction(actionEvent -> {
                while (!search_path.endsWith(File.separator)) {
                    search_path = search_path.substring(0, search_path.length()-1);
                }

                showDetails(search_path.equals(System.getProperty("user.dir")
                        + File.separator + "data" + File.separator + "HelpDocument" + File.separator));   // 是否回到首页
            });

            hBox.getChildren().addAll(return_menu, go_back);
            pane_box.getChildren().add(hBox);

            for (String s:file_value) {
                // 判断变量s代表的是图片还是纯字符串
                if (s.startsWith("image:///")) {
                    String path = s.substring(9);
                    // 绝对路径和相对路径不同加载方式
                    try {
                        ImageView view = WinTool.createImageView(path);
                        pane_box.getChildren().add(view);
                    } catch (Exception e) {
                        ImageView view = WinTool.createImageView(System.getProperty("user.dir") + File.separator + path);
                        pane_box.getChildren().add(view);
                    }
                } else {
                    Label label = createLabel(s, Color.BLACK);
                    pane_box.getChildren().add(label);
                }
            }
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "检索出现错误", "请重新尝试或检查");
        }
    }

    private void openNextLayer(String layer_name) {
        search_path = search_path + File.separator + layer_name;

        // 判断是文件还是目录
        if (new File(search_path).isFile()) {
            showUnderlyingData();
        } else {
            showDetails(false);
        }
    }

    /**
     * 创建Label并设置相关属性。
     *
     * @param value Label的文本值
     * @param color Label的文本颜色
     * @return 创建的Label对象
     */
    private Label createLabel(String value, Color color) {
        Label label = new Label(value);
        label.setFont(Font.font(25));
        label.setTextFill(color);
        return label;
    }
}
