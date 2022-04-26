package com.pporte.rules;

import java.io.PrintWriter;
import java.util.ArrayList;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.stellar.sdk.KeyPair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDigitalAssetsDao;
import com.pporte.dao.FundStellarAccountDao;
import com.pporte.model.AssetAccount;
import com.pporte.model.AssetTransactions;
import com.pporte.utilities.StellarSDKUtility;
import com.pporte.utilities.Utilities;

import framework.v8.Rules;

public class OpsFundAccountsRulesImpl implements Rules{
private static String className = OpsFundAccountsRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String ruleaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception{
		switch (ruleaction) {
	
	
		case "Distribution Account":
			try {
				//getPorteDistributorAccountId
				ArrayList<AssetAccount>porteDistributionAccountBal =null;
				ArrayList<AssetAccount>vesselDistributionAccountBal =null;
				ArrayList<AssetTransactions> arrPorteDistributionTxn = null;
				ArrayList<AssetTransactions> arrVesselDistributionTxn = null;
				String porteBalance = null;
				String vesselBalance = null;
//				String porteDistributorAccountId =NeoBankEnvironment.getPorteDistributorAccountId();
				String porteDistributorAccountId =(String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getAssetDistributionAccount(NeoBankEnvironment.getPorteTokenCode());
				String veselDistributionAccId=(String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getAssetDistributionAccount(NeoBankEnvironment.getVesselCoinCode());
				KeyPair sourcePortePublicKey = KeyPair.fromAccountId(porteDistributorAccountId);
			    KeyPair sourceVeselPublicKey = KeyPair.fromAccountId(veselDistributionAccId);
			    porteDistributionAccountBal = StellarSDKUtility.getAccountBalance(sourcePortePublicKey);
			    vesselDistributionAccountBal = StellarSDKUtility.getAccountBalance(sourceVeselPublicKey);
			    
			    for(int i=0; i<porteDistributionAccountBal.size(); i++) {
			    	if(porteDistributionAccountBal.get(i).getAssetCode().equals("PORTE")) {
			    		porteBalance = porteDistributionAccountBal.get(i).getAssetBalance();
			    		NeoBankEnvironment.setComment(3, className, "porteBalance is====================== "+porteBalance);
			    		NeoBankEnvironment.setComment(3, className, "assetcode is====================== "+porteDistributionAccountBal.get(i).getAssetCode());
			    	}
			    		
			    }
			    
			    for(int i=0; i<vesselDistributionAccountBal.size(); i++) {
			    	//if(porteDistributionAccountBal.get(i).getAssetCode().equals("PORTE")) {
			    		vesselBalance = vesselDistributionAccountBal.get(i).getAssetBalance();
			    		NeoBankEnvironment.setComment(3, className, "vesselBalance is====================== "+vesselBalance);
			    		NeoBankEnvironment.setComment(3, className, "assetcode is====================== "+porteDistributionAccountBal.get(i).getAssetCode());
			    	//}
			    		
			    }
			    
			    
				arrPorteDistributionTxn = StellarSDKUtility.getAccountTransactions(porteDistributorAccountId, "100");
				arrVesselDistributionTxn = StellarSDKUtility.getAccountTransactions(veselDistributionAccId, "100");

				request.setAttribute("portedistributionpubkey",porteDistributorAccountId);
				request.setAttribute("vesseldistributionpubkey",veselDistributionAccId);
				request.setAttribute("portedistributionaccbal", porteBalance);
				request.setAttribute("vesseldistributionaccbal", vesselBalance);
				request.setAttribute("portetxn", arrPorteDistributionTxn);
				request.setAttribute("veseltxn", arrVesselDistributionTxn);
				request.setAttribute("lastaction", "opscrypto");
				request.setAttribute("lastrule", "Distribution Account");
				response.setContentType("text/html");
				
			   try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getOpsDistributionPage()).forward(request,
							response);
				} finally {
					if(sourcePortePublicKey !=null) sourcePortePublicKey = null;if(sourceVeselPublicKey !=null) sourceVeselPublicKey = null;
					if(porteDistributionAccountBal !=null) porteDistributionAccountBal = null;if(vesselDistributionAccountBal !=null) vesselDistributionAccountBal = null;
					if(porteDistributorAccountId !=null) porteDistributorAccountId = null;if(veselDistributionAccId !=null) veselDistributionAccId = null;
					if(arrPorteDistributionTxn !=null) arrPorteDistributionTxn = null;if(arrVesselDistributionTxn !=null) arrVesselDistributionTxn = null;
				}

			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: " + ruleaction + " is " + e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
			}
			break;
			
			
		case "Liquidity Accounts":
			try {
				
				ArrayList<AssetAccount>porteAccountBal =null;
				ArrayList<AssetAccount>vesselAccountBal =null;
				ArrayList<AssetTransactions> arrPorteTxn = null;
				ArrayList<AssetTransactions> arrVesselTxn = null;
				String porteBalance = null;
				String vesselBalance = null;
				String porteId =(String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getLiquidityAccountPublicKey(NeoBankEnvironment.getPorteTokenCode());
				String veselAccId=(String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getLiquidityAccountPublicKey(NeoBankEnvironment.getVesselCoinCode());
				NeoBankEnvironment.setComment(3, className, "porteId: " + porteId + " veselAccId " + veselAccId);
				KeyPair sourcePortePublicKey = KeyPair.fromAccountId(porteId);
			    KeyPair sourceVeselPublicKey = KeyPair.fromAccountId(veselAccId);
			    porteAccountBal = StellarSDKUtility.getAccountBalance(sourcePortePublicKey);
			    vesselAccountBal = StellarSDKUtility.getAccountBalance(sourceVeselPublicKey);
			    NeoBankEnvironment.setComment(3, className, "Step 1 ");
			    for(int i=0; i<porteAccountBal.size(); i++) {
		    		porteBalance = porteAccountBal.get(i).getAssetBalance();
		    		NeoBankEnvironment.setComment(3, className, "porteBalance is====================== "+porteBalance);
			    }
			    NeoBankEnvironment.setComment(3, className, "Step 2 ");
			    for(int i=0; i<vesselAccountBal.size(); i++) {
		    		vesselBalance = vesselAccountBal.get(i).getAssetBalance();
		    		NeoBankEnvironment.setComment(3, className, "vesselBalance "+vesselBalance);
			    }
			    
			    NeoBankEnvironment.setComment(3, className, "Step 3 ");
			    
			    arrPorteTxn = StellarSDKUtility.getAccountTransactions(porteId, "100");
			    NeoBankEnvironment.setComment(3, className, "Step 3.5 ");
			    arrVesselTxn = StellarSDKUtility.getAccountTransactions(veselAccId, "100");
			    NeoBankEnvironment.setComment(3, className, "Step 4 ");
				request.setAttribute("portepubkey",porteId);
				request.setAttribute("vesselpubkey",veselAccId);
				request.setAttribute("porteaccbal", porteBalance);
				request.setAttribute("vesselaccbal", vesselBalance);
				request.setAttribute("portetxn", arrPorteTxn);
				request.setAttribute("veseltxn", arrVesselTxn);
				request.setAttribute("lastaction", "opscrypto");
				request.setAttribute("lastrule", ruleaction);
				response.setContentType("text/html");
				
			   try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getOpsLiquidityAccountPage()).forward(request,
							response);
				} finally {
					if(sourcePortePublicKey !=null) sourcePortePublicKey = null;if(sourceVeselPublicKey !=null) sourceVeselPublicKey = null;
					if(porteAccountBal !=null) porteAccountBal = null;if(vesselAccountBal !=null) vesselAccountBal = null;
					if(porteId !=null) porteId = null;if(veselAccId !=null) veselAccId = null;
					if(arrPorteTxn !=null) arrPorteTxn = null;if(arrVesselTxn !=null) arrVesselTxn = null;
				}
				
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: " + ruleaction + " is " + e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
			}
			
			break;

		
		default:
			throw new IllegalArgumentException("Rule not defined value: " + ruleaction);
		}


		
	}

