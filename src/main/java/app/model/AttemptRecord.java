package app.model;

/**
 * 答题记录实体类
 * 表示学生对[一道题]的答题记录，包含题目索引、学生答案、正确答案和是否正确
 */
public class AttemptRecord {
	/** 题目索引（从0开始） */
	private final int index;
	/** 学生的答案 */
	private final int myAnswer;
	/** 正确答案 */
	private final int correctAnswer;
	/** 是否回答正确 */
	private final boolean correct;
	/**
	 * 构造函数
	 * 
	 * @param index 题目索引（从0开始）
	 * @param myAnswer 学生的答案
	 * @param correctAnswer 正确答案
	 * @param correct 是否回答正确
	 */
	public AttemptRecord(int index, int myAnswer, int correctAnswer, boolean correct) {
		this.index = index;
		this.myAnswer = myAnswer;
		this.correctAnswer = correctAnswer;
		this.correct = correct;
	}

	/**
	 * 获取题目索引
	 * 
	 * @return 题目索引（从0开始）
	 */
	public int getIndex() { return index; }
	
	/**
	 * 获取学生答案
	 * 
	 * @return 学生的答案
	 */
	public int getMyAnswer() { return myAnswer; }
	
	/**
	 * 获取正确答案
	 * 
	 * @return 正确答案
	 */
	public int getCorrectAnswer() { return correctAnswer; }
	
	/**
	 * 判断是否回答正确
	 * 
	 * @return 如果回答正确返回true，否则返回false
	 */
	public boolean isCorrect() { return correct; }

	/**
	 * 转换为CSV格式字符串
	 * 格式：索引,学生答案,正确答案,是否正确
	 * 
	 * @return CSV格式的字符串
	 */
	public String toCsv() {
		return index + "," + myAnswer + "," + correctAnswer + "," + correct;
	}

	/**
	 * 从CSV格式字符串解析答题记录对象
	 * 
	 * @param s CSV格式的字符串，格式为：索引,学生答案,正确答案,是否正确
	 * @return 解析得到的答题记录对象，如果格式不正确则返回null
	 */
	public static AttemptRecord fromCsv(String s) {
		String[] arr = s.split(",");
		if (arr.length != 4) return null;
		int idx = Integer.parseInt(arr[0]);
		int my = Integer.parseInt(arr[1]);
		int ans = Integer.parseInt(arr[2]);
		boolean ok = Boolean.parseBoolean(arr[3]);
		return new AttemptRecord(idx, my, ans, ok);
	}
}
