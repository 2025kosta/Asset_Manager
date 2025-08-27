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

	// 인메모리 DB (사용자별 자산 목록)
	private static final Map<UUID, List<Asset>> assetMap = new HashMap<>();

	// 저장
	public void save(Asset asset) {
		assetMap
				.computeIfAbsent(asset.getUsers().getId(), k -> new ArrayList<>())
				.add(asset);
	}

	// 특정 사용자 자산 전체 조회
	public List<Asset> findByUser(Users user) {
		return assetMap.getOrDefault(user.getId(), new ArrayList<>());
	}

	// 동일한 이름+유형 자산 존재 여부
	public boolean exists(Users user, String name, String type) {
		return findByUser(user).stream()
				.anyMatch(a ->
						a.getName().equalsIgnoreCase(name) &&
								a.getType().equalsIgnoreCase(type)
				);
	}

	// 자산 조회
	public Optional<Asset> findById(Users user, UUID id) {
		return findByUser(user).stream()
				.filter(asset -> asset.getId().equals(id))
				.findFirst();
	}

	// 삭제 (ID 기준)
	public boolean deleteById(Users user, UUID id) {
		return findByUser(user).removeIf(asset -> asset.getId().equals(id));
	}

	// 삭제 (객체 기준)
	public boolean delete(Asset target) {
		List<Asset> list = assetMap.get(target.getUsers().getId());
		return list != null && list.remove(target);
	}

	// 유저 탈퇴 시 자산 전체 삭제
	public void deleteAllByUser(Users user) {
		assetMap.remove(user.getId());
	}
}
