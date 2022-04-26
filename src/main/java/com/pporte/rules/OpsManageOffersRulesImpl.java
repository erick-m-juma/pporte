package com.pporte.rules;
import java.io.PrintWriter;
import java.util.ArrayList;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.CustomerDigitalAssetsDao;
import com.pporte.model.AssetOffer;
import com.pporte.utilities.StellarSDKUtility;
import com.pporte.utilities.Utilities;

import framework.v8.Rules;

public class OpsManageOffersRulesImpl implements Rules{
private static String className = OpsManageOffersRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String ruleaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception{
		//HttpSession session = request.getSession(false);
		switch (ruleaction) {
		case "Create Offers":
			try {

				request.setAttribute("lastaction", "opscrypto");
				request.setAttribute("lastrule", "Create Offers");
				response.setContentType("text/html");
			   try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getCreateOffersPage()).forward(request,
							response);
				} finally {
				}

			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, className, "Error for  rules: " + ruleaction + " is " + e.getMessage());
				Utilities.callOpsException(request, response, ctx, e.getMessage());
			}
			break;
		case"View Offers":
			try {
				String accountId=null; String vesselAccountId=null;
				
//				accountId= NeoBankEnvironment.getPorteLiquidityAccountId();	
				accountId= (String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getLiquidityAccountPublicKey(NeoBankEnvironment.getPorteTokenCode());
//				vesselAccountId=NeoBankEnvironment.getVesselLiquidityAccountId();
				vesselAccountId=(String)CustomerDigitalAssetsDao.class.getConstructor()
						.newInstance().getLiquidityAccountPublicKey(NeoBankEnvironment.getVesselCoinCode());
				
				ArrayList<AssetOffer>allAssetOffers=null; ArrayList<AssetOffer>arrVessleOffers=null;
				
				allAssetOffers=StellarSDKUtility.getAccountOffers(accountId);
				arrVessleOffers=StellarSDKUtility.getAccountOffers(vesselAccountId);
				
				request.setAttribute("lastaction", "opscrypto");
				request.setAttribute("lastrule", "View Offers");
				response.setContentType("text/html");
				request.setAttribute("alloffers", allAssetOffers);
				request.setAttribute("allvessleoffers", arrVessleOffers);

			   try {
					ctx.getRequestDispatcher(NeoBankEnvironment.getViewadEditOffersPage()).forward(request,
							response);
				} finally {
					if (allAssetOffers != null)allAssetOffers = null;
					if (arrVessleOffers != null)arrVessleOffers = null;
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
		case "opscreateselloffer":
			try {
				NeoBankEnvironment.setComment(3, className, " Inside create sell offer ");
				String sellOfferAmount=null; String selPriceUnit=null;String userType=null;
				JsonObject obj = new JsonObject();Gson gson = new Gson();PrintWriter output = null;
				 String assetCode=null;String businessSecretKey =null;String liquidityAccount=null;
				if(request.getParameter("sellingamount")!=null)sellOfferAmount = request.getParameter("sellingamount").trim();
				if(request.getParameter("priceunit")!=null)selPriceUnit = request.getParameter("priceunit").trim();
				if(request.getParameter("hdnusertype")!=null)userType = request.getParameter("hdnusertype").trim();
				if(request.getParameter("hdnselaccount")!=null)assetCode = request.getParameter("hdnselaccount").trim();
				if(request.getParameter("buyprivatekey")!=null)businessSecretKey = request.getParameter("buyprivatekey").trim();
	
				NeoBankEnvironment.setComment(3, className, "assetcode is "+assetCode +" buyOfferAmount "
						+ ""+"buyPriceUnit "+selPriceUnit +" userType "+userType+" assetCode "+assetCode +" businessSecretKey "+businessSecretKey);
				if(assetCode.equals("PORTE")) {
					//liquidityAccount=NeoBankEnvironment.getPorteLiquidityAccountId();
					liquidityAccount=(String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getLiquidityAccountPublicKey(assetCode);
				}else if(assetCode.equals("VESL")) {
					//liquidityAccount=NeoBankEnvironment.getVesselLiquidityAccountId();
					liquidityAccount=(String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getLiquidityAccountPublicKey(assetCode);

				}
				 String result=StellarSDKUtility.manageSellOffer(businessSecretKey, assetCode, liquidityAccount, sellOfferAmount, selPriceUnit);
				 if(result.equals("success")) {
					obj.add("error", gson.toJsonTree("false"));
					obj.add("message", gson.toJsonTree("Sell offer Created Successful"));
				 }else {
					 obj.add("error", gson.toJsonTree("true"));
					 obj.add("message", gson.toJsonTree(result));
				 }
				
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (sellOfferAmount != null)sellOfferAmount = null; if (gson != null)gson = null;
					if (obj != null)obj = null; if (selPriceUnit != null)selPriceUnit = null;if (userType != null)userType = null; 
				}	
				
			}catch(Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error Manage Sell Offer, Please try again later");
			}
			break;
			
		case"opscreatebuyoffer":
			try {
				String buyOfferAmount=null; String buyPriceUnit=null;String userType=null;
				JsonObject obj = new JsonObject();Gson gson = new Gson();PrintWriter output = null;
				String businessSecretKey=null;String assetCode=null;String liquidityAccount=null;
				
				if(request.getParameter("sellbuyamount")!=null)buyOfferAmount = request.getParameter("sellbuyamount").trim();
				if(request.getParameter("sellpriceunit")!=null)buyPriceUnit = request.getParameter("sellpriceunit").trim();
				if(request.getParameter("hdnusertype")!=null)userType = request.getParameter("hdnusertype").trim();
				if(request.getParameter("hdnselaccount")!=null)assetCode = request.getParameter("hdnselaccount").trim();
				if(request.getParameter("sellprivatekey")!=null)  businessSecretKey= request.getParameter("sellprivatekey").trim();
				
				NeoBankEnvironment.setComment(3, className, "assetcode is "+assetCode +" buyOfferAmount "
						+ ""+"buyPriceUnit "+buyPriceUnit +" userType "+userType+" assetCode "+assetCode +" liquiditySecretKey "+businessSecretKey);
				
				if(assetCode.equals("PORTE")) {
					//liquidityAccount=NeoBankEnvironment.getPorteLiquidityAccountId();
					liquidityAccount=(String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getLiquidityAccountPublicKey(assetCode);
				}else if(assetCode.equals("VESL")) {
					//liquidityAccount=NeoBankEnvironment.getVesselLiquidityAccountId();
					liquidityAccount=(String)CustomerDigitalAssetsDao.class.getConstructor()
							.newInstance().getLiquidityAccountPublicKey(assetCode);

				}

				String result=StellarSDKUtility.manageBuyOffer(businessSecretKey, assetCode, liquidityAccount, buyOfferAmount, buyPriceUnit);
				if(result.equals("success")) {
					obj.add("error", gson.toJsonTree("false"));
					obj.add("message", gson.toJsonTree("Buy offer Created Successful"));
				}else {
					 obj.add("error", gson.toJsonTree("true"));
					 obj.add("message", gson.toJsonTree(result));
				}				
				try {
					NeoBankEnvironment.setComment(3, className,rulesaction+" String is " + gson.toJson(obj));
					output = response.getWriter();
					output.print(gson.toJson(obj));
				} finally {
					if (output != null)output.close(); if (buyOfferAmount != null)buyOfferAmount = null; if (gson != null)gson = null;
					if (obj != null)obj = null; if (buyPriceUnit != null)buyPriceUnit = null;if (userType != null)userType = null; 
				}	
				
			}catch(Exception e) {
				NeoBankEnvironment.setComment(1, className, " Overall Exception for case "+rulesaction+" is "+e.getMessage());
				Utilities.sendJsonResponse(response, "error", "Error Manage Buy Offer, Please try again later");
			}
			break;
		default:
			throw new IllegalArgumentException("Rule not defined value: " + rulesaction);
		}
		
		
		
	}



}
