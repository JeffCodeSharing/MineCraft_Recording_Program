package Modes.BehaviorManager.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据控制器类，用于管理任务列表的数据。
 */
public class DataController {
    private final List<String[]> values = new ArrayList<>();

    /**
     * 创建一个DataController对象，用于初始化任务列表的数据。
     *
     * @param values_in 任务数据的初始数组，使用者那一边通过IOTool.read_file(String path)读取得来数据
     */
    public DataController(String[] values_in) {
        for (String s : values_in) {
            values.add(s.split("\0"));
        }
    }

    /**
     * 添加新的任务。
     *
     * @param s 任务描述，本方法是直接添加到末尾，并且添加的一定是一级数据
     */
    public void add(String s) {
        values.add(new String[]{" · " + s, "BLACK"});    // 用于点击title时的创建
    }

    /**
     * 在指定位置插入新的任务。
     * 并且更改插入数据的所有父类的完成情况
     *
     * @param index 插入位置的索引
     * @param s     任务描述
     */
    public void add(int index, String s) {
        // 确定用户创建的信息的父类在第几级
        int flag = getTabNum(values.get(index)[0]);

        // 定位创建信息的创建位置
        for (index++; index < values.size(); index++) {    // 从index的下一个开始尝试
            int compare = getTabNum(values.get(index)[0]);

            // 判断
            if (compare <= flag) {   // 将新添数组移动到最后
                break;
            }
        }

        // 增加创建信息的"\t"数 并创建
        String[] add_values = {"\t".repeat(flag + 1) + "· " + s, "BLACK"};
        values.add(index, add_values);

        // 去除所有直接父类和间接父类的完成属性 -> 改为 "BLACK"
        int last_compare = flag+1;     // 用于记录上一个检索到的index的compare值，初始化为flag+1（新添加的数据的compare）
        for (; index >= 0; index--) {
            int compare = getTabNum(values.get(index)[0]);

            if (compare == last_compare-1) {    // 如果检查到的compare是last_compare的父类时
                String[] set_values = new String[]{values.get(index)[0], "BLACK"};
                values.set(index, set_values);

                // 判断是否是最后一个要改动的父类
                if (compare == 0) {
                    break;
                }
                last_compare = compare;
            }
        }
    }

    /**
     * 移除指定位置的任务。
     * 并且更新它的所有父类的完成情况
     *
     * @param index 要移除的任务的索引
     */
    public void remove(int index) {
        int flag = getTabNum(values.get(index)[0]);

        // 对照flag进行删除
        values.remove(index); // 删除传入的第一项
        while (index < values.size()) { // index不用自增是因为删除的过程中values的大小变小了
            int compare = getTabNum(values.get(index)[0]);

            // 判断
            if (compare <= flag) {
                break;
            } else {
                values.remove(index);
            }
        }

        // 更新Green情况
        if (flag != 0) { // 如果不是一级父类的情况下
            // 定位直接父类
            for (index--; index >= 0; index--) {
                int compare = getTabNum(values.get(index)[0]);
                if (compare == flag - 1) {
                    break;
                }
            }

            // 向下搜索子类，判断是否达到更改条件
            boolean be_green = true;
            boolean has_child_index = false;
            for (int backup = index + 1; backup < values.size(); backup++) {
                String[] value_data = values.get(backup);
                int compare = getTabNum(value_data[0]);

                if (compare == flag) {   // 当与原先删除的是一个级别的情况下，确认是否可以继续
                    has_child_index = true;
                    if (!value_data[1].equals("GREEN")) {
                        be_green = false;
                        break;
                    }
                } else if (compare > flag) {
                    break;
                }
            }

            if (be_green && has_child_index) {
                set(index, values.get(index)[0], "GREEN");
            }
        }
    }

    /**
     * 获取指定索引的字符串数组。
     *
     * @param index 要获取任务的索引
     * @return 包含任务描述和颜色的字符串数组
     */
    public String[] get(int index) {
        return values.get(index);
    }

    /**
     * 更改指定位置的任务描述和颜色。
     * 本函数只是用于方便调用者的描述和颜色是分开的情况时不用打包后再传入
     *
     * @param index       要设置任务的索引
     * @param s           任务描述
     * @param color_name  任务的颜色
     */
    public void set(int index, String s, String color_name) {     // 没有必要返回boolean，在更新后若有必要，可以调整
        set(index, new String[]{s, color_name}); // 递交给 public void set(int index, String[] set_data) 进行递归处理
    }

    /**
     * 设置指定位置的任务描述和颜色。
     *
     * @param index     要设置任务的索引
     * @param set_data  包含任务描述和颜色的字符串数组
     */
    public void set(int index, String[] set_data) {
        values.set(index, set_data);     // 设置自己

        int flag = getTabNum(values.get(index)[0]);

        if (set_data[1].equals("GREEN")) { // 如果第2值有GREEN
            // 将子类设为完成
            for (int backup = index+1; backup<values.size(); backup++) {
                String[] value_data = values.get(backup);
                int compare = getTabNum(value_data[0]);

                if (compare <= flag) {
                    break;
                } else {
                    values.set(backup, new String[]{value_data[0], "GREEN"});
                }
            }

            boolean be_green = true;
            int parent_index = 0;
            // 向上寻找
            for (int backup = index; backup >= 0; backup--) {    // backup是index的备份
                String[] value_data = values.get(backup);
                int compare = getTabNum(value_data[0]);

                // 以下判断没有 compare > flag，原因是因为没有必要比较
                if (compare == flag) {
                    if (!value_data[1].equals("GREEN")) {
                        be_green = false;
                        break;
                    }
                } else if (compare < flag) {
                    parent_index = backup;
                    break;
                }
            }

            // 向下寻找
            if (be_green) { // 如果在向上寻找的过程中已经确定不可能，就不用再执行了
                for (int backup = index; backup < values.size(); backup++) {
                    String[] value_data = values.get(backup);
                    int compare = getTabNum(value_data[0]);

                    // 以下判断没有 compare > flag，原因是因为没有必要比较
                    if (compare == flag) {
                        if (!value_data[1].equals("GREEN")) {
                            be_green = false;
                            break;
                        }
                    } else if (compare < flag) {
                        break;
                    }
                }
            }

            if (be_green) {
                String[] set_value = {values.get(parent_index)[0], "GREEN"};
                values.set(parent_index, set_value); // 如果是GREEN就将直接父类转换为GREEN

                if (!(parent_index == 0)) { // 当parent_index == 0时执行set会出现无限递归，所以中断执行，不要陷入死循环
                    set(parent_index, set_value);
                }
            }
        }
    }

    /**
     * 获取列表的数据。
     *
     * @return 返回列表
     */
    public List<String[]> getValues() {
        return values;
    }

    /**
     * 统计指定字符串开头的制表符个数。
     *
     * @param data 要统计的字符串
     * @return 开头的制表符个数
     */
    private int getTabNum(String data) {
        // 统计字符串开头的制表符个数
        int count = 0;
        while (count < data.length() && data.charAt(count) == '\t') {    // 增加count数量来控制判断的字符
            count++;
        }
        return count;
    }
}
