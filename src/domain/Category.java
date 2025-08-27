package domain;

import java.util.UUID;

public class Category {
	private UUID Id;
	private Users users;
	private String name;
	private CategoryKind category;

	// Getter
	public Users getUsers() {
		return users;
	}

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
	public Category(Users user, String name, CategoryKind category) {
		this.users = user;
		this.name = name;
		this.category = category;
	}

}
