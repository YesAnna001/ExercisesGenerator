package app.api.dto;

import app.model.QuestionBank;

/**
 * 题库信息
 * 用于对外返回简洁题库信息
 */
public class QuestionBankDto {
    public String id;
    public long createdAtMs;
    public int count;
    public String creator;
    public boolean published;
    public long publishedAtMs;
    public String type;

    public QuestionBankDto() {}

    public static QuestionBankDto from(QuestionBank bank) {
        if (bank == null) return null;
        QuestionBankDto d = new QuestionBankDto();
        d.id = bank.getId();
        d.createdAtMs = bank.getCreatedAtMs();
        d.count = bank.getCount();
        d.creator = bank.getCreator();
        d.published = bank.isPublished();
        d.publishedAtMs = bank.getPublishedAtMs();
        d.type = bank.getType(); // 直接返回原生type（文件名或 add/sub/mix）
        return d;
    }
}
