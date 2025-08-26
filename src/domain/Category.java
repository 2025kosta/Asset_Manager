package domain;

import java.util.UUID;

public class Category {
	private UUID Id;
	private String name;
	private CategoryKind category;

	// Getter
	public UUID getId() {
		return Id;
	}

	public CategoryKind getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	// Setter except category(수정불가)
	public void setName(String name) {
		this.name = name;
	}

	public void setId(UUID id) {
		Id = id;
	}

	// Constructor
	public Category(String name, CategoryKind category) {
		this.name = name;
		this.category = category;
	}

}
