package service;

import java.util.regex.Pattern;
import domain.Users;
import repository.UserRepository;

public class UserService {
	private final UserRepository userRepository;
	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
	private static final Pattern NAME_PATTERN =
			Pattern.compile("^[가-힣a-zA-Z]{2,20}$");

	// 기본 생성자에서 UserRepository 초기화 (의존성 주입)
	public UserService() {
		this.userRepository = new UserRepository();
	}
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String createUser(String name, String email) {
		if (name == null || email == null)
			return "❌ 이름 또는 이메일이 null입니다.";

		name = name.trim(); email = email.trim();

		if (name.isEmpty() || email.isEmpty())
			return "❌ 이름과 이메일은 공백일 수 없습니다.";

		if (!NAME_PATTERN.matcher(name).matches())
			return "❌ 이름은 한글 또는 영문 2~20자여야 합니다.";

		if (!EMAIL_PATTERN.matcher(email).matches())
			return "❌ 올바른 이메일 형식이 아닙니다.";

		if (userRepository.existsByEmail(email))
			return "❌ 이미 존재하는 이메일입니다.";

		userRepository.save(new Users(name, email));
		return "✅ 사용자가 성공적으로 생성되었습니다.";
	}

	public Users findByEmail(String email) {
		return (email == null || email.trim().isEmpty())
				? null : userRepository.findByEmail(email.trim());
	}

	public void deleteUser(Users user) {
		userRepository.deleteByEmail(user.getEmail());
	}
}
