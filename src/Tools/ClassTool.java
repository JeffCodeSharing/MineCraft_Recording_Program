package Tools;

import javafx.scene.control.Alert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class ClassTool extends ClassLoader {
    private final String PATH;

    public ClassTool(String relative_path) {
        this.PATH = System.getProperty("user.dir") + File.separator + relative_path;
    }

    public Class<?> get_class(String classname) {
        try {
            byte[] data = load_file_class_data();
            return super.defineClass(classname, data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "Class读取错误", "请重新尝试");
            return null;
        }
    }

    public Object invoke_method(Class<?> clazz, String method_name,
                                Class<?>[] method_params, Object[] method_args) {     // 调用无参构造
        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod(method_name, method_params);
            return method.invoke(obj, method_args);
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "Method调用运行或错误", "请重新尝试");
            return null;
        }
    }

    public Object invoke_method(Class<?> clazz, String method_name,
                                Class<?>[] constructor_params, Object[] constructor_args,
                                Class<?>[] method_params, Object[] method_args) {   // 调用有参构造
        try {
            Object obj = clazz.getDeclaredConstructor(constructor_params).newInstance(constructor_args);
            Method method = clazz.getMethod(method_name, method_params);
            return method.invoke(obj, method_args);
        } catch (Exception e) {
            e.printStackTrace();
            WinTool.createAlert(Alert.AlertType.ERROR, "错误", "Method调用错误", "请重新尝试");
            return null;
        }
    }

    private byte[] load_file_class_data() throws Exception {
        InputStream input = new FileInputStream(PATH);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        input.transferTo(bos);
        byte[] data = bos.toByteArray();
        input.close();
        bos.close();
        return data;
    }
}
