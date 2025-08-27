package service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import domain.Category;
import domain.CategoryKind;
import domain.Users;
import repository.CategoryRepository;
import repository.TransactionRepository;

public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;

	public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
		this.categoryRepository = categoryRepository;
		this.transactionRepository = transactionRepository;
	}

	public void initDefaultCategory(Users user) {
		if (categoryRepository.findByAll(user).isEmpty()) {
			categoryRepository.save(new Category(user, "식비", CategoryKind.EXPENSE));
			categoryRepository.save(new Category(user, "급여", CategoryKind.INCOME));
			categoryRepository.save(new Category(user, "교통", CategoryKind.EXPENSE));
			categoryRepository.save(new Category(user, "이체", CategoryKind.TRANSFER));
		}
	}

	public String registerCategory(Users user, String name, CategoryKind ck) {
		if (categoryRepository.findByType(user, name, ck).isPresent()) {
			return "❌ 이미 존재하는 카테고리 입니다.";
		} else {
			Category newCategory = categoryRepository.save(new Category(user, name, ck));
			return String.format("✅카테고리가 등록되었습니다. ( 이름: %s , 종류: %s )", newCategory.getName(),
					newCategory.getCategory());
		}
	}

	public Optional<Category> findById(Users user, UUID categoryId) {
		return categoryRepository.findById(categoryId, user);

	}

	public String updateCategoryName(Users user, UUID id, String newName) {
		Optional<Category> oCategory = categoryRepository.findById(id, user);
		if (oCategory.isEmpty()) {
			return "❌ 해당 카테고리를 찾을 수 없습니다.";
		}

		Category category = oCategory.get();

		if (!category.getUsers().getId().equals(user.getId())) {
			return "❌ 해당 카테고리를 찾을 수 없습니다.";
		}

		category.setName(newName);
		categoryRepository.save(category);
		return String.format("✅ 카테고리 이름이 수정되었습니다. ( 이름: %s )", category.getName());
	}

	public String deleteCategory(Users user, UUID id) {
		Optional<Category> optCategory = categoryRepository.findById(id, user);
		if (optCategory.isEmpty()) {
			return "❌ 해당 카테고리를 찾을 수 없습니다.";
		}

		if (transactionRepository.existsByCategoryId(id, user)) {
			return "❌ 기록이 있어 카테고리를 삭제할 수 없습니다.";
		}

		categoryRepository.deleteById(id, user);
		return "✅ 카테고리가 삭제되었습니다.";
	}

	public List<Category> getSortedCategories(Users user) {
		List<Category> category = categoryRepository.findByAll(user);
		category.sort(Comparator.comparing(Category::getCategory));
		return category;
	}
}
