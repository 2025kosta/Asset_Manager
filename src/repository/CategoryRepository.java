package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Category;
import domain.CategoryKind;
import domain.Users;

public class CategoryRepository {
	private final Map<UUID, Category> categories = new HashMap<>();

	// Create
	public Category save(Category category) {
		if (category.getId() == null) {
			category.setId(UUID.randomUUID());
		}
		categories.put(category.getId(), category);
		return category;
	}

	// Read id
	public Optional<Category> findById(UUID id, Users user) {
		Category category = categories.get(id);
		if (category != null && category.getUsers().getId().equals(user.getId())) {
			return Optional.of(category);
		}
		return Optional.empty();
	}

	// find same
	public Optional<Category> findByType(Users user, String name, CategoryKind category) {
		return categories.values().stream().filter(c -> c.getUsers().getId().equals(user.getId()))
				.filter(c -> c.getName().equals(name) && c.getCategory().equals(category)).findAny();
	}

	// read all
	public List<Category> findByAll(Users user) {
		return categories.values().stream().filter(category -> category.getUsers().getId().equals(user.getId()))
				.collect(Collectors.toList());
	}

	// delete
	public void deleteById(UUID id, Users user) {
		findById(id, user).ifPresent(category -> categories.remove(id));
	}

}
