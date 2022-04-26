package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import com.pporte.NeoBankEnvironment;
import com.pporte.utilities.Utilities;

public class CustomerDao extends HandleConnections{
	public static String className = CustomerDao.class.getName();
	
	
	public boolean checkIfHasWallet(String assetCode, String relationshipNo)  throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String walletId = null;
		//PPWalletEnvironment.setComment(3,className,"emailId  is  "+emailId);
		boolean result = false;
		try{
			connection = super.getConnection();
			query = " select  walletid from wallet_details_external   where relationshipno = ? and assetcode = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, assetCode);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	
					walletId = StringUtils.trim(rs.getString("walletid"));
				 	} // end of while
				 } //end of if
			 
			 if(walletId != null) {
				 result = true;
			 }
		}catch(Exception e){
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method checkIfHasWallet  is  "+e.getMessage());	
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

	

	public boolean insertReceiverWalletForRegistration(String receiverWalletId, String senderRegNo, String receiverRelationNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		try{

			connection = super.getConnection();	
			 connection.setAutoCommit(false);
			  //                                                       1             2                     3                  4         5
			 query = "insert into customer_receiver_wallet_rel (relationshipno, receiverwalletid, receiver_relationshipno,  status , createdon) "
						+ "values (?, ?, ?, ? , ?) ";
						//		   1  2  3  4  5
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, (senderRegNo)); 
				pstmt.setString(2, ( receiverWalletId)); 
				pstmt.setString(3, ( receiverRelationNo)); 				
				pstmt.setString(4, ("A")); 
				pstmt.setString(5,  Utilities.getMYSQLCurrentTimeStampForInsert() ); 

				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}	
			connection.commit();
			result = true;

		}catch(Exception e){
			connection.rollback(); result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method insertReceiverWalletForRegistration  is  "+e.getMessage());
			throw new Exception ("The exception in method insertReceiverWalletForRegistration  is  "+e.getMessage());			
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


	public String getPublicKey(String relationshipNo) throws Exception {
		NeoBankEnvironment.setComment(3,className," Inside getAllRegisteredWalletsForSender method");
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String publicKey = null;
		try {
			connection = super.getConnection();	                    
			query = " select public_key from stellar_account_relation where relationshipno = ? ";
			
			NeoBankEnvironment.setComment(3,className,"After query");

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
			 	while(rs.next()){	 
			 		publicKey = Utilities.tripleDecryptData(StringUtils.trim(rs.getString("public_key")));
			 		} 
			 } 
			 
			 NeoBankEnvironment.setComment(3,className,"After decrypt");
	
		}catch(Exception e) {
			publicKey = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getPublicKey  is  "+e.getMessage());		
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
	
		return publicKey;
	}
	
	public String getmnemonicCode(String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String mnemonicCode = null;
		try {
		
			connection = super.getConnection();	                    
			query = " select mnemonic_code from mnemonic_cust_relation_bc where relationshipno = ? and network=? ";
			
			NeoBankEnvironment.setComment(3,className,"After query in getmnemonicCode");

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "S");
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
			 	while(rs.next()){	 
			 		mnemonicCode = Utilities.tripleDecryptData( StringUtils.trim(rs.getString("mnemonic_code")));
			 		} 
			 	NeoBankEnvironment.setComment(3, className, "Mnemonic from db is "+mnemonicCode);
			 } 
	
		}catch(Exception e) {
			mnemonicCode = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getmnemonicCode  is  "+e.getMessage());		
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
	
		return mnemonicCode;
	}
	
	public boolean checkIfPasswordIsCorrect(String relationshipNo, String password) throws Exception {
	
	
		boolean isCorrect = false;
		
	
		return isCorrect;
	}
	
	public String checkIfUserHasMnemonicCode (String relationshipNo) throws Exception {
		NeoBankEnvironment.setComment(3, className, "Relno "+relationshipNo);
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String mnemonicFlag = null;
		try {
			connection = super.getConnection();	  			
			query = " select tokenized_mnemonic from stellar_account_relation where relationshipno = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
			 	while(rs.next()){	 
			 		mnemonicFlag = StringUtils.trim(rs.getString("tokenized_mnemonic"));
			 	} 
			 } 
			 NeoBankEnvironment.setComment(3, className, "Mnemonic flag is "+mnemonicFlag);
		}catch(Exception e) {
			mnemonicFlag = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method checkIfUserHasMnemonicCode  is  "+e.getMessage());		
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
	
		return mnemonicFlag;
	}

	
	
	public String getStellarMnemonicCode(String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String mnemonicCode = null;
		try {
		
			connection = super.getConnection();	                    
			query = " select mnemonic_code from mnemonic_cust_relation_bc where relationshipno = ? and network=? ";
			
			//NeoBankEnvironment.setComment(3,className,"After query");

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "S");
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
			 	while(rs.next()){
			 		mnemonicCode=Utilities.tripleDecryptData(( StringUtils.trim(rs.getString("mnemonic_code"))));
			 		} 
			 } 
	
		}catch(Exception e) {
			mnemonicCode = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getmnemonicCode  is  "+e.getMessage());		
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
	
		return mnemonicCode;
	}
	 
	
	
	
}
