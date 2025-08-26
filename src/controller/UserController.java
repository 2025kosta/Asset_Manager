package controller;

import java.util.Scanner;

import domain.Users;
import service.UserService;

public class UserController {
	private final Scanner scanner;
	private final UserService userService;
	private Users currentUser;

	public UserController(Scanner scanner) {
		this.scanner = scanner;
		this.userService = new UserService();
	}

	public void createUser() {
		System.out.print("이름: ");
		String name = scanner.nextLine();
		System.out.print("이메일: ");
		String email = scanner.nextLine();

		String resultMsg = userService.createUser(name, email);
		System.out.println(resultMsg);
	}

	public boolean login() {
		System.out.print("이메일: ");
		String email = scanner.nextLine();
		Users user = userService.findByEmail(email);

		if (user == null) {
			System.out.println("❌ 해당 이메일의 사용자가 없습니다.");
			return false;
		}

		currentUser = user;
		System.out.println("\n환영합니다, " + user.getName() + "님!");
		return true;
	}

	public Users getCurrentUser() {
		return currentUser;
	}

}
