package com.pporte.rules;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import org.apache.commons.lang3.StringUtils;
import org.stellar.sdk.KeyPair;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDao;
import com.pporte.dao.CustomerDigitalAssetsDao;
import com.pporte.dao.CustomerPorteCoinDao;
import com.pporte.dao.SystemUtilsDao;
import com.pporte.dao.TDAManagementDao;
import com.pporte.model.AssetAccount;
import com.pporte.model.User;
import com.pporte.utilities.Bip39Utility;
import com.pporte.utilities.StellarSDKUtility;
import com.pporte.utilities.Utilities;

import framework.v8.Rules;

public class CustomerMobilePorteCoinRulesImpl implements Rules {
	private static String className = CustomerMobilePorteCoinRulesImpl.class.getSimpleName();

	@Override
	public void performJSONOperation(String arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			ServletContext arg3) throws Exception {
		
	}

	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		
		switch (rulesaction) {
			case "get_porte_coins_detailsmbl":
				
				try {
					
					JsonObject obj = new JsonObject();
					PrintWriter output = null;
					Gson gson = new Gson();
					User user = null;
					String relationshipNo = null;KeyPair assetKeyPair = null;
					ArrayList<AssetAccount> assetCoinDetails = null;
					String tokenValue = null;
					if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
					if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
					
					if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
						NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
						Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
						return;
					}
					
					String publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					assetKeyPair = KeyPair.fromAccountId(publicKey);
					assetCoinDetails = StellarSDKUtility.getAccountBalance(assetKeyPair);

					obj.add("publickey", gson.toJsonTree(publicKey));
					obj.add("data", gson.toJsonTree(assetCoinDetails));
					obj.add("error", gson.toJsonTree("false"));
					
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
						if (obj != null)obj = null; if (tokenValue != null)tokenValue = null;
						if (assetCoinDetails != null)assetCoinDetails = null; 
						if (user != null)user = null; 
					}
					
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in geting porte coins, Please try again letter");
				}
			break;
			
			
			
			

			
			case "sell_porte_coin_mbl":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String sourceAssetCode = null; 	String tokenValue = null;
					String destionAssetCode = null; String authMethod = null;
					String sourceAssetIssuer = null; 	
					String destinationIssuier = null; 	
					String sourceAmount = null; 
					String destMinAmount = null; 
					String sourceAcount = null; 
					String sourceAcountId = null; 
					String relationshipNo= null;
					String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
					boolean passIsCorrect = false; KeyPair keyPair = null;
					if(request.getParameter("sourceassetcode")!=null)	 sourceAssetCode = request.getParameter("sourceassetcode").trim();
					if(request.getParameter("destassetcode")!=null)	 destionAssetCode = request.getParameter("destassetcode").trim();
					if(request.getParameter("source_amount")!=null)	 sourceAmount = request.getParameter("source_amount").trim();
					if(request.getParameter("destminamount")!=null)	 destMinAmount = request.getParameter("destminamount").trim();
					//if(request.getParameter("sourceaccount")!=null)	 sourceAcount = request.getParameter("sourceaccount").trim();
					if(request.getParameter("sourceaccountId")!=null)	 sourceAcountId = request.getParameter("sourceaccountId").trim();
					if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
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
						sourceAcount = String.valueOf(keyPair.getSecretSeed());
					}else {
						if(request.getParameter("security")!=null)	 sourceAcount = request.getParameter("security").trim();
					}
					
					
				 	KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(sourceAcount);
				 	if(!sourceAccountKeyPair.getAccountId().equals(sourceAcountId)) {
				 		obj.addProperty("error","true"); 
						obj.addProperty("message", "Incorrect Secret key");
						output = response.getWriter();
						output.print(obj);
						return;
				 	}				 			
				 			
					if(sourceAssetCode.equals("PORTE")) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					}else if(sourceAssetCode.equals("XLM")) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					}else if(sourceAssetCode.equals("VESL")) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					}else if(sourceAssetCode.equals("USDC")) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
					}
					
					if(destionAssetCode.equals("PORTE")) {
						destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destionAssetCode);
					}else if(destionAssetCode.equals("XLM")) {
						destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destionAssetCode);
					}else if(destionAssetCode.equals("VESL")) {
						destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destionAssetCode);
					}else if(destionAssetCode.equals("USDC")) {
						destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destionAssetCode);
					}
					String result = StellarSDKUtility.pathPaymentStrictSend(sourceAssetCode, destionAssetCode, sourceAssetIssuer, destinationIssuier,
							destMinAmount, sourceAmount, sourceAcount);
					
					if(result.equals("success")) {
						
						obj.addProperty("error","false"); 
						obj.addProperty("message", "You've successfuly swapped "+sourceAmount+" "+ sourceAssetCode+ " to "+destMinAmount+" "+ destionAssetCode); 
					}else {
						obj.addProperty("error","true"); 
						obj.addProperty("message", result); 
					}
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " +obj.toString());
						output = response.getWriter();
						output.print(obj);
					} finally {
						if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null;
						if (obj != null)obj = null;if (destinationIssuier != null)destinationIssuier = null;
						if (sourceAssetCode != null)sourceAssetCode = null; if (tokenValue != null)tokenValue = null;
						if (destionAssetCode != null)destionAssetCode = null;
						if (sourceAssetIssuer != null)sourceAssetIssuer = null; 
						if (sourceAcount != null)sourceAcount = null; 
						if (hasMnemonic != null)hasMnemonic = null; 
						if (password != null)password = null; if(authMethod!=null) authMethod= null;
						if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
						if (keyPair != null)keyPair = null;
					}								
					
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Exchange Assets Failed, Try again");
				}
				
				break;
				
			case "transfer_porte_coin_mbl":
				try {
					NeoBankEnvironment.setComment(3, className,"========== start transfer_porte_coin_mbl " + java.time.LocalTime.now() );

					JsonObject obj = new JsonObject(); User user = null;  String relationshipNo= null; PrintWriter output = null;
					Gson gson = new Gson(); String amount= null; String receiverEmail =null; Boolean success = false;
					String walletBalance = null; 	String txnUserCode = null; String walletDetails =null; 
					String senderWalletId=null; String payComments=""; 	String referenceNo=""; 
				    String receiverWalletId = null; String assetCodeSender = null; String [] splitResult=null;
				    String stellarHash=null; String tokenValue = null;  String authMethod = null;

					String assetCodeReceiver = null; String txnPayMode = null; String extSystemRef = null; 
					String senderKey = null; KeyPair destinationAccount = null; KeyPair sourceAccount =null; 
					String sourcewalletIdInternal = null;	String destinationwalletIdInternal = null;
					String destinationAssetBalance = null; String sourceAssetBalance = null; boolean proceed = true;
					String sourceAssetIssuer = null; String sourceAcountId = null; String results = null;
					String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
					boolean passIsCorrect = false; KeyPair keyPair = null;
					if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
					if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
					//NeoBankEnvironment.setComment(3, className, "Token is "+tokenValue);
					if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
						NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
						Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
						return;
					}
					if(request.getParameter("hasMnemonic")!=null)hasMnemonic = request.getParameter("hasMnemonic").trim();
					if(hasMnemonic.equals("true")) {
						if(request.getParameter("auth_method")!=null)	 authMethod = request.getParameter("auth_method").trim();
						if(authMethod.equals("P")) {
							NeoBankEnvironment.setComment(3, className, " authMethod is "+authMethod);
							if(request.getParameter("security")!=null)	 password = request.getParameter("security").trim();
							passIsCorrect = (boolean) CustomerDao.class.getConstructor().newInstance()
									.checkIfPasswordIsCorrect(relationshipNo, password);
							if(!passIsCorrect) {
								NeoBankEnvironment.setComment(1, className, "Password is not correct");
								Utilities.sendJsonResponse(response, "error", "Please enter the correct password");
								return;
							}
						}
						//Get mnemonic code  here
						mnemoniStringFromDB = (String) CustomerDao.class.getConstructor().newInstance()
								.getmnemonicCode(relationshipNo);
						keyPair= Bip39Utility.masterKeyGeneration(mnemoniStringFromDB.replaceAll(",", " "));
						senderKey = String.valueOf(keyPair.getSecretSeed());
					}else {
						if(request.getParameter("security")!=null)	 senderKey = request.getParameter("security").trim();
					}

					
					if(request.getParameter("transfer_amount")!=null) amount = request.getParameter("transfer_amount").trim();
					if(request.getParameter("sender_asset")!=null)	 assetCodeSender = request.getParameter("sender_asset").trim();
					if(request.getParameter("receiver_asset")!=null)  assetCodeReceiver = request.getParameter("receiver_asset").trim();
					if(request.getParameter("narrative")!=null)	 payComments = request.getParameter("narrative").trim();
					if(request.getParameter("receiverwallet")!=null)	 receiverWalletId = request.getParameter("receiverwallet").trim();
					
					String []assetCodeValues = assetCodeSender.split(",");
					assetCodeSender = assetCodeValues[1];
					
					if(!assetCodeSender.equals(assetCodeReceiver)) {
						Utilities.sendJsonResponse(response, "error", "Currently the receiver asset Transfer not supported");
						return;
					}
					
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
						throw new Exception("Customer does not have a Stellar account.");
					
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
						obj.addProperty("error", "true"); 
		        		obj.addProperty("message", "Secret Key is incorrect"); 
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
							
								
					if(assetCodeSender.equals(NeoBankEnvironment.getXLMCode())) {
						 results = StellarSDKUtility.sendNativeCoinPayment(assetCodeReceiver, sourceAccount, destinationAccount, amount, payComments);					
					}else {
						 results = StellarSDKUtility.sendNoNNativeCoinPayment(assetCodeReceiver, sourceAccount, destinationAccount, amount, payComments, sourceAssetIssuer);					
						
					}
					splitResult=results.split(",");
					stellarHash=splitResult[1];
					if(splitResult[0].equals("success")) { 
						//Stellar operation success
						NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
					}else {
						NeoBankEnvironment.setComment(3, className,"========== Payment failed on Stellar " + java.time.LocalTime.now() );
						Utilities.sendJsonResponse(response, "error", results);
						return;
					}
					
					//Get the Wallet balance from Stellar Network after transaction
					ArrayList<AssetAccount> accountBalances = StellarSDKUtility.getAccountBalance(sourceAccount);
					if(accountBalances != null) {

						for(int i = 0; i< accountBalances.size(); i++) {
					 			sourceAssetBalance = accountBalances.get(i).getAssetBalance();
								NeoBankEnvironment.setComment(3, className,"========== "+assetCodeSender +" Source Asset balance is "+ sourceAssetBalance );
						}
						
					}
					
					/**Sender*/
					ArrayList<AssetAccount> receiverAccountBalances = StellarSDKUtility.getAccountBalance(destinationAccount);
					if(receiverAccountBalances != null) {
						for(int i = 0; i< receiverAccountBalances.size(); i++) {
					 			destinationAssetBalance = receiverAccountBalances.get(i).getAssetBalance();
								NeoBankEnvironment.setComment(3, className,"========== "+assetCodeSender +" Destination Asset balance is "+ destinationAssetBalance );
						}
						
					}
										
					//Get the internal wallet details and update the internal wallet ledgers for the coin. **This can be removed later if not required
					sourcewalletIdInternal = (String)CustomerPorteCoinDao.class.getConstructor().newInstance().getAssetWalletDetails(relationshipNo, assetCodeSender, sourceAccount.getAccountId());
					destinationwalletIdInternal = (String)CustomerPorteCoinDao.class.getConstructor().newInstance().getAssetWalletDetails(relationshipNo, assetCodeSender, destinationAccount.getAccountId());
					NeoBankEnvironment.setComment(3, className," === check existence "  );

					//Here wallet provided in not managed by Porte portfoilio.  We dont have to update any ledger on the system
					if(sourcewalletIdInternal ==null) {
						NeoBankEnvironment.setComment(3, className,"========== source not present in porte portfolio  accountId "+ destinationAccount.getAccountId() );
					    //Utilities.sendJsonResponse(response, "error", "Payment successful");
					    proceed = false;
					}
					
					if(destinationwalletIdInternal ==null) {
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
					//proceed = false;  //DONT UPDATE INTERNAL LEDGER FOR NOW REFER TO STELLAR
					if(proceed) {  //Proceed to update Internal ledger
						NeoBankEnvironment.setComment(3, className," === proceed " +referenceNo );

						String customerCharges = (String) SystemUtilsDao.class.getConstructor().newInstance().getChargesApplicable(
								NeoBankEnvironment.getDefaultCustomerUserType(),txnPayMode, amount);
						String minimumTxnAmount=customerCharges.substring(customerCharges.indexOf("|")+1, customerCharges.length());
						
						if ( Double.parseDouble(amount)< Double.parseDouble(minimumTxnAmount)) { 
							Utilities.sendJsonResponse(response, "error", "Transaction amount can not be less than "+minimumTxnAmount);
							return;
						}
						//Check balance // this will be captured in the authorization module once we plugin
						
						
						//Connect to External API here
						extSystemRef=stellarHash; 
						NeoBankEnvironment.setComment(3, className," entering core" +extSystemRef );

						success = (Boolean) CustomerPorteCoinDao.class.getConstructor().newInstance().porteCoinP2P(relationshipNo, sourcewalletIdInternal,  amount, payComments, 
								referenceNo, txnUserCode, customerCharges, txnPayMode, assetCodeSender, extSystemRef, destinationwalletIdInternal, sourceAssetBalance, destinationAssetBalance);
						
						if (success) {
							///SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Porte Asset "+assetCodeSender+"  P2P" + referenceNo , 0, 48));
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, "C",
									txnPayMode," Porte Asset "+assetCodeSender+"  P2P");
							obj.add("error", gson.toJsonTree("false"));
							//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
							obj.add("message", gson.toJsonTree("Payment success  "+ amount +  " "+assetCodeSender )); 

						}else {
							obj.add("error", gson.toJsonTree("true")); 
							obj.add("message", gson.toJsonTree("Transaction failed")); 
						}						
					}else {
						//This account is not managed by Porte portfolio
						obj.add("error", gson.toJsonTree("false"));
						//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
						obj.add("message", gson.toJsonTree("Payment success  "+ amount +  " "+assetCodeSender )); 
					}
							
				try {
					output = response.getWriter();
					output.print(gson.toJson(obj));

				} finally {
					if (output != null)output.close(); if (relationshipNo != null)relationshipNo = null; if (gson != null)gson = null;
					if (obj != null)obj = null; if (receiverEmail != null)receiverEmail = null;
					if (amount != null)amount = null;  if (assetCodeReceiver != null)assetCodeReceiver = null;
					if (extSystemRef != null)extSystemRef = null; if (tokenValue != null)tokenValue = null;
					if (user != null)user = null; if(authMethod!=null) authMethod= null;
					if (walletBalance != null)walletBalance = null; 
					if (walletDetails != null)walletDetails = null; 
					if (senderWalletId != null)senderWalletId = null; 
					if (payComments != null)payComments = null; 
					if (referenceNo != null)referenceNo = null;  if (receiverWalletId != null) receiverWalletId = null; 
					if (assetCodeSender != null)assetCodeSender = null; if (txnPayMode != null)txnPayMode = null;
					if (stellarHash!=null)stellarHash=null; if (splitResult!=null)splitResult=null;
					if (hasMnemonic != null)hasMnemonic = null;  if (password != null)password = null; 
					if (mnemoniStringFromDB != null)mnemoniStringFromDB = null;  if (keyPair != null)keyPair = null;
				}								
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in transfer coin, Please try again letter");
				}
			break;
			


				



			case "asset_exchange_conversion":
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" inside asset_exchange_conversion ");

					JsonObject obj = new JsonObject(); User user = null; String sourceAsset= null;
					String destAssetCode= null; PrintWriter output = null; Gson gson = new Gson();
					String sourceAssetIssuer= null;  String destinationAssetIssuer= null; String sourceAmount= null;
					String destinationAmount= null;  String sourceAssetType= null;

					if(request.getParameter("source")!=null)	 sourceAsset = request.getParameter("source").trim();
					if(request.getParameter("destination")!=null)	 destAssetCode = request.getParameter("destination").trim();
					if(request.getParameter("amount")!=null)	 sourceAmount = request.getParameter("amount").trim();
					
					NeoBankEnvironment.setComment(3, className, " in asset_exchange_conversion sourceAsset "+sourceAsset+" "
							+ "destAssetCode "+ destAssetCode + " sourceAmount "+ sourceAmount  );
					if(sourceAsset.equals(destAssetCode)) {
						destinationAmount = sourceAmount;
						obj.add("data", gson.toJsonTree(destinationAmount));
						obj.add("error", gson.toJsonTree("false"));
						output = response.getWriter();
						output.print(gson.toJson(obj));
						return;
					}
					if(sourceAsset.equals(NeoBankEnvironment.getPorteTokenCode())) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAsset);
						sourceAssetType  ="credit_alphanum12";
					}else if(sourceAsset.equals(NeoBankEnvironment.getVesselCoinCode())) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAsset);
						sourceAssetType  ="credit_alphanum4";
					}else if(sourceAsset.equals(NeoBankEnvironment.getUSDCCode())) {
						sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(sourceAsset);
						sourceAssetType  ="credit_alphanum4";
					}else if(sourceAsset.equals(NeoBankEnvironment.getStellarBTCxCode())) {
						sourceAssetIssuer =  (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						sourceAssetType  ="credit_alphanum4";
				    }

					if(destAssetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
						destinationAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
					}else if(destAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
						destinationAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
					}else if(destAssetCode.equals(NeoBankEnvironment.getUSDCCode())) {
						destinationAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
					}else if(destAssetCode.equals(NeoBankEnvironment.getStellarBTCxCode())) {
						destinationAssetIssuer = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
					}
					
				   if(sourceAsset.equals(NeoBankEnvironment.getXLMCode())) {
						sourceAssetType  ="native";
						sourceAsset = "";
						sourceAssetIssuer = "";
					}
				   
				   if(destAssetCode.equals(NeoBankEnvironment.getXLMCode())) {
						destAssetCode = NeoBankEnvironment.getXLMCode();
						destinationAssetIssuer = "";
					}
					
					destinationAmount = StellarSDKUtility.getPathStrictSendWithDestinationAssets(sourceAssetType, sourceAsset,
							sourceAssetIssuer, sourceAmount, destAssetCode, destinationAssetIssuer);
					
					obj.add("data", gson.toJsonTree(destinationAmount));
					obj.add("error", gson.toJsonTree("false"));
					
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (output != null)output.close(); if (sourceAsset != null)sourceAsset = null; if (gson != null)gson = null;
						if (obj != null)obj = null;if (destinationAssetIssuer != null)destinationAssetIssuer = null; 
						if (sourceAssetIssuer != null)sourceAssetIssuer = null; 
						if (user != null)user = null; if (destinationAmount != null)destinationAmount = null; 
						if (sourceAmount != null)sourceAmount = null; if (sourceAssetType != null)sourceAssetType = null; 
					}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error selling rates, Please try again letter");
				}
				break;
				
				
			
				
			
			
		}
		
	}

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response, 
			ServletContext ctx)throws Exception {
		HttpSession session = request.getSession(false);
		if (session.getAttribute("SESS_USER") == null) 
			Utilities.callException(request, response, ctx, "Session has expired, please log in again");
		switch (rules){
		
		}
		
	}
}
