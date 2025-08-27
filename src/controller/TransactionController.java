package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Asset;
import domain.Category;
import domain.CategoryKind;
import domain.Transaction;
import domain.Users;
import service.AssetService;
import service.CategoryService;
import service.TransactionService;

public class TransactionController {

	private final Scanner scanner;
	private Users currentUser;
	private final TransactionService transactionService;
	private final CategoryService categoryService;
	private final AssetService assetService;

	public TransactionController(Scanner scanner, Users currentUser, TransactionService transactionService,
			CategoryService categoryService, AssetService assetService) {
		this.scanner = scanner;
		this.currentUser = currentUser;
		this.transactionService = transactionService;
		this.categoryService = categoryService;
		this.assetService = assetService;
	}

	public void mainMenu() {
		while (true) {
			System.out.println("\n=============== 거래 기록 관리 ===============");
			System.out.println("1. 수입 기록 추가");
			System.out.println("2. 지출 기록 추가");
			System.out.println("3. 이체 기록 추가");
			System.out.println("4. 기록 삭제");
			System.out.println("5. 기록 조회");
			System.out.println("0. 메인 메뉴로 돌아가기");
			System.out.println("--------------------------------------------");
			System.out.print("원하는 작업의 번호를 입력하세요: ");

			String choice = scanner.nextLine();
			switch (choice) {
			case "1" -> addIncome();
			case "2" -> addExpense();
			case "3" -> addTransfer();
			case "4" -> deleteTransaction();
			case "5" -> searchTransactions();
			case "0" -> {
				System.out.println("메인 메뉴로 돌아갑니다.");
				return;
			}
			default -> System.out.println("❌ 잘못된 입력입니다. 메뉴에 있는 번호를 입력해주세요.");
			}
		}
	}

