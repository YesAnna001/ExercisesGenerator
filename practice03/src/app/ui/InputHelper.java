package app.ui;

import java.util.Scanner;
import java.util.Set;

/**
 * 输入辅助工具类
 * 提供各种输入验证和读取方法，确保用户输入的合法性
 */
public class InputHelper {
	/**
	 * 读取指定范围内的整数
	 * 持续提示用户输入，直到输入有效的整数且在指定范围内
	 * 
	 * @param scanner 扫描器对象，用于读取用户输入
	 * @param prompt 提示信息
	 * @param min 允许的最小值（包含）
	 * @param max 允许的最大值（包含）
	 * @return 用户输入的合法整数值
	 */
	public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
		while (true) {
			System.out.print(prompt);
			String s = scanner.nextLine().trim();
			try {
				int v = Integer.parseInt(s);
				if (v < min || v > max) {
					System.out.println("输入无效，请输入范围" + min + "-" + max + "内的数字。");
					continue;
				}
				return v;
			} catch (Exception e) {
				System.out.println("输入无效，请输入数字。");
			}
		}
	}

	/**
	 * 读取指定范围内的整数，允许为空（直接回车返回）
	 * 持续提示用户输入，直到输入有效的整数且在指定范围内，或直接回车返回null
	 * 
	 * @param scanner 扫描器对象，用于读取用户输入
	 * @param prompt 提示信息
	 * @param min 允许的最小值（包含）
	 * @param max 允许的最大值（包含）
	 * @return 用户输入的合法整数值，如果用户直接回车则返回null
	 */
	public static Integer readIntInRangeOrEmpty(Scanner scanner, String prompt, int min, int max) {
		while (true) {
			System.out.print(prompt);
			String s = scanner.nextLine().trim();
			if (s.isEmpty()) return null;
			try {
				int v = Integer.parseInt(s);
				if (v < min || v > max) {
					System.out.println("输入无效，请输入范围" + min + "-" + max + "内的数字，或直接回车返回。");
					continue;
				}
				return v;
			} catch (Exception e) {
				System.out.println("输入无效，请输入数字，或直接回车返回。");
			}
		}
	}

	/**
	 * 读取可选的列表序号（从1开始）
	 * 用于带"回车返回"的列表序号选择，将用户输入的序号（1-based）转换为数组索引（0-based）
	 * 
	 * @param scanner 扫描器对象，用于读取用户输入
	 * @param prompt 提示信息
	 * @param size 列表的大小，用于验证序号范围
	 * @return 对应的数组索引（0-based），如果用户直接回车则返回null表示返回
	 */
	public static Integer readOptionalIndex(Scanner scanner, String prompt, int size) {
		while (true) {
			System.out.print(prompt);
			String s = scanner.nextLine().trim();
			if (s.isEmpty()) return null;
			try {
				int idx1 = Integer.parseInt(s);
				if (idx1 < 1 || idx1 > size) {
					System.out.println("序号超出范围，请重试。");
					continue;
				}
				return idx1 - 1;
			} catch (Exception e) {
				System.out.println("输入无效，请输入序号数字。");
			}
		}
	}

	/**
	 * 读取指定选项集合中的选项
	 * 持续提示用户输入，直到输入的值在允许的选项集合中
	 * 
	 * @param scanner 扫描器对象，用于读取用户输入
	 * @param prompt 提示信息
	 * @param allowed 允许的选项集合
	 * @return 用户输入的合法选项值
	 */
	public static String readOption(Scanner scanner, String prompt, Set<String> allowed) {
		while (true) {
			System.out.print(prompt);
			String s = scanner.nextLine().trim();
			if (allowed.contains(s)) return s;
			System.out.println("输入无效，可选项为：" + String.join("/", allowed));
		}
	}

	/**
	 * 读取指定选项集合中的选项，允许为空（直接回车返回）
	 * 持续提示用户输入，直到输入的值在允许的选项集合中，或直接回车返回null
	 * 
	 * @param scanner 扫描器对象，用于读取用户输入
	 * @param prompt 提示信息
	 * @param allowed 允许的选项集合
	 * @return 用户输入的合法选项值，如果用户直接回车则返回null表示返回
	 */
	public static String readOptionOrEmpty(Scanner scanner, String prompt, Set<String> allowed) {
		while (true) {
			System.out.print(prompt);
			String s = scanner.nextLine().trim();
			if (s.isEmpty()) return null;
			if (allowed.contains(s)) return s;
			System.out.println("输入无效，可选项为：" + String.join("/", allowed) + "，或直接回车返回。");
		}
	}

	/**
	 * 读取整数
	 * 持续提示用户输入，直到输入有效的整数
	 * 
	 * @param scanner 扫描器对象，用于读取用户输入
	 * @param prompt 提示信息
	 * @return 用户输入的整数值
	 */
	public static int readInt(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String s = scanner.nextLine().trim();
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
				System.out.println("输入无效，请输入数字。");
			}
		}
	}
}
