package com.pporte.model;

public class PricingDetails {
	private String planId;
	private String usetType;
	private String planType;
	private String planDesc;
	private String variableFee;
	private String slabApplicable;
	private String status;
	private String payType;
	private String minimumTxnAmount;
	private String planValue;
	private String isDefault;
	private String payMode;
	private String fixedFee;

	
	public String getPlanId() {
		return planId;
	}
	public String getUsetType() {
		return usetType;
	}
	public String getPlanType() {
		return planType;
	}
	public String getPlanDesc() {
		return planDesc;
	}
	public String getVariableFee() {
		return variableFee;
	}
	public String getSlabApplicable() {
		return slabApplicable;
	}
	public String getStatus() {
		return status;
	}
	public String getPayType() {
		return payType;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public void setUsetType(String usetType) {
		this.usetType = usetType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}
	public void setVariableFee(String variableFee) {
		this.variableFee = variableFee;
	}
	public void setSlabApplicable(String slabApplicable) {
		this.slabApplicable = slabApplicable;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getMinimumTxnAmount() {
		return minimumTxnAmount;
	}
	public void setMinimumTxnAmount(String minimumTxnAmount) {
		this.minimumTxnAmount = minimumTxnAmount;
	}
	public String getPlanValue() {
		return planValue;
	}
	public void setPlanValue(String planValue) {
		this.planValue = planValue;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getFixedFee() {
		return fixedFee;
	}
	public void setFixedFee(String fixedFee) {
		this.fixedFee = fixedFee;
	}

}
