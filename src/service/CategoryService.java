package service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import domain.Category;
import domain.CategoryKind;
import repository.CategoryRepository;
import repository.TransactionRepository;

public class CategoryService {

	private CategoryRepository categoryRepository = new CategoryRepository();
	private TransactionRepository transactionRepository = new TransactionRepository();

	public void initDefaultCategory() {
		if (categoryRepository.findAll().isEmpty()) {
			categoryRepository.save(new Category("식비", CategoryKind.EXPENSE));
			categoryRepository.save(new Category("급여", CategoryKind.INCOME));
			categoryRepository.save(new Category("교통", CategoryKind.EXPENSE));
		}
	}

	public String registerCategory(String name, CategoryKind ck) {
		if (categoryRepository.findByNameandType(name, ck).isPresent()) {
			return "❌ 이미 존재하는 카테고리 입니다.";
		} else {
			Category newCategory = categoryRepository.save(new Category(name, ck));
			return String.format("✅카테고리가 등록되었습니다. ( 이름: %s , 종류: %s )", newCategory.getName(),
					newCategory.getCategory());
		}
	}

	public String updateCategoryName(UUID id, String newName) {
		Optional<Category> oCategory = categoryRepository.findById(id);
		if (oCategory.isEmpty()) {
			return "❌ 해당 카테고리를 찾을 수 없습니다.";
		}
		Category category = oCategory.get();
		category.setName(newName);
		categoryRepository.save(category);
		return String.format("✅ 카테고리 이름이 수정되었습니다. ( 이름: %s )", category.getName());
	}

	public String deleteCategory(UUID id) {
		if (categoryRepository.findById(id).isEmpty()) {
			return "❌ 해당 카테고리를 찾을 수 없습니다.";
		}
		if (transactionRepository.existCategory(id)) {
			return "❌ 기록이 있어 카테고리를 삭제할 수 없습니다.";
		}
		categoryRepository.deleteById(id);
		return "✅ 카테고리가 삭제되었습니다.";
	}

	public List<Category> getSortedCategories() {
		List<Category> category = categoryRepository.findAll();
		category.sort(Comparator.comparing(Category::getCategory));
		return category;
	}
}
