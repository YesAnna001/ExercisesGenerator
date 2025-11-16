package app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.model.operation.BinaryOperation;
import app.model.operation.Addition;
import app.model.operation.Subtraction;

/**
 * OperationBase 表示一个二维算式基（矩阵视图）
 * 左上半加法，右下半减法
 * 加法结果 ≤ maxValue，减法结果 ≥ 0
 */
public class OperationBase {
    private final int maxValue; // 最大操作数值
    private final BinaryOperation[][] base; // 存储整个算式矩阵
    private final Random rand = new Random(); // 随机数生成器
    private final List<BinaryOperation> additionBase;  // 加法算式基
    private final List<BinaryOperation> subtractionBase; // 减法算式基

    /**
     * 构造方法：初始化算式基
     * @param maxValue 最大操作数值
     */
    public OperationBase(int maxValue) {
        this.maxValue = maxValue;
        this.base = new BinaryOperation[maxValue + 1][maxValue + 1];
        this.additionBase = new ArrayList<>();
        this.subtractionBase = new ArrayList<>();
        buildBase();
    }

    /** 获取最大操作数 */
    public int getMaxValue() { 
        return maxValue; 
    }

    /** 获取整个算式矩阵 */
    public BinaryOperation[][] getBase() { 
        return base; 
    }

    /** 构建二维算式矩阵，并将加法、减法分别存入对应列表 */
    private void buildBase() {
        for (int i = 0; i <= maxValue; i++) {
            for (int j = 0; j <= maxValue; j++) {
                if (i + j <= maxValue) { // 加法，结果 ≤ maxValue
                    BinaryOperation op = new Addition(i, j, i + j);
                    base[i][j] = op;
                    additionBase.add(op);
                } else if (i >= j) { // 减法，结果 ≥ 0
                    BinaryOperation op = new Subtraction(i, j, i - j);
                    base[i][j] = op;
                    subtractionBase.add(op);
                } else {
                    base[i][j] = null; // 非法题目
                }
            }
        }
    }

    /** 获取加法算式列表副本 */
    public List<BinaryOperation> getAdditionBase() {
        return new ArrayList<>(additionBase);
    }

    /** 获取减法算式列表副本 */
    public List<BinaryOperation> getSubtractionBase() {
        return new ArrayList<>(subtractionBase);
    }

    /**
     * 随机获取整个矩阵中的一道合法算式（加法或减法）
     * @return 随机 BinaryOperation，不会返回 null
     */
    public BinaryOperation pickRandom() {
        BinaryOperation op = null;
        while (op == null) {
            int r = rand.nextInt(maxValue + 1);
            int c = rand.nextInt(maxValue + 1);
            op = base[r][c];
        }
        return op;
    }

    /** 随机获取一道加法算式 */
    public BinaryOperation pickRandomAddition() {
        if (additionBase.isEmpty()) return null;
        return additionBase.get(rand.nextInt(additionBase.size()));
    }

    /** 随机获取一道减法算式 */
    public BinaryOperation pickRandomSubtraction() {
        if (subtractionBase.isEmpty()) return null;
        return subtractionBase.get(rand.nextInt(subtractionBase.size()));
    }

    /**
     * 根据左操作数和右操作数获取指定位置的算式
     * @param left 左操作数
     * @param right 右操作数
     * @return 对应 BinaryOperation，如果越界或非法返回 null
     */
    public BinaryOperation at(int left, int right) {
        if (left < 0 || left > maxValue || right < 0 || right > maxValue) return null;
        return base[left][right];
    }

    /**
     * 批量生成随机习题（加减混合）
     * @param count 生成数量
     * @return 生成的 BinaryOperation 列表
     */
    public List<BinaryOperation> produceExercises(int count) {
        List<BinaryOperation> exercises = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            exercises.add(pickRandom());
        }
        return exercises;
    }


}
