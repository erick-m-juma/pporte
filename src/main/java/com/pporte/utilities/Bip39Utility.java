package com.pporte.utilities;
import org.stellar.sdk.KeyPair;

import com.google.gson.JsonArray;
import com.pporte.NeoBankEnvironment;
import com.soneso.stellarmnemonics.Wallet;
import com.soneso.stellarmnemonics.derivation.Ed25519Derivation;
import com.soneso.stellarmnemonics.mnemonic.Mnemonic;


public class Bip39Utility {
	private static String className = Bip39Utility.class.getSimpleName();
	public static char[] generateMnemonic() throws Exception{
		char[] mnemonic = null;
		try {
			mnemonic = Wallet.generate12WordMnemonic();
		//	String[] words = String.valueOf(mnemonic).split(" ");	
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return mnemonic;
	}
	/*Without passphrase*/
	public static KeyPair generateKeyPairs(char[] mnemonicCode) throws Exception {
		   KeyPair keyPair = null;
		   try {
		 	   keyPair = Wallet.createKeyPair(mnemonicCode, null, 0);
			} catch (Exception e) {
				keyPair = null;
				NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
				throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			}  
		 	return keyPair;
		}
	/*With passphrase*/
	public static KeyPair generateKeyPairsWithPassPhrase(char[] mnemonic) throws Exception {
		KeyPair keyPair = null;
		try {			
			char[] passphrase = "p4ssphr4se".toCharArray();
			keyPair = Wallet.createKeyPair(mnemonic, passphrase, 0);
		} catch (Exception e) {
			 keyPair = null;
			 NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			 throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return keyPair;
	}
	
	public static KeyPair masterKeyGeneration(String stringMnemonic) throws Exception {
		KeyPair keyPair0 = null;
		try {		
			char[] mnemonic = stringMnemonic.toCharArray();
			byte[] bip39Seed = Mnemonic.createSeed(mnemonic, null);
			Ed25519Derivation masterPrivateKey = Ed25519Derivation.fromSecretSeed(bip39Seed);
			Ed25519Derivation purpose = masterPrivateKey.derived(44);
			Ed25519Derivation coinType = purpose.derived(148);
			Ed25519Derivation account0 = coinType.derived(0);
			try {
				keyPair0 = KeyPair.fromSecretSeed(account0.getPrivateKey());
			} finally {
				if(bip39Seed!=null)bip39Seed=null; if(masterPrivateKey!=null)masterPrivateKey =null;
				if(purpose!=null)purpose=null; if(coinType!=null)coinType =null;
				if(account0!=null)account0=null;
			}
		} catch (Exception e) {
			keyPair0 = null;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return keyPair0;
	}
	
	public static boolean compareMnemonic(String mnemonicFromUser, String mnemonicFromDB) throws Exception {
		boolean result = false;
		try {
			//NeoBankEnvironment.setComment(3, className, "mnemonicFromDB "+mnemonicFromDB);
			//NeoBankEnvironment.setComment(3, className, "mnemonicFromUser "+mnemonicFromUser);
			String[] mnemonicFromUserArr = mnemonicFromUser.split(",");
			String[] mnemonicFromDBArr = mnemonicFromDB.split(",");
			for(int i= 0; i<mnemonicFromDBArr.length; i++) {
				if(!mnemonicFromUserArr[i].trim().equals(mnemonicFromDBArr[i].trim())) {
					//NeoBankEnvironment.setComment(3, className, "words do not match "+mnemonicFromUserArr[i]+" "+mnemonicFromDBArr[i]);
					return false;
				}else {
					//NeoBankEnvironment.setComment(3, className, "words match "+mnemonicFromUserArr[i]+" "+mnemonicFromDBArr[i]);
				}
			}
			result= true;
		} catch (Exception e) {
			result = false;
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return result;
	}
	
	public static String createCSVForMnemonic(JsonArray mnemonicArray) throws Exception {
		String result = "";
		try {
			for(int i= 0; i<mnemonicArray.size(); i++) {
				if(i==0) {
					result=result.concat(mnemonicArray.get(i).getAsString());
				}else {
					result=result.concat(",").concat(mnemonicArray.get(i).getAsString());
				}
				
			}
			NeoBankEnvironment.setComment(3, className, "result is "+result);
		} catch (Exception e) {
			result = "";
			NeoBankEnvironment.setComment(1,className,"The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
			throw new Exception ("The exception in method "+Thread.currentThread().getStackTrace()[1].getMethodName()+" is "+e.getMessage());
		}
		return result;
	}
}

