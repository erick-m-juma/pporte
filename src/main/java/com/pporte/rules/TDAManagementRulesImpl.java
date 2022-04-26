package com.pporte.rules;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDigitalAssetsDao;
import com.pporte.dao.CustomerPorteCoinDao;
import com.pporte.dao.OpsManageCryptoDao;
import com.pporte.dao.SystemUtilsDao;
import com.pporte.dao.TDAManagementDao;
import com.pporte.model.AssetAccount;
import com.pporte.model.AssetCoin;
import com.pporte.model.AssetTransactions;
import com.pporte.model.CryptoAssetCoins;
import com.pporte.model.PaymentOffer;
import com.pporte.model.Transaction;
import com.pporte.model.User;
import com.pporte.utilities.CurrencyTradeUtility;
import com.pporte.utilities.StellarSDKUtility;
import com.pporte.utilities.Utilities;

import framework.v8.Rules;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TDAManagementRulesImpl implements Rules{
	private String className=TDAManagementRulesImpl.class.getSimpleName();
	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		
	}

	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session=request.getSession(false);
		if (session.getAttribute("SESS_USER") == null) 
			Utilities.callOpsException(request, response, ctx, "Session has expired, please log in again");
		switch (rulesaction) {
		case "create_stellar_btcx_account":
			try {
				String hasAccount = null; User user = null;
				String publicKey = null; JsonObject stellarAcount = null;
				Boolean createAccountIsSuccessfull = false;
				Boolean accountExist = false; JsonObject obj = new JsonObject();
				KeyPair keyPair = null; Boolean success = false;  String userId=null;
				PrintWriter output = null; String assetCode =null; String moduleCode=null;
			
				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				assetCode  = NeoBankEnvironment.getStellarBTCxCode();
				if(request.getParameter("has_account")!=null)hasAccount = request.getParameter("has_account").trim();
	
				// Check if BTCx Stellar account has already been linked to the system using our system.
				if(TDAManagementDao.class.getConstructor().newInstance().checkIfBTCxAccountHasBeenLinked()) {
					Utilities.sendJsonResponse(response, "error", "You have already linked Stellar BTCx Account in our system");
					return;
				}
				
				if(Boolean.parseBoolean(hasAccount)) {
					//Check if account exist
					if(request.getParameter("input_public_key")!=null)publicKey = request.getParameter("input_public_key").trim();
					keyPair =  KeyPair.fromAccountId(publicKey);
					accountExist = StellarSDKUtility.CheckAccountIfExist(keyPair);
					if(!accountExist) {
						NeoBankEnvironment.setComment(3, className, " Account Does not exist in stellar ");
						Utilities.sendJsonResponse(response, "error", " Account Does not exist in stellar ");
						return;
					}
					success = (Boolean) TDAManagementDao.class.getConstructor().newInstance().linkStellarBTCxAccount(keyPair.getAccountId(), assetCode );
				}
				
				if(!Boolean.parseBoolean(hasAccount)) {
					keyPair = StellarSDKUtility.generateKeyPair();
					stellarAcount = new JsonObject();
					stellarAcount = StellarSDKUtility.createAccount(keyPair);
					createAccountIsSuccessfull = stellarAcount.get("successful").getAsBoolean();
					if(createAccountIsSuccessfull)
						success = (Boolean) TDAManagementDao.class.getConstructor().newInstance().linkStellarBTCxAccount(keyPair.getAccountId(), assetCode );
				}
				if(success) {
					moduleCode="TCB";// TDA Create BTCx Stellar  Account
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,"T", moduleCode,"TDA Create Stellar BTCx Account" );
					obj.addProperty("error", "false");
					if(!Boolean.parseBoolean(hasAccount)) {
						obj.addProperty("message", "You have successfully created Stellar Account "
								+"\n"+"Account number is: "+keyPair.getAccountId()+"\n"+"Private Key is: "+String.valueOf( keyPair.getSecretSeed()));
					}else {

						obj.addProperty("message", "You have successfully Linked your Stellar Account ");
					}
				}else {
					obj.addProperty("error", "false");
					obj.addProperty("message", "Creating account failed");
				}
				try {

					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " +(obj.toString()));
					output = response.getWriter();
					output.print(obj);
				} finally {
					if(user!=null) user=null; if(publicKey!=null) publicKey=null;
					if(stellarAcount!=null) stellarAcount=null; if(obj!=null) obj=null;if (moduleCode!=null) moduleCode=null;
					if (userId!=null)userId=null; 
					if(keyPair!=null) keyPair=null;if(output!=null) output.close(); if (assetCode!=null) assetCode=null;
				}
				
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in Linking BTCx Stellar Account, Please try again letter");
			}
			
	    break;
		
		
			case "create_btcx_trustline":
				try {
					JsonObject obj = new JsonObject();
					User user = null; int baseFee  = org.stellar.sdk.Transaction.MIN_BASE_FEE;
					String userId= null; String limit = null;
					Boolean success = false; 
					PrintWriter output = null; String assetCode =null;
					String privateKey =null;
					String btcxAccount = null; String moduleCode=null;
					String issuerAccountId = null;
					boolean createTrustline = false;
				
					user = (User) session.getAttribute("SESS_USER");
					userId = user.getUserId();
					
					if(request.getParameter("asset_value")!=null)	
						assetCode = request.getParameter("asset_value").trim();
					if(request.getParameter("input_private_key")!=null)	
						privateKey = request.getParameter("input_private_key").trim();
					NeoBankEnvironment.setComment(3, className, " assetCode "+assetCode );
					
					if (assetCode.equals(NeoBankEnvironment.getStellarBTCxCode())) {
						if(request.getParameter("btcx_issuer")!=null)	
							issuerAccountId = request.getParameter("btcx_issuer").trim();
						// NOTE:- assetCode has been changed to BTC because in the system the we refer Stellar Bitcoin as BTCX but when
						// 		  sending it to Stellar the code should be BTC. 
						assetCode=NeoBankEnvironment.getBitcoinCode();
					}
					if (assetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
//						issuerAccountId=NeoBankEnvironment.getPorteIssuerAccountId();
						issuerAccountId = (String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(assetCode);
						
					}
					if (assetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
//						issuerAccountId=NeoBankEnvironment.getVesselIssuerAccountId();
						issuerAccountId=(String)CustomerDigitalAssetsDao.class.getConstructor()
								.newInstance().getIssueingAccountPublicKey(assetCode);
					}
					
					btcxAccount= TDAManagementDao.class.getConstructor().newInstance().getBTCxDistributionAccount();					
					if(btcxAccount == null) {
						throw new Exception("Stellar BTCx Account not created,please create first before creating trustline ");
						}
					
					KeyPair source = KeyPair.fromSecretSeed(privateKey);
				     if(!source.getAccountId().equals(btcxAccount)) {
						obj.addProperty("error", "true"); 
		        		obj.addProperty("message", "Secret key is incorrect"); 
		        		output = response.getWriter();
						output.print(obj);
				    	 return;
				     }
				
					limit = NeoBankEnvironment.getMaxStellarAssetWalletLimit();
					createTrustline = StellarSDKUtility.createTrustline(issuerAccountId, privateKey,
							 baseFee, limit,  assetCode );
					if(createTrustline) {
						// Save Issuer account in the DB
						success = (Boolean)TDAManagementDao.class.getConstructor().newInstance().saveBTCXIssuerAccount(
								issuerAccountId, assetCode);
					}else {
						throw new Exception("Internal error in creating Trustline");
					}
					 try {
			        	if(success == false) {
			        		obj.addProperty("error", "true"); 
			        		obj.addProperty("message", "Creating trustline failed"); 
			        	}else {
			        		moduleCode="TTC"; // TDA  Trustline creation
			        		SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,"T", moduleCode,"TDA  Trustline creation "+assetCode );
			        		obj.addProperty("error", "false"); 
			        		obj.addProperty("message", "Trustline Created Successfully");
			        	}
						output = response.getWriter();
						output.print(obj);
						}finally {
							if(output != null) output.flush();
							if(obj!=null) obj = null;
							if(user!=null) user = null;
							if(userId!=null) userId = null;
							if(btcxAccount!=null) btcxAccount = null;
						}
				} catch (Exception e) {
					NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
					Utilities.sendJsonResponse(response, "error", "Creating trustline failed, Please try again letter");
				}
				break;
					
				
				
				
				case "approve_fiat_to_btcx_transaction":
					try {
						String custRelNo=null; String publicKey=null; 
						String txnAmount=null; String coinAmount=null; 
						String assetCode=null; String distributorAccountPrivateKey=null; 
						PrintWriter out_json = response.getWriter();  JsonObject obj = new JsonObject();
						boolean success=false;  String extSystemRef = "";String comment=null;
						String buyStatus = null; User user = null;
						String txnCode = null; String [] splitResponse=null; 
						String issuerAccountId = null;
						String customerUserId=null;
						
						if(request.getParameter("relno")!=null)	custRelNo = request.getParameter("relno").trim();
						if(request.getParameter("publickey")!=null)	publicKey = request.getParameter("publickey").trim();
						if(request.getParameter("txnamount")!=null)	txnAmount = request.getParameter("txnamount").trim();
						if(request.getParameter("coinamount")!=null) coinAmount = request.getParameter("coinamount").trim();
						if(request.getParameter("privatekey")!=null) distributorAccountPrivateKey = request.getParameter("privatekey").trim();
						if(request.getParameter("txncode")!=null) txnCode = request.getParameter("txncode").trim();
						if(request.getParameter("comment")!=null) comment = request.getParameter("comment").trim();
						if(request.getParameter("cuustmerid")!=null) customerUserId = request.getParameter("cuustmerid").trim();
						
						
						 if (session.getAttribute("SESS_USER") == null) 
								throw new Exception ("Session has expired, please log in again");
						 user = (User) session.getAttribute("SESS_USER");
						 
						 NeoBankEnvironment.setComment(3,className," txnCode is "+txnCode);

						issuerAccountId = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						// NOTE:- assetCode has been changed to BTC because in the system the we refer Stellar Bitcoin as BTCX but when
						// 		  sending it to Stellar the code should be BTC. 
						assetCode=NeoBankEnvironment.getBitcoinCode();
						buyStatus = StellarSDKUtility.buyNoNNativeCoinPaymentWithStellarHashResponse(distributorAccountPrivateKey,publicKey,assetCode,
									issuerAccountId, coinAmount,comment );
						
						 splitResponse=buyStatus.split(",");
						 extSystemRef=splitResponse[1];
						 NeoBankEnvironment.setComment(3,className,"buyStatus is "+buyStatus+" past here hash is "+extSystemRef);
						if(splitResponse[0].equals("true")) { 
							success= (boolean)OpsManageCryptoDao.class.getConstructor().newInstance().updateCustomerDetails(txnCode, extSystemRef);
								if(success) {
									SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(user.getUserId(),"C", "BIC","Approved Fiat to BTCx transaction" );
									obj.addProperty("error", "false"); 
					        		obj.addProperty("message", "Transaction approved: "+coinAmount+" "+assetCode+" has been transfered to "+customerUserId); 
								}else {
									obj.addProperty("error", "true"); 
					        		obj.addProperty("message", "Transaction failed");
								}
						}else {
							obj.addProperty("error", "true"); 
			        		obj.addProperty("message", "Transaction failed in Stellar");
						}
						try {
							out_json = response.getWriter();
							out_json.print(obj);
						}finally {
							 if(coinAmount!=null) coinAmount=null; if (comment!=null)comment=null;
							if(custRelNo!=null) custRelNo=null; if(publicKey!=null) publicKey=null;
							if(assetCode!=null) assetCode=null; if(distributorAccountPrivateKey!=null) distributorAccountPrivateKey=null;
							if(obj!=null) obj = null; if (customerUserId!=null)customerUserId=null; if (splitResponse!=null)splitResponse=null;
							if (buyStatus!=null)buyStatus=null;
							if(txnCode!=null) txnCode = null; if (txnAmount!=null)txnAmount=null;
							if(out_json!=null) out_json.close();
						}
					} catch (Exception e) {
						NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
						Utilities.sendJsonResponse(response, "error", "Error in approving transaction, Please try again letter");
					}
						
					break;
				case "approve_assets_to_btcx_transaction":
					try {
						NeoBankEnvironment.setComment(3, className,"==== start approve_assets_to_btcx_transaction " + java.time.LocalTime.now() );
						
						JsonObject obj = new JsonObject(); User user = null;  String userId= null; PrintWriter output = null;
						Gson gson = new Gson(); String amount= null; String receiverEmail =null; Boolean success = false;
						String walletBalance = null; 	 String walletDetails =null; 
						String senderWalletId=null; String payComments=""; 	String referenceNo=""; 
						String receiverWalletId = null; String assetCodeSender = null; String customerId=null;
						String assetCodeReceiver = null; String txnPayMode = null; String extSystemRef = null; 
						String senderKey = null; KeyPair destinationAccount = null; KeyPair sourceAccount =null; 
						
						
						String sourceAssetIssuer = null; String sourceAcountId = null; String results = null;
						String [] splitResult=null; String stellarHash=null; String transactionCode=null;
						String sourceAsset=null;
						
						if(request.getParameter("destinationamount")!=null) amount = request.getParameter("destinationamount").trim();
						if(request.getParameter("sourceassetcode")!=null) sourceAsset = request.getParameter("sourceassetcode").trim();
						if(request.getParameter("destinationassetcode")!=null)	 assetCodeSender = request.getParameter("destinationassetcode").trim();
						if(request.getParameter("narrative")!=null)	 payComments = request.getParameter("narrative").trim();
						if(request.getParameter("publickey")!=null)	 receiverWalletId = request.getParameter("publickey").trim(); //Public Key
						if(request.getParameter("txncode")!=null)	 transactionCode = request.getParameter("txncode").trim(); //Public Key
						if(request.getParameter("privatekey")!=null)	 senderKey = request.getParameter("privatekey").trim();
						if(request.getParameter("customerId")!=null)	 customerId = request.getParameter("customerId").trim();
						
						if (session.getAttribute("SESS_USER") == null) 
							throw new Exception ("Session has expired, please log in again");
						user = (User) session.getAttribute("SESS_USER");
						userId = user.getUserId();
						
						NeoBankEnvironment.setComment(3, className," inside approve_assets_to_btcx_transaction is receiverEmail " + receiverEmail +" amount "+ amount + " assetCodeSender "+ assetCodeSender
								+" assetCodeReceiver "+ assetCodeReceiver+" userId "+ userId );
						
						/**Check if accounts exists*/
						
						//Sender key SB7AJALFNBC62RFB7VWCB7EWBT47XA6IYW4I2XFSX5CKPEDMZZFT6XSD
						//receiver walletid GCLVJMQHLYKJKGZOCC5Z24NGAFMJTSSF4N4VF77CZICXTGYAE5XSGUBP
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
						sourceAcountId = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxDistributionAccount();
						sourceAssetIssuer= (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						
						
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
						
						 if (assetCodeSender.equals(NeoBankEnvironment.getStellarBTCxCode())) {
							 // Note:- If BTCX change code to BTC to send to Stellar
							 results = StellarSDKUtility.sendNoNNativeCoinPayment(
									 NeoBankEnvironment.getBitcoinCode(), sourceAccount, destinationAccount, amount, payComments, sourceAssetIssuer);
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
						
						txnPayMode=NeoBankEnvironment.getCodeTDASwapPorteAssetsForBTCx();
						
						referenceNo = txnPayMode+ "-" + (new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
								+ Utilities.genAlphaNumRandom(9);
						
							 // Get Stellar Txn hash
							extSystemRef= stellarHash;
							
							success = (boolean)TDAManagementDao.class.getConstructor().newInstance().updateCustomerDetails(transactionCode, extSystemRef);
							if (success) {
								String moduleCode = txnPayMode;
								SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "C",
										moduleCode," TDA "+sourceAsset+" Asset swap to BTCx ");
								obj.add("error", gson.toJsonTree("false"));
								//obj.add("message", gson.toJsonTree("You have transfered " + assetCodeSender+" "+ Utilities.getMoneyinDecimalFormat(amount) + 
								obj.add("message", gson.toJsonTree("Transaction approved: "+ amount +  " "+assetCodeSender+" has been transferred to "+customerId )); 

							}else {
								obj.add("error", gson.toJsonTree("true")); 
								obj.add("message", gson.toJsonTree("Transaction failed")); 
							}
									
					try {
						NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
						output = response.getWriter();
						output.print(gson.toJson(obj));
						NeoBankEnvironment.setComment(3, className,"========== end approve_assets_to_btcx_transaction " + java.time.LocalTime.now() );

					} finally {
						if (output != null)output.close();  if (gson != null)gson = null;
						if (obj != null)obj = null; if (receiverEmail != null)receiverEmail = null;
						if (amount != null)amount = null;  if (assetCodeReceiver != null)assetCodeReceiver = null;
						if (extSystemRef != null)extSystemRef = null; 
						if (user != null)user = null; 
						if (walletBalance != null)walletBalance = null; 
						if (walletDetails != null)walletDetails = null; 
						if (senderWalletId != null)senderWalletId = null; 
						if (payComments != null)payComments = null; 
						if (referenceNo != null)referenceNo = null;  if (receiverWalletId != null) receiverWalletId = null; 
						if (assetCodeSender != null)assetCodeSender = null; if (txnPayMode != null)txnPayMode = null;
					 }								
					} catch (Exception e) {
						NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
						Utilities.sendJsonResponse(response, "true", "Error in TDA Asset Swap to BTCx Please try again later");
					}
				break;
				
				case "exchange_porte_assets_to_btcx":
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
						User user = null;
						String moduleCode=null;
						String userId=null;
					
						if(request.getParameter("coin_asset")!=null)	 sourceAssetCode = request.getParameter("coin_asset").trim();
						if(request.getParameter("receiver_asset")!=null)	 destionAssetCode = request.getParameter("receiver_asset").trim();
						if(request.getParameter("sell_amount")!=null)	 sourceAmount = request.getParameter("sell_amount").trim();
						if(request.getParameter("receivedamount")!=null)	 destMinAmount = request.getParameter("receivedamount").trim();
						if(request.getParameter("private_key")!=null)	 privateKey = request.getParameter("private_key").trim();
						
						
						user = (User) session.getAttribute("SESS_USER");
						userId=user.getUserId();
						sourceAcountId = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxDistributionAccount();
						if(sourceAcountId == null)
							throw new Exception(" TDA Account doesn't exist in stellar");
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
					 			
						if(sourceAssetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
							sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
						}else if(sourceAssetCode.equals(NeoBankEnvironment.getXLMCode())) {
							sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
						}else if(sourceAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
							sourceAssetIssuer =(String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
						}else if(sourceAssetCode.equals(NeoBankEnvironment.getUSDCCode())) {
							sourceAssetIssuer = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(sourceAssetCode);
						}else if(sourceAssetCode.equals(NeoBankEnvironment.getStellarBTCxCode())) {
							sourceAssetIssuer = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						}
						
						if(destionAssetCode.equals(NeoBankEnvironment.getPorteTokenCode())) {
							destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(destionAssetCode);
						}else if(destionAssetCode.equals(NeoBankEnvironment.getXLMCode())) {
							destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(destionAssetCode);
						}else if(destionAssetCode.equals(NeoBankEnvironment.getVesselCoinCode())) {
							destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(destionAssetCode);
						}else if(destionAssetCode.equals(NeoBankEnvironment.getUSDCCode())) {
							destinationIssuier = (String)CustomerDigitalAssetsDao.class.getConstructor()
									.newInstance().getIssueingAccountPublicKey(destionAssetCode);
						}else if(destionAssetCode.equals(NeoBankEnvironment.getStellarBTCxCode())) {
							destinationIssuier = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxIssuingAccount();
						}
											
						NeoBankEnvironment.setComment(3, className, "inside sell_porte_coin_mbl sourceAssetCode "+sourceAssetCode
								+" destionAssetCode  "+ destionAssetCode + " sourceAssetIssuer "+ sourceAssetIssuer + " destinationIssuier "+ destinationIssuier
								+ " sourceAmount "+ sourceAmount + " destMinAmount "+destMinAmount +" privateKey "+ privateKey);
					
						String result = StellarSDKUtility.pathPaymentStrictSend(sourceAssetCode, destionAssetCode, sourceAssetIssuer, destinationIssuier,
								destMinAmount, sourceAmount, privateKey);
						
						if(result.equals("success")) {
							
							NeoBankEnvironment.setComment(3, className,"========== Payment success on Stellar " + java.time.LocalTime.now() );
							moduleCode="TEP";// TDA Exchange Porte Assets to BTCX
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,"T", moduleCode,"TDA Exchange Porte Assets to BTCX" );
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
							if (output != null)output.close(); 
							if (obj != null)obj = null;if (destinationIssuier != null)destinationIssuier = null;
							if (sourceAssetCode != null)sourceAssetCode = null; 
							if (destionAssetCode != null)destionAssetCode = null;
							if (sourceAssetIssuer != null)sourceAssetIssuer = null; 
							if (privateKey != null)privateKey = null; if (user != null)user = null; 
							if (moduleCode!=null)moduleCode=null; if (userId!=null)userId=null;
						}								
					} catch (Exception e) {
						NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
						Utilities.sendJsonResponse(response, "error", "Exchange Assets Failed, Try again");
					}
					break;
					
				case "addbtcaddress":
					try {
						JsonObject obj = new JsonObject(); PrintWriter output = null; String btcAddress=null; 
						String status=null;User user = null; boolean success=false; String assetCode=null; String userId=null;
						String moduleCode=null; 
						
						if(request.getParameter("btcaddress")!=null)	 btcAddress = request.getParameter("btcaddress").trim();
						if(request.getParameter("seladdstatus")!=null)	 status = request.getParameter("seladdstatus").trim();
						if(request.getParameter("seladdassetcode")!=null)	 assetCode = request.getParameter("seladdassetcode").trim();
							
						success=(boolean)TDAManagementDao.class.getConstructor().newInstance().saveBTCAddress(btcAddress,status, assetCode);
						user=(User) session.getAttribute("SESS_USER");
						userId=user.getUserId();
						
						if (success) {
							// Add Audit trail
							moduleCode="TBA";// TDA Adding Bitcoin Address
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,"T", moduleCode,"TDA added BTC address" );
							obj.addProperty("error", "false");
							obj.addProperty("message", "BTC Address "+btcAddress+" added successfully.");
						}else {
							obj.addProperty("error", "true");
							obj.addProperty("message", "Failed to add BTC address");
						}
						try {
							output=response.getWriter();
							output.print(obj);
						}finally {
							if (output != null)output.close();if (obj != null)obj = null;
							if (btcAddress!=null) btcAddress=null; if (userId!=null) userId=null;
							if (moduleCode!=null) moduleCode=null; if (assetCode!=null) assetCode=null;
						}
					}catch (Exception e) {
						NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
						Utilities.sendJsonResponse(response, "error", "Adding BTC Address Failed, Try again");
					}
				break;
				case "editbtcaddress":
					try {
						JsonObject obj = new JsonObject(); PrintWriter output = null; 
						String status=null;User user = null; boolean success=false; String assetCode=null; String userId=null;
						String moduleCode=null;  String btcAddress=null;
						
						if(request.getParameter("selleditstatus")!=null)	 status = request.getParameter("selleditstatus").trim();
						if(request.getParameter("editassetcode")!=null)	 assetCode = request.getParameter("editassetcode").trim();
						if(request.getParameter("editbtcaddress")!=null)	 btcAddress = request.getParameter("editbtcaddress").trim();
						
						
						success=(boolean)TDAManagementDao.class.getConstructor().newInstance().editBTCAddress(assetCode, status, btcAddress);
						user=(User) session.getAttribute("SESS_USER");
						userId=user.getUserId();
						
						if (success) {
							// Add Audit trail
							moduleCode="TUB";// TDA Updated Bitcoin Address
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,"T", moduleCode,"TDA updated Bitcoin Address status" );
							obj.addProperty("error", "false");
							obj.addProperty("message", "BTC Address status updated successfully.");
						}else {
							obj.addProperty("error", "true");
							obj.addProperty("message", "Failed to update BTC status");
						}
						try {
							output=response.getWriter();
							output.print(obj);
						}finally {
							if (output != null)output.close();if (obj != null)obj = null;
							if (userId!=null) userId=null;if (moduleCode!=null) moduleCode=null;
							if (assetCode!=null) assetCode=null; if (btcAddress!=null)btcAddress=null;
						}
					}catch (Exception e) {
						NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
						Utilities.sendJsonResponse(response, "error", "Updating BTC Address Status Failed, Try again");
					}
					break;
					
	
					
				case "getting_offers_from_stellar":
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
						Utilities.sendJsonResponse(response, "error", "Error in getting offers, Please try again letter");
					}
					break;
		}	
	}

	@Override
	public void performOperation (String rulesaction, HttpServletRequest request, HttpServletResponse response,
	ServletContext ctx)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session.getAttribute("SESS_USER") == null)
			Utilities.callOpsException(request, response, ctx, "Session has expired, please log in again");
		switch (rulesaction) {
		
		case "TDA Dashboard":
			try {
				
				request.setAttribute("lastaction", "tdadash");	
				request.setAttribute("lastrule", "TDA Dashboard");
				response.setContentType("text/html");
				ctx.getRequestDispatcher(NeoBankEnvironment.getTDADashboardPage()).forward(request, response);
				
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());

			}
		break;
	
		case "Purchase BTCx":
			try {
				request.setAttribute("lastaction", "tdaacct");	
				request.setAttribute("lastrule", "Purchase BTCx");
				User user = null;
				String relationshipNo= null;
				 if (session.getAttribute("SESS_USER") == null) 
						throw new Exception ("Session has expired, please log in again");
				user = (User) session.getAttribute("SESS_USER");
				relationshipNo = user.getRelationshipNo();
				String publicKey = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxDistributionAccount();
				KeyPair userAccount = null;
				NeoBankEnvironment.setComment(3, className, " Public key is  "+publicKey);
				ArrayList<AssetAccount> accountBalances = null;
				if(publicKey != null && publicKey != "") {
					userAccount = KeyPair.fromAccountId(publicKey);
					accountBalances = StellarSDKUtility.getAccountBalance(userAccount);
				}
				
				response.setContentType("text/html");
				request.setAttribute("externalwallets",accountBalances);
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getTDAPurchaseBtcxPage()).forward(request, response);
				} finally {
					if(relationshipNo != null) relationshipNo =null; if(user != null) user =null;
					if(publicKey != null) publicKey =null; if(userAccount != null) userAccount =null;
					if(accountBalances != null) accountBalances =null;
				}
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
			}
			break;
		case "Configuration":
			try {
				//Get BTCx account to display 
				String btcxAccount =null; KeyPair userAccount = null; ArrayList<AssetAccount> accountBalances = null; String assetCode=null; 
				ArrayList<AssetAccount> arryAssetAccountDetails =null;
				
				btcxAccount= TDAManagementDao.class.getConstructor().newInstance().getBTCxDistributionAccount();
				NeoBankEnvironment.setComment(3, className,"Past here 1 "+btcxAccount );
				
				if (btcxAccount!=null ) {
					// Get BTCx balance from Stellar
					userAccount = KeyPair.fromAccountId(btcxAccount);
					accountBalances = StellarSDKUtility.getAccountBalance(userAccount);
				}
				NeoBankEnvironment.setComment(3, className,"Past here 2 " );
				assetCode=NeoBankEnvironment.getStellarBTCxCode();
				arryAssetAccountDetails =TDAManagementDao.class.getConstructor().newInstance().getBTCXAccountDetails(assetCode);
				NeoBankEnvironment.setComment(3, className,"Past here 3 " );
				request.setAttribute("lastaction", "tdaacct");	
				request.setAttribute("btcxaccount", btcxAccount);	
				request.setAttribute("accountbalances",accountBalances);
				request.setAttribute("arraccountdetails",arryAssetAccountDetails);
				request.setAttribute("lastrule", "Configuration");
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getTDAAccountConfigurationPage()).forward(request, response);
				}
				finally {
					if (btcxAccount!=null) btcxAccount=null; if(userAccount != null)userAccount = null;
					if(accountBalances != null)accountBalances = null;  if (arryAssetAccountDetails!=null)arryAssetAccountDetails=null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
			}
			break;
		case "Fiat to BTCx":
			try {
				ArrayList<AssetCoin> arrBTCxPricing =null; String assetCode=null;
				assetCode=NeoBankEnvironment.getStellarBTCxCode();
				arrBTCxPricing = (ArrayList<AssetCoin>) TDAManagementDao.class .getConstructor().newInstance().getFiatToAssetPricing(assetCode);
				
				request.setAttribute("lastaction", "tdaprcing");	
				request.setAttribute("lastrule", "Fiat to BTCx");
				request.setAttribute("btcxpricing", arrBTCxPricing);

				response.setContentType("text/html");
				try {
				ctx.getRequestDispatcher(NeoBankEnvironment.getTDAFiatToBTCxPricingPage()).forward(request, response);
				}finally {
					if (arrBTCxPricing!=null)arrBTCxPricing=null; if (assetCode!=null)assetCode=null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
			}
			break;
		case "Porte Asset to BTCx":
			try {
				ArrayList<AssetCoin> arrPorteAssetsToBTCxMarkUpRates =null; String assetCode=null;
				assetCode=NeoBankEnvironment.getStellarBTCxCode(); ArrayList<CryptoAssetCoins> arrAssets=null;
				arrPorteAssetsToBTCxMarkUpRates = (ArrayList<AssetCoin>) TDAManagementDao.class .getConstructor().newInstance().getPorteAssetsMarkUpRates();
				arrAssets = (ArrayList<CryptoAssetCoins>) CustomerPorteCoinDao.class .getConstructor().newInstance().getPorteAssetDetails();
				
				request.setAttribute("lastaction", "tdaprcing");	
				request.setAttribute("lastrule", "Porte Asset to BTCx");
				request.setAttribute("porteassetsmarkuprates", arrPorteAssetsToBTCxMarkUpRates);
				request.setAttribute("assets", arrAssets);

				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getTDAPorteAssetToBTCxPricingPage()).forward(request, response);
				}finally {
					if (arrPorteAssetsToBTCxMarkUpRates!=null)arrPorteAssetsToBTCxMarkUpRates=null; if (assetCode!=null)assetCode=null;
					if (arrAssets!=null) arrAssets=null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
				}
			break;
		
		case "Request from Fiat":
			try {
				List <Transaction> btcxRequestTransactions=null;
				btcxRequestTransactions=(List<Transaction>) TDAManagementDao.class.getConstructor().newInstance().getBTCxFromFiatRequests();
				request.setAttribute("lastaction", "tdarqst");	
				request.setAttribute("lastrule", "Request from Fiat");
				request.setAttribute("fiattobtctransactions", btcxRequestTransactions);
	
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getTDARequestFromFiatPage()).forward(request, response);
				}finally {
					if (btcxRequestTransactions!=null)btcxRequestTransactions=null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());				
			}
			break;
			
		case "Request from Porte Assets":
			try {
				ArrayList <Transaction> porteAssetsToBtcxRequestTransactions=null;
				porteAssetsToBtcxRequestTransactions=(ArrayList<Transaction>) TDAManagementDao.class.getConstructor().newInstance().getBTCxFromPorteAssetsRequests();
				request.setAttribute("lastaction", "tdarqst");	
				request.setAttribute("lastrule", "Request from Porte Assets");
				request.setAttribute("porteassetestobtctransactions", porteAssetsToBtcxRequestTransactions);
				response.setContentType("text/html");
				try {
				ctx.getRequestDispatcher(NeoBankEnvironment.getTDARequestFromPorteAssetsPage()).forward(request, response);
				}finally {
					if (porteAssetsToBtcxRequestTransactions!=null)porteAssetsToBtcxRequestTransactions=null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());				
			}
			break;
		case "Display Stellar Transactions":
			try {
				request.setAttribute("lastaction", "tdaacct");
				request.setAttribute("lastrule", "Display Stellar Transactions");
				response.setContentType("text/html");
				String relationshipNo= null;
				User user = null;
				 if (session.getAttribute("SESS_USER") == null) 
						throw new Exception ("Session has expired, please log in again");
				user = (User) session.getAttribute("SESS_USER");
				String publicKey = (String)TDAManagementDao.class.getConstructor().newInstance().getBTCxDistributionAccount();
				NeoBankEnvironment.setComment(3, className, " Public key is  "+publicKey);
				KeyPair userAccount = null;
				String limit = NeoBankEnvironment.getStellarTdaTransactionLimit();
				ArrayList<AssetTransactions> assetTransactions = null;
				if(publicKey != null && publicKey != "") {
					userAccount = KeyPair.fromAccountId(publicKey);
					assetTransactions = StellarSDKUtility.getAccountTransactions(publicKey, limit);
				}
				request.setAttribute("assetTransactions",assetTransactions);
				request.setAttribute("publickey",publicKey);
				try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getTDADisplayStellarTransactionsPage()).forward(request, response);
				}finally {
					if(relationshipNo != null) relationshipNo =null; if(user != null) user =null;
					if(publicKey != null) publicKey =null; if(userAccount != null) userAccount =null;
					if(assetTransactions != null) assetTransactions =null;
				}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());			}
			break;
			
		
		case"Fiat to BTC Rates":
			try {
				ArrayList<AssetCoin> arrBTCPricing =null; String assetCode=null;
				assetCode=NeoBankEnvironment.getBitcoinCode();
				arrBTCPricing = (ArrayList<AssetCoin>) TDAManagementDao.class .getConstructor().newInstance().getFiatToAssetPricing(assetCode);
				
				request.setAttribute("lastaction", "tdabtc");	
				request.setAttribute("lastrule", "Fiat to BTC Rates");
				request.setAttribute("btcpricing", arrBTCPricing);
				response.setContentType("text/html");
				try {
				ctx.getRequestDispatcher(NeoBankEnvironment.getTDAFiatToBTCPricingPage()).forward(request, response);
				}finally {
					if (arrBTCPricing!=null)arrBTCPricing=null; if (assetCode!=null)assetCode=null;
				}
				
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: "+rulesaction+" is "+e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());	
			}
		break;
			
	
			
		}	
	}

}
