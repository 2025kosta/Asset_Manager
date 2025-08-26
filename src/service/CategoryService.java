package service;

import repository.CategoryRepository;
import repository.TransactionRepository;

public class CategoryService {

	private CategoryRepository categoryRepository = new CategoryRepository();
	private TransactionRepository transactionRepository = new TransactionRepository();

	public void initDefaultCategory() {
		if (categoryRepository.findAll().isEmpty()) {
			categoryRepository.save(null)
		}
	}
}
