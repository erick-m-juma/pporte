package com.pporte.rules;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.pporte.model.AssetTransactions;
import com.pporte.model.User;
import com.pporte.utilities.Bip39Utility;
import com.pporte.utilities.StellarSDKUtility;
import com.pporte.utilities.Utilities;
import framework.v8.Rules;

public class CustomerPorteCoinRulesImpl implements Rules {
	private static String className = CustomerPorteCoinRulesImpl.class.getSimpleName();

	@Override
	public void performJSONOperation(String arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			ServletContext arg3) throws Exception {
		
		
	}

	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session = request.getSession(false);
		
		switch (rulesaction) {
			





			case "transfer_porte_coin":
				try {
					NeoBankEnvironment.setComment(3, className,"==== start transfer_porte_coin " + java.time.LocalTime.now() );

					JsonObject obj = new JsonObject(); User user = null;  String relationshipNo= null; PrintWriter output = null;
					Gson gson = new Gson(); String amount= null; String receiverEmail =null; Boolean success = false;
					String walletBalance = null; 	String txnUserCode = null; String walletDetails =null; 
					String senderWalletId=null; String payComments=""; 	String referenceNo=""; 
					String receiverWalletId = null; String assetCodeSender = null;
					String assetCodeReceiver = null; String txnPayMode = null; String extSystemRef = null; String userId = null;
					String senderKey = null; KeyPair destinationAccount = null; KeyPair sourceAccount =null; 
					String sourcewalletIdInternal = "";	String destinationwalletIdInternal = "";
					String destinationAssetBalance = null; String sourceAssetBalance = null; boolean proceed = true;
					String sourceAssetIssuer = null; String sourceAcountId = null; String results = null;
					String [] splitResult=null;String stellarHash=null;
					String mnemonicCode=null;
					String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
					boolean passIsCorrect = false; KeyPair keyPair = null;

					if(request.getParameter("sendamount")!=null) amount = request.getParameter("sendamount").trim();
					if(request.getParameter("sender_asset")!=null)	 assetCodeSender = request.getParameter("sender_asset").trim();
					if(request.getParameter("narrative")!=null)	 payComments = request.getParameter("narrative").trim();
					if(request.getParameter("input_receiver")!=null)	 receiverWalletId = request.getParameter("input_receiver").trim(); //Public Key
					if(request.getParameter("input_private_key")!=null)	 senderKey = request.getParameter("input_private_key").trim();
					if(request.getParameter("mnemonic_code")!=null)	 mnemonicCode = request.getParameter("mnemonic_code").trim();
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
					
				
					
					NeoBankEnvironment.setComment(3, className," inside transfer_porte_coin_mbl is receiverEmail " + receiverEmail +" amount "+ amount + " assetCodeSender "+ assetCodeSender
							+" assetCodeReceiver "+ assetCodeReceiver + " relationshipNo "+ relationshipNo + " userId "+ userId +"mnemonicCode "+mnemonicCode);
					try {
						if(mnemonicCode!=null) {
							NeoBankEnvironment.setComment(3, className,"==== mnemonicCode is not null ");
							KeyPair srcAccount = Bip39Utility.masterKeyGeneration(mnemonicCode);
							sourceAccount = KeyPair.fromSecretSeed(srcAccount.toString());
							NeoBankEnvironment.setComment(3, className,"==== sourceAccount " + sourceAccount+" "+ java.time.LocalTime.now() );
						}else {
							sourceAccount = KeyPair.fromSecretSeed(senderKey);
						}
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
						
						//if(assetCodeSender.equals(NeoBankEnvironment.getStellarLumensCode())) 
							//sourceAssetIssuer = NeoBankEnvironment.getXLMIsssuerAccountId();
					
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
					
					if(assetCodeSender.equals(NeoBankEnvironment.getXLMCode())) {
						 results = StellarSDKUtility.sendNativeCoinPayment(
								assetCodeSender, sourceAccount, destinationAccount, amount, payComments);					
				 		}else {
						 results = StellarSDKUtility.sendNoNNativeCoinPayment(
								assetCodeSender, sourceAccount, destinationAccount, amount, payComments, sourceAssetIssuer);					
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
						
						
						extSystemRef= stellarHash;
						
						success = (Boolean) CustomerPorteCoinDao.class.getConstructor().newInstance().porteCoinP2P(relationshipNo, sourcewalletIdInternal,  amount, payComments, 
								referenceNo, txnUserCode, customerCharges, txnPayMode, assetCodeSender, extSystemRef, destinationwalletIdInternal, sourceAssetBalance, destinationAssetBalance);
						if (success) {
							String moduleCode = txnPayMode;
							///SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Porte Asset "+assetCodeSender+"  P2P" + referenceNo , 0, 48));
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo, "C",
									moduleCode," Porte Asset "+assetCodeSender+" "+ txnPayMode);
							obj.add("error", gson.toJsonTree("false"));
							//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
							obj.add("message", gson.toJsonTree("Payment success: "+ amount +  " "+assetCodeSender )); 

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
					if (obj != null)obj = null; if (receiverEmail != null)receiverEmail = null;
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
					if (stellarHash!=null) stellarHash=null;if (splitResult!=null)splitResult=null;
					NeoBankEnvironment.setComment(3, className,"========== after cleaning transfer_porte_coin_mbl " + java.time.LocalTime.now() );

				}								
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in transfer coin, Please try again letter");
				}
			break;
		
			
				
			
			case "sell_porte_coin":
				try {
					JsonObject obj = new JsonObject(); PrintWriter output = null;
					String sourceAssetCode = null; 	
					String destionAssetCode = null; 	
					String sourceAssetIssuer = null; 	
					String destinationIssuier = null; 	
					String sourceAmount = null; 
					String destMinAmount = null; 
					String privateKey = null; 
					String sourceAcountId = null; 
					String relationshipNo= null;
					User user = null;
					String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
					boolean passIsCorrect = false; KeyPair keyPair = null;
					
					
					if(request.getParameter("coin_asset")!=null)	 sourceAssetCode = request.getParameter("coin_asset").trim();
					if(request.getParameter("receiver_asset")!=null)	 destionAssetCode = request.getParameter("receiver_asset").trim();
					if(request.getParameter("sell_amount")!=null)	 sourceAmount = request.getParameter("sell_amount").trim();
					if(request.getParameter("receivedamount")!=null)	 destMinAmount = request.getParameter("receivedamount").trim();
					//if(request.getParameter("private_key")!=null)	 privateKey = request.getParameter("private_key").trim();
					if(request.getParameter("relno")!=null)	 relationshipNo = request.getParameter("relno").trim();
					
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
					
					if (session.getAttribute("SESS_USER") == null) {
						Utilities.sendSessionExpiredResponse(response, "error", "Session has expired, please log in again");
						return;
					}
					
					//user = (User) session.getAttribute("SESS_USER");
					//relationshipNo = user.getRelationshipNo();
					sourceAcountId = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					if(sourceAcountId == null)
						throw new Exception("Customer has no wallet in stellar");
				 	KeyPair sourceAccountKeyPair = KeyPair.fromSecretSeed(privateKey);

					NeoBankEnvironment.setComment(3, className," sourceAcountId " + sourceAcountId+ " sourceAccountKeyPair "
							+ ""+ sourceAccountKeyPair.getAccountId() + " privateKey "+ privateKey);

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
										
					NeoBankEnvironment.setComment(3, className, "inside sell_porte_coin_mbl sourceAssetCode "+sourceAssetCode
							+" destionAssetCode  "+ destionAssetCode + " sourceAssetIssuer "+ sourceAssetIssuer + " destinationIssuier "+ destinationIssuier
							+ " sourceAmount "+ sourceAmount + " destMinAmount "+destMinAmount +" privateKey "+ privateKey);
				
					String result = StellarSDKUtility.pathPaymentStrictSend(sourceAssetCode, destionAssetCode, sourceAssetIssuer, destinationIssuier,
							destMinAmount, sourceAmount, privateKey);
					
					if(result.equals("success")) {
						NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
						
						obj.addProperty("error","false"); 
						obj.addProperty("message", "You've successfuly swapped "+sourceAmount+" "+ sourceAssetCode+ " to "+destMinAmount+" "+ destionAssetCode); 
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
						if (obj != null)obj = null;if (destinationIssuier != null)destinationIssuier = null;
						if (sourceAssetCode != null)sourceAssetCode = null; 
						if (destionAssetCode != null)destionAssetCode = null;
						if (sourceAssetIssuer != null)sourceAssetIssuer = null; 
						if (privateKey != null)privateKey = null; if (user != null)user = null; 
						if (hasMnemonic != null)hasMnemonic = null; 
						if (password != null)password = null; 
						if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
						if (keyPair != null)keyPair = null;
					}								
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Exchange Assets Failed, Try again");
				}
				break;
				
			case "get_expected_amount":
				try {
					String sourceAssetType = null; String sourceAssetCode= null;
					String sourceAssetIssuer=null; String sourceAmount=null; String destAssetCode = null;
					String destAssetIssuer=null; String destinationAmount = null;
					JsonObject obj = new JsonObject();
					PrintWriter output = null;
					if(request.getParameter("coin_asset")!=null)	
						sourceAssetCode = request.getParameter("coin_asset").trim();
					if(request.getParameter("receiver_asset")!=null)	
						destAssetCode = request.getParameter("receiver_asset").trim();
					if(request.getParameter("amount")!=null)	
						sourceAmount = request.getParameter("amount").trim();
					
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
					if(sourceAssetCode.equals(NeoBankEnvironment.getStellarBTCxCode())) {
						sourceAssetIssuer =  (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						sourceAssetType  ="credit_alphanum4";
					}
					//Destination Asset
					if(destAssetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
						destAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
						sourceAssetType  ="credit_alphanum12";
					}
					if(destAssetCode.equals(NeoBankEnvironment.getUSDCCode())) {
						sourceAssetType  ="credit_alphanum4";
						destAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
					}
					if(destAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
						destAssetIssuer =  (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
						sourceAssetType  ="credit_alphanum4";
					}
					if(destAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
						destAssetIssuer =  (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
						sourceAssetType  ="credit_alphanum4";
					}
					if(destAssetCode.equals(NeoBankEnvironment.getXLMCode())) {
						sourceAssetIssuer =  (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(destAssetCode);
						sourceAssetType  ="native";
					}
					if(destAssetCode.equals(NeoBankEnvironment.getStellarBTCxCode())) {
						sourceAssetIssuer =  (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						sourceAssetType  ="credit_alphanum4";
					}
					NeoBankEnvironment.setComment(3, className, "sourceAssetType_ "+sourceAssetType+" sourceAssetIssuer "+sourceAssetIssuer+
							" sourceAssetCode_ "+sourceAssetCode+" sourceAmount_ "+
							sourceAmount+" destAssetCode_ "+destAssetCode+" destAssetIssuer_ "+destAssetIssuer);
					
					
					destinationAmount = StellarSDKUtility.getPathStrictSendWithDestinationAssets(sourceAssetType, sourceAssetCode,
							sourceAssetIssuer, sourceAmount, destAssetCode, destAssetIssuer);
					if(destinationAmount==null || destinationAmount=="" ) {
						throw new Exception("In geting destination amount");
					}
					
					obj.addProperty("destination_amount", destinationAmount);
					obj.addProperty("error","false");
					
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + obj.toString());
						output = response.getWriter();
						output.print(obj);
					} finally {
						if (output != null)output.close(); if (sourceAssetType != null)sourceAssetType = null;
						if (obj != null)obj = null;if (sourceAssetCode != null)sourceAssetCode = null; 
						if (sourceAssetIssuer != null)sourceAssetIssuer = null; 
						if (sourceAmount != null)sourceAmount = null; if (destinationAmount != null)destinationAmount = null; 
						if (destAssetCode != null)destAssetCode = null; if (destAssetIssuer != null)destAssetIssuer = null;
					}
						
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in getting destination asset, Please try again letter");
				}
				
			break;
			
			
				


			case "get_cust_next_transactions":
				try {
					JsonObject obj = new JsonObject(); 
					ArrayList<AssetTransactions> assetTransactions =null;
					String nextLink=null; Gson gson = new Gson();PrintWriter output = null;
					String publicKey=null;
					
					if(request.getParameter("link")!=null)	 nextLink = request.getParameter("link").trim();
					if(request.getParameter("publickey")!=null)	 publicKey = request.getParameter("publickey").trim();
							// For Next Transactions all the required parameters are present on the link 
						assetTransactions = StellarSDKUtility.getAccountTransactionsCustomerWeb(publicKey, nextLink);
						
						obj.add("data", gson.toJsonTree(assetTransactions));
						obj.add("error", gson.toJsonTree("false"));
					
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (output != null)output.close();  if (gson != null)gson = null;
						if (obj != null)obj = null; if (assetTransactions!=null)assetTransactions=null;
						if(nextLink!=null)nextLink=null; if (publicKey!=null)publicKey=null;
					}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in geting Transaction rate, Please try again letter");
				}
				break;
			case "get_cust_prev_transactions":
				try {
					JsonObject obj = new JsonObject(); 
					ArrayList<AssetTransactions> assetTransactions =null;
					String prevLink=null; Gson gson = new Gson();PrintWriter output = null;
					String publicKey=null;
					
					if(request.getParameter("link")!=null)	 prevLink = request.getParameter("link").trim();
					if(request.getParameter("publickey")!=null)	 publicKey = request.getParameter("publickey").trim();
					// For Prev Transactions all the required parameters are present on the link 
					assetTransactions = StellarSDKUtility.getAccountTransactionsCustomerWeb(publicKey,prevLink);
					
					obj.add("data", gson.toJsonTree(assetTransactions));
					obj.add("error", gson.toJsonTree("false"));
					
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
					} finally {
						if (output != null)output.close();  if (gson != null)gson = null;
						if (obj != null)obj = null;if (assetTransactions!=null)assetTransactions=null;
						if(prevLink!=null)prevLink=null; if (publicKey!=null)publicKey=null;
					}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Error in geting Transaction rate, Please try again letter");
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
		case "Assets":
			try {
				request.setAttribute("lastaction", "porte");
				request.setAttribute("lastrule", "Assets");
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
				request.setAttribute("publickey",publicKey);
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getCustomerShpwtPorteCoinsPage()).forward(request, response);
				}finally {
					if(relationshipNo != null)relationshipNo = null; if(user != null)user = null;
					if(publicKey != null)publicKey = null; if(userAccount != null)userAccount = null;
					if(accountBalances != null)accountBalances = null; 
				}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rules+" is "+e.getMessage());
				Utilities.callException(request, response, ctx, e.getMessage());
			}
			break;
			case "Display Transactions":
				try {
					request.setAttribute("lastaction", "porte");
					request.setAttribute("lastrule", "Display Transactions");
					response.setContentType("text/html");
					String relationshipNo= null;
					User user = null;
					 if (session.getAttribute("SESS_USER") == null) 
							throw new Exception ("Session has expired, please log in again");
					user = (User) session.getAttribute("SESS_USER");
					relationshipNo = user.getRelationshipNo();
					String publicKey = (String)CustomerDao.class.getConstructor().newInstance().getPublicKey(relationshipNo);
					NeoBankEnvironment.setComment(3, className, " Public key is  "+publicKey);
					KeyPair userAccount = null;
					String limit = NeoBankEnvironment.getStellarCustomerTransactionLimit();
					String selfLink=NeoBankEnvironment.getStellarTestEviromentUrl() + "/accounts/" + publicKey+ "/operations?"+ "limit="+ limit + "&order=desc&"+ "join=transactions";
					ArrayList<AssetTransactions> assetTransactions = null;
					if(publicKey != null && publicKey != "") {
						userAccount = KeyPair.fromAccountId(publicKey);
						assetTransactions = StellarSDKUtility.getAccountTransactionsCustomerWeb(publicKey, selfLink);
					}
					request.setAttribute("assetTransactions",assetTransactions);
					request.setAttribute("publickey",publicKey);
					try {
						ctx.getRequestDispatcher(NeoBankEnvironment.getCustomerPorteDisplayTransactions()).forward(request, response);
					}finally {
						if(relationshipNo != null) relationshipNo =null; if(user != null) user =null;
						if(publicKey != null) publicKey =null; if(userAccount != null) userAccount =null;
						if(assetTransactions != null) assetTransactions =null;
					}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rules+" is "+e.getMessage());
					Utilities.callException(request, response, ctx, e.getMessage());
				}
				break;
			
			case"Buy Asset":
				try {
					request.setAttribute("lastaction", "porte");
					request.setAttribute("lastrule", "Buy Asset");
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
					request.setAttribute("publickey",publicKey);
				
					try {
						ctx.getRequestDispatcher(NeoBankEnvironment.getCustomerPorteBuyCoinsPage()).forward(request, response);
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
				
				case "Swap":
					try {
						request.setAttribute("lastaction", "porte");
						request.setAttribute("lastrule", "Swap");
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
							ctx.getRequestDispatcher(NeoBankEnvironment.getCustomerSellPorteCoin()).forward(request, response);
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
