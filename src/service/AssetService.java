package service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import domain.Asset;
import domain.Users;
import repository.AssetRepository;
// import repository.RecordRepository;

public class AssetService {
	private final AssetRepository assetRepository;
	// private final RecordRepository recordRepository;

	public AssetService() {
		this.assetRepository = new AssetRepository();
		// this.recordRepository = new RecordRepository();
	}

	public String createAsset(Users user, String name, String type, long balance) {
		if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()) {
			return "❌ 이름과 유형은 반드시 입력해야 합니다.";
		}

		boolean exists = assetRepository.findByUser(user).stream()
				.anyMatch(a -> a.getName().equalsIgnoreCase(name.trim()) && a.getType().equalsIgnoreCase(type.trim()));

		if (exists) {
			return "❌ 동일한 이름과 유형의 자산이 이미 존재합니다.";
		}

		Asset asset = new Asset(user, name.trim(), type.trim(), balance);
		assetRepository.save(asset);
		return "✅ 자산이 등록되었습니다.";
	}

	public List<Asset> findAssetsByUser(Users user) {
		return assetRepository.findByUser(user).stream()
				.sorted(Comparator.comparing(Asset::getType).thenComparing(Asset::getName))
				.collect(Collectors.toList());
	}

	public String updateAsset(Users user, int index, String newName, String newType) {
		List<Asset> assets = findAssetsByUser(user);
		if (index < 0 || index >= assets.size()) {
			return "❌ 잘못된 번호입니다.";
		}
		Asset asset = assets.get(index);

		String updatedName = newName.isBlank() ? asset.getName() : newName.trim();
		String updatedType = newType.isBlank() ? asset.getType() : newType.trim();

		// 기존 정보와 다르면 중복 체크
		if (assets.stream().anyMatch(a -> a != asset && a.getName().equalsIgnoreCase(updatedName)
				&& a.getType().equalsIgnoreCase(updatedType))) {
			return "❌ 동일한 이름과 유형의 자산이 이미 존재합니다.";
		}

		asset.setName(updatedName);
		asset.setType(updatedType);
		return "✅ 자산이 수정되었습니다.";
	}

	public String deleteAsset(Users user, int index) {
		List<Asset> assets = findAssetsByUser(user);
		if (index < 0 || index >= assets.size()) {
			return "❌ 잘못된 번호입니다.";
		}
		Asset target = assets.get(index);

		// TODO: 기록 연결 여부 확인 후 삭제 (현재는 생략)
		// boolean hasRecord = recordRepository.existsByAsset(target);
		// if (hasRecord) {
		// return "❌ 연결된 기록이 있어 삭제할 수 없습니다.";
		// }

		assetRepository.delete(target);
		return "✅ 자산이 삭제되었습니다.";
	}

	public void deleteAllByUser(Users user) {
		assetRepository.deleteAllByUser(user);
	}

}
