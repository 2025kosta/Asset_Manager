package service;

import java.util.*;
import domain.Category;
import enums.CategoryKind;
import domain.Users;
import repository.CategoryRepository;
import repository.TransactionRepository;

public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;

	public CategoryService() {
		this.categoryRepository = new CategoryRepository();
		this.transactionRepository = new TransactionRepository();
	}
	public CategoryService(CategoryRepository c, TransactionRepository t) {
		this.categoryRepository = c; this.transactionRepository = t;
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
		if (categoryRepository.findByType(user, name, ck).isPresent()) return "❌ 이미 존재하는 카테고리입니다.";
		Category saved = categoryRepository.save(new Category(user, name, ck));
		return String.format("✅ 카테고리가 등록되었습니다.", saved.getName(), saved.getCategory());
	}
	public Optional<Category> findById(Users user, UUID id) { return categoryRepository.findById(id, user); }
	public String updateCategoryName(Users user, UUID id, String newName) {
		Optional<Category> o = categoryRepository.findById(id, user);
		if (o.isEmpty()) return "❌ 해당 카테고리를 찾을 수 없습니다.";
		Category c = o.get();
		if (!c.getUsers().getId().equals(user.getId())) return "❌ 해당 카테고리를 찾을 수 없습니다.";
		c.setName(newName);
		categoryRepository.save(c);
		return String.format("✅ 카테고리 이름이 수정되었습니다.", c.getName());
	}
	public String deleteCategory(Users user, UUID id) {
		if (categoryRepository.findById(id, user).isEmpty()) return "❌ 해당 카테고리를 찾을 수 없습니다.";
		if (transactionRepository.existsByCategoryId(id, user)) return "❌ 기록이 있어 카테고리를 삭제할 수 없습니다.";
		categoryRepository.deleteById(id, user);
		return "✅ 카테고리가 삭제되었습니다.";
	}
	public List<Category> getSortedCategories(Users user) {
		List<Category> list = categoryRepository.findByAll(user);
		list.sort(Comparator.comparing(Category::getCategory));
		return list;
	}

	public void deleteAllByUser(Users user) {
		categoryRepository.deleteAllByUser(user);
	}
}
