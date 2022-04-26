package com.pporte.security;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.pporte.NeoBankEnvironment;
import com.pporte.security.AESEncrypter;

public class AESEncrypter {
	private static final String ALGO = "";
	private static final int AES_KEYLENGTH = 256;
	private static byte[] iv = new byte[AES_KEYLENGTH / 16];
	private static final String classname = AESEncrypter.class.getSimpleName();
	public static final String apiCall = "";
	
	public static String getClassName() {
		return classname;
	}
	public static String encrypt(String Data) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(NeoBankEnvironment.getKeyValue().getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	public static String encryptWithCustomerKey(String Data, String customerEncryptionKey) throws Exception {
		String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(customerEncryptionKey.getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
		return encryptedValue;
	}
	public static String decryptWithCustomerKey(String encryptedData, String customerEncryptionKey ) throws Exception {
        String decryptedValue=null;
		try {
			Key key  = new SecretKeySpec(customerEncryptionKey.getBytes("UTF-8"), ALGO);
			//NBPRSLEnvironment.setComment(3,classname, "****** Key to decrypt  "+new String( key.getEncoded(), "UTF-8"));
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			decryptedValue = new String(decValue);
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,classname, " Exception in decryption "+e.getMessage());		
		}
        return decryptedValue;
    }
	public static String encryptSecure(String Data, String k) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(k.getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	public static String decrypt(String encryptedData) throws Exception {
        String decryptedValue=null;
		try {
			Key key  = new SecretKeySpec(NeoBankEnvironment.getKeyValue().getBytes("UTF-8"), ALGO);
			//NBPRSLEnvironment.setComment(3,classname, "****** Key to decrypt  "+new String( key.getEncoded(), "UTF-8"));
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			decryptedValue = new String(decValue);
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,classname, " Exception in decryption "+e.getMessage());		
		}
        return decryptedValue;
    }


}
