package controller;

import java.util.Scanner;

import domain.Users;
import service.AssetService;
import service.CategoryService;
import service.TransactionService;
import service.UserService;

public class UserController {
	private final Scanner scanner;
	private final UserService userService;
	private final AssetService assetService;
	private Users currentUser;
	private final CategoryService categoryService;
	private final TransactionService transactionService;

	// 간소 생성사 - 기본 레포를 내부에서 생성
	public UserController(Scanner scanner) {
		this.scanner = scanner;
		this.userService = new UserService();
		this.assetService = new AssetService();
		this.categoryService = new CategoryService();
		this.transactionService = new TransactionService();
	}

	// 기본 생성자 - 외부에서 레포 주입(교체 용이)
	public UserController(Scanner scanner, UserService userService,
						  AssetService assetService,
						  CategoryService categoryService,
						  TransactionService transactionService) {
		this.scanner = scanner;
		this.userService = userService;
		this.assetService = assetService;
		this.categoryService = categoryService;
		this.transactionService = transactionService;
	}

	public void createUser() {
		System.out.println("\n[🔷 사용자 생성]");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.print("👉 이름: ");
		String name = scanner.nextLine();
		System.out.print("👉 이메일: ");
		String email = scanner.nextLine();
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		String resultMsg = userService.createUser(name, email);
		System.out.println("\n" + resultMsg);
		Users user = userService.findByEmail(email);
		if (user != null) {
			categoryService.initDefaultCategory(user);
		}
	}

	public boolean login() {
		System.out.println("\n[🔐 로그인]");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.print("👉 이메일: ");
		String email = scanner.nextLine();
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		Users user = userService.findByEmail(email);

		if (user == null) {
			System.out.println("\n❌ 해당 이메일의 사용자가 없습니다.");
			return false;
		}

		currentUser = user;
		System.out.println("\n✅ 환영합니다, " + user.getName() + "님!");
		return true;
	}

	public boolean deleteCurrentUser() {
		System.out.println("\n[🗑️ 사용자 삭제]");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		if (currentUser == null) {
			System.out.println("❌ 로그인된 사용자가 없습니다.");
			return false;
		}

		System.out.print("정말로 삭제하시겠습니까? (Y/N): ");
		String confirm = scanner.nextLine().trim().toLowerCase();
		if (!confirm.equals("y")) {
			System.out.println("🚫 사용자 삭제가 취소되었습니다.");
			return false;
		}

		// ✅ 순서: 거래 → 자산 → 카테고리 → 사용자
		transactionService.deleteAllByUser(currentUser);
		assetService.deleteAllByUser(currentUser);
		categoryService.deleteAllByUser(currentUser);
		userService.deleteUser(currentUser);
		currentUser = null;

		System.out.println("\n✅ 사용자 및 연결된 모든 데이터가 삭제되었습니다.");
		return true;
	}

	public Users getCurrentUser() {
		return currentUser;
	}
}
