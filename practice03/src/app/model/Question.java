package app.model;

/**
 * 题目实体类
 * 表示一道100以内的加减法题目，包含左操作数、右操作数、运算符和答案
 */
public class Question {
	/** 左操作数 */
	private final int left;
	/** 右操作数 */
	private final int right;
	/** 运算符，'+' 表示加法，'-' 表示减法 */
	private final char op;
	/** 正确答案 */
	private final int answer;

	/**
	 * 构造函数
	 * 
	 * @param left 左操作数
	 * @param right 右操作数
	 * @param op 运算符（'+' 或 '-'）
	 * @param answer 正确答案
	 */
	public Question(int left, int right, char op, int answer) {
		this.left = left;
		this.right = right;
		this.op = op;
		this.answer = answer;
	}

	/**
	 * 获取左操作数
	 * 
	 * @return 左操作数
	 */
	public int getLeft() { return left; }
	
	/**
	 * 获取右操作数
	 * 
	 * @return 右操作数
	 */
	public int getRight() { return right; }
	
	/**
	 * 获取运算符
	 * 
	 * @return 运算符（'+' 或 '-'）
	 */
	public char getOp() { return op; }
	
	/**
	 * 获取正确答案
	 * 
	 * @return 正确答案
	 */
	public int getAnswer() { return answer; }

	/**
	 * 转换为显示字符串
	 * 格式：左操作数 运算符 右操作数 = 
	 * 
	 * @return 显示用的字符串，例如 "5 + 3 = "
	 */
	public String toDisplayString() {
		return left + " " + op + " " + right + " = ";
	}

	/**
	 * 转换为存储字符串
	 * 格式：左操作数,运算符,右操作数,答案
	 * 
	 * @return 存储用的CSV格式字符串，例如 "5,+,3,8"
	 */
	public String toStorageString() {
		return left + "," + op + "," + right + "," + answer;
	}

	/**
	 * 从存储字符串解析题目对象
	 * 
	 * @param s CSV格式的字符串，格式为：左操作数,运算符,右操作数,答案
	 * @return 解析得到的题目对象，如果格式不正确则返回null
	 */
	public static Question fromStorageString(String s) {
		String[] parts = s.split(",");
		if (parts.length != 4) return null;
		int l = Integer.parseInt(parts[0]);
		char o = parts[1].charAt(0);
		int r = Integer.parseInt(parts[2]);
		int a = Integer.parseInt(parts[3]);
		return new Question(l, r, o, a);
	}

	/**
	 * 计算哈希值
	 * 用于在HashSet中判断题目是否重复
	 * 
	 * @return 哈希值
	 */
	@Override
	public int hashCode() {
		int h = 17;
		h = 31 * h + left;
		h = 31 * h + right;
		h = 31 * h + (int) op;
		h = 31 * h + answer;
		return h;
	}

	/**
	 * 判断两个题目是否相等
	 * 比较左操作数、右操作数、运算符和答案是否完全相同
	 * 
	 * @param obj 要比较的对象
	 * @return 如果两个题目完全相同则返回true，否则返回false
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Question q = (Question) obj;
		return left == q.left && right == q.right && op == q.op && answer == q.answer;
	}
}
