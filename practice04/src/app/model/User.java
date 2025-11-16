package app.model;

/**
 * 用户实体类
 * 表示系统中的用户，包含用户名、密码和角色信息
 */
public class User {
	/** 用户名 */
	private final String username;
	/** 密码 */
	private final String password;
	/** 用户角色（教师或学生） */
	private final Role role;

	/**
	 * 构造函数
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @param role 用户角色
	 */
	public User(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	/**
	 * 获取用户名
	 * 
	 * @return 用户名
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 获取密码
	 * 
	 * @return 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 获取用户角色
	 * 
	 * @return 用户角色
	 */
	public Role getRole() {
		return role;
	}
}
