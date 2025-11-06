package app.storage;

import app.model.AttemptRecord;
import app.model.Question;
import app.model.QuestionBank;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件存储服务类
 * 负责题库、题目、答题记录和成绩的持久化存储
 */
public class FileStorage {
	/** 数据目录 */
	private final File dataDir;
	/** 题库信息CSV文件 */
	private final File exercisesCsv;
	/** 成绩CSV文件 */
	private final File scoresCsv;
	/** 题目文件目录 */
	private final File questionsDir;
	/** 答题记录文件目录 */
	private final File attemptsDir;

	/**
	 * 构造函数
	 * 初始化文件存储路径，确保必要的目录和文件存在
	 * 
	 * @param rootDir 项目根目录
	 */
	public FileStorage(File rootDir) {
		this.dataDir = new File(rootDir, "data");
		this.exercisesCsv = new File(dataDir, "exercises.csv");
		this.scoresCsv = new File(dataDir, "scores.csv");
		this.questionsDir = new File(dataDir, "questions");
		this.attemptsDir = new File(dataDir, "attempts");
		ensureDirs();
	}

	/**
	 * 确保必要的目录和文件存在
	 * 如果目录不存在则创建，如果CSV文件不存在则创建并写入表头
	 */
	private void ensureDirs() {
		if (!dataDir.exists()) dataDir.mkdirs();
		if (!questionsDir.exists()) questionsDir.mkdirs();
		if (!attemptsDir.exists()) attemptsDir.mkdirs();
		if (!exercisesCsv.exists()) {
			writeAllLines(exercisesCsv, Collections.singletonList("id,createdAtMs,count,creator,published,publishedAtMs"));
		}
		if (!scoresCsv.exists()) {
			writeAllLines(scoresCsv, Collections.singletonList("exerciseId,student,correct,total,accuracy,submittedAtMs"));
		}
	}

	/**
	 * 加载所有题库
	 * 从exercises.csv文件中读取所有题库信息，按创建时间升序排序
	 * 
	 * @return 题库列表，按创建时间升序排序
	 */
	public List<QuestionBank> loadAllBanks() {
		List<QuestionBank> list = new ArrayList<>();
		List<String> lines = readAllLines(exercisesCsv);
		for (String line : lines) {
			if (line.startsWith("id,")) continue;
			if (line.trim().isEmpty()) continue;
			String[] a = line.split(",");
			if (a.length < 6) continue;
			String id = a[0];
			long created = parseLong(a[1]);
			int count = parseInt(a[2]);
			String creator = a[3];
			boolean published = Boolean.parseBoolean(a[4]);
			long publishedAt = parseLong(a[5]);
			list.add(new QuestionBank(id, created, count, creator, published, publishedAt));
		}
		list.sort(Comparator.comparingLong(QuestionBank::getCreatedAtMs));
		return list;
	}

	/**
	 * 加载所有已发布的题库
	 * 从所有题库中筛选出已发布的题库，按发布时间升序排序
	 * 
	 * @return 已发布的题库列表，按发布时间升序排序
	 */
	public List<QuestionBank> loadPublishedBanksAscByPublishedTime() {
		List<QuestionBank> all = loadAllBanks();
		List<QuestionBank> pub = new ArrayList<>();
		for (QuestionBank qb : all) {
			if (qb.isPublished()) pub.add(qb);
		}
		pub.sort(Comparator.comparingLong(QuestionBank::getPublishedAtMs));
		return pub;
	}

	/**
	 * 保存新创建的题库
	 * 将题库信息保存到exercises.csv，将题目保存到questions目录下的txt文件
	 * 
	 * @param bank 题库对象
	 * @param questions 题目列表
	 */
	public void saveNewBank(QuestionBank bank, List<Question> questions) {
		appendLine(exercisesCsv, String.join(",",
			bank.getId(),
			String.valueOf(bank.getCreatedAtMs()),
			String.valueOf(bank.getCount()),
			bank.getCreator(),
			String.valueOf(bank.isPublished()),
			String.valueOf(bank.getPublishedAtMs())
		));
		File qFile = new File(questionsDir, bank.getId() + ".txt");
		List<String> qs = new ArrayList<>();
		for (Question q : questions) qs.add(q.toStorageString());
		writeAllLines(qFile, qs);
	}

	/**
	 * 更新题库的发布状态
	 * 更新exercises.csv中指定题库的发布状态和发布时间
	 * 
	 * @param bankId 题库ID
	 * @param published 是否发布
	 * @param publishedAtMs 发布时间（毫秒时间戳）
	 */
	public void updateBankPublished(String bankId, boolean published, long publishedAtMs) {
		List<String> lines = readAllLines(exercisesCsv);
		List<String> out = new ArrayList<>();
		for (String line : lines) {
			if (line.startsWith("id,")) { out.add(line); continue; }
			if (line.trim().isEmpty()) continue;
			String[] a = line.split(",");
			if (a.length < 6) continue;
			if (a[0].equals(bankId)) {
				a[4] = String.valueOf(published);
				a[5] = String.valueOf(publishedAtMs);
				out.add(String.join(",", a));
			} else {
				out.add(line);
			}
		}
		writeAllLines(exercisesCsv, out);
	}

