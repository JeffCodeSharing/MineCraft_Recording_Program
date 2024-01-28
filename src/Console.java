import Modes.BehaviorManager.Todo.List.ShowLists;
import Modes.HelpDocument.HelpDocument;
import Modes.LogManager.Data.ShowData;
import Modes.LogManager.Date.CreateDate;
import Modes.LogManager.Date.RemoveDate;
import Modes.LogManager.Date.ShowDate;
import ModServer.Communicator;
import Modes.ModDownloader.ModDownloader;
import Modes.PositionManager.Searcher;
import Modes.ProjectManager.CreateProject;
import Modes.ProjectManager.OpenProject;
import Modes.ProjectManager.RemoveProject;
import Modes.ProjectManager.SaveAsProject;
import Modes.ProjectTypeManager.GameBackup.ShowBackup;
import Modes.ProjectTypeManager.Password.ShowPassword;
import Modes.ProjectTypeManager.Seed.ShowSeed;
import Modes.SettingManager.SettingManager;
import ProjectSafe.CheckPassword;
import Tools.IOTool;
import Tools.JsonTool;
import Tools.WinTool;
import Update.UpdateManager;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * 主程序入口，为Minecraft玩家提供信息记录的辅助功能。
 */
public class Console extends Application {
    private JSONObject jsonData;
    private static String path;
    private String type;
    private Label project_name;
    private VBox pane_box;
    private ScrollPane scrollPane;

