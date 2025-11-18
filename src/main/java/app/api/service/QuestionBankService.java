package app.api.service;

import app.model.QuestionBank;
import app.model.exercise.AdditionExercise;
import app.model.exercise.Exercise;
import app.model.exercise.MixedExercise;
import app.model.exercise.SubtractionExercise;
import app.model.operation.BinaryOperation;
import app.storage.FileStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class QuestionBankService {

    // 注入实例
    private final FileStorage storage;

    // 构造器注入
    public QuestionBankService(FileStorage storage) {
        this.storage = storage;
    }


    /**
     * 生成题库并保存成文件
     * @param type 题库类型 add/sub/mix
     * @param count 题库内题目数量
     * @param creator 创建者
     * @return 返回一个题库bank
     */
    public QuestionBank createByType(String type, int count, String creator) {
        Exercise ex;
        // 选择题型创建不同习题集
        switch (type) {
            case "add": ex = new AdditionExercise(); break;
            case "sub": ex = new SubtractionExercise(); break;
            case "mix": ex = new MixedExercise(); break;
            default: throw new IllegalArgumentException("未知题型 " + type);
        }
        // 开始生成题目
        ex.generateExercise(count);
        // 获取题目列表
        List<BinaryOperation> problems = ex.getProblems();

        String id = creator + "_" + System.currentTimeMillis();
        QuestionBank bank = new QuestionBank(id, System.currentTimeMillis(), problems.size(), creator, false, 0L, type);

        // 使用实例方法保存
        storage.saveNewBank(bank, problems);

        // 返回题库
        return bank;
    }


    /**
     * 从上传的 CSV 导入题库（multipart file），并保存为题库
     */
    public QuestionBank importFromCsv(MultipartFile file, String creator) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }

        // 创建临时文件并把上传内容写入其中
        File tmp = File.createTempFile("import-", ".csv");
        try {
            file.transferTo(tmp);

            // 使用实例方法读取 CSV（注意：loadFromCSV 需为实例方法）
            List<BinaryOperation> problems = storage.loadFromCSV(tmp.getAbsolutePath());
            if (problems == null || problems.isEmpty()) {
                throw new IllegalArgumentException("CSV 文件无题目或格式错误");
            }

            // 使用原始文件名作为 type（可按需去掉扩展名）
            String type = file.getOriginalFilename() != null ? file.getOriginalFilename() : "CSV导入";
            String id = creator + "_" + System.currentTimeMillis();
            QuestionBank bank = new QuestionBank(id, System.currentTimeMillis(), problems.size(), creator, false, 0L, type);

            // 保存题库（实例调用）
            storage.saveNewBank(bank, problems);
            return bank;
        } finally {
            // 尝试删除临时文件（安全回退）
            if (tmp.exists()) {
                try { tmp.delete(); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * 获取题库题目
     */
    public List<BinaryOperation> getQuestions(String id) {
        return storage.loadQuestions(id);
    }

    /**
     * 发布题库
     */
    public void publish(String id) {
        storage.updateBankPublished(id, true, System.currentTimeMillis());
    }
}
