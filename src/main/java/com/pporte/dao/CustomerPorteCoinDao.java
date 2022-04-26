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
import com.pporte.model.CryptoAssetCoins;
import com.pporte.model.PorteAsset;
import com.pporte.model.AssetTransaction;
import com.pporte.utilities.Utilities;

public class CustomerPorteCoinDao  extends HandleConnections {
	private static String className = CustomerPorteCoinDao.class.getSimpleName();
	public List<PorteAsset> getPorteAssetDetailsForCustomer(String relationshipNo) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		PorteAsset porteCoin = null;
		List<PorteAsset> porteCoinsList = null;
		
		try{
			
			connection = super.getConnection();	
			query =   " select  a.walletid walletid, a.relationshipno relationshipno, a.assetcode assetcode, a.walletdesc , b.public_key public_key "
					+ " from wallet_details_external a,stellar_account_relation b  where a.relationshipno = ? and a.status = ? and "
					+ "	 a.relationshipno = b.relationshipno ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "A"); // This can be used to 
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 porteCoinsList = new ArrayList<PorteAsset>();
				 	while(rs.next()){	
				 		porteCoin = new PorteAsset();
				 		porteCoin.setWalletId(StringUtils.trim(rs.getString("walletid")));
				 		porteCoin.setRelationshipNo(StringUtils.trim(rs.getString("relationshipno")));
				 		porteCoin.setAssetCode(StringUtils.trim(rs.getString("assetcode")));
				 		porteCoin.setAssetDescription(StringUtils.trim(rs.getString("walletdesc")));
				 		porteCoin.setPublicKey(StringUtils.trim(rs.getString("public_key")));
				 		porteCoinsList.add(porteCoin);
				 	}
			 }	
			 if(porteCoinsList!=null)
				 if(porteCoinsList.size()==0)
					 porteCoinsList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteAssetDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getPorteAssetDetails  is  "+e.getMessage());			
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
	
	public List<CryptoAssetCoins> getPorteCoinDetails() throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CryptoAssetCoins porteCoin = null;
		 List<CryptoAssetCoins> porteCoinsList = null;
		try{
			connection = super.getConnection();	
			query =   " select  asset_code, asset_desc, status, asset_type, wallettype from wallet_assets where asset_type=? and status=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P");
			pstmt.setString(2, "A");
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
	
	public List<CryptoAssetCoins> getPorteCoinWihoutBTCDetails() throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CryptoAssetCoins porteCoin = null;
		 List<CryptoAssetCoins> porteCoinsList = null;
		try{
			connection = super.getConnection();	
			query =   " select  asset_code, asset_desc, status, asset_type, wallettype "
					+ " from wallet_assets where asset_type=? and status=? and asset_code != ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P");
			pstmt.setString(2, "A");
			pstmt.setString(3, "BTC");
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
	
	public List<CryptoAssetCoins> getVesselAndPorteDetails() throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CryptoAssetCoins porteCoin = null;
		 List<CryptoAssetCoins> porteCoinsList = null;
		try{
			connection = super.getConnection();	
			query =   " select  asset_code, asset_desc, status, asset_type, wallettype "
					+ " from wallet_assets where asset_type=? and status=? and asset_code = ? or asset_code = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P");
			pstmt.setString(2, "A");
			pstmt.setString(3, "VESL");
			pstmt.setString(4, "PORTE");
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
	
	
	public String getAssetWalletDetails(String relationshipNo, String assetCode, String publicKey) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String walletId=null;
		
