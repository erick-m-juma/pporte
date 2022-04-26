package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import com.pporte.NeoBankEnvironment;
import com.pporte.utilities.Utilities;

public class FundStellarAccountDao extends HandleConnections{
	public static String className = FundStellarAccountDao.class.getName();

	public boolean updateStellarAccRel(String status, String publicKey, String amount) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		
		NeoBankEnvironment.setComment(3,className,"status  is  "+status);
		
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			 query = "update stellar_account_relation set  status=?,stellar_amount=? where public_key=?";
			 pstmt = connection.prepareStatement(query);
			pstmt.setString(1, status);
			pstmt.setString(2, amount);
			pstmt.setString(3, Utilities.encryptString(publicKey));
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}
				connection.commit();
				result = true;
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
			if(pstmt!=null) pstmt.close(); 
		}
		return result;
	}
	

	

	public String getMnemonicKey(String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String nmemonicKey= null;
		
		try {
			connection = super.getConnection();	                    
			query = " select mnemonic_code from mnemonic_cust_relation_bc where relationshipno = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
			 	while(rs.next()){	 
			 		nmemonicKey = Utilities.decryptString(StringUtils.trim(rs.getString("mnemonic_code")));
			 		} 
			 } 
	
		} catch (Exception e) {
			nmemonicKey = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getMnemonicKey  is  "+e.getMessage());	
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
		return nmemonicKey;
	}

	public String getWalletBalance( String relationshipNo) throws Exception{
		NeoBankEnvironment.setComment(3, className, "Inside getWalletBalance method");
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String walletBalance= null;
		try {
			
			connection = super.getConnection();
			query = " select a.currbal currbal from wallet_details a where relationshipno = ? and wallettype=? ";
			NeoBankEnvironment.setComment(3,className,"After query");
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "F");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
			 	while(rs.next()){	 
			 		walletBalance=Utilities.getMoneyinDecimalFormat(StringUtils.trim(rs.getString("currbal")));
			 		} 
			 	NeoBankEnvironment.setComment(3,className,"walletBalance dao "+walletBalance);	
			 }
		} catch (Exception e) {
			walletBalance = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getWalletBalance  is  "+e.getMessage());	
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
		return walletBalance;
	}

}
