package app.model;

/**
 * 题库实体类
 * 表示一个练习题题库，包含题库ID、创建时间、题目数量、创建者以及发布状态等信息
 */
public class QuestionBank {
	/** 题库唯一标识符，基于时间戳生成 */
	private final String id;
	/** 创建时间（毫秒时间戳） */
	private final long createdAtMs;
	/** 题目数量 */
	private final int count;
	/** 创建者用户名 */
	private final String creator;
	/** 是否已发布 */
	private boolean published;
	/** 发布时间（毫秒时间戳），如果未发布则为0 */
	private long publishedAtMs;
	/** 习题集类型 */
	private final String type;

	/**
	 * 构造函数
	 * 
	 * @param id 题库唯一标识符
	 * @param createdAtMs 创建时间（毫秒时间戳）
	 * @param count 题目数量
	 * @param creator 创建者用户名
	 * @param published 是否已发布
	 * @param publishedAtMs 发布时间（毫秒时间戳）
	 * @param type 题库类型(add/sub/mix)
	 */
	public QuestionBank(String id, long createdAtMs, int count, String creator, boolean published, long publishedAtMs,String type) {
		this.id = id;
		this.createdAtMs = createdAtMs;
		this.count = count;
		this.creator = creator;
		this.published = published;
		this.publishedAtMs = publishedAtMs;
		this.type = type;
	}

	/**
	 * 获取题库ID
	 * 
	 * @return 题库唯一标识符
	 */
	public String getId() { return id; }
	
	/**
	 * 获取创建时间
	 * 
	 * @return 创建时间（毫秒时间戳）
	 */
	public long getCreatedAtMs() { return createdAtMs; }
	
	/**
	 * 获取题目数量
	 * 
	 * @return 题目数量
	 */
	public int getCount() { return count; }
	
	/**
	 * 获取创建者
	 * 
	 * @return 创建者用户名
	 */
	public String getCreator() { return creator; }
	

	/**
	 * 获取该题库习题类型  
	 * @return
	 */
	public String getType(){ return type; }

	/**
	 * 判断是否已发布
	 * 
	 * @return 如果已发布返回true，否则返回false
	 */
	public boolean isPublished() { return published; }
	
	/**
	 * 获取发布时间
	 * 
	 * @return 发布时间（毫秒时间戳）
	 */
	public long getPublishedAtMs() { return publishedAtMs; }

	/**
	 * 设置发布状态
	 * 
	 * @param published 是否已发布
	 */
	public void setPublished(boolean published) { this.published = published; }
	
	/**
	 * 设置发布时间
	 * 
	 * @param publishedAtMs 发布时间（毫秒时间戳）
	 */
	public void setPublishedAtMs(long publishedAtMs) { this.publishedAtMs = publishedAtMs; }

	/**
	 * 获取格式化后的习题类型名称（对外使用）
	 */
	public String getTypeDisplayName() {
		switch (type) {
			case "add": return "加法专项练习";
			case "sub": return "减法专项练习";
			case "mix": return "加减混合练习";
			default:    return "未知题型";
		}
	}
}
