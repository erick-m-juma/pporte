package com.pporte.utilities;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class JSPUtilities {
	
	private static ArrayList <String> arrMenuOperations = null;
	private static ArrayList <String> arrMenuIconOperations = null;
	
	private static ArrayList <String> arrMenuTDAUser = null;
	private static ArrayList <String> arrMenuIconTDAUser = null;
	
	private static ArrayList <String> arrMenuPartners = null;
	private static ArrayList <String> arrMenuIconPartners = null;
	
	private static ArrayList <String> arrMenuFreeTireCustomer = null;
	private static ArrayList <String> arrMenuIconFreeTireCustomer = null;
	
	private static ArrayList <String> arrMenuBasicCustomer = null;
	private static ArrayList <String> arrMenuIconBasicCustomer = null;
	
	private static ArrayList <String> arrMenuPlatinumCustomer = null;
	private static ArrayList <String> arrMenuIconPlatinumCustomer = null;
	
	private static ConcurrentHashMap <String, String> hashSubMenuOperations  = null;
	private static ConcurrentHashMap <String, String> hashSubMenuPartners  = null;
	
	private static ConcurrentHashMap <String, String> hashSubMenuTDAUser  = null;
	
	private static ConcurrentHashMap <String, String> hashSubMenuFreeTireCustomer  = null;
	private static ConcurrentHashMap <String, String> hashSubMenuBasicCustomer  = null;
	private static ConcurrentHashMap <String, String> hashSubMenuPlatinumCustomer  = null;
	
	
	public static void initAll() throws Exception{
		//Operations Menu and Submenu generation
		if(arrMenuOperations==null || arrMenuIconOperations==null) {
			System.out.println("---- ininitializing the Operation menu -----");
			arrMenuOperations = new ArrayList<String>();	arrMenuIconOperations = new ArrayList<String>();
			
			//******** Populate the menu list and its icon - re fer Functional Spec Items.docx.... no whitespaces
			arrMenuOperations.add(0, "dash,Dashboard");						arrMenuIconOperations.add(0, "side-menu__icon typcn typcn-device-desktop");
			arrMenuOperations.add(1, "prf,Profile");							arrMenuIconOperations.add(1, "side-menu__icon typcn typcn-th-large-outline");
			arrMenuOperations.add(2, "opscust,Manage Customer");				arrMenuIconOperations.add(2, "side-menu__icon typcn typcn-group");
			arrMenuOperations.add(3, "opsprc,Transaction Pricing");			arrMenuIconOperations.add(3, "side-menu__icon typcn typcn-cog-outline");
			arrMenuOperations.add(4, "opspln,Customer Plan");			arrMenuIconOperations.add(4, "side-menu__icon typcn typcn-gift");
			arrMenuOperations.add(5, "opscrypto,Manage Digital Assets");			    arrMenuIconOperations.add(5, "side-menu__icon typcn typcn-clipboard");
			arrMenuOperations.add(6, "opswal,Manage Wallets");			    arrMenuIconOperations.add(6, "side-menu__icon typcn typcn-spanner-outline"); 
			arrMenuOperations.add(7, "opscrd,Manage Cards");			        arrMenuIconOperations.add(7, "side-menu__icon typcn typcn-clipboard");
			arrMenuOperations.add(8, "opstxn,Manage Transactions");			arrMenuIconOperations.add(8, "side-menu__icon typcn typcn-clipboard");
			arrMenuOperations.add(9, "opsremit,Ops Remittance");			arrMenuIconOperations.add(9, "side-menu__icon typcn typcn-clipboard");
			arrMenuOperations.add(10, "opslyt,Manage Loyalty");			    arrMenuIconOperations.add(10, "side-menu__icon typcn typcn-point-of-interest");
			arrMenuOperations.add(11, "opsdspt,Manage Disputes");			    arrMenuIconOperations.add(11, "side-menu__icon typcn typcn-phone-outline");
			arrMenuOperations.add(12, "blockchain,Blockchain Management");			    arrMenuIconOperations.add(12, "side-menu__icon typcn typcn-phone-outline");
			arrMenuOperations.add(13, "lgt,Sign Out");			            	arrMenuIconOperations.add(13, "side-menu__icon typcn typcn-eject-outline");
			
			//Populate Ops Sub-menu
			hashSubMenuOperations = new ConcurrentHashMap <String, String>(); //Strictly NO SPACES BETWEEN COMMAS
			hashSubMenuOperations.put("dash", "Ops Dashboard,Pending Customers,Business Ledger,Audit Logs");
			hashSubMenuOperations.put("opspln","Product Plans,Customer Plan Allocation");	
			hashSubMenuOperations.put("opscust", "View Customers,Pending Customers");
			hashSubMenuOperations.put("opsprc", "Transaction Rules,Set Pricing,Set Transaction Limits");
			hashSubMenuOperations.put("opscrypto", "Fund New Accounts,View Funded Accounts,Create Offers,View Offers,View Porte Request,Asset Pricing,Wallet Asset Accounts,Distribution Account,Liquidity Accounts,Create Claimable Balance");
			hashSubMenuOperations.put("opswal", "Manage Block Codes,View Porte Wallets,Block Wallets,View Authorized Transactions");
			hashSubMenuOperations.put("opscrd", "View Cards");
			hashSubMenuOperations.put("opstxn", "Fiat Wallet Transactions,Porte Transactions,Card Wallets Transactions");
			hashSubMenuOperations.put("opsrprt", "Customer Reports,Merchant Reports");
			hashSubMenuOperations.put("opslyt", "Create Loyalty Rule,View Customer Loyalty,Redeem Rates");
			hashSubMenuOperations.put("opsdspt", "Dispute Reasons,Ops Raise Disputes,View Raised Disputes");
			hashSubMenuOperations.put("blockchain", "Blockchain Transactions");
			hashSubMenuOperations.put("opsremit", "View Partners,View Remittance Transactions");
			hashSubMenuOperations.put("prf", "View Profile,Manage Ops Users");
			
		}
		
		//TDA Menu and Submenu generation
		if(arrMenuTDAUser==null || arrMenuIconTDAUser==null) {
			System.out.println("---- ininitializing the TDA menu -----");
			arrMenuTDAUser = new ArrayList<String>();	arrMenuIconTDAUser = new ArrayList<String>();
			
			// TDA Menus
			arrMenuTDAUser.add(0, "tdadash,Dashboard");						arrMenuIconTDAUser.add(0, "side-menu__icon typcn typcn-device-desktop");
			arrMenuTDAUser.add(1, "prf,Profile");							arrMenuIconTDAUser.add(1, "side-menu__icon typcn typcn-th-large-outline");
			arrMenuTDAUser.add(2, "tdabtc, Manage Bitcoin");				arrMenuIconTDAUser.add(2, "side-menu__icon fa fa-bitcoin");
			arrMenuTDAUser.add(3, "tdaacct,BTCx Account");			arrMenuIconTDAUser.add(3, "side-menu__icon typcn typcn-cog-outline");
			arrMenuTDAUser.add(4, "tdaprcing,BTCx Pricing");			arrMenuIconTDAUser.add(4, "side-menu__icon typcn typcn-gift");
			arrMenuTDAUser.add(5, "tdarqst,BTCx Request");			    arrMenuIconTDAUser.add(5, "side-menu__icon typcn typcn-clipboard");	
			arrMenuTDAUser.add(6, "tdarmt,TDA Remittance");			    arrMenuIconTDAUser.add(6, "side-menu__icon typcn typcn-clipboard");	
			arrMenuTDAUser.add(7, "lgt,Sign Out");			            	arrMenuIconTDAUser.add(7, "side-menu__icon typcn typcn-eject-outline");
			
			//Populate TDA Sub-menu
			hashSubMenuTDAUser = new ConcurrentHashMap <String, String>(); //Strictly NO SPACES BETWEEN COMMAS
			hashSubMenuTDAUser.put("tdadash", "TDA Dashboard");
			hashSubMenuTDAUser.put("prf", "View Profile");
			hashSubMenuTDAUser.put("tdaacct", "Configuration,Purchase BTCx,Display Stellar Transactions");
			hashSubMenuTDAUser.put("tdaprcing", "Fiat to BTCx,Porte Asset to BTCx");
			hashSubMenuTDAUser.put("tdarqst", "Request from Fiat,Request from Porte Assets,Request from BTC");
			hashSubMenuTDAUser.put("tdabtc", "Account Information,Fiat to BTC Rates,BTC to BTCx Rates,View Fiat to BTC Requests,Display BTC Transactions");
			hashSubMenuTDAUser.put("tdarmt", "Fiat Remittance Request");
			
			
		}
		
		//Partners Menu and Submenu generation
		if(arrMenuPartners==null || arrMenuIconPartners==null) {
			System.out.println("---- ininitializing the Partners menu -----");
			arrMenuPartners = new ArrayList<String>();	arrMenuIconPartners = new ArrayList<String>();
			
			//Partners Menu
			arrMenuPartners.add(0, "partdash,Dashboard");	arrMenuIconPartners.add(0, "side-menu__icon typcn typcn-device-desktop");
			arrMenuPartners.add(1, "lgt,Sign Out");			arrMenuIconPartners.add(1, "side-menu__icon typcn typcn-eject-outline");
			
			//Populate Partners Sub-menu
			hashSubMenuPartners = new ConcurrentHashMap <String, String>(); //Strictly NO SPACES BETWEEN COMMAS
			hashSubMenuPartners.put("partdash","Partner Profile,Pending Transactions,Completed Transactions,Create Trustline");
			
		}
		
		//Customers FreeTire Menu and Submenu generation
		if(arrMenuFreeTireCustomer==null || arrMenuIconFreeTireCustomer==null) {
			System.out.println("---- ininitializing the FreeTire menu -----");
			arrMenuFreeTireCustomer = new ArrayList<String>();	arrMenuIconFreeTireCustomer = new ArrayList<String>();
			
			//Customers FreeTire Menu
			arrMenuFreeTireCustomer.add(0, "dash,Dashboard");					   arrMenuIconFreeTireCustomer.add(0, "side-menu__icon typcn typcn-device-desktop");
			arrMenuFreeTireCustomer.add(1, "prf,Profile");						   arrMenuIconFreeTireCustomer.add(1, "side-menu__icon typcn typcn-th-large-outline");
			arrMenuFreeTireCustomer.add(2, "wal,Manage Fiat Wallet");				   arrMenuIconFreeTireCustomer.add(2, "side-menu__icon ti-wallet");
			arrMenuFreeTireCustomer.add(3, "card,Manage Cards");				   arrMenuIconFreeTireCustomer.add(3, "side-menu__icon ti-credit-card");
			arrMenuFreeTireCustomer.add(4, "custdspt,Disputes");				   arrMenuIconFreeTireCustomer.add(4, "side-menu__icon ti-anchor");
			arrMenuFreeTireCustomer.add(5, "plb,Buy Plan");						   arrMenuIconFreeTireCustomer.add(5, "side-menu__icon ti-gift");
			arrMenuFreeTireCustomer.add(6, "lgt,Logout");			               arrMenuIconFreeTireCustomer.add(6, "side-menu__icon typcn typcn-eject-outline");
			
			//Populate Customers FreeTire Sub-menu
			hashSubMenuFreeTireCustomer = new ConcurrentHashMap <String, String>(); //Strictly NO SPACES BETWEEN COMMAS
			hashSubMenuFreeTireCustomer.put("dash","Dashboard");
			hashSubMenuFreeTireCustomer.put("prf", "View and Edit");
			hashSubMenuFreeTireCustomer.put("wal", "View Wallet,Topup Wallet,Send Money,Cash Transactions");
			hashSubMenuFreeTireCustomer.put("card", "Show Cards");
			hashSubMenuFreeTireCustomer.put("custdspt", "View Dispute,Raise Disputes");
			hashSubMenuFreeTireCustomer.put("plb", "Buy New Plan");
					
		}
		
		//Customers Basic Plan Menu and Submenu generation
		if(arrMenuBasicCustomer==null || arrMenuIconBasicCustomer==null) {
			System.out.println( "---- ininitializing the Basic Plan menu -----");
			arrMenuBasicCustomer = new ArrayList<String>();	arrMenuIconBasicCustomer = new ArrayList<String>();
			
			//Customers Basic Plan Menu
			arrMenuBasicCustomer.add(0, "dash,Dashboard");					    arrMenuIconBasicCustomer.add(0, "side-menu__icon typcn typcn-device-desktop");
			arrMenuBasicCustomer.add(1, "prf,Profile");						arrMenuIconBasicCustomer.add(1, "side-menu__icon typcn typcn-th-large-outline");
			arrMenuBasicCustomer.add(2, "wal,Manage Fiat Wallet");						arrMenuIconBasicCustomer.add(2, "side-menu__icon ti-wallet");
			arrMenuBasicCustomer.add(3, "porte,Digital Assets");						arrMenuIconBasicCustomer.add(3, "side-menu__icon typcn typcn-briefcase");
			arrMenuBasicCustomer.add(4, "card,Manage Cards");						arrMenuIconBasicCustomer.add(4, "side-menu__icon ti-credit-card");
			arrMenuBasicCustomer.add(5, "custdspt,Disputes");						   arrMenuIconBasicCustomer.add(5, "side-menu__icon ti-anchor");
			arrMenuBasicCustomer.add(6, "plu,Update Plan");						   arrMenuIconBasicCustomer.add(6, "side-menu__icon ti-gift");
			arrMenuBasicCustomer.add(7, "lgt,Logout");			            arrMenuIconBasicCustomer.add(7, "side-menu__icon typcn typcn-eject-outline");			
			
			//Populate Customers Basic Plan Sub-menu
			hashSubMenuBasicCustomer = new ConcurrentHashMap <String, String>(); //Strictly NO SPACES BETWEEN COMMAS
			hashSubMenuBasicCustomer.put("dash","Dashboard");
			hashSubMenuBasicCustomer.put("prf", "View and Edit");
			hashSubMenuBasicCustomer.put("wal", "View Wallet,Topup Wallet,Send Money,Cash Transactions");
			hashSubMenuBasicCustomer.put("card", "Show Cards");
			hashSubMenuBasicCustomer.put("custdspt", "View Dispute,Raise Disputes");
			hashSubMenuBasicCustomer.put("porte", "Set up Wallet,Assets,Buy Asset,Transfer Coin,Swap,Sell Asset to Fiat,Display Transactions");
			hashSubMenuBasicCustomer.put("plu", "Change Your Plan");
		}
		
		//Customers Platinum Plan Menu and Submenu generation
		if(arrMenuPlatinumCustomer==null || arrMenuIconPlatinumCustomer==null) {
			System.out.println( "---- ininitializing the Platinum Plan menu -----");
			arrMenuPlatinumCustomer = new ArrayList<String>();	arrMenuIconPlatinumCustomer = new ArrayList<String>();
			
			//Customers Platinum Plan Menu
			arrMenuPlatinumCustomer.add(0, "dash,Dashboard");					    arrMenuIconPlatinumCustomer.add(0, "side-menu__icon typcn typcn-device-desktop");
			arrMenuPlatinumCustomer.add(1, "prf,Profile");							arrMenuIconPlatinumCustomer.add(1, "side-menu__icon typcn typcn-th-large-outline");
			arrMenuPlatinumCustomer.add(2, "wal,Manage Fiat Wallet");				arrMenuIconPlatinumCustomer.add(2, "side-menu__icon ti-wallet");
			arrMenuPlatinumCustomer.add(3, "porte,Digital Assets");					arrMenuIconPlatinumCustomer.add(3, "side-menu__icon typcn typcn-briefcase");
			arrMenuPlatinumCustomer.add(4, "btc, Bitcoin");					        arrMenuIconPlatinumCustomer.add(4, "side-menu__icon fa fa-bitcoin");
			arrMenuPlatinumCustomer.add(5, "frx,Currency Remittance");				arrMenuIconPlatinumCustomer.add(5, "side-menu__icon typcn typcn-clipboard");
			//arrMenuPlatinumCustomer.add(5, "btcx, Stellar Bitcoin");				arrMenuIconPlatinumCustomer.add(5, "side-menu__icon fa fa-bitcoin");
			arrMenuPlatinumCustomer.add(6, "card,Manage Cards");					arrMenuIconPlatinumCustomer.add(6, "side-menu__icon ti-credit-card");
			arrMenuPlatinumCustomer.add(7, "custltly,Loyalty");						arrMenuIconPlatinumCustomer.add(7, "side-menu__icon typcn typcn-arrow-move-outline");
			arrMenuPlatinumCustomer.add(8, "custdspt,Disputes");					arrMenuIconPlatinumCustomer.add(8, "side-menu__icon ti-anchor");
			arrMenuPlatinumCustomer.add(9, "plu,Update Plan");						arrMenuIconPlatinumCustomer.add(9, "side-menu__icon ti-gift");		
			arrMenuPlatinumCustomer.add(10, "lgt,Logout");			            	arrMenuIconPlatinumCustomer.add(10, "side-menu__icon typcn typcn-eject-outline");	
			
			//Populate Platinum Basic Plan Sub-menu
			hashSubMenuPlatinumCustomer = new ConcurrentHashMap <String, String>(); //Strictly NO SPACES BETWEEN COMMAS
			hashSubMenuPlatinumCustomer.put("dash","Dashboard");
			hashSubMenuPlatinumCustomer.put("prf", "View and Edit");
			hashSubMenuPlatinumCustomer.put("wal", "View Wallet,Topup Wallet,Send Money,Cash Transactions");
			hashSubMenuPlatinumCustomer.put("card", "Show Cards");
			hashSubMenuPlatinumCustomer.put("custdspt", "View Dispute,Raise Disputes");
			hashSubMenuPlatinumCustomer.put("frx", "Digital Remittance,Fiat Remittance");
			hashSubMenuPlatinumCustomer.put("custltly", "Redeem Points");//Redeem Rates
			hashSubMenuPlatinumCustomer.put("porte", "Set up Wallet,Create Wallet,Bitcoin,Assets,Buy Asset,Transfer Coin,Swap,Sell Asset to Fiat,Buy BTCx using Fiat,Buy BTCx using Assets,Display Transactions");
			hashSubMenuPlatinumCustomer.put("plu", "Change Your Plan");
			//hashSubMenuPlatinumCustomer.put("btcx", "Buy BTCx using Fiat,Buy BTCx using Assets");
			hashSubMenuPlatinumCustomer.put("btc", "Register Bitcoin Account,Buy Bitcoin,Transfer Bitcoin,Swap BTC To BTCx,View Transactions");
			
		}
		
	}
	
	
	
	public static void destroyAll() {
		if(arrMenuOperations!=null) 		{ arrMenuOperations.removeAll(arrMenuOperations); 		arrMenuOperations = null;}
		if(arrMenuIconOperations!=null) 		{ arrMenuIconOperations.removeAll(arrMenuIconOperations); 		arrMenuIconOperations = null;}
		if(arrMenuTDAUser!=null) 		{ arrMenuTDAUser.removeAll(arrMenuTDAUser); 		arrMenuTDAUser = null;}
		if(arrMenuIconTDAUser!=null) 		{ arrMenuIconTDAUser.removeAll(arrMenuIconTDAUser); 		arrMenuIconTDAUser = null;}
		if(arrMenuPartners!=null) 		{ arrMenuPartners.removeAll(arrMenuPartners); 		arrMenuPartners = null;}
		if(arrMenuIconPartners!=null) 		{ arrMenuIconPartners.removeAll(arrMenuIconPartners); 		arrMenuIconPartners = null;}
		if(arrMenuFreeTireCustomer!=null) 		{ arrMenuFreeTireCustomer.removeAll(arrMenuFreeTireCustomer); 		arrMenuFreeTireCustomer = null;}
		if(arrMenuIconFreeTireCustomer!=null) 		{ arrMenuIconFreeTireCustomer.removeAll(arrMenuIconFreeTireCustomer); 		arrMenuIconFreeTireCustomer = null;}
		if(arrMenuBasicCustomer!=null) 		{ arrMenuBasicCustomer.removeAll(arrMenuBasicCustomer); 		arrMenuBasicCustomer = null;}
		if(arrMenuIconBasicCustomer!=null) 		{ arrMenuIconBasicCustomer.removeAll(arrMenuIconBasicCustomer); 		arrMenuIconBasicCustomer = null;}
		if(arrMenuPlatinumCustomer!=null) 		{ arrMenuPlatinumCustomer.removeAll(arrMenuPlatinumCustomer); 		arrMenuPlatinumCustomer = null;}
		if(arrMenuIconPlatinumCustomer!=null) 		{ arrMenuIconPlatinumCustomer.removeAll(arrMenuIconPlatinumCustomer); 		arrMenuIconPlatinumCustomer = null;}
		
		if(hashSubMenuOperations!=null) { hashSubMenuOperations.clear(); hashSubMenuOperations = null;}
		if(hashSubMenuPartners!=null) { hashSubMenuPartners.clear(); hashSubMenuPartners = null;}
		if(hashSubMenuTDAUser!=null) { hashSubMenuTDAUser.clear(); hashSubMenuTDAUser = null;}
		if(hashSubMenuBasicCustomer!=null) { hashSubMenuBasicCustomer.clear(); hashSubMenuBasicCustomer = null;}
		if(hashSubMenuPlatinumCustomer!=null) { hashSubMenuPlatinumCustomer.clear(); hashSubMenuPlatinumCustomer = null;}
	}
	
	
	public static ArrayList<String> getOperationMenu() throws Exception{          	 			return arrMenuOperations; }
	public static ArrayList<String> getOperationMenuIcons() throws Exception{          	 		return arrMenuIconOperations; }
	public static ArrayList<String> getTDAUserMenu() throws Exception{          	 			return arrMenuTDAUser; }
	public static ArrayList<String> getTDAUserMenuIcons() throws Exception{          	 		return arrMenuIconTDAUser;  }
	public static ArrayList<String> getPartnersMenu() throws Exception{          	 			return arrMenuPartners;	}
	public static ArrayList<String> getPartnersMenuIcons() throws Exception{          	 		return arrMenuIconPartners;}
	public static ArrayList<String> getFreeTireCustomerMenu() throws Exception{          	 	return arrMenuFreeTireCustomer;}
	public static ArrayList<String> getFreeTireCustomerMenuIcons() throws Exception{          	return arrMenuIconFreeTireCustomer;}
	public static ArrayList<String> getBasicCustomerMenu() throws Exception{          	 		return arrMenuBasicCustomer;}
	public static ArrayList<String> getBasicCustomerMenuIcons() throws Exception{          	 	return arrMenuIconBasicCustomer;}
	public static ArrayList<String> getPlatinumCustomerMenu() throws Exception{          	 	return arrMenuPlatinumCustomer;}
	public static ArrayList<String> getPlatinumCustomerMenuIcons() throws Exception{          	return arrMenuIconPlatinumCustomer;}
	
	public static ConcurrentHashMap <String, String> getOperationsSubMenu() throws Exception{   return hashSubMenuOperations; }
	public static ConcurrentHashMap <String, String> getPartnersSubMenu() throws Exception{    	return hashSubMenuPartners; }
	public static ConcurrentHashMap <String, String> getTDAUserSubMenu() throws Exception{    	return hashSubMenuTDAUser; }
	public static ConcurrentHashMap <String, String> getFreeTireCustomerSubMenu() throws Exception{    	return hashSubMenuFreeTireCustomer; }
	public static ConcurrentHashMap <String, String> getBasicCustomerSubMenu() throws Exception{    	return hashSubMenuBasicCustomer; }
	public static ConcurrentHashMap <String, String> getPlatinumCustomerSubMenu() throws Exception{    	return hashSubMenuPlatinumCustomer; }

}
