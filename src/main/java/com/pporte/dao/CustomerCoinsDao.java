package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.pporte.NeoBankEnvironment;
import com.pporte.utilities.Utilities;

public class CustomerCoinsDao extends HandleConnections {
	
	public static String className = CustomerCoinsDao.class.getName();


	public Boolean registerStellarAccount(String publicKey, String secretKey, String relationshipNo, String assetCode,String tokenizedMnemonic) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		String walletId =null;
		SimpleDateFormat formatter1=null;
		try{

			connection = super.getConnection();	
			connection.setAutoCommit(false);
			 
			  //                                               1             2            3         4         5				6
			 query = " insert into stellar_account_relation (public_key, secret_key, relationshipno, status , createdon,tokenized_mnemonic ) "
						+ " values (?, ?, ?, ? , ?,?) ";
						 //		    1  2  3  4  5   
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, (publicKey)); 
			pstmt.setString(2, ( secretKey)); 
			pstmt.setString(3, ( relationshipNo)); 				
			pstmt.setString(4, ("P")); 
			pstmt.setString(5,  Utilities.getMYSQLCurrentTimeStampForInsert() ); 
			pstmt.setString(6,tokenizedMnemonic);
			try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}	
			if(pstmt!=null) pstmt.close();
			
			formatter1 = new SimpleDateFormat ("yyMMdd");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
			walletId = (formatter1.format(new Date()))+( RandomStringUtils.random(10, false, true)).toString();
			  //                                               1             2            3         4         5        6          7           8
			 query = "insert into wallet_details_external (walletid, relationshipno, walletdesc, usertype , status, assetcode, lastupdated, createdon  ) "
						+ "values (?, ?, ?, ? , ?, ?, ?, ?) ";
						//		   1  2  3  4  5   6  7  8
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, (walletId)); 
			pstmt.setString(2, ( relationshipNo)); 
			pstmt.setString(3, ( "External wallet")); 				
			pstmt.setString(4, ("C")); 
			pstmt.setString(5, ("A")); 
			pstmt.setString(6, (assetCode)); 
			pstmt.setString(7, (Utilities.getMYSQLCurrentTimeStampForInsert())); 
			pstmt.setString(8,  Utilities.getMYSQLCurrentTimeStampForInsert() ); 

			try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}	

			connection.commit();
			result = true;

		}catch(Exception e){
			result = false;
			connection.rollback(); result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method rigisterStellarAccount  is  "+e.getMessage());
			throw new Exception ("The exception in method rigisterStellarAccount  is  "+e.getMessage());			
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
	
	public boolean checkIfStellarHasBeenLinkedByCustomer(String relationshipNo)  throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String publicKey = null;
		//PPWalletEnvironment.setComment(3,className,"emailId  is  "+emailId);
		boolean result = false;
		try{
			connection = super.getConnection();
			query = " select public_key from stellar_account_relation where relationshipno = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	
					publicKey = Utilities.decryptString(StringUtils.trim(rs.getString("public_key")));
					
				 	} // end of while
				 } //end of if
			 
			 if(publicKey != null) {
				 result = true;
			 }
		}catch(Exception e){
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method checkIfStellarHasBeenLinkedByCustomer  is  "+e.getMessage());	
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
	public boolean insertMnemonicCode(String relationshipNo, String encryptedMnemonic) throws Exception{
		PreparedStatement pstmt=null;
		String query=null;
		Connection connection=null;
		boolean result=false;
		try {
			connection = super.getConnection();
			connection.setAutoCommit(false);
				//													1			2		3			4	   5
			query ="insert into mnemonic_cust_relation_bc (relationshipno,mnemonic_code,status,createdon, network)"
					+ "values(?,?,?,?, ?)";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo );
			pstmt.setString(2, encryptedMnemonic );
			pstmt.setString(3, "P" );//Pending to show that the account has not been funded
			pstmt.setString(4,  Utilities.getMYSQLCurrentTimeStampForInsert() );
			pstmt.setString(5,  "S" );
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}pstmt.close();
			connection.commit();			 	
			result = true;
			
		}catch(Exception e) {
			connection.rollback();
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method insertMnemonicCode  is  "+e.getMessage());
			throw new Exception ("The exception in method insertMnemonicCode  is  "+e.getMessage());
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
