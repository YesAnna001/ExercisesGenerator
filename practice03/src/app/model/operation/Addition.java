package app.model.operation;

// 加法算式--继承于二元运算抽象类
public class Addition extends BinaryOperation {
    
    // 构造函数
    public Addition(int left, int right, int answer) {
        super(left, right, '+', answer);
    }

    // 重写方法：计算一道整数加法算式的结果，返回该结果
    @Override
    public int calculate() {
        return left + right;
    }

    // 重写方法：验证一道加法算式是否有效
    @Override
    public boolean isValid() {
        return (left + right) <= 100 && left >=0 && right >=0;
    }

    
}
