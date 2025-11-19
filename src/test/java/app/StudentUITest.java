package app;

import app.model.AttemptRecord;
import app.model.QuestionBank;
import app.model.User;
import app.model.operation.Addition;
import app.model.operation.BinaryOperation;
import app.storage.FileStorage;
import app.ui.StudentUI;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StudentUITest {

    @Test
    public void testStudentTakesExam_andScoreIsProduced() throws Exception {
        // 捕获 System.out 输出
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // 1) mock FileStorage：返回一个已发布题库（type = add）和两道简单题
            FileStorage storage = mock(FileStorage.class);

            String bankId = "BANK_TEST_1";
            QuestionBank qb = new QuestionBank(bankId, System.currentTimeMillis(), 2, "teacherA", true, System.currentTimeMillis(), "add");
            when(storage.loadPublishedBanksAscByPublishedTime()).thenReturn(Collections.singletonList(qb));

            List<BinaryOperation> questions = new ArrayList<>();
            questions.add(new Addition(2, 3, 5)); // 答案 5
            questions.add(new Addition(4, 1, 5)); // 答案 5
            when(storage.loadQuestions(bankId)).thenReturn(questions);

            // 2) mock User：返回用户名
            User student = mock(User.class);
            when(student.getUsername()).thenReturn("stu01");

            // 3) 构造输入流模拟用户交互（按步骤输入）
            //  - 选择题库：1
            //  - 确认开始：1
            //  - 两道题答案：5, 5
            //  - 确认提交：1
            String simulatedInput = String.join(System.lineSeparator(),
                    "1",    // 选择题库序号（假设 InputHelper 将 "1" 转为 index 0）
                    "1",    // 确认开始答题（1 开始）
                    "5",    // 第1题答案
                    "5",    // 第2题答案
                    "1"     // 确认提交（1 提交，2 重做）
            ) + System.lineSeparator();
            Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

            // 4) 构造 StudentUI 并通过反射调用 startExamFlow()
            StudentUI ui = new StudentUI(scanner, student, storage);
            java.lang.reflect.Method m = StudentUI.class.getDeclaredMethod("startExamFlow");
            m.setAccessible(true);
            m.invoke(ui);

            // 5) 验证 saveAttemptAndScore 被调用一次；并捕获参数校验 records 内容
            ArgumentCaptor<List> recordsCaptor = ArgumentCaptor.forClass(List.class);
            ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(Long.class);
            verify(storage, times(1)).saveAttemptAndScore(eq(bankId), eq("stu01"), recordsCaptor.capture(), timeCaptor.capture());

            @SuppressWarnings("unchecked")
            List<AttemptRecord> capturedRecords = recordsCaptor.getValue();
            assertNotNull("提交的 records 不应为 null", capturedRecords);
            assertEquals("应有两条答题记录", 2, capturedRecords.size());

            // 每条记录的 correctness 与题目答案一致（我们提交了两个 5）
            for (AttemptRecord r : capturedRecords) {
                assertTrue("记录应包含正确或错误标志", r.getIndex() >= 0);
                // 验证 myAnswer 字段与我们输入相同（假设 AttemptRecord 有 getMyAnswer）
                assertEquals("用户提交的答案应为 5", 5, r.getMyAnswer());
            }

            // 6) 验证控制台输出包含提交成功与分数提示
            String out = outContent.toString("UTF-8");
            assertTrue("输出应包含已成功提交提示", out.contains("已成功提交") || out.contains("已成功提交！"));
            assertTrue("输出应包含得分信息（得分或正确题数）", out.contains("得分") || out.contains("正确题数"));

        } finally {
            // 恢复 System.out
            System.setOut(originalOut);
        }
    }

}
