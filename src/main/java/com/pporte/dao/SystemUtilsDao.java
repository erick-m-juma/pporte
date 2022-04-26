package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.model.AssetCoin;
import com.pporte.model.PricingDetails;
import com.pporte.model.TransactionLimitDetails;
import com.pporte.model.TransactionRules;
import com.pporte.utilities.Utilities;
public class SystemUtilsDao extends HandleConnections {

	private static String className = SystemUtilsDao.class.getSimpleName();
	
	public boolean addAuditTrail(String userId, String userType, String moduleCode, String comment) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 if(!userId.equals("")) {
				                            	//		1		2			3			4		5				
				 query = "insert into audit_trail 	(userid, usertype, modulecode, comment, trailtime) "
							+ "values (?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5	  
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, userId); 					
					pstmt.setString(2, userType); 					
					pstmt.setString(3, moduleCode);					
					pstmt.setString(4, comment);					
					pstmt.setString(5, Utilities.getMYSQLCurrentTimeStampForInsert());					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					
					connection.commit();
					result = true;
			 }
		}catch(Exception e){
			connection.rollback(); result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method addAuditTrail  is  "+e.getMessage());
			throw new Exception ("The exception in method addAuditTrail  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				NeoBankEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); if (userId!=null) userId=null;if (userType!=null) userType=null; if (moduleCode!=null) moduleCode=null;
			if (comment!=null) comment=null;
		}
		return result;
	}
	




	
	
	public String getChargesApplicable(String userType, String transactionMode, String amount) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String charges = null;
		PricingDetails m_PricingDetails = null;
		NeoBankEnvironment.setComment(3,className,"in getChargesApplicable userType : "+ userType + " transactionMode "+ transactionMode + " amount "+amount);

		try{
			connection = super.getConnection();	

 		query = "select planid, usertype, paymode,minimum_txn_amount, varfee, slabapplicable, paytype from pricing_table where paymode=? and usertype = ? and status  =?";
 			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionMode);
			pstmt.setString(2, userType);
			pstmt.setString(3, "A");
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 m_PricingDetails=new PricingDetails();
				 	while(rs.next()){	
				 		m_PricingDetails.setPlanId( StringUtils.trim(rs.getString("planid"))    );
				 		m_PricingDetails.setUsetType( StringUtils.trim(rs.getString("usertype"))    );
				 		m_PricingDetails.setPlanType( StringUtils.trim(rs.getString("paymode"))    );
				 		m_PricingDetails.setVariableFee( StringUtils.trim(rs.getString("varfee"))    );
				 		m_PricingDetails.setSlabApplicable( StringUtils.trim(rs.getString("slabapplicable"))    );
				 		m_PricingDetails.setPayType( StringUtils.trim(rs.getString("paytype"))    );
				 		m_PricingDetails.setMinimumTxnAmount( StringUtils.trim(rs.getString("minimum_txn_amount"))    );
				 		
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(pstmt.isClosed()==false) 	 pstmt.close(); if (rs!=null) rs.close();

			 if(m_PricingDetails.getPlanId() !=null) {
				if(  Double.parseDouble(m_PricingDetails.getVariableFee()) == 0 &&  m_PricingDetails.getSlabApplicable().equalsIgnoreCase("Y")) {
					
					query =  " select rate from pricing_slab_rate where ? between fromrange  and torange  and planid = ? ";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, amount);
					pstmt.setString(2,  m_PricingDetails.getPlanId());
					rs = (ResultSet)pstmt.executeQuery();	
					if (rs!=null)
					{
						while(rs.next()){
							charges= StringUtils.trim(rs.getString("rate"));

						}
					}
					 if(pstmt.isClosed()==false) 	 pstmt.close(); if (rs!=null) rs.close();

				}else {
					charges =  Double.toString( Double.parseDouble(m_PricingDetails.getVariableFee()) * Double.parseDouble(amount) )   ;
				}
			 }
			 if(charges!=null) {
			
				 charges = m_PricingDetails.getPayType()+","+charges+"|"+m_PricingDetails.getMinimumTxnAmount();
			 }else {
				 charges = "D,0.0|0.00";
			 }
			 NeoBankEnvironment.setComment(3,className,"The inside charges  " + charges);
			 
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,className,"The exception in method getChargesApplicable  is  "+e.getMessage());
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
		return charges;
	}
}