	/**
	 * 加载指定题库的所有题目
	 * 从questions目录下读取对应题库的题目文件
	 * 
	 * @param bankId 题库ID
	 * @return 题目列表
	 */
	public List<Question> loadQuestions(String bankId) {
		File qFile = new File(questionsDir, bankId + ".txt");
		List<String> lines = readAllLines(qFile);
		List<Question> out = new ArrayList<>();
		for (String s : lines) {
			if (s.trim().isEmpty()) continue;
			Question q = Question.fromStorageString(s);
			if (q != null) out.add(q);
		}
		return out;
	}

	/**
	 * 保存答题记录和成绩
	 * 将答题记录保存到attempts目录下的CSV文件，将成绩信息追加到scores.csv
	 * 
	 * @param bankId 题库ID
	 * @param student 学生用户名
	 * @param records 答题记录列表
	 */
	public void saveAttemptAndScore(String bankId, String student, List<AttemptRecord> records) {
		File aFile = new File(attemptsDir, bankId + "_" + student + ".csv");
		List<String> lines = new ArrayList<>();
		lines.add("index,myAnswer,correctAnswer,correct");
		int correct = 0;
		for (AttemptRecord r : records) {
			lines.add(r.toCsv());
			if (r.isCorrect()) correct++;
		}
		writeAllLines(aFile, lines);
		int total = records.size();
		double acc = total == 0 ? 0.0 : (correct * 1.0 / total);
		appendLine(scoresCsv, String.join(",",
			bankId,
			student,
			String.valueOf(correct),
			String.valueOf(total),
			String.format(Locale.ROOT, "%.4f", acc),
			String.valueOf(System.currentTimeMillis())
		));
	}

	/**
	 * 加载指定学生的所有成绩
	 * 从scores.csv中筛选出指定学生的成绩记录，按提交时间升序排序
	 * 
	 * @param student 学生用户名
	 * @return 成绩记录列表，每条记录为字符串数组，包含：题库ID、学生名、正确数、总数、准确率、提交时间
	 */
	public List<String[]> loadScoresByStudent(String student) {
		List<String[]> out = new ArrayList<>();
		List<String> lines = readAllLines(scoresCsv);
		for (String line : lines) {
			if (line.startsWith("exerciseId,")) continue;
			if (line.trim().isEmpty()) continue;
			String[] a = line.split(",");
			if (a.length < 6) continue;
			if (a[1].equals(student)) out.add(a);
		}
		// sort by submittedAtMs asc
		out.sort(Comparator.comparingLong(o -> parseLong(o[5])));
		return out;
	}

	/**
	 * 加载指定学生对于指定题库的答题记录
	 * 从attempts目录下读取对应的答题记录文件
	 * 
	 * @param bankId 题库ID
	 * @param student 学生用户名
	 * @return 答题记录列表
	 */
	public List<AttemptRecord> loadAttempt(String bankId, String student) {
		File aFile = new File(attemptsDir, bankId + "_" + student + ".csv");
		List<String> lines = readAllLines(aFile);
		List<AttemptRecord> out = new ArrayList<>();
		for (String line : lines) {
			if (line.startsWith("index,")) continue;
			if (line.trim().isEmpty()) continue;
			AttemptRecord r = AttemptRecord.fromCsv(line);
			if (r != null) out.add(r);
		}
		return out;
	}

	/**
	 * 读取文件的所有行
	 * 
	 * @param f 要读取的文件
	 * @return 文件的所有行，如果文件不存在或读取失败则返回空列表
	 */
	private static List<String> readAllLines(File f) {
		List<String> list = new ArrayList<>();
		if (!f.exists()) return list;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) list.add(line);
		} catch (Exception e) {
			// ignore
		}
		return list;
	}

	/**
	 * 将多行内容写入文件（覆盖模式）
	 * 
	 * @param f 目标文件
	 * @param lines 要写入的行列表
	 */
	private static void writeAllLines(File f, List<String> lines) {
		try {
			File parent = f.getParentFile();
			if (parent != null && !parent.exists()) parent.mkdirs();
			try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
				for (String s : lines) {
					bw.write(s);
					bw.newLine();
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * 追加一行内容到文件
	 * 
	 * @param f 目标文件
	 * @param line 要追加的行内容
	 */
	private static void appendLine(File f, String line) {
		try {
			File parent = f.getParentFile();
			if (parent != null && !parent.exists()) parent.mkdirs();
			try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8))) {
				bw.write(line);
				bw.newLine();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * 解析字符串为长整型
	 * 如果解析失败则返回0
	 * 
	 * @param s 要解析的字符串
	 * @return 解析得到的长整型值，失败返回0
	 */
	private static long parseLong(String s) {
		try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
	}
	
	/**
	 * 解析字符串为整型
	 * 如果解析失败则返回0
	 * 
	 * @param s 要解析的字符串
	 * @return 解析得到的整型值，失败返回0
	 */
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
	}

	/**
	 * 格式化时间戳为可读的时间字符串
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param ms 毫秒时间戳
	 * @return 格式化后的时间字符串
	 */
	public static String formatTime(long ms) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(ms));
	}
}
