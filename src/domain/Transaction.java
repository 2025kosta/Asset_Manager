package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
	private UUID id;
	private CategoryKind type;
	private long amount;
	private LocalDateTime dateTime;
	private String memo;
	private int categoryId;
	private int assetId;
	private int toAssetId;

	// 상대가 없는 지출 생성자
	public Transaction(CategoryKind type, long amount, LocalDateTime dateTime, String memo, int categoryId,
			int assetId) {
		super();
		this.type = type;
		this.amount = amount;
		this.dateTime = dateTime;
		this.memo = memo;
		this.categoryId = categoryId;
		this.assetId = assetId;
	}

	// 상대가 있는 이체 생성자
	public Transaction(CategoryKind type, long amount, LocalDateTime dateTime, String memo, int categoryId, int assetId,
			int toAssetId) {
		super();
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

	public int getCategoryId() {
		return categoryId;
	}

	public int getAssetId() {
		return assetId;
	}

	public int getToAssetId() {
		return toAssetId;
	}

	// setter
	public void setId(UUID id) {
		this.id = id;
	}

}
