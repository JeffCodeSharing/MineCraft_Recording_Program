package Tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IOTool {
    /**
     * 读取文件内容并以列表形式返回。
     *
     * @param path 文件路径
     * @return 读取到的文件内容列表，若读取失败则返回 null
     */
    public static List<String> readFileAsArrayList(String path) {
        try {
            List<String> list = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            br.close();
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取文件内容并以字符串数组形式返回。
     *
     * @param path 文件路径
     * @return 读取到的文件内容数组，若读取失败则返回 null
     */
    public static String[] readFile(String path) {
        List<String> list = readFileAsArrayList(path);
        if (list == null) {
            return null;
        } else {
            return list.toArray(new String[0]);
        }
    }

    public static String[] readFile(File file) {
        return readFile(file.getPath());
    }

    /**
     * 覆盖写入文件。
     *
     * @param path  文件路径
     * @param array 要写入的内容数组
     * @return 写入成功返回true，写入失败返回false
     */
    public static boolean overrideFile(String path, String[] array) {
        try (FileWriter writer = new FileWriter(path, StandardCharsets.UTF_8)) {
            for (String event : array) {
                writer.append(event).append("\n");
            }
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean overrideFile(String path, String data) {
        return overrideFile(path, new String[]{data});
    }

    /**
     * 删除目录及其下所有文件和子目录。
     *
     * @param path 要删除的目录路径
     * @return 删除成功返回true，删除失败返回false
     */
    public static boolean removeDirectory(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            String[] list = file.list();
            if (list != null) {
                for (String temp : list) {
                    if (!removeDirectory(new File(file.getPath(), temp).getPath())) {
                        return true;
                    }
                }
            }
        }
        return file.delete();
    }

    /**
     * 复制文件夹及其下所有文件和子目录到指定目录。
     *
     * @param start 要复制的文件夹路径
     * @param end   目标文件夹路径
     * @return 复制成功返回true，复制失败返回false
     */
    public static boolean saveAsDirectory(String start, String end) {
        File[] files = new File(start).listFiles();

        if (files == null) {
            return false;
        }

        for (File file : files) {
            File endCheckFile = new File(end, file.getName());
            File startCheckFile = new File(start, file.getName());

            if (startCheckFile.isDirectory()) {
                if (!endCheckFile.mkdirs() || !saveAsDirectory(startCheckFile.getPath(), endCheckFile.getPath())) {
                    return false;
                }
            } else if (startCheckFile.isFile()) {
                try {
                    Path sourcePath = startCheckFile.toPath();
                    Path destinationPath = endCheckFile.toPath();

                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean copyFile(String start, String end) {
        try {
            InputStream in = new FileInputStream(start);
            OutputStream out = new FileOutputStream(end);

            int reading_byte;
            while ((reading_byte = in.read()) != -1) {
                out.write(reading_byte);
            }

            in.close();
            out.close();

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean removeFile(String path) {
        try {
            File file = new File(path);
            file.delete();

            return false;
        } catch (Exception e) {
            return true;
        }
    }


    /**
     * 移动文件或文件夹。
     *
     * @param start 要移动的文件或文件夹的路径
     * @param end   目标路径
     * @return 移动成功返回true，移动失败返回false
     */
    public static boolean moveFile(String start, String end) {
        Path source = Paths.get(start);
        Path target = Paths.get(end);

        try {
            Files.move(source, target);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void removeEmptyDir(String folderPath) {
        File folder = new File(folderPath);

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    removeEmptyDir(file.getAbsolutePath());
                }
            }
        }

        if (Objects.requireNonNull(folder.listFiles()).length == 0) {
            folder.delete();
        }
    }
}
