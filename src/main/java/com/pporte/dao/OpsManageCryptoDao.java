package com.pporte.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.pporte.NeoBankEnvironment;
import com.pporte.model.Transaction;
import com.pporte.model.WalletAssetAccounts;
import com.pporte.utilities.Utilities;

public class OpsManageCryptoDao extends HandleConnections {
	public static String className = OpsManageCryptoDao.class.getName();
	
	public boolean updateCustomerDetails(String transactionCode, String externalRef) throws Exception {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String walletId = null;
		String systemreference = null;
		String amount = null;
		String currencyId = null;
		String transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
		String assetCode = null;
		
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);	 

			 //                                      1                 2             3          
			query = "update txn_buy_coins set  status=?, sysreference_ext=? where txn_code=? "; 
		    pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P"); 						 
			pstmt.setString(2, externalRef);						 
			pstmt.setString(3, transactionCode);						 
								 
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); 
			
			//Select transaction details
			query = " select txn_code, walletid, custrelno, asset_code, txnamount, coin_amount,  sysreference_int   from txn_buy_coins where txn_code=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				 while(rs.next()){	 			 			
					 walletId = (StringUtils.trim(rs.getString("walletid"))  );
					 systemreference = (StringUtils.trim(rs.getString("sysreference_int"))  );
					 amount = (StringUtils.trim(rs.getString("coin_amount"))  );
					 currencyId = (StringUtils.trim(rs.getString("asset_code"))  );
					 assetCode = (StringUtils.trim(rs.getString("asset_code"))  );
					 
				 } // end of while
			} //end of if rs!=null check 
			if( pstmt!=null)	pstmt.close();
			if(rs!=null) rs.close();
		
						// 1		2			3			4			5				6		7			8
			query = "insert into txn_crypto_wallet_cust (txncode, walletid, sysreference_int, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel, sysreference_ext, assetcode, asset_type) "
			+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?) ";
			// 1  2  3  4  5  6   7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 			
			pstmt.setString(2, walletId); 				
			pstmt.setString(3, systemreference );							
			pstmt.setBigDecimal(4, new BigDecimal(amount));  
			pstmt.setString(5, currencyId);
			pstmt.setString(6, "D"); // Debit as it is a payment 
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, "");
			pstmt.setString(9, "W");
			pstmt.setString(10, externalRef);
			pstmt.setString(11, assetCode);
			pstmt.setString(12, "P");
			try {
			NeoBankEnvironment.setComment(3,className,"Executed Porte txn_crypto_wallet_cust amount"+"transactionCode is"+ transactionCode +"porteWalletId is"+walletId 
			+"systemreference is"+systemreference +"payAmount is"+amount +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);
			
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if (pstmt != null) pstmt.close();
			
			

			connection.commit();
			result = true;
		}catch(Exception e){
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method updateCustomerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method updateCustomerDetails  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); 
		}
		return result;
	}
	
	public List<Transaction> getPorteRequests() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		List<Transaction> aryTransactions = null;
		try{
		  connection = super.getConnection();	

		  query = " select a.txn_code txn_code, a.walletid walletid,a.txndate txndate, a.custrelno custrelno, "
		  		 + " a.asset_code asset_code, a.txnamount txnamount, a.coin_amount coin_amount, a.commet commet, "
		  		 + " b.customername customername, b.customerid, b.customerid customerid, c.public_key pulickey from txn_buy_coins a, "
		  		 + " customer_details b, stellar_account_relation c where a.custrelno=b.relationshipno "
		  		 + " and a.custrelno = c.relationshipno and a.status = ? and asset_code !=? order by a.txndate desc ";
		  
		 pstmt = connection.prepareStatement(query);
		 pstmt.setString(1, "N");
		 pstmt.setString(2, "BTC");
		 rs = pstmt.executeQuery();
		  if(rs!=null){	
			  aryTransactions = new ArrayList<Transaction>();
			 	while(rs.next()){	
			 		Transaction m_Transaction = new Transaction();
			 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txn_code")));
			 		m_Transaction.setCustomerWalletId( StringUtils.trim(rs.getString("walletid")));
			 		m_Transaction.setRelationshipNo( StringUtils.trim(rs.getString("custrelno")));
			 		m_Transaction.setAssetCode( StringUtils.trim(rs.getString("asset_code")));
			 		m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount")));
			 		m_Transaction.setCoinAmount(StringUtils.trim(rs.getString("coin_amount")));
			 		m_Transaction.setCustomerName(StringUtils.trim(rs.getString("customername")));
			 		m_Transaction.setCustomerId(Utilities.tripleDecryptData(StringUtils.trim(rs.getString("customerid"))));
			 		m_Transaction.setComment(StringUtils.trim(rs.getString("commet")));
			 		m_Transaction.setPublicKey(Utilities.decryptString( StringUtils.trim(rs.getString("pulickey"))));
			 		//Utilities.decryptString( StringUtils.trim(rs.getString("pulickey")))
			 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndate")));
			 		aryTransactions.add(m_Transaction);
		 		} // end of while						 	
		 	} //end of if 
		  	if(aryTransactions!=null)
		  		if(aryTransactions.size()==0)
		  			aryTransactions=null;			  
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteRequests  is  "+e.getMessage());
			throw new Exception ("The exception in method getPorteRequests  is  "+e.getMessage());	
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); if(rs!=null) rs.close();
		}
		return aryTransactions;
	}
	
	public ArrayList<WalletAssetAccounts> getAllWalletAssetAccounts() throws Exception {
		
		PreparedStatement pstmt = null;
		Connection connection = null;
		ResultSet rs = null;
		String query = null;
		ArrayList<WalletAssetAccounts> arrWalletAssetAccounts = null;
		WalletAssetAccounts accounts = null;
		try {
			connection = super.getConnection();	
			query = " select sequence_id, asset_code, account_type, public_key, status, createdon from wallet_assets_account order by createdon desc ";
			pstmt = connection.prepareStatement(query);
	
			rs = (ResultSet) pstmt.executeQuery();
			if (rs != null) {
				arrWalletAssetAccounts = new ArrayList<WalletAssetAccounts>();
				while (rs.next()) {
					accounts = new WalletAssetAccounts();
					accounts.setSequenceId(StringUtils.trim(rs.getString("sequence_id")));
					accounts.setAssetCode(StringUtils.trim(rs.getString("asset_code")));
					accounts.setAccountType(StringUtils.trim(rs.getString("account_type")));
					accounts.setPublicKey(StringUtils.trim(rs.getString("public_key")));
					accounts.setStatus(StringUtils.trim(rs.getString("status")));
					accounts.setCreatedOn(StringUtils.trim(rs.getString("createdon")));
					arrWalletAssetAccounts.add(accounts);
				} // end of while

			} // end of if rs!=null check
			if (arrWalletAssetAccounts != null)
				if (arrWalletAssetAccounts.size() == 0)
					arrWalletAssetAccounts = null;
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
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		}
		return arrWalletAssetAccounts;
	}
	
	public boolean editAssetAccountDetails(String sequeceId, String accountType,
			String publicKey, String status)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);		 
		            //                                              1             2         3              4                
			 query = " update wallet_assets_account set account_type=?, public_key=?, status=?  where sequence_id=? ";
			
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, accountType); 						 
				pstmt.setString(2, publicKey);						 
				pstmt.setString(3, status);					 
				pstmt.setInt(4,  Integer.parseInt(sequeceId));	
			
			try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}

				connection.commit();
				result = true;
		}catch(Exception e){
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());

		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}
	
	public boolean addNewAssetConfigurationAccount(String assetCode, String accountType,
			String publicKey, String status )throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			//Inactivate any other active account
			 query = " update wallet_assets_account set  status=? where asset_code=? and account_type=?  ";
				
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "I"); 						 
				pstmt.setString(2, assetCode);	
				pstmt.setString(3, accountType);	
				try {
					pstmt.executeUpdate();
				    }catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
				    }
				pstmt.close();
		 
				                                          //		 1		  2			    3        4	      5		   
				query = " insert into wallet_assets_account (asset_code, account_type, public_key, status, createdon ) "
						+ "values ( ?,  ?,  ?, ?, ?) ";
						  //		1   2   3  4  5 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, assetCode); 						 
					pstmt.setString(2, accountType);						 
					pstmt.setString(3, publicKey);					 
					pstmt.setString(4, status);					 
					pstmt.setString(5, Utilities.getMYSQLCurrentTimeStampForInsert());					 

				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}
				pstmt.close();
							
					connection.commit();
					result = true;
		}catch(Exception e){
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}

}
