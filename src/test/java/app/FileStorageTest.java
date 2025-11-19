package app;
import app.model.AttemptRecord;
import app.model.QuestionBank;
import app.model.operation.Addition;
import app.model.operation.BinaryOperation;
import app.model.operation.Subtraction;
import app.storage.FileStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 针对 FileStorage 的单元测试
 * - 使用临时目录作为 rootDir，测试文件读写行为
 * - 覆盖 CSV 读取、题库保存/加载、答题记录与成绩保存/加载等功能
 */
public class FileStorageTest {

    private Path tempRoot;
    private FileStorage storage;

    // 创建临时目录作为根目录
    @Before
    public void setUp() throws Exception {
        tempRoot = Files.createTempDirectory("filestorage_test_");
        storage = new FileStorage(tempRoot.toFile());
    }

    // 删除临时目录（递归）
    @After
    public void tearDown() throws Exception {
        if (tempRoot != null && Files.exists(tempRoot)) {
            Files.walk(tempRoot)
                    .sorted((a, b) -> b.compareTo(a)) // 反序删除文件夹内文件
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                    });
        }
    }


    /**
     * 测试 loadFromCSV 能正确解析简单的 CSV（加法/减法）
     */
    @Test
    public void testLoadFromCSV_parsesAddAndSub() throws Exception {
        Path csv = tempRoot.resolve("sample_ops.csv");
        List<String> lines = new ArrayList<>();
        lines.add("2,+,3");
        lines.add("7,-,2");
        Files.write(csv, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        List<BinaryOperation> ops = FileStorage.loadFromCSV(csv.toString());
        assertEquals("应解析两条记录", 2, ops.size());
        assertTrue("第一条应为 Addition", ops.get(0) instanceof Addition);
        assertTrue("第二条应为 Subtraction", ops.get(1) instanceof Subtraction);
        assertEquals("第一条答案正确", 5, ops.get(0).getAnswer());
        assertEquals("第二条答案正确", 5, ops.get(1).getAnswer());
    }

    /**
     * 测试 saveNewBank 后 exercises.csv 被追加，questions 文件生成且内容正确
     */
    @Test
    public void testSaveNewBank_andLoadQuestions() throws Exception {
        String bankId = "B_TEST_1";
        QuestionBank qb = new QuestionBank(bankId, System.currentTimeMillis(), 2, "teacherA", false, 0L, "mix");

        List<BinaryOperation> questions = new ArrayList<>();
        questions.add(new Addition(1, 2, 3));
        questions.add(new Subtraction(10, 4, 6));

        storage.saveNewBank(qb, questions);

        // exercises.csv 应包含 bankId 的一行
        Path exercisesCsv = tempRoot.resolve("data").resolve("exercises.csv");
        List<String> exLines = Files.readAllLines(exercisesCsv, StandardCharsets.UTF_8);
        boolean found = exLines.stream().anyMatch(l -> l.startsWith(bankId + ","));
        assertTrue("exercises.csv 应包含新题库的元数据行", found);

        // questions 文件应存在并包含两行
        Path qFile = tempRoot.resolve("data").resolve("questions").resolve(bankId + ".txt");
        assertTrue("题目文件应被创建", Files.exists(qFile));
        List<String> qLines = Files.readAllLines(qFile, StandardCharsets.UTF_8);
        assertEquals("题目文件应包含两行", 2, qLines.size());
    }

    /**
     * 测试 updateBankPublished 与 loadPublishedBanksAscByPublishedTime 的联合行为
     * 通过创建两个题库并分别设置不同的 publishedAtMs，检查排序
     */
    @Test
    public void testUpdatePublishedAndLoadPublishedAsc() throws Exception {
        // 先创建两个题库
        String b1 = "BANK_A";
        String b2 = "BANK_B";
        QuestionBank qb1 = new QuestionBank(b1, System.currentTimeMillis() - 1000, 1, "t1", false, 0L, "mix");
        QuestionBank qb2 = new QuestionBank(b2, System.currentTimeMillis(), 1, "t2", false, 0L, "mix");

        List<BinaryOperation> q = new ArrayList<>();
        q.add(new Addition(1,1,2));
        storage.saveNewBank(qb1, q);
        Thread.sleep(3); // 保证时间差异
        storage.saveNewBank(qb2, q);

        // 设置发布时间，b2 先发布（较小时间），b1 后发布（较大时间）
        long t1 = System.currentTimeMillis() + 10000; // later
        long t2 = System.currentTimeMillis() + 1;     // earlier

        storage.updateBankPublished(b1, true, t1);
        storage.updateBankPublished(b2, true, t2);

        List<QuestionBank> pubs = storage.loadPublishedBanksAscByPublishedTime();
        assertEquals("应返回两个已发布的题库", 2, pubs.size());
        assertEquals("按 publishedAt 升序，第一项应为 b2", b2, pubs.get(0).getId());
        assertEquals("第二项应为 b1", b1, pubs.get(1).getId());
    }

    /**
     * 测试 saveAttemptAndScore 会创建 attempts 文件并向 scores.csv 追加记录，
     * 并测试 loadAttempt(..., submittedAtMs) 与 loadScoresByStudent 能够读取对应数据。
     */
    @Test
    public void testSaveAttemptAndLoadAttemptAndScores() throws Exception {
        String bankId = "BANK_SCORE";
        String student = "stu01";

        // 构造两条答题记录
        List<AttemptRecord> records = new ArrayList<>();
        records.add(new AttemptRecord(0, 1, 2, false));
        records.add(new AttemptRecord(1, 3, 3, true));

        // 保存（saveAttemptAndScore 内部使用当前时间作为 submittedAt）
        storage.saveAttemptAndScore(bankId, student, records, 123L);

        // attempts 目录下应存在以 bankId_student_ 开头的文件
        Path attemptsDir = tempRoot.resolve("data").resolve("attempts");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(attemptsDir, bankId + "_" + student + "_*.csv")) {
            Path found = null;
            for (Path p : ds) { found = p; break; }
            assertNotNull("应找到 attempts 文件（包含时间戳）", found);

            // 读取 attempts 文件内容，第一行为表头，下面为记录
            List<String> lines = Files.readAllLines(found, StandardCharsets.UTF_8);
            assertTrue("attempts 首行为 header", lines.get(0).startsWith("index,myAnswer,correctAnswer,correct"));
            assertEquals("attempts 应包含 header + 2 条记录", 3, lines.size());

            // 提取时间戳并测试 loadAttempt(bankId, student, submittedAtMs)
            String filename = found.getFileName().toString(); // 格式 bank_student_ts.csv
            String tsPart = filename.substring((bankId + "_" + student + "_").length(), filename.length() - 4);
            long ts = Long.parseLong(tsPart);

            List<AttemptRecord> loaded = storage.loadAttempt(bankId, student, ts);
            assertEquals("loadAttempt by timestamp 应返回两条记录", 2, loaded.size());
        }

        // scores.csv 中应包含一行 student 的成绩记录
        Path scoresCsv = tempRoot.resolve("data").resolve("scores.csv");
        List<String> scoreLines = Files.readAllLines(scoresCsv, StandardCharsets.UTF_8);
        boolean matched = scoreLines.stream().anyMatch(l -> l.contains(bankId + "," + student + ","));
        assertTrue("scores.csv 应包含本次提交的成绩记录", matched);

        // 使用 loadScoresByStudent 应能检索到至少一条记录
        List<String[]> rows = storage.loadScoresByStudent(student);
        assertTrue("loadScoresByStudent 应返回非空列表", rows.size() >= 1);
    }

    /**
     * 测试 loadQuestions：先保存题库文件，然后通过 loadQuestions 读取并解析
     */
    @Test
    public void testLoadQuestions() throws Exception {
        String bankId = "LOAD_QS";
        // 手工创建 questions 文件
        Path qFile = tempRoot.resolve("data").resolve("questions").resolve(bankId + ".txt");
        List<String> lines = new ArrayList<>();
        lines.add(new Addition(2, 3, 5).toStorageString());
        lines.add(new Subtraction(9, 4, 5).toStorageString());
        Files.write(qFile, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        List<BinaryOperation> loaded = storage.loadQuestions(bankId);
        assertEquals("loadQuestions 应返回 2 道题", 2, loaded.size());
        assertTrue("第一题应为 Addition", loaded.get(0) instanceof Addition);
        assertTrue("第二题应为 Subtraction", loaded.get(1) instanceof Subtraction);
    }
}

