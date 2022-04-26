package com.pporte.model;

public class Transaction {
	private String txnCode;
	private String merchantWalletId;
	private String customerWalletId;
	private String relationshipNo;
	private String merchantCode;
	private String systemReferenceInt;
	private String systemReferenceExt;
	private String txnMode;
	private String txnAmount;
	private String txnDateTime;
	private String txnCurrencyId;
	private String tokenId;
	private String payMode;
	private String txnUserCode;
	private String txnType;
	private String customerName;
	private String pymtChannel;
	private String assetCode;
	private String txnDescription;
	private String coinAmount;
	private String customerId;
	private String publicKey;
	private String comment;
	private String status;
	
	//partners module
	private String sourceAssetCode;
	private String destinationAssetCode;
	private String sourceAmount;
	private String destinationAmount;
	private String partnersComment;
	private String senderComment;
	private String receiverName;
	private String receiverBankName;
	private String receiverBankCode;
	private String receiverAccountNo;
	private String receiverEmail;
	
	public String getTxnCode() {
		return txnCode;
	}
	public String getMerchantWalletId() {
		return merchantWalletId;
	}
	public String getCustomerWalletId() {
		return customerWalletId;
	}
	public String getRelationshipNo() {
		return relationshipNo;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public String getSystemReferenceInt() {
		return systemReferenceInt;
	}
	public String getSystemReferenceExt() {
		return systemReferenceExt;
	}
	public String getTxnMode() {
		return txnMode;
	}
	public String getTxnAmount() {
		return txnAmount;
	}
	public String getTxnDateTime() {
		return txnDateTime;
	}
	public String getTxnCurrencyId() {
		return txnCurrencyId;
	}
	public String getTokenId() {
		return tokenId;
	}
	public String getPayMode() {
		return payMode;
	}
	public String getTxnUserCode() {
		return txnUserCode;
	}
	public String getTxnType() {
		return txnType;
	}
	public String getCustomerName() {
		return customerName;
	}
	public String getPymtChannel() {
		return pymtChannel;
	}
	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}
	public void setMerchantWalletId(String merchantWalletId) {
		this.merchantWalletId = merchantWalletId;
	}
	public void setCustomerWalletId(String customerWalletId) {
		this.customerWalletId = customerWalletId;
	}
	public void setRelationshipNo(String relationshipNo) {
		this.relationshipNo = relationshipNo;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public void setSystemReferenceInt(String systemReferenceInt) {
		this.systemReferenceInt = systemReferenceInt;
	}
	public void setSystemReferenceExt(String systemReferenceExt) {
		this.systemReferenceExt = systemReferenceExt;
	}
	public void setTxnMode(String txnMode) {
		this.txnMode = txnMode;
	}
	public String getCoinAmount() {
		return coinAmount;
	}
	public void setCoinAmount(String coinAmount) {
		this.coinAmount = coinAmount;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}
	public void setTxnDateTime(String txnDateTime) {
		this.txnDateTime = txnDateTime;
	}
	public void setTxnCurrencyId(String txnCurrencyId) {
		this.txnCurrencyId = txnCurrencyId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public void setTxnUserCode(String txnUserCode) {
		this.txnUserCode = txnUserCode;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public void setPymtChannel(String pymtChannel) {
		this.pymtChannel = pymtChannel;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public String getTxnDescription() {
		return txnDescription;
	}
	public String getSourceAssetCode() {
		return sourceAssetCode;
	}
	public void setSourceAssetCode(String sourceAssetCode) {
		this.sourceAssetCode = sourceAssetCode;
	}
	public String getDestinationAssetCode() {
		return destinationAssetCode;
	}
	public void setDestinationAssetCode(String destinationAssetCode) {
		this.destinationAssetCode = destinationAssetCode;
	}
	public String getSourceAmount() {
		return sourceAmount;
	}
	public void setSourceAmount(String sourceAmount) {
		this.sourceAmount = sourceAmount;
	}
	public String getDestinationAmount() {
		return destinationAmount;
	}
	public void setDestinationAmount(String destinationAmount) {
		this.destinationAmount = destinationAmount;
	}
	public String getPartnersComment() {
		return partnersComment;
	}
	public void setPartnersComment(String partnersComment) {
		this.partnersComment = partnersComment;
	}
	public String getSenderComment() {
		return senderComment;
	}
	public void setSenderComment(String senderComment) {
		this.senderComment = senderComment;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverBankName() {
		return receiverBankName;
	}
	public void setReceiverBankName(String receiverBankName) {
		this.receiverBankName = receiverBankName;
	}
	public String getReceiverBankCode() {
		return receiverBankCode;
	}
	public void setReceiverBankCode(String receiverBankCode) {
		this.receiverBankCode = receiverBankCode;
	}
	public String getReceiverAccountNo() {
		return receiverAccountNo;
	}
	public void setReceiverAccountNo(String receiverAccountNo) {
		this.receiverAccountNo = receiverAccountNo;
	}
	public String getReceiverEmail() {
		return receiverEmail;
	}
	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}
	public void setTxnDescription(String txnDescription) {
		this.txnDescription = txnDescription;
	}


}
