package Tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Optional;

/**
 * WinTool 类用于创建窗口控件。
 * @implNote 此工具类用于创建窗口控件，提供了创建标签、按钮、文本框等常用控件的方法。
 */
public class WinTool {
    /**
     * 创建标签控件。
     *
     * @param x      标签控件的 x 坐标
     * @param y      标签控件的 y 坐标
     * @param width  标签控件的宽度
     * @param height 标签控件的高度
     * @param size   标签控件的字体大小
     * @param data   标签控件的文本内容
     * @return 返回创建的标签控件
     */
    public static Label createLabel(int x, int y, int width, int height, int size, String data) {
        Label label = new Label(data);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setMaxSize(width, height);
        label.setMinSize(width, height);
        label.setFont(Font.font(size));
        return label;
    }

    /**
     * 创建带颜色的标签控件。
     *
     * @param x      标签控件的 x 坐标
     * @param y      标签控件的 y 坐标
     * @param width  标签控件的宽度
     * @param height 标签控件的高度
     * @param size   标签控件的字体大小
     * @param data   标签控件的文本内容
     * @param color  标签控件的文本颜色
     * @return 返回创建的带颜色的标签控件
     */
    public static Label createLabel(int x, int y, int width, int height, int size, String data, Color color) {
        Label label = createLabel(x, y, width, height, size, data);
        label.setTextFill(color);
        return label;
    }

    /**
     * 创建不含宽度定义的标签控件。
     *
     * @param x      标签控件的 x 坐标
     * @param y      标签控件的 y 坐标
     * @param height 标签控件的高度
     * @param size   标签控件的字体大小
     * @param data   标签控件的文本内容
     * @return 返回标签控件
     */
    public static Label createLabelWithNoWidth(int x, int y, int height, int size, String data) {
        Label label = new Label(data);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setMaxHeight(height);
        label.setMinHeight(height);
        label.setFont(Font.font(size));
        return label;
    }

    /**
     * 创建不含宽度定义的标签控件。
     *
     * @param x      标签控件的 x 坐标
     * @param y      标签控件的 y 坐标
     * @param height 标签控件的高度
     * @param size   标签控件的字体大小
     * @param data   标签控件的文本内容
     * @param color  标签控件的颜色
     * @return 返回标签控件
     */
    public static Label createLabelWithNoWidth(int x, int y, int height, int size, String data, Color color) {
        Label label = createLabelWithNoWidth(x, y, height, size, data);
        label.setTextFill(color);
        return label;
    }

    /**
     * 创建按钮控件。
     *
     * @param x      按钮控件的 x 坐标
     * @param y      按钮控件的 y 坐标
     * @param width  按钮控件的宽度
     * @param height 按钮控件的高度
     * @param size   按钮控件的字体大小
     * @param data   按钮控件的文本内容
     * @return 返回创建的按钮控件
     */
    public static Button createButton(int x, int y, int width, int height, int size, String data) {
        Button button = new Button(data);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setMaxSize(width, height);
        button.setMinSize(width, height);
        button.setFont(Font.font(size));
        return button;
    }

    /**
     * 创建文本框控件。
     *
     * @param x      文本框控件的 x 坐标
     * @param y      文本框控件的 y 坐标
     * @param width  文本框控件的宽度
     * @param height 文本框控件的高度
     * @param size   文本框控件的字体大小
     * @return 返回创建的文本框控件
     */
    public static TextField createTextField(int x, int y, int width, int height, int size) {
        TextField textField = new TextField();
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setMaxSize(width, height);
        textField.setMinSize(width, height);
        textField.setFont(new Font(size));
        return textField;
    }

    /**
     * 创建带有默认文本和提示文本的文本框控件。
     *
     * @param x           文本框控件的 x 坐标
     * @param y           文本框控件的 y 坐标
     * @param width       文本框控件的宽度
     * @param height      文本框控件的高度
     * @param size        文本框控件的字体大小
     * @param data        文本框中的默认文本
     * @param prompt_text 文本框中的提示文本，当用户输入任何字符时，提示文本将消失
     * @return 返回创建的文本框控件
     */
    public static TextField createTextField(int x, int y, int width, int height, int size,
                                            String data, String prompt_text) {
        TextField textField = createTextField(x, y, width, height, size);
        textField.setText(data);
        textField.setPromptText(prompt_text);
        return textField;
    }

    public static PasswordField createPasswordField(int x, int y, int width, int height, int size) {
        PasswordField field = new PasswordField();
        field.setLayoutX(x);
        field.setLayoutY(y);
        field.setMaxSize(width, height);
        field.setMinSize(width, height);
        field.setFont(Font.font(size));
        return field;
    }

    /**
     * 创建列表控件。
     *
     * @param x      列表控件的 x 坐标
     * @param y      列表控件的 y 坐标
     * @param width  列表控件的宽度
     * @param height 列表控件的高度
     * @param args   列表控件中的所有项目
     * @return 返回创建的列表控件
     */
    public static ListView<String> createListView(int x, int y, int width, int height, String... args) {
        ListView<String> listView = new ListView<>();
        listView.setLayoutX(x);
        listView.setLayoutY(y);
        listView.setMaxSize(width, height);
        listView.setMinSize(width, height);
        listView.setEditable(false);
        listView.getItems().addAll(args);
        return listView;
    }

