package app.util;

import app.util.ColorTextUtil;
import java.util.Scanner;
import java.util.Set;

/**
 * 输入辅助工具类
 * 提供各种输入验证和读取方法，确保用户输入的合法性
 */
public class InputHelper {

    /** 把系统提示文本统一转成蓝色 */
    private static String blue(String text) {
        return ColorTextUtil.color(text, "blue");
    }

    /** 读取指定范围内的整数（必须输入） */
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(blue(prompt));
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println(blue("输入不能为空，请输入数字。"));
                continue;
            }
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println(blue("输入无效，请输入范围 " + min + "-" + max + " 内的数字。"));
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println(blue("输入无效，请输入数字。"));
            }
        }
    }

    /** 读取整数，可回车返回 null */
    public static Integer readIntInRangeOrEmpty(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(blue(prompt));
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println(blue("输入无效，请输入范围 " + min + "-" + max + " 内的数字，或直接回车返回。"));
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println(blue("输入无效，请输入数字！"));
            }
        }
    }

    /** 读取可选的列表序号 */
    public static Integer readOptionalIndex(Scanner scanner, String prompt, int size) {
        while (true) {
            System.out.print(blue(prompt));
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            try {
                int idx1 = Integer.parseInt(s);
                if (idx1 < 1 || idx1 > size) {
                    System.out.println(blue("序号超出范围，请重试。"));
                    continue;
                }
                return idx1 - 1;
            } catch (NumberFormatException e) {
                System.out.println(blue("输入无效，请输入序号数字。"));
            }
        }
    }

    /** 必须输入有效选项 */
    public static String readOption(Scanner scanner, String prompt, Set<String> allowed) {
        while (true) {
            System.out.print(blue(prompt));
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println(blue("输入不能为空，请输入有效选项。"));
                continue;
            }
            if (allowed.contains(s)) return s;
            System.out.println(blue("输入无效，可选项为：" + String.join("/", allowed)));
        }
    }

    /** 可回车返回 null 的选项输入 */
    public static String readOptionOrEmpty(Scanner scanner, String prompt, Set<String> allowed) {
        while (true) {
            System.out.print(blue(prompt));
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            if (allowed.contains(s)) return s;
            System.out.println(blue("输入无效，可选项为：" + String.join("/", allowed) + "，或直接回车返回。"));
        }
    }

    /** 必须输入整数 */
    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(blue(prompt));
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println(blue("输入不能为空，请输入数字。"));
                continue;
            }
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println(blue("输入无效，请输入数字。"));
            }
        }
    }
}
