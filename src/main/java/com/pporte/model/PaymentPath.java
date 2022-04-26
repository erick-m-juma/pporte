package com.pporte.model;

public class PaymentPath {
	private String assetType;
	private String assetCode;
	private String assetIssuers;
	
	public String getAssetType() {
		return assetType;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public String getAssetIssuers() {
		return assetIssuers;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public void setAssetIssuers(String assetIssuers) {
		this.assetIssuers = assetIssuers;
	}
}
