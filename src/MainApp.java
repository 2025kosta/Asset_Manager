
import java.util.Scanner;

import controller.AssetController;
import controller.CategoryController;
import controller.TransactionController;
import controller.UserController;
import domain.Users;
import repository.AssetRepository;
import repository.CategoryRepository;
import repository.TransactionRepository;
import repository.UserRepository;
import service.AssetService;
import service.CategoryService;
import service.TransactionService;
import service.UserService;

public class MainApp {

	public static void main(String[] args) {
		AssetRepository assetRepository = new AssetRepository();
		CategoryRepository categoryRepository = new CategoryRepository();
		TransactionRepository transactionRepository = new TransactionRepository();
		UserRepository userRepository = new UserRepository();

		AssetService assetService = new AssetService(assetRepository);
		CategoryService categoryService = new CategoryService(categoryRepository, transactionRepository);
		UserService userService = new UserService(userRepository);
		TransactionService transactionService = new TransactionService(transactionRepository, assetRepository);

		Scanner scanner = new Scanner(System.in);
		UserController userController = new UserController(scanner, userService, assetService, categoryService);

		while (true) {
			System.out.println("\n================= 📊 자산관리 시스템 =================");
			System.out.println("1. 👤 사용자 생성");
			System.out.println("2. 🔐 로그인");
			System.out.println("0. ❌ 종료");
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.print("👉 선택(번호 입력): ");
			int choice;

			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("\n❗ 숫자만 입력해주세요.");
				continue;
			}

			switch (choice) {
			case 1 -> userController.createUser();
			case 2 -> {
				if (userController.login()) {
					Users currentUser = userController.getCurrentUser();
					AssetController assetController = new AssetController(scanner, assetService);
					assetController.setCurrentUser(currentUser);
					CategoryController categoryController = new CategoryController(scanner, currentUser,
							categoryService);
					TransactionController transactionController = new TransactionController(scanner, currentUser,
							transactionService, categoryService, assetService);
					loginMenu(scanner, userController, assetController, categoryController, transactionController);
				}
			}
			case 0 -> {
				System.out.println("\n👋 시스템을 종료합니다. 안녕히 가세요!");
				return;
			}
			default -> System.out.println("\n❗ 잘못된 입력입니다. 다시 시도해주세요.");
			}
		}
	}

	private static void loginMenu(Scanner scanner, UserController userController, AssetController assetController,
			CategoryController categoryController, TransactionController transactionController) {
		while (true) {
			System.out.println("\n환영합니다, " + userController.getCurrentUser().getName() + "님!");
			System.out.println("================= 🧭 메인 메뉴 =================");
			System.out.println("1. 💼 자산 관리");
			System.out.println("2. 📁 카테고리 관리");
			System.out.println("3. 🧾 기록 관리 ");
			System.out.println("4. 🔒 로그아웃");
			System.out.println("5. 🗑️ 사용자 삭제");
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.print("👉 선택(번호 입력): ");
			String input = scanner.nextLine();

			switch (input) {
			case "1" -> assetController.mainMenu();
			case "2" -> categoryController.mainMenu();
			case "3" -> transactionController.mainMenu();
			case "4" -> {
				System.out.println("\n🔒 로그아웃 되었습니다.");
				return;
			}
			case "5" -> {
				boolean deleted = userController.deleteCurrentUser();
				if (deleted) {
					return;
				}
			}
			default -> System.out.println("\n❗ 잘못된 입력입니다.");
			}
		}

	}

}
