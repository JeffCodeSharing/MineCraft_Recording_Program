import Tools.ClassTool;
import Tools.IOTool;
import Tools.WinTool;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 主程序入口，为Minecraft玩家提供信息记录的辅助功能。
 */
public class Console extends Application {
    private static String path;
    private String type;
    private Label project_name;
    private VBox pane_box;
    private ScrollPane scrollPane;

    /**
     * 构造函数，初始化系统数据和项目路径，并创建临时文件夹
     */
    public Console() {
        String[] system_data = IOTool.read_file(System.getProperty("user.dir") + File.separator +
                "data" + File.separator + "last_usage_information");
        if (system_data == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取系统文件错误", "");
            path = "none";
            type = "日志";
        } else {
            path = system_data[0];
            type = system_data[1];
        }
    }

    /**
     * 启动JavaFX应用程序，创建主舞台
     */
    @Override
    public void start(Stage stage) {
        // 密码确认
        ClassTool tool = new ClassTool("ProjectSafe" + File.separator + "CheckPassword.class");
        Class<?> checker = tool.get_class("ProjectSafe.CheckPassword");
        String return_value = ((String[]) tool.invoke_method(checker, "entrance",
                new Class[]{String.class}, new Object[]{path}, new Class[0], new Object[0]))[0];

        if (return_value.equals("false")) {
            // 所有的东西进行归零
            path = "none";
            type = "日志";
        } else {
            create_temp_directory();
        }

        Group group = new Group();
        Scene scene = new Scene(group);

        // 绘制控件
        draw_controls(group);
        if (!path.equals("none")) {
            File check = new File(path + File.separator + "check_item");
            try {
                if (check.exists()) {
                    Scanner sc = new Scanner(check);
                    String check_temp = sc.nextLine();
                    if (!check_temp.startsWith("Create Time:")) {
                        throw new RuntimeException();
                    } else {
                        update_project_name();
                    }
                    sc.close();
                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                path = "none";
                WinTool.createAlert(Alert.AlertType.ERROR, "错误", "上一次打开的项目不存在或损坏", "");
            }
        }

        stage.setTitle("MineCraft数据管理器");
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.setOnCloseRequest(windowEvent -> {
            boolean exit_value = IOTool.override_file(System.getProperty("user.dir") + File.separator +
                    "data" + File.separator + "last_usage_information", new String[]{path, type});
            System.exit(exit_value ? 0 : 1);
        });
        stage.show();
    }

    /**
     * 绘制控件和菜单
     */
    public void draw_controls(Group group) {
        pane_box = new VBox();
        scrollPane = WinTool.createScrollPane(150, 30, 630, 720, pane_box);

        project_name = WinTool.createLabel(0, 30, 150, 40, 14, "项目名:");

        ComboBox<String> typeBox = WinTool.createComboBox(0, 70, 150, 25, false,
                type, "日志", "坐标", "计划表及正在做");
        typeBox.valueProperty().addListener((observableValue, strings, t1) -> {
            type = typeBox.getSelectionModel().getSelectedItem();
            update_type(group, false);
        });


        MenuBar menuBar = new MenuBar();
        menuBar.setMaxSize(800, 30);
        menuBar.setMinSize(800, 30);

        // 文件菜单
        Menu file = new Menu("文件");

        MenuItem createProject = new MenuItem("创建项目");
        createProject.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool(
                    "Modes" + File.separator + "ProjectManager" + File.separator + "CreateProject.class");
            Class<?> clazz = tool.get_class("Modes.ProjectManager.CreateProject");
            String return_path = ((String[]) tool.invoke_method(clazz, "entrance", new Class[0], new Object[0]))[0];
            if (!return_path.equals("")) {
                path = return_path;
                update_project_name();

                pane_box.getChildren().clear();
            }
        });

