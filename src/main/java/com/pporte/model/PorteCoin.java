package com.pporte.model;

public class PorteCoin {
	private String walletId;
	private String currentBalance;
	private String lastUpdated;
	private String relationshipNo;
	private String blockCodeId;
	private String assetCode;
	private String assetDescription;
	private String status;
	private String assetType;
	private String walletType;
	private String createdOn;
	private String publicKey;
	private String sercretKey;
	
	public String getWalletId() {
		return walletId;
	}
	public String getCurrentBalance() {
		return currentBalance;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public String getRelationshipNo() {
		return relationshipNo;
	}
	public String getBlockCodeId() {
		return blockCodeId;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public String getAssetDescription() {
		return assetDescription;
	}
	public String getStatus() {
		return status;
	}
	public String getAssetType() {
		return assetType;
	}
	public String getWalletType() {
		return walletType;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public void setRelationshipNo(String relationshipNo) {
		this.relationshipNo = relationshipNo;
	}
	public void setBlockCodeId(String blockCodeId) {
		this.blockCodeId = blockCodeId;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getSercretKey() {
		return sercretKey;
	}
	public void setSercretKey(String sercretKey) {
		this.sercretKey = sercretKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	

}
