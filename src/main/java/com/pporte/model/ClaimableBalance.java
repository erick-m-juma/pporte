package com.pporte.model;

import java.util.ArrayList;

public class ClaimableBalance {
	private String claimableBalanceId;
	private String assetCode;
	private String assetIssuer;
	private String amount;
	private String sourceAccount;
	private int ledgerNo;
	private String createdOn;
	private String paginationToken;
	private ArrayList<String> claimants;
	
	public String getClaimableBalanceId() {
		return claimableBalanceId;
	}
	public void setClaimableBalanceId(String claimableBalanceId) {
		this.claimableBalanceId = claimableBalanceId;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public String getAssetIssuer() {
		return assetIssuer;
	}
	public void setAssetIssuer(String assetIssuer) {
		this.assetIssuer = assetIssuer;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSourceAccount() {
		return sourceAccount;
	}
	public void setSourceAccount(String sourceAccount) {
		this.sourceAccount = sourceAccount;
	}
	public int getLedgerNo() {
		return ledgerNo;
	}
	public void setLedgerNo(int ledgerNo) {
		this.ledgerNo = ledgerNo;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getPaginationToken() {
		return paginationToken;
	}
	public void setPaginationToken(String paginationToken) {
		this.paginationToken = paginationToken;
	}
	public ArrayList<String> getClaimants() {
		return claimants;
	}
	public void setClaimants(ArrayList<String> claimants) {
		this.claimants = claimants;
	}
	
	
	
	

}
