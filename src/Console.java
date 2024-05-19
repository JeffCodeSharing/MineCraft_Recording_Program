import Modes.BehaviorManager.Todo.List.ShowLists;
import Modes.HelpDocument.HelpDocument;
import Modes.LogManager.Data.ShowData;
import Modes.LogManager.Date.CreateDate;
import Modes.LogManager.Date.RemoveDate;
import Modes.LogManager.Date.ShowDate;
import Modes.PositionManager.Searcher;
import Modes.ProjectManager.CreateProject;
import Modes.ProjectManager.OpenProject;
import Modes.ProjectManager.RemoveProject;
import Modes.ProjectManager.SaveAsProject;
import Modes.ProjectTypeManager.GameBackup.ShowBackup;
import Modes.ProjectTypeManager.Password.ShowPassword;
import Modes.ProjectTypeManager.Seed.ShowSeed;
import Modes.SettingManager.SettingManager;
import Modes.StationMap.ShowMap;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * 主程序入口，为Minecraft玩家提供信息记录的辅助功能。
 */
public class Console extends Application {
    private JSONObject jsonData;
    private static String path;
    private String type;
    private Label project_name;
    private Pane detail_pane;

    /**
     * 启动JavaFX应用程序，创建主舞台，检查更新
     */
    @Override
    public void start(Stage stage) {
        // 初始化所有数据
        initJsonData();

        // 密码确认
        CheckPassword checker = new CheckPassword(path);
        boolean returnValue = checker.entrance();

        if (!returnValue) {
            // 所有的东西进行归零
            path = null;
            type = "日志";
        } else {
            createTempDirectory();
        }

        Group group = new Group();
        Scene scene = new Scene(group);

        // 绘制控件
        drawControls(group);
        if (path != null) {
            File check = new File(path, "checkItem.json");
            try {
                if (check.exists()) {
                    JSONObject jsonData = JSONObject.parseObject(String.join("", IOTool.readFile(check.getPath())));
                    if (jsonData.getString("CreateTime") == null) {
                        throw new RuntimeException();
                    } else {
                        updateProjectName();
                    }
                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                path = null;
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

            JsonTool.writeJson(jsonData, jsonPath);
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
        detail_pane = new Pane();
        ScrollPane scrollPane = WinTool.createScrollPane(150, 30, 630, 720, detail_pane);

        project_name = WinTool.createLabel(0, 30, 150, 40, 14, "项目名:");

        ComboBox<String> typeBox = WinTool.createComboBox(0, 70, 150, 25, false,
                type, "日志", "坐标", "计划表及正在做", "地铁线路");
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

            path = creator.entrance();
            updateProjectName();
            detail_pane.getChildren().clear();
        });

        MenuItem openProject = new MenuItem("打开项目");
        openProject.setOnAction(actionEvent -> {
            OpenProject opener = new OpenProject();

            path = opener.entrance();
            updateProjectName();
            updateType(group, false);
        });

        MenuItem removeProject = new MenuItem("删除项目");
        removeProject.setOnAction(actionEvent -> {
            RemoveProject remover = new RemoveProject();
            boolean return_value = remover.entrance(path);

            // 删除项目后重置路径并清空界面
            if (return_value) {
                path = null;
                project_name.setText("项目名:");
                detail_pane.getChildren().clear();
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
                ShowSeed clazz = new ShowSeed(path + File.separator + "checkItem.json");
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

        // 帮助菜单
        Menu help = new Menu("帮助");
        MenuItem help_document = new MenuItem("帮助文档");
        help_document.setOnAction(actionEvent ->
                HelpDocument.entrance());

        help.getItems().addAll(help_document);

        // 设置菜单
        Menu settings = new Menu("设置");
        MenuItem settings_item = new MenuItem("设置");
        settings_item.setOnAction(actionEvent -> {
            SettingManager manager = new SettingManager();
            manager.entrance();
        });
        settings.getItems().addAll(settings_item);

        menuBar.getMenus().addAll(file, project_type, help, settings);
        group.getChildren().addAll(menuBar, project_name, typeBox, scrollPane);

        updateType(group, true);
    }

    /**
     * 更新项目名标签
     */
    private void updateProjectName() {
        String projectName = (path == null) ? "" : new File(path).getName();
        String add_str = "项目名:" + projectName;
        project_name.setText(add_str);
    }

    /**
     * 更新正在使用的项目类型
     */
    private void updateType(Group group, boolean is_first) {
        detail_pane.getChildren().clear();
        group.getChildren().remove(4, group.getChildren().size());

        switch (type) {
            case "日志" -> {
                // 从temp文件夹中读取缓存
                // 在lambda中不能使用非final的数据，所以给initRial_content加了final标签
                String temp_path = path + File.separator + "temp" + File.separator + "log_temp.json";
                JSONObject temp_data = JsonTool.readJson(temp_path);
                if (temp_data == null) {    // 如果temp_array是空
                    createTempDirectory();
                }

                // 创建一个空的JsonObject
                JSONObject empty_data = new JSONObject();
                empty_data.put("year", "");
                empty_data.put("month", "");
                empty_data.put("day", "");

                final JSONObject temp_data_final = (temp_data == null) ? empty_data : temp_data;

                Label year = WinTool.createLabel(5, 100, 20, 25, 12, "年:");
                TextField year_field = WinTool.createTextField(25, 100, 50, 25, 12, temp_data_final.getString("year"), "");
                Label month = WinTool.createLabel(85, 100, 20, 25, 12, "月:");
                TextField month_field = WinTool.createTextField(105, 100, 30, 25, 12, temp_data_final.getString("month"), "");
                ListView<String> date_list = WinTool.createListView(0, 130, 150, 560);
                year_field.textProperty().addListener((observableValue, s, t1) -> {
                    temp_data_final.replace("year", year_field.getText());
                    JsonTool.writeJson(temp_data_final, temp_path);
                });
                month_field.textProperty().addListener((observableValue, s, t1) -> {
                    temp_data_final.replace("month", month_field.getText());
                    JsonTool.writeJson(temp_data_final, temp_path);
                });
                Button button_search = WinTool.createButton(10, 690, 60, 30, 15, "查询");
                button_search.setOnAction(actionEvent -> {
                    ShowDate showDate = new ShowDate(date_list, false);
                    showDate.entrance(new File(path, "log").getPath(), year_field.getText(), month_field.getText());
                });
                Button button_delete = WinTool.createButton(80, 690, 60, 30, 15, "删除");
                button_delete.setOnAction(actionEvent -> {
                    temp_data_final.replace("day", "");
                    JsonTool.writeJson(temp_data_final, temp_path);

                    RemoveDate remover = new RemoveDate();
                    remover.entrance(date_list, new File(path, "log").getPath());
                });
                Button button_create = WinTool.createButton(10, 725, 60, 30, 10, "创建日志");
                button_create.setOnAction(actionEvent -> {
                    CreateDate creator = new CreateDate(new File(path, "log").getPath(), date_list, year_field.getText(), month_field.getText());
                    creator.entrance();
                });
                Button button_open = WinTool.createButton(80, 725, 60, 30, 10, "打开日志");
                button_open.setOnAction(actionEvent -> {
                    temp_data_final.replace("day", date_list.getSelectionModel().getSelectedItem());
                    JsonTool.writeJson(temp_data_final, temp_path);

                    ShowData showData = new ShowData(detail_pane, new File(path, "log").getPath(),
                            date_list.getSelectionModel().getSelectedItem());
                    showData.entrance();
                });
                group.getChildren().addAll(year, year_field, month, month_field, date_list,
                        button_search, button_delete, button_create, button_open);

                // 打开基本信息
                if (!is_first) {
                    // SearchDate
                    ShowDate searchDate = new ShowDate(date_list, true);
                    searchDate.entrance(path, year_field.getText(), month_field.getText());

                    // SearchData
                    ShowData searchData = new ShowData(detail_pane, new File(path, "log").getPath(),
                            temp_data_final.getString("day"));
                    searchData.entrance();
                }
            }

            case "坐标" -> {
                Searcher searcher = new Searcher();
                searcher.entrance(detail_pane, path);
            }

            case "计划表及正在做" -> {
                ShowLists showLists = new ShowLists(detail_pane, path + File.separator + "behavior" + File.separator + "doing");
                showLists.entrance();
            }

            case "地铁线路" -> {
                ShowMap showMap = new ShowMap(path);
                showMap.entrance(detail_pane);
            }
        }
    }

    /**
     * 创建临时文件夹，并初始化缓存文件
     */
    private void createTempDirectory() {
        if (path != null) {
            File temp_dir = new File(path, "temp");
            File log_temp = new File(temp_dir.getPath(), "log_temp.json");

            IOTool.removeDirectory(temp_dir.getPath());
            temp_dir.mkdirs();

            try {
                log_temp.createNewFile();

                // 添加空的 年、月、日编号
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("year", "");
                jsonObject.put("month", "");
                jsonObject.put("day", "");

                JsonTool.writeJson(jsonObject, log_temp.getPath());
            } catch (IOException ignored) {
            }
        }
    }

    private void initJsonData() {
        jsonData = JsonTool.readJson(System.getProperty("user.dir") + File.separator +
                "data" + File.separator + "information.json");

        if (jsonData == null) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "读取系统文件错误", "");
            path = null;
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
        Console.main(args);
    }
}
