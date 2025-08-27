package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Category;
import enums.CategoryKind;
import domain.Users;

public class CategoryRepository {

	// 인메모리 DB (카테고리 ID -> 카테고리)
	private static final Map<UUID, Category> categories = new HashMap<>();

	// Create
	public Category save(Category category) {
		if (category.getId() == null) {
			category.setId(UUID.randomUUID());
		}
		categories.put(category.getId(), category);
		return category;
	}

	// Read by id (해당 사용자 소유만)
	public Optional<Category> findById(UUID id, Users user) {
		Category category = categories.get(id);
		if (category != null && category.getUsers().getId().equals(user.getId())) {
			return Optional.of(category);
		}
		return Optional.empty();
	}

	// 동일 타입/이름 카테고리 존재 여부
	public Optional<Category> findByType(Users user, String name, CategoryKind kind) {
		return categories.values().stream()
				.filter(c -> c.getUsers().getId().equals(user.getId()))
				.filter(c -> c.getName().equals(name) && c.getCategory().equals(kind))
				.findAny();
	}

	// 사용자별 전체 조회
	public List<Category> findByAll(Users user) {
		return categories.values().stream()
				.filter(c -> c.getUsers().getId().equals(user.getId()))
				.collect(Collectors.toList());
	}

	// Delete by id (해당 사용자 소유만)
	public void deleteById(UUID id, Users user) {
		findById(id, user).ifPresent(category -> categories.remove(id));
	}
}
