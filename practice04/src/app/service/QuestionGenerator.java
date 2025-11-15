package app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import app.model.operation.Addition;
import app.model.operation.BinaryOperation;
import app.model.operation.Subtraction;

/**
 * 题目生成器
 * 负责生成100以内的加减法题目，确保题目不重复
 */
public class QuestionGenerator {
	/** 随机数生成器 */
	private final Random random = new Random();


	/**
	 * 随机生成一道加法/减法题目
	 * 随机选择加法或减法，确保答案在0-100范围内
	 * 
	 * @return 随机生成的题目对象
	 */
	private BinaryOperation randomQuestion() {
		boolean add = random.nextBoolean();
		if (add) {
			int a = random.nextInt(101);
			int b = random.nextInt(101 - a); // 确保 a+b <= 100
			int ans = a + b;
			return new Addition(a, b, ans);
		} else {
			int a = random.nextInt(101);
			int b = random.nextInt(a + 1); // 确保 a-b >= 0
			int ans = a - b;
			return new Subtraction(a, b, ans);
		}
	}



	/**
	 * 生成指定数量的加/减题目
	 * 使用HashSet确保题目不重复，随机生成加法或减法题目
	 * 
	 * @param count 要生成的题目数量（1-100）
	 * @return 题目列表
	 * @throws IllegalArgumentException 如果题目数量不在1-100范围内
	 */
	public List<BinaryOperation> generateMixedQuestions(int count) {
		if (count < 1 || count > 100) throw new IllegalArgumentException("题目数量必须在1-100");
		List<BinaryOperation> list = new ArrayList<>();
		Set<BinaryOperation> set = new HashSet<>();
		while (list.size() < count) {
			BinaryOperation q = randomQuestion();
			if (set.add(q)) {
				list.add(q);
			}
		}
		return list;
	}




}
