package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.pporte.NeoBankEnvironment;
import com.pporte.model.Transaction;
import com.pporte.model.User;
import com.pporte.utilities.Utilities;

public class RemittanceDao extends HandleConnections  {
	public static String className = RemittanceDao.class.getName();


	



	public User getPartnerUserDetails(String userId)  throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		User user = null;
		try{
			connection = super.getConnection();	
			query =" select a.adminid adminid, a.adminname adminname, a.adminemail adminemail, "
					+ " a.accesstype accesstype, a.admincontact admincontact, a.expiry expirydate, "
					+ " b.userid userid, b.location location, b.currency currency,  b.stellarid stellarid, b.status status, b.password_type password_type, "
					+ " b.createdon createdon from admin_details a, partner_details b where a.adminid = b.userid  and a.adminid =? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1,  (Utilities.tripleEncryptData(userId))); 
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 	while(rs.next()){
				 		user = new User(); 
				 		user.setUserId(Utilities.tripleDecryptData(  StringUtils.trim(rs.getString("adminid")) ));
				 		user.setUserName(Utilities.tripleDecryptData(  StringUtils.trim(rs.getString("adminname"))  ));
				 		user.setUserType(  StringUtils.trim(rs.getString("accesstype"))    );
				 		user.setUserEmail(Utilities.tripleDecryptData(  StringUtils.trim(rs.getString("adminemail")) )  );
				 		user.setUserContact(Utilities.tripleDecryptData(  StringUtils.trim(rs.getString("admincontact")))    );
				 		user.setExpiryDate(  StringUtils.trim(rs.getString("expirydate"))    );
				 		user.setUserStatus(  StringUtils.trim(rs.getString("status"))    );
				 		user.setLocation(Utilities.tripleDecryptData(StringUtils.trim(rs.getString("location"))));
				 		user.setCurrency(StringUtils.trim(rs.getString("currency")));
				 		user.setStellarId(Utilities.tripleDecryptData(StringUtils.trim(rs.getString("stellarid"))));
				 		user.setPasswordType(StringUtils.trim(rs.getString("password_type")));
				 	}
			 }
		}catch(Exception e){
			user=null;
			NeoBankEnvironment.setComment(1,className,"The exception in method getPartnerUserDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getPartnerUserDetails  is  "+e.getMessage());			
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
		return user;
	}


	
	
	public List<Transaction> getPatnersPendingTransactions(String userId) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		List<Transaction> transactions = null;
		Transaction transaction = null;
		try{
			connection = super.getConnection();	
			query =" select a.txncode txncode, a.sysreference_int sysreference_int, a.txnusercode txnusercode, a.custrelno custrelno, "
					+ "	 a.paymode paymode, a.source_assetcode source_assetcode, a.destination_currency destination_currency, "
					+ "	 a.source_amount source_amount, a.destination_amount destination_amount, a.partner_userid partner_userid, "
					+ "	 a.partner_stellar_id partner_stellar_id, a.stellar_txnhash stellar_txnhash, a.sender_comment sender_comment, a.partners_comment partners_comment,  "
					+ "	 a.receiver_name receiver_name, a.receiver_bankname receiver_bankname, a.receiver_bankcode receiver_bankcode, a.receiver_accountno receiver_accountno, "
					+ "	a.receiver_email receiver_email, a.status status, a.txndatetime txndatetime  "
					+ " from txn_currency_remittance a where a.status = ?  and a.partner_userid = ? order by txndatetime desc ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "P"); 
			pstmt.setString(2, (userId)); 
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactions = new ArrayList<Transaction>();
				 	while(rs.next()){
				 		transaction = new Transaction(); 
				 		transaction.setTxnCode(StringUtils.trim(rs.getString("txncode")));
				 		transaction.setSystemReferenceInt(StringUtils.trim(rs.getString("sysreference_int")));
				 		transaction.setTxnUserCode(StringUtils.trim(rs.getString("txnusercode")));
				 		transaction.setRelationshipNo(StringUtils.trim(rs.getString("custrelno")));
				 		transaction.setPayMode(StringUtils.trim(rs.getString("custrelno")));
				 		transaction.setPayMode(StringUtils.trim(rs.getString("paymode")));
				 		transaction.setSourceAssetCode(StringUtils.trim(rs.getString("source_assetcode")));
				 		transaction.setDestinationAssetCode(StringUtils.trim(rs.getString("destination_currency")));
				 		transaction.setSourceAmount(StringUtils.trim(rs.getString("source_amount")));
				 		transaction.setDestinationAmount(StringUtils.trim(rs.getString("destination_amount")));
				 		transaction.setCustomerId((StringUtils.trim(rs.getString("partner_userid"))));
				 		transaction.setPublicKey((StringUtils.trim(rs.getString("partner_stellar_id"))));
				 		transaction.setSystemReferenceExt(StringUtils.trim(rs.getString("stellar_txnhash")));
				 		transaction.setSenderComment((StringUtils.trim(rs.getString("sender_comment"))));
				 		transaction.setPartnersComment((StringUtils.trim(rs.getString("partners_comment"))));
				 		transaction.setReceiverName((StringUtils.trim(rs.getString("receiver_name"))));
				 		transaction.setReceiverBankName((StringUtils.trim(rs.getString("receiver_bankname"))));
				 		transaction.setReceiverBankCode((StringUtils.trim(rs.getString("receiver_bankcode"))));
				 		transaction.setReceiverAccountNo(StringUtils.trim(rs.getString("receiver_accountno")));
				 		transaction.setReceiverEmail((StringUtils.trim(rs.getString("receiver_email"))));
				 		transaction.setStatus(StringUtils.trim(rs.getString("status")));
				 		transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime")));
				 		transactions.add(transaction);
				 	}
			 }
			 if (transactions != null)
					if (transactions.size() == 0)
						transactions = null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getPatnersTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getPatnersTransactions  is  "+e.getMessage());			
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
		return transactions;
	}
	
	
	public List<Transaction> getPatnersCompleteTransactions(String userId) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		List<Transaction> transactions = null;
		Transaction transaction = null;
		try{
			connection = super.getConnection();	
			query =" select a.txncode txncode, a.sysreference_int sysreference_int, a.txnusercode txnusercode, a.custrelno custrelno, "
					+ "	 a.paymode paymode, a.source_assetcode source_assetcode, a.destination_currency destination_currency, "
					+ "	 a.source_amount source_amount, a.destination_amount destination_amount, a.partner_userid partner_userid, "
					+ "	 a.partner_stellar_id partner_stellar_id, a.stellar_txnhash stellar_txnhash, a.sender_comment sender_comment, a.partners_comment partners_comment,  "
					+ "	 a.receiver_name receiver_name, a.receiver_bankname receiver_bankname, a.receiver_bankcode receiver_bankcode, a.receiver_accountno receiver_accountno, "
					+ "	a.receiver_email receiver_email, a.status status, a.txndatetime txndatetime  from txn_currency_remittance a where a.status = ?  and a.partner_userid = ? "
					+ "  order by txndatetime desc ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "C"); 
			pstmt.setString(2, (userId)); 
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 transactions = new ArrayList<Transaction>();
				 	while(rs.next()){
				 		transaction = new Transaction(); 
				 		transaction.setTxnCode(StringUtils.trim(rs.getString("txncode")));
				 		transaction.setSystemReferenceInt(StringUtils.trim(rs.getString("sysreference_int")));
				 		transaction.setTxnUserCode(StringUtils.trim(rs.getString("txnusercode")));
				 		transaction.setRelationshipNo(StringUtils.trim(rs.getString("custrelno")));
				 		transaction.setPayMode(StringUtils.trim(rs.getString("custrelno")));
				 		transaction.setPayMode(StringUtils.trim(rs.getString("paymode")));
				 		transaction.setSourceAssetCode(StringUtils.trim(rs.getString("source_assetcode")));
				 		transaction.setDestinationAssetCode(StringUtils.trim(rs.getString("destination_currency")));
				 		transaction.setSourceAmount(StringUtils.trim(rs.getString("source_amount")));
				 		transaction.setDestinationAmount(StringUtils.trim(rs.getString("destination_amount")));
				 		transaction.setCustomerId((StringUtils.trim(rs.getString("partner_userid"))));
				 		transaction.setPublicKey((StringUtils.trim(rs.getString("partner_stellar_id"))));
				 		transaction.setSystemReferenceExt(StringUtils.trim(rs.getString("stellar_txnhash")));
				 		transaction.setSenderComment((StringUtils.trim(rs.getString("sender_comment"))));
				 		transaction.setPartnersComment((StringUtils.trim(rs.getString("partners_comment"))));
				 		transaction.setReceiverName((StringUtils.trim(rs.getString("receiver_name"))));
				 		transaction.setReceiverBankName((StringUtils.trim(rs.getString("receiver_bankname"))));
				 		transaction.setReceiverBankCode((StringUtils.trim(rs.getString("receiver_bankcode"))));
				 		transaction.setReceiverAccountNo((StringUtils.trim(rs.getString("receiver_accountno"))));
				 		transaction.setReceiverEmail((StringUtils.trim(rs.getString("receiver_email"))));
				 		transaction.setStatus(StringUtils.trim(rs.getString("status")));
				 		transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime")));
				 		transactions.add(transaction);
				 	}
			 }
			 if (transactions != null)
					if (transactions.size() == 0)
						transactions = null;
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getPatnersTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getPatnersTransactions  is  "+e.getMessage());			
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
		return transactions;
	}

	public boolean editUpdateTxnStatus(String systemRef, String comment, String status,String date) throws Exception {
		NeoBankEnvironment.setComment(3, className, "systemRef "+systemRef+" comment "+comment+" status "+status+"date "+date);
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			//                                               1                 2                3          
			query = " update txn_currency_remittance set  status=?, partners_comment=?,updatetxndatetime=? where sysreference_int=? "; 
		    pstmt = connection.prepareStatement(query);
			pstmt.setString(1, status); 						 
			pstmt.setString(2,  (comment));						 
			pstmt.setString(3, date);						 					 
			pstmt.setString(4, systemRef);
	 
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}			
			connection.commit();
			result = true;
		}catch(Exception e){
			connection.rollback();
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method editUpdateTxnStatus  is  "+e.getMessage());
			throw new Exception ("The exception in method editUpdateTxnStatus  is  "+e.getMessage());
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

	public boolean updateTxnToStatus(String systemRef, String comment, String status)  throws Exception {
		NeoBankEnvironment.setComment(3, className, "systemRef "+systemRef+" comment "+comment+" status "+status);
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			//                                               1                 2                3          
			query = " update txn_currency_remittance set  status=?, partners_comment=? where sysreference_int=? "; 
		    pstmt = connection.prepareStatement(query);
			pstmt.setString(1, status); 						 
			pstmt.setString(2,  Utilities.tripleEncryptData(comment));						 
			pstmt.setString(3, systemRef);						 
								 
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}
							
			connection.commit();
			result = true;
		}catch(Exception e){
			connection.rollback();
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method editUpdateTxnStatus  is  "+e.getMessage());
			throw new Exception ("The exception in method editUpdateTxnStatus  is  "+e.getMessage());
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
