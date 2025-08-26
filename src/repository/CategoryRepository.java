package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import domain.Category;
import domain.CategoryKind;

public class CategoryRepository {
	private static Map<UUID, Category> categories = new HashMap<>();
	private static int cnt = 0;

	// Create
	public Category save(Category category) {
		if (category.getId() == null) {
			cnt++;
			category.setId(UUID.randomUUID());
		}
		categories.put(category.getId(), category);
		return category;
	}

	// Read id
	public Optional<Category> findById(int id) {
		return Optional.ofNullable(categories.get(id));
	}

	// find same
	public Optional<Category> findByNameandType(String name, CategoryKind category) {
		return categories.values().stream().filter(c -> c.getName().equals(name) && c.getCategory().equals(category))
				.findAny();
	}

	// read all
	public List<Category> findAll() {
		return new ArrayList<>(categories.values());
	}

	// delete
	public void deleteById(int id) {
		categories.remove(id);
	}

}