	private void addIncome() {
		System.out.println("\n--- 수입 기록 추가 ---");
		try {
			System.out.print("금액: ");
			long amount = Long.parseLong(scanner.nextLine());

			System.out.print("메모: ");
			String memo = scanner.nextLine();

			Asset selectedAsset = selectAsset(currentUser);
			if (selectedAsset == null) {
				return;
			}

			Category selectedCategory = selectCategory(currentUser, "INCOME");
			if (selectedCategory == null) {
				return;
			}

			String result = transactionService.addIncome(currentUser, amount, LocalDateTime.now(), memo,
					selectedCategory.getId(), selectedAsset.getId());
			System.out.println(result);

		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private void addExpense() {
		System.out.println("\n--- 지출 기록 추가 ---");
		try {
			System.out.print("금액: ");
			long amount = Long.parseLong(scanner.nextLine());

			System.out.print("메모: ");
			String memo = scanner.nextLine();

			Asset selectedAsset = selectAsset(currentUser);
			if (selectedAsset == null) {
				return;
			}

			Category selectedCategory = selectCategory(currentUser, "EXPENSE");
			if (selectedCategory == null) {
				return;
			}

			String result = transactionService.addExpense(currentUser, amount, LocalDateTime.now(), memo,
					selectedCategory.getId(), selectedAsset.getId());
			System.out.println(result);

		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private void addTransfer() {
		System.out.println("\n--- 이체 기록 추가 ---");
		try {
			System.out.print("금액: ");
			long amount = Long.parseLong(scanner.nextLine());

			System.out.print("메모: ");
			String memo = scanner.nextLine();

			System.out.println("--- 출금 자산 선택 ---");
			Asset fromAsset = selectAsset(currentUser);
			if (fromAsset == null) {
				return;
			}

			System.out.println("--- 입금 자산 선택 ---");
			Asset toAsset = selectAsset(currentUser);
			if (toAsset == null) {
				return;
			}

			if (fromAsset.getId().equals(toAsset.getId())) {
				System.out.println("❌ 출금 자산과 입금 자산은 같을 수 없습니다.");
				return;
			}

			Category selectedCategory = selectCategory(currentUser, "TRANSFER");
			if (selectedCategory == null) {
				return;
			}

			String result = transactionService.addTransfer(currentUser, amount, LocalDateTime.now(), memo,
					selectedCategory.getId(), fromAsset.getId(), toAsset.getId());
			System.out.println(result);

		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private void deleteTransaction() {
		System.out.println("\n--- 거래 기록 삭제 ---");
		List<Transaction> transactions = transactionService.searchTransactions(currentUser, null, null, null, null,
				null, null);

		if (transactions.isEmpty()) {
			System.out.println("삭제할 거래 기록이 없습니다.");
			return;
		}

		System.out.println("삭제할 거래 기록을 선택하세요:");
		for (int i = 0; i < transactions.size(); i++) {
			Transaction t = transactions.get(i);
			System.out.printf("%d. [%s] %s: %,d원 (%s)\n", (i + 1), t.getType(), t.getMemo(), t.getAmount(),
					t.getDateTime().toLocalDate());
		}
		System.out.println("--------------------------------------------");
		System.out.print("번호 입력 (0: 취소): ");

		try {
			int choice = Integer.parseInt(scanner.nextLine());
			if (choice == 0) {
				return;
			}
			if (choice < 1 || choice > transactions.size()) {
				System.out.println("❌ 잘못된 번호입니다.");
				return;
			}
			Transaction selectedTx = transactions.get(choice - 1);
			String result = transactionService.deleteTransaction(currentUser, selectedTx.getId());
			System.out.println(result);
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자로 입력해야 합니다.");
		}
	}

	private void searchTransactions() {
		System.out.println("\n--- 기록 조회 및 검색 ---");
		System.out.println("💡 각 항목에 대해 Enter를 누르면 해당 조건은 건너뜁니다.");

		try {
			System.out.print("👉 시작 날짜 (YYYY-MM-DD): ");
			String startStr = scanner.nextLine();
			LocalDate startDate = startStr.isBlank() ? null : LocalDate.parse(startStr);

			System.out.print("👉 종료 날짜 (YYYY-MM-DD): ");
			String endStr = scanner.nextLine();
			LocalDate endDate = endStr.isBlank() ? null : LocalDate.parse(endStr);

			System.out.println("--- 자산 선택 (전체: 0 또는 Enter) ---");
			Asset asset = selectAsset(currentUser);
			UUID assetId = (asset != null) ? asset.getId() : null;

			System.out.println("--- 카테고리 선택 (전체: 0 또는 Enter) ---");
			Category category = selectCategory(currentUser, null);
			UUID categoryId = (category != null) ? category.getId() : null;

			System.out.print("👉 최소 금액: ");
			String minStr = scanner.nextLine();
			Long minAmount = minStr.isBlank() ? null : Long.parseLong(minStr);

			System.out.print("👉 최대 금액: ");
			String maxStr = scanner.nextLine();
			Long maxAmount = maxStr.isBlank() ? null : Long.parseLong(maxStr);

			List<Transaction> results = transactionService.searchTransactions(currentUser, startDate, endDate, assetId,
					categoryId, minAmount, maxAmount);

			displayTransactions(results);

		} catch (DateTimeParseException e) {
			System.out.println("❌ 날짜 형식이 잘못되었습니다. (YYYY-MM-DD)");
		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private Asset selectAsset(Users user) {
		List<Asset> assets = assetService.findAssetsByUser(user);
		if (assets.isEmpty()) {
			System.out.println("❌ 먼저 자산을 등록해야 합니다.");
			return null;
		}
		while (true) {
			System.out.println("자산 번호를 선택하세요 (0: 취소):");
			for (int i = 0; i < assets.size(); i++) {
				Asset a = assets.get(i);
				System.out.printf("%d. %s (잔액: %,d원)\n", (i + 1), a.getName(), a.getBalance());
			}
			try {
				int choice = Integer.parseInt(scanner.nextLine());
				if (choice == 0) {
					return null;
				}
				if (choice > 0 && choice <= assets.size()) {
					return assets.get(choice - 1);
				}
				System.out.println("❌ 잘못된 번호입니다.");
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자로 입력해야 합니다.");
			}
		}
	}

	private void displayTransactions(List<Transaction> transactions) {
		System.out.println("\n--- 🧾 거래 기록 조회 결과 ---");
		if (transactions.isEmpty()) {
			System.out.println("⚠️ 조건에 맞는 거래 기록이 없습니다.");
			return;
		}

		System.out.printf("%-4s %-12s | %-8s | %-12s | %-12s | %-12s | %-15s | %s\n", "번호", "날짜", "유형", "카테고리", "출금 자산",
				"입금 자산", "금액", "메모");
		System.out.println(
				"----------------------------------------------------------------------------------------------------------");
		long totalAmount = 0;
		for (int i = 0; i < transactions.size(); i++) {
			Transaction tx = transactions.get(i);
			String categoryName = categoryService.findById(currentUser, tx.getCategoryId()).map(Category::getName)
					.orElse("N/A");

			String fromAssetName = "";
			String toAssetName = "";
			CategoryKind type = tx.getType();

			if (type == CategoryKind.INCOME) {
				toAssetName = assetService.findById(currentUser, tx.getAssetId()).map(Asset::getName).orElse("N/A");
				totalAmount += tx.getAmount();
			} else if (type == CategoryKind.EXPENSE) {
				fromAssetName = assetService.findById(currentUser, tx.getAssetId()).map(Asset::getName).orElse("N/A");
				totalAmount -= tx.getAmount();
			} else if (type == CategoryKind.TRANSFER) {
				fromAssetName = assetService.findById(currentUser, tx.getAssetId()).map(Asset::getName).orElse("N/A");
				toAssetName = assetService.findById(currentUser, tx.getToAssetId()).map(Asset::getName).orElse("N/A");
			}

			System.out.printf("%-4d %-12s | %-8s | %-12s | %-12s | %-12s | %,15d원 | %s\n", (i + 1),
					tx.getDateTime().toLocalDate(), tx.getType().name(), categoryName, fromAssetName, toAssetName,
					tx.getAmount(), tx.getMemo());
		}
		System.out.println(
				"----------------------------------------------------------------------------------------------------------");
		System.out.printf("조회된 기록 자산 변동 합계: %,65d원\n", totalAmount);
	}

	private Category selectCategory(Users user, String type) {
		List<Category> allCategories = categoryService.getSortedCategories(user);

		List<Category> filteredCategories;
		if (type == null) {
			filteredCategories = allCategories;
		} else {
			filteredCategories = allCategories.stream().filter(c -> c.getCategory().name().equals(type))
					.collect(Collectors.toList());
		}
		System.out.println("결과: 총 " + filteredCategories.size() + "개의 카테고리를 찾았습니다.");
		for (Category c : filteredCategories) {
			System.out.printf("- 이름: %s, 타입: %s\n", c.getName(), c.getCategory().name());
		}

		if (filteredCategories.isEmpty()) {
			System.out.printf("❌ 먼저 '%s' 타입의 카테고리를 등록해야 합니다.\n", type == null ? "일반" : type);
			return null;
		}

		while (true) {
			String displayType = (type == null) ? "전체" : type;
			System.out.printf("'%s' 카테고리 번호를 선택하세요 (0: 취소):\n", displayType);
			for (int i = 0; i < filteredCategories.size(); i++) {
				System.out.printf("%d. %s\n", (i + 1), filteredCategories.get(i).getName());
			}
			try {
				int choice = Integer.parseInt(scanner.nextLine());
				if (choice == 0) {
					return null;
				}
				if (choice > 0 && choice <= filteredCategories.size()) {
					return filteredCategories.get(choice - 1);
				}
				System.out.println("❌ 잘못된 번호입니다.");
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자로 입력해야 합니다.");
			}
		}
	}
}