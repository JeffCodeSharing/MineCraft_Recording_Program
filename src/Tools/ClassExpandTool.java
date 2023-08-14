package Tools;

import javafx.scene.control.Alert;

import java.lang.reflect.Method;

/**
 * 本是ClassTool的扩展类，可以记录实例化的class
 */
public class ClassExpandTool extends ClassTool {
    private Class<?> clazz;
    private Object initialization_clazz;

    public ClassExpandTool(String relative_path) {
        super(relative_path);
    }

    public void initialize_class(String classname, Class<?>[] constructor_params, Object[] constructor_args) {
        clazz = super.get_class(classname);    // 获得class

        try {
            // 获取实例化class
            initialization_clazz = clazz.getDeclaredConstructor(constructor_params).newInstance(constructor_args);
        } catch (Exception e) {
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "初始化class错误", "请重新尝试");
            initialization_clazz = null;
        }
    }

    public Object invoke_method(String method_name, Class<?>[] method_params, Object[] method_args) {
        try {
            Method method = clazz.getMethod(method_name, method_params);
            return method.invoke(initialization_clazz, method_args);
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "Method调用运行或错误", "请重新尝试");
            return null;
        }
    }
}
