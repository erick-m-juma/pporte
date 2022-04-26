package com.pporte.model;

public class AssetAccount {
	private String accountId;
	private String assetType;
	private String assetCode;
	private String assetBalance;
	private String status;
	private String createdOn;
	private String publicKey;
	private String accountType;
	public String getAssetType() {
		return assetType;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public String getAssetBalance() {
		return assetBalance;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public void setAssetBalance(String assetBalance) {
		this.assetBalance = assetBalance;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}


}
