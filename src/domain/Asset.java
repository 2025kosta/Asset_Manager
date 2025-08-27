package domain;

import java.util.UUID;

public class Asset {
	private UUID id;
	private Users users;
	private String name;
	private String type;
	private long balance;

	public Asset(Users users, String name, String type, long balance) {
		this.id = UUID.randomUUID();
		this.users = users;
		this.name = name;
		this.type = type;
		this.balance = balance;
	}

	// Getter / Setter
	public UUID getId() {
		return id;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Assets{" + "id=" + id + ", users=" + users.getName() + ", name='" + name + '\'' + ", type='" + type
				+ '\'' + ", balance=" + balance + '}';
	}

}
