package app.model;

/**
 * 用户角色枚举
 * 定义系统中的两种用户角色：教师和学生
 */
public enum Role {
	/** 教师角色，可以创建、查看和发布练习题 */
	TEACHER,
	/** 学生角色，可以参加考试和查看成绩 */
	STUDENT
}
