package Tools;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 本程序用于加密或解密指定字段
 */
public class EDTool {
    public static String encrypt(String s) {     // 加密
        char[] array = s.toCharArray();

        // 奇偶交换处理
        for (int i=0; i<array.length; i+=2) {
            if (i != array.length-1) {    // 如果字符列表不是只剩最后一个
                char temp = array[i];
                array[i] = array[i + 1];
                array[i + 1] = temp;
            }
        }

        // 使用Base64加密后输出
        return Base64.getUrlEncoder()
                .encodeToString(new String(array).getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(String s) {       // 解密
        // Base64解密处理
        s = new String(Base64.getUrlDecoder().decode(s), StandardCharsets.UTF_8);
        char[] array = s.toCharArray();

        // 奇偶交换处理
        for (int i=0; i<array.length; i+=2) {
            if (i != array.length-1) {    // 如果字符列表不是只剩最后一个
                char temp = array[i];
                array[i] = array[i + 1];
                array[i + 1] = temp;
            }
        }

        return new String(array);
    }
}
