package app.ui;

import app.model.AttemptRecord;
import app.model.QuestionBank;
import app.model.User;
import app.model.exercise.Exercise;
import app.model.exercise.MixedExercise;
import app.model.operation.BinaryOperation;
import app.storage.FileStorage;

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
			System.out.println();
			System.out.println("----------------------------------------------------------");
			System.out.println("学生菜单：");
			System.out.println("1. 开始考试");
			System.out.println("2. 查看成绩");
			System.out.println("3. 注销");
			System.out.println("----------------------------------------------------------");
			String choice = InputHelper.readOption(scanner, "请输入选项序号：", new HashSet<>(Arrays.asList("1","2","3")));
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
	 * 开始考试流程
	 * 显示已发布的题库列表，让学生选择并开始答题
	 */
	private void startExamFlow() {
		// 1. 加载所有已发布的答题库，存入List<QuestionBank>列表，循环遍历打印题库信息
		List<QuestionBank> pubs = storage.loadPublishedBanksAscByPublishedTime();
		if (pubs.isEmpty()) { System.out.println("暂无已发布题库。"); return; }
		System.out.println("序号 | 发布时间              | 数量 | 创建人");
		for (int i = 0; i < pubs.size(); i++) {
			QuestionBank b = pubs.get(i);
			System.out.printf(Locale.ROOT, "%3d  | %s | %4d | %s%n", i+1, FileStorage.formatTime(b.getPublishedAtMs()), b.getCount(), b.getCreator());
		}
		// 2. 获取用户选择的题库序号
		Integer idx = InputHelper.readOptionalIndex(scanner, "请选择答题题库序号（或直接回车返回）：", pubs.size());
		if (idx == null) return;
		// 3. 根据序号在list中寻找该题库
		QuestionBank bank = pubs.get(idx);
		// 4. 根据该题库的id加载题库内的所有题目
		List<BinaryOperation> qs = storage.loadQuestions(bank.getId());
		printQuestions6PerLine(qs);
		System.out.println("----------------------------------------------------------");
		System.out.println("是否开始答题？");
		String c = InputHelper.readOptionOrEmpty(scanner, "1. 开始答题   2. 返回（或直接回车返回）\n", new HashSet<>(Arrays.asList("1","2")));
		if (c == null || "2".equals(c)) {
			return;
		}
		System.out.println("----------------------------------------------------------");
		// （废弃）5. 学生开始答题，并得到一个答题记录列表
		// List<AttemptRecord> records = doExam(qs);
		
		// 5. 创建 Exercise 管理题目，传入上面读取到的所有题目
		Exercise exercise = new MixedExercise();
		exercise.setProblems(qs);
		exercise.start();
		// 5. 调用抽取方法完成答题流程
        List<AttemptRecord> records = takeExam(exercise);
		// 6. 存储本次做题的相关信息到本地
		storage.saveAttemptAndScore(bank.getId(), student.getUsername(), records);
		// 7. 打印得分信息
		long correct = records.stream().filter(AttemptRecord::isCorrect).count();
		int total = records.size();
		int score = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);
		System.out.println("提交完成。正确题数：" + correct + "/" + total + "    得分：" + score + " 分（满分100）");
	}

	 /**
     * 抽取出的答题方法
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
	// /**
	//  * 执行考试
	//  * 逐题显示题目，获取学生答案，并记录答题结果
	//  * 
	//  * @param qs 题目列表
	//  * @return 答题记录列表
	//  */
	// private List<AttemptRecord> doExam(List<BinaryOperation> qs) {
	// 	List<AttemptRecord> records = new ArrayList<>();	// 记录学生的答题
	// 	for (int i = 0; i < qs.size(); i++) {
	// 		BinaryOperation q = qs.get(i);
	// 		//打印出题目格式如（1）54-12 	同时获取用户输入的答案
	// 		int my = InputHelper.readInt(scanner, "(" + (i+1) + ") " + q.toDisplayString());	
	// 		boolean correct = (my == q.getAnswer());
	// 		// 每做一题都加入答题记录列表
	// 		records.add(new AttemptRecord(i, my, q.getAnswer(), correct));
	// 	}
	// 	return records;
	// }

	/**
	 * 查看成绩流程
	 * 显示学生的所有成绩记录，并允许查看每道题的详细答题情况
	 */
	private void viewScoresFlow() {
		List<String[]> list = storage.loadScoresByStudent(student.getUsername());
		System.out.println("----------------------------------------------------------");
		if (list.isEmpty()) { System.out.println("暂无成绩。"); return; }
		System.out.println("序号 | 标题                      | 提交时间            | 正确/总数 | 分数");
		for (int i = 0; i < list.size(); i++) {
			String[] a = list.get(i);
			// a[0]=bankId, a[2]=correct, a[3]=total, a[5]=submittedAt
			int correct = Integer.parseInt(a[2]);
			int total = Integer.parseInt(a[3]);
			String title = "100以内加减法" + total + "题";
			long submitAt = Long.parseLong(a[5]);
			int score = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);
			System.out.printf(Locale.ROOT, "%3d  | %-24s | %s | %2d/%-3d | %3d%n", i+1, title, FileStorage.formatTime(submitAt), correct, total, score);
		}
		Integer idx = InputHelper.readOptionalIndex(scanner, "请选择要查看的题库序号（或直接回车返回）：", list.size());
		if (idx == null) return;
		String bankId = list.get(idx)[0];
		List<BinaryOperation> qs = storage.loadQuestions(bankId);
		List<AttemptRecord> attempts = storage.loadAttempt(bankId, student.getUsername());
		System.out.println("----------------------------------------------------------");
		for (int i = 0; i < qs.size(); i++) {
			BinaryOperation q = qs.get(i);
			AttemptRecord r = attempts.size() > i ? attempts.get(i) : null;
			System.out.println("第" + (i+1) + "题：" + q.toDisplayString());
			if (r != null) {
				System.out.println("我的答案：" + r.getMyAnswer() + "    正确答案：" + r.getCorrectAnswer());
			} else {
				System.out.println("无作答记录");
			}
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
