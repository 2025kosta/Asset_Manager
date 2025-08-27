package repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import domain.Transaction;
import domain.Users;

public class TransactionRepository {

	// 인메모리 DB (거래 ID -> 거래)
	private static final Map<UUID, Transaction> record = new HashMap<>();

	// Create
	public Transaction save(Transaction transaction) {
		if (transaction.getId() == null) {
			transaction.setId(UUID.randomUUID());
		}
		record.put(transaction.getId(), transaction);
		return transaction;
	}

	// Read by id (해당 사용자 소유만)
	public Optional<Transaction> findById(UUID transactionId, Users user) {
		Transaction transaction = record.get(transactionId);
		if (transaction != null && transaction.getUsers().getId().equals(user.getId())) {
			return Optional.of(transaction);
		}
		return Optional.empty();
	}

	// Delete by id (해당 사용자 소유만)
	public void deleteById(UUID id, Users user) {
		findById(id, user).ifPresent(tx -> record.remove(id));
	}

	// 카테고리 사용 여부 확인 (버그 수정: 사용자 비교는 getId()로)
	public boolean existsByCategoryId(UUID categoryId, Users user) {
		return record.values().stream()
				.filter(t -> t.getUsers().getId().equals(user.getId()))
				.anyMatch(t -> t.getCategoryId().equals(categoryId));
	}

	// 조건 검색
	public List<Transaction> findByConditions(
			Users user,
			LocalDate start,
			LocalDate end,
			UUID assetId,
			UUID categoryId,
			Long min,
			Long max
	) {
		Stream<Transaction> stream = record.values().stream()
				.filter(t -> t.getUsers().getId().equals(user.getId()));

		if (start != null) {
			stream = stream.filter(t -> !t.getDateTime().toLocalDate().isBefore(start));
		}
		if (end != null) {
			stream = stream.filter(t -> !t.getDateTime().toLocalDate().isAfter(end));
		}
		if (assetId != null) {
			stream = stream.filter(t ->
					assetId.equals(t.getAssetId()) || assetId.equals(t.getToAssetId())
			);
		}
		if (categoryId != null) {
			stream = stream.filter(t -> categoryId.equals(t.getCategoryId()));
		}
		if (min != null) {
			stream = stream.filter(t -> t.getAmount() >= min);
		}
		if (max != null) {
			stream = stream.filter(t -> t.getAmount() <= max);
		}

		return stream.collect(Collectors.toList());
	}
}
