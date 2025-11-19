package app;
import app.model.OperationBase;
import app.model.operation.BinaryOperation;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import app.model.OperationBase;
import app.model.operation.BinaryOperation;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 针对 OperationBase 的单元测试
 */
public class OperationBaseTest {

    /**
     * 测试目的：确保构造 OperationBase 后，加法和减法题目列表非空。
     * 测试逻辑：调用 getAdditionBase() 和 getSubtractionBase()，检查返回列表不为 null 且非空。
     */
    @Test
    public void testBuildBase_AdditionAndSubtractionListsNotEmpty() {
        OperationBase base = new OperationBase(10); // 使用小 maxValue 加快测试
        List<BinaryOperation> adds = base.getAdditionBase();
        List<BinaryOperation> subs = base.getSubtractionBase();

        assertNotNull(adds); // 确认加法列表非 null
        assertNotNull(subs); // 确认减法列表非 null
        assertFalse("additionBase 不应为空", adds.isEmpty()); // 加法列表不为空
        assertFalse("subtractionBase 不应为空", subs.isEmpty()); // 减法列表不为空
    }

    /**
     * 测试目的：验证 pickRandom 方法返回的题目不为 null，并且答案在合法范围内。
     * 测试逻辑：调用 50 次 pickRandom()，检查返回的 BinaryOperation 不为 null，答案在 0..maxValue。
     */
    @Test
    public void testPickRandomReturnsNonNullAndValid() {
        OperationBase base = new OperationBase(10);
        for (int i = 0; i < 50; i++) {
            BinaryOperation op = base.pickRandom();
            assertNotNull("pickRandom 不应返回 null", op);
            int ans = op.getAnswer();
            assertTrue("答案应在 0..maxValue 范围内", ans >= 0 && ans <= base.getMaxValue());
        }
    }

    /**
     * 测试目的：验证 pickRandomAddition() 和 pickRandomSubtraction() 返回非 null，
     *           并且加法结果 ≤ maxValue，减法结果 ≥ 0。
     * 测试逻辑：各抽样一次，检查返回值及结果范围。
     */
    @Test
    public void testPickRandomAdditionAndSubtraction() {
        OperationBase base = new OperationBase(10);
        BinaryOperation a = base.pickRandomAddition();
        BinaryOperation s = base.pickRandomSubtraction();

        assertNotNull("pickRandomAddition 不应返回 null", a);
        assertNotNull("pickRandomSubtraction 不应返回 null", s);

        assertTrue("加法结果应 <= maxValue", a.getAnswer() <= base.getMaxValue());
        assertTrue("减法结果应 >= 0", s.getAnswer() >= 0);
    }

    /**
     * 测试目的：验证 at() 方法在合法索引和越界索引下的行为。
     * 测试逻辑：
     *   - 合法索引：返回 BinaryOperation 或 null（根据矩阵构造规则）。
     *   - 越界索引：应返回 null，不抛异常。
     */
    @Test
    public void testAtBoundaryAndInvalid() {
        int max = 7;
        OperationBase base = new OperationBase(max);

        // 合法索引
        BinaryOperation op00 = base.at(0, 0);
        assertNotNull(op00);

        // 最大索引可能返回 null（合法位置但 i<j 且 i+j>max）
        BinaryOperation opMaxMax = base.at(max, max);

        // 越界索引应返回 null
        assertNull("left < 0 应返回 null", base.at(-1, 0));
        assertNull("right < 0 应返回 null", base.at(0, -1));
        assertNull("left > max 应返回 null", base.at(max + 1, 0));
        assertNull("right > max 应返回 null", base.at(0, max + 1));
    }

    /**
     * 测试目的：验证 produceExercises(count) 返回指定数量的题目，并进行简单唯一性检查。
     * 测试逻辑：
     *   - 调用 produceExercises(count) 返回列表，确认非 null 且数量正确。
     *   - 将题目组合成 left:right:answer 字符串，统计唯一数量，确保重复率不极高。
     */
    @Test
    public void testProduceExercisesCountAndUniquenessBasic() {
        OperationBase base = new OperationBase(10);
        int count = 30;
        List<BinaryOperation> exs = base.produceExercises(count);

        assertNotNull(exs);
        assertEquals("produceExercises 返回数量应等于请求的 count", count, exs.size());

        // 简单唯一性检查
        Set<String> repr = new HashSet<>();
        for (BinaryOperation op : exs) {
            String s = op.getLeft() + ":" + op.getRight() + ":" + op.getAnswer();
            repr.add(s);
        }
        assertTrue("重复率不应极高（简单检查唯一性）", repr.size() > count / 4);
    }
}



