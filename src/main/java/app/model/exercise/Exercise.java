package app.model.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.model.AttemptRecord;
import app.model.operation.BinaryOperation;

/**
 * Exercise 抽象类
 * 用于表示一个习题集
 * 
 * 既可以作为题库容器，也可以在学生做题时作为“做题对象”使用
 */
public abstract class Exercise {

    // ------- 基本信息 -------
    /** 题目列表 */
    protected List<BinaryOperation> problems;   
    /** 习题集 ID */
    protected String exerciseId;                
    /** 创建时间（毫秒） */
    protected long createTimeMs;                

    /** 用户提交的答案（按题目顺序存储） */
    protected List<Integer> userAnswers;        
    /** 下一个题目的索引 */
    protected int index;                        
    /** 开始做题时间（毫秒），未开始为0 */
    protected long startMs;                     
    /** 结束时间（毫秒），未结束为0 */
    protected long endMs;                       


    /**
     * 默认构造函数
     * 初始化题目列表、生成习题ID、记录创建时间
     */
    public Exercise() {
        this.problems = new ArrayList<>();
        this.exerciseId = generateId();
        this.createTimeMs = System.currentTimeMillis();
        this.userAnswers = new ArrayList<>();
        this.index = 0;
        this.startMs = 0;
        this.endMs = 0;
    }

    // ------- 抽象方法 -------
    /**
     * 生成习题
     * 子类必须实现此方法，将生成的题目加入 problems 列表
     *
     * @param count 要生成的题目数量
     */
    public abstract void generateExercise(int count);


    /**
     * 返回题目类型字符串
     * 例如 "add" / "sub" / "mix"
     */
    public abstract String getType();

    // ------- 题目列表相关方法 -------
    /**
     * 获取题目列表
     * 
     * @return BinaryOperation格式的题目列表
     */
    public List<BinaryOperation> getProblems() {
        return Collections.unmodifiableList(problems);
    }

    /**
     * 获取题目数量
     * 
     * @return 题目数量
     */
    public int getProblemCount() {
        return problems.size();
    }

    /**
     * 生成唯一ID
     * 
     * @return 习题ID字符串
     */
    private String generateId() {
        return "EX_" + System.currentTimeMillis();
    }

    // ------- 做题相关方法 -------
    /**
     * 开始做题
     * 清空旧的答题记录，重置索引，记录开始时间
     */
    public void start() {
        userAnswers.clear();
        index = 0;
        startMs = System.currentTimeMillis();
        endMs = 0;
    }

    /**
     * 是否还有下一题
     * 
     * @return true 表示还有题目，false 表示已经做完
     */
    public boolean hasNext() {
        return index < problems.size();
    }

    /**
     * 获取下一题，并推进题目索引
     * 
     * @return 当前题目对象
     * @throws IllegalStateException 如果没有下一题
     */
    public BinaryOperation next() {
        if (!hasNext()) throw new IllegalStateException("这是最后一题了哦");
        return problems.get(index++);
    }

    /**
     * 提交答案
     * 
     * @param answer 用户提交的答案
     * 自动记录结束时间（如果是最后一题）
     */
    public void submitAnswer(int answer) {
        userAnswers.add(answer);
        if (!hasNext()) {
            endMs = System.currentTimeMillis();
        }
    }

    /**
     * 获取题目总数
     * 
     * @return 总题数
     */
    public int total() {
        return problems.size();
    }

    /** 获取当前题目索引（1-based，方便显示） */
    public int getIndex() {
        return index;
    }

    /** 获取用户答案列表 */
    public List<Integer> getUserAnswers() {
        // 返回一个“不可修改的列表”，而不是直接返回 userAnswers 本身。
        // 作用：保证 Exercise 类对 userAnswers 的控制权，只能通过 submitAnswer(int answer) 来添加答案
        return Collections.unmodifiableList(userAnswers);
    }


    /**
     * 获取答对题目数量
     * 
     * @return 正确题目数量
    */
    public int correctCount() {
        int c = 0;
        // 取较小值，避免索引越界
        int n = Math.min(userAnswers.size(), problems.size());
        for (int i = 0; i < n; i++) {
            // 用 BinaryOperation 的 getAnswer() 判断用户提交答案是否正确
            if (userAnswers.get(i) == problems.get(i).getAnswer()) {
                c++;
            }
        }
        return c;
    }

    /**
     * 设置题目列表
     * 允许在外部直接赋值题目（例如从文件或题库加载）
     * @param problems 题目列表
     */
    public void setProblems(List<BinaryOperation> problems) {
        this.problems = new ArrayList<>(problems);
    }



    /**
     * 计算做题时长（秒）
     * 
     * @return 时长（秒），如果未开始返回0
     */
    public long durationSeconds() {
        long end = (endMs == 0) ? System.currentTimeMillis() : endMs;
        if (startMs == 0) return 0;
        return (end - startMs) / 1000;
    }

    /**
     * 返回做题总结字符串
     * 格式：Correct: 正确/总题数, Time: 时长秒数
     * 
     * @return 总结字符串
     */
    public String summary() {
        return "正确 / 总数: " + correctCount() + "/" + total() + ", 时间: " + durationSeconds() + "s";
    }

    /**
     * 将用户答题记录转换为 AttemptRecord 列表
     * 
     * @param userAnswers 用户提交的答案列表
     * @return AttemptRecord 列表，用于记录每道题答题情况
     */
    public List<AttemptRecord> toAttemptRecords(List<Integer> userAnswers) {
        List<AttemptRecord> list = new ArrayList<>();

        for (int i = 0; i < problems.size(); i++) {
            BinaryOperation op = problems.get(i);
            int my = userAnswers.get(i);
            int correct = op.getAnswer(); // BinaryOperation 子类需实现 getResult()
            boolean ok = (my == correct);
            list.add(new AttemptRecord(i, my, correct, ok));
        }

        return list;
    }
}
