package com.pporte.model;

import java.util.List;

public class PaymentOffer {
	private String sourceAsset;
	private String sourceAssetIssuer;
	private String sourceAmount;
	private String destinationAsset;
	private String destinationIssuer;
	private double destinationAmount;
	private List<PaymentPath> paymentPaths;
	
	public String getSourceAsset() {
		return sourceAsset;
	}
	public String getSourceAssetIssuer() {
		return sourceAssetIssuer;
	}
	public String getSourceAmount() {
		return sourceAmount;
	}
	public String getDestinationAsset() {
		return destinationAsset;
	}
	public String getDestinationIssuer() {
		return destinationIssuer;
	}
	public double getDestinationAmount() {
		return destinationAmount;
	}
	public void setDestinationAmount(double destinationAmount) {
		this.destinationAmount = destinationAmount;
	}
	public List<PaymentPath> getPaymentPaths() {
		return paymentPaths;
	}
	public void setSourceAsset(String sourceAsset) {
		this.sourceAsset = sourceAsset;
	}
	public void setSourceAssetIssuer(String sourceAssetIssuer) {
		this.sourceAssetIssuer = sourceAssetIssuer;
	}
	public void setSourceAmount(String sourceAmount) {
		this.sourceAmount = sourceAmount;
	}
	public void setDestinationAsset(String destinationAsset) {
		this.destinationAsset = destinationAsset;
	}
	public void setDestinationIssuer(String destinationIssuer) {
		this.destinationIssuer = destinationIssuer;
	}
	
	public void setPaymentPaths(List<PaymentPath> paymentPaths) {
		this.paymentPaths = paymentPaths;
	}
	
	
}
