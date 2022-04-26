package com.pporte.rules;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.stellar.sdk.KeyPair;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDao;
import com.pporte.dao.CustomerDigitalAssetsDao;

import com.pporte.dao.SystemUtilsDao;
import com.pporte.model.User;
import com.pporte.utilities.Bip39Utility;
import com.pporte.utilities.CurrencyTradeUtility;
import com.pporte.utilities.Utilities;

import framework.v8.Rules;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomerMobileDigitalAssetsRulesImpl implements Rules {
	private static String className = CustomerMobileDigitalAssetsRulesImpl.class.getSimpleName();
	@Override
	public void performJSONOperation(String arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			ServletContext arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		
		switch (rulesaction) {

		
		case "mobile_exchange_btcx_with_porte_coin":
			try {
				JsonObject obj = new JsonObject(); User user = null; String sourceAsset= null;
				String destAssetCode= null; PrintWriter output = null; Gson gson = new Gson();
				String sourceAssetIssuer= null;  String destinationAssetIssuer= null; String sourceAmount= null;
				String destinationAmount= null;  String sourceAssetType= null; String relationshipNo = null;
				String hasMnemonic = null; String password  = null; String mnemoniStringFromDB  = null;
				boolean passIsCorrect = false; KeyPair keyPair = null; String privateKey = null;
				String tdaAccount = null; boolean success = false; String result = "";String authMethod = null;
				String payType = NeoBankEnvironment.getCodeExchangePorteAssetForBTCx();
				String internalReference = null; String sourceAcountId  = null; String tokenValue = null;
				
				
				if(request.getParameter("source_asset")!=null)	 sourceAsset = request.getParameter("source_asset").trim();
				if(request.getParameter("destination_asset")!=null)	 destAssetCode = request.getParameter("destination_asset").trim();
				if(request.getParameter("source_amount")!=null)	 sourceAmount = request.getParameter("source_amount").trim();
				if(request.getParameter("destination_amount")!=null)	 destinationAmount = request.getParameter("destination_amount").trim();
				if(request.getParameter("relno")!=null) relationshipNo = request.getParameter("relno").trim();
				if(request.getParameter("hasMnemonic")!=null)	 hasMnemonic = request.getParameter("hasMnemonic").trim();
				if(request.getParameter("token")!=null)	tokenValue = StringUtils.trim(request.getParameter("token"));
				
				if(!Utilities.compareMobileToken(relationshipNo, tokenValue)) {
					NeoBankEnvironment.setComment(1, className, "Error in rule: "+rulesaction+" is invalid token");
					Utilities.sendJsonResponseOfInvalidToken(response, "invalid", "Token value is invalid, please login again");
					return;
				}
				
				NeoBankEnvironment.setComment(3, className, "sourceAsset "+sourceAsset+" destAssetCode "+destAssetCode+" soureAmount "+sourceAmount
						+" destinationAmount "+destinationAmount+" relationshipNo "+relationshipNo+" hasMnemonic "+hasMnemonic);
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
					if (password != null)password = null; if(authMethod!=null) authMethod= null; 
					if (mnemoniStringFromDB != null)mnemoniStringFromDB = null; 
					if (keyPair != null)keyPair = null;
					if (obj != null)obj = null; if (user != null)user = null; if (sourceAsset != null)sourceAsset = null;
					if (destAssetCode != null)destAssetCode = null; if(output!= null)output.close(); if (gson != null)gson = null;
					if (sourceAssetIssuer != null)sourceAssetIssuer = null; if (destinationAssetIssuer != null)destinationAssetIssuer = null; 
					if (sourceAssetType != null)sourceAssetType = null; if (relationshipNo != null)relationshipNo = null; 
					if (payType != null)payType = null; if (internalReference != null)internalReference = null; 
					if (sourceAcountId != null)sourceAcountId = null;
					if (privateKey != null)privateKey = null;if (tdaAccount != null)tdaAccount = null; if (result != null)result = null;
					if (sourceAccount != null)sourceAccount = null;if (destinationAccount != null)destinationAccount = null; 
					if (source != null)source = null; if (destinationAmount != null)destinationAmount = null; 
					if (tokenValue != null)tokenValue = null;
					
				}
					
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in getting exchanging assets to BTCx, Please try again letter");
			}
			break;
		
		}
		
		
	}

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx)
			throws Exception {
		
		
	}

}
