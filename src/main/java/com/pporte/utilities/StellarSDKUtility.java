package com.pporte.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustAsset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageSellOfferOperation;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PathPaymentStrictReceiveOperation;
import org.stellar.sdk.PathPaymentStrictSendOperation;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.OfferResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.pporte.NeoBankEnvironment;
import com.pporte.model.AssetAccount;
import com.pporte.model.AssetOffer;
import com.pporte.model.AssetTransactions;
import shadow.com.google.common.collect.Lists;

public class StellarSDKUtility {
	private static String className = StellarSDKUtility.class.getSimpleName();
	
	public static KeyPair generateKeyPair() throws Exception {
	   KeyPair keyPair = null;
	   try {
	 	   keyPair = KeyPair.random();
		} catch (Exception e) {
			keyPair = null;
			 throw new Exception(" Method generateKeyPair:  Error in generating key pair " ,e);
		}  
	 	return keyPair;
	}
	public static JsonObject createAccount(KeyPair keyPair) throws Exception {
		  JsonObject obj = null;
		   try {
			   String friendbotUrl = String.format(
		 				 NeoBankEnvironment.getFriendBootStellarSDKUrl(),
		 		keyPair.getAccountId());
				InputStream response = new URL(friendbotUrl).openStream();
				Scanner scanner = new Scanner(response, "UTF-8");
				String body = scanner.useDelimiter("\\A").next();
				
				try {
					obj = new Gson().fromJson(body, JsonObject.class);
				}finally{
					if(scanner != null) scanner.close();
					if(response != null) response.close();
					if(friendbotUrl != null) friendbotUrl = null;
				}
				
			} catch (Exception e) {
				obj = null;
				 throw new Exception(" Method createAccount:  Error in creating account ",e);
			}  
		 	return obj;
		}
	public static boolean createTrustline(String issuerAccountId, String accountPrivateKey,
			int baseFee, String limit, String assetCode ) throws Exception {
		NeoBankEnvironment.setComment(3, className, "issuerAccountId is "+issuerAccountId);
		NeoBankEnvironment.setComment(2, className, "issuerAccountId "+issuerAccountId+ 
				" accountPrivateKey "+accountPrivateKey +" baseFee "+baseFee +" limit "+limit +" assetCode "+assetCode );
		 boolean result = false;
		 try {
		 	Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
		 	// Keys for accounts to PORTE issuer and the account to create trustline
		 	
		 	KeyPair accountKeys = KeyPair .fromSecretSeed(accountPrivateKey);
		 	// Create an object to represent the asset
		 	Asset porte = Asset.createNonNativeAsset(assetCode, issuerAccountId);
		 	// Create the trustline to the asset
		 	AccountResponse account = server.accounts().account(accountKeys.getAccountId());
		 	Transaction allowPORTE = new Transaction.Builder(account, Network.TESTNET)
		 	.addOperation(new ChangeTrustOperation.Builder(
		 			new ChangeTrustAsset.Wrapper(porte), limit).build())
		 	.setTimeout(180)
		 	.setBaseFee(baseFee) // stroops 
		 	.build();
		 	allowPORTE.sign(accountKeys);
		 	SubmitTransactionResponse response = null;
		 	try {
		 		response = server.submitTransaction(allowPORTE);
			 	result = response.isSuccess();
			 	if(!result) 
			 		//TODO Get Error message here
			 	;
			} finally {
				if(server != null) server.close();
				if(response != null) response =null;
				if(porte != null) porte =null;
				if(accountKeys != null) accountKeys =null;
				if(account != null) account =null;
				if(allowPORTE != null) allowPORTE =null;
			}
		} catch (Exception e) {
			result = false;
			throw new Exception(" Method createTrustline:  Error in Creating trustline "+e.getMessage());
		}
		 	return result;
	}
	

	public static boolean CheckAccountIfExist(KeyPair account) throws Exception {
		boolean result = false;

		try {
		 	Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
		 	AccountResponse sourceAccountResponse = server.accounts().account(account.getAccountId());
		 	try {
		 		result = true;
		 	}finally {
				if(sourceAccountResponse!=null)sourceAccountResponse=null;
				if(server!=null)server.close();
			}
		 	
		} catch (Exception e) {
		 	result = false;
			throw new Exception("Account "+account+" does not exist in Steller Network : "+e.getMessage());
		}
		return result;	
	}

