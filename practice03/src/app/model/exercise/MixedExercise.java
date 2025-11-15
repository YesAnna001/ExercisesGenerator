package app.model.exercise;

import java.util.List;

import app.model.operation.BinaryOperation;
import app.service.QuestionGenerator;

/**
 * 混合运算练习（100以内加减法）
 */
public class MixedExercise extends Exercise {

    public MixedExercise() {
        super();
    }

    /**
     * 生成习题
     * 使用 QuestionGenerator 生成指定数量的题目
     * 
     * @param count 题目数量
     */
    @Override
    public void generateExercise(int count) {
        QuestionGenerator generator = new QuestionGenerator();
        List<BinaryOperation> generatedProblems = generator.generateMixedQuestions(count);

        // 直接将生成的题目列表添加到 Exercise 的 problems 字段中
        this.problems.clear(); // 如果之前有题目，先清空
        this.problems.addAll(generatedProblems);
    }
}
