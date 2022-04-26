package com.pporte.utilities;

import java.io.PrintWriter;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.security.AESEncrypter;
import com.pporte.utilities.Utilities;

import framework.v8.security.EncryptionHandler;

public class Utilities {
	private static String classname = Utilities.class.getSimpleName();
	
	static final String RNDSTRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static SecureRandom rnd = new SecureRandom();

      public static String getMYSQLCurrentTimeStampForInsert() throws Exception{
          	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
          	 java.util.Date date = new Date();      	             	        
          	return formatter1.format(date);
          	}
    	public static String getMySQLDateTimeConvertor(String datetimestring) throws Exception{
    	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
    	 java.util.Date date = formatter1.parse(datetimestring);      	        
    	 SimpleDateFormat formatter2 = new SimpleDateFormat ("dd-MMM-yyyy HH:mm:ss");      	        
    	return formatter2.format(date);

    	}
    	
		public static void sendJsonResponse(HttpServletResponse response, 
				String status, String message) {
			try {
				JsonObject obj = new JsonObject( );
				obj.addProperty("error",status);
				obj.addProperty("message",message);
				try {
					NeoBankEnvironment.setComment(3, "Method Send JSON Response","  String is " + obj.toString());
					response.getWriter().print(obj);
				}finally {
					response.getWriter().close();
					if(obj!=null) obj = null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, classname, "Problem from method sendJsonResponse : "+e.getMessage());
			}
			
		}
		
		//Genarate Transaction code for user
		public static String generateTransactionCode(int count) {
			int count2 = 9;
			String txnCode = RandomStringUtils.randomAlphanumeric(count2).toUpperCase();
			return txnCode;
	     }
		
		public static synchronized String genAlphaNumRandom(int len) throws Exception {
	  		StringBuilder sb = null;
				try {
					sb = new StringBuilder( len );
					for( int i = 0; i < len; i++ ) {
					      sb.append( RNDSTRING.charAt( rnd.nextInt(RNDSTRING.length()) ) );
					}

				} catch (Exception e) {
					throw new Exception("Can't generate Random Number");
				}
	  		return sb.toString();  		
	  	}	 
		public static void sendSessionExpiredResponse(HttpServletResponse response, 
				String status, String message) {
			try {
				JsonObject obj = new JsonObject( );
				obj.addProperty("error","sess_expired");
				obj.addProperty("message",message);
				try {
					response.getWriter().print(obj);
				}finally {
					response.getWriter().close();
					if(obj!=null) obj = null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, classname, "Problem from method sendJsonResponse : "+e.getMessage());
			}
			
		}
		
		public static boolean compareMobileToken(String relationshipNo, String tokenValue) {
			boolean result = false;
			try {
				String generatedToken = generateMobileToken(relationshipNo);
				//NeoBankEnvironment.setComment(3, classname, "Token value from user is "+tokenValue);
				//NeoBankEnvironment.setComment(3, classname, "Token generatedToken "+generatedToken);
				if(generatedToken.equals(tokenValue)) {
					result = true;
					//NeoBankEnvironment.setComment(3, classname, "Token matches");
				}
			} catch (Exception e) {
				result = false;
				NeoBankEnvironment.setComment(1, classname, "Error in compareMobileToken is: "+e.getMessage());
			}
			return result;
		}
		
		public static String generateMobileToken(String relationshipNo) {
			  String md5TokenValue = "";
				try {
					
					
				} catch (Exception e) {
					md5TokenValue = "";
					NeoBankEnvironment.setComment(1, classname, "Error in generateMobileToken is: "+e.getMessage());
				}
				return md5TokenValue;
			}
		
		public static void sendJsonResponseOfInvalidToken(HttpServletResponse response, 
				String status, String message) {
			try {
				JsonObject obj = new JsonObject( );
				obj.addProperty("error",status);
				obj.addProperty("message",message);
				try {
					NeoBankEnvironment.setComment(3, classname,"sendJsonResponseOfInvalidToken "+" Response is " +obj.toString());
					response.getWriter().print(obj);
				}finally {
					response.getWriter().close();
					if(obj!=null) obj = null;
				}
			}catch (Exception e) {
				NeoBankEnvironment.setComment(1, classname, "Problem from method sendJsonResponse : "+e.getMessage());
			}
			
		}
		
