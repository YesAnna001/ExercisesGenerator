package app.ui;

import app.model.QuestionBank;
import app.model.User;
import app.model.exercise.AdditionExercise;
import app.model.exercise.Exercise;
import app.model.exercise.MixedExercise;
import app.model.exercise.SubtractionExercise;
import app.model.operation.BinaryOperation;
import app.storage.FileStorage;

import java.io.File;
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
            String choice = InputHelper.readOption(scanner, "请输入教师菜单选项序号：", new HashSet<>(Arrays.asList("1","2","3","4")));
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
        // 选择题目来源 1. 从本地导入csv文件 2. 系统自动生成
        String source = chooseExerciseSource();
        if (source == null) return;

        if (source.equals("1")) {
            // 从 CSV 导入
            exercisesFromCSV();
        } else if (source.equals("2")) {
            // 系统生成
            String choice = chooseExerciseType();
            if (choice == null) return;

            Integer n = readCountOrBack();
            if (n == null) return;

            Exercise exercise = createExerciseByChoice(choice);
            if (exercise == null) {
                System.out.println("无法创建练习，请重试！");
                return;
            }
            saveBank(exercise,n);
        }
    }

    /**
     * 系统随机生成题库
     */
    private void exercisesFromRandomlyGenerate() {

    }


    /**
     * 从本地导入csv文件来生成题库
     */
    private void exercisesFromCSV() {
        System.out.print("请输入 CSV 文件路径（例如 D:\\questions.csv）：");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            System.out.println("未输入路径，操作取消。");
            return;
        }

        // 从 CSV 文件加载题目
        List<BinaryOperation> problems = FileStorage.loadFromCSV(path);
        if (problems == null || problems.isEmpty()) {
            System.out.println("CSV 文件无题目或路径错误！");
            return;
        }

        System.out.println("已导入 CSV 文件题目：");
        printQuestions6PerLine(problems);

        // 取文件名作为 type
        String name = new File(path).getName();
        String type = name.substring(0, name.lastIndexOf('.'));
        // 创建题库元信息
        String id = teacher.getUsername() + "_" + System.currentTimeMillis();
        QuestionBank bank = new QuestionBank(id, System.currentTimeMillis(), problems.size(),
                teacher.getUsername(), false, 0L, type);
        storage.saveNewBank(bank, problems);

        System.out.println("题库已保存！");
    }


    private String chooseExerciseSource() {
        System.out.printf("%s\n", "1. 从本地导入csv文件");
        System.out.printf("%s\n", "2. 系统自动生成");
        return InputHelper.readOptionOrEmpty(
                scanner,
                "请选择习题生成方式：",
                new HashSet<>(Arrays.asList("1", "2"))
        );
    }


    /**
 * 显示题型菜单并读取用户选择
 * @return 用户选择的字符串 "1","2","3"，或 null（表示回车返回）
 */
private String chooseExerciseType() {
    System.out.printf("%s\n", "1. 加法专项练习题");
    System.out.printf("%s\n", "2. 减法专项练习题");
    System.out.printf("%s\n", "3. 加减混合练习题");

    return InputHelper.readOptionOrEmpty(
        scanner,
        "请输入要生成的习题选项序号(或回车退出)：",
        new HashSet<>(Arrays.asList("1", "2", "3"))
    );
}

/**
 * 读取题目数量（1-100），如果用户回车则返回 null
 * @return 用户输入的数量，或 null（表示回车退出）
 */
private Integer readCountOrBack() {
    return InputHelper.readIntInRangeOrEmpty(
        scanner,
        "请输入生成题目数量（1-100）（或回车退出）：",
        1,
        100
    );
}

/**
 * 根据用户选择返回对应的 Exercise 子类实例
 * @param choice "1" 表示 AdditionExercise, "2" SubtractionExercise, "3" MixedExercise
 * @return 对应的 Exercise 实例（若未知则返回 null）
 */
private Exercise createExerciseByChoice(String choice) {
    switch (choice) {
        case "1": return new AdditionExercise();
        case "2": return new SubtractionExercise();
        case "3": return new MixedExercise();
        default:  return null;
    }
}

/**
 * 生成习题、打印并保存题库到本地存储
 * @param exercise Exercise 实例（未生成题目）
 * @param n 题目数量
 */
private void saveBank(Exercise exercise, int n) {
    // 生成题目
    exercise.generateExercise(n); //BUG
    // 打印提示与题目
    System.out.println("已生成题库并保存！");
    printQuestions6PerLine(exercise.getProblems());

    // 创建题库元信息（id、时间等）
    String id = teacher.getUsername() + "_" + System.currentTimeMillis();
    String type = exercise.getType();
    QuestionBank bank = new QuestionBank(id, System.currentTimeMillis(), n, teacher.getUsername(), false, 0L,type);
//    System.out.println("bank类型："+ bank.getType());
    // 保存到本地（storage.saveNewBank 假设存在并可用）
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

        Integer idx = InputHelper.readOptionalIndex(scanner, "请选择练习题查看详情（或直接回退出）：", list.size());
        if (idx == null) return; // 用户回车返回

        QuestionBank bank = list.get(idx);
		System.out.println("----------------------------------------------------------");
        System.out.println("创建时间：" + FileStorage.formatTime(bank.getCreatedAtMs()));
        System.out.println("题目数量：" + bank.getCount());
        System.out.println("创建人：" + bank.getCreator());
        System.out.println("发布状态：" + (bank.isPublished() ? "已发布" : "未发布"));
        System.out.println("题库类型："  + bank.getTypeDisplayName());

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
            System.out.println("该题库已发布，无需重复发布！");
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
        // 列宽：序号 4，创建时间 20，数量 6，创建人 10，发布状态 8，题库类型 12
        System.out.printf("%-4s | %-20s | %-6s | %-10s | %-8s | %-12s%n",
                "序号", "创建时间", "数量", "创建人", "发布状态", "题库类型");
        
        for (int i = 0; i < list.size(); i++) {
            QuestionBank b = list.get(i);
            String time = FileStorage.formatTime(b.getCreatedAtMs());
            String status = withStatus ? (b.isPublished() ? "已发布" : "未发布") : "";
            String typeName = b.getTypeDisplayName();
            
            System.out.printf("%-6d | %-24s | %-8d | %-13s | %-9s | %-10s%n",
                    i + 1, time, b.getCount(), b.getCreator(), status, typeName);
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
