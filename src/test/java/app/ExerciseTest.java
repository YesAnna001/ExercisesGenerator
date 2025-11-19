package app;

import app.model.AttemptRecord;
import app.model.OperationBase;
import app.model.exercise.Exercise;
import app.model.operation.BinaryOperation;
import app.model.operation.Addition;
import app.model.operation.Subtraction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
/**
 * 测试 Exercise 抽象类的通用行为（使用一个简单的 ConcreteExercise 作为测试替身）。
 * 该测试不依赖 BinaryOperation 的内部实现，只验证 Exercise 提供的公共 API 是否按预期工作。
 */
public class ExerciseTest {

    /**
     * 测试替身：ConcreteExercise
     * - 使用 OperationBase 生成题目
     * - 重写 generateExercise 以生成不同类型的题（add/sub/mix）
     * - getType 返回构造时传入的类型
     */
    public static class ConcreteExercise extends Exercise {
        private final OperationBase base;
        private final String type;

        public ConcreteExercise(int maxValue, String type) {
            super();
            this.base = new OperationBase(maxValue);
            this.type = type;
        }

        @Override
        public void generateExercise(int count) {
            problems.clear();
            if ("add".equalsIgnoreCase(type)) {
                List<BinaryOperation> adds = base.getAdditionBase();
                for (int i = 0; i < Math.min(count, adds.size()); i++) problems.add(adds.get(i));
            } else if ("sub".equalsIgnoreCase(type)) {
                List<BinaryOperation> subs = base.getSubtractionBase();
                for (int i = 0; i < Math.min(count, subs.size()); i++) problems.add(subs.get(i));
            } else {
                problems.addAll(base.produceExercises(count));
            }
        }

        @Override
        public String getType() {
            return type;
        }
    }

    /**
     * **测试流程：生成题目 → 开始作答 → 逐题提交答案 → 检查计数、时间、总结、AttemptRecord 生成**
     *
     * 主要验证点：
     * 1. generateExercise 是否正确生成题目数量
     * 2. start() 是否初始化 index、计时
     * 3. next() / hasNext() 是否按序遍历题目
     * 4. submitAnswer() 是否记录用户答案
     * 5. total()/correctCount()/durationSeconds() 等统计方法是否正常
     * 6. summary() 是否生成包含总题数的文本
     * 7. toAttemptRecords() 生成的 AttemptRecord 数量是否正确
     */
    @Test
    public void testGenerateStartNextSubmitAndCounts() {
        ConcreteExercise ex = new ConcreteExercise(10, "mix");
        ex.generateExercise(5);

        assertEquals("generateExercise 应创建指定数量的题目", 5, ex.getProblemCount());
        assertTrue("getProblems 应返回非空列表", ex.getProblems().size() > 0);

        ex.start();
        assertEquals("start 后索引应为 0", 0, ex.getIndex());
        assertTrue("刚开始应有下一题", ex.hasNext());

        // 逐题取题并随意提交答案（验证提交流程是否正确）
        while (ex.hasNext()) {
            BinaryOperation q = ex.next();
            ex.submitAnswer(0); // 不依赖正确性，只验证记录行为
        }

        assertFalse("做完题目后 hasNext 应为 false", ex.hasNext());
        assertEquals("total 应返回题目总数", 5, ex.total());

        int correct = ex.correctCount();
        assertTrue("correctCount 应在 0..total 之间", correct >= 0 && correct <= ex.total());
        assertTrue("durationSeconds 应返回非负值", ex.durationSeconds() >= 0);

        String summary = ex.summary();
        assertNotNull("summary 不应为 null", summary);
        assertTrue("summary 应包含总题数信息", summary.contains("/" + ex.total()));

        // 构造与题目数一样多的答案，用于生成 AttemptRecord
        List<Integer> userAnswers = new ArrayList<>();
        for (int i = 0; i < ex.total(); i++) userAnswers.add(0);
        List<AttemptRecord> records = ex.toAttemptRecords(userAnswers);
        assertEquals("toAttemptRecords 返回的记录数应等于题目数量", ex.total(), records.size());
    }

    /**
     * **测试 resetForRedo 的行为**
     *
     * resetForRedo 用于“重做练习”，应满足：
     * 1. **保持 startMs 不变（不重置计时）**
     * 2. **将 index 重置为 0**
     * 3. **清空 userAnswers（重新答题）**
     * 4. **将 endMs 置 0**
     *
     * 此测试模拟：
     *      start → 做部分题 → 调用 resetForRedo
     * 并验证上述行为是否一致。
     */
    @Test
    public void testResetForRedoKeepsStartTimeAndClearsAnswers() throws InterruptedException {
        ConcreteExercise ex = new ConcreteExercise(10, "mix");
        ex.generateExercise(3);

        // start() 后 startMs 应被设置
        ex.start();
        Thread.sleep(5); // 确保 startMs 和当前时间不同
        assertTrue("start() 应设置 startMs 为正值", ex.getStartMs() > 0);

        // 只做两题，还未完成
        ex.submitAnswer(0);
        ex.submitAnswer(0);

        // 未做完 endMs 必须为 0
        assertEquals("未完成时 endMs 应为 0", 0, ex.getEndMs());

        long beforeStart = ex.getStartMs();
        ex.resetForRedo();

        // resetForRedo 验证
        assertEquals("resetForRedo 不应修改 startMs", beforeStart, ex.getStartMs());
        assertEquals("resetForRedo 应重置索引为 0", 0, ex.getIndex());
        assertTrue("resetForRedo 应清空 userAnswers", ex.getUserAnswers().isEmpty());
        assertEquals("resetForRedo 应将 endMs 置 0", 0, ex.getEndMs());
    }

    /**
     * **测试 setProblems / getProblems 的语义是否正确**
     *
     * 要求：
     * 1. setProblems 应覆盖原题目
     * 2. getProblems 返回的 List 应是“只读不可修改”的（防止外部改坏内部状态）
     */
    @Test
    public void testSetProblemsAndGetProblemsImmutable() {
        ConcreteExercise ex = new ConcreteExercise(10, "mix");

        // 构造 2 道假题
        List<BinaryOperation> list = new ArrayList<>();
        list.add(new Addition(2, 3, 5));
        list.add(new Subtraction(7, 2, 5));
        ex.setProblems(list);

        List<BinaryOperation> got = ex.getProblems();
        assertEquals("getProblems 大小应与设置一致", 2, got.size());

        // 尝试修改 getProblems 返回的列表，应抛出异常
        try {
            got.add(new Addition(1, 1, 2));
            fail("getProblems 返回的应为不可修改的列表");
        } catch (UnsupportedOperationException expected) {
            // 符合预期
        }
    }
}
