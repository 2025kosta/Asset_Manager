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

public class TransactionRepository {
	private static Map<UUID, Transaction> record = new HashMap<UUID, Transaction>();
	private static int cnt = 0;

	// create
	public Transaction save(Transaction transaction) {
		if (transaction.getId() == null) {
			cnt++;
			transaction.setId(UUID.randomUUID());
		}
		record.put(transaction.getId(), transaction);
		return transaction;
	}

	// read id
	public Optional<Transaction> readById(int id) {
		return Optional.ofNullable(record.get(id));
	}

	// check category record
	public boolean existCategory(int categoryId) {
		return record.values().stream().anyMatch(c -> c.getCategoryId() == categoryId);
	}

	public List<Transaction> findBy(LocalDate start, LocalDate end, Integer assetId, Integer toAssetId, Long min,
			Long max) {
		Stream<Transaction> stream = record.values().stream();
		if (start != null) {
			stream.filter(t -> !t.getDateTime().toLocalDate().isBefore(start));
		}
		if (end != null) {
			stream.filter(t -> !t.getDateTime().toLocalDate().isAfter(end));
		}
		if (assetId != null) {
			stream.filter(t -> t.getAssetId() == assetId);
		}
		if (toAssetId != null) {
			stream.filter(t -> t.getToAssetId() == toAssetId);
		}
		if (min != null) {
			stream.filter(t -> t.getAmount() >= min);
		}
		if (max != null) {
			stream.filter(t -> t.getAmount() <= max);
		}
		return stream.collect(Collectors.toList());
	}
}
