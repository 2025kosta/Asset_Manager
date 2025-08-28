package service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Asset;
import domain.Users;
import repository.AssetRepository;
import repository.TransactionRepository;

public class AssetService {
	private final AssetRepository assetRepository;
	private final TransactionRepository transactionRepository;

	public AssetService() {
		this.assetRepository = new AssetRepository();
		this.transactionRepository = new TransactionRepository();
	}

	public AssetService(AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
		this.transactionRepository = new TransactionRepository();
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

	public List<Asset> findAssetsByUserAndType(Users user, String type) {
		if (type == null || type.trim().isEmpty()) {
			return findAssetsByUser(user);
		}
		String want = type.trim();
		return assetRepository.findByUser(user).stream()
				.filter(a -> a.getType() != null && a.getType().equalsIgnoreCase(want))
				.sorted(Comparator.comparing(Asset::getType).thenComparing(Asset::getName))
				.collect(Collectors.toList());
	}

	public Optional<Asset> findById(Users user, UUID assetId) {
		return assetRepository.findById(user, assetId);
	}

	public String updateAsset(Users user, UUID assetId, String newName, String newType) {
		Optional<Asset> optionalAsset = assetRepository.findById(user, assetId);
		if (optionalAsset.isEmpty()) {
			return "❌ 자산을 찾을 수 없습니다.";
		}
		Asset asset = optionalAsset.get();

		String updatedName = newName.isBlank() ? asset.getName() : newName.trim();
		String updatedType = newType.isBlank() ? asset.getType() : newType.trim();

		boolean exists = assetRepository.findByUser(user).stream().anyMatch(a -> !a.getId().equals(asset.getId())
				&& a.getName().equalsIgnoreCase(updatedName) && a.getType().equalsIgnoreCase(updatedType));
		if (exists) {
			return "❌ 동일한 이름과 유형의 자산이 이미 존재합니다.";
		}

		asset.setName(updatedName);
		asset.setType(updatedType);
		return "✅ 자산이 수정되었습니다.";
	}

	public String deleteAsset(Users user, UUID assetId) {
		Optional<Asset> optionalAsset = assetRepository.findById(user, assetId);
		if (optionalAsset.isEmpty()) {
			return "❌ 자산을 찾을 수 없습니다.";
		}

		// ✅ 연결된 거래 기록 존재 시 삭제 불가
		boolean inUse = transactionRepository.existsByAssetId(user, assetId);
		if (inUse) {
			return "❌ 연결된 기록이 있어 삭제할 수 없습니다.";
		}

		assetRepository.delete(optionalAsset.get());
		return "✅ 자산이 삭제되었습니다.";
	}

	public void deleteAllByUser(Users user) {
		assetRepository.deleteAllByUser(user);
	}
}
