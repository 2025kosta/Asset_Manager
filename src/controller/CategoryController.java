package controller;

import java.util.List;
import java.util.Scanner;

import domain.Category;
import enums.CategoryKind;
import domain.Users;
import service.CategoryService;

public class CategoryController {

	private final Scanner scanner;
	private final CategoryService categoryService;
	private Users currentUser;

	// 간소 생성자
	public CategoryController(Scanner scanner, Users currentUser) {
		this(scanner, currentUser, new CategoryService());
	}

	// 기존 주입용 생성자도 유지
	public CategoryController(Scanner scanner, Users currentUser, CategoryService categoryService) {
		this.scanner = scanner;
		this.currentUser = currentUser;
		this.categoryService = categoryService;
	}

	public void mainMenu() {
		while (true) {
			System.out.println("\n================= 📁 카테고리 관리 =================");
			System.out.println("1. ➕ 카테고리 등록");
			System.out.println("2. 📝 카테고리 수정");
			System.out.println("3. 🗑️ 카테고리 삭제");
			System.out.println("4. 📋 카테고리 전체 조회");
			System.out.println("0. 🔙 메인 메뉴로 돌아가기");
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.print("👉 선택: ");
			String input = scanner.nextLine().trim();

			switch (input) {
				case "1" -> registerCategory();
				case "2" -> updateCategory();
				case "3" -> deleteCategory();
				case "4" -> viewAllCategories();
				case "0" -> {
					System.out.println("\n🔙 메인 메뉴로 돌아갑니다.");
					return;
				}
				default -> System.out.println("\n❗ 올바른 번호를 입력해주세요.");
			}
		}
	}

	private void registerCategory() {
		System.out.println("\n[➕ 카테고리 등록]");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.print("👉 이름: ");
		String name = scanner.nextLine().trim();

		System.out.println("👉 종류 선택: (1) 수입  (2) 지출  (3) 이체");
		System.out.print("👉 선택: ");
		String typeInput = scanner.nextLine().trim();

		CategoryKind kind;
		switch (typeInput) {
			case "1" -> kind = CategoryKind.INCOME;
			case "2" -> kind = CategoryKind.EXPENSE;
			case "3" -> kind = CategoryKind.TRANSFER;
			default -> {
				System.out.println("❌ 잘못된 입력입니다.");
				return;
			}
		}

		String result = categoryService.registerCategory(currentUser, name, kind);
		System.out.println("\n" + result);
	}

	private void updateCategory() {
		List<Category> list = categoryService.getSortedCategories(currentUser);
		if (list.isEmpty()) {
			System.out.println("\n⚠️ 수정할 카테고리가 없습니다.");
			return;
		}

		System.out.println("\n[📝 카테고리 수정]");
		printCategoryTable(list);

		System.out.print("👉 수정할 번호 입력 (0: 취소): ");
		int idx;
		try {
			idx = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			return;
		}
		if (idx == 0) return;
		if (idx < 1 || idx > list.size()) {
			System.out.println("❌ 잘못된 번호입니다.");
			return;
		}

		Category selected = list.get(idx - 1);
		System.out.print("👉 새 이름 (변경 없으면 Enter): ");
		String newName = scanner.nextLine().trim();
		if (newName.isEmpty()) {
			System.out.println("🚫 변경이 취소되었습니다.");
			return;
		}

		String result = categoryService.updateCategoryName(currentUser, selected.getId(), newName);
		System.out.println("\n" + result);
	}

	private void deleteCategory() {
		List<Category> list = categoryService.getSortedCategories(currentUser);
		if (list.isEmpty()) {
			System.out.println("\n⚠️ 삭제할 카테고리가 없습니다.");
			return;
		}

		System.out.println("\n[🗑️ 카테고리 삭제]");
		printCategoryTable(list);

		System.out.print("👉 삭제할 번호 입력 (0: 취소): ");
		int idx;
		try {
			idx = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			return;
		}
		if (idx == 0) return;
		if (idx < 1 || idx > list.size()) {
			System.out.println("❌ 잘못된 번호입니다.");
			return;
		}

		Category selected = list.get(idx - 1);
		System.out.print("정말 '" + selected.getName() + "'을(를) 삭제하시겠습니까? (Y/N): ");
		String confirm = scanner.nextLine().trim().toLowerCase();
		if (!confirm.equals("y")) {
			System.out.println("🚫 삭제가 취소되었습니다.");
			return;
		}

		String result = categoryService.deleteCategory(currentUser, selected.getId());
		System.out.println("\n" + result);
	}

	private void viewAllCategories() {
		List<Category> list = categoryService.getSortedCategories(currentUser);
		System.out.println("\n[📋 카테고리 전체 조회]");
		if (list.isEmpty()) {
			System.out.println("⚠️ 등록된 카테고리가 없습니다.");
			return;
		}
		printCategoryTable(list);
	}

	private void printCategoryTable(List<Category> list) {
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf("%-4s %-16s %-10s\n", "번호", "이름", "종류");
		System.out.println("--------------------------------------------------");
		int i = 1;
		for (Category c : list) {
			System.out.printf("%-4d %-16s %-10s\n", i++, c.getName(), c.getCategory().name());
		}
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	}
}
