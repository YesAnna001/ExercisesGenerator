package app;

import app.util.ColorTextUtil;
import org.junit.Test;

/**
 * 颜色测试
 */
public class ColorTextTest {


    @Test
    public void ColorTest(){
        System.out.println(ColorTextUtil.color("黑色测试文本","black"));
        System.out.println(ColorTextUtil.color("红色测试文本","red"));;
        System.out.println(ColorTextUtil.color("绿色测试文本","green"));;
        System.out.println(ColorTextUtil.color("黄色测试文本","yellow"));;
        System.out.println(ColorTextUtil.color("蓝色测试文本","blue"));;
        System.out.println(ColorTextUtil.color("紫红色测试文本","purplish-red"));;
        System.out.println(ColorTextUtil.color("青蓝色测试文本","Cyan"));;
        System.out.println(ColorTextUtil.color("白色测试文本","white"));;
    }
}
