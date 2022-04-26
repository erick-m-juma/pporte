package com.pporte.rules;

import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.stellar.sdk.KeyPair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDao;
import com.pporte.dao.CustomerDigitalAssetsDao;
import com.pporte.dao.CustomerDigitalCurrenciesDao;
import com.pporte.dao.CustomerPorteCoinDao;
import com.pporte.dao.CustomerWalletDao;
import com.pporte.dao.SystemUtilsDao;
import com.pporte.dao.WalletAuthorizationDao;
import com.pporte.model.AssetAccount;
import com.pporte.model.ClaimableBalance;
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

public class CustomerDigitalAssetsRulesImpl implements Rules {
	private static String className = CustomerDigitalAssetsRulesImpl.class.getSimpleName();
	@Override
	public void performJSONOperation(String arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			ServletContext arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session = request.getSession(false);
		switch (rulesaction) {
		case "cust_create_claimable_balance":
			try {
				JsonObject obj = new JsonObject(); User user = null;  String relationshipNo= null; PrintWriter output = null;
				Gson gson = new Gson(); String amount= null; Boolean success = false;
				String walletBalance = null; 	String txnUserCode = null; String walletDetails =null; 
				String senderWalletId=null; String payComments=""; 	String referenceNo=""; 
				String receiverWalletId = null; String assetCodeSender = null;
				String assetCodeReceiver = null; String txnPayMode = null; String extSystemRef = null; String userId = null;
				String senderKey = null; KeyPair destinationAccount = null; KeyPair sourceAccount =null; 
				String sourcewalletIdInternal = "";	String destinationwalletIdInternal = "";
				String destinationAssetBalance = null; String sourceAssetBalance = null; boolean proceed = true;
				String sourceAssetIssuer = null; String sourceAcountId = null; String results = null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null;
				
				if(request.getParameter("sendamount_cb")!=null) amount = request.getParameter("sendamount_cb").trim();
				if(request.getParameter("sender_asset_cb")!=null)	 assetCodeSender = request.getParameter("sender_asset_cb").trim();
				if(request.getParameter("narrative_cb")!=null)	 payComments = request.getParameter("narrative_cb").trim();
				if(request.getParameter("input_receiver_cb")!=null)	 receiverWalletId = request.getParameter("input_receiver_cb").trim(); //Public Key
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				if (session.getAttribute("SESS_USER") == null) 
					throw new Exception ("Session has expired, please log in again");
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
					senderKey = String.valueOf(keyPair.getSecretSeed());
				}else {
					if(request.getParameter("security")!=null)	 senderKey = request.getParameter("security").trim();
				}
				
			
				
				NeoBankEnvironment.setComment(3, className," inside cust_create_claimable_balance: " + " amount "+ amount + " assetCodeSender "+ assetCodeSender
						+" assetCodeReceiver "+ assetCodeReceiver + " relationshipNo "+ relationshipNo + " userId "+ userId );
				
				/**Check if accounts exists*/
				
				try {
					 sourceAccount = KeyPair.fromSecretSeed(senderKey);
				}catch (Exception e) {
					Utilities.sendJsonResponse(response, "error", "Key provided is invalid");
					return;
				}
				
				try {
			 		 destinationAccount = KeyPair.fromAccountId(receiverWalletId);
				}catch (Exception e) {
					Utilities.sendJsonResponse(response, "error", "Receiver wallet is invalid");
					return;
				}
				sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				if(sourceAcountId == null)
					throw new Exception("Customer has no wallet in stellar");
				
				if(assetCodeSender.equals(NeoBankEnvironment.getPorteTokenCode())) 
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(assetCodeSender);
				if(assetCodeSender.equals(NeoBankEnvironment.getUSDCCode())) 
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(assetCodeSender);
				if(assetCodeSender.equals(NeoBankEnvironment.getVesselCoinCode())) 
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(assetCodeSender);
					
				
				KeyPair source = KeyPair.fromSecretSeed(senderKey);
				NeoBankEnvironment.setComment(3, className, "senderKey  "+ senderKey+" sourceAssetIssuer "+
						sourceAssetIssuer+"  account from pvt "+ source.getAccountId());
				if(!source.getAccountId().equals(sourceAcountId)) { 
					NeoBankEnvironment.setComment(3, className, "Secret Key is incorrect ");
					Utilities.sendJsonResponse(response, "error", "Secret Key is incorrect ");
					return;
				}
								
				/**Check from the Network if exist*/
				if(StellarSDKUtility.CheckAccountIfExist(sourceAccount)==false) {
					Utilities.sendJsonResponse(response, "error", "Source account does not exist");
					return;
				}
				if(StellarSDKUtility.CheckAccountIfExist(destinationAccount)==false) {
					Utilities.sendJsonResponse(response, "error", "Destination account does not exist");
					return;
				}
				
				if((sourceAccount.getAccountId()).equals(destinationAccount.getAccountId())) {
					Utilities.sendJsonResponse(response, "error", "You cannot send Asset from your wallet to the same wallet account");
					return;
				}
				
				//Before posting payment first check the FIAT wallet if it has some amount to cater for the internal transaction charge.
				
				/**Post payments on stellar*/
				NeoBankEnvironment.setComment(3, className,"========== getting into Stellar " + java.time.LocalTime.now() );

				
				results =  CurrencyTradeUtility.createClaimableBalance(
						senderKey, receiverWalletId, amount, assetCodeSender,sourceAssetIssuer, payComments);	
				
			 	
				if(results.equals("success")) {
					NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
				}else {
					NeoBankEnvironment.setComment(3, className,"========== Payment failed on Stellar " + java.time.LocalTime.now() );
					Utilities.sendJsonResponse(response, "error", results);
					return;
				}
				
				sourcewalletIdInternal = (String)CustomerPorteCoinDao.class.getConstructor().newInstance().getAssetWalletDetails(relationshipNo, assetCodeSender, sourceAccount.getAccountId());
				if(sourcewalletIdInternal ==null) {
					NeoBankEnvironment.setComment(3, className,"========== destination not present in porte portfolio  accountId "+ destinationAccount.getAccountId() );

				    //Utilities.sendJsonResponse(response, "error", "Payment successful");
				    proceed = false;
				}
				
				if(assetCodeSender.equals(NeoBankEnvironment.getPorteTokenCode())) {
					txnPayMode= NeoBankEnvironment.getCodePorteUtilityCoinP2P();
				}else if(assetCodeSender.equals(NeoBankEnvironment.getVesselCoinCode())) {
					txnPayMode= NeoBankEnvironment.getCodeVesselCoinP2P();
				}else if(assetCodeSender.equals(NeoBankEnvironment.getXLMCode())) {
					txnPayMode= NeoBankEnvironment.getCodeXLMCoinP2P();
				}else if(assetCodeSender.equals(NeoBankEnvironment.getUSDCCode())) {
					txnPayMode= NeoBankEnvironment.getCodeUSDCCoinP2P();
				}else {
					Utilities.sendJsonResponse(response, "error", "Try again later");
					NeoBankEnvironment.setComment(3, className," Unknown txnPayMode " +txnPayMode );
					return;
				}
				
				txnUserCode = Utilities.generateTransactionCode(10);
				referenceNo = txnPayMode+ "-" + (new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
						+ Utilities.genAlphaNumRandom(9);
				
				if(proceed) {  //Proceed to update Internal ledger
					String customerCharges = (String) SystemUtilsDao.class.getConstructor().newInstance().getChargesApplicable(
							NeoBankEnvironment.getDefaultCustomerUserType(),txnPayMode, amount);
					String minimumTxnAmount=customerCharges.substring(customerCharges.indexOf("|")+1, customerCharges.length());
					
					if ( Double.parseDouble(amount)< Double.parseDouble(minimumTxnAmount)) { 
						Utilities.sendJsonResponse(response, "error", "Transaction amount can not be less than "+minimumTxnAmount);
						return;
					}
					
					//Connect to External API here
					extSystemRef=Utilities.genAlphaNumRandom(24).toUpperCase(); //Remove this after integration
					
					success = (Boolean) CustomerPorteCoinDao.class.getConstructor().newInstance().porteCoinP2P(relationshipNo, sourcewalletIdInternal,  amount, payComments, 
							referenceNo, txnUserCode, customerCharges, txnPayMode, assetCodeSender, extSystemRef, destinationwalletIdInternal, sourceAssetBalance, destinationAssetBalance);
					if (success) {
						String moduleCode = txnPayMode;
						///SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Porte Asset "+assetCodeSender+"  P2P" + referenceNo , 0, 48));
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, "C",
								moduleCode," Porte Asset "+assetCodeSender+" "+ txnPayMode);
						obj.add("error", gson.toJsonTree("false"));
						//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
						obj.add("message", gson.toJsonTree("You have Successfully Created Claimable Balance of: "+ amount +  " "+assetCodeSender )); 

					}else {
						obj.add("error", gson.toJsonTree("true")); 
						obj.add("message", gson.toJsonTree("Transaction failed")); 
					}
					
					
				}else {
					//This account is not managed by Porte portfolio
					obj.add("error", gson.toJsonTree("false"));
					//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
					obj.add("message", gson.toJsonTree("Payment success "+ amount +  " "+assetCodeSender )); 
				}
						
			try {
				NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
				output = response.getWriter();
				output.print(gson.toJson(obj));
				NeoBankEnvironment.setComment(3, className,"========== end transfer_porte_coin_mbl " + java.time.LocalTime.now() );

			} finally {
				NeoBankEnvironment.setComment(3, className,"========== start cleaning transfer_porte_coin_mbl " + java.time.LocalTime.now() );

				if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
				if (obj != null)obj = null; 
				if (amount != null)amount = null;  if (assetCodeReceiver != null)assetCodeReceiver = null;
				if (extSystemRef != null)extSystemRef = null; 
				if (user != null)user = null; 
				if (walletBalance != null)walletBalance = null; 
				if (walletDetails != null)walletDetails = null; 
				if (senderWalletId != null)senderWalletId = null; 
				if (payComments != null)payComments = null; 
				if (hasMnemonic != null)hasMnemonic = null; 
				if (password != null)password = null; 
				if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
				if (keyPair != null)keyPair = null; 
				if (senderKey != null)senderKey = null; 
				if (txnUserCode != null)txnUserCode = null; 
				if (sourcewalletIdInternal != null)sourcewalletIdInternal = null; 
				if (referenceNo != null)referenceNo = null; 
				if (receiverWalletId != null) receiverWalletId = null; 
				if (assetCodeSender != null)assetCodeSender = null; if (txnPayMode != null)txnPayMode = null;
				NeoBankEnvironment.setComment(3, className,"========== after cleaning transfer_porte_coin_mbl " + java.time.LocalTime.now() );

			}								
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in transfer coin, Please try again letter");
			}
		break;
		case "claim_claimable_balance":
			try {
				JsonObject obj = new JsonObject(); PrintWriter output = null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null; String privateKey = null;
				String claimableBalaceId = null; String assetCode = null; String assetIssuer = null;
				User user = null; String relationshipNo = null; boolean hasTrustLine = false; 
				String result = null; String txnHash = null; String status = null;
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				if(request.getParameter("claimableBalaceId")!=null)	 claimableBalaceId = request.getParameter("claimableBalaceId").trim();
				if(request.getParameter("assetCode")!=null)	 assetCode = request.getParameter("assetCode").trim();
				if(request.getParameter("assetIssuer")!=null)	 assetIssuer = request.getParameter("assetIssuer").trim();
				
				//HttpSession session = request.getSession(false);
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
					if(request.getParameter("security")!=null)	 privateKey = request.getParameter("security").trim();
				}
				
				KeyPair sourceAccountKeyPair = null;
				String sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				if(sourceAcountId == null)
					throw new Exception("Customer has no wallet in stellar");
			 	sourceAccountKeyPair = KeyPair.fromSecretSeed(privateKey);

				NeoBankEnvironment.setComment(3, className," sourceAcountId " + sourceAcountId+ " sourceAccountKeyPair "
						+ ""+ sourceAccountKeyPair.getAccountId() + " privateKey "+ privateKey);

			 	if(!sourceAccountKeyPair.getAccountId().equals(sourceAcountId)) {
			 		Utilities.sendJsonResponse(response, "true", "Incorrect Secret key");
					return;
			 	}	
			 	
			 	if(assetCode.equals(NeoBankEnvironment.getXLMCode())) {
					hasTrustLine = true;
				}else {
					hasTrustLine = CurrencyTradeUtility.checkTrustLine(assetCode, assetIssuer, sourceAcountId);
				}
				
				if(!hasTrustLine) {
					StellarSDKUtility.createTrustline(assetIssuer, privateKey, org.stellar.sdk.Transaction.MIN_BASE_FEE,
							NeoBankEnvironment.getMaxStellarAssetWalletLimit(),  assetCode );
					CustomerWalletDao.class.getConstructor().newInstance().createWallet(
							relationshipNo, assetCode,sourceAcountId, assetCode);
				}
				result = CurrencyTradeUtility.claimClaimableBalance(claimableBalaceId, privateKey);
				NeoBankEnvironment.setComment(3, className," result "+result);
				if(result == null)
					throw new Exception ("Error in Claiming Claimable Balance");
				status = result.split(",")[0];
				txnHash = result.split(",")[1];
				NeoBankEnvironment.setComment(3, className," status "+status);
				if(status.trim().equalsIgnoreCase("true") ) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", "CCB","Claimed Claimable Balance transaction hash: "+txnHash );
					obj.addProperty("error", "false"); 
					obj.addProperty("message", "Balance Claimed Suceessfully"); 
				}else {
					obj.addProperty("error", "true"); 
					obj.addProperty("message", "Error in Claiming Balance"); 
				}
				
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + obj.toString());
					output = response.getWriter();
					output.print(obj);
				} finally {
					if (hasMnemonic != null)hasMnemonic = null; 
					if (password != null)password = null; 
					if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
					if (keyPair != null)keyPair = null;
					if(output != null) output.flush();
					if(obj!=null) obj = null;
					if(user!=null) user = null;if(sourceAccountKeyPair!=null) sourceAccountKeyPair = null;
					if(relationshipNo!=null) relationshipNo = null;if(sourceAcountId!=null) sourceAcountId = null;
					if(privateKey!=null) privateKey = null;if(claimableBalaceId!=null) claimableBalaceId = null;
					if(assetCode!=null) assetCode = null;if(assetIssuer!=null) assetIssuer = null;
					if(result!=null) result = null;if(txnHash!=null) txnHash = null;if(status!=null) status = null;
				}

			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in Claiming Balance, Please try again letter");
			}
			
			break;
			
		case "cust_create_claimable_balance_mbl":
			try {
				JsonObject obj = new JsonObject(); User user = null;  String relationshipNo= null; PrintWriter output = null;
				Gson gson = new Gson(); String amount= null; Boolean success = false;
				String walletBalance = null; 	String txnUserCode = null; String walletDetails =null; 
				String senderWalletId=null; String payComments=""; 	String referenceNo=""; 
				String receiverWalletId = null; String assetCodeSender = null;
				String assetCodeReceiver = null; String txnPayMode = null; String extSystemRef = null; String userId = null;
				String senderKey = null; KeyPair destinationAccount = null; KeyPair sourceAccount =null; 
				String sourcewalletIdInternal = "";	String destinationwalletIdInternal = "";
				String destinationAssetBalance = null; String sourceAssetBalance = null; boolean proceed = true;
				String sourceAssetIssuer = null; String sourceAcountId = null; String results = null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null; String tokenValue = null; String authMethod = null;
				
				if(request.getParameter("transfer_amount_cb")!=null) amount = request.getParameter("transfer_amount_cb").trim();
				if(request.getParameter("receiver_asset_cb")!=null)	 assetCodeSender = request.getParameter("receiver_asset_cb").trim();
				if(request.getParameter("narrative_cb")!=null)	 payComments = request.getParameter("narrative_cb").trim();
				if(request.getParameter("receiverwallet_cb")!=null)	 receiverWalletId = request.getParameter("receiverwallet_cb").trim(); //Public Key
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
				if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
				//NeoBankEnvironment.setComment(3, className, "Token is "+tokenValue);
				if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
					NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
					Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
					return;
				}
				
				if(hasMnemonic.equals("true")) {
					if(request.getParameter("auth_method")!=null)	 authMethod = request.getParameter("auth_method").trim();
					NeoBankEnvironment.setComment(3, className, "authMethod is "+authMethod);
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
					senderKey = String.valueOf(keyPair.getSecretSeed());
				}else {
					if(request.getParameter("security")!=null)	 senderKey = request.getParameter("security").trim();
				}
				
			
				
				NeoBankEnvironment.setComment(3, className," inside cust_create_claimable_balance: " + " amount "+ amount + " assetCodeSender "+ assetCodeSender
						+" assetCodeReceiver "+ assetCodeReceiver + " relationshipNo "+ relationshipNo + " userId "+ userId );
				
				/**Check if accounts exists*/
				
				try {
					 sourceAccount = KeyPair.fromSecretSeed(senderKey);
				}catch (Exception e) {
					Utilities.sendJsonResponse(response, "error", "Key provided is invalid");
					return;
				}
				
				try {
			 		 destinationAccount = KeyPair.fromAccountId(receiverWalletId);
				}catch (Exception e) {
					Utilities.sendJsonResponse(response, "error", "Receiver wallet is invalid");
					return;
				}
				sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				if(sourceAcountId == null)
					throw new Exception("Customer has no wallet in stellar");
				
				if(assetCodeSender.equals(NeoBankEnvironment.getPorteTokenCode())) 
					sourceAssetIssuer =(String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(assetCodeSender);
				if(assetCodeSender.equals(NeoBankEnvironment.getUSDCCode())) 
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(assetCodeSender);
				if(assetCodeSender.equals(NeoBankEnvironment.getVesselCoinCode())) 
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(assetCodeSender);
					
				KeyPair source = KeyPair.fromSecretSeed(senderKey);
				NeoBankEnvironment.setComment(3, className, "senderKey  "+ senderKey+" sourceAssetIssuer "+
						sourceAssetIssuer+"  account from pvt "+ source.getAccountId());
				if(!source.getAccountId().equals(sourceAcountId)) { 
					NeoBankEnvironment.setComment(3, className, "Secret Key is incorrect ");
					Utilities.sendJsonResponse(response, "error", "Secret Key is incorrect ");
					return;
				}
								
				/**Check from the Network if exist*/
				//boolean exist = StellarSDKUtility.CheckAccountIfExist(sourceAccount);
				if(StellarSDKUtility.CheckAccountIfExist(sourceAccount)==false) {
					Utilities.sendJsonResponse(response, "error", "Source account does not exist");
					return;
				}
				if(StellarSDKUtility.CheckAccountIfExist(destinationAccount)==false) {
					Utilities.sendJsonResponse(response, "error", "Destination account does not exist");
					return;
				}
				
				if((sourceAccount.getAccountId()).equals(destinationAccount.getAccountId())) {
					Utilities.sendJsonResponse(response, "error", "You cannot send Asset from your wallet to the same wallet account");
					return;
				}
				
				//Before posting payment first check the FIAT wallet if it has some amount to cater for the internal transaction charge.
				
				/**Post payments on stellar*/
				NeoBankEnvironment.setComment(3, className,"========== getting into Stellar " + java.time.LocalTime.now() );

				
				results =  CurrencyTradeUtility.createClaimableBalance(
						senderKey, receiverWalletId, amount, assetCodeSender,sourceAssetIssuer, payComments);	
				
			 	
				if(results.equals("success")) {
					NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
				}else {
					NeoBankEnvironment.setComment(3, className,"========== Payment failed on Stellar " + java.time.LocalTime.now() );
					Utilities.sendJsonResponse(response, "error", results);
					return;
				}
				
				sourcewalletIdInternal = (String)CustomerPorteCoinDao.class.getConstructor().newInstance().getAssetWalletDetails(relationshipNo, assetCodeSender, sourceAccount.getAccountId());
				if(sourcewalletIdInternal ==null) {
					NeoBankEnvironment.setComment(3, className,"========== destination not present in porte portfolio  accountId "+ destinationAccount.getAccountId() );

				    //Utilities.sendJsonResponse(response, "error", "Payment successful");
				    proceed = false;
				}
				
				if(assetCodeSender.equals(NeoBankEnvironment.getPorteTokenCode())) {
					txnPayMode= NeoBankEnvironment.getCodePorteUtilityCoinP2P();
				}else if(assetCodeSender.equals(NeoBankEnvironment.getVesselCoinCode())) {
					txnPayMode= NeoBankEnvironment.getCodeVesselCoinP2P();
				}else if(assetCodeSender.equals(NeoBankEnvironment.getXLMCode())) {
					txnPayMode= NeoBankEnvironment.getCodeXLMCoinP2P();
				}else if(assetCodeSender.equals(NeoBankEnvironment.getUSDCCode())) {
					txnPayMode= NeoBankEnvironment.getCodeUSDCCoinP2P();
				}else {
					Utilities.sendJsonResponse(response, "error", "Try again later");
					NeoBankEnvironment.setComment(3, className," Unknown txnPayMode " +txnPayMode );
					return;
				}
				
				txnUserCode = Utilities.generateTransactionCode(10);
				referenceNo = txnPayMode+ "-" + (new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
						+ Utilities.genAlphaNumRandom(9);
				
				if(proceed) {  //Proceed to update Internal ledger
					String customerCharges = (String) SystemUtilsDao.class.getConstructor().newInstance().getChargesApplicable(
							NeoBankEnvironment.getDefaultCustomerUserType(),txnPayMode, amount);
					String minimumTxnAmount=customerCharges.substring(customerCharges.indexOf("|")+1, customerCharges.length());
					
					if ( Double.parseDouble(amount)< Double.parseDouble(minimumTxnAmount)) { 
						Utilities.sendJsonResponse(response, "error", "Transaction amount can not be less than "+minimumTxnAmount);
						return;
					}
					
					//Connect to External API here
					extSystemRef=Utilities.genAlphaNumRandom(24).toUpperCase(); //Remove this after integration
					
					success = (Boolean) CustomerPorteCoinDao.class.getConstructor().newInstance().porteCoinP2P(relationshipNo, sourcewalletIdInternal,  amount, payComments, 
							referenceNo, txnUserCode, customerCharges, txnPayMode, assetCodeSender, extSystemRef, destinationwalletIdInternal, sourceAssetBalance, destinationAssetBalance);
					if (success) {
						String moduleCode = txnPayMode;
						///SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Porte Asset "+assetCodeSender+"  P2P" + referenceNo , 0, 48));
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, "C",
								moduleCode," Porte Asset "+assetCodeSender+" "+ txnPayMode);
						obj.add("error", gson.toJsonTree("false"));
						//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
						obj.add("message", gson.toJsonTree("You have Successfully Created Claimable Balance of: "+ amount +  " "+assetCodeSender )); 

					}else {
						obj.add("error", gson.toJsonTree("true")); 
						obj.add("message", gson.toJsonTree("Transaction failed")); 
					}
					
					
				}else {
					//This account is not managed by Porte portfolio
					obj.add("error", gson.toJsonTree("false"));
					//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
					obj.add("message", gson.toJsonTree("Payment success "+ amount +  " "+assetCodeSender )); 
				}
						
			try {
				NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
				output = response.getWriter();
				output.print(gson.toJson(obj));
				NeoBankEnvironment.setComment(3, className,"========== end transfer_porte_coin_mbl " + java.time.LocalTime.now() );

			} finally {
				NeoBankEnvironment.setComment(3, className,"========== start cleaning transfer_porte_coin_mbl " + java.time.LocalTime.now() );

				if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
				if (obj != null)obj = null; 
				if (amount != null)amount = null;  if (assetCodeReceiver != null)assetCodeReceiver = null;
				if (extSystemRef != null)extSystemRef = null;  if (tokenValue != null)tokenValue = null;
				if (user != null)user = null; if(authMethod!=null) authMethod= null; 
				if (walletBalance != null)walletBalance = null; 
				if (walletDetails != null)walletDetails = null; 
				if (senderWalletId != null)senderWalletId = null; 
				if (payComments != null)payComments = null; 
				if (hasMnemonic != null)hasMnemonic = null; 
				if (password != null)password = null; 
				if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
				if (keyPair != null)keyPair = null; 
				if (senderKey != null)senderKey = null; 
				if (txnUserCode != null)txnUserCode = null; 
				if (sourcewalletIdInternal != null)sourcewalletIdInternal = null; 
				if (referenceNo != null)referenceNo = null; 
				if (receiverWalletId != null) receiverWalletId = null; 
				if (assetCodeSender != null)assetCodeSender = null; if (txnPayMode != null)txnPayMode = null;
				NeoBankEnvironment.setComment(3, className,"========== after cleaning transfer_porte_coin_mbl " + java.time.LocalTime.now() );

			}								
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in transfer coin, Please try again letter");
			}
		break;
		case "claim_claimable_balance_mbl":
			try {
				JsonObject obj = new JsonObject(); PrintWriter output = null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null; String privateKey = null;
				String claimableBalaceId = null; String assetCode = null; String assetIssuer = null;
				User user = null; String relationshipNo = null; boolean hasTrustLine = false; String authMethod = null;
				String result = null; String txnHash = null; String status = null; String tokenValue = null;
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				if(request.getParameter("claimableBalaceId")!=null)	 claimableBalaceId = request.getParameter("claimableBalaceId").trim();
				if(request.getParameter("assetCode")!=null)	 assetCode = request.getParameter("assetCode").trim();
				if(request.getParameter("assetIssuer")!=null)	 assetIssuer = request.getParameter("assetIssuer").trim();
				if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
				
				if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
				//NeoBankEnvironment.setComment(3, className, "Token is "+tokenValue);
				if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
					NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
					Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
					return;
				}

				
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
				
				KeyPair sourceAccountKeyPair = null;
				String sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				if(sourceAcountId == null)
					throw new Exception("Customer has no wallet in stellar");
			 	sourceAccountKeyPair = KeyPair.fromSecretSeed(privateKey);

				NeoBankEnvironment.setComment(3, className," sourceAcountId " + sourceAcountId+ " sourceAccountKeyPair "
						+ ""+ sourceAccountKeyPair.getAccountId() + " privateKey "+ privateKey);

			 	if(!sourceAccountKeyPair.getAccountId().equals(sourceAcountId)) {
			 		Utilities.sendJsonResponse(response, "true", "Incorrect Secret key");
					return;
			 	}	
				if(assetCode.equals(NeoBankEnvironment.getXLMCode())) {
					hasTrustLine = true;
				}else {
					hasTrustLine = CurrencyTradeUtility.checkTrustLine(assetCode, assetIssuer, sourceAcountId);
				}
				
				if(!hasTrustLine) {
					StellarSDKUtility.createTrustline(assetIssuer, privateKey, org.stellar.sdk.Transaction.MIN_BASE_FEE,
							NeoBankEnvironment.getMaxStellarAssetWalletLimit(),  assetCode );
					CustomerWalletDao.class.getConstructor().newInstance().createWallet(
							relationshipNo, assetCode,sourceAcountId, assetCode);
				}
				result = CurrencyTradeUtility.claimClaimableBalance(claimableBalaceId, privateKey);
				NeoBankEnvironment.setComment(3, className," result "+result);
				if(result == null)
					throw new Exception ("Error in Claiming Claimable Balance");
				status = result.split(",")[0];
				txnHash = result.split(",")[1];
				NeoBankEnvironment.setComment(3, className," status "+status);
				if(status.trim().equalsIgnoreCase("true") ) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo,"C", "CCB","Claimed Claimable Balance transaction hash: "+txnHash );
					obj.addProperty("error", "false"); 
					obj.addProperty("message", "Balance Claimed Suceessfully"); 
				}else {
					obj.addProperty("error", "true"); 
					obj.addProperty("message", "Error in Claiming Balance"); 
				}
				
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + obj.toString());
					output = response.getWriter();
					output.print(obj);
				} finally {
					if (hasMnemonic != null)hasMnemonic = null; 
					if (password != null)password = null; 
					if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
					if (keyPair != null)keyPair = null; if (tokenValue != null)tokenValue = null;
					if(output != null) output.flush();
					if(obj!=null) obj = null; if(authMethod!=null) authMethod= null; 
					if(user!=null) user = null;if(sourceAccountKeyPair!=null) sourceAccountKeyPair = null;
					if(relationshipNo!=null) relationshipNo = null;if(sourceAcountId!=null) sourceAcountId = null;
					if(privateKey!=null) privateKey = null;if(claimableBalaceId!=null) claimableBalaceId = null;
					if(assetCode!=null) assetCode = null;if(assetIssuer!=null) assetIssuer = null;
					if(result!=null) result = null;if(txnHash!=null) txnHash = null;if(status!=null) status = null;
				}

			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in Claiming Balance, Please try again letter");
			}
			
			break;
		
		case "buy_btcx_using_fiat":
			try {
				JsonObject obj = new JsonObject(); String customerCharges = null;
				User user = null; String minimumTxnAmount = null; PrintWriter output = null;
				String relationshipNo= null; 
				Gson gson = new Gson(); String amount= null; String channel =null; // T= Tokenized Card, F=Fiat Wallet, B =Bank  
				Boolean success = false; 	String walletBalance = null; String txnUserCode = null;
				String walletDetails =null; String fiatWalletId=null; 	String btcxWalletId=null; //This is either porte token or vessel coin
				String payComments=""; 	String referenceNo=""; String tokenId = null;
				String assetCode = null; String txnPayMode = null; String extSystemRef = null;
				String cvv = null; String amountBTCx=null; //Get this from stellar
				String authStatus=""; String authMessage=""; String walletId=null; String authResponse=null;
				boolean recordAuthorization=false; String currencyId = null; String transactionCode=null;
				String txnMode=null; String userType=null;
				
				if(request.getParameter("coin_asset")!=null) assetCode = request.getParameter("coin_asset").trim();
				if(request.getParameter("receivedamount")!=null) amount = request.getParameter("receivedamount").trim();
				if(request.getParameter("amount")!=null)	 	amountBTCx = request.getParameter("amount").trim();
				if(request.getParameter("comment")!=null)	 payComments = request.getParameter("comment").trim();
				if(request.getParameter("payment_method")!=null) channel = request.getParameter("payment_method").trim();
				if(channel.equalsIgnoreCase("T")) {
				if(request.getParameter("tokenid")!=null)	
						tokenId = request.getParameter("tokenid").trim();
					if(request.getParameter("cvv")!=null)	
						cvv = request.getParameter("cvv").trim();
				}			
				NeoBankEnvironment.setComment(1, className, "amountBTCx "+amountBTCx);
				NeoBankEnvironment.setComment(1, className, "amount "+amount);
				if (session.getAttribute("SESS_USER") == null) 
					throw new Exception ("Session has expired, please log in again");
				user = (User) session.getAttribute("SESS_USER");
				relationshipNo = user.getRelationshipNo();
				walletDetails = (String)CustomerPorteCoinDao.class.getConstructor()
						.newInstance().getAssetWalletDetails(relationshipNo, assetCode, "");
				if(walletDetails==null) {
					Utilities.sendJsonResponse(response, "error", "Dear customer your dont have coresponding wallet, "
							+ "please create wallet and try again");
					return;
				}
				btcxWalletId = walletDetails.split(",")[0];
				txnUserCode = Utilities.generateTransactionCode(10);
				if(channel.equalsIgnoreCase("T")) {
					//Debit card here
					//Call Gate way here
					//Get charges 
					txnPayMode = NeoBankEnvironment.getCodeBuyBTCxViaToken();
					
					referenceNo = txnPayMode+ "-" + (new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
							+ Utilities.genAlphaNumRandom(9);
					customerCharges = (String) SystemUtilsDao.class.getConstructor().newInstance().getChargesApplicable(
							NeoBankEnvironment.getDefaultCustomerUserType(),txnPayMode, amount);
					 minimumTxnAmount=customerCharges.substring(customerCharges.indexOf("|")+1, customerCharges.length());

					
					if ( Double.parseDouble(amount)< Double.parseDouble(minimumTxnAmount)) { 
						Utilities.sendJsonResponse(response, "error", "Transaction amount can not be less than "+minimumTxnAmount);
						throw new Exception("minimumAmount amount inputed:- Minimum exptected is:- "+minimumTxnAmount+"Amount inputed is"+amount);
					}
					extSystemRef=Utilities.genAlphaNumRandom(24).toUpperCase(); //Remove this after integration
			
					success = (Boolean) CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().
							buyBTCxCoinViaToken(relationshipNo, tokenId, amount, payComments, referenceNo, 
									txnUserCode, customerCharges, txnPayMode, assetCode, extSystemRef, btcxWalletId, amountBTCx);
					
				}
				if(channel.equalsIgnoreCase("F")) {
					walletDetails = (String)CustomerWalletDao.class.getConstructor()
							.newInstance().getFiatWalletBalance(relationshipNo);
					fiatWalletId = walletDetails.split(",")[0];
					walletBalance = walletDetails.split(",")[1];
					txnMode="D";// Debit
					userType="C";
					currencyId = NeoBankEnvironment.getUSDCurrencyId();
					transactionCode=(new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
							+ Utilities.genAlphaNumRandom(9);
					txnPayMode = NeoBankEnvironment.getCodeBuyBTCxViaFiat();
					referenceNo = txnPayMode+ "-" + transactionCode;
					customerCharges = (String) SystemUtilsDao.class.getConstructor().newInstance().getChargesApplicable(
							NeoBankEnvironment.getDefaultCustomerUserType(),txnPayMode, amount);
					 minimumTxnAmount=customerCharges.substring(customerCharges.indexOf("|")+1, customerCharges.length());
					
					if ( Double.parseDouble(amount)< Double.parseDouble(minimumTxnAmount)) { 
						Utilities.sendJsonResponse(response, "error", "Transaction amount can not be less than "+minimumTxnAmount);
						return;
					}
					//Check balance // 
					double senderDebitAmount =  Double.parseDouble(amount) + 
							Double.parseDouble(customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|")));
					if ( senderDebitAmount> Double.parseDouble(walletBalance)) {
						Utilities.sendJsonResponse(response, "error", "Dear customer you have insufficient funds, Kindly top up and try again");
						return;
					}
					/****** Wallet Authorization******/
					
					authResponse=(String)WalletAuthorizationDao.class.getConstructor().newInstance().performWalletAuthorization(relationshipNo, amount, txnMode);
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
						
						recordAuthorization=(boolean)WalletAuthorizationDao.class.getConstructor().newInstance().recordWalletAuthorizationResult(relationshipNo, amount, txnMode, currencyId, 
								userType, referenceNo, walletId, authStatus, authMessage);
						if(!recordAuthorization) { 
							 Utilities.sendJsonResponse(response, "authfailed", "Error in authorization, please try again");
						return;
						}
						     Utilities.sendJsonResponse(response, "authfailed", authMessage);
						return;
					}else {
						// Record successful authorization
						recordAuthorization=(boolean)WalletAuthorizationDao.class.getConstructor().newInstance().recordWalletAuthorizationResult(relationshipNo, amount, txnMode, currencyId, 
								userType, referenceNo, walletId, authStatus, authMessage);
						if(!recordAuthorization) { 
							 Utilities.sendJsonResponse(response, "authfailed", "Error in authorization, please try again");
						return;
						}
					}
					/****** End of Wallet Authorization******/
					
					success = (Boolean) CustomerDigitalCurrenciesDao.class.getConstructor().newInstance().buyBTCxViaFiatWallet(
							relationshipNo, fiatWalletId, amount, payComments, referenceNo, txnUserCode, txnPayMode,
							assetCode, extSystemRef, btcxWalletId, customerCharges, amountBTCx,transactionCode);
				}
				if (success) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, "C",
							txnPayMode," Buy BTCx Using fiat money ");
					obj.add("error", gson.toJsonTree("false"));
					obj.add("message", gson.toJsonTree(" Your Transaction of "+assetCode +":"+ amountBTCx + 
							" is being processed we will notify you once the operation is done ")); 
				}else {
					obj.add("error", gson.toJsonTree("true")); 
					obj.add("message", gson.toJsonTree("Transaction failed")); 
				}
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
					if (obj != null)obj = null; if (minimumTxnAmount != null)minimumTxnAmount = null;
					if (txnUserCode != null)txnUserCode = null; if (channel != null)channel = null;
					if (btcxWalletId != null)btcxWalletId = null; if (tokenId != null)tokenId = null;
					if (assetCode != null)assetCode = null; 
					if (txnPayMode != null)txnPayMode = null; if (extSystemRef != null)extSystemRef = null;
					if (amount != null)amount = null; 
					if (user != null)user = null; 
					if (walletBalance != null)walletBalance = null; 
					if (walletDetails != null)walletDetails = null; 
					if (fiatWalletId != null)fiatWalletId = null; 
					if (payComments != null)payComments = null; 
					if (referenceNo != null)referenceNo = null; 
					if (cvv != null)cvv = null; 
					if (userType != null)userType = null; 
					if (currencyId!=null); currencyId=null;   if (walletId!=null); walletId=null; if (authResponse != null)authResponse = null;
					if (transactionCode!=null); transactionCode=null;    if (txnMode!=null); txnMode=null; 
				}								
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in coin purchase, Please try again letter");
			}
		break;
		
		case "get_exchange_rate_with_markeup":
			try {
				NeoBankEnvironment.setComment(3, className,rulesaction+" inside get_exchange_rate_with_markeup ");

				JsonObject obj = new JsonObject(); User user = null; String sourceAsset= null;
				String destAssetCode= null; PrintWriter output = null; Gson gson = new Gson();
				String sourceAssetIssuer= null;  String destinationAssetIssuer= null; String sourceAmount= null;
				String destinationAmount= null;  String sourceAssetType= null;

				if(request.getParameter("source_asset")!=null)	 sourceAsset = request.getParameter("source_asset").trim();
				if(request.getParameter("destination_asset")!=null)	 destAssetCode = request.getParameter("destination_asset").trim();
				if(request.getParameter("destination_amount")!=null)	 sourceAmount = request.getParameter("destination_amount").trim();
				
				NeoBankEnvironment.setComment(3, className, " in get_exchange_rate_with_markeup sourceAsset "+sourceAsset+" "
						+ "destAssetCode "+ destAssetCode + " sourceAmount "+ sourceAmount  );
				String destinationAssetType = "credit_alphanum4";
				if(sourceAsset.equals("PORTE")) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAsset);
					sourceAssetType  ="credit_alphanum12";
				}else if(sourceAsset.equals("VESL")) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAsset);
					sourceAssetType  ="credit_alphanum4";
				}else if(sourceAsset.equals("USDC")) {
					sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getIssueingAccountPublicKey(sourceAsset);
					sourceAssetType  ="credit_alphanum4";
				}
				
				//TODO
				/*Get BTCx Issuer here
				 */
				
				//destinationAssetIssuer = NeoBankEnvironment.getBTCIssuerAccountId();
				destinationAssetIssuer =(String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getIssueingAccountPublicKey(destAssetCode);
				
				
			   if(sourceAsset.equals("XLM")) {
					sourceAssetType  ="native";
					sourceAssetIssuer = "";
				}
			   
			  
				NeoBankEnvironment.setComment(3, className, " sourceAsset "+sourceAsset+" destAssetCode "+ destAssetCode
						+ " sourceAmount "+ sourceAmount +" sourceAssetIssuer "+ sourceAssetIssuer +" sourceAssetType "+ sourceAssetType
						+ " destinationAssetIssuer "+ destinationAssetIssuer);
				
//				getStrictReceivePath(String sourceAssetCode, String sourceAssetIssuer, String destinationAssetType, 
//						String destinationAssetIssuer, String destinationAssetCode, String destinationAmount )
				
				destinationAmount = CurrencyTradeUtility .getStrictReceivePath(sourceAsset, sourceAssetIssuer,
						destinationAssetType, destinationAssetIssuer, NeoBankEnvironment.getBitcoinCode(), sourceAmount);
				 String markupRate =  (String) CustomerDigitalAssetsDao.class.getConstructor().newInstance().getExChangeRatesMarkUp(sourceAsset);
				 double finalDestinationAmaount = 0;
				 String finalDestinationAmaountString = null;
				 DecimalFormat df = null;
				 double percentage = 100;
				  if (destinationAmount != null && markupRate != null ) {
					  finalDestinationAmaount = (Double.parseDouble(destinationAmount)*(percentage+Double.parseDouble(markupRate))/100);
					  df = new DecimalFormat("#.#######");
				      df.setRoundingMode(RoundingMode.CEILING);
				      finalDestinationAmaountString = df.format(finalDestinationAmaount);
					  NeoBankEnvironment.setComment(3, className, "finalDestinationAmaount is "+finalDestinationAmaount);
				  }else {
					  throw new Exception("Error in getting final destination amount");
				  }
				
				NeoBankEnvironment.setComment(3, className, " finalDestinationAmaount" + finalDestinationAmaount);
				
				obj.add("data", gson.toJsonTree(finalDestinationAmaountString));
				obj.add("error", gson.toJsonTree("false"));
				
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (sourceAsset != null)sourceAsset = null; if (gson != null)gson = null;
					if (obj != null)obj = null;if (destinationAssetIssuer != null)destinationAssetIssuer = null; 
					if (sourceAssetIssuer != null)sourceAssetIssuer = null; 
					if(finalDestinationAmaountString!=null)finalDestinationAmaountString=null; if(df!=null)df=null;
					if (user != null)user = null; if (destinationAmount != null)destinationAmount = null; 
					if (sourceAmount != null)sourceAmount = null; if (sourceAssetType != null)sourceAssetType = null; 
				}
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in getting exchange rates, Please try again letter");
			}
			
			break;
			
			case "exchange_btcx_with_porte_coin":
				try {
					JsonObject obj = new JsonObject(); User user = null; String sourceAsset= null;
					String destAssetCode= null; PrintWriter output = null; Gson gson = new Gson();
					String sourceAssetIssuer= null;  String destinationAssetIssuer= null; String sourceAmount= null;
					String destinationAmount= null;  String sourceAssetType= null; String relationshipNo = null;
					String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
					boolean passIsCorrect = false; KeyPair keyPair = null; String privateKey = null;
					String tdaAccount = null; boolean success = false; String result = "";
					String payType = NeoBankEnvironment.getCodeExchangePorteAssetForBTCx();
					String internalReference = null; String sourceAcountId  = null;
					String walletDetails = null;
					
					
					if(request.getParameter("source_asset")!=null)	 sourceAsset = request.getParameter("source_asset").trim();
					if(request.getParameter("destination_asset")!=null)	 destAssetCode = request.getParameter("destination_asset").trim();
					if(request.getParameter("source_amount")!=null)	 sourceAmount = request.getParameter("source_amount").trim();
					if(request.getParameter("destination_amount")!=null)	 destinationAmount = request.getParameter("destination_amount").trim();
					
					NeoBankEnvironment.setComment(3, className, "SourceAsset is "+sourceAsset);
					if (session.getAttribute("SESS_USER") == null) 
						throw new Exception ("Session has expired, please log in again");
					user = (User) session.getAttribute("SESS_USER");
					relationshipNo = user.getRelationshipNo();
					
					walletDetails = (String)CustomerPorteCoinDao.class.getConstructor()
							.newInstance().getAssetWalletDetails(relationshipNo, destAssetCode, "");
					if(walletDetails==null) {
						Utilities.sendJsonResponse(response, "error", "Dear customer your dont have coresponding wallet, "
								+ "please create wallet and try again");
						return;
					}
					
					if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
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
						if(request.getParameter("security")!=null)	 privateKey = request.getParameter("security").trim();
					}
					
					
					
					if(sourceAsset.equals(NeoBankEnvironment.getPorteTokenCode())) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAsset);
					}else if(sourceAsset.equals(NeoBankEnvironment.getVesselCoinCode())) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAsset);
					}else if(sourceAsset.equals(NeoBankEnvironment.getUSDCCode())) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAsset);
					}else if(sourceAsset.equals(NeoBankEnvironment.getXLMCode())) {
						sourceAssetType="native";
						sourceAssetIssuer="";
					}
					
					sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					if(sourceAcountId == null)
						throw new Exception("Customer does not have a Stellar account.");
					
					KeyPair source = KeyPair.fromSecretSeed(privateKey);
					NeoBankEnvironment.setComment(3, className, "privateKey  "+ privateKey+" sourceAssetIssuer "+
							sourceAssetIssuer+"  account from pvt "+ source.getAccountId());
					if(!source.getAccountId().equals(sourceAcountId)) {
						Utilities.sendJsonResponse(response, "true", "Secret Key is incorrect");
						return;
					}
					
					tdaAccount = (String)CustomerDigitalAssetsDao.class.getConstructor().newInstance().
							getAssetDistributionAccount(destAssetCode);
					KeyPair sourceAccount  = null;
					KeyPair destinationAccount = null;
					if(sourceAsset.equals(NeoBankEnvironment.getXLMCode())) {
						result = CurrencyTradeUtility.sendNativeCoinPayment(tdaAccount, privateKey, sourceAmount);
					}else {			
						sourceAccount = KeyPair.fromSecretSeed(privateKey);
						destinationAccount = KeyPair.fromAccountId(tdaAccount);
						result = CurrencyTradeUtility.sendNoNNativeCoinPayment(sourceAsset, sourceAccount, 
								destinationAccount, sourceAmount, "", sourceAssetIssuer);
					}
					
					if(result.split(",")[0].equals("success")) {
						internalReference =  NeoBankEnvironment.getCodeExchangePorteAssetForBTCx()+ "-" 
					   + (new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
								+ Utilities.genAlphaNumRandom(9);
//						success = (Boolean) CustomerDigitalAssetsDao.class.getConstructor().newInstance().
//								insertBTCExchange(payType, relationshipNo, sourceAsset, sourceAmount, 
//										destAssetCode, destinationAmount, internalReference, result.split(",")[1]);
					}else {
						throw new Exception("Error in transferring assets");
					}
					if (success) {
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, "C",
								payType," Exchanged "+sourceAsset+" to  BTCx ");
						obj.add("error", gson.toJsonTree("false"));
						obj.add("message", gson.toJsonTree(" Your Transaction of "+destAssetCode +":"+ destinationAmount + 
								" is being processed we will notify you once the operation is done ")); 
					}else {
						obj.add("error", gson.toJsonTree("true")); 
						obj.add("message", gson.toJsonTree("Transaction failed")); 
					}

					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" Response is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (hasMnemonic != null)hasMnemonic = null; 
						if (password != null)password = null; 
						if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
						if (keyPair != null)keyPair = null;
						if (obj != null)obj = null; if (user != null)user = null; if (sourceAsset != null)sourceAsset = null;
						if (destAssetCode != null)destAssetCode = null; if (output != null)output.close(); if (gson != null)gson = null;
						if (sourceAssetIssuer != null)sourceAssetIssuer = null; if (destinationAssetIssuer != null)destinationAssetIssuer = null; 
						if (sourceAssetType != null)sourceAssetType = null; if (relationshipNo != null)relationshipNo = null; 
						if (payType != null)payType = null; if (internalReference != null)internalReference = null; 
						if (sourceAcountId != null)sourceAcountId = null;
						if (privateKey != null)privateKey = null;if (tdaAccount != null)tdaAccount = null; if (result != null)result = null;
						if (sourceAccount != null)sourceAccount = null;if (destinationAccount != null)destinationAccount = null; 
						if (source != null)source = null; if (destinationAmount != null)destinationAmount = null; 
						
					}
   					
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in getting exchanging assets to BTCx, Please try again letter");
				}
				break;
				case "get_claimable_balance":
					try {
						JsonObject obj = new JsonObject(); PrintWriter output = null; Gson gson = new Gson();
						String relationshipNo = null; String tokenValue = null;
						if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
						if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
						//NeoBankEnvironment.setComment(3, className, "Token is "+tokenValue);
//						NeoBankEnvironment.setComment(3, className, "relationshipNo is "+relationshipNo);
						if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
							NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
							Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
							return;
						}

						String publicKey =  (String)CustomerDao.class.getConstructor().
								newInstance().getPublicKey(relationshipNo);
						ArrayList<ClaimableBalance> claimableBalances = 
								CurrencyTradeUtility.getClaimClaimableBalances(publicKey);
						if(claimableBalances != null) {
							obj.add("error", gson.toJsonTree("false"));
							obj.add("data", gson.toJsonTree(claimableBalances));
						}else {
							obj.add("error", gson.toJsonTree("true"));
							obj.add("message", gson.toJsonTree("No data found"));
						}
						try {
							NeoBankEnvironment.setComment(3, className,rulesaction+" Response is " + gson.toJson(obj));
							output = response.getWriter();
							output.print(gson.toJson(obj));
						} finally {
							if(relationshipNo!=null)relationshipNo = null; if(publicKey!=null)publicKey = null;
							if(claimableBalances!=null)claimableBalances = null; if (tokenValue != null)tokenValue = null;
							if (output != null)output.close();
						}						
					} catch (Exception e) {
						NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
						Utilities.sendJsonResponse(response, "error", "Error in getting Claimable balances, Please try again letter");
					}
				break;
		
		}
		
		
	}

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session.getAttribute("SESS_USER") == null) 
			Utilities.callException(request, response, ctx, "Session has expired, please log in again");
		switch (rules) {

		
		
		case"Buy BTCx using Fiat":
			try {
				
				request.setAttribute("lastaction", "porte");
				request.setAttribute("lastrule", rules);
				response.setContentType("text/html");
				User user = null;
				String relationshipNo= null;
				 if (session.getAttribute("SESS_USER") == null) 
						throw new Exception ("Session has expired, please log in again");
				user = (User) session.getAttribute("SESS_USER");
				relationshipNo = user.getRelationshipNo();
				String publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
				KeyPair userAccount = null;
				NeoBankEnvironment.setComment(3, className, " Public key is  "+publicKey);
				ArrayList<AssetAccount> accountBalances = null;
				if(publicKey != null && publicKey != "") {
					userAccount = KeyPair.fromAccountId(publicKey);
					accountBalances = StellarSDKUtility.getAccountBalance(userAccount);
				}
				request.setAttribute("externalwallets",accountBalances);
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getCustomerBuyBTCxPage()).forward(request, response);
				} finally {
					if(relationshipNo != null) relationshipNo =null; if(user != null) user =null;
					if(publicKey != null) publicKey =null; if(userAccount != null) userAccount =null;
					if(accountBalances != null) accountBalances =null;
				}
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rules+" is "+e.getMessage());
				Utilities.callException(request, response, ctx, e.getMessage());
			}
			
			break;
		
			
			case "Buy BTCx using Assets":
				try {
					request.setAttribute("lastaction", "porte");
					request.setAttribute("lastrule", rules);
					response.setContentType("text/html");
					User user = null;
					String relationshipNo= null;
					 if (session.getAttribute("SESS_USER") == null) 
							throw new Exception ("Session has expired, please log in again");
					user = (User) session.getAttribute("SESS_USER");
					relationshipNo = user.getRelationshipNo();
					String publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					KeyPair userAccount = null;
					NeoBankEnvironment.setComment(3, className, " Public key is  "+publicKey);
					ArrayList<AssetAccount> accountBalances = null;
					if(publicKey != null && publicKey != "") {
						userAccount = KeyPair.fromAccountId(publicKey);
						accountBalances = StellarSDKUtility.getAccountBalance(userAccount);
					}
					request.setAttribute("externalwallets",accountBalances);
					
					try {
						ctx.getRequestDispatcher(NeoBankEnvironment.getCustomerExchangeBTCxPage()).forward(request, response);
					} finally {
						if(relationshipNo != null) relationshipNo =null; if(user != null) user =null;
						if(publicKey != null) publicKey =null; if(userAccount != null) userAccount =null;
						if(accountBalances != null) accountBalances =null;
					}
					
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rules+" is "+e.getMessage());
					Utilities.callException(request, response, ctx, e.getMessage());
				}
			break;
			
		
		}
		
	}

}
