package Constant;

import java.io.File;

/**
 * This constant class is used to record the relative path
 * that program may use usually
 */
public final class SystemPathConstant {
    public static final String ROOT_PATH = System.getProperty("user.dir");
    public static final String SEPARATOR = File.separator;

    public static final String HIDE_PASSWORD = ROOT_PATH + SEPARATOR + "data" + SEPARATOR + "hide_password.png";
    public static final String SHOW_PASSWORD = ROOT_PATH + SEPARATOR + "data" + SEPARATOR + "show_password.png";
}
