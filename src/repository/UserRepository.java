package repository;

import java.util.HashMap;
import java.util.Map;

import domain.Users;

public class UserRepository {
	private final Map<String, Users> userMap;

	public UserRepository() {
		this.userMap = new HashMap<>();
	}

	public void save(Users user) {
		userMap.put(user.getEmail().toLowerCase(), user);
	}

	public boolean existsByEmail(String email) {
		return userMap.containsKey(email.toLowerCase());
	}

	public Users findByEmail(String email) {
		return userMap.get(email.toLowerCase());
	}

	public void deleteByEmail(String email) {
		userMap.remove(email.toLowerCase());
	}

}
