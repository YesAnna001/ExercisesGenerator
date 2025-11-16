package app.service;

import app.model.Role;
import app.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 负责用户信息的加载和认证
 */
public class UserService {
	/** 用户数据文件 */
	private final File usersFile;
	/** 用户列表 */
	private final List<User> users = new ArrayList<>();

	/**
	 * 构造函数
	 * 初始化用户服务，从文件中加载用户数据
	 * 
	 * @param usersFile 用户数据文件路径
	 */
	public UserService(File usersFile) {
		this.usersFile = usersFile;
		load();
	}

	/**
	 * 从文件加载用户数据
	 * 读取CSV格式的用户文件，解析用户信息并加载到内存中
	 * CSV格式：角色,用户名,密码
	 */
	private void load() {
		if (!usersFile.exists()) return;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(usersFile), StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				if (line.startsWith("role")) continue;
				String[] arr = line.split(",");
				if (arr.length < 3) continue;
				Role role = Role.valueOf(arr[0].trim());
				String username = arr[1].trim();
				String password = arr[2].trim();
				users.add(new User(username, password, role));
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * 用户认证
	 * 根据用户名和密码验证用户身份
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @return 如果认证成功返回用户对象，否则返回空的Optional
	 */
	public Optional<User> authenticate(String username, String password) {
		return users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst();
	}
}
