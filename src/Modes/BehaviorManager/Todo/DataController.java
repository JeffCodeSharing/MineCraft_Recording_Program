package Modes.BehaviorManager.Todo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataController {
    private final DataPack value;

    public DataController(JSONObject data) {
        // way设为 ""，因为大标题没有way
        value = new DataPack(data.getString("name"), "", data.getString("color"), null);
        initData(data.getJSONArray("children"), value);
    }

    private static void initData(JSONArray children, DataPack parentPack) {
        for (Object tempChildData : children) {
            JSONObject childData = (JSONObject) tempChildData;
            String[] splitName = childData.getString("name").split(" ", 2);    // 防止用户因为没有填写way而读取不到空值的way
            DataPack child = new DataPack(splitName[0], splitName[1], childData.getString("color"), parentPack);
            parentPack.addChildren(child);

            if (!childData.getJSONArray("children").isEmpty()) {
                initData(childData.getJSONArray("children"), child);
            }
        }
    }

    /**
     * @return 将DataPack的信息一同全部返回，方便查询
     */
    public DataPack getValues() {
        return value;
    }

    /**
     * @return 生成一个JSONObject对象，方便进行保存操作
     */
    public JSONObject getJsonValues() {
        return value.toJsonObject();
    }

    /**
     * @implNote 将每一级封装成一个DataPack，并且保存相应的子DataPack
     */
    public static class DataPack {
        private final List<DataPack> children = new ArrayList<>();
        private final DataPack parent;      // 如果parent的值为null，那么它是标题
        private String note;
        private String way;
        private String color;

        public DataPack(String note, String way, String color, DataPack parent) {
            this.note = note;
            this.way = way;
            this.color = color;
            this.parent = parent;
        }

        public String getNote() {
            return note;
        }

        public String getWay() {
            return way;
        }

        public String getColor() {
            return color;
        }

        public List<DataPack> getChildren() {
            return children;
        }

        public DataPack getParent() {
            return parent;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setWay(String way) {
            this.way = way;
        }

        public void setColor(String color) {
            this.color = color;
        }

        /**
         * @implNote 用于在用户将一个 子DataPack 变换为绿色时，对父类进行能否变为绿色的检查
         */
        public void updateColor() {
            boolean canDone = true;
            for (DataPack child:children){
                if (!child.isDone()) {
                    canDone = false;
                    break;
                }
            }

            if (canDone) {
                // 此处不能够使用setDone(true); 否则会引发StackOverflowError
                // 因为setDone会调用 子DataPack 中的parent.updateColor(); 从而陷入循环
                setColor("GREEN");
                if (parent != null) {     // 检查是否到达标题
                    parent.updateColor();
                }
            }
        }

        public void setDone(boolean done) {
            if (done) {       // 由黑色变为绿色
                // 遵循将所有 子DataPack 都变为绿色
                setColor("GREEN");
                for (DataPack child:children) {
                    child.setDone(true);
                }

                // 遵循如果在同一个父类下的同级也都是绿的，那么父类也变绿
                if (parent != null) {
                    parent.updateColor();
                }
            } else {          // 由绿色变为黑色（CreateValue触发）
                // 遵循直接父类和所有间接父类变为黑色
                setColor("BLACK");
                if (parent != null) {     // 当调整父类时到达标题的时候（标题的parent值为null），停止对parent的调整
                    parent.setDone(false);
                }
            }
        }

        public void addChildren(DataPack addPack) {
            children.add(addPack);
        }

        public JSONObject toJsonObject() {
            JSONObject obj = new JSONObject();
            obj.put("name", note + " " + way);
            obj.put("color", color);

            JSONArray childrenArray = new JSONArray();
            for (DataPack child:children) {
                childrenArray.add(child.toJsonObject());
            }
            obj.put("children", childrenArray);
            return obj;
        }

        public boolean isDone() {
            return this.color.equalsIgnoreCase("GREEN");
        }
    }
}