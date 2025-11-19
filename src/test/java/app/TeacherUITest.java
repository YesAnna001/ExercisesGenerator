//package app;
//
//import app.model.QuestionBank;
//import app.model.Role;
//import app.model.User;
//import app.storage.FileStorage;
//import app.ui.TeacherUI;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.mockito.Mockito.*;
//
//public class TeacherUITest {
//
//    @Test
//    public void testViewBanks() {
//        // 1. 模拟教师用户
//        User teacher = new User("teacher1", "pwd", Role.TEACHER);
//
//        // 2. 模拟 FileStorage
//        FileStorage fileStorage = mock(FileStorage.class);
//
//        // 创建假题库列表
//        QuestionBank bank = new QuestionBank("id1", System.currentTimeMillis(), 5, "teacher1", false, 0L, "add");
//        List<QuestionBank> banks = Collections.singletonList(bank);
//
//        // 当调用 loadAllBanks 时返回假题库
//        when(fileStorage.loadAllBanks()).thenReturn(banks);
//
//        // 当调用 loadQuestions 时返回一些假题
//        when(fileStorage.loadQuestions("id1")).thenReturn(Collections.emptyList());
//
//        // 3. 模拟输入流
//        // "2" -> 选择菜单“查看练习题”
//        // "0" -> 选择第0个题库查看详情
//        // "\n" -> 回车返回
//        // "4" -> 退出教师菜单
//        String simulatedInput = "2\n0\n\n4\n";
//        Scanner scanner = new Scanner(simulatedInput);
//
//        // 4. 创建 TeacherUI
//        TeacherUI teacherUI = new TeacherUI(scanner, teacher, fileStorage);
//
//        // 5. 运行 UI
//        teacherUI.run();
//
//        // 6. 验证方法调用
//        verify(fileStorage).loadAllBanks();
//        verify(fileStorage).loadQuestions("id1");
//    }
//}