	@Override
	public void performJSONOperation(String arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			ServletContext arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unused")
	@Override
	public void performMultiPartOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		
		HttpSession session = request.getSession(false);
		if (session.getAttribute("SESS_USER") == null) Utilities.callException(request, response, ctx, "Session has expired, please log in again");
		
		switch (rulesaction) {
		case "opsfundspecificacc":
			try {
				
				String publicKey=null;String langPref=null; String srcAccountSecretKey=null;
				String customerName=null;String status=null;String createdOn=null;
				String businessPvtKey=null;String amount=null;JsonObject obj = new JsonObject();
				Gson gson = new Gson();PrintWriter output = null; boolean success =false;
				
				if(request.getParameter("customername")!=null)customerName = StringUtils.trim( request.getParameter("customername") );
				if(request.getParameter("publickey")!=null)publicKey = StringUtils.trim( request.getParameter("publickey") );
				if(request.getParameter("status")!=null)status = StringUtils.trim( request.getParameter("status") );
				if(request.getParameter("createdon")!=null)createdOn = StringUtils.trim( request.getParameter("createdon") );
				if(request.getParameter("businesspvtkey")!=null)businessPvtKey = StringUtils.trim( request.getParameter("businesspvtkey") );
				if(request.getParameter("amount")!=null)amount = StringUtils.trim( request.getParameter("amount") );
				if(request.getParameter("hdnlangpref")!=null)langPref = request.getParameter("hdnlangpref").trim();

				NeoBankEnvironment.setComment(3, className, "businessPvtKey "+businessPvtKey );
				NeoBankEnvironment.setComment(3, className, "amount "+amount );
				//amount=NeoBankEnvironment.getFundPorteAccountAmount();
				status="A";			
				
//				String xlmIssuerAccount = NeoBankEnvironment.getXLMDistributorAccountId();
				String xlmIssuerAccount = (String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getAssetDistributionAccount(NeoBankEnvironment.getXLMCode());
			     KeyPair source = KeyPair.fromSecretSeed(businessPvtKey);
			     if(!source.getAccountId().equals(xlmIssuerAccount)) {
			    	 obj.add("error", gson.toJsonTree("true"));
					 obj.add("message", gson.toJsonTree("Secret key is incorrect"));
			     }else {
			    	 String result=StellarSDKUtility.sendNativeCoinPayment(publicKey, businessPvtKey,amount);
					 if(result.equals("success")) {
							NeoBankEnvironment.setComment(3, className, "Before updating table " );

							success = (boolean)FundStellarAccountDao.class.getConstructor().newInstance().updateStellarAccRel(status,publicKey,amount);
							NeoBankEnvironment.setComment(3, className, "After updating table " );

							obj.add("error", gson.toJsonTree("false"));
							obj.add("message", gson.toJsonTree("Account funded successfuly"));
						 }else {
							 obj.add("error", gson.toJsonTree("true"));
							 obj.add("message", gson.toJsonTree(result));
						 }
			     }
					
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if(langPref !=null) langPref = null;if(publicKey !=null) publicKey = null;
					if(customerName !=null) customerName = null;if(status !=null) status = null;
					if(createdOn !=null) createdOn = null;if(businessPvtKey !=null) businessPvtKey = null;
					if (output != null)output.close(); if (gson != null)gson = null;if (obj != null)obj = null;
				}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: " + rulesaction + " is " + e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in opsfundspecificacc, Please try again letter");
			}
			break;
		case "get_porte_distribution_txn":
			try {
				JsonObject obj = new JsonObject();
				PrintWriter output = null;
				ArrayList<AssetTransactions> arrTransactions = null;
				Gson gson = new Gson();
				String txnDesc= null;

				String accountId = (String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getAssetDistributionAccount(NeoBankEnvironment.getPorteTokenCode());
				arrTransactions = StellarSDKUtility.getAccountTransactions(accountId, "100");
				
				obj.add("data", gson.toJsonTree(arrTransactions));
				obj.add("error", gson.toJsonTree("false"));
				
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (gson != null)gson = null;
					if (obj != null)obj = null;
					if (arrTransactions != null)arrTransactions = null; 
					if (txnDesc != null)txnDesc = null; 
				}
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error in geting Transactions, Please try again letter");
			}
			break;
		}
		
		
		
	}



}
