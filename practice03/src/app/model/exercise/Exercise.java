package app.model.exercise;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import app.model.operation.BinaryOperation;

/**
 *  习题类（抽象类）
 *  其它类型的习题继承它，如只包含减法的习题类，只包含加法的习题类，混合型习题类...
 */
public abstract class Exercise {
    protected List<BinaryOperation> problems;   // 习题列表
    protected String exerciseId;    // 习题列表ID
    protected LocalDateTime createTime; // 创建时间
    
    // 构造方法
    public Exercise() {
        this.problems = new ArrayList<>();
        this.exerciseId = generateId();
        this.createTime = LocalDateTime.now();
    }
    

    // 抽象方法 - 生成指定习题数量的习题集
    public abstract void generateExercise(int count);
    

    // 添加习题到习题集
    public void addProblem(BinaryOperation problem) {
        if (problem.isValid()) {
            problems.add(problem);
        }
    }
    

    // 获取习题集
    public List<BinaryOperation> getProblems() {
        return Collections.unmodifiableList(problems);
    }
    

    // 获取习题集里的习题数量
    public int getProblemCount() {
        return problems.size();
    }
    

    // 获取习题集id
    private String generateId() {
        return "EX_" + System.currentTimeMillis();
    }
}
