package app.model.operation;

/**
 * 二元运算抽象类
 * 表示一个二元算式，包含左操作数、右操作数、运算符和答案
 */
public abstract class BinaryOperation {
    /** 左操作数 */
    protected final int left;
    /** 右操作数 */
    protected final int right;
    /** 运算符，例如 '+' 或 '-' */
    protected final char op;
    /** 答案，由子类构造时计算 */
    protected final int answer;

    /**
     * 构造函数
     *
     * @param left 左操作数
     * @param right 右操作数
     * @param op 运算符
     * @param answer 该算式的答案
     */
    public BinaryOperation(int left, int right, char op, int answer) {
        this.left = left;
        this.right = right;
        this.op = op;
        this.answer = answer;
    }

    /** 获取运算符 */
    public char getOp() {
        return op;
    }

    /** 获取答案 */
    public int getAnswer() {
        return answer;
    }

    /** 验证题目是否合法（由子类实现） */
    public abstract boolean isValid();

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
     * CSV格式：左操作数,运算符,右操作数,答案
     *
     * @return 存储用的字符串，例如 "5,+,3,8"
     */
    public String toStorageString() {
        return left + "," + op + "," + right + "," + answer;
    }


	// /**
	//  * 从存储字符串解析加法题目对象
	//  * 
	//  * @param s CSV格式的字符串，格式为：左操作数,运算符,右操作数,答案
	//  * @return 解析得到的题目对象，如果格式不正确则返回null
	// */
	public static BinaryOperation fromStorageString(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }

        try {
			String[] parts = s.split(",");
			if (parts.length != 4) return null;
			int left = Integer.parseInt(parts[0]);
			char operator = parts[1].charAt(0);
			int right = Integer.parseInt(parts[2]);
			int answer = Integer.parseInt(parts[3]);
            switch (operator) {
                case '+':
                    return new Addition(left, right,answer);
                case '-':
					return new Subtraction(left, right,answer);
                default:	
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 计算哈希值，用于 HashSet 判断重复
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
     * 比较左操作数、右操作数、运算符和答案
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BinaryOperation q = (BinaryOperation) obj;
        return left == q.left && right == q.right && op == q.op && answer == q.answer;
    }
}


