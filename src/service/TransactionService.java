package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import domain.Asset;
import enums.CategoryKind;
import domain.Transaction;
import domain.Users;
import repository.AssetRepository;
import repository.TransactionRepository;

public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final AssetRepository assetRepository;

	public TransactionService() {
		this.transactionRepository = new TransactionRepository();
		this.assetRepository = new AssetRepository();
	}

	public TransactionService(TransactionRepository t, AssetRepository a) {
		this.transactionRepository = t; this.assetRepository = a;
	}

	public String addIncome(Users user, long amount, LocalDateTime dateTime,
							String memo, UUID categoryId, UUID assetId) {
		Optional<Asset> o = assetRepository.findById(user, assetId);
		if (o.isEmpty()) return "❌ 해당 번호의 자산을 찾을 수 없습니다.";
		Asset asset = o.get();
		asset.setBalance(asset.getBalance() + amount);
		transactionRepository.save(
				new Transaction(user, CategoryKind.INCOME, amount, dateTime,
						memo, categoryId, assetId, null));
		return "✅ 수입 기록이 등록되었습니다.";
	}

	public String addExpense(Users user, long amount, LocalDateTime dateTime,
							 String memo, UUID categoryId, UUID assetId) {
		Optional<Asset> o = assetRepository.findById(user, assetId);
		if (o.isEmpty()) return "❌ 해당 번호의 자산을 찾을 수 없습니다.";
		Asset asset = o.get();
		if (asset.getBalance() < amount)
			return "❌ 잔고가 부족합니다.";
		asset.setBalance(asset.getBalance() - amount);
		transactionRepository.save(
				new Transaction(user, CategoryKind.EXPENSE, amount,
						dateTime, memo, categoryId, assetId, null));
		return "✅ 지출 기록이 등록되었습니다.";
	}
	public String addTransfer(Users user, long amount, LocalDateTime dateTime,
							  String memo, UUID categoryId, UUID fromAssetId,
							  UUID toAssetId) {
		Optional<Asset> of = assetRepository.findById(user, fromAssetId);
		Optional<Asset> ot = assetRepository.findById(user, toAssetId);
		if (of.isEmpty() || ot.isEmpty())
			return "❌ 해당 번호의 자산을 찾을 수 없습니다.";
		Asset from = of.get(), to = ot.get();
		if (from.getBalance() < amount)
			return "❌ 잔고가 부족합니다.";
		from.setBalance(from.getBalance() - amount);
		to.setBalance(to.getBalance() + amount);
		transactionRepository.save(
				new Transaction(user, CategoryKind.TRANSFER, amount,
						dateTime, memo, categoryId, fromAssetId, toAssetId));
		return "✅ 이체 기록이 등록되었습니다.";
	}
	public String deleteTransaction(Users user, UUID id) {
		Optional<Transaction> o = transactionRepository.findById(id, user);
		if (o.isEmpty())
			return "❌ 해당 번호의 기록을 찾을 수 없습니다.";
		Transaction tx = o.get();
		long amount = tx.getAmount();
		switch (tx.getType()) {
			case INCOME ->
					assetRepository.findById(user, tx.getAssetId())
							.ifPresent(a -> a.setBalance(a.getBalance() - amount));
			case EXPENSE ->
					assetRepository.findById(user, tx.getAssetId())
							.ifPresent(a -> a.setBalance(a.getBalance() + amount));
			case TRANSFER -> {
				assetRepository.findById(user, tx.getAssetId())
						.ifPresent(a -> a.setBalance(a.getBalance() + amount));
				assetRepository.findById(user, tx.getToAssetId())
						.ifPresent(a -> a.setBalance(a.getBalance() - amount));
			}
		}
		transactionRepository.deleteById(id, user);
		return "✅ 기록이 삭제되었습니다. 자산 잔액 변동이 롤백되었습니다.";
	}

	public List<Transaction> searchTransactions(Users user, LocalDate start,
												LocalDate end, UUID assetId,
												UUID categoryId, Long min, Long max) {
		List<Transaction> list = transactionRepository.findByConditions(
				user, start, end, assetId, categoryId, min, max);
		list.sort(Comparator.comparing(Transaction::getDateTime).reversed());
		return list;
	}

	public void deleteAllByUser(Users user) {
		transactionRepository.deleteAllByUser(user);
	}
}
