package Tools;

import javafx.scene.paint.Color;

public class ColorTool {
    /**
     * 将英文颜色名称转换为对应的颜色对象。
     *
     * @param s 英文颜色名称
     * @return 对应的颜色对象，如果传入的颜色名称无法识别，则返回 null
     */
    public static Color englishToColor(String s) {
        return switch (s.toUpperCase()) {
            case "BLACK" -> Color.BLACK;
            case "GREEN" -> Color.GREEN;
            case "ORANGE" -> Color.ORANGE;
            case "YELLOW" -> Color.YELLOW;
            case "RED" -> Color.RED;
            case "BLUE" -> Color.BLUE;
            case "PURPLE" -> Color.PURPLE;
            case "WHITE" -> Color.WHITE;
            case "GREY", "GRAY" -> Color.GREY;
            case "BROWN" -> Color.BROWN;
            default -> null;
        };
    }

    /**
     * 获取所有颜色名称的数组。数组中的元素为中文颜色名称。
     *
     * @return 所有颜色名称的数组
     */
    public static String[] getColorsName() {
        return new String[]{
                "黑色", "绿色", "橙色", "黄色", "红色", "蓝色", "紫色", "白色", "灰色", "褐色"
        };
    }

    /**
     * 将中文颜色名称转换为对应的英文颜色名称。
     *
     * @param chinese 中文颜色名称
     * @return 对应的英文颜色名称，如果传入的中文颜色名称无法识别，则返回 null
     */
    public static String chineseToEnglish(String chinese) {
        return switch (chinese) {
            case "黑色" -> "BLACK";
            case "绿色" -> "GREEN";
            case "橙色" -> "ORANGE";
            case "黄色" -> "YELLOW";
            case "红色" -> "RED";
            case "蓝色" -> "BLUE";
            case "紫色" -> "PURPLE";
            case "白色" -> "WHITE";
            case "灰色" -> "GREY";
            case "褐色" -> "BROWN";
            default -> null;
        };
    }

    /**
     * 将英文颜色名称转换为对应的中文颜色名称。
     *
     * @param english 英文颜色名称
     * @return 对应的中文颜色名称，如果传入的英文颜色名称无法识别，则返回 null
     */
    public static String englishToChinese(String english) {
        return switch (english.toUpperCase()) {
            case "BLACK" -> "黑色";
            case "GREEN" -> "绿色";
            case "ORANGE" -> "橙色";
            case "YELLOW" -> "黄色";
            case "RED" -> "红色";
            case "BLUE" -> "蓝色";
            case "PURPLE" -> "紫色";
            case "WHITE" -> "白色";
            case "GREY", "GRAY" -> "灰色";
            case "BROWN" -> "褐色";
            default -> null;
        };
    }
}
