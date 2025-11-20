package app;

import app.model.Role;
import app.model.User;
import app.service.UserService;
import app.service.WrongService;
import app.storage.FileStorage;
import app.ui.TeacherUI;
import app.ui.StudentUI;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

/**
 * 100以内加减法练习题系统的主程序，负责用户登录认证和界面路由
 */
public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		File root = new File(System.getProperty("user.dir"));
		UserService userService = new UserService(new File(root, "data/users.csv"));
		FileStorage storage = new FileStorage(root);
		WrongService wrongService = new WrongService(root);

		while (true) {
			System.out.println();
			System.out.println("===== 100以内加减法练习题系统 =====");
			System.out.print("用户名：");
			String username = scanner.nextLine().trim();
			System.out.print("密码：");
			String password = scanner.nextLine().trim();

			Optional<User> userOpt = userService.authenticate(username, password);
			if (!userOpt.isPresent()) {
				System.out.println("登录失败，用户名或密码错误。\n");
				continue;
			}

			User user = userOpt.get();
			System.out.println("登录成功，欢迎：" + user.getUsername() + "（" + user.getRole() + "）");

			if (user.getRole() == Role.TEACHER) {
				new TeacherUI(scanner, user, storage).run();
			} else {
				// ★ StudentUI 新构造方法
				new StudentUI(scanner, user, storage, wrongService).run();
			}

			System.out.println("已注销。返回登录界面。\n");
		}
	}
}
