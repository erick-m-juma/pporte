package com.pporte.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.pporte.NeoBankEnvironment;
import com.pporte.model.PorteAsset;
import com.pporte.model.Transaction;
import com.pporte.model.AssetTransaction;
import com.pporte.model.Wallet;
import com.pporte.utilities.Utilities;

public class CustomerMobilePorteCoinDao extends HandleConnections {
	public static String className = CustomerMobilePorteCoinDao.class.getName();
	
	public List<Wallet> getPorteAssetCoinsWallets(String relationshipNo) throws Exception {
		PreparedStatement pstmt = null;
		Connection connection = null;
		ResultSet rs = null;
		
		String query = null;
		ArrayList<Wallet> arrWalletCoins = null;
		try {
			connection = super.getConnection();
			
			query = "select a.walletid, a.wallettype, a.currbal, a.status, a.walletdesc,a.lastupdated, b.assetcode, c.asset_desc from  wallet_details a, wallet_asset_relation b,"
			+" wallet_assets c where relationshipno =? and a.status =? and a.walletid = b.walletid and b.assetcode = c.asset_code";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo); //External Crypto
			pstmt.setString(2, "A"); //For Fiat wallet 
			rs = (ResultSet) pstmt.executeQuery();
			if (rs != null) {
				
				arrWalletCoins = new ArrayList<Wallet>();
				while (rs.next()) {
					Wallet m_WalletCoin = new Wallet();
					m_WalletCoin.setWalletId(StringUtils.trim(rs.getString("walletid")));
					m_WalletCoin.setWalletType(StringUtils.trim(rs.getString("wallettype")));
					m_WalletCoin.setCurrentBalance(StringUtils.trim(rs.getString("currbal")));
					m_WalletCoin.setStatus(StringUtils.trim(rs.getString("status")));
					m_WalletCoin.setWalletDesc(StringUtils.trim(rs.getString("walletdesc")));
					m_WalletCoin.setWalletAssetCode(StringUtils.trim(rs.getString("assetcode")));
					m_WalletCoin.setWalletAssetDesc(StringUtils.trim(rs.getString("asset_desc")));
					m_WalletCoin.setLastUpdated(Utilities.displayDateFormat(StringUtils.trim(rs.getString("lastupdated")),"dd MMM yy HH:mm a"));
					arrWalletCoins.add(m_WalletCoin);
				} // end of while

			} // end of if rs!=null check
			if (arrWalletCoins != null)
				if (arrWalletCoins.size() == 0)
					arrWalletCoins = null;
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1, className, "The exception in method getAssetCoinsWallets is  " + e.getMessage());
			throw new Exception("The exception in method getAssetCoinsWallets is  " + e.getMessage());
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
		return arrWalletCoins;
	}
	public List<AssetTransaction> getPorteLastTwentyTxn(String relationshipNo)  throws Exception {
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
					+ "	and b.relationshipno = ? and a.asset_type =?  order by txndatetime desc limit 25 ";
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
			NeoBankEnvironment.setComment(1,className,"The exception in method getPorteLastTwentyTxn  is  "+e.getMessage());
			throw new Exception ("The exception in method getPorteLastTwentyTxn  is  "+e.getMessage());			
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
	public ConcurrentHashMap<String, PorteAsset> getPorteAssetDetails(String relationshipNo) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		PorteAsset porteCoin = null;
		ConcurrentHashMap<String, PorteAsset> hashPorteAssetDetails = null;
		try{
			connection = super.getConnection();	
			query =   " select  a.walletid walletid, a.relationshipno relationshipno, a.assetcode assetcode, a.walletdesc walletdesc, "
					+ " b.public_key public_key from wallet_details_external a, stellar_account_relation b  where a.relationshipno = ? and "
					+ " a.status = ? and  a.relationshipno = b.relationshipno  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "A"); // This can be used to 
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 hashPorteAssetDetails = new ConcurrentHashMap<String, PorteAsset>();
				 	while(rs.next()){	
				 		porteCoin = new PorteAsset();
				 		porteCoin.setWalletId(StringUtils.trim(rs.getString("walletid")));
				 		porteCoin.setRelationshipNo(StringUtils.trim(rs.getString("relationshipno")));
				 		porteCoin.setAssetCode(StringUtils.trim(rs.getString("assetcode")));
				 		porteCoin.setAssetDescription(StringUtils.trim(rs.getString("walletdesc")));
				 		porteCoin.setPublicKey(StringUtils.trim(rs.getString("public_key")));
				 		hashPorteAssetDetails.put(porteCoin.getAssetCode(),  porteCoin);

				 	}
			 }	
			 if(hashPorteAssetDetails!=null)
				 if(hashPorteAssetDetails.size()==0)
					 hashPorteAssetDetails=null;
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
		return hashPorteAssetDetails;
	}
	public String getPorteWalletDetail(String relationshipNo, String assetCode, String publicKey) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String walletIdInternal=null;