	public static String sendNoNNativeCoinPayment(String assetCode, KeyPair sourceAccount, KeyPair destinationAccount, String amount,
			String narrative, String issuerPublicKey) throws Exception {
		String result = "false";
		String txnHash = "";
		try {

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
					// Get Stellar Hash
					txnHash= response.getHash();
					result = "success"+","+txnHash;
					
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
				if (txnHash!=null)txnHash=null;

			}
		} catch (Exception e) {
			result = "false";
			throw new Exception("Error from sendNoNNativeCoinPayment is: "+e.getMessage());
		}
		return result;
	}

	public static ArrayList<AssetAccount> getAccountBalance(KeyPair accountForBalance) throws Exception {
		ArrayList<AssetAccount> arrAccountBalance = null;
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			AccountResponse account = server.accounts().account(accountForBalance.getAccountId());
			arrAccountBalance = new ArrayList<AssetAccount>();
			for (AccountResponse.Balance balance : account.getBalances()) {
				AssetAccount assetAccount = new AssetAccount();
				assetAccount.setAssetType(balance.getAssetType());
				assetAccount.setAssetBalance(balance.getBalance());
				assetAccount.setAccountId(accountForBalance.getAccountId());

				if (!balance.getAssetCode().isPresent()) {
					assetAccount.setAssetCode("XLM");
				} else {
					assetAccount.setAssetCode(balance.getAssetCode().get());
				}
				
				arrAccountBalance.add(assetAccount);

			}	 		
			NeoBankEnvironment.setComment(3, className, "====== end  getAccountBalance "+ java.time.LocalTime.now());
			try {
				
			} finally {
				if (server != null)
					server.close();
				if (account != null)
					account = null;
			}

		} catch (Exception e) {
			throw new Exception("Error in getAccountBalance is: " + e.getMessage());
		}
		return arrAccountBalance;
	}
	
	public static ArrayList<AssetAccount> getAccountBalanceWithNoBTC(KeyPair accountForBalance) throws Exception {
		ArrayList<AssetAccount> arrAccountBalance = null;
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			AccountResponse account = server.accounts().account(accountForBalance.getAccountId());
			arrAccountBalance = new ArrayList<AssetAccount>();
			AssetAccount assetAccount =null;
			for (AccountResponse.Balance balance : account.getBalances()) {
				
				if (!balance.getAssetCode().isPresent()) {
					assetAccount = new AssetAccount();
					assetAccount.setAssetCode("XLM");
					assetAccount.setAssetType(balance.getAssetType());
					assetAccount.setAssetBalance(balance.getBalance());
					assetAccount.setAccountId(accountForBalance.getAccountId());
					arrAccountBalance.add(assetAccount);
				} else {
					
					if(balance.getAssetCode().get().equals(NeoBankEnvironment.getPorteTokenCode()) ||
							balance.getAssetCode().get().equals(NeoBankEnvironment.getVesselCoinCode()) ||
							balance.getAssetCode().get().equals(NeoBankEnvironment.getUSDCCode())
							) {
						assetAccount = new AssetAccount();
						assetAccount.setAssetCode(balance.getAssetCode().get());
						assetAccount.setAssetType(balance.getAssetType());
						assetAccount.setAssetBalance(balance.getBalance());
						assetAccount.setAccountId(accountForBalance.getAccountId());
						arrAccountBalance.add(assetAccount);
					}
					
				}
				
				
				

			}	 		
			
			try {
				
			} finally {
				if (server != null)
					server.close();
				if (account != null)
					account = null;
			}

		} catch (Exception e) {
			throw new Exception("Error in getAccountBalance is: " + e.getMessage());
		}
		return arrAccountBalance;
	}
	
	public static ArrayList<AssetAccount> getAccountOffRampAssets(KeyPair accountForBalance) throws Exception {
		ArrayList<AssetAccount> arrAccountBalance = null;
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			AccountResponse account = server.accounts().account(accountForBalance.getAccountId());
			arrAccountBalance = new ArrayList<AssetAccount>();
			AssetAccount assetAccount =null;
			for (AccountResponse.Balance balance : account.getBalances()) {
				
				if (balance.getAssetCode().isPresent()) {
					if(balance.getAssetCode().get().equals(NeoBankEnvironment.getPorteTokenCode()) ||
							balance.getAssetCode().get().equals(NeoBankEnvironment.getVesselCoinCode())
							) {
						assetAccount = new AssetAccount();
						assetAccount.setAssetCode(balance.getAssetCode().get());
						assetAccount.setAssetType(balance.getAssetType());
						assetAccount.setAssetBalance(balance.getBalance());
						assetAccount.setAccountId(accountForBalance.getAccountId());
						arrAccountBalance.add(assetAccount);
					}
					
				}
				
			}	 		
			
			try {
				
			} finally {
				if (server != null)
					server.close();
				if (account != null)
					account = null;
			}

		} catch (Exception e) {
			throw new Exception("Error in getAccountBalance is: " + e.getMessage());
		}
		return arrAccountBalance;
	}
	
	public static boolean buyNoNNativeCoinPayment(String distributorPrivateKey, String customerPublicKey,
			String assetCode, String issuerPublicKey, String coinsToBuy, String Comment) throws Exception {
		boolean result = false;
		try {
			
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());

			KeyPair source = KeyPair.fromSecretSeed(distributorPrivateKey);
			KeyPair destination = KeyPair.fromAccountId(customerPublicKey);
			server.accounts().account(destination.getAccountId());

			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());

			Asset porteAsset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);// Loading Custom Asset

			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
					.addOperation(
							new PaymentOperation.Builder(destination.getAccountId(), porteAsset, coinsToBuy).build())					// optional and does not affect how Stellar treats the transaction.
					.addMemo(Memo.text(Comment))
					.setTimeout(180)
					.setBaseFee(Transaction.MIN_BASE_FEE).build();
			transaction.sign(source);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				result = response.isSuccess();
			} finally {
				if (server != null)
					server.close();
				if (response != null)
					response = null;
				if (transaction != null)
					transaction = null;
				if (sourceAccount != null)
					sourceAccount = null;
				if (source != null)
					source = null;
				if (destination != null)
					destination = null;
			}

		} catch (Exception e) {
			result = false;
			throw new Exception(" Method buyNoNNativeCoinPayment:  Error in buying " + e.getMessage());
		}
		return result;
	}
	public static String buyNoNNativeCoinPaymentWithStellarHashResponse(String distributorPrivateKey, String customerPublicKey,
			String assetCode, String issuerPublicKey, String coinsToBuy, String Comment) throws Exception {
		String result = "false";
		String stellarHash=null;
		try {
			
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			
			KeyPair source = KeyPair.fromSecretSeed(distributorPrivateKey);
			KeyPair destination = KeyPair.fromAccountId(customerPublicKey);
			server.accounts().account(destination.getAccountId());
			
			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
			
			Asset porteAsset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);// Loading Custom Asset
			
			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
					.addOperation(
							new PaymentOperation.Builder(destination.getAccountId(), porteAsset, coinsToBuy).build())					// optional and does not affect how Stellar treats the transaction.
					.addMemo(Memo.text(Comment))
					.setTimeout(180)
					.setBaseFee(Transaction.MIN_BASE_FEE).build();
			transaction.sign(source);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				stellarHash=response.getHash();
				result = "true"+","+stellarHash;
			} finally {
				if (server != null)
					server.close();
				if (response != null)
					response = null;
				if (transaction != null)
					transaction = null;
				if (sourceAccount != null)
					sourceAccount = null;
				if (source != null)
					source = null;
				if (destination != null)
					destination = null;
				if (stellarHash != null)
					stellarHash = null;
			}
			
		} catch (Exception e) {
			result = "false";
			throw new Exception(" Method buyNoNNativeCoinPayment:  Error in buying " + e.getMessage());
		}
		return result;
	}
	
	public static String jsonPretyPrint(JSONObject jsonObject)  {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
	 	String json = gson.toJson(jsonObject);
		return json;
	}
	
	public static ConcurrentHashMap<String, String> getAssetAccountBalances(KeyPair accountForBalance) throws Exception{
		ConcurrentHashMap<String, String> hashAssetBalance = null;
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			AccountResponse account = server.accounts().account(accountForBalance.getAccountId());
			hashAssetBalance = new ConcurrentHashMap<String, String>();
			for (AccountResponse.Balance balance : account.getBalances()) {
				AssetAccount assetAccount = new AssetAccount();
				assetAccount.setAssetType(balance.getAssetType());
				assetAccount.setAssetBalance(balance.getBalance());

				if (!balance.getAssetCode().isPresent()) {
					assetAccount.setAssetCode("XLM");
				} else {
					assetAccount.setAssetCode(balance.getAssetCode().get());
				}
				hashAssetBalance.put(assetAccount.getAssetCode(), balance.getBalance());
				if (!balance.getAssetCode().isPresent()) {
					 NeoBankEnvironment.setComment(3, className, "==== "+ assetAccount.getAssetCode()+ " balance is "+balance.getBalance());
				} else {
					 NeoBankEnvironment.setComment(3, className, "==== "+assetAccount.getAssetCode() +"  balance is " + balance.getBalance());
				}
			}

		} catch (Exception e) {
			throw new Exception("Error in getAssetAccountBalances is: " + e.getMessage());
		}
	 	return hashAssetBalance;
	}
	
	public static String manageSellOffer( String liquiditySecretKey, String assetCode, String issuerPublicKey, 
			String amountToSell, String pricePerUnit) throws Exception {
		String result = "false";
		try {
			 NeoBankEnvironment.setComment(3, className, "==== inside manageSellOffer : " + java.time.LocalTime.now());

		    Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			KeyPair source = KeyPair.fromSecretSeed(liquiditySecretKey);
			// First, check to make sure that the destination account exists.
			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
			Asset selling = Asset.createNonNativeAsset(assetCode, issuerPublicKey);// Loading Custom Asset
			Asset buying = new AssetTypeNative();
							
			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
					.addOperation(new ManageSellOfferOperation.Builder(selling, buying, amountToSell, pricePerUnit).build())
					.addMemo(Memo.text("Create Sell Offer"))
					.setTimeout(180) //
					.setBaseFee(Transaction.MIN_BASE_FEE).build();
			transaction.sign(source);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				 if(response.getExtras()==null) {
					  NeoBankEnvironment.setComment(3, className, "==== Sell offer Created Successful : " + java.time.LocalTime.now());
					  result = "success";

				  }else {
						String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
						result = StellarSDKUtility.getSellOfferResultCode(operationError);
						NeoBankEnvironment.setComment(3, className, "==== Sell offer Failed operation error: "+operationError
								+" description: "+result+" "+  java.time.LocalTime.now());
				  }
			} finally {
				if (server != null)
					server.close();
				if (response != null)
					response = null;
				if (transaction != null)
					transaction = null;
				if (sourceAccount != null)
					sourceAccount = null;
				if (source != null)
					source = null;
			}

		} catch (Exception e) {
			result = "false";
			throw new Exception(" Method manageSellOffer:  Error is " + e.getMessage());
		}
		return result;
	}
	
	public static String manageBuyOffer( String liquiditySecretKey, String assetCode, String issuerPublicKey, 
			String amountToSell, String pricePerUnit) throws Exception {
		String result = "false";
		try {
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			KeyPair source = KeyPair.fromSecretSeed(liquiditySecretKey);
			
			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
			Asset selling = Asset.createNonNativeAsset(assetCode, issuerPublicKey);// Loading Custom Asset
			Asset buying = new AssetTypeNative();
							
			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
					.addOperation(
							new ManageSellOfferOperation.Builder(selling, buying, amountToSell, pricePerUnit).build())
					.addMemo(Memo.text("Create Sell Offer"))
					.setTimeout(180) //
					.setBaseFee(Transaction.MIN_BASE_FEE).build();
			transaction.sign(source);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				 if(response.getExtras()==null) {
					System.out.println("sell offer created ");
					  NeoBankEnvironment.setComment(3, className, "==== Buy offer Created Successful : " + java.time.LocalTime.now());
					  result = "success";

				  }else {
						String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
						result = StellarSDKUtility.getSellOfferResultCode(operationError);
						NeoBankEnvironment.setComment(3, className, "==== Buy offer Failed operation error: "+operationError
								+" description: "+result+" "+  java.time.LocalTime.now());
				  }
			} finally {
				if (server != null)
					server.close();
				if (response != null)
					response = null;
				if (transaction != null)
					transaction = null;
				if (sourceAccount != null)
					sourceAccount = null;
				if (source != null)
					source = null;
			}

		} catch (Exception e) {
			result = "false";
			throw new Exception(" Method manageBuyOffer:  Error is " + e.getMessage());
		}
		return result;
	}
	
	public static ArrayList<AssetOffer> getAccountOffers(String accountId)throws Exception {
		ArrayList<AssetOffer> arrAssetOffers = null;
		
		try {
			@SuppressWarnings("resource")
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			KeyPair source = KeyPair.fromAccountId(accountId);
			@SuppressWarnings("deprecation")
			Page<OfferResponse> offerResponse = server.offers().forAccount(source.getAccountId()).execute();
	        arrAssetOffers = new ArrayList<AssetOffer>();
	        for (OfferResponse offers: offerResponse.getRecords()) {
	        	AssetOffer m_AssetOffer = new AssetOffer();
	        	m_AssetOffer.setAccountId(source.getAccountId());
	        	if(offers.getSelling().toString().equals("native")) {
		        	m_AssetOffer.setSellAsset("XLM");
	        	}else { m_AssetOffer.setSellAsset(offers.getSelling().toString()); }
	        	if(offers.getBuying().toString().equals("native")) {
		        	m_AssetOffer.setBuyAsset("XLM");
	        	}else { m_AssetOffer.setBuyAsset(offers.getBuying().toString()); }
	            m_AssetOffer.setDate(offers.getLastModifiedTime());
	        	m_AssetOffer.setPrice(offers.getPrice());
	        	arrAssetOffers.add(m_AssetOffer);
	            
	        }
		} catch (Exception e) {
			throw new Exception(" Method getAccountOffers:  Error is " + e.getMessage());	
		}

		return arrAssetOffers;
    }
	
	public static String getSellOfferResultCode(String opError)throws IOException {
		ConcurrentHashMap<String, String>  manageSellOfferResultCode = new ConcurrentHashMap<String, String>();
			manageSellOfferResultCode.put("op_success", "The payment was successfully completed.");
			manageSellOfferResultCode.put("[op_malformed]", "The input to the payment is invalid");
			manageSellOfferResultCode.put("[op_sell_no_trust]", "The account creating the offer does not have a trustline for the asset it is selling.");
			manageSellOfferResultCode.put("[op_buy_no_trust]", "The account creating the offer does not have a trustline for the asset it is buying.");
			manageSellOfferResultCode.put("[sell_not_authorized]", "The account creating the offer is not authorized to sell this asset.");
			manageSellOfferResultCode.put("[buy_not_authorized]", "The account creating the offer is not authorized to buy this asset.");
			manageSellOfferResultCode.put("[op_linefull]", "The account creating the offer does not have sufficient limits to receive buying and still satisfy its buying liabilities.");
			manageSellOfferResultCode.put("[op_underfunded]", "The account creating the offer does not have sufficient limits to send selling and still satisfy its selling liabilities.");
			manageSellOfferResultCode.put("[op_cross_self]", "The account has opposite offer of equal or lesser price active, so the account creating this offer would immediately cross itself.");
			manageSellOfferResultCode.put("[op_sell_no_issuer]", "The issuer of selling asset does not exist.");
			manageSellOfferResultCode.put("[buy_no_issuer]", "The issuer of buying asset does not exist.");
			manageSellOfferResultCode.put("[op_offer_not_found]", "An offer with that offerID cannot be found.");
			manageSellOfferResultCode.put("[OpLowReserve]", "The account creating this offer does not have enough XLM to satisfy the minimum XLM reserve");
			
		return manageSellOfferResultCode.get(opError);
	}
	
	public static String getBuyOfferResultCode(String opError)throws IOException {
		ConcurrentHashMap<String, String>  manageSellOfferResultCode = new ConcurrentHashMap<String, String>();
			manageSellOfferResultCode.put("op_success", "The payment was successfully completed.");
			manageSellOfferResultCode.put("[op_malformed]", "The input to the payment is invalid");
			manageSellOfferResultCode.put("[op_sell_no_trust]", "The account creating the offer does not have a trustline for the asset it is selling.");
			manageSellOfferResultCode.put("[op_buy_no_trust]", "The account creating the offer does not have a trustline for the asset it is buying.");
			manageSellOfferResultCode.put("[sell_not_authorized]", "The account creating the offer is not authorized to sell this asset.");
			manageSellOfferResultCode.put("[buy_not_authorized]", "The account creating the offer is not authorized to buy this asset.");
			manageSellOfferResultCode.put("[op_linefull]", "The account creating the offer does not have sufficient limits to receive buying and still satisfy its buying liabilities.");
			manageSellOfferResultCode.put("[op_underfunded]", "The account creating the offer does not have sufficient limits to send selling and still satisfy its selling liabilities.");
			manageSellOfferResultCode.put("[op_cross_self]", "The account has opposite offer of equal or lesser price active, so the account creating this offer would immediately cross itself.");
			manageSellOfferResultCode.put("[op_sell_no_issuer]", "The issuer of selling asset does not exist.");
			manageSellOfferResultCode.put("[buy_no_issuer]", "The issuer of buying asset does not exist.");
			manageSellOfferResultCode.put("[op_offer_not_found]", "An offer with that offerID cannot be found.");
			manageSellOfferResultCode.put("[op_lowreserve]", "The account creating this offer does not have enough XLM to satisfy the minimum XLM reserve");
			
		return manageSellOfferResultCode.get(opError);
		
	}
	public static String sendNativeCoinPayment(String publicKey, String sourceAccountPvt, String amount) throws Exception {
		String result = "false";
		try {
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
					  NeoBankEnvironment.setComment(3, className, "==== Account funded successfully : " + java.time.LocalTime.now());
					  result = "success";

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
			}
			
		} catch (Exception e) {
			result = "false";
			throw new Exception("Error from sendNativeCoinPayment is: "+e.getMessage());
		}
		return result;
	}
	
	public static JSONObject getObjectLinks(String accountId, String limit) throws Exception {
		JSONObject jsonObjectLinks = null;
		CloseableHttpClient client = null; HttpGet httpGet = null;  CloseableHttpResponse response = null;
		String result = null; JSONParser parser = null; JSONObject jsonObject = null; 
		try {
			client = HttpClients.createDefault();
			httpGet = new HttpGet("https://horizon-testnet.stellar.org" + "/accounts/" + 
			accountId+ "/operations?"
			+ "limit="+ limit + "&order=desc&"
			+ "join=transactions");
			response = client.execute(httpGet);
			if (response.getCode() == 200) {
				NeoBankEnvironment.setComment(3, className, "==== status Ok : " + java.time.LocalTime.now());
				try {
					result = EntityUtils.toString(response.getEntity());
				} catch (Exception e) {
					throw new Exception(" Error in getting account Transaction from Stellar " + e.getMessage());
				}
				 parser = new JSONParser();
				 jsonObject = (JSONObject) parser.parse(result.toString());
				 jsonObjectLinks = (JSONObject) parser.parse(jsonObject.get("_links").toString());
			}
	}catch (Exception e) {
		throw new Exception(" Method getObjectLinks:  Error is " + e.getMessage());		
		}
		return jsonObjectLinks;
	}
	public static ArrayList<AssetTransactions> getAccountTransactions(String accountId, String limit)throws Exception {        
        //String account = "GALBMMADCBZJOA77QXEFWZOZ3LHV6CIPRLFGYNQEHWQHJFJS3UC6HWDY";
        //String limit = "2";s
		CloseableHttpClient client = null; HttpGet httpGet = null;  CloseableHttpResponse response = null;
		String result = null; JSONParser parser = null; JSONObject jsonObject = null; JSONObject jsonObjectLinks = null;
		JSONObject jsonObjectEmbeded = null; JSONObject transactionsObj = null; JSONObject transactionsObj2 = null;
		ArrayList<AssetTransactions> arrAssetTransactions = null;String txnmode = null; String description = "";
		int length = 13;  	 String link=null;
		String assetCode = null;
		try {
			NeoBankEnvironment.setComment(3, className, "====  in getAccountTransactions accountId : " + accountId + " limit "+ limit);

			NeoBankEnvironment.setComment(3, className, "==== start getAccountTransactions : " + java.time.LocalTime.now());
			link=NeoBankEnvironment.getStellarTestEviromentUrl()+ "/accounts/" + accountId+ "/operations?"+ "limit="+ limit + "&order=desc&"+ "join=transactions";
			
			client = HttpClients.createDefault();
			
			httpGet = new HttpGet(link);
			response = client.execute(httpGet);
			if (response.getCode() == 200) {
				NeoBankEnvironment.setComment(3, className, "==== status Ok : " + java.time.LocalTime.now());
				try {
					result = EntityUtils.toString(response.getEntity());
				} catch (Exception e) {
					throw new Exception(" Error in getting account Transaction from Stellar " + e.getMessage());
				}
				 parser = new JSONParser();
				 jsonObject = (JSONObject) parser.parse(result.toString());
				 jsonObjectLinks = (JSONObject) parser.parse(jsonObject.get("_links").toString());
				

			    jsonObjectEmbeded = (JSONObject) parser.parse(jsonObject.get("_embedded").toString());

				JSONArray jsonArray = (JSONArray) jsonObjectEmbeded.get("records");
				arrAssetTransactions = new ArrayList<AssetTransactions>();

				for (int i = 0; i < jsonArray.size(); i++) {
					
					AssetTransactions assetTransactions = new AssetTransactions();
					 transactionsObj = (JSONObject) parser.parse(jsonArray.get(i).toString());
					String type = transactionsObj.get("type").toString();
					assetTransactions.setOperationId(StringUtils.trim(transactionsObj.get("id").toString()));
					assetTransactions.setIsSuccess(StringUtils.trim(transactionsObj.get("transaction_successful").toString()));
					assetTransactions.setType(StringUtils.trim(transactionsObj.get("type").toString()));
					assetTransactions.setCreatedOn(StringUtils.trim(Utilities.getStellarDateConvertor(transactionsObj.get("created_at").toString())));
					assetTransactions.setSourceAccount(StringUtils.trim(transactionsObj.get("source_account").toString()));
					if (type.equals("payment")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("from").toString()));
						assetTransactions.setToAccount(StringUtils.trim(transactionsObj.get("to").toString()));
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						if(StringUtils.trim(transactionsObj.get("asset_type").toString()).equals("native")){
							assetCode = "XLM";
						}else {
							assetCode = StringUtils.trim(transactionsObj.get("asset_code").toString());
						}
						assetTransactions.setAssetCode(assetCode);
						 if(transactionsObj.get("from").toString().equals( accountId)) {
		                	  txnmode = "D";		                	 
		                 }else  {
		                	  txnmode = "C";		                	 
		                 }
					  assetTransactions.setTxnMode(StringUtils.trim(txnmode));
					  
					  description = "Payment "+transactionsObj.get("amount").toString()+ " "+assetCode
							  +" from "+Utilities.ellipsis(transactionsObj.get("from").toString(), length)  + " to "+Utilities.ellipsis(transactionsObj.get("to").toString(), length);
					} else if (type.equals("manage_sell_offer")) {
						
					}else if (type.equals("manage_buy_offer")) {
						
					} else if (type.equals("change_trust")) {
						assetTransactions.setTrustee(StringUtils.trim(transactionsObj.get("trustee").toString()));
						assetTransactions.setTrustor(StringUtils.trim(transactionsObj.get("trustor").toString()));
						assetTransactions.setAssetCode(StringUtils.trim(transactionsObj.get("asset_code").toString()));
						String trustLimit  = transactionsObj.get("limit").toString();
						
						//Trust GALBMMADCB... issuing USDC with limit 922337203685.4775807
						description = "Trust "+Utilities.ellipsis(transactionsObj.get("trustee").toString(), length) +" issuing "+transactionsObj.get("asset_code").toString()
								+" with limit "+trustLimit;
					} else if (type.equals("path_payment_strict_send")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("from").toString()));
						assetTransactions.setToAccount(StringUtils.trim(transactionsObj.get("to").toString()));
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						String asset_code = null;
		                 if (transactionsObj.get("asset_type").toString().equals("native")) {
		                	 asset_code = "XLM";
						}else {
				             asset_code = transactionsObj.get("asset_code").toString();							
						}
						assetTransactions.setAssetCode(StringUtils.trim(asset_code));
			              txnmode = "C";	
						  assetTransactions.setTxnMode(StringUtils.trim(txnmode));
						  
			              description = "Path Payment "+transactionsObj.get("amount").toString() + " "+ asset_code+" to "+ Utilities.ellipsis(transactionsObj.get("to").toString(), length);

					}else if (type.equals("path_payment_strict_receive")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("from").toString()));
						assetTransactions.setToAccount(StringUtils.trim(transactionsObj.get("to").toString()));
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						String asset_code = null;
		                 if (transactionsObj.get("asset_type").toString().equals("native")) {
		                	 asset_code = "XLM";
						}else {
				             asset_code = transactionsObj.get("asset_code").toString();							
						}
						assetTransactions.setAssetCode(StringUtils.trim(asset_code));
			              txnmode = "C";	
						  assetTransactions.setTxnMode(StringUtils.trim(txnmode));
						  
			              description = "Path Payment "+transactionsObj.get("amount").toString() + " "+ asset_code+" to "+ Utilities.ellipsis(transactionsObj.get("to").toString(), length);
			              
					}else if(type.equals("create_account")) {
			              assetTransactions.setFunder(StringUtils.trim(transactionsObj.get("funder").toString()));
			              assetTransactions.setAccount(StringUtils.trim(transactionsObj.get("account").toString()));
			              assetTransactions.setStartingBalance(StringUtils.trim(transactionsObj.get("starting_balance").toString()));
			               txnmode = "D";		                	 
		               
		                   description = "Create account "+Utilities.ellipsis(transactionsObj.get("account").toString(), 13) + " with  "+ transactionsObj.get("starting_balance").toString()+" XLM " ;
	                 }else if(type.equals("create_claimable_balance")) {
                	 	assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("sponsor").toString()));
						assetTransactions.setToAccount("");
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						if(StringUtils.trim(transactionsObj.get("asset").toString()).equals("native")){
							assetCode = "XLM";
						}else {
							assetCode = StringUtils.trim(transactionsObj.get("asset").toString()).split(":")[0];
						}
						assetTransactions.setAssetCode(assetCode);
						if(!assetTransactions.getFromAccount().equals(accountId)){
							txnmode = "C";
							assetTransactions.setType("Claimable Balance to Claim");
							description ="Created Claimable balance of "+assetTransactions.getAmount() +" "+assetCode;
						}else {
							txnmode = "D";
							description ="Created Claimable balance of "+assetTransactions.getAmount() +" "+assetCode;
						}
								                	 
					   assetTransactions.setTxnMode(StringUtils.trim(txnmode));
	                 }else if(type.equals("claim_claimable_balance")) {
	                	 assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("source_account").toString()));
						assetTransactions.setToAccount(accountId);
						
						assetTransactions.setAssetCode("");
						txnmode = "C";		                	 
					   assetTransactions.setTxnMode(StringUtils.trim(txnmode));
					   description =" Claimabled Claimable balance "; 
	                 }
				    transactionsObj2 = (JSONObject) parser.parse(transactionsObj.get("transaction").toString());
					assetTransactions.setFeeCharged(StringUtils.trim(transactionsObj2.get("fee_charged").toString()));
					assetTransactions.setLedger(StringUtils.trim(transactionsObj2.get("ledger").toString()));
					assetTransactions.setDescription(StringUtils.trim(description));
					
					arrAssetTransactions.add(assetTransactions);
										
					if(arrAssetTransactions !=null) {
					}
				}

			} else {
				NeoBankEnvironment.setComment(3, className, "==== Failed to get Transactions " + response.toString());
			}
			NeoBankEnvironment.setComment(3, className, "==== end getAccountTransactions : " + java.time.LocalTime.now());

		} catch (Exception e) {
			throw new Exception(" Method getAccountTransactions:  Error is " + e.getMessage());		
		} finally {
			if(client !=null) client.close(); client=null; if(httpGet !=null) httpGet.clear(); httpGet=null;   
			if(response !=null) response.close(); response = null; if(result !=null) result=null;
			if(parser !=null) parser= null; if(jsonObject !=null) jsonObject.clear(); jsonObject = null;
			if(jsonObjectLinks !=null) jsonObjectLinks.clear();jsonObjectLinks = null; 
			if(jsonObjectEmbeded !=null) jsonObjectEmbeded.clear();jsonObjectEmbeded = null;
			if(transactionsObj !=null) transactionsObj.clear();transactionsObj = null;
			if(transactionsObj2 !=null) transactionsObj2.clear();transactionsObj2 = null;
		}
		return arrAssetTransactions;
     }
	
	public static ArrayList<AssetTransactions> getAccountTransactionsCustomerWeb(String accountId,String link)throws Exception {        
		CloseableHttpClient client = null; HttpGet httpGet = null;  CloseableHttpResponse response = null;
		String result = null; JSONParser parser = null; JSONObject jsonObject = null; JSONObject jsonObjectLinks = null;
		JSONObject jsonObjectEmbeded = null; JSONObject transactionsObj = null; JSONObject transactionsObj2 = null;
		ArrayList<AssetTransactions> arrAssetTransactions = null;String txnmode = null; String description = "";
		int length = 13; 	String nextLink=null; String previousLink=null;
		JSONObject nextLinkObj=null;
		JSONObject prevLinkObj=null;
		String assetCode = null;
		try {
			
			client = HttpClients.createDefault();
			
			httpGet = new HttpGet(link);
			response = client.execute(httpGet);
			if (response.getCode() == 200) {
				NeoBankEnvironment.setComment(3, className, "==== status Ok : " + java.time.LocalTime.now());
				try {
					result = EntityUtils.toString(response.getEntity());
				} catch (Exception e) {
					throw new Exception(" Error in getting account Transaction from Stellar " + e.getMessage());
				}
				parser = new JSONParser();
				jsonObject = (JSONObject) parser.parse(result.toString());
				jsonObjectLinks = (JSONObject) parser.parse(jsonObject.get("_links").toString());
				
				jsonObjectEmbeded = (JSONObject) parser.parse(jsonObject.get("_embedded").toString());

				JSONArray jsonArray = (JSONArray) jsonObjectEmbeded.get("records");
				arrAssetTransactions = new ArrayList<AssetTransactions>();
				
				for (int i = 0; i < jsonArray.size(); i++) {
					AssetTransactions assetTransactions = new AssetTransactions();
					transactionsObj = (JSONObject) parser.parse(jsonArray.get(i).toString());
					String type = transactionsObj.get("type").toString();
					assetTransactions.setOperationId(StringUtils.trim(transactionsObj.get("id").toString()));
					assetTransactions.setIsSuccess(StringUtils.trim(transactionsObj.get("transaction_successful").toString()));
					assetTransactions.setType(StringUtils.trim(transactionsObj.get("type").toString()));
					assetTransactions.setCreatedOn(StringUtils.trim(Utilities.getStellarDateConvertor(transactionsObj.get("created_at").toString())));
					assetTransactions.setSourceAccount(StringUtils.trim(transactionsObj.get("source_account").toString()));
					if (type.equals("payment")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("from").toString()));
						assetTransactions.setToAccount(StringUtils.trim(transactionsObj.get("to").toString()));
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						if(StringUtils.trim(transactionsObj.get("asset_type").toString()).equals("native")){
							assetCode = "XLM";
						}else {
							assetCode = StringUtils.trim(transactionsObj.get("asset_code").toString());
						}
						assetTransactions.setAssetCode(assetCode);
						if(transactionsObj.get("from").toString().equals( accountId)) {
							txnmode = "D";		                	 
						}else  {
							txnmode = "C";		                	 
						}
						assetTransactions.setTxnMode(StringUtils.trim(txnmode));
						
						//Payment 15.0000000 PORTE from GALBMMADCB... to GA4E2RGZQ5...
						description = "Payment "+transactionsObj.get("amount").toString()+ " "+assetCode
								+" from "+Utilities.ellipsis(transactionsObj.get("from").toString(), length)  + " to "+Utilities.ellipsis(transactionsObj.get("to").toString(), length);
					} else if (type.equals("manage_sell_offer")) {
						
					}else if (type.equals("manage_buy_offer")) {
						
					} else if (type.equals("change_trust")) {
						assetTransactions.setTrustee(StringUtils.trim(transactionsObj.get("trustee").toString()));
						assetTransactions.setTrustor(StringUtils.trim(transactionsObj.get("trustor").toString()));
						assetTransactions.setAssetCode(StringUtils.trim(transactionsObj.get("asset_code").toString()));
						String trustLimit  = transactionsObj.get("limit").toString();
						
						description = "Trust "+Utilities.ellipsis(transactionsObj.get("trustee").toString(), length) +" issuing "+transactionsObj.get("asset_code").toString()
								+" with limit "+trustLimit;
					} else if (type.equals("path_payment_strict_send")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("from").toString()));
						assetTransactions.setToAccount(StringUtils.trim(transactionsObj.get("to").toString()));
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						String asset_code = null;
						if (transactionsObj.get("asset_type").toString().equals("native")) {
							asset_code = "XLM";
						}else {
							asset_code = transactionsObj.get("asset_code").toString();							
						}
						assetTransactions.setAssetCode(StringUtils.trim(asset_code));
						txnmode = "C";	
						assetTransactions.setTxnMode(StringUtils.trim(txnmode));
						
						description = "Path Payment "+transactionsObj.get("amount").toString() + " "+ asset_code+" to "+ Utilities.ellipsis(transactionsObj.get("to").toString(), length);
						
					}else if (type.equals("path_payment_strict_receive")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("from").toString()));
						assetTransactions.setToAccount(StringUtils.trim(transactionsObj.get("to").toString()));
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						String asset_code = null;
						if (transactionsObj.get("asset_type").toString().equals("native")) {
							asset_code = "XLM";
						}else {
							asset_code = transactionsObj.get("asset_code").toString();							
						}
						assetTransactions.setAssetCode(StringUtils.trim(asset_code));
						txnmode = "C";	
						assetTransactions.setTxnMode(StringUtils.trim(txnmode));
						
						description = "Path Payment "+transactionsObj.get("amount").toString() + " "+ asset_code+" to "+ Utilities.ellipsis(transactionsObj.get("to").toString(), length);
						
					}else if(type.equals("create_account")) {
						assetTransactions.setFunder(StringUtils.trim(transactionsObj.get("funder").toString()));
						assetTransactions.setAccount(StringUtils.trim(transactionsObj.get("account").toString()));
						assetTransactions.setStartingBalance(StringUtils.trim(transactionsObj.get("starting_balance").toString()));
						txnmode = "D";		                	 
						
						description = "Create account "+Utilities.ellipsis(transactionsObj.get("account").toString(), 13) + " with  "+ transactionsObj.get("starting_balance").toString()+" XLM " ;
					}else if(type.equals("create_claimable_balance")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("sponsor").toString()));
						assetTransactions.setToAccount("");
						assetTransactions.setAmount(StringUtils.trim(transactionsObj.get("amount").toString()));
						if(StringUtils.trim(transactionsObj.get("asset").toString()).equals("native")){
							assetCode = "XLM";
						}else {
							assetCode = StringUtils.trim(transactionsObj.get("asset").toString()).split(":")[0];
						}
						assetTransactions.setAssetCode(assetCode);
						if(!assetTransactions.getFromAccount().equals(accountId)){
							txnmode = "C";
							assetTransactions.setType("Claimable Balance to Claim");
							description ="Created Claimable balance of "+assetTransactions.getAmount() +" "+assetCode;
						}else {
							txnmode = "D";
							description ="Created Claimable balance of "+assetTransactions.getAmount() +" "+assetCode;
						}
						
						assetTransactions.setTxnMode(StringUtils.trim(txnmode));
					}else if(type.equals("claim_claimable_balance")) {
						assetTransactions.setFromAccount(StringUtils.trim(transactionsObj.get("source_account").toString()));
						assetTransactions.setToAccount(accountId);
						
						assetTransactions.setAssetCode("");
						txnmode = "C";		                	 
						assetTransactions.setTxnMode(StringUtils.trim(txnmode));
						description =" Claimabled Claimable balance "; 
					}
					transactionsObj2 = (JSONObject) parser.parse(transactionsObj.get("transaction").toString());
					assetTransactions.setFeeCharged(StringUtils.trim(transactionsObj2.get("fee_charged").toString()));
					assetTransactions.setLedger(StringUtils.trim(transactionsObj2.get("ledger").toString()));
					assetTransactions.setDescription(StringUtils.trim(description));
					
					 nextLinkObj = (JSONObject) parser.parse(jsonObjectLinks.get("next").toString());
					 nextLink = (String)nextLinkObj.get("href");
					 prevLinkObj = (JSONObject) parser.parse(jsonObjectLinks.get("prev").toString());
					 previousLink = (String)prevLinkObj.get("href");
					 NeoBankEnvironment.setComment(3, className, " nextLink " +nextLink);		
					 NeoBankEnvironment.setComment(3, className, " previousLink " +previousLink);		
					 assetTransactions.setPreviousLink(previousLink);
					 assetTransactions.setNextLink(nextLink);
					
					 arrAssetTransactions.add(assetTransactions);
					
				}
				
				
			} else {
				NeoBankEnvironment.setComment(3, className, "==== Failed to get Transactions " + response.toString());
			}
			NeoBankEnvironment.setComment(3, className, "==== end getAccountTransactions : " + java.time.LocalTime.now());
			
		} catch (Exception e) {
			throw new Exception(" Method getAccountTransactions:  Error is " + e.getMessage());		
		} finally {
			if(client !=null) client.close(); client=null; if(httpGet !=null) httpGet.clear(); httpGet=null;   
			if(response !=null) response.close(); response = null; if(result !=null) result=null;
			if(parser !=null) parser= null; if(jsonObject !=null) jsonObject.clear(); jsonObject = null;
			if(jsonObjectLinks !=null) jsonObjectLinks.clear();jsonObjectLinks = null; 
			if(jsonObjectEmbeded !=null) jsonObjectEmbeded.clear();jsonObjectEmbeded = null;
			if(transactionsObj !=null) transactionsObj.clear();transactionsObj = null;
			if(transactionsObj2 !=null) transactionsObj2.clear();transactionsObj2 = null;
		}
		return arrAssetTransactions;
	}
	
	public static String getPaymentResultCode(String opError)throws IOException {
		ConcurrentHashMap<String, String>  manageSellOfferResultCode = new ConcurrentHashMap<String, String>();
			manageSellOfferResultCode.put("[op_bad_auth]", "There are too few valid signatures, or the transaction was submitted to the wrong network");
			manageSellOfferResultCode.put("[op_bad_auth]", "There are too few valid signatures, or the transaction was submitted to the wrong network");
			manageSellOfferResultCode.put("[op_no_source_account]", "The source account was not found.");
			manageSellOfferResultCode.put("[op_not_supported]", "The operation is not supported at this time.");
			manageSellOfferResultCode.put("[op_too_many_subentries]", "Maximu number of subentries already reached.");
			manageSellOfferResultCode.put("[op_exceeded_work_limit]", "Operation did too much work.");
		 
			manageSellOfferResultCode.put("op_success", "The payment was successfully completed.");
			manageSellOfferResultCode.put("[op_malformed]", "The input to the payment is invalid");
			manageSellOfferResultCode.put("[op_malformed]", "The input to the payment is invalid");
			manageSellOfferResultCode.put("[op_underfunded]", "The source account (sender) does not have enough lumens to send the payment.");
			manageSellOfferResultCode.put("[op_source_no_trust]", "The source account does not have a trustline for the asset it is tring to send.");
			manageSellOfferResultCode.put("[op_source_not_authorized]", "The source account is not authorized to send this asset");
			manageSellOfferResultCode.put("[op_no_destination]", "The destination account does not exist.");
			manageSellOfferResultCode.put("[op_no_trust]", "The destination account does not have a trustline for the asset being sent.");
			manageSellOfferResultCode.put("[op_not_authorized]", "The destination account is not authorized to hold this asset.");
			manageSellOfferResultCode.put("[op_linefull]", "The account creating the offer does not have sufficient limits to receive buying and still satisfy its buying liabilities.");
			manageSellOfferResultCode.put("[op_no_issuer]", "The issuer of the asset does not exist.");
			
		return manageSellOfferResultCode.get(opError);
	}
	
	public static String getPathPaymentStrictReceiveResultCode(String opError)throws IOException {
		ConcurrentHashMap<String, String>  manageSellOfferResultCode = new ConcurrentHashMap<String, String>();
			manageSellOfferResultCode.put("op_success", "The payment was successfully completed.");
			manageSellOfferResultCode.put("[op_malformed]", "The input to the payment is invalid");
			manageSellOfferResultCode.put("[op_underfunded]", "The source account (sender) does not have enough lumens to send the payment.");
			manageSellOfferResultCode.put("[op_source_no_trust]", "The source account does not have a trustline for the asset it is tring to send.");
			manageSellOfferResultCode.put("[op_source_not_authorized]", "The source account is not authorized to send this asset");
			manageSellOfferResultCode.put("[op_no_destination]", "The destination account does not exist.");
			manageSellOfferResultCode.put("[op_no_trust]", "The destination account does not have a trustline for the asset being sent.");
			manageSellOfferResultCode.put("[op_not_authorized]", "The destination account is not authorized to hold this asset.");
			manageSellOfferResultCode.put("[op_linefull]", "The destination account (receiver) does not have sufficient limits to receive amount and still satisfy its buying liabilities.");
			manageSellOfferResultCode.put("[op_no_issuer]", "The issuer of the asset does not exist.");
			manageSellOfferResultCode.put("[op_too_few_offers]", "There is no path of offers connecting the send asset and destination asset.");
			manageSellOfferResultCode.put("[op_cross_self]", "This path payment would cross one of its own offers.");
			manageSellOfferResultCode.put("[op_over_source_max]", "The paths that could send destination amount of destination asset would fall short of destination min.");
			
			manageSellOfferResultCode.put("[op_bad_auth]", "There are too few valid signatures, or the transaction was submitted to the wrong network");
			manageSellOfferResultCode.put("[op_bad_auth]", "There are too few valid signatures, or the transaction was submitted to the wrong network");
			manageSellOfferResultCode.put("[op_no_source_account]", "The source account was not found.");
			manageSellOfferResultCode.put("[op_not_supported]", "The operation is not supported at this time.");
			manageSellOfferResultCode.put("[op_too_many_subentries]", "Maximu number of subentries already reached.");
			manageSellOfferResultCode.put("[op_exceeded_work_limit]", "Operation did too much work.");
						
		return manageSellOfferResultCode.get(opError);
	}
	
	public static String getPathPaymentStrictSendResultCode(String opError)throws IOException {
		ConcurrentHashMap<String, String>  manageSellOfferResultCode = new ConcurrentHashMap<String, String>();
			manageSellOfferResultCode.put("op_success", "The payment was successfully completed.");
			manageSellOfferResultCode.put("[op_malformed]", "The input to the payment is invalid");
			manageSellOfferResultCode.put("[op_underfunded]", "The source account (sender) does not have enough lumens to send the payment.");
			manageSellOfferResultCode.put("[op_source_no_trust]", "The source account is missing the appropriate trustline.");
			manageSellOfferResultCode.put("[op_source_not_authorized]", "The source account is not authorized to send this asset");
			manageSellOfferResultCode.put("[op_no_destination]", "The destination account does not exist.");
			manageSellOfferResultCode.put("[op_no_trust]", "The destination account does not have a trustline for the asset being sent.");
			manageSellOfferResultCode.put("[op_not_authorized]", "The destination account is not authorized to hold this asset.");
			manageSellOfferResultCode.put("[op_linefull]", "The destination account (receiver) does not have sufficient limits to receive amount and still satisfy its buying liabilities.");
			manageSellOfferResultCode.put("[op_no_issuer]", "The issuer of the asset does not exist.");
			manageSellOfferResultCode.put("[op_too_few_offers]", "There is no path of offers connecting the send asset and destination asset.");
			manageSellOfferResultCode.put("[op_cross_self]", "This path payment would cross one of its own offers.");
			manageSellOfferResultCode.put("[op_under_dest_min]", "The paths that could send destination amount of destination asset would fall short of destination min.");
			
			manageSellOfferResultCode.put("[op_bad_auth]", "There are too few valid signatures, or the transaction was submitted to the wrong network");
			manageSellOfferResultCode.put("[op_bad_auth]", "There are too few valid signatures, or the transaction was submitted to the wrong network");
			manageSellOfferResultCode.put("[op_no_source_account]", "The source account was not found.");
			manageSellOfferResultCode.put("[op_not_supported]", "The operation is not supported at this time.");
			manageSellOfferResultCode.put("[op_too_many_subentries]", "Maximu number of subentries already reached.");
			manageSellOfferResultCode.put("[op_exceeded_work_limit]", "Operation did too much work.");
						
		return manageSellOfferResultCode.get(opError);
	}
	
	public static String pathPaymentStrictSend(String sourceAssetCode,String destionAssetCode, String sourceIssuierPublicKey, 
			String destinationIssuierPublicKey, String destMinAmount, String sendAmount,String sourceAcount ) throws Exception{
		
		String result = "false";
		try {
			NeoBankEnvironment.setComment(3, className, "====inside pathPaymentStrictSend : " + java.time.LocalTime.now());
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			
		 	KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(sourceAcount);
		 	server.accounts().account(sourceAccountKeyPair.getAccountId());
		 	AccountResponse sourceAccount = server.accounts().account(sourceAccountKeyPair.getAccountId());

		 	Asset sourceAsset = Asset.createNonNativeAsset(sourceAssetCode, sourceIssuierPublicKey);
		 	Asset destinationAsset = Asset.createNonNativeAsset(destionAssetCode, destinationIssuierPublicKey);
		 		 			 	
		 	//Intermediate path asset
		 	AssetTypeNative nativeAsset = new AssetTypeNative();
		 	Asset[] path = {nativeAsset};
		 	Transaction transaction = null;
		 	if(sourceAssetCode.equals("XLM")) {
		 	 	 transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
				        .addOperation(new PathPaymentStrictSendOperation.Builder(
				        		new AssetTypeNative(), sendAmount, sourceAccountKeyPair.getAccountId(), 
				        		destinationAsset, destMinAmount )
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
				        		sourceAsset, sendAmount, sourceAccountKeyPair.getAccountId(), 
				        		destinationAsset, destMinAmount )
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
						result = "success";
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
				 
				}
		} catch (Exception e) {
			result = "false";
			throw new Exception("Error from pathPaymentStrictSend is: "+e.getMessage());		}
		return result;
	}
	
	public static String pathPaymentStrictReceive(String sourceAssetCode,String destionAssetCode, String sourceIssuierPublicKey, 
			String destinationIssuierPublicKey, String maximumSendAmount, String destinationAmount,String sourceAcount) throws Exception {
		String result = "false";

		try {
			System.out.println("start pathPaymentStrictReceive " +java.time.LocalDateTime.now());
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
		 				
		 	 
			KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(sourceAcount);

		 	server.accounts().account(sourceAccountKeyPair.getAccountId());
		 	AccountResponse sourceAccount = server.accounts().account(sourceAccountKeyPair.getAccountId());

		   //Create an object to represent the new asset
		 	Asset sourceAsset = Asset.createNonNativeAsset(sourceAssetCode, sourceIssuierPublicKey);
		 	Asset destinationAsset = Asset.createNonNativeAsset(destionAssetCode, destinationIssuierPublicKey);
		 	
			//Intermediate path asset
		 	AssetTypeNative nativeAsset = new AssetTypeNative();
		 	//Porte XLM USDC
		 	Asset[] path = {nativeAsset};
		 			 	
		 	Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
			        .addOperation(new PathPaymentStrictReceiveOperation.Builder(
			        		sourceAsset, maximumSendAmount, sourceAccountKeyPair.getAccountId(),
			        		destinationAsset, destinationAmount  )
			        		.setPath(path)
			        		.setSourceAccount(sourceAccountKeyPair.getAccountId())
			        		.build())
			        .addMemo(Memo.text("Test Transaction"))
			        .setTimeout(180)
			        .setBaseFee(Transaction.MIN_BASE_FEE)
			        .build();
			transaction.sign(sourceAccountKeyPair);
			
			SubmitTransactionResponse response = null;
			
			try {
				  response = server.submitTransaction(transaction);
				 
				  if(response.getExtras()==null) {
						result = "success";
					  }else {							
							String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
							result = StellarSDKUtility.getSellOfferResultCode(operationError);
							NeoBankEnvironment.setComment(3, className, "==== Failed in Path Payment Strict Receive error: "+operationError
									+" description: "+result+" "+  java.time.LocalTime.now());
					  }
				} finally {
					if (server != null)server.close();if (response != null)response = null;
					if (transaction != null)transaction = null;if (sourceAccount != null)sourceAccount = null;
				 
				}
		 	
		} catch (Exception e) {
			result = "false";
			throw new Exception("Error from pathPaymentStrictSend is: "+e.getMessage());			}
		return result;
	}

	public static String getPathStrictSendWithDestinationAssets(String sourceAssetType, String sourceAssetCode,
			String sourceAssetIssuer, String sourceAmount, String destAssetCode, String destAssetIssuer)
			throws Exception {
		
		String exchangeRate = null; Asset customerAsset = null;
		try {
			String cursor = "13537736921089";
			String order = "asc";
			String limit = "3";
			String path = "/paths/strict-send";
			if(destAssetCode.equals("XLM")) {
				 customerAsset = new AssetTypeNative();
			}else {
				 customerAsset = Asset.createNonNativeAsset(destAssetCode, destAssetIssuer);
			}
			
			List<Asset> assets = Lists.newArrayList();
			assets.add(customerAsset);
			String destinationAsssets = Arrays.toString(assets.toArray()).replace("[", "").replace("]", "");

			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost("horizon-testnet.stellar.org").setPath(path)
					.setParameter("destination_assets", destinationAsssets).setParameter("source_amount", sourceAmount)
					.setParameter("source_asset_type", sourceAssetType)
					.setParameter("source_asset_code", sourceAssetCode)
					.setParameter("source_asset_issuer", sourceAssetIssuer).setParameter("cursor", cursor)
					.setParameter("limit", limit).setParameter("order", order);
			URI uri = builder.build();

			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = client.execute(httpGet);
			if (response.getCode() == 200) {
				String result = null;
				try {
					result = EntityUtils.toString(response.getEntity());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(result.toString());
				JSONObject jsonObjectEmbeded = (JSONObject) parser.parse(jsonObject.get("_embedded").toString());
				JSONArray jsonArray = (JSONArray) jsonObjectEmbeded.get("records");
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonAssetToDestObj = (JSONObject) parser.parse(jsonArray.get(i).toString());
					String destinationAmount = jsonAssetToDestObj.get("destination_amount").toString();
					JSONArray jsonArrayPath = (JSONArray) jsonAssetToDestObj.get("path");
				 	  if(jsonArrayPath.size() == 1) {
				 		   for (int j = 0; j < jsonArrayPath.size(); j++) {
				   			 	 JSONObject pathObj = (JSONObject) parser.parse(jsonArrayPath.get(j).toString());
						          String assetType = pathObj.get("asset_type").toString();
						          String pathAssetCode = null;
						           if(assetType.equals("native")) {
						        	   pathAssetCode = "XLM";
						           }else {
						        	   pathAssetCode = pathObj.get("asset_code").toString();
						           }
						           if(pathAssetCode.equals("XLM")) {
						        	   exchangeRate = destinationAmount;
						           }
						    }   
				 	  }
				 	  
				 	  //Linked direct to native
				 	 if(jsonArrayPath.size() == 0) {
			        	   exchangeRate = destinationAmount;
				 	 }
				}
				
				 
				NeoBankEnvironment.setComment(3, className, "====end getPathStrictSendWithDestinationAssets "+exchangeRate+" "+java.time.LocalTime.now());

			} else {
				NeoBankEnvironment.setComment(3, className, "==== Failed is error is "+response.getCode());
				throw new Exception("Failed is error is "+response.getCode());
			}
		} catch (Exception e) {
			throw new Exception("Error from getPathStrictSendWithDestinationAssets is: "+e.getMessage());	
		}
	
		return exchangeRate;
	}
	
	public static boolean buyNativeCoinPaymen(String distributorPrivateKey, String customerPublicKey,
			String assetCode, String issuerPublicKey, String coinsToBuy, String Comment) throws Exception {
		boolean result = false;
		try {
			
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());

			KeyPair source = KeyPair.fromSecretSeed(distributorPrivateKey);
			KeyPair destination = KeyPair.fromAccountId(customerPublicKey);
			server.accounts().account(destination.getAccountId());

			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
					.addOperation(
							new PaymentOperation.Builder(destination.getAccountId(), new AssetTypeNative(), coinsToBuy).build())					// optional and does not affect how Stellar treats the transaction.
					.addMemo(Memo.text(Comment))
					.setTimeout(180)
					.setBaseFee(Transaction.MIN_BASE_FEE).build();
			transaction.sign(source);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				result = response.isSuccess();
			} finally {
				if (server != null) server.close(); if (response != null) response = null;
				if (transaction != null) transaction = null; if (sourceAccount != null) sourceAccount = null;
				if (source != null) 	source = null; 	if (destination != null) destination = null;
			}

		} catch (Exception e) {
			result = false;
			throw new Exception(" Method buyNativeCoinPayment:  Error in buying " + e.getMessage());
		}
		return result;
	}
	public static String sendNativeCoinPayment(String assetCode, KeyPair sourceAccount, KeyPair destinationAccount, String amount,
			String narrative) throws Exception {
		String result = "false";
		String stellarHash="";

		try {
			NeoBankEnvironment.setComment(3, className, " assetCode : " + assetCode+ " sourceAccount " + sourceAccount.getAccountId()+ " destinationAccount "
					+ " "+ destinationAccount.getAccountId()+ " amount "+ amount+ " narrative "+ narrative);

		 	Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());
			server.accounts().account(destinationAccount.getAccountId());
			AccountResponse sourceAccountResponse = server.accounts().account(sourceAccount.getAccountId());	 		
			Transaction transaction = new Transaction.Builder(sourceAccountResponse, Network.TESTNET)
			        .addOperation(new PaymentOperation.Builder(destinationAccount.getAccountId(), new AssetTypeNative(), amount).build())
			        .addMemo(Memo.text(narrative))
			        .setTimeout(180)
			        .setBaseFee(Transaction.MIN_BASE_FEE)
			        .build();
			transaction.sign(sourceAccount);
			try {
				SubmitTransactionResponse response = server.submitTransaction(transaction); // System.out.println(response.isSuccess());
				if (response.getExtras() == null) {
					//Get Stellar Hash
					stellarHash=response.getHash();
					NeoBankEnvironment.setComment(3, className, "Porte Coin Transfer successful : " + java.time.LocalTime.now() + "  " + response.toString());
					result = "success"+","+stellarHash;
				} else {
					String operationError = response.getExtras().getResultCodes().getOperationsResultCodes().toString();
					result = StellarSDKUtility.getPaymentResultCode(operationError);
					NeoBankEnvironment.setComment(3, className, "==== Transfer asset Failed operation error: "+operationError
							+" description: "+result+" "+  java.time.LocalTime.now());
				}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(3, className, "Error occured in Porte Transfer: " + e.getMessage());
				throw new Exception("Failed to complete the Transfer Payment");
			}
		} catch (Exception e) {
			result = "false";
			throw new Exception("Error from sendNoNNativeCoinPayment is: "+e.getMessage());
		}
		return result;
	}
	public static boolean burnCoin(String sourcePrivateKey, 
			String assetCode, String issuerPublicKey, String coinsToBurn, String Comment) throws Exception {
		boolean result = false;
		try {
			
			Server server = new Server(NeoBankEnvironment.getStellarTestEviromentUrl());

			KeyPair source = KeyPair.fromSecretSeed(sourcePrivateKey);
			KeyPair destination = KeyPair.fromAccountId(issuerPublicKey);
			server.accounts().account(destination.getAccountId());

			AccountResponse sourceAccount = server.accounts().account(source.getAccountId());

			Asset sourceAsset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);// Loading Custom Asset

			Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
					.addOperation(
							new PaymentOperation.Builder(destination.getAccountId(), sourceAsset, coinsToBurn).build())					// optional and does not affect how Stellar treats the transaction.
					.addMemo(Memo.text(Comment))
					.setTimeout(180)
					.setBaseFee(Transaction.MIN_BASE_FEE).build();
			transaction.sign(source);
			SubmitTransactionResponse response = null;
			try {
				response = server.submitTransaction(transaction);
				result = response.isSuccess();
			} finally {
				if (server != null)
					server.close();
				if (response != null)
					response = null;
				if (transaction != null)
					transaction = null;
				if (sourceAccount != null)
					sourceAccount = null;
				if (source != null)
					source = null;
				if (destination != null)
					destination = null;
			}

		} catch (Exception e) {
			result = false;
			throw new Exception(" Method burnCoin:  Error in buying " + e.getMessage());
		}
		return result;
	}
	
	
	
	
	
}
