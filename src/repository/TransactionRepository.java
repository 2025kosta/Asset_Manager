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
	private final Map<UUID, Transaction> record = new HashMap<>();

	// create
	public Transaction save(Transaction transaction) {
		if (transaction.getId() == null) {
			transaction.setId(UUID.randomUUID());
		}
		record.put(transaction.getId(), transaction);
		return transaction;
	}

	public Optional<Transaction> findById(UUID transactionId, Users user) {
		Transaction transaction = record.get(transactionId);
		if (transaction != null && transaction.getUsers().getId().equals(user.getId())) {
			return Optional.of(transaction);
		}
		return Optional.empty();
	}

	// delete by id
	public void deleteById(UUID id, Users user) {
		findById(id, user).ifPresent(transaction -> record.remove(id));
	}

	public boolean existsByCategoryId(UUID categoryId, Users user) {
		return record.values().stream().filter(t -> t.getUsers().equals(user.getId()))
				.anyMatch(t -> t.getCategoryId().equals(categoryId)); // ◀️ == 대신 .equals() 사용
	}

	public List<Transaction> findByConditions(Users user, LocalDate start, LocalDate end, UUID assetId, UUID categoryId,
			Long min, Long max) {
		Stream<Transaction> stream = record.values().stream();

		stream = stream.filter(t -> t.getUsers().getId().equals(user.getId()));

		if (start != null) {
			stream = stream.filter(t -> !t.getDateTime().toLocalDate().isBefore(start));
		}
		if (end != null) {
			stream = stream.filter(t -> !t.getDateTime().toLocalDate().isAfter(end));
		}
		if (assetId != null) {
			stream = stream.filter(t -> assetId.equals(t.getAssetId()) || assetId.equals(t.getToAssetId()));
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
