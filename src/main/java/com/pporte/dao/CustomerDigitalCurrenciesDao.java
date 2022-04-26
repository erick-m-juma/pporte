package com.pporte.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pporte.NeoBankEnvironment;
import com.pporte.model.AssetCoin;
import com.pporte.model.CryptoAssetCoins;
import com.pporte.model.Transaction;
import com.pporte.utilities.Utilities;

public class CustomerDigitalCurrenciesDao extends HandleConnections{
	public static String className = CustomerDigitalCurrenciesDao.class.getSimpleName();

	
	public String getStellarIssuerAccountId(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String issuerAccountId = null;
		try{			
			connection = super.getConnection();	
			query =   " select  stellar_issuer_ac from wallet_assets where asset_code=? and status=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 	while(rs.next()){	
				 		issuerAccountId = StringUtils.trim(rs.getString("stellar_issuer_ac"));
				 	}
			 }	
		}catch(Exception e){
			issuerAccountId = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getStellarIssuerAccountId  is  "+e.getMessage());
			throw new Exception ("The exception in method getStellarIssuerAccountId  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return issuerAccountId;
	}
	

	
	public String getCurrencyPartnerStellarId(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String stellarPublicKey = null;
		try{	
			connection = super.getConnection();	
			query =   " select  stellarid from partner_details where currency=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 	while(rs.next()){	
				 		stellarPublicKey = StringUtils.trim(Utilities.tripleDecryptData(rs.getString("stellarid")));
				 	}
			 }	
		}catch(Exception e){
			stellarPublicKey = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getCurrencyPartnerStellarId  is  "+e.getMessage());
			throw new Exception ("The exception in method getCurrencyPartnerStellarId  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return stellarPublicKey;
	}
	
	

	public boolean insertIntoCurrencyRemittanceQueue(String relationshipNo, String payMode, String sourceAssetCode, String destinationCurrency,
			String sourceAmount, String destinationAmount, String stellarTxnHash, String senderComment,String receiverName,String receiverBankName,
			String receiverBankCode, String receiverAccountNo, String receiverEmail,String transactionCode) throws Exception{
					PreparedStatement pstmt=null;
					Connection connection = null;
					ResultSet rs=null;
					String query = null;
					boolean result = false;
					String partnerUserId=null;
					String partnerStellarPublicKey=null;
					String systemReferenceInt=null;
					String txnUserCode=null;
					String transactionDatetime=null;
		try {
			
			systemReferenceInt = payMode + "-"+transactionCode;
			transactionDatetime= Utilities.getMYSQLCurrentTimeStampForInsert();
			txnUserCode = Utilities.generateTransactionCode(10);
			
			connection = super.getConnection();
			connection.setAutoCommit(false);
			
			query="select userid, stellarid from partner_details where currency=? and status=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, destinationCurrency);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					partnerUserId= (Utilities.tripleDecryptData(StringUtils.trim(rs.getString("userid")) ) );
					partnerStellarPublicKey= (Utilities.tripleDecryptData( StringUtils.trim(rs.getString("stellarid")))  );
					} // end of while
				NeoBankEnvironment.setComment(3,className,"  partnerUserId is  "+partnerUserId+" partnerStellarPublicKey "+partnerStellarPublicKey   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
			
			query="insert into txn_currency_remittance(txncode,sysreference_int,txnusercode,custrelno,paytype,source_assetcode,destination_currency,"
					+ "source_amount,destination_amount,partner_userid,partner_stellar_id,stellar_txnhash,sender_comment,receiver_name,"
					+ "receiver_bankname,receiver_bankcode,receiver_accountno,receiver_email,status,txndatetime, remittance_type)"
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			              //  1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20 21
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1,  transactionCode);
			pstmt.setString(2,  systemReferenceInt);
			pstmt.setString(3,  txnUserCode);
			pstmt.setString(4,  relationshipNo);
			pstmt.setString(5,  payMode);
			pstmt.setString(6,  sourceAssetCode);
			pstmt.setString(7,  destinationCurrency);
			pstmt.setString(8,  sourceAmount);
			pstmt.setString(9,  destinationAmount);
			pstmt.setString(10, partnerUserId);
			pstmt.setString(11, partnerStellarPublicKey);
			pstmt.setString(12, stellarTxnHash);
			pstmt.setString(13, (""));
			pstmt.setString(14, (receiverName));
			pstmt.setString(15, (receiverBankName));
			pstmt.setString(16, (receiverBankCode));
			pstmt.setString(17, (receiverAccountNo));
			pstmt.setString(18, (receiverEmail));
			pstmt.setString(19, "PP");// Pending Partner
			pstmt.setString(20, transactionDatetime);
			pstmt.setString(21, "D");//Digital Assets Remittance
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if(pstmt!=null)	pstmt=null;
		
			connection.commit();	result = true;

		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}

			if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(partnerUserId!=null)  partnerUserId=null;
			if(partnerStellarPublicKey!=null)  partnerStellarPublicKey=null; if(transactionCode!=null)  transactionCode=null; if(systemReferenceInt!=null)  systemReferenceInt=null;
			if(txnUserCode != null) txnUserCode = null;
			}
		return result;
	}
	
	public List<Transaction> getPendingCurrencyTradingTransactions(String relationshipNo)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Transaction transaction = null;
		List<Transaction> transactionList=null;
		try{
			
			connection = super.getConnection();	
			query = "select txnusercode,destination_currency,destination_amount,partner_stellar_id, status,txndatetime,updatetxndatetime from txn_currency_remittance\r\n"
					+ "where custrelno=?  order by txndatetime desc";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactionList = new ArrayList<Transaction>();
				 	while(rs.next()){	
				 		transaction = new Transaction();
				 		transaction.setTxnDateTime(Utilities.displayDateFormat( StringUtils.trim(rs.getString("txndatetime")), "dd MMM yy HH:mm a"));
				 		transaction.setTxnUserCode(StringUtils.trim(rs.getString("txnusercode")));
				 		transaction.setTxnCurrencyId(StringUtils.trim(rs.getString("destination_currency")));
				 		transaction.setTxnAmount(StringUtils.trim(rs.getString("destination_amount")));
				 		transaction.setPublicKey(StringUtils.trim(rs.getString("partner_stellar_id")));
				 		transaction.setStatus(StringUtils.trim(rs.getString("status")));
				 		//transaction.setTxnDateTime(Utilities.displayDateFormat( StringUtils.trim(rs.getString("updatetxndatetime")), "dd MMM yy HH:mm a"));
				 		transactionList.add(transaction);
				 	}
			 }
			 if(transactionList!=null)
				 if(transactionList.size()==0)
					 transactionList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return transactionList;
	}



	public ArrayList<CryptoAssetCoins> getBTCxDetails() throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CryptoAssetCoins porteCoin = null;
		ArrayList<CryptoAssetCoins> porteCoinsList = null;
		try{
			connection = super.getConnection();	
			query =   " select  asset_code, asset_desc, status, asset_type, wallettype from wallet_assets"
					+ " where asset_type=? and status=? and asset_code=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P");
			pstmt.setString(2, "A");
			pstmt.setString(3, "BTCx");
			rs = (ResultSet)pstmt.executeQuery();
	
			 if(rs!=null){
				 porteCoinsList = new ArrayList<CryptoAssetCoins>();
				 	while(rs.next()){	
				 		porteCoin = new CryptoAssetCoins();
				 		porteCoin.setWalletType(StringUtils.trim(rs.getString("wallettype")));
				 		porteCoin.setAssetCode(StringUtils.trim(rs.getString("asset_code")));
				 		porteCoin.setAssetDescription(StringUtils.trim(rs.getString("asset_desc")));
				 		porteCoin.setAssetType(StringUtils.trim(rs.getString("asset_type")));
				 		porteCoinsList.add(porteCoin);
				 	}
			 }	
			 
			 if(porteCoinsList!=null)
			 if(porteCoinsList.size()==0)
				 porteCoinsList=null;
				
		}catch(Exception e){
			porteCoinsList = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());		
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return porteCoinsList;
	}
	
	public ArrayList<CryptoAssetCoins> getSourceAssetDetails() throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CryptoAssetCoins porteCoin = null;
		ArrayList<CryptoAssetCoins> porteCoinsList = null;
		try{
			connection = super.getConnection();	
			query =   " select  asset_code, asset_desc, status, asset_type, wallettype from wallet_assets where asset_type=? "
					+ " and status=? and asset_code=? or asset_code=? or asset_code=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P");
			pstmt.setString(2, "A");
			pstmt.setString(3, "VESL");
			pstmt.setString(4, "PORTE");
			pstmt.setString(5, "XLM");
			rs = (ResultSet)pstmt.executeQuery();
	
			 if(rs!=null){
				 porteCoinsList = new ArrayList<CryptoAssetCoins>();
				 	while(rs.next()){	
				 		porteCoin = new CryptoAssetCoins();
				 		porteCoin.setWalletType(StringUtils.trim(rs.getString("wallettype")));
				 		porteCoin.setAssetCode(StringUtils.trim(rs.getString("asset_code")));
				 		porteCoin.setAssetDescription(StringUtils.trim(rs.getString("asset_desc")));
				 		porteCoin.setAssetType(StringUtils.trim(rs.getString("asset_type")));
				 		porteCoinsList.add(porteCoin);
				 	}
			 }	
			 
			 if(porteCoinsList!=null)
			 if(porteCoinsList.size()==0)
				 porteCoinsList=null;
				
		}catch(Exception e){
			porteCoinsList = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());		
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return porteCoinsList;
	}
	
	
	public Boolean buyBTCxViaFiatWallet(String relationshipNo, String fiatWalletId, String amountInUSD,
			String payComments, String referenceNo, String txnUserCode, String txnPayMode, String assetCode,
			String extSystemRef, String BTCxWalletId,
			String customerCharges, String BTCxAmount,String fiatTransactionCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String transactionDatetime = null;
		String currencyId= assetCode;
		String fiatWalletTxnMode = "D";
		String btcWalletTxnMode = "C";
		String fiatWalletTransactionCode2 = null;
		String btcWalletTransactionCode = null;
		String userType = "C";
		String systemreference =referenceNo;
		String totalAccruedBalance=null;
		try {
			String customerChargesValue = customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|"));
			String customerPayType = customerCharges.substring(0, customerCharges.indexOf(","));

			amountInUSD = amountInUSD.replaceAll(",", "");
			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
			fiatWalletTransactionCode2 = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			btcWalletTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
		    connection = super.getConnection();
		    connection.setAutoCommit(false);
		    
		    //********Step 1*********\\\\	
			 double fiatWalletDebitAmount =  Double.parseDouble(amountInUSD) + Double.parseDouble(customerChargesValue);
			 
			 //*******Step 2: Update the Fiat wallet 
		     query = " update wallet_details set currbal= currbal - ?, lastupdated = ? where  walletid=? ";
			 pstmt = connection.prepareStatement(query);
			 pstmt.setBigDecimal(1,BigDecimal.valueOf(fiatWalletDebitAmount));
			 pstmt.setString(2, transactionDatetime); 
			 pstmt.setString(3, fiatWalletId); 
			 try {
				 NeoBankEnvironment.setComment(3,className,"update Fiat wallet amount" + fiatWalletDebitAmount );
				 pstmt.executeUpdate();
				 }catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
			 }					
			 if( pstmt!=null)	pstmt.close();
			
			// **********Step 3: Record the wallet transaction ledger
			
													// 1		2			3			4			5				6		7			8
			query = "insert into txn_wallet_cust_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel) "
			+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?) ";
					// 1  2  3  4  5  6   7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, fiatTransactionCode); 			
			pstmt.setString(2, fiatWalletId); 				
			pstmt.setString(3, systemreference );							
			pstmt.setBigDecimal(4, new BigDecimal(amountInUSD));  
			pstmt.setString(5, NeoBankEnvironment.getUSDCurrencyId());

			pstmt.setString(6, fiatWalletTxnMode); // Debit as it is a payment by the sender
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, txnUserCode);
			pstmt.setString(9, "W");
			try {
				NeoBankEnvironment.setComment(3,className,"Executed Sender wallet_tnx_bc amount"+"fiatTransactionCode is"+ fiatTransactionCode +"fiatWalletId is"+fiatWalletId 
				+"systemreference is"+systemreference +"payAmount is"+amountInUSD +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if( pstmt!=null)	pstmt.close();
			
			// **********Step 3: credit to business ledger

			//Select the balance to be updated. 	
			query = "select accrued_balance, sequenceid from txn_business_ledger_bc order by sequenceid desc limit 1";
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					 totalAccruedBalance= ( StringUtils.trim(rs.getString("accrued_balance"))  );
					} // end of while
				NeoBankEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalAccruedBalance   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
			//Credit to business ledger
			//											   1		2			3			4				5				6			7			8		   9          10            11
			query = "insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, btcWalletTransactionCode); 
			pstmt.setString(2, txnPayMode);
			pstmt.setString(3, fiatWalletId);
			pstmt.setString(4, "");
			pstmt.setString(5, systemreference);			// Additional charges for customer						
			pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(amountInUSD)));  
			pstmt.setString(7, NeoBankEnvironment.getUSDCurrencyId());

			pstmt.setString(8, "C"); // Credit 
			pstmt.setString(9, transactionDatetime);
			
			if (totalAccruedBalance!=null) {
				pstmt.setBigDecimal(10,BigDecimal.valueOf( (Double.parseDouble(totalAccruedBalance)+Double.parseDouble(amountInUSD) )));
			}else {
				pstmt.setBigDecimal(10,BigDecimal.valueOf( Double.parseDouble(amountInUSD) ));
			}
			pstmt.setString(11, "W");
			try {
					pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query +" "+e.getMessage());
				}					
			if( pstmt!=null)pstmt.close();
			
			
			// **********Step 4 add to Buy coins transaction table
			//			                           1		2			3			4		  5				6			   7			   8		 9          10      11
			query = "insert into txn_buy_coins (txn_code, walletid, custrelno, asset_code, txnamount, sysreference_ext, sysreference_int, status, txndate, coin_amount, commet ) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
					// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, btcWalletTransactionCode); 
			pstmt.setString(2, BTCxWalletId);
			pstmt.setString(3, relationshipNo);
			pstmt.setString(4, assetCode);
			pstmt.setBigDecimal(5, BigDecimal.valueOf( Double.parseDouble(amountInUSD)));
			pstmt.setString(6, "");								
			pstmt.setString(7, systemreference);								
			pstmt.setString(8, "N"); 
			pstmt.setString(9, transactionDatetime);
			pstmt.setString(10, BTCxAmount);
			pstmt.setString(11, payComments);
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query +" "+e.getMessage());
			}					
			if( pstmt!=null)pstmt.close();
						
			// **********Step 5 customer charges
			if(Double.parseDouble(customerChargesValue)>0) {
				query = "insert into txn_wallet_cust_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel) "
						+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?) ";
								// 1  2  3  4  5  6   7  8
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, fiatWalletTransactionCode2); 			
				pstmt.setString(2, fiatWalletId); 				
				pstmt.setString(3, systemreference+"-AC" );							
				pstmt.setBigDecimal(4, new BigDecimal(Double.parseDouble(customerChargesValue)));  
				pstmt.setString(5, NeoBankEnvironment.getUSDCurrencyId());
				pstmt.setString(6, customerPayType); 
				pstmt.setString(7, transactionDatetime);
				pstmt.setString(8, txnUserCode);
				pstmt.setString(9, "W");
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}					
					pstmt.close();	
				if(pstmt!=null)			
					pstmt=null;					
				
				//Select the balance to be updated. 	
				query = "select accrued_balance, sequenceid from txn_business_ledger_bc order by sequenceid desc limit 1";
				pstmt = connection.prepareStatement(query);
				rs = (ResultSet)pstmt.executeQuery();
				if(rs!=null){
					while(rs.next()){	 			 			
						 totalAccruedBalance= ( StringUtils.trim(rs.getString("accrued_balance"))  );
						} // end of while
					NeoBankEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalAccruedBalance   );
				} 
				if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
				

				//
             //												   1		2			3			4				5				6			7			8		   9          10            11
				query = "insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
								// 1  2  3  4  5  6  7  8  9  10 11
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, fiatWalletTransactionCode2); 
				pstmt.setString(2, txnPayMode);
				pstmt.setString(3, fiatWalletId);
				pstmt.setString(4, "");
				pstmt.setString(5, systemreference+"-AC" );			// Additional charges for customer						
				pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(customerChargesValue)));  
				pstmt.setString(7, NeoBankEnvironment.getUSDCurrencyId());

				pstmt.setString(8, "C"); // Credit 
				pstmt.setString(9, transactionDatetime);
				
				if (totalAccruedBalance!=null) {
					pstmt.setBigDecimal(10,BigDecimal.valueOf( (Double.parseDouble(totalAccruedBalance)+Double.parseDouble(customerChargesValue) )));
				}else {
					pstmt.setBigDecimal(10,BigDecimal.valueOf( Double.parseDouble(customerChargesValue) ));
				}
				pstmt.setString(11, "W");
				try {
						pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query +" "+e.getMessage());
					}					
				if( pstmt!=null)pstmt.close();
			}
			
			
			// Step 6.1: Get the Loyalty Rules based on the type of transaction and conversion
			String pointsConversion = null;
			String previousPoinsBalance = null;
			boolean firstime = false;
			String pointsaccrued =null;
			query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, txnPayMode);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				 while(rs.next()){	 			 			
				 	pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
				 } // end of while
			} //end of if rs!=null check 
			if( pstmt!=null)	pstmt.close();
			if(pointsConversion!=null) {
				query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, relationshipNo);
				rs = (ResultSet)pstmt.executeQuery();
				 if(rs!=null){
				 	while(rs.next()){	 			 			
				 		previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
				 		} // end of while
				} //end of if rs!=null check
				 if( pstmt!=null)	pstmt.close();
				NeoBankEnvironment.setComment(3,className,"*****Points Balance for relationshipNo   "+ relationshipNo + "  is " + previousPoinsBalance );
				
				pointsaccrued = Double.toString((Double.parseDouble(amountInUSD) * Double.parseDouble(pointsConversion)));
				NeoBankEnvironment.setComment(3,className,"********* pointsaccrued   "+ pointsaccrued + " amount "+ amountInUSD  +" pointsConversion "+pointsConversion);
			   if(previousPoinsBalance==null) { // For the first time 
				  firstime =true;
				  NeoBankEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +fiatWalletId); 
			 
			   }else {
				   NeoBankEnvironment.setComment(3,	className," Previous Point balance for user : " +fiatWalletId+ " is  " +previousPoinsBalance); 
			   }	 

				//***** Step 6.2****: Insert loyalty points for the sender user		
				 
						                             //	  	1		2				3			4			5			6				7        8        9
				query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, status, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?,?) ";
							//		   1  2  3  4  5  6  7  8 9
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, fiatWalletId); 	
				pstmt.setString(2, relationshipNo);
				pstmt.setString(3, "C"); 						// 
				pstmt.setString(4, txnPayMode);	
				pstmt.setString(5,systemreference ); // transaction reference generated by the system
				pstmt.setBigDecimal(6, new BigDecimal(pointsaccrued));  // pointaccrued
				if(firstime)
				pstmt.setBigDecimal(7, new BigDecimal(pointsaccrued));  // pointaccrued
				else {
					pstmt.setBigDecimal(7, BigDecimal.valueOf(  (Double.parseDouble(pointsaccrued) + Double.parseDouble(previousPoinsBalance ) ) )  );  // pointbalance
				}
				pstmt.setString(8,"U" );	
				pstmt.setString(9,transactionDatetime );	
				try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}							 
			 }
			 NeoBankEnvironment.setComment(3,	className,"inserting into loyalyty  pointsaccrued " + pointsaccrued ); 
			 
			 
		 	if(CustomerWalletDao.insertGenericWalletTransactions(fiatTransactionCode, fiatWalletId,txnUserCode, systemreference, amountInUSD, NeoBankEnvironment.getUSDCurrencyId(),fiatWalletTxnMode, transactionDatetime ,userType) == false) {
				throw new Exception ("Blockchain insert issue");
			}
			if(Double.parseDouble(customerChargesValue)>0) {
				if(	CustomerWalletDao.insertGenericWalletTransactions(fiatWalletTransactionCode2, fiatWalletId,txnUserCode, systemreference+"-AC", customerChargesValue, NeoBankEnvironment.getUSDCurrencyId(),customerPayType, transactionDatetime ,userType) == false) {
					throw new Exception ("Blockchain insert issue");
				}
			}

			connection.commit();
			result = true;
			
		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
	
			if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(btcWalletTransactionCode!=null)  btcWalletTransactionCode=null;
			if(userType!=null)  userType=null; if(fiatTransactionCode!=null)  fiatTransactionCode=null; if(fiatWalletTransactionCode2!=null)  fiatWalletTransactionCode2=null; if(txnUserCode != null) txnUserCode = null;
			if(systemreference!=null)  systemreference=null; if(currencyId!=null)  currencyId=null; if ( totalAccruedBalance!=null) totalAccruedBalance=null;
			if(btcWalletTxnMode!=null)  btcWalletTxnMode=null; if ( fiatWalletTxnMode!=null) fiatWalletTxnMode=null;
		
			}
		
		return result;
		
	}
	
	public Boolean buyBTCxCoinViaToken(String relationshipNo, String tokenId, String amountInUSD, String payComments,
			String referenceNo, String txnUserCode, String customerCharges, String txnPayMode, String assetCode,
			String extSystemRef, String btcWalletId, String amountInBTC) throws Exception {
		NeoBankEnvironment.setComment(3, className, "tokenId is "+tokenId);
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String transactionDatetime = null;
		String currencyId= assetCode;
		String transactionCode = null;
		String transactionCode2 = null;
		String userType = "C";
		String systemreference =referenceNo;
		String senderRefNo = null;
		String totalAccruedBalance=null;
		try {
			String customerChargesValue = customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|"));
			//String customerPayType = customerCharges.substring(0, customerCharges.indexOf(","));
			amountInUSD = amountInUSD.replaceAll(",", "");
			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
			transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			transactionCode2 = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			connection = super.getConnection();
			connection.setAutoCommit(false);
			//********Step 1 update wallet ballance*********\\\\	
						
												//    1        2         3         4              5                    6             7       8         9          10                 11        
			query = "insert into txn_cardtoken_bc (txncode, tokenid, txnusercode, sysreference_ext, sysreference_int, txnamount, custrelno, paymode, txndatetime, txncurrencyid ) "
			+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?  ) ";
			// 1  2  3  4  5  6   7	 8  9  10 11 
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 			
			pstmt.setString(2, tokenId); 										
			pstmt.setString(3, txnUserCode);							
			pstmt.setString(4, extSystemRef);  // External 
			pstmt.setString(5, systemreference); // Internal generated
			pstmt.setBigDecimal(6, new BigDecimal(amountInUSD));
			pstmt.setString(7, relationshipNo); 
			pstmt.setString(8,  txnPayMode);
			pstmt.setString(9,  transactionDatetime );
			pstmt.setString(10,  NeoBankEnvironment.getUSDCurrencyId() );

			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if( pstmt!=null)		pstmt.close();	

			// **********Step 5.1: credit to business ledger

			//Select the balance to be updated. 	
			query = "select accrued_balance, sequenceid from txn_business_ledger_bc order by sequenceid desc limit 1";
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					 totalAccruedBalance= ( StringUtils.trim(rs.getString("accrued_balance"))  );
					} // end of while
				NeoBankEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalAccruedBalance   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
			//Credit to business ledger
			//											   1		2			3			4				5				6			7			8		   9          10            11

			query = " insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "

					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 
			pstmt.setString(2, txnPayMode);
			pstmt.setString(3, btcWalletId);
			pstmt.setString(4, "");
			pstmt.setString(5, systemreference);			// Additional charges for customer						
			pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(amountInUSD)));  
			pstmt.setString(7, NeoBankEnvironment.getUSDCurrencyId());
			pstmt.setString(8, "C"); // Credit 
			pstmt.setString(9, transactionDatetime);
			
			if (totalAccruedBalance!=null) {
				pstmt.setBigDecimal(10,BigDecimal.valueOf( (Double.parseDouble(totalAccruedBalance)+Double.parseDouble(amountInUSD) )));
			}else {
				pstmt.setBigDecimal(10,BigDecimal.valueOf( Double.parseDouble(amountInUSD) ));
			}
			pstmt.setString(11, "W");
			try {
					pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query +" "+e.getMessage());
				}					
			if( pstmt!=null)pstmt.close();
			
			
			// **********Step 5.2 add to Buy coins transaction table
			//			                           1		2			3			4		  5				6			   7			   8		 9          10      11
			query = "insert into txn_buy_coins (txn_code, walletid, custrelno, asset_code, txnamount, sysreference_ext, sysreference_int, status, txndate, coin_amount, commet ) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
					// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 
			pstmt.setString(2, btcWalletId);
			pstmt.setString(3, relationshipNo);
			pstmt.setString(4, assetCode);
			pstmt.setBigDecimal(5, BigDecimal.valueOf( Double.parseDouble(amountInUSD)));
			pstmt.setString(6, "");								
			pstmt.setString(7, systemreference);								
			pstmt.setString(8, "N"); 
			pstmt.setString(9, transactionDatetime);
			pstmt.setString(10, amountInBTC);
			pstmt.setString(11, payComments);
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query +" "+e.getMessage());
			}					
			if( pstmt!=null)pstmt.close();
			
			//**********Step 4: Record the charges to transaction ledger
			if(Double.parseDouble(customerChargesValue) > 0 ) {

								//    1        2         3         4              5                    6             7       8         9          10                 11        
				query = "insert into txn_cardtoken_bc (txncode, tokenid, txnusercode, sysreference_ext, sysreference_int, txnamount, custrelno, paymode, txndatetime, txncurrencyid ) "
				+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?  ) ";
				// 1  2  3  4  5  6   7	 8  9  10 11 
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode2); 			
				pstmt.setString(2, tokenId); 										
				pstmt.setString(3, txnUserCode);							
				pstmt.setString(4, extSystemRef);  // External 
				pstmt.setString(5, systemreference+"-AC"); // Internal generated
				pstmt.setBigDecimal(6, new BigDecimal(customerChargesValue));
				pstmt.setString(7, relationshipNo); 
				pstmt.setString(8,  txnPayMode);
				pstmt.setString(9,  transactionDatetime );
				pstmt.setString(10,  currencyId );
				try {
					pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}					
				if( pstmt!=null)		pstmt.close();	
								
				query = "select accrued_balance, sequenceid from txn_business_ledger_bc order by sequenceid desc limit 1";
				pstmt = connection.prepareStatement(query);
				rs = (ResultSet)pstmt.executeQuery();
				if(rs!=null){
					while(rs.next()){	 			 			
						 totalAccruedBalance= ( StringUtils.trim(rs.getString("accrued_balance"))  );
						} // end of while
					NeoBankEnvironment.setComment(3,className,"step 1   total Business AccruedBalance is  "+totalAccruedBalance   );
				} 
				if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
				
				
             //												   1		2			3			4				5				6			7			8		   9          10            11
				query = "insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
								// 1  2  3  4  5  6  7  8  9  10 11
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode2); 
				pstmt.setString(2, txnPayMode);
				pstmt.setString(3, btcWalletId);
				pstmt.setString(4, "");
				pstmt.setString(5, systemreference+"-AC" );			// Additional charges for customer						
				pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(customerChargesValue)));  
				pstmt.setString(7, currencyId);
				pstmt.setString(8, "C"); // Credit 
				pstmt.setString(9, transactionDatetime);
				if (totalAccruedBalance!=null) {
					pstmt.setBigDecimal(10,BigDecimal.valueOf( (Double.parseDouble(totalAccruedBalance)+Double.parseDouble(customerChargesValue) )));
				}else {
					pstmt.setBigDecimal(10,BigDecimal.valueOf( Double.parseDouble(customerChargesValue) ));
				}
				pstmt.setString(11, "W");
				try {
						pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query +" "+e.getMessage());
					}					
				if( pstmt!=null) pstmt.close();
		}
			
		// Step 6.1: Get the Loyalty Rules based on the type of transaction and conversion
			String pointsConversion = null;
			String previousPoinsBalance = null;
			boolean firstime = false;
			String pointsaccrued =null;
			query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, txnPayMode);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				 while(rs.next()){	 			 			
				 	pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
				 } // end of while
			} //end of if rs!=null check 
			if( pstmt!=null)	pstmt.close();
			if(pointsConversion!=null) {
				query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, relationshipNo);
				rs = (ResultSet)pstmt.executeQuery();
				 if(rs!=null){
				 	while(rs.next()){	 			 			
				 		previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
				 		} // end of while
				} //end of if rs!=null check
				 if( pstmt!=null)	pstmt.close();
				NeoBankEnvironment.setComment(3,className,"************ Points Balance for relationshipNo   "+ relationshipNo + "  is " + previousPoinsBalance );
				pointsaccrued = Double.toString((Double.parseDouble(amountInUSD) * Double.parseDouble(pointsConversion)));
				NeoBankEnvironment.setComment(3,className,"******** pointsaccrued   "+ pointsaccrued +" amountInUSD "+ amountInUSD + " pointsConversion "+ pointsConversion);
			   if(previousPoinsBalance==null) { // For the first time 
				  firstime =true;
				  NeoBankEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +btcWalletId); 
			 
			   }else {
				   NeoBankEnvironment.setComment(3,	className," Previous Point balance for user : " +btcWalletId+ " is  " +previousPoinsBalance); 
			   }	 

				//***** Step 6.2****: Insert loyalty points for the sender user		
				 
						                             //	  	1		2				3			4			5			6				7        8        9
				query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, status, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?,?) ";
							//		   1  2  3  4  5  6  7  8 9
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, btcWalletId); 	
				pstmt.setString(2, relationshipNo);
				pstmt.setString(3, "C"); 						// 
				pstmt.setString(4, txnPayMode);	
				pstmt.setString(5,systemreference ); // transaction reference generated by the system
				pstmt.setBigDecimal(6, new BigDecimal(pointsaccrued));  // pointaccrued
				if(firstime)
					pstmt.setBigDecimal(7, new BigDecimal(pointsaccrued));  // pointaccrued
				else {
					pstmt.setBigDecimal(7, BigDecimal.valueOf(  (Double.parseDouble(pointsaccrued) + Double.parseDouble(previousPoinsBalance ) ) )  );  // pointbalance
				}
				pstmt.setString(8,"U" );	
				pstmt.setString(9,transactionDatetime );	
				try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}							 
			 }
			 NeoBankEnvironment.setComment(3,	className,"inserting into loyalyty  pointsaccrued " + pointsaccrued ); 
			 
			 
			
			connection.commit();
			result = true;
			
		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; 
			if(userType!=null)  userType=null; if(txnUserCode != null) txnUserCode = null;
			if(systemreference!=null)  systemreference=null; if(senderRefNo!=null)  senderRefNo=null; 
			if ( totalAccruedBalance!=null) totalAccruedBalance=null;
		}
		return result;
	}
	
	public List<Transaction> getPendingBTCxTransactions(String realationshipNo)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Transaction transaction = null;
		List<Transaction> transactionList=null;
		try{
			
			connection = super.getConnection();	
			query = " select txn_code,  walletid,  custrelno,  asset_code, "
					+ " coin_amount,  sysreference_int, txndate, status from txn_buy_coins "
					+ " where custrelno = ? and status = ? and asset_code=? order by txndate desc ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, realationshipNo);
			pstmt.setString(2, "N");
			pstmt.setString(3, "BTCx");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactionList = new ArrayList<Transaction>();
				 	while(rs.next()){	
				 		transaction = new Transaction();
				 		transaction.setTxnDateTime(Utilities.displayDateFormat( StringUtils.trim(rs.getString("txndate")), "dd MMM yy HH:mm a"));
				 		transaction.setTxnCode(StringUtils.trim(rs.getString("txn_code")));
				 		transaction.setCustomerWalletId(StringUtils.trim(rs.getString("walletid")));
				 		transaction.setRelationshipNo(StringUtils.trim(rs.getString("custrelno")));
				 		transaction.setAssetCode(StringUtils.trim(rs.getString("asset_code")));
				 		transaction.setCoinAmount(StringUtils.trim(rs.getString("coin_amount")));
				 		transaction.setStatus(StringUtils.trim(rs.getString("status")));
				 		transactionList.add(transaction);
				 	}
			 }
			 if(transactionList!=null)
				 if(transactionList.size()==0)
					 transactionList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getPendingTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getPendingTransactions  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return transactionList;
	}
	
	public List<Transaction> getPendingBTCxSwapping(String realationshipNo)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Transaction transaction = null;
		List<Transaction> transactionList=null;
		try{
			
			connection = super.getConnection();	
			query = " select txncode,  paytype,  custrelno,  source_asset, "
					+ " source_amount,  destination_amount, destination_asset, sysreference_ext, "
					+ " sysreference_int, txndate from txn_btc_exchange "
					+ " where custrelno = ? and status = ? order by txndate desc ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, realationshipNo);
			pstmt.setString(2, "N");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactionList = new ArrayList<Transaction>();
				 	while(rs.next()){	
				 		transaction = new Transaction();
				 		transaction.setTxnDateTime(Utilities.displayDateFormat( StringUtils.trim(rs.getString("txndate")), "dd MMM yy HH:mm a"));
				 		transaction.setSourceAssetCode(StringUtils.trim(rs.getString("source_asset")));
				 		transaction.setTxnCode(StringUtils.trim(rs.getString("txncode")));
				 		transaction.setSourceAmount(StringUtils.trim(rs.getString("source_amount")));
				 		transaction.setDestinationAmount(StringUtils.trim(rs.getString("destination_amount")));
				 		transaction.setDestinationAssetCode(StringUtils.trim(rs.getString("destination_asset")));
				 		transaction.setSystemReferenceExt(StringUtils.trim(rs.getString("sysreference_ext")));
				 		transaction.setSystemReferenceInt(StringUtils.trim(rs.getString("sysreference_int")));
				 		transaction.setRelationshipNo(StringUtils.trim(rs.getString("custrelno")));
				 		transactionList.add(transaction);
				 	}
			 }
				NeoBankEnvironment.setComment(3,className,"listTxn size is "+transactionList.size());

			 if(transactionList!=null)
				 if(transactionList.size()==0)
					 transactionList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return transactionList;
	}

	public List<Transaction> getMobilePendingBTCxSwapping(String realationshipNo)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Transaction transaction = null;
		List<Transaction> transactionList=null;
		try{
			
			connection = super.getConnection();	
			query = " select txncode,  paytype,  custrelno,  source_asset, "
					+ " source_amount,  destination_amount, destination_asset, sysreference_ext, "
					+ " sysreference_int, txndate from txn_btc_exchange "
					+ " where custrelno = ? and status = ? order by txndate desc limit 10 ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, realationshipNo);
			pstmt.setString(2, "N");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactionList = new ArrayList<Transaction>();
				 	while(rs.next()){	
				 		transaction = new Transaction();
				 		transaction.setTxnDateTime(Utilities.displayDateFormat( StringUtils.trim(rs.getString("txndate")), "dd MMM yy HH:mm a"));
				 		transaction.setSourceAssetCode(StringUtils.trim(rs.getString("source_asset")));
				 		transaction.setTxnCode(StringUtils.trim(rs.getString("txncode")));
				 		transaction.setSourceAmount(StringUtils.trim(rs.getString("source_amount")));
				 		transaction.setDestinationAmount(StringUtils.trim(rs.getString("destination_amount")));
				 		transaction.setDestinationAssetCode(StringUtils.trim(rs.getString("destination_asset")));
				 		transaction.setSystemReferenceExt(StringUtils.trim(rs.getString("sysreference_ext")));
				 		transaction.setSystemReferenceInt(StringUtils.trim(rs.getString("sysreference_int")));
				 		transaction.setRelationshipNo(StringUtils.trim(rs.getString("custrelno")));
				 		transactionList.add(transaction);
				 	}
			 }
			 if(transactionList!=null)
				 if(transactionList.size()==0)
					 transactionList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}		
		return transactionList;
	}
	
	public ArrayList<AssetCoin> getFiatCurrencyCodes() throws Exception {
		PreparedStatement pstmt = null;
		Connection connection = null;
		ResultSet rs = null;
		String query = null;
		ArrayList<AssetCoin> arrAssetCoins = null;
		try {
			connection = super.getConnection();
			query = " select asset_code, asset_desc, status, asset_type, wallettype from  "
					+ "wallet_assets where asset_type= ? and wallettype= ? and status=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "C"); 
			pstmt.setString(2, "F"); 
			pstmt.setString(3, "A"); 
			rs = (ResultSet) pstmt.executeQuery();
			if (rs != null) {
				arrAssetCoins = new ArrayList<AssetCoin>();
				while (rs.next()) {
					AssetCoin m_AssetCoin = new AssetCoin();
					m_AssetCoin.setAssetCode(StringUtils.trim(rs.getString("asset_code")));
					m_AssetCoin.setAssetDescription(StringUtils.trim(rs.getString("asset_desc")));
					m_AssetCoin.setStatus(StringUtils.trim(rs.getString("status")));
					m_AssetCoin.setAssetType(StringUtils.trim(rs.getString("asset_type")));
					m_AssetCoin.setWalletType(StringUtils.trim(rs.getString("wallettype")));
					arrAssetCoins.add(m_AssetCoin);
				} // end of while

			} // end of if rs!=null check
			/*
			 * if (arrAssetCoins != null) if (arrAssetCoins.size() == 0) arrAssetCoins =
			 * null;
			 */
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		} finally {
			if (connection != null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) rs.close();
			if (pstmt != null) pstmt.close();
		}
		return arrAssetCoins;
	}
	
	public boolean fiatCurrencyRemittance(String relationship, String walletId, 
			String remmitAmount, String referenceNo, String txnUserCode, String customerCharges, String transactionCode,
			String txnType
			) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			boolean result = false;
			String transactionDatetime = null;
			String currencyId= NeoBankEnvironment.getUSDCurrencyId();
			String userTxnMode = "D";
			String businessTxnMode = "C";
			String transactionCode2 = null;
			String userType = "C";
			String systemreference =referenceNo;
			String totalAccruedBalance=null;
			try {	
				 connection = super.getConnection();
				 connection.setAutoCommit(false);

				String customerChargesValue = customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|"));
				String customerPayType = customerCharges.substring(0, customerCharges.indexOf(","));

				remmitAmount = remmitAmount.replaceAll(",", "");
				transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
				SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
				transactionCode2 = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
				
				 
				 
				 double userDebitAmount =  Double.parseDouble(remmitAmount) + Double.parseDouble(customerChargesValue);
				 
				 //*******Step 1: Update the User wallet Ledger
			     query = " update wallet_details set currbal= currbal - ?, lastupdated = ? where  walletid=? ";
				 pstmt = connection.prepareStatement(query);
				 pstmt.setBigDecimal(1,BigDecimal.valueOf(userDebitAmount));
				 pstmt.setString(2, transactionDatetime); 
				 pstmt.setString(3, walletId); 
				 try {
					 NeoBankEnvironment.setComment(3,className,"update user wallet amount" + walletId );
					 pstmt.executeUpdate();
					 }catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
				}					
				pstmt.close();
				
					
				// **********Step 2: Record the wallet transaction ledger
				
								// 1		2			3			4			5		6		7				8      		 9				10
				query = "insert into txn_wallet_cust_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode,txncomment, txndatetime, txnusercode, pymtchannel) "
						+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?,?) ";
								// 1  2  3  4  5  6   7  8, 9,10
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 			
				pstmt.setString(2, walletId); 				
				pstmt.setString(3, systemreference );							
				pstmt.setBigDecimal(4, new BigDecimal(remmitAmount));  
				pstmt.setString(5, currencyId);
				pstmt.setString(6, userTxnMode); // Debit as it is a payment by the sender
				pstmt.setString(7, ""); 
				pstmt.setString(8, transactionDatetime);
				pstmt.setString(9, txnUserCode);
				pstmt.setString(10, "W");
				try {
					NeoBankEnvironment.setComment(3,className,"Executed Sender wallet_tnx_bc amount"+"senderTransactioncode is"+ transactionCode +"walletId is"+walletId 
							+"systemreference is"+systemreference +"remmitAmount is"+remmitAmount +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}					
					pstmt.close();	
				if(pstmt!=null)			
					pstmt=null;
				
				
				// **********Step 3: Record txn Charges
				if(Double.parseDouble(customerChargesValue)>0) {
					query = "insert into txn_wallet_cust_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel) "
							+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?) ";
									// 1  2  3  4  5  6   7  8
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode2); 			
					pstmt.setString(2, walletId); 				
					pstmt.setString(3, systemreference+"-AC" );							
					pstmt.setBigDecimal(4, new BigDecimal(Double.parseDouble(customerChargesValue)));  
					pstmt.setString(5, currencyId);
					pstmt.setString(6, customerPayType); //Debit as it is a payment by the sender
					pstmt.setString(7, transactionDatetime);
					pstmt.setString(8, txnUserCode);
					pstmt.setString(9, "W");
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}					
						pstmt.close();	
					if(pstmt!=null)			
						pstmt=null;					
					
					   //Select the balance to be updated. 
					
					query = " select accrued_balance, sequenceid from txn_business_ledger_bc order by sequenceid desc limit 1 ";
					pstmt = connection.prepareStatement(query);
					rs = (ResultSet)pstmt.executeQuery();
					if(rs!=null){
						while(rs.next()){	 			 			
							 totalAccruedBalance= ( StringUtils.trim(rs.getString("accrued_balance"))  );
							} // end of while
						NeoBankEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalAccruedBalance   );
					} 
					if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
					

					//
                 //												   1		2			3			4				5				6			7			8		   9          10            11
					query = "insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
									// 1  2  3  4  5  6  7  8  9  10 11
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode2); 
					pstmt.setString(2, txnType);
					pstmt.setString(3, walletId);
					pstmt.setString(4, "");
					pstmt.setString(5, systemreference+"-AC" );			// Additional charges for customer						
					pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(customerChargesValue)));  
					pstmt.setString(7, currencyId);
					pstmt.setString(8, "C"); // Credit 
					pstmt.setString(9, transactionDatetime);
					
					if (totalAccruedBalance!=null) {
						pstmt.setBigDecimal(10,BigDecimal.valueOf( (Double.parseDouble(totalAccruedBalance)+Double.parseDouble(customerChargesValue) )));
					}else {
						pstmt.setBigDecimal(10,BigDecimal.valueOf( Double.parseDouble(customerChargesValue) ));
					}
					pstmt.setString(11, "W");
					try {
							pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query +" "+e.getMessage());
						}					
					if( pstmt!=null)				
						pstmt.close();
				}
				
				// *********Step 4*****Update Receiver Wallet 
				//Select the balance to be updated. 
				
				query = " select accrued_balance, sequenceid from txn_business_ledger_bc order by sequenceid desc limit 1 ";
				pstmt = connection.prepareStatement(query);
				rs = (ResultSet)pstmt.executeQuery();
				if(rs!=null){
					while(rs.next()){	 			 			
						 totalAccruedBalance= ( StringUtils.trim(rs.getString("accrued_balance"))  );
						} // end of while
					NeoBankEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalAccruedBalance   );
				} 
				if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
				

				//
             //												   1		2			3			4				5				6			7			8		   9          10            11
				query = "insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
								// 1  2  3  4  5  6  7  8  9  10 11
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 
				pstmt.setString(2, txnType);
				pstmt.setString(3, walletId);
				pstmt.setString(4, "");
				pstmt.setString(5, systemreference+"-AC" );			// Additional charges for customer						
				pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(remmitAmount)));  
				pstmt.setString(7, currencyId);
				pstmt.setString(8, businessTxnMode); // Credit 
				pstmt.setString(9, transactionDatetime);
				
				if (totalAccruedBalance!=null) {
					pstmt.setBigDecimal(10,BigDecimal.valueOf( (Double.parseDouble(totalAccruedBalance)+Double.parseDouble(remmitAmount) )));
				}else {
					pstmt.setBigDecimal(10,BigDecimal.valueOf( Double.parseDouble(remmitAmount) ));
				}
				pstmt.setString(11, "W");
				try {
						pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query +" "+e.getMessage());
					}					
				if( pstmt!=null) pstmt.close();
				
				// *********Step 5*****Update Receiver Wallet 
				//Select the balance to be updated. 
					 
				// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				// inserting block data for sender
				 
				if(	CustomerWalletDao.insertGenericWalletTransactions(transactionCode, walletId,txnUserCode, systemreference, remmitAmount, currencyId,userTxnMode, transactionDatetime ,userType) == false) {
					throw new Exception ("Blockchain insert issue");
				}
				if(Double.parseDouble(customerChargesValue)>0) {
					if(	CustomerWalletDao.insertGenericWalletTransactions(transactionCode2, walletId,txnUserCode, systemreference+"-AC", customerChargesValue, currencyId,customerPayType, transactionDatetime ,userType) == false) {
						throw new Exception ("Blockchain insert issue");
					}
				}
					 
				
				connection.commit();	result = true;
			
		
			}catch(Exception e){
				result = false;
				connection.rollback();
				NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
				throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
				}
					if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; 
					if(userType!=null)  userType=null; if(txnUserCode != null) txnUserCode = null;
					if(systemreference!=null)  systemreference=null; if ( totalAccruedBalance!=null) totalAccruedBalance=null;
			
			}
			
			return result;
	}	
	
	public boolean insertIntoCurrencyFiatRemittanceQueue(String relationshipNo, String payMode, String sourceAssetCode, String destinationCurrency,
			String sourceAmount, String destinationAmount, String stellarTxnHash, String senderComment,String receiverName,String receiverBankName,
			String receiverBankCode, String receiverAccountNo, String receiverEmail,String transactionCode, String systemReferenceInt,
			String txnUserCode) throws Exception{
					PreparedStatement pstmt=null;
					Connection connection = null;
					ResultSet rs=null;
					String query = null;
					boolean result = false;
					String partnerUserId=null;
					String partnerStellarPublicKey=null;
					
					String transactionDatetime=null;
		try {
			
			systemReferenceInt = payMode + "-"+transactionCode;
			transactionDatetime= Utilities.getMYSQLCurrentTimeStampForInsert();
			txnUserCode = Utilities.generateTransactionCode(10);
			
			connection = super.getConnection();
			connection.setAutoCommit(false);
			
			query="select userid, stellarid from partner_details where currency=? and status=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, destinationCurrency);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					partnerUserId= (Utilities.tripleDecryptData(StringUtils.trim(rs.getString("userid")) ) );
					partnerStellarPublicKey= (Utilities.tripleDecryptData( StringUtils.trim(rs.getString("stellarid")))  );
					} // end of while
				NeoBankEnvironment.setComment(3,className,"  partnerUserId is  "+partnerUserId+" partnerStellarPublicKey "+partnerStellarPublicKey   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
			
			query="insert into txn_currency_remittance(txncode,sysreference_int,txnusercode,custrelno,paytype,source_assetcode,destination_currency,"
					+ "source_amount,destination_amount,partner_userid,partner_stellar_id,stellar_txnhash,sender_comment,receiver_name,"
					+ "receiver_bankname,receiver_bankcode,receiver_accountno,receiver_email,status,txndatetime, remittance_type)"
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			              //  1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20 21
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1,  transactionCode);
			pstmt.setString(2,  systemReferenceInt);
			pstmt.setString(3,  txnUserCode);
			pstmt.setString(4,  relationshipNo);
			pstmt.setString(5,  payMode);
			pstmt.setString(6,  sourceAssetCode);
			pstmt.setString(7,  destinationCurrency);
			pstmt.setString(8,  sourceAmount);
			pstmt.setString(9,  destinationAmount);
			pstmt.setString(10, partnerUserId);
			pstmt.setString(11, partnerStellarPublicKey);
			pstmt.setString(12, stellarTxnHash);
			pstmt.setString(13, (""));
			pstmt.setString(14, (receiverName));
			pstmt.setString(15, (receiverBankName));
			pstmt.setString(16, (receiverBankCode));
			pstmt.setString(17, (receiverAccountNo));
			pstmt.setString(18, (receiverEmail));
			pstmt.setString(19, "P");// Pending
			pstmt.setString(20, transactionDatetime);
			pstmt.setString(21, "PT");
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if(pstmt!=null)	pstmt=null;
		
			connection.commit();	result = true;

		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}

			if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(partnerUserId!=null)  partnerUserId=null;
			if(partnerStellarPublicKey!=null)  partnerStellarPublicKey=null; if(transactionCode!=null)  transactionCode=null; if(systemReferenceInt!=null)  systemReferenceInt=null;
			if(txnUserCode != null) txnUserCode = null;
			}
		return result;
	}
	
	public double getXLMConversionRate( String sourceAmount) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String sellRate = null;
		double destinationAmount = 0;
		try {
			connection = super.getConnection();	                    
			query = "select selling_rate from asset_distribution_pricing   where assetcode= ? and status = ? ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "XLM");
			pstmt.setString(2, "A");
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
			 	while(rs.next()){	 
			 		sellRate = StringUtils.trim(rs.getString("selling_rate"));
			 		} 
			 } 
			 
			 if(sellRate!= null) {
				 destinationAmount = Double.parseDouble(sourceAmount)/ Double.parseDouble(sellRate);
			 }
	
		}catch(Exception e) {
			destinationAmount = 0;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());	
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
	
		return destinationAmount;
	}
	
}
