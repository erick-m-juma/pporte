package com.pporte.rules;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDao;
import com.pporte.dao.CustomerDigitalAssetsDao;
import com.pporte.dao.CustomerDigitalCurrenciesDao;
import com.pporte.dao.CustomerWalletDao;
import com.pporte.dao.SystemUtilsDao;
import com.pporte.dao.WalletAuthorizationDao;

import com.pporte.model.PaymentOffer;
import com.pporte.model.User;
import com.pporte.utilities.Bip39Utility;
import com.pporte.utilities.CurrencyTradeUtility;
import com.pporte.utilities.StellarSDKUtility;
import com.pporte.utilities.Utilities;

import framework.v8.Rules;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CustomerDigitalCurrenciesRulesImpl implements Rules{
	private static String className = CustomerDigitalCurrenciesRulesImpl.class.getSimpleName();

	@Override
	public void performJSONOperation(String arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			ServletContext arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		switch (rulesaction) {
		
		case "add_digital_currency":
			try {
				JsonObject obj = new JsonObject();
				User user = null; int baseFee  = org.stellar.sdk.Transaction.MIN_BASE_FEE;
				String relationshipNo= null; String limit = null;
				PrintWriter output = null; String assetCode =null;
				String privateKey =null;
				String publicKey = null;
				String issuerAccountId = null;
				boolean createTrustline = false;
				HttpSession session = request.getSession(false);
				if (session.getAttribute("SESS_USER") == null) {
					Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
					return;
				}
				user = (User) session.getAttribute("SESS_USER");
				relationshipNo = user.getRelationshipNo();
				if(request.getParameter("selcurrency")!=null)	
					assetCode = request.getParameter("selcurrency").trim();
				if(request.getParameter("input_private_key")!=null)	
					privateKey = request.getParameter("input_private_key").trim();
				NeoBankEnvironment.setComment(3, className, " assetCode "+assetCode );
				publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
									
				if(publicKey == null) {
					throw new Exception("In creating wallet");
					}
				
				KeyPair source = KeyPair.fromSecretSeed(privateKey);
			     if(!source.getAccountId().equals(publicKey)) {
					Utilities.sendJsonResponse(response, "error", "Secret key is incorrect");
			    	return;
			     }
				issuerAccountId = NeoBankEnvironment.getDigitalCurrencyIssuerAccountId();
				
				if(CustomerDao.class.getConstructor().newInstance().checkIfHasWallet(assetCode, relationshipNo)) {
					Utilities.sendJsonResponse(response, "error", "Digital Currency already registered");
					return;
				}
				limit = NeoBankEnvironment.getMaxStellarAssetWalletLimit();
				createTrustline = StellarSDKUtility.createTrustline(issuerAccountId, privateKey,
						 baseFee, limit,  assetCode );
				if(createTrustline) {
					CustomerWalletDao.class.getConstructor().newInstance().createWallet(
							relationshipNo, "",publicKey, assetCode);
				}else {
					throw new Exception("Error in creating Trustline");
				}
				 try {
		        	if(createTrustline == false) {
		        		SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", "CDW","Customer Added Digital Currency "+assetCode );
		        		obj.addProperty("error", "true"); 
		        		obj.addProperty("message", "Adding Digital Currency failed"); 
		        	}else {
		        		obj.addProperty("error", "false"); 
		        		obj.addProperty("message", "Digital Currency added suceessfully");
		        	}
					output = response.getWriter();
					output.print(obj);
					}finally {
						if(output != null) output.flush();
						if(obj!=null) obj = null;
						if(user!=null) user = null;
						if(relationshipNo!=null) relationshipNo = null;
					}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Adding Digital failed, Please try again letter");
			}
			
			break;
			
		case "exchange_digital_asset_to_currency":
			try {
				JsonObject obj = new JsonObject(); PrintWriter output = null;
				JsonObject offerObj = null; Gson gson = new Gson();
				String jsonString = null; User user = null;
				String privateKey = null; String sourceAcountId= null;
				String sourceAsset = null;
				String sourceAssetIssuer = null;
				String sourceAmount = null;
				String destinationAsset = null;
				String destinationIssuer = null;
				String destinationAmount = null;
				JsonArray paymentPaths = null;
				String relationshipNo = null;
				String receiverName=null;String receiverEmail=null; String [] splitResult=null;
				String receiverBankName=null;	String receiverBankCode=null; String receiverAccountNo=null; String senderComment=null;
				boolean success=false;String stellarTxnHash=null; String payMode="CCR"; String transactionCode=null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null;
				if(request.getParameter("json_string")!=null)	 jsonString = request.getParameter("json_string").trim();
				//if(request.getParameter("private_key")!=null)	 privateKey = request.getParameter("private_key").trim();
				if(request.getParameter("receiver_name")!=null)	 receiverName = request.getParameter("receiver_name").trim();
				if(request.getParameter("receiver_email")!=null)	 receiverEmail = request.getParameter("receiver_email").trim();
				if(request.getParameter("receiver_bank_name")!=null)	 receiverBankName = request.getParameter("receiver_bank_name").trim();
				if(request.getParameter("receiver_bank_code")!=null)	 receiverBankCode = request.getParameter("receiver_bank_code").trim();
				if(request.getParameter("receiver_account_no")!=null)	 receiverAccountNo = request.getParameter("receiver_account_no").trim();
				
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				HttpSession session = request.getSession(false);
				if (session.getAttribute("SESS_USER") == null) {
					Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
					return;
				}
				
				user = (User) session.getAttribute("SESS_USER");
				relationshipNo = user.getRelationshipNo();
				if(hasMnemonic.equals("true")) {
					if(request.getParameter("security")!=null)	 password = request.getParameter("security").trim();
					passIsCorrect = (boolean) CustomerDao.class.getConstructor().newInstance()
							.checkIfPasswordIsCorrect(relationshipNo, password);
					if(!passIsCorrect) {
						NeoBankEnvironment.setComment(1, className, "Password is not correct");
						Utilities.sendJsonResponse(response, "error", "Please enter the correct password");
						return;
					}
					mnemoniStringFromDB = (String) CustomerDao.class.getConstructor().newInstance()
							.getmnemonicCode(relationshipNo);
					keyPair= Bip39Utility.masterKeyGeneration(mnemoniStringFromDB.replaceAll(",", " "));
					privateKey = String.valueOf(keyPair.getSecretSeed());
				}else {
					if(request.getParameter("security")!=null)	privateKey = request.getParameter("security").trim();
				}
			
				NeoBankEnvironment.setComment(3, className, "jsonString "+jsonString );
				if(jsonString==null||jsonString.isEmpty())
					throw new Exception("Json string is empty");
				offerObj = gson.fromJson(jsonString, JsonObject.class);
				sourceAsset = offerObj.get("sourceAsset").getAsString();
				sourceAssetIssuer = offerObj.get("sourceAssetIssuer").getAsString();
				sourceAmount = offerObj.get("sourceAmount").getAsString();
				destinationAsset = offerObj.get("destinationAsset").getAsString();
				destinationIssuer = offerObj.get("destinationIssuer").getAsString();
				destinationAmount = offerObj.get("destinationAmount").getAsString();
				paymentPaths = offerObj.get("paymentPaths").getAsJsonArray();
				
				NeoBankEnvironment.setComment(3, className, "sourceAsset "+sourceAsset+" sourceAssetIssuer "+sourceAssetIssuer+" sourceAmount "+sourceAmount );
				NeoBankEnvironment.setComment(3, className, "destinationAsset "+destinationAsset+" destinationIssuer "+destinationIssuer+
						" destinationAmount "+destinationAmount+" paymentPaths "+paymentPaths.toString() );
				
				sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				if(sourceAcountId == null)
					throw new Exception("Customer has no wallet in stellar");
			 	KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(privateKey);

				NeoBankEnvironment.setComment(3, className," sourceAcountId " + sourceAcountId+ " sourceAccountKeyPair "
						+ ""+ sourceAccountKeyPair.getAccountId() + " privateKey "+ privateKey);

			 	if(!sourceAccountKeyPair.getAccountId().equals(sourceAcountId)) {
					Utilities.sendJsonResponse(response, "error", "Incorrect Secret key");
					return;
			 	}		
			 	String patnerPublicKey =(String)CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().getCurrencyPartnerStellarId(destinationAsset);
			 	NeoBankEnvironment.setComment(3, className," patnerPublicKey " + patnerPublicKey);
				String result =CurrencyTradeUtility.pathPaymentStrictSend(
						sourceAsset, sourceAssetIssuer, sourceAmount, destinationAsset,

						destinationIssuer, destinationAmount, privateKey, paymentPaths, patnerPublicKey);
				 splitResult= result.split(",");
				stellarTxnHash=splitResult[1];
				NeoBankEnvironment.setComment(3, className, "stellarTxnHash is "+stellarTxnHash);
				
				if(splitResult[0].equals("true")) {
					SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
					transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
					
					success= (boolean)CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().insertIntoCurrencyRemittanceQueue( relationshipNo,  payMode,  sourceAsset,  destinationAsset,
							 sourceAmount,  destinationAmount,  stellarTxnHash,  senderComment, receiverName, receiverBankName,
							 receiverBankCode, receiverAccountNo,  receiverEmail, transactionCode);
					if (success) {
						NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", payMode,"Currency remittance "+transactionCode );
						obj.addProperty("error","false"); 
						obj.addProperty("message", "You've successfuly exchanged "+ sourceAsset+ " to "+ destinationAsset+". We will inform you when your money arrives in receiver's bank.");
					}else {
						// internal error
						obj.addProperty("error","true"); 
						obj.addProperty("message", "Internal Error, Please Contact the Support Team");
					}
				}else {
					obj.addProperty("error","true"); 
					obj.addProperty("message", result); 
					NeoBankEnvironment.setComment(3, className,"========== Payment Failed on Stellar " + java.time.LocalTime.now() );
				}
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " +obj.toString());
					output = response.getWriter();
					output.print(obj);
				} finally {
					if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null;
					if (obj != null)obj = null;if (sourceAssetIssuer != null)sourceAssetIssuer = null;
					if (sourceAsset != null)sourceAsset = null; 
					if (patnerPublicKey != null)patnerPublicKey = null; 
					if (sourceAmount != null)sourceAmount = null; 
					if (destinationAsset != null)destinationAsset = null;
					if (destinationIssuer != null)destinationIssuer = null;
					if (destinationAmount != null)destinationAmount = null;
					if (privateKey != null)privateKey = null; if (user != null)user = null; 
					if (receiverName != null)receiverName = null; if (receiverEmail!=null)receiverEmail = null; 
					if (receiverAccountNo != null)receiverAccountNo = null; if (senderComment != null)senderComment = null; 
					if (stellarTxnHash != null)stellarTxnHash = null; if (payMode != null)payMode = null; 
					if (transactionCode!=null) transactionCode=null; if (splitResult!=null) splitResult=null;
					if (hasMnemonic != null)hasMnemonic = null;if (password != null)password = null; 
					if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; if (keyPair != null)keyPair = null;
				}								
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Exchange failed, Please try again letter");
			}
			
			break;
		case "exchange_digital_asset_to_currency_mbl":
			try {
				JsonObject obj = new JsonObject(); PrintWriter output = null;
				JsonObject offerObj = null; Gson gson = new Gson(); String tokenValue = null;
				String jsonString = null; User user = null;
				String privateKey = null; String sourceAcountId= null;		String sourceAsset = null;String sourceAssetIssuer = null;
				String sourceAmount = null;String destinationAsset = null;String destinationIssuer = null;String destinationAmount = null;
				JsonArray paymentPaths = null; String relationshipNo = null; String receiverName=null;String receiverEmail=null;
				String receiverBankName=null;	String receiverBankCode=null; String receiverAccountNo=null; String senderComment=null;
				boolean success=false;String stellarTxnHash=null; String payMode=null; String transactionCode=null; String [] splitResult=null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null; String authMethod = null;
				
				if(request.getParameter("json_string")!=null)	 jsonString = request.getParameter("json_string").trim();
				//if(request.getParameter("private_key")!=null)	 privateKey = request.getParameter("private_key").trim();
				if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
				if(request.getParameter("receiver_name")!=null)	 receiverName = request.getParameter("receiver_name").trim();
				if(request.getParameter("receiver_email")!=null)	 receiverEmail = request.getParameter("receiver_email").trim();
				if(request.getParameter("receiver_bank_name")!=null)	 receiverBankName = request.getParameter("receiver_bank_name").trim();
				if(request.getParameter("receiver_bank_code")!=null)	 receiverBankCode = request.getParameter("receiver_bank_code").trim();
				if(request.getParameter("receiver_account_no")!=null)	 receiverAccountNo = request.getParameter("receiver_account_no").trim();
				if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
				//NeoBankEnvironment.setComment(3, className, "Token is "+tokenValue);
				if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
					NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
					Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
					return;
				}
				
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				if(hasMnemonic.equals("true")) {
					if(request.getParameter("auth_method")!=null)	 authMethod = request.getParameter("auth_method").trim();
					if(authMethod.equals("P")) {
						if(request.getParameter("security")!=null)	 password = request.getParameter("security").trim();
						passIsCorrect = (boolean) CustomerDao.class.getConstructor().newInstance()
								.checkIfPasswordIsCorrect(relationshipNo, password);
						if(!passIsCorrect) {
							NeoBankEnvironment.setComment(1, className, "Password is not correct");
							Utilities.sendJsonResponse(response, "error", "Please enter the correct password");
							return;
						}
					}
					mnemoniStringFromDB = (String) CustomerDao.class.getConstructor().newInstance()
							.getmnemonicCode(relationshipNo);
					keyPair= Bip39Utility.masterKeyGeneration(mnemoniStringFromDB.replaceAll(",", " "));
					privateKey = String.valueOf(keyPair.getSecretSeed());
				}else {
					if(request.getParameter("security")!=null)	 privateKey = request.getParameter("security").trim();
				}
			
				payMode=NeoBankEnvironment.getCodeCustomerCurrencyRemittance();
				
				NeoBankEnvironment.setComment(3, className, "jsonString "+jsonString );
				if(jsonString==null||jsonString.isEmpty())
					throw new Exception("Json string is empty");
				offerObj = gson.fromJson(jsonString, JsonObject.class);
				sourceAsset = offerObj.get("sourceAsset").getAsString();
				sourceAssetIssuer = offerObj.get("sourceAssetIssuer").getAsString();
				sourceAmount = offerObj.get("sourceAmount").getAsString();
				destinationAsset = offerObj.get("destinationAsset").getAsString();
				destinationIssuer = offerObj.get("destinationIssuer").getAsString();
				destinationAmount = offerObj.get("destinationAmount").getAsString();
				paymentPaths = offerObj.get("paymentPaths").getAsJsonArray();
				
				NeoBankEnvironment.setComment(3, className, "sourceAsset "+sourceAsset+" sourceAssetIssuer "+sourceAssetIssuer+" sourceAmount "+sourceAmount );
				NeoBankEnvironment.setComment(3, className, "destinationAsset "+destinationAsset+" destinationIssuer "+destinationIssuer+
						" destinationAmount "+destinationAmount+" paymentPaths "+paymentPaths.toString() );
				
				sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				if(sourceAcountId == null)
					throw new Exception("Customer has no wallet in stellar");
				KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(privateKey);
				
				NeoBankEnvironment.setComment(3, className," sourceAcountId " + sourceAcountId+ " sourceAccountKeyPair "
						+ ""+ sourceAccountKeyPair.getAccountId() + " privateKey "+ privateKey);
				
				if(!sourceAccountKeyPair.getAccountId().equals(sourceAcountId)) {
					Utilities.sendJsonResponse(response, "error", "Incorrect Secret key");
					return;
				}			
			 	String patnerPublicKey =(String)CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().getCurrencyPartnerStellarId(destinationAsset);

				
				String result =CurrencyTradeUtility.pathPaymentStrictSend(
						sourceAsset, sourceAssetIssuer, sourceAmount, destinationAsset,

						destinationIssuer, destinationAmount, privateKey, paymentPaths, patnerPublicKey);
				splitResult= result.split(",");
				stellarTxnHash=splitResult[1];
				NeoBankEnvironment.setComment(3, className, "stellarTxnHash is "+stellarTxnHash);
				
				if(splitResult[0].equals("true")) {
					SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
					transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
					
					success= (boolean)CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().insertIntoCurrencyRemittanceQueue( relationshipNo,  payMode,  sourceAsset,  destinationAsset,
							 sourceAmount,  destinationAmount,  stellarTxnHash,  senderComment, receiverName, receiverBankName,
							 receiverBankCode, receiverAccountNo,  receiverEmail, transactionCode);
					if (success) {
						NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", payMode,"Currency remittance "+transactionCode );
						obj.addProperty("error","false"); 
						obj.addProperty("message", "You've successfuly exchanged "+ sourceAsset+ " to "+ destinationAsset+". We will inform you when your money arrives in your bank.");
					}else {
						// internal error
						obj.addProperty("error","true"); 
						obj.addProperty("message", "Internal Error, Please Contact the Support Team");
					}
				}else {
					obj.addProperty("error","true"); 
					obj.addProperty("message", result); 
					NeoBankEnvironment.setComment(3, className,"========== Payment Failed on Stellar " + java.time.LocalTime.now() );
				}
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " +obj.toString());
					output = response.getWriter();
					output.print(obj);
				} finally {
					if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null;
					if (obj != null)obj = null;if (sourceAssetIssuer != null)sourceAssetIssuer = null;
					if (sourceAsset != null)sourceAsset = null; if(authMethod!=null) authMethod= null;
					if (sourceAmount != null)sourceAmount = null; 
					if (destinationAsset != null)destinationAsset = null;
					if (destinationIssuer != null)destinationIssuer = null;
					if (destinationAmount != null)destinationAmount = null; 
					if (privateKey != null)privateKey = null; if (user != null)user = null; 
					if (receiverName != null)receiverName = null; if (receiverEmail!=null)receiverEmail = null; 
					if (receiverAccountNo != null)receiverAccountNo = null; if (senderComment != null)senderComment = null; 
					if (stellarTxnHash != null)stellarTxnHash = null; if (payMode != null)payMode = null; 
					if (transactionCode!=null) transactionCode=null; if (splitResult!=null) splitResult=null;
					if (hasMnemonic != null)hasMnemonic = null; 
					if (password != null)password = null; if (tokenValue != null)tokenValue = null;
					if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
					if (keyPair != null)keyPair = null;

				}								
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Exchange failed, Please try again letter");
			}
			
			break;
			
		case "get_offers_from_stellar":
			try {
				JsonObject obj = new JsonObject(); PrintWriter output = null;
				String sourceAssetCode = null;Gson gson = new Gson();
				String currencyCode = null;
				String amountToSpent = null;
				String relationshipNo= null;
				String sourceAssetIssuer= null;
				String sourceAssetType= null;
				List<Asset> issuersList = null;
				List<PaymentOffer> offers = null;
				User user = null;
				if(request.getParameter("source_coin")!=null)	 sourceAssetCode = request.getParameter("source_coin").trim();
				if(request.getParameter("sel_currency")!=null)	 currencyCode = request.getParameter("sel_currency").trim();
				if(request.getParameter("amount_to_spend")!=null)	 amountToSpent = request.getParameter("amount_to_spend").trim();
				if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
				HttpSession session = request.getSession(false);
				if (session.getAttribute("SESS_USER") == null) {
					Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
					return;
				}
				//Source Asset
				if(sourceAssetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="credit_alphanum12";
				}
				if(sourceAssetCode.equals(NeoBankEnvironment.getUSDCCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="credit_alphanum4";
				}
				if(sourceAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="credit_alphanum4";
				}
				if(sourceAssetCode.equals(NeoBankEnvironment.getXLMCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="native";
				}
				
				issuersList = CurrencyTradeUtility.getIssuersData(currencyCode);
				if(issuersList==null) 
					throw new Exception("Error in getting asset issuers");
				offers = CurrencyTradeUtility.getPathStrictSendWithDestinationAssets(issuersList, sourceAssetCode, 
						amountToSpent, sourceAssetType, sourceAssetIssuer);
				if(offers==null) 
					throw new Exception("Error in getting offers from the DEX");
				 offers = CurrencyTradeUtility.sortListByDestinationAmount(offers);
				 
				if(offers !=null) {
					obj.add("data", gson.toJsonTree(offers));
					obj.add("error", gson.toJsonTree("false"));
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
					if (obj != null)obj = null; if (sourceAssetCode != null)sourceAssetCode = null; if (currencyCode != null)currencyCode = null;
					if (offers != null)offers = null; if (amountToSpent != null)amountToSpent = null;if (sourceAssetIssuer != null)sourceAssetIssuer = null;
					if (user != null)user = null; 
					if (sourceAssetType != null)sourceAssetType = null; 
				}
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Getting offers failed, Please try again letter");
			}
			break;
		case "get_offers_from_stellar_mbl":
			try {
				JsonObject obj = new JsonObject(); PrintWriter output = null;
				String sourceAssetCode = null;Gson gson = new Gson();
				String currencyCode = null;String tokenValue = null;
				String amountToSpent = null;
				String relationshipNo= null;
				String sourceAssetIssuer= null;
				String sourceAssetType= null;
				List<Asset> issuersList = null;
				List<PaymentOffer> offers = null;
				User user = null;
				if(request.getParameter("source_coin")!=null)	 sourceAssetCode = request.getParameter("source_coin").trim();
				if(request.getParameter("sel_currency")!=null)	 currencyCode = request.getParameter("sel_currency").trim();
				if(request.getParameter("amount_to_spend")!=null)	 amountToSpent = request.getParameter("amount_to_spend").trim();
				if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
				if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
				//NeoBankEnvironment.setComment(3, className, "Token is "+tokenValue);
				if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
					NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
					Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
					return;
				}
				
				//Source Asset
				if(sourceAssetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="credit_alphanum12";
				}
				if(sourceAssetCode.equals(NeoBankEnvironment.getUSDCCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="credit_alphanum4";
				}
				if(sourceAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="credit_alphanum4";
				}
				if(sourceAssetCode.equals(NeoBankEnvironment.getXLMCode())) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					sourceAssetType  ="native";
				}
				
				issuersList = CurrencyTradeUtility.getIssuersData(currencyCode);
				if(issuersList==null) 
					throw new Exception("Error in getting asset issuers");
				offers = CurrencyTradeUtility.getPathStrictSendWithDestinationAssets(issuersList, sourceAssetCode, 
						amountToSpent, sourceAssetType, sourceAssetIssuer);
				if(offers==null) 
					throw new Exception("Error in getting offers from the DEX");
				 offers = CurrencyTradeUtility.sortListByDestinationAmount(offers);
				 
				if(offers !=null) {
					obj.add("data", gson.toJsonTree(offers));
					obj.add("error", gson.toJsonTree("false"));
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
					if (obj != null)obj = null; if (sourceAssetCode != null)sourceAssetCode = null; if (currencyCode != null)currencyCode = null;
					if (offers != null)offers = null; if (amountToSpent != null)amountToSpent = null;if (sourceAssetIssuer != null)sourceAssetIssuer = null;
					if (user != null)user = null;  if (tokenValue != null)tokenValue = null;
					if (sourceAssetType != null)sourceAssetType = null; 
				}
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Getting offers failed, Please try again letter");
			}
			break;
			
			case "check_trustline":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String assetCode = null;Gson gson = new Gson();
					String assetIssuer = null; 
					String relationshipNo = null;
					String publicKey = null;
					String radioValue = null;
					boolean success = false;
					boolean hasTrustLine = false;
					User user = null;
					if(request.getParameter("radio_value")!=null)	 radioValue = request.getParameter("radio_value").trim();
					NeoBankEnvironment.setComment(3, className, "Radio value "+radioValue);
					if(radioValue==null||radioValue.isEmpty()) 
						throw new Exception("No Data from frontend");
					assetCode = radioValue.split(",")[3];
					assetIssuer = radioValue.split(",")[4];
					NeoBankEnvironment.setComment(3, className, "Asset code is "+assetCode+" Asset issuer "+assetIssuer);
					HttpSession session = request.getSession(false);
					if (session.getAttribute("SESS_USER") == null) {
						Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
						return;
					}
					user = (User) session.getAttribute("SESS_USER");
					relationshipNo = user.getRelationshipNo();
					publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					if(publicKey== null || publicKey.isEmpty())
						throw new Exception("Stellar account not added");
					hasTrustLine = CurrencyTradeUtility.checkTrustLine(assetCode, assetIssuer, publicKey);
					success = true;
					if(success) {
						obj.add("has_trustline", gson.toJsonTree(hasTrustLine));
						obj.add("error", gson.toJsonTree("false"));
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
						if (obj != null)obj = null; if (assetCode != null)assetCode = null; if (assetIssuer != null)assetIssuer = null;
						if (publicKey != null)publicKey = null; 
						if (user != null)user = null; 
					}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Check trustline failed, Please try again letter");
				}
				
			break;
			case "check_trustline_mbl":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String assetCode = null;Gson gson = new Gson();
					String assetIssuer = null; 
					String relationshipNo = null;
					String publicKey = null;
					String radioValue = null;
					boolean success = false;
					boolean hasTrustLine = false;
					User user = null;
					if(request.getParameter("radio_value")!=null)	 radioValue = request.getParameter("radio_value").trim();
					if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
					NeoBankEnvironment.setComment(3, className, "Radio value "+radioValue);
					if(radioValue==null||radioValue.isEmpty()) 
						throw new Exception("No Data from frontend"); //TODO:- How do we dipslay this error on the frontend?? Ask Eric
					assetCode = radioValue.split(",")[3];
					assetIssuer = radioValue.split(",")[4];
					NeoBankEnvironment.setComment(3, className, "Asset code is "+assetCode+" Asset issuer "+assetIssuer);
					
					publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					if(publicKey== null || publicKey.isEmpty())
						throw new Exception("Stellar account not added");
					hasTrustLine = CurrencyTradeUtility.checkTrustLine(assetCode, assetIssuer, publicKey);
					success = true;
					if(success) {
						obj.add("has_trustline", gson.toJsonTree(hasTrustLine));
						obj.add("error", gson.toJsonTree("false"));
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
						if (obj != null)obj = null; if (assetCode != null)assetCode = null; if (assetIssuer != null)assetIssuer = null;
						if (publicKey != null)publicKey = null; 
						if (user != null)user = null; 
					}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Check trustline failed, Please try again letter");
				}
				
				break;
			case "create_trustline":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String issuerAccountId = null;
					String accountPrivateKey = null;
					String publicKey = null;
					String assetCode = null;
					String relationshipNo = null;
					User user = null;
					String limit = NeoBankEnvironment.getMaxStellarAssetWalletLimit();
					int baseFee = org.stellar.sdk.Transaction.MIN_BASE_FEE;
					Boolean createTrustline = false; String radioValue = null;
					Boolean success = false;
					if(request.getParameter("radio_value")!=null)	 radioValue = request.getParameter("radio_value").trim();
					if(request.getParameter("private_key")!=null)	 accountPrivateKey = request.getParameter("private_key").trim();
					NeoBankEnvironment.setComment(3, className, "Radio value "+radioValue);
					if(radioValue==null||radioValue.isEmpty()) 
						throw new Exception("No Data from frontend");
					assetCode = radioValue.split(",")[3];
					issuerAccountId = radioValue.split(",")[4];
					NeoBankEnvironment.setComment(3, className, "Asset code is "+assetCode+" Asset issuer "+issuerAccountId);
					
					HttpSession session = request.getSession(false);
					if (session.getAttribute("SESS_USER") == null) {
						Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
						return;
					}
					user = (User) session.getAttribute("SESS_USER");
					relationshipNo = user.getRelationshipNo();
					publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					NeoBankEnvironment.setComment(3, className, "PublicKey is "+publicKey);
					if(publicKey == null) {
						throw new Exception("In creating Trustline");
					}
					KeyPair source = KeyPair.fromSecretSeed(accountPrivateKey);
			        if(!source.getAccountId().equals(publicKey)) {
			        	Utilities.sendJsonResponse(response, "error", "Secret key is incorrect");
			        	return;
			       }
			        createTrustline = StellarSDKUtility.createTrustline(issuerAccountId, accountPrivateKey,
							 baseFee, limit,  assetCode );
					if(createTrustline) {
						success = (Boolean)CustomerWalletDao.class.getConstructor().newInstance().createWallet(
								relationshipNo, assetCode,publicKey, assetCode);
					}else {
						throw new Exception("Error in creating Trustline");
					}
					
					if(success == false) {
		        		SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", "CCT","Trustline created "+assetCode );
		        		obj.addProperty("error", "true"); 
		        		obj.addProperty("message", "Creating Trustline failed"); 
		        	}else {
		        		obj.addProperty("error", "false"); 
		        		obj.addProperty("message", "Trustline created suceessfully");
		        	}
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + obj.toString());
						output = response.getWriter();
						output.print(obj);
					}finally {
					
						if(output != null) output.flush();
						if(obj!=null) obj = null;if(limit!=null) limit = null;
						if(user!=null) user = null;
						if(relationshipNo!=null) relationshipNo = null;if(publicKey!=null) publicKey = null;
						if(issuerAccountId!=null) issuerAccountId = null;if(assetCode!=null) assetCode = null;
						if(accountPrivateKey!=null) accountPrivateKey = null;
					}
				     
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Create trustline failed, Please try again letter");
				}
			 break;
			case "create_trustline_mbl":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String issuerAccountId = null;
					String accountPrivateKey = null;
					String publicKey = null;
					String assetCode = null;
					String relationshipNo = null;
					User user = null;
					String limit = NeoBankEnvironment.getMaxStellarAssetWalletLimit();
					int baseFee = org.stellar.sdk.Transaction.MIN_BASE_FEE;
					boolean createTrustline = false; String radioValue = null;
					boolean success = false;
					if(request.getParameter("radio_value")!=null)	 radioValue = request.getParameter("radio_value").trim();
					if(request.getParameter("private_key")!=null)	 accountPrivateKey = request.getParameter("private_key").trim();
					if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
					NeoBankEnvironment.setComment(3, className, "Radio value "+radioValue);
					if(radioValue==null||radioValue.isEmpty()) 
						throw new Exception("No Data from frontend");
					assetCode = radioValue.split(",")[3];
					issuerAccountId = radioValue.split(",")[4];
					NeoBankEnvironment.setComment(3, className, "Asset code is "+assetCode+" Asset issuer "+issuerAccountId);
					
					publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					NeoBankEnvironment.setComment(3, className, "PublicKey is "+publicKey);
					if(publicKey == null) {
						throw new Exception("In creating Trustline");
					}
					KeyPair source = KeyPair.fromSecretSeed(accountPrivateKey);
					if(!source.getAccountId().equals(publicKey)) {
						Utilities.sendJsonResponse(response, "error", "Secret key is incorrect");
						return;
					}
					createTrustline = StellarSDKUtility.createTrustline(issuerAccountId, accountPrivateKey,
							baseFee, limit,  assetCode );
					if(createTrustline) {
						success = (boolean)CustomerWalletDao.class.getConstructor().newInstance().createWallet(
								relationshipNo, assetCode,publicKey, assetCode);
					}else {
						throw new Exception("Error in creating Trustline");
					}
					
					if(success == false) {
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", "CCT","Trustline created "+assetCode );
						obj.addProperty("error", "true"); 
						obj.addProperty("message", "Creating Trustline failed"); 
					}else {
						obj.addProperty("error", "false"); 
						obj.addProperty("message", "Trustline created suceessfully");
					}
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + obj.toString());
						output = response.getWriter();
						output.print(obj);
					}finally {
						
						if(output != null) output.flush();
						if(obj!=null) obj = null;if(limit!=null) limit = null;
						if(user!=null) user = null;
						if(relationshipNo!=null) relationshipNo = null;if(publicKey!=null) publicKey = null;
						if(issuerAccountId!=null) issuerAccountId = null;if(assetCode!=null) assetCode = null;
						if(accountPrivateKey!=null) accountPrivateKey = null;
					}
					
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Create trustline failed, Please try again letter");
				}
				break;
				
			case "cust_fiat_remmittance":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String sourceCurrencyCode = null; String destinationCurrencyCode = null;
					String sourceAmount = null; boolean success = false;
					String sourceExpectedAmount = null; String receiverName = null;
					String receiverEmail = null; String receiverBankName = null;
					String receiverBankCode = null; String receiverBankAccountNo = null;
					String walletBalance = null;String txnUserCode = null;String walletDetails =null;String walletId=null;
					String payComments="";String referenceNo="";String userType="C";
					String customerChargesValue=null; String customerCharges=null; String minimumTxnAmount=null;
					String authStatus=""; String authMessage=""; String authResponse=null;
					boolean recordAuthorization=false; String currencyId = null; String txnMode=null; String transactionCode=null;
					User user = null; String relationshipNo = null; String txnType = NeoBankEnvironment.getFiatRemittanceTxnCode();
					
					if(request.getParameter("sel_fiat_currency")!=null)	sourceCurrencyCode = request.getParameter("sel_fiat_currency").trim();
					if(request.getParameter("sel_digital_currency")!=null)	destinationCurrencyCode = request.getParameter("sel_digital_currency").trim();
					if(request.getParameter("source_amount")!=null)	sourceAmount = request.getParameter("source_amount").trim();
					if(request.getParameter("expected_amount")!=null)	sourceExpectedAmount = request.getParameter("expected_amount").trim();
					if(request.getParameter("receiver_name")!=null)	receiverName = request.getParameter("receiver_name").trim();
					if(request.getParameter("receiver_email")!=null)	receiverEmail = request.getParameter("receiver_email").trim();
					if(request.getParameter("receiver_bank_name")!=null)	receiverBankName = request.getParameter("receiver_bank_name").trim();
					if(request.getParameter("receiver_bank_code")!=null)	receiverBankCode = request.getParameter("receiver_bank_code").trim();
					if(request.getParameter("receiver_account_no")!=null)	receiverBankAccountNo = request.getParameter("receiver_account_no").trim();
					
					HttpSession session = request.getSession(false);
					if (session.getAttribute("SESS_USER") == null) {
						Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
						return;
					}
					user = (User) session.getAttribute("SESS_USER");
					relationshipNo = user.getRelationshipNo();
					walletDetails = (String)CustomerWalletDao.class.getConstructor().newInstance().getFiatWalletBalance(relationshipNo);
					//
					txnMode="D";
					currencyId = sourceCurrencyCode;
					txnUserCode = Utilities.generateTransactionCode(10);
					transactionCode=(new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
							+ Utilities.genAlphaNumRandom(9);
					referenceNo = NeoBankEnvironment.getCodeFiatWalletP2P()+ "-" +transactionCode;
					customerCharges = (String) SystemUtilsDao.class.getConstructor().newInstance().getChargesApplicable(
							NeoBankEnvironment.getDefaultCustomerUserType(),txnType, sourceAmount);
					 minimumTxnAmount=customerCharges.substring(customerCharges.indexOf("|")+1, customerCharges.length());
					
					// Check Wallet Balance

					customerChargesValue = customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|"));
					// Check Wallet Balance
					 NeoBankEnvironment.setComment(3,className,"customerChargesValue is "+customerChargesValue);
					 
					if ((Double.parseDouble(sourceAmount)+Double.parseDouble(customerChargesValue)) >Double.parseDouble(walletBalance)) {
						Utilities.sendJsonResponse(response, "error", "You do not have enough balance to complete this transaction. Please topup to proceed");
						return;
					}
					if ( Double.parseDouble(sourceAmount)< Double.parseDouble(minimumTxnAmount)) { 
						Utilities.sendJsonResponse(response, "error", "Transaction amount can not be less than "+minimumTxnAmount);
						return;
					}
					
					/****** Wallet Authorization******/
					authResponse=(String)WalletAuthorizationDao.class.getConstructor().newInstance().performWalletAuthorization(relationshipNo, sourceAmount, txnMode);
					if (authResponse.isEmpty()) {
						Utilities.sendJsonResponse(response, "authfailed", "Error in authorization, please try again later");
						return;
					}
					authStatus=authResponse.substring(0,authResponse.indexOf(","));
					authMessage=authResponse.substring(authResponse.indexOf(",")+1,authResponse.indexOf("|"));
					walletId=authResponse.substring(authResponse.indexOf("|")+1,authResponse.length());
			
					if (authStatus.equals("S")==false) {
						//Authorization failed
						// Record failed authorization
						recordAuthorization=(boolean)WalletAuthorizationDao.class.getConstructor().newInstance().recordWalletAuthorizationResult(relationshipNo, sourceAmount, txnMode, currencyId, 
								userType, referenceNo, walletId, authStatus, authMessage);
						if(!recordAuthorization) { 
							 Utilities.sendJsonResponse(response, "authfailed", "Error in authorization, please try again");
						return;
						}
						     Utilities.sendJsonResponse(response, "authfailed", authMessage);
						return;
					}else {
						// Record successful authorization
						recordAuthorization=(boolean)WalletAuthorizationDao.class.getConstructor().newInstance().recordWalletAuthorizationResult(relationshipNo, sourceAmount, txnMode, currencyId, 
								userType, referenceNo, walletId, authStatus, authMessage);
						if(!recordAuthorization) { 
							 Utilities.sendJsonResponse(response, "authfailed", "Error in authorization, please try again");
						return;
						}
					}
					
					/****** End of Wallet Authorization******/	
//					//Debit customer Credit Business
					success = (boolean) CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().fiatCurrencyRemittance(
							relationshipNo, walletId,  sourceAmount, referenceNo, txnUserCode, customerCharges,transactionCode,txnType);
					String destinationAmount = "0"; //To be updated by the TDA 
					String stellarTxnHash = "";//To be updated by the TDA 
					//Insert in FiatRemmitance Queue
					if(!success)
						success = (boolean) CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().insertIntoCurrencyFiatRemittanceQueue
								( relationshipNo,  txnType,  sourceCurrencyCode,  destinationCurrencyCode,
										 sourceAmount,  destinationAmount,  stellarTxnHash, "", receiverName, receiverBankName,
										 receiverBankCode,  receiverBankAccountNo,  receiverEmail, transactionCode,  referenceNo,
										 txnUserCode);
					if(!success)
						throw new Exception("Failed to insert into Fiat Remittance Queue");
					if (success) {
						//TODO Check if TDA has enough XLM 
						//Send Email if the TDA does not have enough XLM
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, userType, txnType, " Fiat Remmitance " );
						obj.addProperty("error", ("false"));
						obj.addProperty("message", ("You have transfered "+sourceCurrencyCode+":" + Utilities.getMoneyinDecimalFormat(sourceAmount) + 
								" This transcation is being processed we will inform you once the transaction is done ")); 
					}else {
						obj.addProperty("error", ("true")); 
						obj.addProperty("message", ("Transaction failed")); 
					}
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + (obj.toString()));
						output = response.getWriter();
						output.print((obj));
					} finally {
						if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; 
						if (obj != null)obj = null; if (receiverEmail != null)receiverEmail = null;
						if (sourceCurrencyCode != null)sourceCurrencyCode = null; if (destinationCurrencyCode != null)destinationCurrencyCode = null; 
						if (sourceAmount != null)sourceAmount = null; if (sourceExpectedAmount != null)sourceExpectedAmount = null; 
						if (receiverBankName != null)receiverBankName = null; if (receiverBankCode != null)receiverBankCode = null; 
						if (receiverBankAccountNo != null)receiverBankAccountNo = null; 
						if (user != null)user = null; 
						if (walletBalance != null)walletBalance = null; 
						if (walletDetails != null)walletDetails = null; if(userType!=null) userType=null;
						if (walletId != null)walletId = null; 
						if (payComments != null)payComments = null; 
						if (referenceNo != null)referenceNo = null; 
						if (customerChargesValue != null)customerChargesValue = null; if (customerCharges != null)customerCharges = null; 
						if (minimumTxnAmount != null)minimumTxnAmount = null;
						if (currencyId!=null); currencyId=null;  if (authResponse != null)authResponse = null;
						if (transactionCode!=null); transactionCode=null;    if (txnMode!=null); txnMode=null; 
					}								
					
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Transaction failed, Please try again letter");
				}
				break;
			case "get_expected_amount_fiat_remittance":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String sourceAmount = null; String sourceAssetCode = null;
					String destinationAssetcode = null;  List<Asset> issuersList = null;
					List<PaymentOffer> offers = null;
					
					if(request.getParameter("source_amount")!=null)	sourceAmount = request.getParameter("source_amount").trim();
					if(request.getParameter("sel_fiat_currency")!=null)	sourceAssetCode = request.getParameter("sel_fiat_currency").trim();
					if(request.getParameter("sel_digital_currency")!=null)	destinationAssetcode = request.getParameter("sel_digital_currency").trim();
					
					NeoBankEnvironment.setComment(3, className,"sourceAssetCode "+sourceAssetCode +" destinationAssetcode "+ destinationAssetcode+" sourceAmount "+sourceAmount);
					
					double xlmAmountDouble = (double) CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().getXLMConversionRate(sourceAmount);
		
					issuersList = CurrencyTradeUtility.getIssuersData(destinationAssetcode);
					
					if(issuersList==null) 
						throw new Exception("Error in getting asset issuers");
				
					 DecimalFormat df_obj = new DecimalFormat("#.######");
					 
					
					offers = CurrencyTradeUtility.getPathStrictSendWithNativeAsset(issuersList,  df_obj.format(xlmAmountDouble));
					if(offers==null) 
						throw new Exception("Error in getting offers from the DEX");
					 offers = CurrencyTradeUtility.sortListByDestinationAmount(offers);
					 if(offers==null) 
							throw new Exception("Error in getting Sortting assets");
					 double expectedAmount = offers.get(0).getDestinationAmount();
					 
					obj.addProperty("error", "false"); 
					obj.addProperty("expectedAmount", expectedAmount); 
					obj.addProperty("message", "Success"); 
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + obj.toString());
						output = response.getWriter();
						output.print(obj);
					}finally {
						if(output != null) output.flush();
						if(obj!=null) obj = null;if(sourceAmount!=null) sourceAmount = null;
						if(sourceAssetCode!=null) sourceAssetCode = null;
						if(destinationAssetcode!=null) destinationAssetcode = null;if(issuersList!=null) issuersList = null;
						if(offers!=null) offers = null;
					}
				 
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in Getting Expected amount, Please try again letter");
				}
			break;
			
		}
		
		
	}

	@Override
	public void performOperation(String ruleaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx)
			throws Exception {

	}

}
