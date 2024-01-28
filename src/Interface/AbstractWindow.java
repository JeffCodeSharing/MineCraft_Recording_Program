package Interface;

import javafx.scene.Group;

public interface AbstractWindow {
    /**
     * 打开窗口并返回被改变数据的类，该类用于调用程序并改变自身记录的数据。
     *
     * @return 返回程序的一些数据点
     */
    String[] entrance();

    /**
     * 绘制窗口组的控件。
     *
     * @param group 用于添加控件的 JavaFX Group 对象
     */
    void drawControls(Group group);
}
