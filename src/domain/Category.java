package domain;

public class Category {
	private int Id;
	private String name;
	private CategoryKind category;

	// Getter
	public int getId() {
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

	public void setId(int Id) {
		Id = Id;
	}

	// Constructor
	public Category(String name, CategoryKind category) {
		this.name = name;
		this.category = category;
	}

}