//		/String walletDetails =null;
			try {
				connection = super.getConnection();	
				NeoBankEnvironment.setComment(3,className,"in getPorteWalletDetails is  "+assetCode+ " publicKey "+ publicKey );

				query = "select a.walletid, a.status  from wallet_details_external a, stellar_account_relation b where a.relationshipno = b.relationshipno  "
						+ "and b.public_key = ? and a.assetcode = ? ";
				pstmt=connection.prepareStatement(query);
				pstmt.setString(1, publicKey);
				pstmt.setString(2, assetCode);
				rs= (ResultSet)pstmt.executeQuery();
				if (rs!=null) {
					while (rs.next()) {
						//walletbalance= StringUtils.trim(rs.getString("currbal"));
						walletIdInternal= StringUtils.trim(rs.getString("walletid"));
						//walletStatus= StringUtils.trim(rs.getString("status")); //TODO To see if we can use this to block transaction or?
					}
				}
//				if(walletIdInternal!=null ) {
//					walletDetails=walletId.concat(",").concat(walletbalance);
//				}
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
			NeoBankEnvironment.setComment(3,className,"walletIdInternal is  "+walletIdInternal);

		return walletIdInternal;
	}
	
	public String getAssetAccountId(String relationshipNo) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String accountId = null;
		try{
			connection = super.getConnection();	
			query =   " select  a.public_key public_key from  stellar_account_relation a where a.relationshipno = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 	while(rs.next()){	
						accountId =Utilities.decryptString( StringUtils.trim(rs.getString("public_key")));
						
				 	}
			 }	
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getAssetAccountId  is  "+e.getMessage());
			throw new Exception ("The exception in method getAssetAccountId  is  "+e.getMessage());			
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
		return accountId;
	}
		
	public Boolean buyPorteCoinViaFiatWallet(String relationshipNo, String fiatWalletId, String amount,
			String payComments, String referenceNo, String txnUserCode, String txnPayMode, String assetCode,
			 String porteWalletId, double newWalletBalanceFromStellar,
			String customerCharges, String amountPorte, String fiatTransactionCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String transactionDatetime = null;
		String currencyId= assetCode;
		String fiatWalletTxnMode = "D";
		String porteWalletTxnMode = "C";
		String fiatWalletTransactionCode2 = null;
		String porteWalletTransactionCode = null;
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
			fiatWalletTransactionCode2 = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
		    connection = super.getConnection();
		    connection.setAutoCommit(false);
		    
		    //********Step 1*********\\\\	
			 double fiatWalletDebitAmount =  Double.parseDouble(amount) + Double.parseDouble(customerChargesValue);
			 
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
			
			// **********Step 5: Record the wallet transaction ledger
			
													// 1		2			3			4			5				6		7			8
			query = "insert into txn_wallet_cust_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, txnusercode, pymtchannel) "
			+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?) ";
					// 1  2  3  4  5  6   7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, fiatTransactionCode); 			
			pstmt.setString(2, fiatWalletId); 				
			pstmt.setString(3, systemreference );							
			pstmt.setBigDecimal(4, new BigDecimal(amount));  
			pstmt.setString(5, NeoBankEnvironment.getUSDCurrencyId());

			pstmt.setString(6, fiatWalletTxnMode); // Debit as it is a payment by the sender
			pstmt.setString(7, transactionDatetime);
			pstmt.setString(8, txnUserCode);
			pstmt.setString(9, "W");
			try {
				NeoBankEnvironment.setComment(3,className,"Executed Sender wallet_tnx_bc amount"+"fiatTransactionCode is"+ fiatTransactionCode +"fiatWalletId is"+fiatWalletId 
				+"systemreference is"+systemreference +"payAmount is"+amount +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
			if( pstmt!=null)	pstmt.close();
			
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
			query = "insert into txn_business_ledger_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime, accrued_balance, pymtchannel) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, porteWalletTransactionCode); 
			pstmt.setString(2, txnPayMode);
			pstmt.setString(3, fiatWalletId);
			pstmt.setString(4, "");
			pstmt.setString(5, systemreference);			// Additional charges for customer						
			pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(amount)));  
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
			
			
			// **********Step 5.2 add to Buy coins transaction table
			//			                           1		2			3			4		  5				6			   7			   8		 9          10      11
			query = "insert into txn_buy_coins (txn_code, walletid, custrelno, asset_code, txnamount, sysreference_ext, sysreference_int, status, txndate, coin_amount, commet ) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
					// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, porteWalletTransactionCode); 
			pstmt.setString(2, porteWalletId);
			pstmt.setString(3, relationshipNo);
			pstmt.setString(4, assetCode);
			pstmt.setBigDecimal(5, BigDecimal.valueOf( Double.parseDouble(amount)));
			pstmt.setString(6, "");								
			pstmt.setString(7, systemreference);								
			pstmt.setString(8, "N"); 
			pstmt.setString(9, transactionDatetime);
			pstmt.setString(10, amountPorte);
			pstmt.setString(11, payComments);
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query +" "+e.getMessage());
			}					
			if( pstmt!=null)pstmt.close();
						
			
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
				
				pointsaccrued = Double.toString((Double.parseDouble(amount) * Double.parseDouble(pointsConversion)));
				NeoBankEnvironment.setComment(3,className,"********* pointsaccrued   "+ pointsaccrued + " amount "+ amount  +" pointsConversion "+pointsConversion);
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
			
			connection.commit();
			result = true;
			
		}catch(Exception e){
			result = false;
			connection.rollback();
			NeoBankEnvironment.setComment(1,className,"The exception in method buyPorteCoinViaFiatWallet  is  "+e.getMessage());
			throw new Exception ("The exception in method buyPorteCoinViaFiatWallet  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
	
			if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(porteWalletTransactionCode!=null)  porteWalletTransactionCode=null;
			if(userType!=null)  userType=null; if(fiatTransactionCode!=null)  fiatTransactionCode=null; if(fiatWalletTransactionCode2!=null)  fiatWalletTransactionCode2=null; if(txnUserCode != null) txnUserCode = null;
			if(systemreference!=null)  systemreference=null; if(currencyId!=null)  currencyId=null; if ( totalAccruedBalance!=null) totalAccruedBalance=null;
			if(porteWalletTxnMode!=null)  porteWalletTxnMode=null; if ( fiatWalletTxnMode!=null) fiatWalletTxnMode=null;
		
			}
		
		return result;
		
	}
	
	public Boolean buyPorteCoinViaToken(String relationshipNo, String tokenId, String amount, String payComments,
			String referenceNo, String txnUserCode, String customerCharges, String txnPayMode, String assetCode,
			String extSystemRef, String porteWalletId, double newWalletBalanceFromStellar, String amountPorte) throws Exception {
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
			amount = amount.replaceAll(",", "");
			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");
			transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			transactionCode2 = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
			connection = super.getConnection();
			connection.setAutoCommit(false);
			//********Step 1 update wallet ballance*********\\\\	
			
			//Please remove this code after integration, just 
			/* String previousWalletBalance = null;
			 query = "select currbal  from wallet_details where walletid=?  ";
			 pstmt = connection.prepareStatement(query);
			 pstmt.setString(1, porteWalletId);
			 rs = (ResultSet)pstmt.executeQuery();	
			 if(rs!=null){
			 	while(rs.next()){	 			 			
					 	previousWalletBalance = (StringUtils.trim(rs.getString("currbal"))  );
					 	NeoBankEnvironment.setComment(3,className,"The pointbalance   is " + previousWalletBalance );
					} // end of while
				} //end of if rs!=null check
			 if(pstmt!=null)  pstmt.close(); if(rs!=null)  rs.close();
			 newWalletBalance  = Double.parseDouble(previousWalletBalance) + Double.parseDouble(amountPorte);
			
			 //***update wallet ballance
			query = "update wallet_details set currbal =  ? , lastupdated = ?  where walletid=?  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setBigDecimal(1, BigDecimal.valueOf(newWalletBalance));  
			pstmt.setString(2, transactionDatetime);  	
			pstmt.setString(3, porteWalletId);
			try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}
			if(pstmt!=null)  pstmt.close();		if(rs!=null)  rs.close();
			NeoBankEnvironment.setComment(3,className,"The currbal after Topup is " +newWalletBalance );
			*/
	
			
			// **********Step 2: Record the txn_cardtoken_bc
			
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
			pstmt.setBigDecimal(6, new BigDecimal(amount));
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
			pstmt.setString(3, porteWalletId);
			pstmt.setString(4, "");
			pstmt.setString(5, systemreference);			// Additional charges for customer						
			pstmt.setBigDecimal(6, BigDecimal.valueOf( Double.parseDouble(amount)));  
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
			
			
			// **********Step 5.2 add to Buy coins transaction table
			//			                           1		2			3			4		  5				6			   7			   8		 9          10      11
			query = "insert into txn_buy_coins (txn_code, walletid, custrelno, asset_code, txnamount, sysreference_ext, sysreference_int, status, txndate, coin_amount, commet ) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
					// 1  2  3  4  5  6  7  8  9  10 11
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 
			pstmt.setString(2, porteWalletId);
			pstmt.setString(3, relationshipNo);
			pstmt.setString(4, assetCode);
			pstmt.setBigDecimal(5, BigDecimal.valueOf( Double.parseDouble(amount)));
			pstmt.setString(6, "");								
			pstmt.setString(7, systemreference);								
			pstmt.setString(8, "N"); 
			pstmt.setString(9, transactionDatetime);
			pstmt.setString(10, amountPorte);
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
				pointsaccrued = Double.toString((Double.parseDouble(amount) * Double.parseDouble(pointsConversion)));
				NeoBankEnvironment.setComment(3,className,"******** pointsaccrued   "+ pointsaccrued +" amountPorte "+ amountPorte + " pointsConversion "+ pointsConversion);
			   if(previousPoinsBalance==null) { // For the first time 
				  firstime =true;
				  NeoBankEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +porteWalletId); 
			 
			   }else {
				   NeoBankEnvironment.setComment(3,	className," Previous Point balance for user : " +porteWalletId+ " is  " +previousPoinsBalance); 
			   }	 

				//***** Step 6.2****: Insert loyalty points for the sender user		
				 
						                             //	  	1		2				3			4			5			6				7        8        9
				query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, status, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?,?) ";
							//		   1  2  3  4  5  6  7  8 9
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, porteWalletId); 	
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
			NeoBankEnvironment.setComment(1,className,"The exception in method buyPorteCoinViaToken is  "+e.getMessage());
			throw new Exception ("The exception in method buyPorteCoinViaToken  is  "+e.getMessage());
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
	
	public List<Transaction> getPendingTransactions(String realationshipNo)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Transaction transaction = null;
		List<Transaction> transactionList=null;
		try{
			
			connection = super.getConnection();	
			query = " select txn_code,  walletid,  custrelno,  asset_code, coin_amount,  sysreference_int, txndate, status"
					+ " from txn_buy_coins  where custrelno = ? and status = ? order by txndate desc  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, realationshipNo);
			pstmt.setString(2, "N");
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
			 //if(transactionList!=null)
				// if(transactionList.size()==0)
					// transactionList=null;
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
	public String getPorteWalletDetails(String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String walletIdInternal=null;
//		/String walletDetails =null;
			try {
				connection = super.getConnection();	
				
				query = "select  a.walletid  from  wallet_details a where  a.relationshipno = ? and  a.status = ? ";
				pstmt=connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, "A");
				rs= (ResultSet)pstmt.executeQuery();
				if (rs!=null) {
					while (rs.next()) {
						walletIdInternal= StringUtils.trim(rs.getString("walletid"));
					}
				}
//				if(walletIdInternal!=null ) {
//					walletDetails=walletId.concat(",").concat(walletbalance);
//				}
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
		return walletIdInternal;
	}
	




	
	

}
