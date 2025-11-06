package app.service;

import app.model.Question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 题目生成器
 * 负责生成100以内的加减法题目，确保题目不重复
 */
public class QuestionGenerator {
	/** 随机数生成器 */
	private final Random random = new Random();

	/**
	 * 生成指定数量的题目
	 * 使用HashSet确保题目不重复，随机生成加法或减法题目
	 * 
	 * @param count 要生成的题目数量（1-100）
	 * @return 题目列表
	 * @throws IllegalArgumentException 如果题目数量不在1-100范围内
	 */
	public List<Question> generate(int count) {
		if (count < 1 || count > 100) throw new IllegalArgumentException("题目数量必须在1-100");
		List<Question> list = new ArrayList<>();
		Set<Question> set = new HashSet<>();
		while (list.size() < count) {
			Question q = randomQuestion();
			if (set.add(q)) {
				list.add(q);
			}
		}
		return list;
	}

	/**
	 * 随机生成一道题目
	 * 随机选择加法或减法，确保答案在0-100范围内
	 * 
	 * @return 随机生成的题目对象
	 */
	private Question randomQuestion() {
		boolean add = random.nextBoolean();
		if (add) {
			int a = random.nextInt(101);
			int b = random.nextInt(101 - a); // ensure a+b <= 100
			int ans = a + b;
			return new Question(a, b, '+', ans);
		} else {
			int a = random.nextInt(101);
			int b = random.nextInt(a + 1); // ensure a-b >= 0
			int ans = a - b;
			return new Question(a, b, '-', ans);
		}
	}
}
