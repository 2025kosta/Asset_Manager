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
	private final Scanner scanner;
	private final AssetService assetService;
	private Users currentUser;

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
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.print("👉 선택: ");
			String input = scanner.nextLine();

			switch (input) {
			case "1" -> registerAsset();
			case "2" -> updateAsset();
			case "3" -> deleteAsset();
			case "4" -> viewAssets();
			case "0" -> {
				System.out.println("\n🔙 메인 메뉴로 돌아갑니다.");
				return;
			}
			default -> System.out.println("\n❗ 올바른 번호를 입력해주세요.");
			}
		}
	}

	private void registerAsset() {
		System.out.println("\n[💰 자산 등록]");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		List<Asset> existing = assetService.findAssetsByUser(currentUser);
		Set<String> existingTypes = existing.stream().map(Asset::getType).collect(Collectors.toSet());

		if (existingTypes.isEmpty()) {
			System.out.println("💡 기본 제공 자산 유형: 현금, 계좌, 카드, 주식, 대출");
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

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		String result = assetService.createAsset(currentUser, name, type, balance);
		System.out.println("\n" + result);
	}

	private void updateAsset() {
		List<Asset> assets = assetService.findAssetsByUser(currentUser);
		if (assets.isEmpty()) {
			System.out.println("\n⚠️ 수정할 자산이 없습니다.");
			return;
		}
		viewAssets();

		System.out.print("👉 수정할 자산 번호 입력: ");
		int index;
		try {
			index = Integer.parseInt(scanner.nextLine()) - 1; // 1-based → 0-based
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			return;
		}

		if (index < 0 || index >= assets.size()) {
			System.out.println("❌ 잘못된 번호입니다.");
			return;
		}

		Asset selected = assets.get(index); // 번호 → 자산 객체
		UUID assetId = selected.getId();

		System.out.print("👉 새 이름 (변경 없으면 Enter): ");
		String name = scanner.nextLine();

		System.out.print("👉 새 유형 (변경 없으면 Enter): ");
		String type = scanner.nextLine();

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		String result = assetService.updateAsset(currentUser, assetId, name, type);
		System.out.println("\n" + result);
	}

	private void deleteAsset() {
		List<Asset> assets = assetService.findAssetsByUser(currentUser);
		if (assets.isEmpty()) {
			System.out.println("\n⚠️ 삭제할 자산이 없습니다.");
			return;
		}
		viewAssets();

		System.out.print("👉 삭제할 자산 번호 입력: ");
		int index;
		try {
			index = Integer.parseInt(scanner.nextLine()) - 1; // 1-based → 0-based
		} catch (NumberFormatException e) {
			System.out.println("❌ 숫자를 입력해주세요.");
			return;
		}

		if (index < 0 || index >= assets.size()) {
			System.out.println("❌ 잘못된 번호입니다.");
			return;
		}

		UUID assetId = assets.get(index).getId();

		String result = assetService.deleteAsset(currentUser, assetId);
		System.out.println("\n" + result);
	}

	private void viewAssets() {
		System.out.println("\n[📋 자산 조회]");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		List<Asset> assets = assetService.findAssetsByUser(currentUser);

		if (assets.isEmpty()) {
			System.out.println("⚠️ 등록된 자산이 없습니다.");
		} else {
			long total = 0;
			int idx = 1;
			System.out.printf("%-4s %-10s %-10s %-15s\n", "번호", "자산명", "유형", "잔액");
			System.out.println("-----------------------------------------------------------------------------------");
			for (Asset a : assets) {
				System.out.printf("%-4d %-10s %-10s %,15d원\n", idx++, a.getName(), a.getType(), a.getBalance());
				total += a.getBalance();
			}
			System.out.println("-----------------------------------------------------------------------------------");
			System.out.printf("총 자산 합계: %,15d원\n", total);
		}

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	}
}