        MenuItem openProject = new MenuItem("打开项目");
        openProject.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool(
                    "Modes" + File.separator + "ProjectManager" + File.separator + "OpenProject.class");
            Class<?> clazz = tool.get_class("Modes.ProjectManager.OpenProject");
            String return_path = ((String[]) tool.invoke_method(clazz, "entrance", new Class[0], new Object[0]))[0];
            if (!return_path.equals("")) {
                path = return_path;
                update_project_name();

                update_type(group, false);
            }
        });

        MenuItem removeProject = new MenuItem("删除项目");
        removeProject.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool(
                    "Modes" + File.separator + "ProjectManager" + File.separator + "RemoveProject.class");
            Class<?> clazz = tool.get_class("Modes.ProjectManager.RemoveProject");
            String return_value = (String) tool.invoke_method(clazz, "entrance",
                    new Class[]{String.class}, new Object[]{path});

            // 删除项目后重置路径并清空界面
            if (return_value.equals("TRUE")) {
                path = "none";
                project_name.setText("项目名:");
                pane_box.getChildren().clear();
            }
        });

        MenuItem saveAsProject = new MenuItem("另存为");
        saveAsProject.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool(
                    "Modes" + File.separator + "ProjectManager" + File.separator + "SaveAsProject.class");
            Class<?> clazz = tool.get_class("Modes.ProjectManager.SaveAsProject");
            tool.invoke_method(clazz, "entrance",
                    new Class[]{String.class}, new Object[]{path});
        });

        file.getItems().addAll(createProject, openProject, new SeparatorMenuItem(),
                removeProject, saveAsProject
        );

        // 项目信息菜单
        Menu project_type = new Menu("项目信息");

        MenuItem seed = new MenuItem("种子");
        seed.setOnAction(actionEvent -> {
            if (!path.equals("none")) {
                ClassTool tool = new ClassTool(
                        "Modes" + File.separator + "ProjectTypeManager" + File.separator +
                                "Seed" + File.separator + "ShowSeed.class");
                Class<?> clazz = tool.get_class("Modes.ProjectTypeManager.Seed.ShowSeed");
                tool.invoke_method(clazz, "entrance",
                        new Class[]{String.class}, new Object[]{path + File.separator + "check_item"},
                        new Class[0], new Object[0]);
            } else {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "还没有打开或创建项目", "请打开或创建项目后重试");
            }
        });

        MenuItem password = new MenuItem("密码");
        password.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool(
                    "Modes" + File.separator + "ProjectTypeManager" + File.separator +
                            "Password" + File.separator + "ShowPassword.class");
            Class<?> clazz = tool.get_class("Modes.ProjectTypeManager.Password.ShowPassword");
            tool.invoke_method(clazz, "entrance",
                    new Class[]{String.class}, new Object[]{path}, new Class[0], new Object[0]);
        });

        MenuItem game_backup = new MenuItem("游戏备份");
        game_backup.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool("Modes" + File.separator + "ProjectTypeManager" + File.separator +
                    "GameBackup" + File.separator + "ShowBackup.class");
            Class<?> clazz = tool.get_class("Modes.ProjectTypeManager.GameBackup.ShowBackup");
            tool.invoke_method(clazz, "entrance",
                    new Class[]{String.class}, new Object[]{path}, new Class[0], new Object[0]);
        });

        project_type.getItems().addAll(seed, password, game_backup);

        // 帮助菜单
        Menu help = new Menu("帮助");

        MenuItem help_document = new MenuItem("帮助文档");
        help_document.setOnAction(actionEvent -> {
            ClassTool tool = new ClassTool(
                    "Modes" + File.separator + "HelpDocument" + File.separator + "HelpDocument.class");
            Class<?> clazz = tool.get_class("Modes.HelpDocument.HelpDocument");
            tool.invoke_method(clazz, "entrance", new Class[0], new Object[0]);
        });

        help.getItems().addAll(help_document);

        menuBar.getMenus().addAll(file, project_type, help);
        group.getChildren().addAll(menuBar, project_name, typeBox, scrollPane);

        update_type(group, true);
    }

    /**
     * 更新项目名标签
     */
    private void update_project_name() {
        String[] temp = path.replace(File.separator, "/").split("/");
        String add_str = "项目名:" + temp[temp.length-1];
        project_name.setText(add_str);
    }

    /**
     * 更新项目类型
     */
    private void update_type(Group group, boolean is_first) {
        pane_box.getChildren().clear();
        group.getChildren().remove(4, group.getChildren().size());

        switch (type) {
            case "日志" -> {
                // 从temp文件夹中读取缓存
                // 在lambda中不能使用非final的数据，所以给initRial_content加了final标签
                String[] temp_array = IOTool.read_file(path + File.separator + "temp" + File.separator + "log_temp");
                if (temp_array == null) {    // 如果temp_array是空
                    create_temp_directory();
                }
                final String[] initial_content = (temp_array == null) ? new String[]{"", "", ""} : temp_array;

                Label year = WinTool.createLabel(5, 100, 20, 25, 12, "年:");
                TextField year_field = WinTool.createTextField(25, 100, 50, 25, 12, initial_content[0], "");
                Label month = WinTool.createLabel(85, 100, 20, 25, 12, "月:");
                TextField month_field = WinTool.createTextField(105, 100, 30, 25, 12, initial_content[1], "");
                ListView<String> date_list = WinTool.createListView(0, 130, 150, 560);
                year_field.textProperty().addListener((observableValue, s, t1) -> {
                    initial_content[0] = year_field.getText();
                    IOTool.override_file(path + File.separator + "temp" + File.separator + "log_temp", initial_content);
                });
                month_field.textProperty().addListener((observableValue, s, t1) -> {
                    initial_content[1] = month_field.getText();
                    IOTool.override_file(path + File.separator + "temp" + File.separator + "log_temp", initial_content);
                });
                Button button_search = WinTool.createButton(10, 690, 60, 30, 15, "查询");
                button_search.setOnAction(actionEvent -> {
                    ClassTool tool = new ClassTool(
                            "Modes" + File.separator + "LogManager" + File.separator +
                                    "Date" + File.separator + "ShowDate.class");
                    Class<?> clazz = tool.get_class("Modes.LogManager.Date.ShowDate");
                    tool.invoke_method(clazz, "entrance",
                            new Class[]{ListView.class, boolean.class}, new Object[]{date_list, false},
                            new Class[]{String.class, String.class, String.class},
                            new Object[]{path, year_field.getText(), month_field.getText()});
                });
                Button button_delete = WinTool.createButton(80, 690, 60, 30, 15, "删除");
                button_delete.setOnAction(actionEvent -> {
                    initial_content[2] = "";
                    IOTool.override_file(path + File.separator + "temp" + File.separator + "log_temp", initial_content);

                    ClassTool tool = new ClassTool(
                            "Modes" + File.separator + "LogManager" + File.separator +
                                    "Date" + File.separator + "RemoveDate.class");
                    Class<?> clazz = tool.get_class("Modes.LogManager.Date.RemoveDate");
                    tool.invoke_method(clazz, "entrance",
                            new Class[]{ListView.class, String.class}, new Object[]{date_list, path});
                });
                Button button_create = WinTool.createButton(10, 725, 60, 30, 10, "创建日志");
                button_create.setOnAction(actionEvent -> {
                    ClassTool tool = new ClassTool(
                            "Modes" + File.separator + "LogManager" + File.separator +
                                    "Date" + File.separator + "CreateDate.class");
                    Class<?> clazz = tool.get_class("Modes.LogManager.Date.CreateDate");
                    tool.invoke_method(clazz, "entrance",
                            new Class[]{String.class, ListView.class, String.class, String.class},
                            new Object[]{path, date_list, year_field.getText(), month_field.getText()},
                            new Class[0], new Object[0]);    // 使用有参构造
                });
                Button button_open = WinTool.createButton(80, 725, 60, 30, 10, "打开日志");
                button_open.setOnAction(actionEvent -> {
                    initial_content[2] = date_list.getSelectionModel().getSelectedItem();
                    IOTool.override_file(path + File.separator + "temp" + File.separator + "log_temp", initial_content);

                    ClassTool tool = new ClassTool(
                            "Modes" + File.separator + "LogManager" + File.separator +
                                    "Data" + File.separator + "ShowData.class");
                    Class<?> clazz = tool.get_class("Modes.LogManager.Data.ShowData");
                    tool.invoke_method(clazz, "entrance",
                            new Class[]{VBox.class}, new Object[]{pane_box},
                            new Class[]{String.class, String.class},
                            new Object[]{path, date_list.getSelectionModel().getSelectedItem()});    // 使用有参构造
                });
                group.getChildren().addAll(year, year_field, month, month_field, date_list,
                        button_search, button_delete, button_create, button_open);

                // 打开基本信息
                if (!is_first) {
                    // SearchDate
                    ClassTool SearchDate = new ClassTool(
                            "Modes" + File.separator + "LogManager" + File.separator +
                                    "Date" + File.separator + "ShowDate.class");
                    Class<?> date_clazz = SearchDate.get_class("Modes.LogManager.Date.ShowDate");
                    SearchDate.invoke_method(date_clazz, "entrance",
                            new Class[]{ListView.class, boolean.class}, new Object[]{date_list, true},
                            new Class[]{String.class, String.class, String.class},
                            new Object[]{path, year_field.getText(), month_field.getText()});    // 使用有参构造

                    // SearchData
                    ClassTool SearchData = new ClassTool(
                            "Modes" + File.separator + "LogManager" + File.separator +
                                    "Data" + File.separator + "ShowData.class");
                    Class<?> data_clazz = SearchData.get_class("Modes.LogManager.Data.ShowData");
                    SearchData.invoke_method(data_clazz, "entrance",
                            new Class[]{VBox.class}, new Object[]{pane_box},
                            new Class[]{String.class, String.class},
                            new Object[]{path, initial_content[2]});    // 使用有参构造
                }
            }

            case "坐标" -> {
                ClassTool tool = new ClassTool(
                        "Modes" + File.separator + "PositionManager" + File.separator + "Searcher.class");
                Class<?> clazz = tool.get_class("Modes.PositionManager.Searcher");
                tool.invoke_method(clazz, "entrance",
                        new Class[]{ScrollPane.class, VBox.class, String.class},
                        new Object[]{scrollPane, pane_box, path});
            }

            case "计划表及正在做" -> {
                ClassTool tool = new ClassTool(
                        "Modes" + File.separator + "BehaviorManager" + File.separator +
                                "Todo" + File.separator + "List" + File.separator + "ShowLists.class");
                Class<?> clazz = tool.get_class("Modes.BehaviorManager.Todo.List.ShowLists");
                tool.invoke_method(clazz, "entrance",
                        new Class[]{VBox.class, String.class},
                        new Object[]{pane_box, path + File.separator + "behavior" + File.separator + "doing"},
                        new Class[0], new Object[0]);
            }
        }
    }

    /**
     * 创建临时文件夹，并初始化缓存文件
     */
    private void create_temp_directory() {
        File temp_dir = new File(path + File.separator + "temp");

        File log_temp = new File(temp_dir.getPath() + File.separator + "log_temp");
        if (!temp_dir.mkdir()) {
            log_temp.delete();
        }

        try {
            log_temp.createNewFile();

            String[] initial_content = {"", "", ""};    // 年，月，日志编号
            IOTool.override_file(log_temp.getPath(), initial_content);
        } catch (IOException ignored){}
    }

    /**
     * 主函数，启动JavaFX应用程序
     */
    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * @implNote 为了在打包成.exe后能够正常使用，添加了一个ConsoleEntrance用于调用Console的main函数
 */
class ConsoleEntrance {
    public static void main(String[] args) {
        // 创建连接
        Thread thread = new Thread(new ModConnector());
        thread.start();

        Console.main(args);
    }
}

/**
 * @implNote 用于和游戏中的Mod进行连接
 */
class ModConnector implements Runnable {
    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();
            Scanner scanner = new Scanner(client.getInputStream());
            PrintStream out = new PrintStream(client.getOutputStream());

            while (true) {
                if (scanner.hasNext()) {
                    String read_str = scanner.next();
                    // todo 2.0.0版本只推出test功能，2.0.1版本再继续完善
                    if (read_str.equals("test")) {     // 客户端要获取正在做
                        out.println("testing!!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
