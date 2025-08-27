package controller;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Asset;
import domain.Users;
import service.AssetService;

public class AssetController {

	private static final String SEP = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
	private static final String LINE = "--------------------------------------------------------------";
	private static final String PROMPT_SELECT = "👉 선택: ";
	private static final String PROMPT_INPUT_NUMBER_EDIT = "👉 수정할 번호 입력 (0: 취소): ";
	private static final String PROMPT_INPUT_NUMBER_DELETE = "👉 삭제할 번호 입력 (0: 취소): ";
	private static final String MSG_BACK = "\n🔙 메인 메뉴로 돌아갑니다.";
	private static final String MSG_INPUT_NUMBER = "❌ 숫자를 입력해주세요.";
	private static final String MSG_WRONG_NUMBER = "❌ 잘못된 번호입니다.";
	private static final String MSG_NO_ASSET_FOR_EDIT = "\n⚠️ 수정할 자산이 없습니다.";
	private static final String MSG_NO_ASSET_FOR_DELETE = "\n⚠️ 삭제할 자산이 없습니다.";
	private static final String MSG_NO_ASSET_REGISTERED = "⚠️ 등록된 자산이 없습니다.";
	private static final String MSG_CANCELLED = "🚫 삭제가 취소되었습니다.";

	// 테이블 포맷
	private static final String HEADER_FMT = "%-4s %-14s %-10s %16s%n";
	private static final String ROW_FMT    = "%-4d %-14s %-10s %,16d원%n";

	private final Scanner scanner;
	private final AssetService assetService;
	private Users currentUser;

	// 간소 생성자 - MainApp이 서비스 모름
	public AssetController(Scanner scanner) {
		this.scanner = scanner;
		this.assetService = new AssetService();
	}

	public AssetController(Scanner scanner, AssetService assetService) {
		this.scanner = scanner;
		this.assetService = assetService;
	}

	public void setCurrentUser(Users user) {
		this.currentUser = user;
	}

	public void mainMenu() {
		while (true) {
			System.out.println("\n================= 💼 자산 관리 =================");
			System.out.println("1. ➕ 자산 등록");
			System.out.println("2. 📝 자산 수정");
			System.out.println("3. 🗑️ 자산 삭제");
			System.out.println("4. 📋 자산 조회");
			System.out.println("0. 🔙 메인 메뉴로 돌아가기");
			System.out.println(SEP);
			System.out.print(PROMPT_SELECT);
			String input = scanner.nextLine();

			switch (input) {
				case "1" -> registerAsset();
				case "2" -> updateAsset();
				case "3" -> deleteAsset();
				case "4" -> viewAssets();
				case "0" -> {
					System.out.println(MSG_BACK);
					return;
				}
				default -> System.out.println("\n❗ 올바른 번호를 입력해주세요.");
			}
		}
	}

	private void registerAsset() {
		System.out.println("\n[💰 자산 등록]");
		System.out.println(SEP);

		List<Asset> existing = assetService.findAssetsByUser(currentUser);
		Set<String> existingTypes = existing.stream().map(Asset::getType).collect(Collectors.toSet());

		if (existingTypes.isEmpty()) {
			System.out.println("💡 추천 자산 유형: 현금, 계좌, 카드, 주식, 대출");
		} else {
			System.out.println("💡 현재 보유한 자산 유형: " + String.join(", ", existingTypes));
		}

		System.out.print("👉 자산명: ");
		String name = scanner.nextLine();
		System.out.print("👉 자산 유형: ");
		String type = scanner.nextLine();
		System.out.print("👉 초기 잔액: ");
		long balance;
		try {
			balance = Long.parseLong(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자만 입력해주세요. 자산 등록을 취소합니다.");
			return;
		}

		System.out.println(SEP);
		String result = assetService.createAsset(currentUser, name, type, balance);
		System.out.println("\n" + result);
	}

	private void updateAsset() {
		List<Asset> assets = assetService.findAssetsByUser(currentUser);
		if (assets.isEmpty()) {
			System.out.println(MSG_NO_ASSET_FOR_EDIT);
			return;
		}
		viewAssets();

		System.out.print(PROMPT_INPUT_NUMBER_EDIT);
		int index;
		try {
			index = Integer.parseInt(scanner.nextLine().trim()) - 1; // 1-based → 0-based
		} catch (NumberFormatException e) {
			System.out.println(MSG_INPUT_NUMBER);
			return;
		}

		if (index == -1) { // 0 입력
			return;
		}
		if (index < 0 || index >= assets.size()) {
			System.out.println(MSG_WRONG_NUMBER);
			return;
		}

		Asset selected = assets.get(index);
		UUID assetId = selected.getId();

		System.out.print("👉 새 이름 (변경 없으면 Enter): ");
		String name = scanner.nextLine();

		System.out.print("👉 새 유형 (변경 없으면 Enter): ");
		String type = scanner.nextLine();

		System.out.println(SEP);
		String result = assetService.updateAsset(currentUser, assetId, name, type);
		System.out.println("\n" + result);
	}

	private void deleteAsset() {
		List<Asset> assets = assetService.findAssetsByUser(currentUser);
		if (assets.isEmpty()) {
			System.out.println(MSG_NO_ASSET_FOR_DELETE);
			return;
		}
		viewAssets();

		System.out.print(PROMPT_INPUT_NUMBER_DELETE);
		int index;
		try {
			index = Integer.parseInt(scanner.nextLine().trim()) - 1; // 1-based → 0-based
		} catch (NumberFormatException e) {
			System.out.println(MSG_INPUT_NUMBER);
			return;
		}

		if (index == -1) return;
		if (index < 0 || index >= assets.size()) {
			System.out.println(MSG_WRONG_NUMBER);
			return;
		}

		Asset target = assets.get(index);

		System.out.print("정말 '" + target.getName() + "' 자산을 삭제하시겠습니까? (Y/N): ");
		String confirm = scanner.nextLine().trim().toLowerCase();
		if (!confirm.equals("y")) {
			System.out.println(MSG_CANCELLED);
			return;
		}

		String result = assetService.deleteAsset(currentUser, target.getId());
		System.out.println("\n" + result);
	}

	private void viewAssets() {
		System.out.println("\n[📋 자산 조회]");
		System.out.println(SEP);

		List<Asset> assets = assetService.findAssetsByUser(currentUser);

		if (assets.isEmpty()) {
			System.out.println(MSG_NO_ASSET_REGISTERED);
			System.out.println(SEP);
			return;
		}

		long total = 0;

		System.out.printf(HEADER_FMT, "번호", "자산명", "유형", "잔액");
		System.out.println(LINE);

		int idx = 1;
		for (Asset a : assets) {
			System.out.printf(ROW_FMT, idx++, a.getName(), a.getType(), a.getBalance());
			total += a.getBalance();
		}

		System.out.println(LINE);
		System.out.printf("총 합계: %,d원%n", total);
		System.out.println(SEP);
	}
}