    /**
     * 创建文本域控件。
     *
     * @param x      文本域控件的 x 坐标
     * @param y      文本域控件的 y 坐标
     * @param width  文本域控件的宽度
     * @param height 文本域控件的高度
     * @param size   文本域控件的字体大小
     * @param warp   文本域控件是否自动换行
     * @param text   文本域控件的文本内容
     * @return 返回设置完所有属性的文本域控件
     */
    public static TextArea createTextArea(int x, int y, int width, int height, int size, boolean warp, String text) {
        TextArea textArea = new TextArea(text);
        textArea.setLayoutX(x);
        textArea.setLayoutY(y);
        textArea.setMaxSize(width, height);
        textArea.setMinSize(width, height);
        textArea.setFont(Font.font(size));
        textArea.setWrapText(warp);
        return textArea;
    }

    /**
     * 创建下拉列表控件。
     *
     * @param x         下拉列表控件的 x 坐标
     * @param y         下拉列表控件的 y 坐标
     * @param width     下拉列表控件的宽度
     * @param height    下拉列表控件的高度
     * @param editable  是否可以编辑下拉列表的选项
     * @param default_str 默认显示的选项
     * @param values    下拉列表中的选项
     * @return 返回设置完所有属性的下拉列表控件
     */
    public static ComboBox<String> createComboBox(int x, int y, int width, int height, boolean editable,
                                                  String default_str, String... values) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLayoutX(x);
        comboBox.setLayoutY(y);
        comboBox.setMaxSize(width, height);
        comboBox.setMinSize(width, height);
        comboBox.setEditable(editable);
        comboBox.getSelectionModel().select(default_str);
        comboBox.getItems().addAll(values);
        return comboBox;
    }

    /**
     * 创建带有滚动条的面板控件。
     *
     * @param x            面板控件的 x 坐标
     * @param y            面板控件的 y 坐标
     * @param width        面板控件的宽度
     * @param height       面板控件的高度
     * @param content_box  面板控件的内部内容
     * @return 返回设置完所有属性的带有滚动条的面板控件
     */
    public static ScrollPane createScrollPane(int x, int y, int width, int height, Pane content_box) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setLayoutX(x);
        scrollPane.setLayoutY(y);
        scrollPane.setMaxSize(width, height);
        scrollPane.setMinSize(width, height);
        scrollPane.setContent(content_box);
        return scrollPane;
    }

    /**
     * 创建图像视图控件。
     *
     * @param path 图像文件的路径
     * @return 返回创建的图像视图控件，如果路径无效或发生异常，则返回 null
     */
    public static ImageView createImageView(String path) {
        try {
            Image image = new Image(path);
            return new ImageView(image);
        } catch (Exception e) {
            return null;
        }
    }

    public static ImageView createImageView(int x, int y, int width, int height, String path) {
        ImageView view = createImageView(path);
        if (path == null) {
            return null;
        } else {
            if (view != null) {
                view.setX(x);
                view.setY(y);
                view.setFitWidth(width);
                view.setFitHeight(height);
            }
            return view;
        }
    }

    /**
     * 创建复选框控件。
     *
     * @param x      复选框控件的 x 坐标
     * @param y      复选框控件的 y 坐标
     * @param width  复选框控件的宽度
     * @param height 复选框控件的高度
     * @param size   复选框控件的字体大小
     * @param data   复选框控件的文本内容
     * @return 返回创建的复选框控件
     */
    public static CheckBox createCheckBox(int x, int y, int width, int height, int size, String data) {
        CheckBox checkBox = new CheckBox(data);
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setMaxSize(width, height);
        checkBox.setMinSize(width, height);
        checkBox.setFont(Font.font(size));
        return checkBox;
    }

    /**
     * 创建警告、错误、信息等提示对话框。
     *
     * @param alert_type 弹窗类型，如 Error、Warning、Information
     * @param title      弹窗的标题
     * @param text1      弹窗的主文本
     * @param text2      弹窗的辅助文本
     * @return 返回用户点击的按钮
     * @implNote 用于创建用于向用户展示信息的对话框，如信息提示、警告或错误提示
     */
    public static Optional<ButtonType> createAlert(Alert.AlertType alert_type, String title, String text1, String text2) {
        Alert alert = new Alert(alert_type);
        alert.setTitle(title);
        alert.setHeaderText(text1);
        alert.setContentText(text2);
        return alert.showAndWait();
    }

    public static void createAlertWithNoWait(Alert.AlertType alert_type, String title, String text1, String text2) {
        Alert alert = new Alert(alert_type);
        alert.setTitle(title);
        alert.setHeaderText(text1);
        alert.setContentText(text2);
        alert.show();
    }

    public static Canvas createCanvas(int x, int y, int width, int height) {
        Canvas canvas = new Canvas();
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        canvas.setWidth(width);
        canvas.setHeight(height);
        return canvas;
    }

    public static Canvas createCanvas(int x, int y, int width, int height, Color back_color) {
        Canvas canvas = createCanvas(x, y, width, height);
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(back_color);
        context.fillRect(x, y, width, height);
        return canvas;
    }
}

