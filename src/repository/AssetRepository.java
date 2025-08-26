package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import domain.Asset;
import domain.Users;

public class AssetRepository {
	private final Map<UUID, List<Asset>> assetMap = new HashMap<>();

	// 저장
	public void save(Asset asset) {
		assetMap.computeIfAbsent(asset.getUsers().getId(), k -> new ArrayList<>()).add(asset);
	}

	// 특정 사용자 자산 전체 조회
	public List<Asset> findByUser(Users user) {
		return assetMap.getOrDefault(user.getId(), new ArrayList<>());
	}

	// 동일한 이름+유형 자산 존재 여부
	public boolean exists(Users user, String name, String type) {
		return findByUser(user).stream()
				.anyMatch(a -> a.getName().equalsIgnoreCase(name) && a.getType().equalsIgnoreCase(type));
	}

	// index 기반 자산 조회
	public Optional<Asset> findByIndex(Users user, int index) {
		List<Asset> list = findByUser(user);
		if (index < 0 || index >= list.size()) {
			return Optional.empty();
		}
		return Optional.of(list.get(index));
	}

	// index 기반 삭제
	public boolean deleteByIndex(Users user, int index) {
		List<Asset> list = findByUser(user);
		if (index < 0 || index >= list.size()) {
			return false;
		}
		list.remove(index);
		return true;
	}

	// Asset 객체 자체로 삭제
	public boolean delete(Asset target) {
		List<Asset> list = assetMap.get(target.getUsers().getId());
		if (list != null) {
			return list.remove(target);
		}
		return false;
	}

	// 유저 탈퇴 시 자산 전체 삭제
	public void deleteAllByUser(Users user) {
		assetMap.remove(user.getId());
	}
}
