package app.util;

public class TimeUtils {

    /** 
     * 将秒数格式化为 hh小时 mm分钟 ss秒
     * @param seconds 总秒数
     * @return 格式化字符串
     */
    public static String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d小时%02d分钟%02d秒", h, m, s);
    }
}
