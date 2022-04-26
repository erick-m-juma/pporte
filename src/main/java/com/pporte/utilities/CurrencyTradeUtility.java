package com.pporte.utilities;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum12;
import org.stellar.sdk.AssetTypeCreditAlphaNum4;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ClaimClaimableBalanceOperation;
import org.stellar.sdk.Claimant;
import org.stellar.sdk.CreateClaimableBalanceOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PathPaymentStrictSendOperation;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Predicate;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.model.ClaimableBalance;
import com.pporte.model.PaymentOffer;
import com.pporte.model.PaymentPath;

public class CurrencyTradeUtility {
	private static String className = CurrencyTradeUtility.class.getSimpleName();
	
	public static List<Asset> getIssuersData(String assetCode) throws Exception {
		Asset asset = null;
		List<Asset> assetIssuers = null;
		try {
			 Gson gson = new Gson();
			String path = "/assets/";
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("horizon-testnet.stellar.org").setPath(path)
			.setParameter("asset_code", assetCode);
			URI uri = builder.build();
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			NeoBankEnvironment.setComment(3, className,"Response "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());
			if (response.getCode()!=200) {
				NeoBankEnvironment.setComment(1, className, "Stellar Api failed, response is "+result);
				throw new Exception("Error from Stellar ");
			}
			
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			//System.out.println("obj "+obj);
			JsonObject embedded = obj.get("_embedded").getAsJsonObject();
			//System.out.println("embedded "+embedded);
			JsonArray recordsArray = embedded.get("records").getAsJsonArray();
			//System.out.println("recordsArray "+recordsArray);
			assetIssuers = new ArrayList<Asset>();
			JsonObject assetObj = null;
			for(int i = 0; i<recordsArray.size(); i++) {
				assetObj = new JsonObject();
				assetObj = (JsonObject) recordsArray.get(i);
				//System.out.println("Asset code is "+assetObj.get("asset_code").getAsString());
				//System.out.println("Asset issuer is "+assetObj.get("asset_issuer").getAsString());
				asset = Asset.createNonNativeAsset(assetObj.get("asset_code").getAsString(),assetObj.get("asset_issuer").getAsString());
				assetIssuers.add(asset);
			}
			try {
				NeoBankEnvironment.setComment(3, className,"Size is "+assetIssuers.size());
			} finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;if(client!=null)client.close();
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;if(embedded!=null)embedded=null;
				if(recordsArray!=null)recordsArray=null;if(asset!=null)asset=null;
				if(assetObj!=null)assetObj=null;
				if(client!=null)client.close();
			}
			
		} catch (Exception e) {
			assetIssuers=null;
			NeoBankEnvironment.setComment(1, className,"Error from getIssuersData "+e.getMessage());
			throw new Exception(" Method getIssuersData:  Error is ",e);
		}
		return assetIssuers;
	}
	
	public static List<PaymentOffer> getPathStrictSendWithDestinationAssets( List<Asset> issuersList, String sourceAssetCode, 
			String sourceAmount, String sourceAssetType, String sourceAssetIssuer) throws Exception {
		List<PaymentOffer> paymentOffers = null;
		try {
			Gson gson = new Gson();			
			String cursor = "13537736921089";
			String order = "asc";
		
			String path = "/paths/strict-send";
						
			String destinationAsssets = Arrays.toString(issuersList.toArray()).replace("[", "").replace("]", "");
			destinationAsssets = destinationAsssets.replaceAll(" ", "");
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("horizon-testnet.stellar.org").setPath(path)
					.setParameter("destination_assets", destinationAsssets).setParameter("source_amount", sourceAmount)
					.setParameter("source_asset_type", sourceAssetType)
					.setParameter("source_asset_code", sourceAssetCode)
					.setParameter("source_asset_issuer", sourceAssetIssuer).setParameter("cursor", cursor)
					.setParameter("order", order);
			URI uri = builder.build();
			
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			NeoBankEnvironment.setComment(3, className,"Result is "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());
			if (response.getCode()!=200) {
				NeoBankEnvironment.setComment(1, className, "Stellar Api failed, response is "+result);
				throw new Exception("Stellar Api failed");
			}
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			//System.out.println("Offer are "+obj);
			JsonObject obj_embedded = new JsonObject();
			obj_embedded = obj.get("_embedded").getAsJsonObject();
			JsonArray records = obj_embedded.get("records").getAsJsonArray();
			List<PaymentPath> paymentPaths = null;
			paymentOffers = new ArrayList<PaymentOffer>();
			for(int i = 0; i<records.size(); i++) {
				paymentPaths = new ArrayList<PaymentPath>();
				PaymentOffer offer = new PaymentOffer();
				JsonObject objOffer = new JsonObject();
				JsonArray paths = new JsonArray();
				objOffer = (JsonObject) records.get(i);
				paths = objOffer.get("path").getAsJsonArray();
				PaymentPath paymentPath = null;
				for(int j = 0; j<paths.size(); j++) {
					JsonObject pathObj = paths.get(j).getAsJsonObject();
					paymentPath = new PaymentPath();
					if(pathObj.get("asset_type").getAsString().equals("native")) {
						paymentPath.setAssetType("native");
						paymentPath.setAssetCode("XLM");
					}else {
						paymentPath.setAssetType(pathObj.get("asset_type").getAsString());
						paymentPath.setAssetCode(pathObj.get("asset_code").getAsString());
						paymentPath.setAssetIssuers(pathObj.get("asset_issuer").getAsString());
					}
					paymentPaths.add(paymentPath);
				}
				offer.setPaymentPaths(paymentPaths);
				offer.setSourceAsset(objOffer.get("source_asset_code").getAsString());
				offer.setSourceAmount(objOffer.get("source_amount").getAsString());
				offer.setSourceAssetIssuer(objOffer.get("source_asset_issuer").getAsString());
				offer.setDestinationAsset(objOffer.get("destination_asset_code").getAsString());
				offer.setDestinationIssuer(objOffer.get("destination_asset_issuer").getAsString());
				offer.setDestinationAmount(Double.parseDouble(objOffer.get("destination_amount").getAsString()));
				paymentOffers.add(offer);
			}
			try {
				NeoBankEnvironment.setComment(3, className,"Offers Size is "+paymentOffers.size());
			} finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;if(client!=null)client.close();
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;if(obj_embedded!=null)obj_embedded=null;
				if(paymentPaths!=null)paymentPaths=null;if(records!=null)records=null;
				if(client!=null)client.close();
			}
		
		} catch (Exception e) {
			paymentOffers = null;
			NeoBankEnvironment.setComment(1, className, "Error from getPathStrictSendWithDestinationAssets is "+e.getMessage());
			throw new Exception(" Method getIssuersData:  Error is ",e);
		}
	
		return paymentOffers;
	}
	public static List<PaymentOffer> sortListByDestinationAmount(  List<PaymentOffer> offers) throws Exception {
		List<PaymentOffer> sortedOffers = null;
		try {
			sortedOffers = offers.stream()
			        .sorted(Comparator.comparingDouble(PaymentOffer::getDestinationAmount).reversed())
			        .collect(Collectors.toList());
			
		} catch (Exception e) {
			sortedOffers = null;
			NeoBankEnvironment.setComment(1, className, "Error from sortListAndGetBestThree is "+e.getMessage());
		}
		return sortedOffers;
		
	}
	
	public static boolean checkTrustLine(String assetCode, String assetIssuer, String accountId) throws Exception {
		boolean hasTrustLine = false;
		try {
			 Gson gson = new Gson();
			//String accountId= "GAKXWAYGXLEPMGLKQSMU7TVZM7MDQFJ4M5AZ6A5UTP2MDYHHMPV3VLO7";
			String path = "/accounts/"+accountId;
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("horizon-testnet.stellar.org").setPath(path);
			URI uri = builder.build();
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			//System.out.println("Response "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());
			if (response.getCode()!=200) {
				NeoBankEnvironment.setComment(1, className, "Stellar Api failed, response is "+result);
				throw new Exception("Stellar Api failed");
			}
			
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			//System.out.println("obj "+obj);
			JsonArray balances = obj.get("balances").getAsJsonArray();
			JsonObject balanceObj = null;
			//System.out.println("balances "+balances);
			for (int i = 0; i < balances.size(); i++) {
				balanceObj = (JsonObject) balances.get(i);
				if(!balanceObj.get("asset_type").getAsString().equals("native")) {
					if(balanceObj.get("asset_code").getAsString().equals(assetCode)
							&&balanceObj.get("asset_issuer").getAsString().equals(assetIssuer)) {
						hasTrustLine = true;
					}
				}
				
			}
			try {
				NeoBankEnvironment.setComment(3, className,"hasTrustLine is "+hasTrustLine);
			} finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;if(client!=null)client.close();
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;if(balances!=null)balances=null;
				if(balanceObj!=null)balanceObj=null;
				if(client!=null)client.close();
			}
		} catch (Exception e) {
			hasTrustLine = false;
			NeoBankEnvironment.setComment(1, className, "Error from checkTrustLine is "+e.getMessage());
			throw new Exception(" Method checkTrustLine:  Error is ",e);
		}
		return hasTrustLine;
		
	}
	
	
	public static String pathPaymentStrictSend(String sourceAssetCode,String sourceAssetIssuer, String sourceAmount, 
			String destinationAssetCode, String destinationIssuer, String destinationAmount,
			String sourceAcountPrivateKey, JsonArray paths, String partnerPublicKey  ) throws Exception{
		
		String result = "false";
		String txnHash=null;
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			
		 	KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(sourceAcountPrivateKey);
		 	server.accounts().account(sourceAccountKeyPair.getAccountId());
		 	AccountResponse sourceAccount = server.accounts().account(sourceAccountKeyPair.getAccountId());

		   //Create an object to represent the new asset
		 	
		 	Asset sourceAsset = Asset.createNonNativeAsset(sourceAssetCode, sourceAssetIssuer);
		 	Asset destinationAsset = Asset.createNonNativeAsset(destinationAssetCode, destinationIssuer);
		 	JsonObject pathObj = null;
		 	Asset pathAsset = null;
		 	List<Asset> assetsList = new ArrayList<Asset>();
		 	for(int i = 0; i<paths.size(); i++) {
		 		pathAsset = null;
		 		pathObj = (JsonObject) paths.get(i);
		 		String assetType = pathObj.get("assetType").getAsString();
		 		switch (assetType) {
		 			case "native":
		 				pathAsset = new AssetTypeNative();
		 			break;
		 			case "credit_alphanum4":
		 				pathAsset = new AssetTypeCreditAlphaNum4(pathObj.get("assetCode").getAsString(),
		 						pathObj.get("assetIssuers").getAsString());
			 		break;
		 			case "credit_alphanum12":
		 				pathAsset = new AssetTypeCreditAlphaNum12(pathObj.get("assetCode").getAsString(),
		 						pathObj.get("assetIssuers").getAsString());
				 	break;
				 	
		 		}
		 		assetsList.add(pathAsset);		 		
		 		
		 	}
		 	//Intermediate path asset
		 	Asset[] path = assetsList.toArray(new Asset[0]);
		 	Transaction transaction = null;
		 	if(sourceAssetCode.equals("XLM")) {
		 	 	 transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
				        .addOperation(new PathPaymentStrictSendOperation.Builder(
				        		new AssetTypeNative(), sourceAmount, partnerPublicKey, 
				        		destinationAsset, destinationAmount )
				        		.setPath(path)
				        		.setSourceAccount(sourceAccountKeyPair.getAccountId())
				        		.build())
				        .addMemo(Memo.text("Path Payment Strict Send"))
				        .setTimeout(180)
				        .setBaseFee(Transaction.MIN_BASE_FEE)
				        .build();
		 	}else {
		 	 	 transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
				        .addOperation(new PathPaymentStrictSendOperation.Builder(
				        		sourceAsset, sourceAmount, partnerPublicKey, 
				        		destinationAsset, destinationAmount )
				        		.setPath(path)
				        		.setSourceAccount(sourceAccountKeyPair.getAccountId())
				        		.build())
				        .addMemo(Memo.text("Path Payment Strict Send"))
				        .setTimeout(180)
				        .setBaseFee(Transaction.MIN_BASE_FEE)
				        .build();
		 	}
		
			transaction.sign(sourceAccountKeyPair);
			
			SubmitTransactionResponse response = null;
			
			try {
				  response = server.submitTransaction(transaction);
				 
				  if(response.getExtras()==null) {
						result = "true";
						txnHash=response.getHash();
						result=result+","+txnHash;
					  }else {							
							String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
							result = StellarSDKUtility.getPathPaymentStrictSendResultCode(operationError);
							NeoBankEnvironment.setComment(3, className, "==== Failed in Path Payment Strict Send error: "+operationError
									+" description: "+result+" "+  java.time.LocalTime.now());
					  }
					NeoBankEnvironment.setComment(3, className, "====end pathPaymentStrictSend : " + java.time.LocalTime.now());

				} finally {
					if (server != null)server.close();if (response != null)response = null;
					if (transaction != null)transaction = null;if (sourceAccount != null)sourceAccount = null;
					if (sourceAccountKeyPair != null)sourceAccountKeyPair = null;if (sourceAsset != null)sourceAsset = null;
					if (destinationAsset != null)destinationAsset = null;if (pathObj != null)pathObj = null;
					if (pathAsset != null)pathAsset = null;if (assetsList != null)assetsList = null;
					if (path != null)path = null;
					//TODO clear all objects here
					if(txnHash!=null) txnHash=null;
				 
				}
		} catch (Exception e) {
			result = "false";
			throw new Exception("Error from pathPaymentStrictSend is: "+e.getMessage());		}
		return result;
	}
	
	
	
	
	public static JsonObject getIssuersDataJson(String assetCode) throws Exception {
		JsonObject resultObj =null;
		try {
			 Gson gson = new Gson();
			String path = "/assets/";
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("horizon-testnet.stellar.org").setPath(path)
			.setParameter("asset_code", assetCode);
			URI uri = builder.build();
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			NeoBankEnvironment.setComment(3, className,"Response "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());
			if (response.getCode()!=200) {
				NeoBankEnvironment.setComment(1, className, "Stellar Api failed, response is "+result);
				throw new Exception("Error from Stellar ");
			}
			
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			//System.out.println("obj "+obj);
			resultObj = obj.get("_embedded").getAsJsonObject();
			try {
				//NeoBankEnvironment.setComment(3, className,"Size is "+resultObj.size());
			} finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;if(client!=null)client.close();
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;
				if(client!=null)client.close();
			}
			
		} catch (Exception e) {
			resultObj=null;
			NeoBankEnvironment.setComment(1, className,"Error from getIssuersDataJson "+e.getMessage());
			throw new Exception(" Method getIssuersDataJson:  Error is ",e);
		}
		return resultObj;
	}
	
	
	
	public static String createClaimableBalance(String sourceAccountPvt, String destinationPublicKey, 
			String amount, String assetCode, String issuerPublicKey, String comment ) throws Exception {
		String result = "";
		try {
		
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(sourceAccountPvt);
			KeyPair destinationAccountKeyPair = KeyPair.fromAccountId(destinationPublicKey);
			server.accounts().account(destinationAccountKeyPair.getAccountId());
			AccountResponse sourceAccount = server.accounts().account(sourceAccountKeyPair.getAccountId());
	 		Asset asset = null;
	 		if(assetCode.equals("XLM")) {
	 			asset = new AssetTypeNative();
	 		}else {  
	 			asset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);//Loading Custom Asset
	 		}
	 		Claimant claimant = new Claimant(destinationAccountKeyPair.getAccountId(), new Predicate.Unconditional() );
	 		ArrayList<Claimant> claimantList = new ArrayList<Claimant>();
	 		claimantList.add(claimant);
			CreateClaimableBalanceOperation operation = new CreateClaimableBalanceOperation
					.Builder(amount, asset, claimantList)
					.build();
			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
			        .addOperation(operation)
			        .addMemo(Memo.text(comment))
			        .setTimeout(180)
			        .setBaseFee(Transaction.MIN_BASE_FEE)
			        .build();
			transaction.sign(sourceAccountKeyPair);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				NeoBankEnvironment.setComment(3, className, " response is "+response.isSuccess() );
				  if(response.getExtras()==null) {
						result = "success";
					  }else {							
							String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
							NeoBankEnvironment.setComment(3, className, " operationError is "+operationError );
							throw new Exception(" Method createClaimableBalance:  Error is "+operationError);
					  }
			} finally {
				if (server != null) server.close(); if (response != null) response = null;
				if (transaction != null) transaction = null; if (sourceAccount != null) sourceAccount = null;
				if (sourceAccountKeyPair != null) 	sourceAccountKeyPair = null; 	
				if (destinationAccountKeyPair != null) destinationAccountKeyPair = null;
				if (claimantList != null) claimantList = null;
				if (operation != null) operation = null;
				if (asset != null) asset = null; if (claimant != null) claimant = null;
			}			
		} catch (Exception e) {
			result = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return result;
		
	}
	
	public static ArrayList<ClaimableBalance> getClaimClaimableBalances(String claimant) throws Exception {
		ArrayList<ClaimableBalance> clArrayList = null;
		try {
			Gson gson = new Gson();
			String path = "/claimable_balances/";
			//String claimant = "GDYCU6GOZZ7LTO53ZIZCM3W3XT6K5W6Y54NVJF5N7E7PJWUTXHZ4J6AO";
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost(
					NeoBankEnvironment.getStellarTestEviromentUrl().split("//")[1])
			.setPath(path)
			.setParameter("claimant", claimant);
			URI uri = builder.build();
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			if (response.getCode() != 200) 
				throw new Exception ("Error in geting transactions");
			//System.out.println("Response "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());			
			//System.out.println("result "+result);
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			//System.out.println("obj "+obj);
			JsonObject embedded = obj.get("_embedded").getAsJsonObject();
			//System.out.println("embedded "+embedded);
			JsonArray recordsArray = embedded.get("records").getAsJsonArray();
			//System.out.println("recordsArray "+recordsArray);
			clArrayList = new ArrayList<ClaimableBalance>();
			JsonObject balanceObj = null;
			ClaimableBalance claimableBalance = null;
			String assetString = null;
			JsonArray climantArray = null;
			JsonObject climantObj = null;
			ArrayList<String> climantList = new ArrayList<String>();
			for(int i = 0; i<recordsArray.size(); i++) {
				claimableBalance = new ClaimableBalance();
				balanceObj = new JsonObject();
				balanceObj = (JsonObject) recordsArray.get(i);
				claimableBalance.setClaimableBalanceId(balanceObj.get("id").getAsString());
				assetString = balanceObj.get("asset").getAsString();
				if(assetString.equalsIgnoreCase("native")) {
					claimableBalance.setAssetCode("XLM");
					claimableBalance.setAssetIssuer(assetString);
				}else {
					claimableBalance.setAssetCode(assetString.split(":")[0]);
					claimableBalance.setAssetIssuer(assetString.split(":")[1]);
				}
				claimableBalance.setAmount(balanceObj.get("amount").getAsString());
				claimableBalance.setSourceAccount((balanceObj.get("sponsor").getAsString()));
				claimableBalance.setLedgerNo((balanceObj.get("last_modified_ledger").getAsInt()));
				claimableBalance.setCreatedOn(
						StringUtils.trim(Utilities.getStellarDateConvertor(balanceObj.get("last_modified_time").getAsString())));
				climantArray = balanceObj.get("claimants").getAsJsonArray();
				for (int j = 0; j < climantArray.size(); j++) {
					climantObj =(JsonObject) climantArray.get(j);
					climantList.add(climantObj.get("destination").getAsString());
				}
				claimableBalance.setClaimants(climantList);
				clArrayList.add(claimableBalance);
			}
			try {
				
			}finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;if(embedded!=null)embedded=null;
				if(recordsArray!=null)recordsArray=null;if(balanceObj!=null)balanceObj=null;
				if(claimableBalance!=null)claimableBalance=null;if(assetString!=null)assetString=null;
				if(climantArray!=null)climantArray=null;if(climantObj!=null)climantObj=null;
				if(client!=null)client.close();
			}
			
		} catch (Exception e) {
			clArrayList = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		
		return clArrayList;
		
	}
	
	
	public static String claimClaimableBalance(String claimableBalanceId, String privateKey) throws Exception {
		String result = "";
		String txnHash = "";
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			KeyPair claimantAccount = KeyPair.fromSecretSeed(privateKey);
			server.accounts().account(claimantAccount.getAccountId());
			AccountResponse account = server.accounts().account(claimantAccount.getAccountId());			
			ClaimClaimableBalanceOperation operation = new ClaimClaimableBalanceOperation
					.Builder(claimableBalanceId)
					.build();
			
			Transaction transaction = new Transaction.Builder(account, Network.TESTNET)
			        .addOperation(operation)
			        .addMemo(Memo.text(""))
			        .setTimeout(180)
			        .setBaseFee(Transaction.MIN_BASE_FEE)
			        .build();
			transaction.sign(claimantAccount);
			SubmitTransactionResponse response =null;
			try {
				response = server.submitTransaction(transaction);
				if(response.getExtras()==null) {
					result = "true";
					txnHash=response.getHash();
					result=result+","+txnHash;
					NeoBankEnvironment.setComment(3,className,"result  is "+result);
				  }else {							
						String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
						throw new Exception(" Method claimClaimableBalance:  Error is "+operationError);
				  }
			} finally {
				if (server != null) server.close(); if (response != null) response = null;
				if (transaction != null) transaction = null; if (claimantAccount != null) claimantAccount = null;
				if (account != null) 	account = null; 	
				if (operation != null) operation = null;
				if (txnHash != null) txnHash = null;
			}
			
		} catch (Exception e) {
			result = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		
		return result;
		
	}
	
	public static String sendNoNNativeCoinPayment(String assetCode, KeyPair sourceAccount, KeyPair destinationAccount, String amount,
			String narrative, String issuerPublicKey) throws Exception {
		String result = "false";

		try {
			String txnHash = null;

		 	Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			server.accounts().account(destinationAccount.getAccountId());
			AccountResponse sourceAccountResponse = server.accounts().account(sourceAccount.getAccountId());
			 		
	 		Asset asset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);//Loading Custom Asset
			Transaction transaction = new Transaction.Builder(sourceAccountResponse, Network.TESTNET)
			        .addOperation(new PaymentOperation.Builder(destinationAccount.getAccountId(), asset, amount).build())
			        .addMemo(Memo.text(narrative))
			        .setTimeout(180)
			        .setBaseFee(Transaction.MIN_BASE_FEE)
			        .build();
			transaction.sign(sourceAccount);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction); // System.out.println(response.isSuccess());
				if (response.getExtras() == null) {
					NeoBankEnvironment.setComment(3, className, "Porte Coin Transfer successful : " + java.time.LocalTime.now() + "  " + response.toString());
					txnHash = response.getHash();
					result = "success,"+txnHash;
				} else {
					String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
					result = StellarSDKUtility.getPaymentResultCode(operationError);
					NeoBankEnvironment.setComment(3, className, "==== Transfer asset Failed operation error: "+operationError
							+" description: "+result+" "+  java.time.LocalTime.now());

				}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(3, className, "Error occured in sendNoNNativeCoinPayment: " + e.getMessage());
				throw new Exception("Error occured in sendNoNNativeCoinPayment: " + e.getMessage());
			}finally {
				if (server != null)
					server.close();
				if (response != null)
					response = null;
				if (transaction != null)
					transaction = null;
				if (sourceAccount != null)
					sourceAccount = null;
				if (asset != null)
					asset = null;
				if (sourceAccountResponse != null)
					sourceAccountResponse = null;
				if (txnHash != null)
					txnHash = null;
			}
		} catch (Exception e) {
			result = "false";
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return result;
	}
	
	public static String sendNativeCoinPayment(String publicKey, String sourceAccountPvt, String amount) throws Exception {
		String result = "false";
		try {
			String txnHash = "";
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			
			KeyPair source = KeyPair.fromSecretSeed(sourceAccountPvt);
			KeyPair destination = KeyPair.fromAccountId(publicKey);
			
			server.accounts().account(destination.getAccountId());
			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
			
			// Start building the transaction.
			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
			        .addOperation(new PaymentOperation.Builder(destination.getAccountId(), new AssetTypeNative(), amount).build())
			        // A memo allows you to add your own metadata to a transaction. It's
			        // optional and does not affect how Stellar treats the transaction.
			        .addMemo(Memo.text("Fund Accounts"))
			        // Wait a maximum of three minutes for the transaction
			        .setTimeout(180)
			        // Set the amount of lumens you're willing to pay per operation to submit your transaction
			        .setBaseFee(Transaction.MIN_BASE_FEE+1)
			        .build();
			// Sign the transaction to prove you are actually the person sending it.
			transaction.sign(source);
			
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				 if(response.getExtras()==null) {
					  txnHash = response.getHash();
					  result = "success,"+txnHash;

				  }else {
						String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
						result = StellarSDKUtility.getSellOfferResultCode(operationError);
						NeoBankEnvironment.setComment(3, className, "==== Failed to fund account operation error: "+operationError
								+" description: "+result+" "+  java.time.LocalTime.now());
				  }
			} finally {
				if (server != null)server.close();if (response != null)response = null;
				if (transaction != null)transaction = null;if (sourceAccount != null)sourceAccount = null;
				if (source != null)source = null;
				if (txnHash != null)txnHash = null;
			}
			
		} catch (Exception e) {
			result = "false";
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return result;
	}
	
	
	public static String  getStrictReceivePath(String sourceAssetCode, String sourceAssetIssuer, String destinationAssetType, 
			String destinationAssetIssuer, String destinationAssetCode, String destinationAmount ) throws Exception {
		String sourceAmount = null;
		
		try {
			Gson gson = new Gson();
			String path = "/paths/strict-receive";
			URIBuilder builder = new URIBuilder();
			String sourceAssetParam = null;
			if(sourceAssetCode.equals(NeoBankEnvironment.getXLMCode())){
				sourceAssetParam = "native";
			}else {
				sourceAssetParam = sourceAssetCode+":"+sourceAssetIssuer;
			}
			builder.setScheme("https").setHost(NeoBankEnvironment.getStellarTestEviromentUrl().split("//")[1]).setPath(path)
			.setParameter("source_assets", sourceAssetParam )
			.setParameter("destination_asset_type", destinationAssetType )
			.setParameter("destination_asset_issuer", destinationAssetIssuer )
			.setParameter("destination_asset_code", destinationAssetCode )
			.setParameter("destination_amount", destinationAmount );
			URI uri = builder.build();
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			if (response.getCode() != 200) 
				throw new Exception ("Error in geting path");
			//System.out.println("Response "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());			
			//System.out.println("result "+result);
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			//System.out.println("obj "+obj);
			JsonObject embedded = obj.get("_embedded").getAsJsonObject();
			//System.out.println("embedded "+embedded);
			JsonArray recordsArray = embedded.get("records").getAsJsonArray();
			JsonObject pathObj = null;
			for(int i = 0; i<recordsArray.size(); i++) {
				pathObj = (JsonObject) recordsArray.get(i);
				sourceAmount = pathObj.get("source_amount").getAsString();
			}
			try {
				NeoBankEnvironment.setComment(3,className,"sourceAmount is "+sourceAmount);
			} finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;if(embedded!=null)embedded=null;
				if(recordsArray!=null)recordsArray=null;
				if(pathObj!=null)pathObj=null;
				if(client!=null)client.close();
			}
			
		} catch (Exception e) {
			sourceAmount = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return sourceAmount;
	}
	
	public static List<PaymentOffer> getPathStrictSendWithNativeAsset( List<Asset> issuersList, String sourceAmount) throws Exception {
		NeoBankEnvironment.setComment(3, className, " sourceAmount "+sourceAmount);
		List<PaymentOffer> paymentOffers = null;
		try {
			Gson gson = new Gson();			
			String cursor = "13537736921089";
			String order = "asc";
		
			String path = "/paths/strict-send";
						
			String destinationAsssets = Arrays.toString(issuersList.toArray()).replace("[", "").replace("]", "");
			destinationAsssets = destinationAsssets.replaceAll(" ", "");
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("horizon-testnet.stellar.org").setPath(path)
					.setParameter("destination_assets", destinationAsssets)
					.setParameter("source_amount", sourceAmount)
					.setParameter("source_asset_type", "native")
					.setParameter("cursor", cursor)
					.setParameter("order", order);
			URI uri = builder.build();
			
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			NeoBankEnvironment.setComment(3, className,"Result is "+response.getCode());
			String result = EntityUtils.toString(response.getEntity());
			if (response.getCode()!=200) {
				NeoBankEnvironment.setComment(1, className, "Stellar Api failed, response is "+result);
				throw new Exception("Stellar Api failed");
			}
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			NeoBankEnvironment.setComment(3, className,"Offer are "+obj);
			JsonObject obj_embedded = new JsonObject();
			obj_embedded = obj.get("_embedded").getAsJsonObject();
			JsonArray records = obj_embedded.get("records").getAsJsonArray();
			List<PaymentPath> paymentPaths = null;
			paymentOffers = new ArrayList<PaymentOffer>();
			NeoBankEnvironment.setComment(3, className,"Step 1 ");
			for(int i = 0; i<records.size(); i++) {
				paymentPaths = new ArrayList<PaymentPath>();
				PaymentOffer offer = new PaymentOffer();
				JsonObject objOffer = new JsonObject();
				JsonArray paths = new JsonArray();
				objOffer = (JsonObject) records.get(i);
				paths = objOffer.get("path").getAsJsonArray();
				PaymentPath paymentPath = null;
				for(int j = 0; j<paths.size(); j++) {
					JsonObject pathObj = paths.get(j).getAsJsonObject();
					paymentPath = new PaymentPath();
					if(pathObj.get("asset_type").getAsString().equals("native")) {
						paymentPath.setAssetType("native");
						paymentPath.setAssetCode("XLM");
					}else {
						paymentPath.setAssetType(pathObj.get("asset_type").getAsString());
						paymentPath.setAssetCode(pathObj.get("asset_code").getAsString());
						paymentPath.setAssetIssuers(pathObj.get("asset_issuer").getAsString());
					}
					paymentPaths.add(paymentPath);
				}
				offer.setPaymentPaths(paymentPaths);
				offer.setSourceAsset(objOffer.get("source_asset_type").getAsString());
				offer.setSourceAmount(objOffer.get("source_amount").getAsString());
				//offer.setSourceAssetIssuer(objOffer.get("source_asset_issuer").getAsString());
				offer.setDestinationAsset(objOffer.get("destination_asset_code").getAsString());
				offer.setDestinationIssuer(objOffer.get("destination_asset_issuer").getAsString());
				offer.setDestinationAmount(Double.parseDouble(objOffer.get("destination_amount").getAsString()));
				paymentOffers.add(offer);
			}
			try {
				NeoBankEnvironment.setComment(3, className,"Offers Size is "+paymentOffers.size());
			} finally {
				if(gson!=null)gson=null;if(path!=null)path=null;
				if(builder!=null)builder=null;if(client!=null)client.close();
				if(uri!=null)uri=null;if(httpGet!=null)httpGet.clear();
				if(response!=null)response.close();if(result!=null)result=null;
				if(obj!=null)obj=null;if(obj_embedded!=null)obj_embedded=null;
				if(paymentPaths!=null)paymentPaths=null;if(records!=null)records=null;
				if(client!=null)client.close();
			}
		
		} catch (Exception e) {
			paymentOffers = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
	
		return paymentOffers;
	}
		

}
