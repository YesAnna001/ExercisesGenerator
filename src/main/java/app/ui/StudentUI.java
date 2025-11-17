package app.ui;

import app.model.AttemptRecord;
import app.model.QuestionBank;
import app.model.User;
import app.model.exercise.AdditionExercise;
import app.model.exercise.Exercise;
import app.model.exercise.MixedExercise;
import app.model.exercise.SubtractionExercise;
import app.model.operation.BinaryOperation;
import app.storage.FileStorage;
import app.util.ColorTextUtil;
import app.util.TimeUtils;

import java.util.*;

/**
 * 学生界面类
 * 提供学生用户的功能界面，包括开始考试和查看成绩
 */
public class StudentUI {
	/** 输入扫描器 */
	private final Scanner scanner;
	/** 当前登录的学生用户 */
	private final User student;
	/** 文件存储服务 */
	private final FileStorage storage;

	/**
	 * 构造函数
	 *
	 * @param scanner 输入扫描器
	 * @param student 学生用户对象
	 * @param storage 文件存储服务
	 */
	public StudentUI(Scanner scanner, User student, FileStorage storage) {
		this.scanner = scanner;
		this.student = student;
		this.storage = storage;
	}

	/**
	 * 运行学生界面主循环
	 * 显示菜单并处理用户选择
	 */
	public void run() {
		while (true) {

			String border = ColorTextUtil.color("----------------------------------------------------------", "blue");
			System.out.println(border);
			System.out.println(ColorTextUtil.color(String.format("| %-51s |", "学生菜单"), "blue"));
			System.out.println(border);
			System.out.println(ColorTextUtil.color(String.format("| %-51s |", "1. 开始考试"), "blue"));
			System.out.println(ColorTextUtil.color(String.format("| %-51s |", "2. 查看成绩"), "blue"));
			System.out.println(ColorTextUtil.color(String.format("| %-52s |", "3. 注销"), "blue"));
			System.out.println(border);

			// Arrays.asList("1","2","3") → 生成一个 List<String> ["1","2","3"] 不可变长
			// new HashSet<>(...) → 用这个 List 初始化一个 HashSet
			// 最终得到一个 HashSet<String>，里面的元素是 "1", "2", "3"，没有重复，顺序不保证。
			String choice = InputHelper.readOption(scanner, "请输入学生菜单选项序号：", new HashSet<>(Arrays.asList("1","2","3")));
			if ("1".equals(choice)) {
				startExamFlow();
			} else if ("2".equals(choice)) {
				viewScoresFlow();
			} else if ("3".equals(choice)) {
				break;
			}
		}
	}

