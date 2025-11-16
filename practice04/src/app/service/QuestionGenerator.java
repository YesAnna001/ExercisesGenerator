package app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.model.OperationBase;
import app.model.operation.BinaryOperation;

/**
 * 题目生成器
 * 使用 OperationBase 生成100以内的加减法题目
 */
public class QuestionGenerator {

    private final OperationBase opBase;

    /**
     * 构造方法，初始化 OperationBase（最大值 100）
     */
    public QuestionGenerator() {
        this.opBase = new OperationBase(100);
    }

    /**
     * 生成指定数量的混合加减题目
     * 
     * @param count 题目数量
     * @return 题目列表
     */
    public List<BinaryOperation> generateMixedQuestions(int count) {
        int addCount = count / 2;
        int subCount = count - addCount;

        List<BinaryOperation> exercises = new ArrayList<>();
        for (int i = 0; i < addCount; i++) {
            exercises.add(opBase.pickRandomAddition());
        }
        for (int i = 0; i < subCount; i++) {
            exercises.add(opBase.pickRandomSubtraction());
        }

        Collections.shuffle(exercises); // 打乱题目顺序
        return exercises;
    }
}
