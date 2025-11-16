package app.model.exercise;

import java.util.List;

import app.model.operation.BinaryOperation;
import app.service.QuestionGenerator;

/**
 *      加法专项练习
 */
public class AdditionExercise extends Exercise {
    
    public AdditionExercise() {
        super();
    }
    
    /**
     * 生成习题
     * @param count 题目数量
     */
    @Override
    public void generateExercise(int count) {
        System.out.println("进入方法了===============");
        QuestionGenerator generator = new QuestionGenerator();
        System.out.println("开始=================");
        List<BinaryOperation> generatedProblems = generator.generateAdditionQuestion(count);
        System.out.println("生成完成====================");
        // 直接将生成的题目列表添加到 Exercise 的 problems 字段中
        this.problems.clear(); // 如果之前有题目，先清空
        this.problems.addAll(generatedProblems);
    }

    @Override
    public String getType() {
        return "add";
    }
}

