package app.ui;

import app.model.QuestionBank;
import app.model.User;
import app.model.exercise.Exercise;
import app.model.exercise.MixedExercise;
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
     */
    public void run() {
        while (true) {
			String border = "----------------------------------------------------------";
			System.out.println(border);
			System.out.printf("| %-51s |\n", "教师菜单");
			System.out.println(border);
			System.out.printf("| %-50s |\n", "1. 创建练习题");
			System.out.printf("| %-50s |\n", "2. 查看练习题");
			System.out.printf("| %-50s |\n", "3. 发布练习题");
			System.out.printf("| %-53s |\n", "4. 注销");
			System.out.println(border);
            String choice = InputHelper.readOption(scanner, "请输入选项序号：", new HashSet<>(Arrays.asList("1","2","3","4")));
            switch (choice) {
                case "1":
                    createBank();
                    break;
                case "2":
                    viewBanks();
                    break;
                case "3":
                    publishFlow();
                    break;
                case "4":
                    return;
            }
        }
    }

    /**
     * 创建练习题流程
     * 用户输入题目数量，生成题库并保存
     */
    private void createBank() {
        // 1. 获取用户输入的题目数量
        Integer n = InputHelper.readIntInRangeOrEmpty(scanner, "请输入生成题目数量（1-100）（或直接回车返回）：", 1, 100);
        if (n == null) return;
    
        // 2. 创建 Exercise 子类管理题目
        Exercise exercise = new MixedExercise(); 
        exercise.generateExercise(n);            
    
        // 3. 打印提示信息和题目
        System.out.println("已生成题库并保存！");
        printQuestions6PerLine(exercise.getProblems());
    
        // 4. 创建题库QuestionBank对象设置相关信息
        String id = teacher.getUsername() + "_" + System.currentTimeMillis();
        QuestionBank bank = new QuestionBank(id, System.currentTimeMillis(), n, teacher.getUsername(), false, 0L);
    
        // 5. 将题库信息和具体的题目列表存入本地
        storage.saveNewBank(bank, exercise.getProblems());
    }
    

    /**
     * 查看题库流程
     * 显示题库列表并允许查看详情
     */
    private void viewBanks() {
        List<QuestionBank> list = storage.loadAllBanks();
        if (list.isEmpty()) {
            System.out.println("暂无题库。");
            return;
        }

        printBankList(list, true);

        Integer idx = InputHelper.readOptionalIndex(scanner, "请选择练习题查看详情（或直接回车返回）：", list.size());
        if (idx == null) return; // 用户回车返回

        QuestionBank bank = list.get(idx);
		System.out.println("----------------------------------------------------------");
        System.out.println("创建时间：" + FileStorage.formatTime(bank.getCreatedAtMs()));
        System.out.println("题目数量：" + bank.getCount());
        System.out.println("创建人：" + bank.getCreator());
        System.out.println("发布状态：" + (bank.isPublished() ? "已发布" : "未发布"));

        List<BinaryOperation> qs = storage.loadQuestions(bank.getId());
        printQuestions6PerLine(qs);
    }

    /**
     * 发布练习题流程
     * 用户选择未发布题库进行发布
     */
    private void publishFlow() {
        List<QuestionBank> list = storage.loadAllBanks();
        if (list.isEmpty()) {
            System.out.println("暂无题库。");
            return;
        }

        printBankList(list, true);

        Integer idx = InputHelper.readOptionalIndex(scanner, "请选择练习题序号进行发布（或直接回车返回）：", list.size());
        if (idx == null) return; // 用户回车返回

        QuestionBank bank = list.get(idx);
        if (bank.isPublished()) {
            System.out.println("该题库已发布！");
            return;
        }

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
            String status = withStatus ? (b.isPublished() ? "已发布" : "未发布") : "";
            System.out.printf(Locale.ROOT, "%3d  | %s | %4d | %s | %s%n", i + 1, time, b.getCount(), b.getCreator(), status);
        }
    }

    /**
     * 每行打印6道题目
     *
     * @param questions 题目列表
     */
    private void printQuestions6PerLine(List<BinaryOperation> questions) {
        for (int i = 0; i < questions.size(); i++) {
            System.out.printf(Locale.ROOT, "%-12s", questions.get(i).toDisplayString());
            if ((i + 1) % 6 == 0 || i == questions.size() - 1) System.out.println();
        }
    }
}
