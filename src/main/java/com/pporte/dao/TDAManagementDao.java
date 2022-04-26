package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.pporte.NeoBankEnvironment;
import com.pporte.model.AssetAccount;
import com.pporte.model.AssetCoin;
import com.pporte.model.Transaction;
import com.pporte.utilities.Utilities;

public class TDAManagementDao extends HandleConnections {
	private static String className = TDAManagementDao.class.getSimpleName();

	public boolean checkIfBTCxAccountHasBeenLinked() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String publicKey = null;
		boolean result = false;
		try{
			connection = super.getConnection();
			query = " select public_key from wallet_assets_account where asset_code = ? and account_type=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, NeoBankEnvironment.getStellarBTCxCode());
			pstmt.setString(2, "DA"); // Distribution Account
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	
					publicKey = StringUtils.trim(rs.getString("asset_distribution_account"));
				 	} // end of while
				 } //end of if
			 
			 if(publicKey != null) {
				 result = true;
			 }
		}catch(Exception e){
			result = false;
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
				if(pstmt!=null) pstmt.close(); if (publicKey!=null)publicKey=null;
			}
		
		return result;
	}
	public String getBTCxDistributionAccount() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String btcxDistributionAccount = null;
		try{
			connection = super.getConnection();
			query = "select public_key from wallet_assets_account where asset_code = ? and account_type=? and status=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1,NeoBankEnvironment.getStellarBTCxCode());
			pstmt.setString(2,"DA");// Distribution Account
			pstmt.setString(3,"A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	
					btcxDistributionAccount = StringUtils.trim(rs.getString("public_key"));
				} // end of while
			} //end of if
			 if (btcxDistributionAccount.equals("")) {
				 btcxDistributionAccount=null;
			 }
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
		
		return btcxDistributionAccount;
	}
	public String getBTCxIssuingAccount() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String btcxIssuingAccount = null;
		try{
			connection = super.getConnection();
			query = "select public_key from wallet_assets_account where asset_code = ? and account_type=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, NeoBankEnvironment.getStellarBTCxCode());
			pstmt.setString(2, "IA"); // Issuing account
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	
					btcxIssuingAccount = StringUtils.trim(rs.getString("public_key"));
				} // end of while
			} //end of if
			 if (btcxIssuingAccount.equals("")) {
				 btcxIssuingAccount=null;
			 }
			 NeoBankEnvironment.setComment(3, className, "BTCx issuing account is "+btcxIssuingAccount);
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
		
		return btcxIssuingAccount;
	}
	
	public Boolean linkStellarBTCxAccount(String publicKey, String assetCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		try{

			connection = super.getConnection();	
			connection.setAutoCommit(false);
			 
			  //                                            1              2          3		    4	    5
			 query = "insert into wallet_assets_account  (asset_code, public_key, account_type,status, createdon)"
			 		+"value (?,?,?,?,?)";	
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, (assetCode)); 
			pstmt.setString(2, ( publicKey)); 
			pstmt.setString(3, ( "DA")); // Distribution Account
			pstmt.setString(4, ( "A")); // Active
			pstmt.setString(5, ( Utilities.getMYSQLCurrentTimeStampForInsert())); 

			try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}	
			if(pstmt!=null) pstmt.close();
			
			connection.commit();
			result = true;

		}catch(Exception e){
			result = false;
			connection.rollback(); result = false;
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
		return result;
	}
	
	public Boolean saveBTCXIssuerAccount(String publicKey, String assetCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		try{
			
			connection = super.getConnection();	
			connection.setAutoCommit(false);
			
			  //                                                1              2              3
			query = "insert into wallet_assets_account  (asset_code, public_key, account_type,status, createdon)"
			 		+"value (?,?,?,?,?)";	
			//		   1  2
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, (assetCode)); 
			pstmt.setString(2, ( publicKey)); 
			pstmt.setString(3, ( "IA")); // Issuing Account
			pstmt.setString(4, ( "A")); // Active
			pstmt.setString(5, ( Utilities.getMYSQLCurrentTimeStampForInsert())); 
			
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}	
			if(pstmt!=null) pstmt.close();
			
			connection.commit();
			result = true;
			
		}catch(Exception e){
			result = false;
			connection.rollback(); result = false;
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
		return result;
	}
	
	public ArrayList<AssetAccount> getBTCXAccountDetails (String assetCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList <AssetAccount> arrAssetAccountDetails=null;
		AssetAccount m_AssetAccount=null;
		try{
			connection = super.getConnection();
			query = " select asset_code, public_key, status, account_type, createdon  from wallet_assets_account where asset_code = ? order by createdon desc";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				arrAssetAccountDetails = new ArrayList <AssetAccount>();
				while(rs.next()){	
					m_AssetAccount=new AssetAccount();
					m_AssetAccount.setAssetCode(StringUtils.trim(rs.getString("asset_code"))); 
					m_AssetAccount.setPublicKey(StringUtils.trim(rs.getString("public_key"))); 
					m_AssetAccount.setAccountType(StringUtils.trim(rs.getString("account_type"))); 
					m_AssetAccount.setStatus(StringUtils.trim(rs.getString("status"))); 
					m_AssetAccount.setCreatedOn(StringUtils.trim(rs.getString("createdon"))); 
					arrAssetAccountDetails.add(m_AssetAccount);
				} // end of while
				if(arrAssetAccountDetails!=null)
					 if(arrAssetAccountDetails.size()==0) 
						 arrAssetAccountDetails=null;
			} //end of if
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
		return arrAssetAccountDetails;
		
		
	}
	public ArrayList<AssetCoin> getFiatToAssetPricing(String assetCode) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<AssetCoin> arrBTCxPricing = null;	
		AssetCoin m_AssetCoin=null;

		try {
			connection = super.getConnection();	
			query = " select assetcode, seq_no,  selling_rate, status, createdon from asset_distribution_pricing where assetcode=? order by createdon desc";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);

			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				arrBTCxPricing =  new ArrayList<AssetCoin>();
				while(rs.next()){
					m_AssetCoin=new AssetCoin();
					m_AssetCoin.setAssetCode(StringUtils.trim(rs.getString("assetcode")));
					m_AssetCoin.setSellingRate(Utilities.getMoneyinDecimalFormat(StringUtils.trim(rs.getString("selling_rate"))));
					m_AssetCoin.setStatus(StringUtils.trim(rs.getString("status")));
					m_AssetCoin.setCreatedOn(StringUtils.trim(rs.getString("createdon")));
					m_AssetCoin.setSequenceNo(StringUtils.trim(rs.getString("seq_no")));
					arrBTCxPricing.add(m_AssetCoin);
				}// end of while
				if(arrBTCxPricing!=null)
					 if(arrBTCxPricing.size()==0) 
						 arrBTCxPricing=null;
			}// end of if		 
			
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
		return arrBTCxPricing;
	}
	public ArrayList<AssetCoin> getAssetExchange(String sourceAssetCode, String destinationAssetCode) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<AssetCoin> arrAssetExchangePricing = null;	
		AssetCoin m_AssetCoin=null;
		try {
			connection = super.getConnection();	
			query = " select sequenceno, source_asset,  destination_asset, exchange_rate,createdon, status from asset_exchange_rates where source_asset=? and destination_asset=? order by createdon desc";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, sourceAssetCode);
			pstmt.setString(2, destinationAssetCode);

			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				arrAssetExchangePricing =  new ArrayList<AssetCoin>();
				while(rs.next()){
					m_AssetCoin=new AssetCoin();
					m_AssetCoin.setSourceAssetCode(StringUtils.trim(rs.getString("source_asset")));
					m_AssetCoin.setDestinationAssetCode(StringUtils.trim(rs.getString("destination_asset")));
					m_AssetCoin.setExchangeRate(Utilities.getMoneyinDecimalFormat(StringUtils.trim(rs.getString("exchange_rate"))));
					m_AssetCoin.setStatus(StringUtils.trim(rs.getString("status")));
					m_AssetCoin.setCreatedOn(StringUtils.trim(rs.getString("createdon")));
					m_AssetCoin.setSequenceNo(StringUtils.trim(rs.getString("sequenceno")));
					arrAssetExchangePricing.add(m_AssetCoin);
				}// end of while
				if(arrAssetExchangePricing!=null)
					 if(arrAssetExchangePricing.size()==0) 
						 arrAssetExchangePricing=null;
			}// end of if		 
			
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
		return arrAssetExchangePricing;
	}
	public boolean addNewAssetPricing(String assetCode, String assetSellRate,
			String priceStatus)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			//Inactivate any other active offer
			 query = " update asset_distribution_pricing set  status=? where assetcode=? ";
				
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "I"); 						 
				pstmt.setString(2, assetCode);	
				try {
					pstmt.executeUpdate();
				    }catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
				    }
				pstmt.close();
		 
				                                          //		 1		  2			    3       4	      		   
				query = " insert into asset_distribution_pricing (assetcode, selling_rate, status, createdon ) "
						+ "values ( ?,  ?,  ?, ?) ";
						  //		1   2   3  4
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, assetCode); 						 
					pstmt.setString(2, assetSellRate);						 
					pstmt.setString(3, "A");					 
					pstmt.setString(4, Utilities.getMYSQLCurrentTimeStampForInsert());					 

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
	public boolean addNewAssetExchangePricing(String sourceAssetCode, String destinationCode, String assetExchangeRate,
			String priceStatus)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			connection = super.getConnection();
			connection.setAutoCommit(false);
			
			//Inactivate any other active offer
			query = " update asset_exchange_rates set  status=? where source_asset=? and destination_asset=? ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "I"); 						 
			pstmt.setString(2, sourceAssetCode);	
			pstmt.setString(3, destinationCode);	
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}
			pstmt.close();
			
			                                      //		 1		     2			      3             4	     5     		   
			query = " insert into asset_exchange_rates (source_asset,destination_asset,exchange_rate, status, createdon ) "
					+ "values ( ?,  ?,  ?, ?, ?) ";
			           //		1   2   3  4  5
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, sourceAssetCode); 						 
			pstmt.setString(2, destinationCode); 						 
			pstmt.setString(3, assetExchangeRate);						 
			pstmt.setString(4, "A");					 
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
	public boolean editAssetPricing(String assetCode, String assetSellRate,
			String priceStatus, String sequencId)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
		 
			            //                                            1           2                             
				 query = " update asset_distribution_pricing set status=? where seq_no=? ";
				
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, priceStatus);					 
					pstmt.setInt(2,  Integer.parseInt(sequencId));	
				
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
	public boolean editAssetExchangePricing(String status,  String sequencId)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			connection = super.getConnection();
			connection.setAutoCommit(false);
			
			//                                              1           2                             
			query = " update asset_exchange_rates set status=? where sequenceno=? ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, status);					 
			pstmt.setInt(2,  Integer.parseInt(sequencId));	
			
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
	public ArrayList<AssetCoin> getPorteAssetsMarkUpRates() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<AssetCoin> arrPorteAssetsToBTCxMarkUpRates = null;	
		try {
			connection = super.getConnection();	
			
			query = "select seqeunce_no, asset_code, markup_rate, status, createdon from porte_assets_to_btcx_markup order by createdon desc";
			pstmt = connection.prepareStatement(query);

			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				arrPorteAssetsToBTCxMarkUpRates =  new ArrayList<AssetCoin>();
				while(rs.next()){
					AssetCoin m_AssetCoin=new AssetCoin();
					m_AssetCoin.setAssetCode(StringUtils.trim(rs.getString("asset_code")));
					m_AssetCoin.setMarkupRate(StringUtils.trim(rs.getString("markup_rate")));
					m_AssetCoin.setStatus(StringUtils.trim(rs.getString("status")));
					m_AssetCoin.setCreatedOn(StringUtils.trim(rs.getString("createdon")));
					m_AssetCoin.setSequenceNo(StringUtils.trim(rs.getString("seqeunce_no")));
					arrPorteAssetsToBTCxMarkUpRates.add(m_AssetCoin);
				}
				if(arrPorteAssetsToBTCxMarkUpRates!=null)
					 if(arrPorteAssetsToBTCxMarkUpRates.size()==0) 
						 arrPorteAssetsToBTCxMarkUpRates=null;
			}			 
			
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
		return arrPorteAssetsToBTCxMarkUpRates;
	}
	
	public boolean addNewPorteAssetMarkupRate(String assetCode, String porteAssetMarkupRate,
			String priceStatus)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			//Inactivate any other active offer
			 query = " update porte_assets_to_btcx_markup set  status=? where asset_code=? ";
				
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "I"); 						 
				pstmt.setString(2, assetCode);	
				try {
					pstmt.executeUpdate();
				    }catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
				    }
				pstmt.close();
		 
				                                          //		 1		  2			    3       4	      		   
				query = " insert into porte_assets_to_btcx_markup (asset_code, markup_rate, status, createdon ) "
						+ "values ( ?,  ?,  ?, ?) ";
						  //		1   2   3  4
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, assetCode); 						 
					pstmt.setString(2, porteAssetMarkupRate);						 
					pstmt.setString(3, "A");					 
					pstmt.setString(4, Utilities.getMYSQLCurrentTimeStampForInsert());					 

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
	
	public boolean editPorteAssetMarkupRate(String assetCode, String porteAssetMarkupRate,
			String priceStatus, String sequencId)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
		 
			            //                                            1           2                             
				 query = " update porte_assets_to_btcx_markup set status=? where seqeunce_no=? ";
				
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, priceStatus);					 
					pstmt.setInt(2,  Integer.parseInt(sequencId));	
				
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
	
	public List<Transaction> getBTCxFromFiatRequests() throws Exception{
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
		  		 + " and a.custrelno = c.relationshipno and a.status = ? and a.asset_code=? order by a.txndate desc ";
		  
		 pstmt = connection.prepareStatement(query);
		 pstmt.setString(1, "N");
		 pstmt.setString(2, NeoBankEnvironment.getStellarBTCxCode());
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
			 		m_Transaction.setCoinAmount(Utilities.formatToSevenDecimalPlace(StringUtils.trim(rs.getString("coin_amount"))));
			 		m_Transaction.setCustomerName(StringUtils.trim(rs.getString("customername")));
			 		m_Transaction.setCustomerId(Utilities.tripleDecryptData(StringUtils.trim(rs.getString("customerid"))));
			 		m_Transaction.setComment(StringUtils.trim(rs.getString("commet")));
//			 		m_Transaction.setPublicKey(StringUtils.trim(rs.getString("pulickey")));
			 		m_Transaction.setPublicKey(Utilities.decryptString( StringUtils.trim(rs.getString("pulickey"))));
			 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndate")));
			 		aryTransactions.add(m_Transaction);
		 		} // end of while						 	new BigDecimal(tabOfFloatString[l]).toPlainString()
		 	} //end of if 
		  	if(aryTransactions!=null)
		  		if(aryTransactions.size()==0)
		  			aryTransactions=null;			  
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
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
	public ArrayList<Transaction> getBTCxFromPorteAssetsRequests() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Transaction> aryTransactions = null;
		try {
		  connection = super.getConnection();	
		  
		  query = 	"select a.txncode txncode,a.txndate txndate, a.custrelno custrelno, "
			  		 + "a.source_amount source_amount, a.source_asset source_asset, a.destination_amount destination_amount,a.destination_asset destination_asset, "
			  		 + "a.sysreference_ext sysreference_ext,a.sysreference_int sysreference_int,a.status status,a.paytype paytype,"
			  		 + "b.customername customername, b.customerid, b.customerid customerid, c.public_key pulickey from txn_btc_exchange a, "
			  		 + "customer_details b, stellar_account_relation c where a.custrelno=b.relationshipno "
			  		 + "and a.custrelno = c.relationshipno and a.status = ? order by a.txndate desc ";
	
		  
		    pstmt = connection.prepareStatement(query);
			 pstmt.setString(1, "N");
			 rs = pstmt.executeQuery();
			  if(rs!=null){	
				  aryTransactions = new ArrayList<Transaction>();
				 	while(rs.next()){	
				 		Transaction m_Transaction = new Transaction();
				 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode")));
				 		m_Transaction.setRelationshipNo( StringUtils.trim(rs.getString("custrelno")));
				 		m_Transaction.setSourceAmount( StringUtils.trim(rs.getString("source_amount")));
				 		m_Transaction.setSourceAssetCode( StringUtils.trim(rs.getString("source_asset")));
				 		m_Transaction.setDestinationAmount( StringUtils.trim(rs.getString("destination_amount")));
				 		m_Transaction.setDestinationAssetCode( StringUtils.trim(rs.getString("destination_asset")));
				 		m_Transaction.setCustomerName(StringUtils.trim(rs.getString("customername")));
				 		m_Transaction.setCustomerId(StringUtils.trim(rs.getString("customerid")));
				 		//m_Transaction.setPublicKey(StringUtils.trim(rs.getString("pulickey")));
				 		m_Transaction.setPublicKey(Utilities.decryptString( StringUtils.trim(rs.getString("pulickey"))));
				 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndate")));
				 		aryTransactions.add(m_Transaction);
			 		} // end of while						 	
			 	} //end of if 
			  	if(aryTransactions!=null)
			  		if(aryTransactions.size()==0)
			  			aryTransactions=null;			  
			}catch(Exception e){
				NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
				throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
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
	
public boolean updateCustomerDetails(String transactionCode, String externalRef) throws Exception {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);	 

			 //                                      1                 2             3          
			query = "update txn_btc_exchange set  status=?, sysreference_ext=? where txncode=? "; 
		    pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P"); 		// Processed				 
			pstmt.setString(2, externalRef);						 
			pstmt.setString(3, transactionCode);						 
								 
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); 
			
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
		public boolean saveBTCAddress(String btcAddress, String status, String assetCode) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			boolean result = false;
			try{
				
				connection = super.getConnection();	
				connection.setAutoCommit(false);
				
				  //                                                1              2              3
				query = "insert into wallet_assets_account  (asset_code, public_key, account_type,status, createdon)"
				 		+"value (?,?,?,?,?)";	
				//		   1  2
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, (assetCode)); 
				pstmt.setString(2, ( btcAddress)); 
				pstmt.setString(3, ( "DA")); // Distribution Account
				pstmt.setString(4, ( "A")); // Active
				pstmt.setString(5, ( Utilities.getMYSQLCurrentTimeStampForInsert())); 
				
				try {
					pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}	
				if(pstmt!=null) pstmt.close();
				
				connection.commit();
				result = true;
				
			}catch(Exception e){
				result = false;
				connection.rollback(); result = false;
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
			return result;
		
		}

		
		public boolean editBTCAddress(String assetCode, String status, String btcAddress)throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
			 
				 NeoBankEnvironment.setComment(3, className, "assetCode "+assetCode+" status "+status+" btcAddress "+btcAddress);
				
				 
				            //                                            1           2            3                          
					 query = "update wallet_assets_account set status=? where asset_code=? and account_type=? and public_key=?";
					
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, status);					 
						pstmt.setString(2,  assetCode);	
						pstmt.setString(3,  "DA");	// Distribution Account
						pstmt.setString(4,  btcAddress);	

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
		
		public ArrayList<Transaction> getFiatToBTCRequests() throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<Transaction> aryTransactions = null;
			try{
			  connection = super.getConnection();	

			  query = " select a.txn_code txn_code, a.walletid walletid,a.txndate txndate, a.custrelno custrelno, "
			  		 + " a.asset_code asset_code, a.txnamount txnamount, a.coin_amount coin_amount, a.commet commet, "
			  		 + " b.customername customername, b.customerid customerid, c.btc_address public_key from txn_buy_coins a, "
			  		 + " customer_details b, bitcoin_account_relation c where a.custrelno=b.relationshipno "
			  		 + " and a.custrelno = c.relationshipno and a.status = ? and a.asset_code=? order by a.txndate desc ";
			  
			 pstmt = connection.prepareStatement(query);
			 pstmt.setString(1, "N");
			 pstmt.setString(2, NeoBankEnvironment.getBitcoinCode());
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
				 		m_Transaction.setCoinAmount(Utilities.formatToSevenDecimalPlace(StringUtils.trim(rs.getString("coin_amount"))));
				 		m_Transaction.setCustomerName(StringUtils.trim(rs.getString("customername")));
				 		m_Transaction.setCustomerId(Utilities.tripleDecryptData(StringUtils.trim(rs.getString("customerid"))));
				 		m_Transaction.setComment(StringUtils.trim(rs.getString("commet")));
				 		m_Transaction.setPublicKey(Utilities.tripleDecryptData( StringUtils.trim(rs.getString("public_key"))));
				 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndate")));
				 		aryTransactions.add(m_Transaction);
			 		} // end of while						 	new BigDecimal(tabOfFloatString[l]).toPlainString()
			 	} //end of if 
			  	if(aryTransactions!=null)
			  		if(aryTransactions.size()==0)
			  			aryTransactions=null;			  
			}catch(Exception e){
				NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
				throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
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
		public ArrayList<Transaction> getBTCTOBTCXRequests() throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<Transaction> aryTransactions = null;
			try {
			  connection = super.getConnection();	
			  
			  query = 	"select a.txncode txncode,a.txndate txndate, a.custrelno custrelno, "
				  		 + "a.source_amount source_amount, a.source_asset source_asset, a.destination_amount destination_amount,a.destination_asset destination_asset, "
				  		 + "a.sysreference_ext sysreference_ext,a.sysreference_int sysreference_int,a.status status,a.paytype paytype,"
				  		 + "b.customername customername, b.customerid, b.customerid customerid, c.public_key publickey from txn_btc_exchange a, "
				  		 + "customer_details b, stellar_account_relation c where a.custrelno=b.relationshipno "
				  		 + "and a.custrelno = c.relationshipno and a.status = ? and source_asset=? and destination_asset=? order by a.txndate desc ";
		
			  
			    pstmt = connection.prepareStatement(query);
				 pstmt.setString(1, "N");
				 pstmt.setString(2, NeoBankEnvironment.getBitcoinCode());// Source Asset
				 pstmt.setString(3, NeoBankEnvironment.getStellarBTCxCode());// Destination Asset
				 rs = pstmt.executeQuery();
				  if(rs!=null){	
					  aryTransactions = new ArrayList<Transaction>();
					 	while(rs.next()){	
					 		Transaction m_Transaction = new Transaction();
					 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode")));
					 		m_Transaction.setRelationshipNo( StringUtils.trim(rs.getString("custrelno")));
					 		m_Transaction.setSourceAmount( StringUtils.trim(rs.getString("source_amount")));
					 		m_Transaction.setSourceAssetCode( StringUtils.trim(rs.getString("source_asset")));
					 		m_Transaction.setDestinationAmount( StringUtils.trim(rs.getString("destination_amount")));
					 		m_Transaction.setDestinationAssetCode( StringUtils.trim(rs.getString("destination_asset")));
					 		m_Transaction.setCustomerName(StringUtils.trim(rs.getString("customername")));
					 		m_Transaction.setCustomerId(Utilities.tripleDecryptData(StringUtils.trim(rs.getString("customerid"))));
					 		m_Transaction.setPublicKey(Utilities.decryptString( StringUtils.trim(rs.getString("publickey"))));
					 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndate")));
					 		aryTransactions.add(m_Transaction);
				 		} // end of while						 	
				 	} //end of if 
				  	if(aryTransactions!=null)
				  		if(aryTransactions.size()==0)
				  			aryTransactions=null;			  
				}catch(Exception e){
					NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());			
					throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
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
}
