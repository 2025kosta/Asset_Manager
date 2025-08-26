package controller;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import domain.Category;
import domain.CategoryKind;
import service.CategoryService;

public class CategoryController {

	private final Scanner scanner;
	private final CategoryService categoryService;

	public CategoryController(Scanner scanner) {
		this.scanner = scanner;
		this.categoryService = new CategoryService();
	}

	public void mainMenu() {
		while (true) {
			System.out.println("\n=============== 카테고리 관리 ===============");
			System.out.println("1. 카테고리 등록");
			System.out.println("2. 카테고리 수정");
			System.out.println("3. 카테고리 삭제");
			System.out.println("4. 카테고리 전체 조회");
			System.out.println("0. 메인 메뉴로 돌아가기");
			System.out.println("--------------------------------------------");
			System.out.print("원하는 작업의 번호를 입력하세요: ");

			String choice = scanner.nextLine();

			switch (choice) {
			case "1" -> registerCategory();
			case "2" -> updateCategory();
			case "3" -> deleteCategory();
			case "4" -> viewAllCategories();
			case "0" -> {
				System.out.println("메인 메뉴로 돌아갑니다.");
				return;
			}
			default -> System.out.println("❌ 잘못된 입력입니다. 메뉴에 있는 번호를 입력해주세요.");
			}
		}
	}

	/**
	 * 1. 카테고리 등록
	 */
	private void registerCategory() {
		System.out.println("\n--- 카테고리 등록 ---");
		System.out.print("등록할 카테고리 이름을 입력하세요: ");
		String name = scanner.nextLine();

		CategoryKind type;
		while (true) {
			System.out.print("카테고리 종류를 선택하세요 (1: 수입, 2: 지출, 3: 이체): ");
			String typeChoice = scanner.nextLine();
			if (typeChoice.equals("1")) {
				type = CategoryKind.INCOME;
				break;
			} else if (typeChoice.equals("2")) {
				type = CategoryKind.EXPENSE;
				break;
			} else if (typeChoice.equals("3")) {
				type = CategoryKind.TRANSFER;
				break;
			} else {
				System.out.println("❌ 잘못된 선택입니다. 1, 2, 3 중에서 선택해주세요.");
			}
		}

		String result = categoryService.registerCategory(name, type);
		System.out.println(result);
	}

	private void updateCategory() {
		while (true) {
			System.out.println("\n=============== 카테고리 수정 ===============");
			List<Category> categories = categoryService.getSortedCategories();
			if (displayCategoryList(categories)) {
				return;
			}

			System.out.print("수정할 카테고리 번호를 입력하세요 (뒤로가기: 0): ");
			int choice = getIntegerInput();
			if (choice == -1) {
				continue;
			}
			if (choice == 0) {
				return;
			}

			if (isInvalidChoice(choice, categories.size())) {
				continue;
			}

			Category selectedCategory = categories.get(choice - 1);

			UUID categoryId = selectedCategory.getId();

			System.out.println("\n선택한 카테고리: " + selectedCategory.getName());
			System.out.print("새 카테고리명을 입력하세요: ");
			String newName = scanner.nextLine();

			String resultMessage = categoryService.updateCategoryName(categoryId, newName);
			System.out.println("\n" + resultMessage);

			if (!askToContinue("계속 수정하시겠습니까?")) {
				return;
			}
		}
	}

	private void deleteCategory() {
		while (true) {
			System.out.println("\n=============== 카테고리 삭제 ===============");
			List<Category> categories = categoryService.getSortedCategories();
			if (displayCategoryList(categories)) {
				return;
			}

			System.out.print("삭제할 카테고리 번호를 입력하세요 (뒤로가기: 0): ");
			int choice = getIntegerInput();
			if (choice == -1) {
				continue;
			}
			if (choice == 0) {
				return;
			}

			if (isInvalidChoice(choice, categories.size())) {
				continue;
			}

			Category selectedCategory = categories.get(choice - 1);
			System.out.printf("'%s' 카테고리를 정말 삭제하시겠습니까? (Y/N): ", selectedCategory.getName());
			String confirm = scanner.nextLine();

			if (confirm.equalsIgnoreCase("Y")) {
				String resultMessage = categoryService.deleteCategory(selectedCategory.getId());
				System.out.println(resultMessage);
			} else {
				System.out.println("삭제가 취소되었습니다.");
			}

			if (!askToContinue("계속 삭제하시겠습니까?")) {
				return;
			}
		}
	}

	private void viewAllCategories() {
		System.out.println("\n--- 카테고리 전체 조회 ---");
		List<Category> categories = categoryService.getSortedCategories();
		if (categories.isEmpty()) {
			System.out.println("등록된 카테고리가 없습니다.");
			return;
		}
		for (Category c : categories) {
			System.out.printf("이름: %-10s | 종류: %s\n", c.getName(), c.getCategory());
		}
	}

	private boolean displayCategoryList(List<Category> categories) {
		if (categories.isEmpty()) {
			System.out.println("현재 등록된 카테고리가 없습니다.");
			return true;
		}
		System.out.println("현재 등록된 카테고리:");
		for (int i = 0; i < categories.size(); i++) {
			Category c = categories.get(i);
			System.out.printf("%d. %-10s [%s]\n", (i + 1), c.getName(), c.getCategory());
		}
		System.out.println("--------------------------------------------");
		return false;
	}

	private int getIntegerInput() {
		try {
			return Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("❌ 잘못된 입력입니다. 숫자를 입력해주세요.");
			return -1;
		}
	}

	private boolean isInvalidChoice(int choice, int listSize) {
		if (choice < 1 || choice > listSize) {
			System.out.println("❌ 목록에 없는 번호입니다. 다시 입력해주세요.");
			return true;
		}
		return false;
	}

	private boolean askToContinue(String prompt) {
		System.out.printf("%s (Y/N): ", prompt);
		String answer = scanner.nextLine();
		return answer.equalsIgnoreCase("Y");
	}
}