    /**
     * 启动JavaFX应用程序，创建主舞台，检查更新
     */
    @Override
    public void start(Stage stage) {
        // 初始化所有数据
        initJsonData();

        // 密码确认
        CheckPassword checker = new CheckPassword(path);
        String return_value = checker.entrance()[0];

        if (return_value.equals("false")) {
            // 所有的东西进行归零
            path = "none";
            type = "日志";
        } else {
            createTempDirectory();
        }

        Group group = new Group();
        Scene scene = new Scene(group);

        // 绘制控件
        drawControls(group);
        if (!path.equals("none")) {
            File check = new File(path + File.separator + "check_item");
            try {
                if (check.exists()) {
                    Scanner sc = new Scanner(check);
                    String check_temp = sc.nextLine();
                    if (!check_temp.startsWith("Create Time:")) {
                        throw new RuntimeException();
                    } else {
                        updateProjectName();
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
            String jsonPath = System.getProperty("user.dir") + File.separator + "data" + File.separator + "information.json";
            jsonData.replace("path", path);
            jsonData.replace("function", type);

            JsonTool.write_json(jsonData, jsonPath);
            System.exit(0);
        });
        stage.show();

        // 检查更新，循环更新
        Thread manager = new Thread(new UpdateManager());
        manager.start();
    }

    /**
     * 绘制控件和菜单
     */
    public void drawControls(Group group) {
        pane_box = new VBox();
        scrollPane = WinTool.createScrollPane(150, 30, 630, 720, pane_box);

        project_name = WinTool.createLabel(0, 30, 150, 40, 14, "项目名:");

        ComboBox<String> typeBox = WinTool.createComboBox(0, 70, 150, 25, false,
                type, "日志", "坐标", "计划表及正在做");
        typeBox.valueProperty().addListener((observableValue, strings, t1) -> {
            type = typeBox.getSelectionModel().getSelectedItem();
            updateType(group, false);
        });


        MenuBar menuBar = new MenuBar();
        menuBar.setMaxSize(800, 30);
        menuBar.setMinSize(800, 30);

        // 文件菜单
        Menu file = new Menu("文件");

        MenuItem createProject = new MenuItem("创建项目");
        createProject.setOnAction(actionEvent -> {
            CreateProject creator = new CreateProject();
            String return_path = creator.entrance()[0];

            if (!return_path.equals("")) {
                path = return_path;
                updateProjectName();

                pane_box.getChildren().clear();
            }
        });

        MenuItem openProject = new MenuItem("打开项目");
        openProject.setOnAction(actionEvent -> {
            OpenProject opener = new OpenProject();
            String return_path = opener.entrance()[0];

            if (!return_path.equals("")) {
                path = return_path;
                updateProjectName();

                updateType(group, false);
            }
        });

        MenuItem removeProject = new MenuItem("删除项目");
        removeProject.setOnAction(actionEvent -> {
            RemoveProject remover = new RemoveProject();
            boolean return_value = remover.entrance(path);

            // 删除项目后重置路径并清空界面
            if (return_value) {
                path = "none";
                project_name.setText("项目名:");
                pane_box.getChildren().clear();
            }
        });

        MenuItem saveAsProject = new MenuItem("另存为");
        saveAsProject.setOnAction(actionEvent -> {
            SaveAsProject saver = new SaveAsProject();
            saver.entrance(path);
        });

        file.getItems().addAll(createProject, openProject, new SeparatorMenuItem(),
                removeProject, saveAsProject
        );

        // 项目信息菜单
        Menu project_type = new Menu("项目信息");

        MenuItem seed = new MenuItem("种子");
        seed.setOnAction(actionEvent -> {
            if (!path.equals("none")) {
                ShowSeed clazz = new ShowSeed(path + File.separator + "check_item");
                clazz.entrance();
            } else {
                WinTool.createAlert(Alert.AlertType.INFORMATION, "提示", "还没有打开或创建项目", "请打开或创建项目后重试");
            }
        });

        MenuItem password = new MenuItem("密码");
        password.setOnAction(actionEvent -> {
            ShowPassword clazz = new ShowPassword(path);
            clazz.entrance();
        });

        MenuItem game_backup = new MenuItem("游戏备份");
        game_backup.setOnAction(actionEvent -> {
            ShowBackup clazz = new ShowBackup(path);
            clazz.entrance();
        });

        project_type.getItems().addAll(seed, password, game_backup);

        // java版Model下载菜单
        Menu model = new Menu("模组");
        MenuItem download_model = new MenuItem("下载模组");
        download_model.setOnAction(actionEvent -> {
            ModDownloader manager = new ModDownloader();
            manager.entrance();
        });

        model.getItems().addAll(download_model);

        // 帮助菜单
        Menu help = new Menu("帮助");
        MenuItem help_document = new MenuItem("帮助文档");
        help_document.setOnAction(actionEvent -> {
            HelpDocument helper = new HelpDocument();
            helper.entrance();
        });

        help.getItems().addAll(help_document);

        // 设置菜单
        Menu settings = new Menu("设置");
        MenuItem settings_item = new MenuItem("设置");
        settings_item.setOnAction(actionEvent -> {
            SettingManager manager = new SettingManager();
            manager.entrance();
        });
        settings.getItems().addAll(settings_item);

        menuBar.getMenus().addAll(file, project_type, model, help, settings);
        group.getChildren().addAll(menuBar, project_name, typeBox, scrollPane);

        updateType(group, true);
    }

    /**
     * 更新项目名标签
     */
    private void updateProjectName() {
        String[] temp = path.replace(File.separator, "/").split("/");
        String add_str = "项目名:" + temp[temp.length-1];
        project_name.setText(add_str);
    }

    /**
     * 更新项目类型
     */
    private void updateType(Group group, boolean is_first) {
        pane_box.getChildren().clear();
        group.getChildren().remove(4, group.getChildren().size());

        switch (type) {
            case "日志" -> {
                // 从temp文件夹中读取缓存
                // 在lambda中不能使用非final的数据，所以给initRial_content加了final标签
                String[] temp_array = IOTool.readFile(path + File.separator + "temp" + File.separator + "log_temp");
                if (temp_array == null) {    // 如果temp_array是空
                    createTempDirectory();
                }
                final String[] initial_content = (temp_array == null) ? new String[]{"", "", ""} : temp_array;

                Label year = WinTool.createLabel(5, 100, 20, 25, 12, "年:");
                TextField year_field = WinTool.createTextField(25, 100, 50, 25, 12, initial_content[0], "");
                Label month = WinTool.createLabel(85, 100, 20, 25, 12, "月:");
                TextField month_field = WinTool.createTextField(105, 100, 30, 25, 12, initial_content[1], "");
                ListView<String> date_list = WinTool.createListView(0, 130, 150, 560);
                year_field.textProperty().addListener((observableValue, s, t1) -> {
                    initial_content[0] = year_field.getText();
                    IOTool.overrideFile(path + File.separator + "temp" + File.separator + "log_temp", initial_content);
                });
                month_field.textProperty().addListener((observableValue, s, t1) -> {
                    initial_content[1] = month_field.getText();
                    IOTool.overrideFile(path + File.separator + "temp" + File.separator + "log_temp", initial_content);
                });
                Button button_search = WinTool.createButton(10, 690, 60, 30, 15, "查询");
                button_search.setOnAction(actionEvent -> {
                    ShowDate clazz = new ShowDate(date_list, false);
                    clazz.entrance(path, year_field.getText(), month_field.getText());
                });
                Button button_delete = WinTool.createButton(80, 690, 60, 30, 15, "删除");
                button_delete.setOnAction(actionEvent -> {
                    initial_content[2] = "";
                    IOTool.overrideFile(path + File.separator + "temp" + File.separator + "log_temp", initial_content);

                    RemoveDate remover = new RemoveDate();
                    remover.entrance(date_list, path);
                });
                Button button_create = WinTool.createButton(10, 725, 60, 30, 10, "创建日志");
                button_create.setOnAction(actionEvent -> {
                    CreateDate creator = new CreateDate(path, date_list, year_field.getText(), month_field.getText());
                    creator.entrance();
                });
                Button button_open = WinTool.createButton(80, 725, 60, 30, 10, "打开日志");
                button_open.setOnAction(actionEvent -> {
                    initial_content[2] = date_list.getSelectionModel().getSelectedItem();
                    IOTool.overrideFile(path + File.separator + "temp" + File.separator + "log_temp", initial_content);

                    ShowData clazz = new ShowData(pane_box);
                    clazz.entrance(path, date_list.getSelectionModel().getSelectedItem());
                });
                group.getChildren().addAll(year, year_field, month, month_field, date_list,
                        button_search, button_delete, button_create, button_open);

                // 打开基本信息
                if (!is_first) {
                    // SearchDate
                    ShowDate searchDate = new ShowDate(date_list, true);
                    searchDate.entrance(path, year_field.getText(), month_field.getText());

                    // SearchData
                    ShowData searchData = new ShowData(pane_box);
                    searchData.entrance(path, initial_content[2]);
                }
            }

            case "坐标" -> {
                Searcher searcher = new Searcher();
                searcher.entrance(scrollPane, pane_box, path);
            }

            case "计划表及正在做" -> {
                ShowLists clazz = new ShowLists(pane_box, path + File.separator + "behavior" + File.separator + "doing");
                clazz.entrance();
            }
        }
    }

    /**
     * 创建临时文件夹，并初始化缓存文件
     */
    private void createTempDirectory() {
        File temp_dir = new File(path + File.separator + "temp");

        File log_temp = new File(temp_dir.getPath() + File.separator + "log_temp");
        if (!temp_dir.mkdir()) {
            log_temp.delete();
        }

        try {
            log_temp.createNewFile();

            String[] initial_content = {"", "", ""};    // 年，月，日志编号
            IOTool.overrideFile(log_temp.getPath(), initial_content);
        } catch (IOException ignored){}
    }

    private void initJsonData() {
        jsonData = JsonTool.readJson(System.getProperty("user.dir") + File.separator +
                "data" + File.separator + "information.json");

        if (jsonData == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取系统文件错误", "");
            path = "none";
            type = "日志";
        } else {
            path = jsonData.getString("path");
            type = jsonData.getString("function");
        }
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
        Thread thread = new Thread(new Communicator());
        thread.start();

        Console.main(args);
    }
}
