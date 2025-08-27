package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import domain.Asset;
import domain.Category;
import enums.CategoryKind;
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

	// 간소 생성사 - 기본 레포를 내부에서 생성
	public TransactionController(Scanner scanner, Users currentUser) {
		this.scanner = scanner;
		this.currentUser = currentUser;
		this.transactionService = new TransactionService();
		this.categoryService = new CategoryService();
		this.assetService = new AssetService();
	}

	// 기본 생성자 - 외부에서 레포 주입(교체 용이)
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
			System.out.println("\n================= 🧾 기록 관리 =================");
			System.out.println("1. ➕ 수입 기록 추가");
			System.out.println("2. ➖ 지출 기록 추가");
			System.out.println("3. 🔁 이체 기록 추가");
			System.out.println("4. 🗑️ 기록 삭제");
			System.out.println("5. 🔎 기록 조회");
			System.out.println("0. 🔙 메인 메뉴로 돌아가기");
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.print("👉 선택: ");

			String choice = scanner.nextLine().trim();
			switch (choice) {
				case "1" -> addIncome();
				case "2" -> addExpense();
				case "3" -> addTransfer();
				case "4" -> deleteTransaction();
				case "5" -> searchTransactions();
				case "0" -> {
					System.out.println("\n🔙 메인 메뉴로 돌아갑니다.");
					return;
				}
				default -> System.out.println("\n❗ 올바른 번호를 입력해주세요.");
			}
		}
	}

	private void addIncome() {
		System.out.println("\n[➕ 수입 기록 추가]");
		try {
			System.out.print("👉 금액: ");
			long amount = Long.parseLong(scanner.nextLine().trim());

			System.out.print("👉 메모: ");
			String memo = scanner.nextLine();

			System.out.println("\n--- 💼 입금 자산 선택 ---");
			Asset selectedAsset = selectAsset(currentUser);
			if (selectedAsset == null) return;

			Category selectedCategory = selectCategory(currentUser, "INCOME");
			if (selectedCategory == null) return;

			String result = transactionService.addIncome(
					currentUser, amount, LocalDateTime.now(), memo,
					selectedCategory.getId(), selectedAsset.getId()
			);
			System.out.println("\n" + result);

		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private void addExpense() {
		System.out.println("\n[➖ 지출 기록 추가]");
		try {
			System.out.print("👉 금액: ");
			long amount = Long.parseLong(scanner.nextLine().trim());

			System.out.print("👉 메모: ");
			String memo = scanner.nextLine();

			System.out.println("\n--- 💳 출금 자산 선택 ---");
			Asset selectedAsset = selectAsset(currentUser);
			if (selectedAsset == null) return;

			Category selectedCategory = selectCategory(currentUser, "EXPENSE");
			if (selectedCategory == null) return;

			String result = transactionService.addExpense(
					currentUser, amount, LocalDateTime.now(), memo,
					selectedCategory.getId(), selectedAsset.getId()
			);
			System.out.println("\n" + result);

		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private void addTransfer() {
		System.out.println("\n[🔁 이체 기록 추가]");
		try {
			System.out.print("👉 금액: ");
			long amount = Long.parseLong(scanner.nextLine().trim());

			System.out.print("👉 메모: ");
			String memo = scanner.nextLine();

			System.out.println("\n--- 💳 출금 자산 선택 ---");
			Asset fromAsset = selectAsset(currentUser);
			if (fromAsset == null) return;

			System.out.println("\n--- 💼 입금 자산 선택 ---");
			Asset toAsset = selectAsset(currentUser);
			if (toAsset == null) return;

			if (fromAsset.getId().equals(toAsset.getId())) {
				System.out.println("❌ 출금 자산과 입금 자산은 같을 수 없습니다.");
				return;
			}

			Category selectedCategory = selectCategory(currentUser, "TRANSFER");
			if (selectedCategory == null) return;

			String result = transactionService.addTransfer(
					currentUser, amount, LocalDateTime.now(), memo,
					selectedCategory.getId(), fromAsset.getId(), toAsset.getId()
			);
			System.out.println("\n" + result);

		} catch (NumberFormatException e) {
			System.out.println("❌ 금액은 숫자로 입력해야 합니다.");
		}
	}

	private void deleteTransaction() {
		System.out.println("\n[🗑️ 거래 기록 삭제]");

		List<Transaction> transactions = transactionService.searchTransactions(
				currentUser, null, null, null, null, null, null
		);
		if (transactions.isEmpty()) {
			System.out.println("⚠️ 삭제할 거래 기록이 없습니다.");
			return;
		}

		final int idxW = 4, typeW = 8, amtW = 10, catW = 10, fromW = 10, toW = 10, dateW = 10;
		final String H = "%-" + idxW + "s %-" + typeW + "s %" + amtW + "s %-" + catW + "s %-" + fromW + "s %-" + toW + "s %-" + dateW + "s%n";
		final String R = "%-" + idxW + "d %-" + typeW + "s %" + amtW + "s %-" + catW + "s %-" + fromW + "s %-" + toW + "s %-" + dateW + "s%n";
		final String LINE = "--------------------------------------------------------------------------------";

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf(H, "번호", "유형", "금액", "카테고리", "출금 자산", "입금 자산", "날짜");
		System.out.println(LINE);

		for (int i = 0; i < transactions.size(); i++) {
			Transaction t = transactions.get(i);

			String categoryName = categoryService.findById(currentUser, t.getCategoryId())
					.map(Category::getName).orElse("");

			String fromAssetName = "";
			String toAssetName   = "";
			if (t.getType() == CategoryKind.INCOME) {
				toAssetName = assetService.findById(currentUser, t.getAssetId()).map(Asset::getName).orElse("");
			} else if (t.getType() == CategoryKind.EXPENSE) {
				fromAssetName = assetService.findById(currentUser, t.getAssetId()).map(Asset::getName).orElse("");
			} else { // TRANSFER
				fromAssetName = assetService.findById(currentUser, t.getAssetId()).map(Asset::getName).orElse("");
				toAssetName   = assetService.findById(currentUser, t.getToAssetId()).map(Asset::getName).orElse("");
			}

			String amtStr = String.format("%,d원", t.getAmount());
			String dateStr = t.getDateTime().toLocalDate().toString();

			System.out.printf(R, (i + 1), t.getType().name(), amtStr, categoryName, fromAssetName, toAssetName, dateStr);
		}

		System.out.println(LINE);
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		System.out.print("👉 삭제할 번호 입력 (0: 취소): ");
		try {
			int choice = Integer.parseInt(scanner.nextLine().trim());
			if (choice == 0) return;
			if (choice < 1 || choice > transactions.size()) {
				System.out.println("❌ 잘못된 번호입니다.");
				return;
			}

			Transaction selectedTx = transactions.get(choice - 1);

			System.out.print("정말 삭제하시겠습니까? (Y/N): ");
			String confirm = scanner.nextLine().trim().toLowerCase();
			if (!confirm.equals("y")) {
				System.out.println("🚫 삭제가 취소되었습니다.");
				return;
			}

			String result = transactionService.deleteTransaction(currentUser, selectedTx.getId());
			System.out.println("\n" + result);
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자로 입력해야 합니다.");
		}
	}

	private void searchTransactions() {
		System.out.println("\n[🔎 기록 조회 및 검색]");
		System.out.println("💡 각 항목에서 Enter는 해당 조건을 건너뜁니다.");

		try {
			System.out.print("👉 시작 날짜 (YYYY-MM-DD): ");
			String startStr = scanner.nextLine().trim();
			LocalDate startDate = startStr.isBlank() ? null : LocalDate.parse(startStr);

			System.out.print("👉 종료 날짜 (YYYY-MM-DD): ");
			String endStr = scanner.nextLine().trim();
			LocalDate endDate = endStr.isBlank() ? null : LocalDate.parse(endStr);

			System.out.println("\n--- 💼 자산 선택 ---");
			Asset asset = selectAsset(currentUser); // 0 입력 시 null 반환 → 전체
			UUID assetId = (asset != null) ? asset.getId() : null;

			System.out.println("\n--- 📂 카테고리 선택 ---");
			Category category = selectCategory(currentUser, null); // 0 입력 시 null 반환 → 전체
			UUID categoryId = (category != null) ? category.getId() : null;

			System.out.print("👉 최소 금액: ");
			String minStr = scanner.nextLine().trim();
			Long minAmount = minStr.isBlank() ? null : Long.parseLong(minStr);

			System.out.print("👉 최대 금액: ");
			String maxStr = scanner.nextLine().trim();
			Long maxAmount = maxStr.isBlank() ? null : Long.parseLong(maxStr);

			List<Transaction> results = transactionService.searchTransactions(
					currentUser, startDate, endDate, assetId, categoryId, minAmount, maxAmount
			);
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
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.println("번호  자산명           잔액");
			System.out.println("--------------------------------------------------");
			for (int i = 0; i < assets.size(); i++) {
				Asset a = assets.get(i);
				System.out.printf("%-4d %-15s %,d원\n", (i + 1), a.getName(), a.getBalance());
			}
			System.out.println("--------------------------------------------------");
			System.out.print("👉 번호 선택 (0: 취소): ");
			try {
				int choice = Integer.parseInt(scanner.nextLine().trim());
				if (choice == 0) return null;
				if (choice > 0 && choice <= assets.size()) return assets.get(choice - 1);
				System.out.println("❌ 잘못된 번호입니다.");
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자로 입력해야 합니다.");
			}
		}
	}

	private void displayTransactions(List<Transaction> transactions) {
		System.out.println("\n[🧾 거래 기록 조회 결과]");
		if (transactions.isEmpty()) {
			System.out.println("⚠️ 조건에 맞는 거래 기록이 없습니다.");
			return;
		}

		final String H    = "%-4s %-12s %-8s %-10s %-12s %-12s %12s %s%n";
		final String ROW  = "%-4d %-12s %-8s %-10s %-12s %-12s %,12d원 %s%n";
		final String LINE = "--------------------------------------------------------------------------------";

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf(H, "번호", "날짜", "유형", "카테고리", "출금 자산", "입금 자산", "금액", "메모");
		System.out.println(LINE);

		long totalAmount = 0;

		for (int i = 0; i < transactions.size(); i++) {
			Transaction tx = transactions.get(i);

			String categoryName = categoryService.findById(currentUser, tx.getCategoryId())
					.map(Category::getName).orElse("");

			String fromAssetName = "";
			String toAssetName   = "";

			if (tx.getType() == CategoryKind.INCOME) {
				toAssetName = assetService.findById(currentUser, tx.getAssetId())
						.map(Asset::getName).orElse("");
				totalAmount += tx.getAmount();
			} else if (tx.getType() == CategoryKind.EXPENSE) {
				fromAssetName = assetService.findById(currentUser, tx.getAssetId())
						.map(Asset::getName).orElse("");
				totalAmount -= tx.getAmount();
			} else { // TRANSFER
				fromAssetName = assetService.findById(currentUser, tx.getAssetId())
						.map(Asset::getName).orElse("");
				toAssetName = assetService.findById(currentUser, tx.getToAssetId())
						.map(Asset::getName).orElse("");
			}

			System.out.printf(
					ROW,
					(i + 1),
					tx.getDateTime().toLocalDate().toString(),
					tx.getType().name(),
					categoryName,
					fromAssetName,
					toAssetName,
					tx.getAmount(),
					tx.getMemo()
			);
		}

		System.out.println(LINE);
		System.out.printf("자산 변동 합계: %,d원%n", totalAmount);
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	}

	private Category selectCategory(Users user, String type) {
		List<Category> allCategories = categoryService.getSortedCategories(user);

		List<Category> filtered;
		if (type == null) {
			filtered = allCategories;
		} else {
			filtered = allCategories.stream()
					.filter(c -> c.getCategory().name().equals(type))
					.toList();
		}

		String displayType = (type == null) ? "전체" : type;
		if (filtered.isEmpty()) {
			System.out.printf("❌ 먼저 '%s' 타입의 카테고리를 등록해야 합니다.\n", displayType);
			return null;
		}

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.printf("[📂 %s 카테고리 선택]\n", displayType);
		System.out.println("번호  이름              종류");
		System.out.println("--------------------------------------------------");
		for (int i = 0; i < filtered.size(); i++) {
			Category c = filtered.get(i);
			System.out.printf("%-4d %-16s %-10s\n", (i + 1), c.getName(), c.getCategory().name());
		}
		System.out.println("--------------------------------------------------");

		while (true) {
			System.out.print("👉 번호 선택 (0: 취소): ");
			try {
				int choice = Integer.parseInt(scanner.nextLine().trim());
				if (choice == 0) return null;
				if (choice > 0 && choice <= filtered.size()) {
					return filtered.get(choice - 1);
				}
				System.out.println("❌ 잘못된 번호입니다.");
			} catch (NumberFormatException e) {
				System.out.println("❌ 숫자로 입력해야 합니다.");
			}
		}
	}
}