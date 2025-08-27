package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import domain.Asset;
import domain.CategoryKind;
import domain.Transaction;
import domain.Users;
import repository.AssetRepository;
import repository.TransactionRepository;

public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final AssetRepository assetRepository;

	public TransactionService(TransactionRepository transactionRepository, AssetRepository assetRepository) {
		this.transactionRepository = transactionRepository;
		this.assetRepository = assetRepository;
	}

	// --- 수입 기록 추가 ---
	public String addIncome(Users user, long amount, LocalDateTime dateTime, String memo, UUID categoryId,
			UUID assetId) {
		Optional<Asset> oAsset = assetRepository.findById(user, assetId);
		if (oAsset.isEmpty()) {
			return "❌ 해당 번호의 자산을 찾을 수 없습니다.";
		}
		Asset asset = oAsset.get();
		asset.setBalance(asset.getBalance() + amount);

		Transaction transaction = new Transaction(user, CategoryKind.INCOME, amount, dateTime, memo, categoryId,
				assetId, null);
		transactionRepository.save(transaction);
		return "✅ 수입 기록이 등록되었습니다.";
	}

	// --- 지출 기록 추가 ---
	public String addExpense(Users user, long amount, LocalDateTime dateTime, String memo, UUID categoryId,
			UUID assetId) {
		Optional<Asset> oAsset = assetRepository.findById(user, assetId);
		if (oAsset.isEmpty()) {
			return "❌ 해당 번호의 자산을 찾을 수 없습니다.";
		}
		Asset asset = oAsset.get();
		if (asset.getBalance() < amount) {
			return "❌ 잔고가 부족합니다.";
		}
		asset.setBalance(asset.getBalance() - amount);

		Transaction transaction = new Transaction(user, CategoryKind.EXPENSE, amount, dateTime, memo, categoryId,
				assetId, null);
		transactionRepository.save(transaction);
		return "✅ 지출 기록이 등록되었습니다.";
	}

	// --- 이체 기록 추가 ---
	public String addTransfer(Users user, long amount, LocalDateTime dateTime, String memo, UUID categoryId,
			UUID fromAssetId, UUID toAssetId) {
		Optional<Asset> oFromAsset = assetRepository.findById(user, fromAssetId);
		Optional<Asset> oToAsset = assetRepository.findById(user, toAssetId);
		if (oFromAsset.isEmpty() || oToAsset.isEmpty()) {
			return "❌ 해당 번호의 자산을 찾을 수 없습니다.";
		}
		Asset fromAsset = oFromAsset.get();
		Asset toAsset = oToAsset.get();
		if (fromAsset.getBalance() < amount) {
			return "❌ 잔고가 부족합니다.";
		}
		fromAsset.setBalance(fromAsset.getBalance() - amount);
		toAsset.setBalance(toAsset.getBalance() + amount);

		Transaction transaction = new Transaction(user, CategoryKind.TRANSFER, amount, dateTime, memo, categoryId,
				fromAssetId, toAssetId);
		transactionRepository.save(transaction);
		return "✅ 이체 기록이 등록되었습니다.";
	}

	public String deleteTransaction(Users user, UUID transactionId) {
		Optional<Transaction> oTran = transactionRepository.findById(transactionId, user);
		if (oTran.isEmpty()) {
			return "❌ 해당 번호의 기록을 찾을 수 없습니다.";
		}
		Transaction tx = oTran.get();
		long amount = tx.getAmount();

		switch (tx.getType()) {
		case INCOME -> {
			assetRepository.findById(user, tx.getAssetId()).ifPresent(asset -> {
				asset.setBalance(asset.getBalance() - amount);
			});
		}
		case EXPENSE -> {
			assetRepository.findById(user, tx.getAssetId()).ifPresent(asset -> {
				asset.setBalance(asset.getBalance() + amount);
			});
		}
		case TRANSFER -> {
			assetRepository.findById(user, tx.getAssetId()).ifPresent(fromAsset -> {
				fromAsset.setBalance(fromAsset.getBalance() + amount);
			});
			assetRepository.findById(user, tx.getToAssetId()).ifPresent(toAsset -> {
				toAsset.setBalance(toAsset.getBalance() - amount);
			});
		}
		}
		transactionRepository.deleteById(transactionId, user);
		return "✅ 기록이 삭제되었습니다. 자산 잔액 변동이 롤백되었습니다.";
	}

	public List<Transaction> searchTransactions(Users user, LocalDate start, LocalDate end, UUID assetId,
			UUID categoryId, Long min, Long max) {
		List<Transaction> transactions = transactionRepository.findByConditions(user, start, end, assetId, categoryId,
				min, max);
		transactions.sort(Comparator.comparing(Transaction::getDateTime).reversed());
		return transactions;
	}
}