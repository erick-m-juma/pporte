package com.pporte.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import com.pporte.NeoBankEnvironment;
import com.pporte.utilities.Utilities;

public class CustomerDigitalAssetsDao extends HandleConnections{
	public static String className = CustomerDigitalAssetsDao.class.getName();

	public String getExChangeRatesMarkUp(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String markUpRate = null;
		try{
			connection = super.getConnection();	
			query =   " select markup_rate from porte_assets_to_btcx_markup where asset_code=? and status=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
	
			 if(rs!=null){
			 	while(rs.next()){	
			 		markUpRate = StringUtils.trim(rs.getString("markup_rate"));
			 		
			 	}
			 }	
				
		}catch(Exception e){
			markUpRate = null;
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
		return markUpRate;
	}
	
	public String getAssetDistributionAccount(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String distributionAccount = null;
		try{
			connection = super.getConnection();	
			query =   " select public_key from wallet_assets_account where asset_code=? and account_type=? and  status=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			pstmt.setString(2, "DA");
			pstmt.setString(3, "A");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
			 	while(rs.next()){	
			 		distributionAccount = StringUtils.trim(rs.getString("public_key"));
			 	}
			 }	
				
		}catch(Exception e){
			distributionAccount = null;
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
		return distributionAccount;
	}
	
		
	public String getDistributionAccountPublicKey(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String publicKey = null;
		try{			
			connection = super.getConnection();	
			query =   " select public_key from wallet_assets_account where asset_code=? and status=? and account_type=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			pstmt.setString(2, "A");
			pstmt.setString(3, "DA");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
			 	while(rs.next()){	
			 		publicKey = StringUtils.trim(rs.getString("public_key"));
			 	}
			 }	
				
		}catch(Exception e){
			publicKey = null;
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
		return publicKey;
	}
	
	public String getIssueingAccountPublicKey(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String publicKey = null;
		try{			
			connection = super.getConnection();	
			query =   " select public_key from wallet_assets_account where asset_code=? and status=? and account_type=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			pstmt.setString(2, "A");
			pstmt.setString(3, "IA");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
			 	while(rs.next()){	
			 		publicKey = StringUtils.trim(rs.getString("public_key"));
			 	}
			 }	
				
		}catch(Exception e){
			publicKey = null;
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
		return publicKey;
	}
	
	public String getLiquidityAccountPublicKey(String assetCode) throws Exception{                       
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		String publicKey = null;
		try{			
			connection = super.getConnection();	
			query =   " select public_key from wallet_assets_account where asset_code=? and status=? and account_type=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, assetCode);
			pstmt.setString(2, "A");
			pstmt.setString(3, "LA");
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
			 	while(rs.next()){	
			 		publicKey = StringUtils.trim(rs.getString("public_key"));
			 	}
			 }	
				
		}catch(Exception e){
			publicKey = null;
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
		return publicKey;
	}
	
	

}
