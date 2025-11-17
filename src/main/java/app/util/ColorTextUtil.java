package app.util;

import java.util.HashMap;
import java.util.Map;

public class ColorTextUtil {

    /** ANSI 颜色映射表（前景色） */
    private static final Map<String, String> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("黑色", "30");
        COLOR_MAP.put("红色", "31");
        COLOR_MAP.put("绿色", "32");
        COLOR_MAP.put("黄色", "33");
        COLOR_MAP.put("蓝色", "34");
        COLOR_MAP.put("紫红色", "35");
        COLOR_MAP.put("青蓝色", "36");
        COLOR_MAP.put("白色", "37");
    }

    /**
     * 将文本用指定颜色包裹（使用 ANSI 转义序列）
     *
     * @param text       文本内容
     * @param colorName  中文颜色名，如 "红色", "蓝色"
     * @return 带颜色的字符串（终端支持 ANSI 时可见）
     */
    public static String color(String text, String colorName) {
        String code = COLOR_MAP.get(colorName);
        if (code == null) {
            return text; // 不支持的颜色名 → 返回原文本
        }
        return "\033[1;" + code + "m" + text + "\033[0m";
    }

}
