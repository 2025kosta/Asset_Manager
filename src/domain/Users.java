package domain;

import java.util.UUID;

public class Users {
	private UUID id;
	private String name;
	private String email;

	public Users(String name, String email) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.email = email;
	}

	// Getter / Setter
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Users{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + '}';
	}

}
