package app.model.operation;

// 加法算式--继承于二元运算抽象类
public class Addition extends BinaryOperation {
    
    // 构造函数
    public Addition(int left, int right, int answer) {
        super(left, right, '+', answer);
    }

    // 不用传入答案的构造函数
    public Addition(int left, int right) {
        super(left, right, '+'); // 传入运算符
    }

    @Override
    protected int calculateAnswer() {
        return left + right;
    }

    // 重写方法：验证一道加法算式是否有效
    @Override
    public boolean isValid() {
        return (left + right) <= 100 && left >=0 && right >=0;
    }

    
}
