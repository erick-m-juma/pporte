package com.pporte.model;

public class TransactionLimitDetails {
	
	private String txnLimitId;
	private String limitType;
	private String limitAmount;
	private String limitDescription;
	private String status;
	private String createdOn;
	private String userType;
	public String getTxnLimitId() {
		return txnLimitId;
	}
	public void setTxnLimitId(String txnLimitId) {
		this.txnLimitId = txnLimitId;
	}
	public String getLimitType() {
		return limitType;
	}
	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}
	public String getLimitAmount() {
		return limitAmount;
	}
	public void setLimitAmount(String limitAmount) {
		this.limitAmount = limitAmount;
	}
	public String getLimitDescription() {
		return limitDescription;
	}
	public void setLimitDescription(String limitDescription) {
		this.limitDescription = limitDescription;
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
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	


}