			try {
				connection = super.getConnection();	
				query=" select a.walletid walletid from wallet_details_external a where a.relationshipno = ? and a.assetcode = ? ";
				pstmt=connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, assetCode);
				rs= (ResultSet)pstmt.executeQuery();
				if (rs!=null) {
					while (rs.next()) {
						walletId= StringUtils.trim(rs.getString("walletid"));
					}
				}
				
			}catch(Exception e){
				NeoBankEnvironment.setComment(1,className,"The exception in method getFiatWalletBalance  is  "+e.getMessage());
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
		return walletId;
	}
			
			
			
			
			
			
	public String getReceiverPorteWalletId(String receiverEmail, String assetCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String receiverWalletId=null;
			try {
			 connection = super.getConnection();	
			 query = " select a.walletid walletid from wallet_details a, customer_details b, wallet_assets c "
				 	   + " where a.relationshipno = b.relationshipno and b.custemail = ? and a.wallettype= c.wallettype and c.asset_code = ?";
			 pstmt = connection.prepareStatement(query);
			 pstmt.setString(1, receiverEmail);
			 pstmt.setString(2, assetCode);
			 rs = (ResultSet)pstmt.executeQuery();	
			 if(rs!=null){
			 	while(rs.next()){	 			 			
			 		receiverWalletId = (StringUtils.trim(rs.getString("walletid"))    );
			 		NeoBankEnvironment.setComment(3,className,"receiverWalletId is " +receiverWalletId  );
					} // end of while
				} //end of if rs!=null check
			
			}catch(Exception e){
				receiverWalletId = null;
				NeoBankEnvironment.setComment(1,className,"The exception in method getReceiverPorteWalletId  is  "+e.getMessage());
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
		return receiverWalletId;
	}

	public Boolean porteCoinP2P(String relationshipNo, String senderWalletId, String payAmount, String payComments,
			String referenceNo, String txnUserCode, String customerCharges, String txnPayMode,
			String assetCode, String extSystemRef,String receiverWalletId, String senderWalBalance, String receiverWalBalance ) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String transactionDatetime = null;
		String currencyId= assetCode;
		String senderTxnMode = "D";
		String receiverTxnMode = "C";
		String senderTransactionCode = null;
		String receiverTransactionCode = null;
		String userType = "C";
		String systemreference =referenceNo;
		String receiverSystemreference =null;
		String senderRefNo = null;
		String receiverRefNo = null;
		String totalAccruedBalance=null;
		try {	

			//String customerChargesValue = customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|"));
			//String customerPayType = customerCharges.substring(0, customerCharges.indexOf(","));
			payAmount = payAmount.replaceAll(",", "");

			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
			senderTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			receiverTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			receiverSystemreference = txnPayMode + "-"
						+ (new SimpleDateFormat("yyMMddHHmmssSSS")).format(new java.util.Date())
						+ Utilities.genAlphaNumRandom(9);
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
		 
			 //********Step 1*********\\\\	

			// **********Step 4: Record the wallet transaction ledger
														// 1		2			3			4			5				6		7			8
			query = "insert into txn_crypto_wallet_cust (txncode, walletid, sysreference_int, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel, sysreference_ext, assetcode, asset_type) "
					+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?) ";
							// 1  2  3  4  5  6   7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, senderTransactionCode); 			
			pstmt.setString(2, senderWalletId); 				
			pstmt.setString(3, systemreference );							
			pstmt.setBigDecimal(4, new BigDecimal(payAmount));  
			pstmt.setString(5, currencyId);
			pstmt.setString(6, senderTxnMode); // Debit as it is a payment by the sender
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, txnUserCode);
			pstmt.setString(9, "W");
			pstmt.setString(10, extSystemRef);
			pstmt.setString(11, assetCode);
			pstmt.setString(12, "P");
			try {
				//NeoBankEnvironment.setComment(3,className,"Executed Sender txn_crypto_wallet_cust amount"+"senderTransactioncode is"+ senderTransactionCode +"senderWalletId is"+senderWalletId 
						//+"systemreference is"+systemreference +"payAmount is"+payAmount +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);

				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}					
				pstmt.close();	
			if(pstmt!=null)			
				pstmt=null;
			/*
			if(Double.parseDouble(customerChargesValue)>0) {
				query = "insert into txn_crypto_wallet_cust (txncode, walletid, sysreference_int, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel, sysreference_ext, assetcode, asset_type) "
						+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?) ";
								// 1  2  3  4  5  6   7  8
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, senderTransactionCode2); 			
				pstmt.setString(2, senderWalletId); 				
				pstmt.setString(3, systemreference+"-AC" );							
				pstmt.setBigDecimal(4, new BigDecimal(Double.parseDouble(customerChargesValue)));  
				pstmt.setString(5, currencyId);
				pstmt.setString(6, customerPayType); // Debit as it is a payment by the sender
				pstmt.setString(7, transactionDatetime);
				pstmt.setString(8, txnUserCode);
				pstmt.setString(9, "W");
				pstmt.setString(10, extSystemRef);
				pstmt.setString(11, assetCode);
				pstmt.setString(12, "P");
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
				pstmt.setString(1, senderTransactionCode2); 
				pstmt.setString(2, txnPayMode);
				pstmt.setString(3, senderWalletId);
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
			*/
			// *********5.2*****Receiver Wallet 
														//   1		 2			 3			       4			 5		   6		 7			   8          9               10               11         12
			query = " insert into txn_crypto_wallet_cust (txncode, walletid, sysreference_int, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel, sysreference_ext, assetcode, asset_type) "
				  + " values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?) ";
						   // 1  2  3  4  5  6   7  8  9 10 11 12
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, receiverTransactionCode); 			
			pstmt.setString(2, receiverWalletId); 				
			pstmt.setString(3, receiverSystemreference );		  					
			pstmt.setBigDecimal(4, new BigDecimal(payAmount));  
			pstmt.setString(5, currencyId);
			pstmt.setString(6, receiverTxnMode); // Debit as it is a payment by the sender
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, txnUserCode);
			pstmt.setString(9, "W");
			pstmt.setString(10, extSystemRef);
			pstmt.setString(11, assetCode);
			pstmt.setString(12, "P");
			try {
				NeoBankEnvironment.setComment(3,className,"Executed receiver txn_crypto_wallet_cust amount"+ payAmount   );
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			pstmt.close();	if(pstmt!=null)	pstmt=null;
				 
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
				NeoBankEnvironment.setComment(3,className,"Points Balance for relationshipNo   "+ relationshipNo + "  is " + previousPoinsBalance );
				pointsaccrued = Double.toString((Double.parseDouble(payAmount) * Double.parseDouble(pointsConversion)));
				NeoBankEnvironment.setComment(3,className,"pointsaccrued   "+ pointsaccrued );
			   if(previousPoinsBalance==null) { // For the first time 
				  firstime =true;
				  NeoBankEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +senderWalletId); 
			 
			   }else {
				   NeoBankEnvironment.setComment(3,	className," Previous Point balance for user : " +senderWalletId+ " is  " +previousPoinsBalance); 
			   }	 

				//***** Step 6.2****: Insert loyalty points for the sender user		
				 
						                             //	  	1		2				3			4			5			6				7        8        9
				query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, status, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?,?) ";
							//		   1  2  3  4  5  6  7  8 9
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, senderWalletId); 	
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
			

			connection.commit();	result = true;
		
	
		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method porteCoinP2P  is  "+e.getMessage());
			throw new Exception ("The exception in method porteCoinP2P  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
				if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(receiverTransactionCode!=null)  receiverTransactionCode=null;
				if(userType!=null)  userType=null; if(senderTransactionCode!=null)  senderTransactionCode=null; if(receiverRefNo!=null)  receiverRefNo=null; if(txnUserCode != null) txnUserCode = null;
				if(systemreference!=null)  systemreference=null; if(senderRefNo!=null)  senderRefNo=null; if ( totalAccruedBalance!=null) totalAccruedBalance=null;
		
			}
		
		return result;
	}

	public List<AssetTransaction> getPorteCoinTxnBtnDates(String dateFrom, String dateTo, String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		AssetTransaction transaction = null;
		List<AssetTransaction> transactionList=null;
		try{
			connection = super.getConnection();	
			query = " select a.txnusercode txnusercode, a.sysreference_int sysreference, a.pymtchannel pymtchannel, "
					+ "	a.txnamount txnamount, a.txncurrencyid txncurrencyid, a.txnmode txnmode, a.txndatetime txndatetime, b.walletid walletid, "
					+ "	b.wallettype wallettype from txn_crypto_wallet_cust a, wallet_details b  where a.walletid = b.walletid "
					+ "	and b.relationshipno = ? and a.asset_type =? and a.txndatetime between ? and ? order by txndatetime desc limit 1000";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "P");
			pstmt.setString(3, dateFrom);
			pstmt.setString(4, dateTo);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactionList = new ArrayList<AssetTransaction>();
				 	while(rs.next()){	
				 		transaction = new AssetTransaction();
				 		transaction.setTxnUserCode(StringUtils.trim(rs.getString("txnusercode")));
				 		transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime")));
				 		transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode")));
				 		transaction.setPymtChannel(StringUtils.trim(rs.getString("pymtchannel")));
				 		transaction.setSystemReferenceInt(StringUtils.trim(rs.getString("sysreference")));
				 		transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount")));
				 		transactionList.add(transaction);
				 	}
			 }
			 if(transactionList!=null)
				 if(transactionList.size()==0)
					 transactionList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getFiatWalletTxnBtnDates  is  "+e.getMessage());
			throw new Exception ("The exception in method getFiatWalletTxnBtnDates  is  "+e.getMessage());			
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
	
	public List<AssetTransaction> getPorteLastFiveTxn(String relationshipNo)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		AssetTransaction transaction = null;
		List<AssetTransaction> transactionList=null;
		try{
			connection = super.getConnection();	
			query = " select a.txnusercode txnusercode, a.sysreference_int sysreference, a.pymtchannel pymtchannel, a.assetcode assetcode, "
					+ "	a.txnamount txnamount, a.txncurrencyid txncurrencyid, a.txnmode txnmode, a.txndatetime txndatetime, b.walletid walletid, "
					+ "	b.wallettype wallettype from txn_crypto_wallet_cust a, wallet_details b  where a.walletid = b.walletid "
					+ "	and b.relationshipno = ? and a.asset_type =?  order by txndatetime desc limit 5 ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "P");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactionList = new ArrayList<AssetTransaction>();
				 	while(rs.next()){	
				 		transaction = new AssetTransaction();
				 		transaction.setTxnDateTime(Utilities.displayDateFormat( StringUtils.trim(rs.getString("txndatetime")), "dd MMM yy HH:mm a"));
				 		transaction.setTxnUserCode(StringUtils.trim(rs.getString("txnusercode")));
				 		transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode")));
				 		transaction.setPymtChannel(StringUtils.trim(rs.getString("pymtchannel")));
				 		transaction.setSystemReferenceInt(StringUtils.trim(rs.getString("sysreference")));
				 		transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount")));
				 		transaction.setAssetCode(StringUtils.trim(rs.getString("assetcode")));
				 		transactionList.add(transaction);
				 	}
			 }
			 if(transactionList!=null)
				 if(transactionList.size()==0)
					 transactionList=null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteLastFiveTxn  is  "+e.getMessage());
			throw new Exception ("The exception in method getPorteLastFiveTxn  is  "+e.getMessage());			
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

	public Boolean sellPorteCoinPorteCoin(String relationshipNo, String fiatWalletId, String amount, String referenceNo,
			String txnUserCode, String txnPayMode, String assetCode, String extSystemRef, String porteWalletId,
			double newWalletBalanceFromStellar, String customerCharges, String amountInUSD) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String transactionDatetime = null;
		String currencyId= assetCode;
		String porteTxnMode = "D";
		String fiatTxnMode = "C";
		String fiatTransactionCode = null;
		String porteTransactionCode2 = null;
		String porteTransactionCode = null;
		String userType = "C";
		String systemreference =referenceNo;
		String totalAccruedBalance=null;
		
		try {
			String customerChargesValue = customerCharges.substring(customerCharges.indexOf(",")+1, customerCharges.indexOf("|"));
			String customerPayType = customerCharges.substring(0, customerCharges.indexOf(","));

			amount = amount.replaceAll(",", "");
			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
			fiatTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			porteTransactionCode2 = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			porteTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			//********Step 1*********\\\\	
			 double porteDebitAmount =  Double.parseDouble(amount) + Double.parseDouble(customerChargesValue);
			 double fiatCreditAmount =  Double.parseDouble(amountInUSD);
			 //*******Step 2: Update the porte wallet Ledger
		     query = " update wallet_details set currbal= currbal - ?, lastupdated = ? where  walletid=? ";
			 pstmt = connection.prepareStatement(query);
			 pstmt.setBigDecimal(1,BigDecimal.valueOf(porteDebitAmount));
			 pstmt.setString(2, transactionDatetime); 
			 pstmt.setString(3, porteWalletId); 
			 try {
				 NeoBankEnvironment.setComment(3,className,"update porte wallet amount" + porteDebitAmount );
				 pstmt.executeUpdate();
				 }catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			pstmt.close();
			// *******Step 2: Update the fiat wallet Ledger
			query = " update wallet_details set currbal= currbal + ?, lastupdated = ? where  walletid=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setBigDecimal(1, BigDecimal.valueOf( fiatCreditAmount));
			pstmt.setString(2, transactionDatetime); 
			pstmt.setString(3, fiatWalletId);
			try {
				NeoBankEnvironment.setComment(3,className,"update receiver wallet amount" + fiatCreditAmount );
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}					
			pstmt.close();	
			
			// **********Step 3: Record the wallet transaction ledger
						// 1		2			3			4			5				6		7			8
			query = "insert into txn_crypto_wallet_cust (txncode, walletid, sysreference_int, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel, sysreference_ext, assetcode, asset_type) "
			+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?) ";
			// 1  2  3  4  5  6   7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, porteTransactionCode); 			
			pstmt.setString(2, porteWalletId); 				
			pstmt.setString(3, systemreference );							
			pstmt.setBigDecimal(4, new BigDecimal(amount));  
			pstmt.setString(5, currencyId);
			pstmt.setString(6, porteTxnMode); // Debit as it is a payment by the sender
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, txnUserCode);
			pstmt.setString(9, "W");
			pstmt.setString(10, extSystemRef);
			pstmt.setString(11, assetCode);
			pstmt.setString(12, "P");
			try {
			NeoBankEnvironment.setComment(3,className,"Executed Porte txn_crypto_wallet_cust amount"+"porteTransactionCode is"+ porteTransactionCode +"porteWalletId is"+porteWalletId 
			+"systemreference is"+systemreference +"payAmount is"+amount +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);
			
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if (pstmt != null) pstmt.close();
			// **********Step 4: Record transaction charge 
			if(Double.parseDouble(customerChargesValue)>0) {
				query = "insert into txn_crypto_wallet_cust (txncode, walletid, sysreference_int, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel, sysreference_ext, assetcode, asset_type) "
						+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?) ";
								// 1  2  3  4  5  6   7  8
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, porteTransactionCode2); 			
				pstmt.setString(2, porteWalletId); 				
				pstmt.setString(3, systemreference+"-AC" );							
				pstmt.setBigDecimal(4, new BigDecimal(Double.parseDouble(customerChargesValue)));  
				pstmt.setString(5, currencyId);
				pstmt.setString(6, customerPayType); // Debit as it is a payment by the sender
				pstmt.setString(7, transactionDatetime);
				pstmt.setString(8, txnUserCode);
				pstmt.setString(9, "W");
				pstmt.setString(10, extSystemRef);
				pstmt.setString(11, assetCode);
				pstmt.setString(12, "P");
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}					
				if (pstmt != null) pstmt.close();			
				
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
				pstmt.setString(1, porteTransactionCode2); 
				pstmt.setString(2, txnPayMode);
				pstmt.setString(3, porteWalletId);
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
			// **********Step 5: Record the wallet transaction ledger
													// 1		2			3			4			5				6		7			8
			query = "insert into txn_wallet_cust_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel) "
			+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?) ";
			// 1  2  3  4  5  6   7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, fiatTransactionCode); 			
			pstmt.setString(2, fiatWalletId); 				
			pstmt.setString(3, systemreference );							
			pstmt.setBigDecimal(4, new BigDecimal(amountInUSD));  
			pstmt.setString(5, currencyId);
			pstmt.setString(6, fiatTxnMode); 
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, txnUserCode);
			pstmt.setString(9, "W");
			try {
				NeoBankEnvironment.setComment(3,className,"Executed  wallet_tnx_bc amount"+"fiatTransactionCode is"+ fiatTransactionCode +"fiatWalletId is"+fiatWalletId 
				+"systemreference is"+systemreference +"payAmount is"+amountInUSD +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if( pstmt!=null)	pstmt.close();
			
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
				NeoBankEnvironment.setComment(3,className,"Points Balance for relationshipNo   "+ relationshipNo + "  is " + previousPoinsBalance );
				pointsaccrued = Double.toString((Double.parseDouble(amount) * 1));
				NeoBankEnvironment.setComment(3,className,"pointsaccrued   "+ pointsaccrued );
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

			connection.commit();	result = true;
		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method sellPorteCoinPorteCoin  is  "+e.getMessage());
			throw new Exception ("The exception in method porteCoinP2P  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
				if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(porteTransactionCode2!=null)  porteTransactionCode2=null;
				if(userType!=null)  userType=null; if(fiatTransactionCode!=null)  fiatTransactionCode=null; if(porteTransactionCode!=null)  porteTransactionCode=null; if(txnUserCode != null) txnUserCode = null;
				if(systemreference!=null)  systemreference=null; if ( totalAccruedBalance!=null) totalAccruedBalance=null;
		
			}
		
		return result;
	}
	
	public List<CryptoAssetCoins> getPorteAssetDetails() throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CryptoAssetCoins porteCoin = null;
		 List<CryptoAssetCoins> porteCoinsList = null;
		try{
			connection = super.getConnection();	
			query =   " select  asset_code, asset_desc, status, asset_type, wallettype from wallet_assets where asset_type=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P");
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
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteCoinDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getPorteCoinDetails  is  "+e.getMessage());			
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

	public double getPorteAssetConversion(String sourceAsset, String destAssetCode, String sourceAmount) throws Exception {
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
			pstmt.setString(1, destAssetCode);
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
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteAssetConversion  is  "+e.getMessage());		
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
	
	public double getPorteAssetConversionOffRamp(String sourceAsset, String destAssetCode, String sourceAmount) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String burningRate = null;
		double destinationAmount = 0;
		try {
			connection = super.getConnection();	                    
			query = " select buying_rate from asset_distribution_pricing   where assetcode= ? and status = ? ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, sourceAsset);
			pstmt.setString(2, "A");
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
			 	while(rs.next()){	 
			 		burningRate = StringUtils.trim(rs.getString("buying_rate"));
			 		} 
			 } 
			 NeoBankEnvironment.setComment(3,className,"burningRate  "+burningRate);
			 if(burningRate!= null) {
				 destinationAmount = Double.parseDouble(sourceAmount)/Double.parseDouble(burningRate); //In usd
			 }
	
		}catch(Exception e) {
			//destinationAmount = -1;
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteAssetConversionOffRamp  is  "+e.getMessage());		
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
