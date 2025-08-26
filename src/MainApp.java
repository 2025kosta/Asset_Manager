
import java.util.Scanner;

import controller.UserController;

public class MainApp {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		UserController userController = new UserController(scanner);

		while (true) {
			System.out.println("================= 자산관리 시스템 =================");
			System.out.println("1. 사용자 생성");
			System.out.println("2. 로그인");
			System.out.println("0. 종료");
			System.out.print("선택(번호 입력): ");
			int choice;

			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자만 입력해주세요.\n");
				continue;
			}

			switch (choice) {
			case 1 -> userController.createUser();
			case 2 -> {
				if (userController.login()) {
					// AssetController가 아직 없으므로 아래는 임시 출력
					System.out.println("✅ 로그인 성공! 자산 관리 메뉴는 아직 구현되지 않았습니다.\n");
				}
			}
			case 0 -> {
				System.out.println("시스템을 종료합니다.");
				return;
			}
			default -> System.out.println("❌ 잘못된 입력입니다. 다시 선택해주세요.\n");
			}
		}

	}

}
