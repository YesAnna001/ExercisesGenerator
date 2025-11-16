package app.model.operation;

public class Subtraction extends BinaryOperation {

    // 构造函数
    public Subtraction(int left, int right, int answer) {
        super(left, right, '-', answer);
    }


    // 重写方法：验证一道减法算式a是否有效
    // 要求：相减不能小于0 ，两个操作数在1-100以内
    @Override
    public boolean isValid() {
        return left >= right && left <=100 && right >=0;
    }   



}
