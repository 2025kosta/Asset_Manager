package domain;

import enums.CategoryKind;

import java.time.LocalDateTime;
import java.util.UUID;


public class Transaction {
	private UUID id;
	private Users users;
	private CategoryKind type;
	private long amount;
	private LocalDateTime dateTime;
	private String memo;
	private UUID categoryId;
	private UUID assetId;
	private UUID toAssetId;

	// 상대가 없는 지출 생성자
	public Transaction(Users user, CategoryKind type, long amount, LocalDateTime dateTime, String memo, UUID categoryId,
			UUID assetId) {
		this.users = user;
		this.type = type;
		this.amount = amount;
		this.dateTime = dateTime;
		this.memo = memo;
		this.categoryId = categoryId;
		this.assetId = assetId;
	}

	// 상대가 있는 이체 생성자
	public Transaction(Users user, CategoryKind type, long amount, LocalDateTime dateTime, String memo, UUID categoryId,
			UUID assetId, UUID toAssetId) {
		this.users = user;
		this.type = type;
		this.amount = amount;
		this.dateTime = dateTime;
		this.memo = memo;
		this.categoryId = categoryId;
		this.assetId = assetId;
		this.toAssetId = toAssetId;
	}

	// getter

	public UUID getId() {
		return id;
	}

	public Users getUsers() {
		return users;
	}

	public CategoryKind getType() {
		return type;
	}

	public long getAmount() {
		return amount;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public String getMemo() {
		return memo;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public UUID getAssetId() {
		return assetId;
	}

	public UUID getToAssetId() {
		return toAssetId;
	}

	// setter
	public void setId(UUID id) {
		this.id = id;
	}

}
