package app.service;

import app.model.operation.BinaryOperation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 错题服务
 *
 * - 所有错题相关文件统一放在 data/wrongs/ 目录下
 * - 错题文件： data/wrongs/{bankId}_{student}_{createdAt}.txt  存储具体错题题目
 * - 索引文件： data/wrongs/wrongs.csv   （每行：bankId,student,count,createdAt） 错题相关信息
 */
public class WrongService {

    private final File rootDir;    // 程序根目录
    private final File wrongsDir;  // data/wrongs 目录

    public WrongService(File rootDir) { // 加载或创建所有必须的目录和文件
        this.rootDir = rootDir;  // 保存根目录
        File dataDir = new File(rootDir, "data"); // data 目录
        if (!dataDir.exists()) dataDir.mkdirs(); // 不存在就创建
        this.wrongsDir = new File(dataDir, "wrongs"); // data/wrongs 目录
        if (!this.wrongsDir.exists()) this.wrongsDir.mkdirs(); // 不存在就创建
        File idx = new File(this.wrongsDir, "wrongs.csv"); // 索引文件
        if (!idx.exists()) writeAllLines(idx, Collections.emptyList()); // 不存在就创建空文件
    }

    /**
     * 保存错题（覆盖旧文件并更新索引）
     */
    public void saveWrongExercises(String bankId, String student, List<BinaryOperation> wrongs) {
        deleteWrongFilesFor(bankId, student); // 删除旧文件

        if (wrongs == null || wrongs.isEmpty()) return; // 如果没有错题直接返回

        long createdAt = System.currentTimeMillis(); // 当前时间戳
        File outFile = new File(wrongsDir, bankId + "_" + student + "_" + createdAt + ".txt"); // 输出文件

        // 去重写入
        Set<String> set = new LinkedHashSet<>();
        for (BinaryOperation q : wrongs) {
            if (q != null) set.add(q.toStorageString()); // 转为存储字符串并去重
        }
        writeAllLines(outFile, new ArrayList<>(set)); // 写入文件

        // 更新索引文件
        File indexFile = new File(wrongsDir, "wrongs.csv");
        List<String> idxLines = readAllLines(indexFile);
        List<String> out = new ArrayList<>();
        for (String l : idxLines) {
            if (l.trim().isEmpty()) continue;
            String[] a = l.split(",");
            if (a.length < 4) continue;
            if (a[0].equals(bankId) && a[1].equals(student)) continue; // 跳过旧条目
            out.add(l); // 保留其他条目
        }
        // 添加新条目
        out.add(String.join(",", bankId, student, String.valueOf(set.size()), String.valueOf(createdAt)));
        writeAllLines(indexFile, out); // 写回索引文件
    }

    /**
     * 读取指定学生的所有错题（按 bankId 聚合并去重）
     */
    public Map<String, List<BinaryOperation>> loadAllWrongExercises(String student) {
        Map<String, List<BinaryOperation>> out = new LinkedHashMap<>(); // 保存结果
        File indexFile = new File(wrongsDir, "wrongs.csv"); // 索引文件
        List<String> lines = readAllLines(indexFile); // 读取索引
        if (lines.isEmpty()) return out; // 空索引直接返回

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] a = line.split(",");
            if (a.length < 4) continue;
            String bankId = a[0];
            String owner = a[1];
            String createdAt = a[3];
            if (!owner.equals(student)) continue;

            File wrongsFile = new File(wrongsDir, bankId + "_" + owner + "_" + createdAt + ".txt"); // 错题文件
            List<String> qlines = readAllLines(wrongsFile); // 读取错题文件
            if (qlines.isEmpty()) continue;

            // 合并到 Map，去重
            List<BinaryOperation> list = out.computeIfAbsent(bankId, k -> new ArrayList<>());
            Set<String> seen = new LinkedHashSet<>(); // 局部去重
            for (String s : qlines) {
                if (s == null) continue;
                s = s.trim();
                if (s.isEmpty()) continue;
                if (!seen.add(s)) continue;
                BinaryOperation q = BinaryOperation.fromStorageString(s);
                if (q != null) list.add(q);
            }
        }
        return out;
    }

    /**
     * 清空某学生某题库的错题（删除对应文件并更新索引）
     */
    public void clearWrongExercises(String bankId, String student) {
        deleteWrongFilesFor(bankId, student); // 删除文件

        // 更新索引
        File indexFile = new File(wrongsDir, "wrongs.csv");
        List<String> lines = readAllLines(indexFile);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] a = line.split(",");
            if (a.length < 4) continue;
            if (a[0].equals(bankId) && a[1].equals(student)) continue; // 跳过被删除的
            out.add(line); // 其他保留
        }
        writeAllLines(indexFile, out); // 写回索引
    }

    /**
     * 删除某题库某学生的所有错题文件（不删除索引）
     */
    private void deleteWrongFilesFor(String bankId, String student) {
        File[] files = wrongsDir.listFiles();
        if (files == null) return;
        for (File f : files) {
            String name = f.getName();
            if (name.startsWith(bankId + "_" + student + "_") && name.endsWith(".txt")) {
                try { f.delete(); } catch (Exception ignored) {} // 删除文件
            }
        }
    }

    /**
     * 读取文件所有行，并把每一行作为元素添加进list
     */
    private static List<String> readAllLines(File f) {
        List<String> list = new ArrayList<>();
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) list.add(line);
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * 写入文件所有行
     */
    private static void writeAllLines(File f, List<String> lines) {
        try {
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs(); // 创建父目录
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
                for (String s : lines) {
                    bw.write(s); // 写入每一行
                    bw.newLine(); // 换行
                }
            }
        } catch (Exception e) {
        }
    }
}