	  	public static String getMoneyinDecimalFormat(String toformat) throws ParseException{
	  		DecimalFormat moneyFormat = new DecimalFormat("#,###,##0.00");
	  		return moneyFormat.format(Double.parseDouble(toformat)).toString();
	   }
	  	public static synchronized void callOpsException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
				String msg) throws Exception {
	  		try {
				if(msg!=null) {
				//NeoBankEnvironment.setComment(1, classname, "Error is "+msg);
					request.setAttribute("errormsg", msg);
				}else {
					msg="Undefined Error";
				}
				response.setContentType("text/html");
				ctx.getRequestDispatcher(NeoBankEnvironment.getOpsErrorPage()).forward(request, response);
				
			} catch (Exception e1) { 
					NeoBankEnvironment.setComment(1, classname, "Problem in forwarding to Error Page, error : "+e1.getMessage());
			}
			
		}
	  	public static String getStellarDateConvertor(String datestring) throws Exception{
	   	   	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssX");// 2021-11-02T05:51:31Z
	   	   	 java.util.Date date = formatter1.parse(datestring);
	   	   	 SimpleDateFormat formatter2 = new SimpleDateFormat ("dd-MMM-yyyy HH:mm:ss");
   	   	 return formatter2.format(date);

   	   	}
	  	
	  	 public static String ellipsis(final String text, int length)
		 {
		     return text.substring(0, length - 3) + "...";
		 }


  	public static String getMoneyinNoDecimalFormat(String toformat) throws ParseException{
  		DecimalFormat moneyFormat = new DecimalFormat("#,###,##0");
  		return moneyFormat.format(Double.parseDouble(toformat)).toString();
     }
  	
  	public static String displayDateFormat(String sDate, String sDateFormat) {
	 	DateFormat df = new SimpleDateFormat(sDateFormat);
	 	String infi = null;
	 	try {
			Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sDate);
			try {
				infi = df.format(date1);
			} finally {
				if(df!=null)df=null;if(date1!=null)date1=null;
			}
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1, classname, "Error in method displayDateFormat, error : "+e.getMessage());
		}  
	 	return infi;
  	}

  	

			public static synchronized void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
					String msg) throws Exception {
			try {
					if(msg!=null) {
					//NeoBankEnvironment.setComment(1, classname, "Error is "+msg);
						request.setAttribute("errormsg", msg);
					}else {
						msg="Undefined Error";
					}
					response.setContentType("text/html");
					ctx.getRequestDispatcher(NeoBankEnvironment.getErrorPage()).forward(request, response);
				} catch (Exception e1) { 
						NeoBankEnvironment.setComment(1, classname, "Problem in forwarding to Error Page, error : "+e1.getMessage());
				}
				
			}

			
			@SuppressWarnings("unchecked")
			public static void jsonResponse(String message, HttpServletResponse response) throws Exception{
				try {
					PrintWriter jsonOutput_1 = null; jsonOutput_1 = response.getWriter();  JSONObject jsonResponse = new JSONObject();
					
					jsonResponse.put("message", message);
					try {
						jsonOutput_1.print(jsonResponse);
					}finally {
						if(jsonOutput_1!=null) {jsonOutput_1.flush(); jsonOutput_1.close();} 
						 if(message!=null)message = null; if(jsonResponse!=null) jsonResponse =null;
					}
				}catch (Exception e1) { 
					NeoBankEnvironment.setComment(1, classname, "Problem in forwarding jsonresponse from method jsonResponse : "+e1.getMessage());
				}
			}
	 
		// Get current date at midnight
		public static String getCurrentDateAtMidnight() throws Exception{
	     	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd 00:00:00");     	        
	     	 java.util.Date date = new Date();      	             	        
	     	return formatter1.format(date);
	     	}
		public static String convertSatoshiToBTC(String satoshi) {
				// 1 BTC = 100000000 Satoshi && 1 Satoshi = 0.00000001
			Double satoshiValue=0.00000001; 
			DecimalFormat btcformat = new DecimalFormat("######0.00000000");
			return btcformat.format(Double.valueOf(satoshi)*satoshiValue);
		}
		public static String convertBTCToSatoshi(String btc) {
			// 1 BTC = 100000000 Satoshi && 1 Satoshi = 0.00000001
			String btcValue= "100000000"; 
			String convertedSatoshi=Double.toString(Double.valueOf(btc)*Double.valueOf(btcValue));
			return convertedSatoshi;
		}
		
		public static String encryptString(String stringToEncrypt) throws Exception {
	  		return AESEncrypter.encrypt(stringToEncrypt);
	  	}
	  	public static String decryptString(String stringToDecrypt) throws Exception {
	  		return AESEncrypter.decrypt(stringToDecrypt);

	  	}
	  	public static String decryptJsonString(String stringToDecrypt) throws Exception {
	  		return EncryptionHandler.decryptJson(stringToDecrypt);
	  	}
	  	
	  	 public static String tripleDecryptData(String data) {
			  String decryptedData=null; String decryptedDataHex=null; String reversedHexDecryptedDataHex=null; String decryptedJsonHashofReversedDecryptedDataHex=null;
			  try {
				  decryptedJsonHashofReversedDecryptedDataHex=Utilities.decryptJsonString(data);
				  reversedHexDecryptedDataHex=StringUtils.reverse(decryptedJsonHashofReversedDecryptedDataHex);
				  decryptedDataHex=Utilities.hexToASCII(reversedHexDecryptedDataHex);
				  decryptedData=Utilities.decryptString(decryptedDataHex);
				  try {
					  
				  }finally {
					  if (decryptedDataHex!=null)decryptedDataHex=null;  if (reversedHexDecryptedDataHex!=null)reversedHexDecryptedDataHex=null;
					  if (decryptedJsonHashofReversedDecryptedDataHex!=null)decryptedJsonHashofReversedDecryptedDataHex=null; 
				  }
			  }catch (Exception e) {
				  NeoBankEnvironment.setComment(1, classname, "Error in method tripleDecryptData, error : "+e.getMessage());
			  }
			  return decryptedData;
		  }
	  	public static String encryptJsonString(String stringToEncrypt) throws Exception {
	  		return EncryptionHandler.encryptJson(stringToEncrypt);
	  	}
	  	
	  	public static String formatToSevenDecimalPlace(String toformat) throws ParseException{
	  		DecimalFormat moneyFormat = new DecimalFormat("######0.0000000");
	  		return moneyFormat.format(Double.parseDouble(toformat)).toString();
	  	}
	  	 
	  	public static String asciiToHex(String asciiValue)
	 	{
	 	    char[] chars = asciiValue.toCharArray();
	 	    StringBuffer hex = new StringBuffer();
	 	    for (int i = 0; i < chars.length; i++)
	 	    {
	 	        hex.append(Integer.toHexString((int) chars[i]));
	 	    }
	 	    return hex.toString();
	 	}
	  	 
	  	public static String hexToASCII(String hexValue)
	 	{
	 	    StringBuilder output = new StringBuilder("");
	 	    for (int i = 0; i < hexValue.length(); i += 2)
	 	    {
	 	        String str = hexValue.substring(i, i + 2);
	 	        output.append((char) Integer.parseInt(str, 16));
	 	    }
	 	    return output.toString();
	 	}
	  	
	  	public static String getDateTimeFormatInFullForDisplay(String datetimestring) throws Exception{
    		SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
    		java.util.Date date = formatter1.parse(datetimestring);      	        
    		SimpleDateFormat formatter2 = new SimpleDateFormat ("dd-MMMM-yyyy , EEEE HH:mm:ss");      	        
    		return formatter2.format(date);
    		
    	}
	  	
		  public static String tripleEncryptData(String data) {
			  String encryptedData=null; String encryptedDataHex=null; String reversedHexEncryptedDataHex=null; String encryptedJsonHashofReversedEncryptedDataHex=null;
			  try {
				  encryptedData=Utilities.encryptString(data);
				  encryptedDataHex=Utilities.asciiToHex(encryptedData);
				  reversedHexEncryptedDataHex=StringUtils.reverse(encryptedDataHex);
				  encryptedJsonHashofReversedEncryptedDataHex=Utilities.encryptJsonString(reversedHexEncryptedDataHex);
				  try {
					  
				  }finally {
					  if (encryptedData!=null)encryptedData=null;  if (encryptedDataHex!=null)encryptedDataHex=null;
					  if (reversedHexEncryptedDataHex!=null)reversedHexEncryptedDataHex=null; 
				  }
			  }catch (Exception e) {
				  NeoBankEnvironment.setComment(1, classname, "Error in method tripleEncryptData, error : "+e.getMessage());
			  }
			  return encryptedJsonHashofReversedEncryptedDataHex;
		  }
	  	public static String getBasicAuthHeader( String userName, String password) {
			try {
				String plainCredentials = userName.concat(":").concat(password);
				String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
		        // Create authorization header
		         String authorizationHeader = "Basic " + base64Credentials;
		         return authorizationHeader;  
			} catch (Exception e) {
				NeoBankEnvironment.setComment(1, classname, "Problem from method getBasicAuthHeader : "+e.getMessage());
				return null;
			}
		}

}