	/**
	 * 1. 开始考试
	 */
	private void startExamFlow() {
		// 1. 加载所有已发布的答题库
		List<QuestionBank> pubs = storage.loadPublishedBanksAscByPublishedTime();
		if (pubs.isEmpty()) {
			System.out.println("暂无已发布题库。");
			return;
		}

		// 打印表头
		System.out.printf("%-4s | %-20s | %-6s | %-10s | %-12s%n",
				"序号", "发布时间", "数量", "创建人", "题库类型");
		// 打印每一行题库信息
		for (int i = 0; i < pubs.size(); i++) {
			QuestionBank b = pubs.get(i);
			String time = FileStorage.formatTime(b.getPublishedAtMs());
			String typeName = b.getTypeDisplayName();

			System.out.printf("%-6d | %-24s | %-8d | %-13s | %-12s%n",
					i + 1, time, b.getCount(), b.getCreator(), typeName);
		}

		// 2. 获取用户选择的题库序号
		Integer idx = InputHelper.readOptionalIndex(scanner, "请选择答题题库序号（或直接回车返回）：", pubs.size());
		if (idx == null) return;

		// 3. 根据序号在list中寻找该题库
		QuestionBank bank = pubs.get(idx);

		// 4. 根据该题库的id加载题库内的所有题目
		List<BinaryOperation> qs = storage.loadQuestions(bank.getId());

		System.out.println("----------------------------------------------------------");
		System.out.println(ColorTextUtil.color("是否开始答题？","red"));
		String c = InputHelper.readOptionOrEmpty(scanner, "1. 开始答题   2. 退出\n",
				new HashSet<>(Arrays.asList("1", "2")));
		if (c == null || "2".equals(c)) {
			return;
		}
		System.out.println("----------------------------------------------------------");

		// 5. 创建 Exercise 管理题目，根据题库的类型选择不同的exercise进行管理
		Exercise exercise = null;
		String type = bank.getType();
		if(type.equals("add")){
			exercise = new AdditionExercise();
		}else if(type.equals("sub")){
			exercise = new SubtractionExercise();
		}else{
			exercise = new MixedExercise();
		}
		exercise.setProblems(qs);
		exercise.start();

		// 6. 调用抽取方法完成答题流程
		List<AttemptRecord> records = takeExam(exercise);
		long spentTime = exercise.durationSeconds();
		String spentTimeFormated = TimeUtils.formatDuration(spentTime);
		// 7. 存储本次做题的相关信息到本地
		storage.saveAttemptAndScore(bank.getId(), student.getUsername(), records, spentTime);

		// 8. 打印得分信息
		long correct = records.stream().filter(AttemptRecord::isCorrect).count();
		int total = records.size();
		int score = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);
		System.out.println("已成功提交！");
		System.out.println("正确题数：" + correct + "/" + total + "    得分：" + score + " 分（满分100）" + "    答题时长：" + spentTimeFormated);
	}

	/**
	 * 循环展示题目，获取用户答案，并生成答题记录列表
	 *
	 * @param exercise Exercise 对象，包含题目和答题逻辑
	 * @return List<AttemptRecord> 答题记录列表
	 */
	private List<AttemptRecord> takeExam(Exercise exercise) {
		while (exercise.hasNext()) {
			BinaryOperation q = exercise.next();
			int answer = InputHelper.readInt(scanner, "(" + exercise.getIndex() + ") " + q.toDisplayString());
			exercise.submitAnswer(answer);
		}
		return exercise.toAttemptRecords(exercise.getUserAnswers());
	}


	/**
	 * 查看成绩流程
	 * 显示学生的所有成绩记录，并允许查看每道题的详细答题情况
	 */
	private void viewScoresFlow() {
		// 1. 根据学生的姓名加载他的所有的成绩记录表
		List<String[]> list = storage.loadScoresByStudent(student.getUsername());
		System.out.println("----------------------------------------------------------");
		if (list.isEmpty()) {
			System.out.println("暂无成绩。");
			return;
		}

		// 2. 打印成绩表头
		System.out.printf("%-4s | %-28s | %-22s | %-11s | %-10s | %-6s%n",
				"序号", "标题", "提交时间", "答题时长", "正确/总数", "分数");

		// 3. 循环打印每条成绩记录表信息
		for (int i = 0; i < list.size(); i++) {
			String[] a = list.get(i);
			String bankId = a[0];
			int correct = Integer.parseInt(a[2]);
			int total = Integer.parseInt(a[3]);
			long submitAt = Long.parseLong(a[5]);
			long spentTime = Long.parseLong(a[6]);
			String spentTimeFormated = TimeUtils.formatDuration(spentTime);
			int score = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);
			String title = buildTitleByBank(bankId, total);

			// 格式化输出，每列宽度固定
			System.out.printf(Locale.ROOT, "%-4d | %-24s | %-24s | %-10s | %3d/%-8d | %3d%n",
					i + 1,
					title,
					FileStorage.formatTime(submitAt),
					spentTimeFormated,
					correct,
					total,
					score
			);
		}
		// -----------------------------用户选择了查看成绩的具体内容-----------------------------------
		// 4. 获取用户要查看的列表序号
		Integer idx = InputHelper.readOptionalIndex(scanner, "请选择序号查看答题详情（或直接回车返回）：", list.size());
		if (idx == null) return;

		// 5. 获取题库ID、提交时间和题目列表（根据提交时间加载对应的 attempts 文件）
		String bankId = list.get(idx)[0];
		long submitAtMs = 0L;
		try {
			submitAtMs = Long.parseLong(list.get(idx)[5]); // scores.csv 的第6列是 submittedAtMs
		} catch (Exception e) {
			// 如果解析失败，fallback 到原来的单文件读取（保持向后兼容）
			submitAtMs = 0L;
		}
		List<BinaryOperation> qs = storage.loadQuestions(bankId);

		// 根据是否成功解析到 submitAtMs 决定调用哪个重载方法（0 表示未解析到具体时间，使用旧的单文件方式）
		List<AttemptRecord> attempts;
		if (submitAtMs > 0L) {
			attempts = storage.loadAttempt(bankId, student.getUsername(), submitAtMs);
		} else {
			attempts = storage.loadAttempt(bankId, student.getUsername());
		}

		System.out.println("----------------------------------------------------------");
		String rightIcon = ColorTextUtil.color("✔","green");
		String wrongIcon = ColorTextUtil.color("✖","red");
		// 6. 循环打印每道题的详细信息
		for (int i = 0; i < qs.size(); i++) {
			BinaryOperation q = qs.get(i);
			AttemptRecord r = attempts.size() > i ? attempts.get(i) : null;
			System.out.printf("第%-2d题：%-12s", i + 1, q.toDisplayString());
			if (r != null) {
//            System.out.printf(" 我的答案：%-6d 正确答案：%-6d%n", r.getMyAnswer(), r.getCorrectAnswer());
				System.out.printf(" 我的答案：%-6d", r.getMyAnswer());
				if(r.isCorrect()){
					System.out.println(rightIcon);
				}else {
					System.out.println(wrongIcon);
				}

				System.out.printf("正确答案：%-6d%n",r.getCorrectAnswer());
			} else {
				System.out.println(" 无作答记录");
			}
		}
	}



	/**
	 * 根据题库ID和题目数量total返回恰当的标题
	 * @param bankId
	 * @param total
	 * @return
	 */
	private String buildTitleByBank(String bankId, int total) {
		QuestionBank bank = storage.loadBank(bankId);
		if (bank != null) {
			String type = bank.getType();
			if (type != null && !type.trim().isEmpty()) {
				switch (type) {
					case "add":
						return "100以内加法专项练习" + total + "道题";
					case "sub":
						return "100以内减法专项练习" + total + "道题";
					case "mix":
						return "100以内加减混合练习" + total + "道题";
					default:
						return type;
				}
			}
		}
		return "练习" + total + "道题";
	}


	/**
	 * 每行打印6道题目
	 * 将题目列表格式化显示，每行最多显示6道题
	 *
	 * @param questions 题目列表
	 */
	private void printQuestions6PerLine(List<BinaryOperation> questions) {
		for (int i = 0; i < questions.size(); i++) {
			//Locale.ROOT：确保格式化统一，不受地区语言限制
			System.out.printf(Locale.ROOT, "%-12s", questions.get(i).toDisplayString());
			if ((i+1) % 6 == 0 || i == questions.size()-1) System.out.println();
		}
	}

	/**
	 * 根据题库的type字段格式化显示的描述信息
	 * @param type 题库类型 add / sub / mix
	 * @return
	 */
	private String getTypeDisplayName(String type) {
		switch (type) {
			case "add": return "加法专项练习";
			case "sub": return "减法专项练习";
			case "mix": return "加减混合练习";
			default:    return "未知题型";
		}
	}
}
