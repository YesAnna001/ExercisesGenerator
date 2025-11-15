package app.ui;

import app.model.QuestionBank;
import app.model.User;
import app.model.operation.BinaryOperation;
import app.service.QuestionGenerator;
import app.storage.FileStorage;

import java.util.*;

/**
 * 教师界面类
 * 提供教师用户的功能界面，包括创建练习题、查看练习题和发布练习题
 */
public class TeacherUI {
	/** 输入扫描器 */
	private final Scanner scanner;
	/** 当前登录的教师用户 */
	private final User teacher;
	/** 文件存储服务 */
	private final FileStorage storage;
	/** 题目生成器 */
	private final QuestionGenerator generator = new QuestionGenerator();

	/**
	 * 构造函数
	 * 
	 * @param scanner 输入扫描器
	 * @param teacher 教师用户对象
	 * @param storage 文件存储服务
	 */
	public TeacherUI(Scanner scanner, User teacher, FileStorage storage) {
		this.scanner = scanner;
		this.teacher = teacher;
		this.storage = storage;
	}

	/**
	 * 运行教师界面主循环
	 * 显示菜单并处理用户选择
	 */
	public void run() {
		while (true) {
			System.out.println();
			System.out.println("----------------------------------------------------------");
			System.out.println("教师菜单：");
			System.out.println("1. 创建练习题");
			System.out.println("2. 查看练习题");
			System.out.println("3. 发布练习题");
			System.out.println("4. 注销");
			System.out.println("----------------------------------------------------------");
			String choice = InputHelper.readOption(scanner, "请输入选项序号：", new HashSet<>(Arrays.asList("1","2","3","4")));
			if ("1".equals(choice)) {
				createBank();
			} else if ("2".equals(choice)) {
				viewBanks();
			} else if ("3".equals(choice)) {
				publishFlow();
			} else if ("4".equals(choice)) {
				break;
			}
		}
	}

	/**
	 * 创建练习题流程
	 * 生成指定数量的题目并保存为新的题库
	 */
	private void createBank() {
		Integer n = InputHelper.readIntInRangeOrEmpty(scanner, "请输入生成题目数量（1-100）（或直接回车返回）：", 1, 100);
		if (n == null) return;
		List<BinaryOperation> questions = generator.generateMixedQuestions(n);
		String id = teacher.getUsername() + "_" + System.currentTimeMillis();
		QuestionBank bank = new QuestionBank(id, System.currentTimeMillis(), n, teacher.getUsername(), false, 0L);
		storage.saveNewBank(bank, questions);
		System.out.println("已生成题库并保存。");
		printQuestions6PerLine(questions);
	}

	/**
	 * 查看练习题流程
	 * 显示所有题库列表，并允许查看题库详情和题目
	 */
	private void viewBanks() {
		List<QuestionBank> list = storage.loadAllBanks();
		if (list.isEmpty()) { System.out.println("暂无题库。"); return; }
		printBankList(list, true);
		Integer idx = InputHelper.readOptionalIndex(scanner, "输入练习题序号查看详情（或直接回车返回）：", list.size());
		if (idx == null) return;
		QuestionBank bank = list.get(idx);
		System.out.println("创建时间：" + FileStorage.formatTime(bank.getCreatedAtMs()));
		System.out.println("题目数量：" + bank.getCount());
		System.out.println("创建人：" + bank.getCreator());
		System.out.println("发布状态：" + (bank.isPublished()?"已发布":"未发布"));
		List<BinaryOperation> qs = storage.loadQuestions(bank.getId());
		printQuestions6PerLine(qs);
	}

	/**
	 * 发布练习题流程
	 * 选择未发布的题库进行发布
	 */
	private void publishFlow() {
		List<QuestionBank> list = storage.loadAllBanks();
		if (list.isEmpty()) { System.out.println("暂无题库。"); return; }
		printBankList(list, true);
		Integer idx = InputHelper.readOptionalIndex(scanner, "输入练习题序号进行发布（或直接回车返回）：", list.size());
		if (idx == null) return;
		QuestionBank bank = list.get(idx);
		if (bank.isPublished()) { System.out.println("该题库已发布。"); return; }
		storage.updateBankPublished(bank.getId(), true, System.currentTimeMillis());
		System.out.println("发布成功！");
	}

	/**
	 * 打印题库列表
	 * 
	 * @param list 题库列表
	 * @param withStatus 是否显示发布状态
	 */
	private void printBankList(List<QuestionBank> list, boolean withStatus) {
		System.out.println("序号 | 创建时间              | 数量 | 创建人 | 发布状态");
		for (int i = 0; i < list.size(); i++) {
			QuestionBank b = list.get(i);
			String time = FileStorage.formatTime(b.getCreatedAtMs());
			String status = withStatus ? (b.isPublished()?"已发布":"未发布") : "";
			// ocale.ROOT 是一个用于表示根区域设置的常量。
			//在格式化输出时，使用 Locale.ROOT 可以确保格式化的方式不依赖于当前系统的区域设置，而是使用一个中立的、稳定的区域设置
			System.out.printf(Locale.ROOT, "%3d  | %s | %4d | %s | %s%n", i+1, time, b.getCount(), b.getCreator(), status);
		}
	}

	/**
	 * 每行打印6道题目
	 * 将题目列表格式化显示，每行最多显示6道题
	 * 
	 * @param questions 题目列表
	 */
	private void printQuestions6PerLine(List<BinaryOperation> questions) {
		for (int i = 0; i < questions.size(); i++) {
			System.out.printf(Locale.ROOT, "%-12s", questions.get(i).toDisplayString());
			if ((i+1) % 6 == 0 || i == questions.size()-1) System.out.println();
		}
	}
}
