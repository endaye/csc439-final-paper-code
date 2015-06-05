package singleComputer;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PasswdCracker {
	private String dictionaryPath;
	private String knowHashFilePath;
	private String saltFilePath = null;
	private boolean saltCase;
	private static long startTime;
	private static long endTime;

	// Use this constructor when there is no salt value
	public PasswdCracker(String dictionaryPath,String knowHashFilePath,boolean saltCase){
		this.dictionaryPath 	= dictionaryPath;
		this.knowHashFilePath 	= knowHashFilePath;
		this.saltCase			= saltCase;
	}

	// Use this constructor when there is salt value
	public PasswdCracker(String dictionaryPath,String knowHashFilePath,String saltFilePath,boolean saltCase){
		this.dictionaryPath 	= dictionaryPath;
		this.knowHashFilePath 	= knowHashFilePath;
		this.saltFilePath		= saltFilePath;
		this.saltCase			= saltCase;
	}

	private String readFile(String filePath) throws FileNotFoundException{
		Scanner fileScanner = new Scanner(new File(filePath));
		String str = fileScanner.nextLine();
		str = str.replaceAll("(\\r|\\n|\\s+)", "");
		fileScanner.close();
		return str;
	}
	private String getAlgorithmName(String hashPath) throws FileNotFoundException{
		String str = readFile(hashPath);
		switch (str.length()){
		case 32:
			return "MD5";	
		case 40: 
			return "SHA-1";
		case 64: 
			return "SHA-256";
		default:
			throw new RuntimeException();
		}
	}

	private String getKnownHashVal(String knowHashFilePath) throws FileNotFoundException {
		return readFile(knowHashFilePath);
	}

	private byte [] getSaltVal(String saltFilePath) throws FileNotFoundException{
		/*
		 * Logic taken from 
		 * http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
		 */
		String strSaltVal =  readFile(saltFilePath);	
		int len = strSaltVal.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(strSaltVal.charAt(i), 16) << 4)
	                             + Character.digit(strSaltVal.charAt(i+1), 16));
	    }
		return data;
	}

	private byte [] createSaltPwd(byte [] first, byte [] second){
		byte [] toReturn = new byte[first.length+second.length];
		int j = 0;
		int k = 0;
		for(int i=0 ; i < toReturn.length ; i++){
			if (i < first.length){
				toReturn[i] = first[j++];
			}else{
				toReturn[i] = second[k++];
			}
		}
		return toReturn;
	}

	/*
	 * This method calculates the hash of password and compares with it with the 
	 * knownHash using the know algorithm name.
	 * This method is called from crackPassword()
	 */

	private boolean crackPassword(byte [] password, String knownHash, String algoName){
		try {
			MessageDigest md=MessageDigest.getInstance(algoName);
			md.reset();
			md.update(password);
			byte [] byteStr = md.digest();
			
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteStr.length; i++) {
				/*
				 * Conversion of byte [] to hex taken from
				 * http://www.mkyong.com/java/java-sha-hashing-example/
				 */
				sb.append(Integer.toString((byteStr[i] & 0xff) + 0x100, 16).substring(1));
			}
			String hashString = sb.toString();
			//String hashString = new BigInteger(1, md.digest()).toString(16);
			if(hashString.equals(knownHash)){
				return true;
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String crackPassword() throws FileNotFoundException{
		String algoName 	= getAlgorithmName(this.knowHashFilePath);
		String knownHash 	= getKnownHashVal(this.knowHashFilePath);
		Scanner fileScanner = new Scanner(new File(this.dictionaryPath));
		while (fileScanner.hasNextLine()){
			String guessPass = fileScanner.nextLine();
			//System.out.println(guessPass);
			guessPass = guessPass.replaceAll("(\\r|\\n|\\s+)", "");
			byte [] gPass = guessPass.getBytes();
			
			if(!saltCase){
				if(crackPassword(gPass, knownHash, algoName)){
					System.out.println("Password Cracked - " + this.knowHashFilePath + "\n" 
					+ algoName + ": \t" + knownHash + " \t" + guessPass);
					fileScanner.close();
					return guessPass;
				}	
			}else{
				byte [] saltVal = getSaltVal(this.saltFilePath);;
				if(crackPassword(createSaltPwd(gPass, saltVal),knownHash,algoName)){
					System.out.println("Password Cracked - " + this.knowHashFilePath + "\n" 
							+ algoName + ": \t" + knownHash + " \t" + guessPass);
					fileScanner.close();
					return guessPass;
				}
				if(crackPassword(createSaltPwd(saltVal, gPass),knownHash,algoName)){
					System.out.println("Password Cracked - " + this.knowHashFilePath + "\n" 
							+ algoName + ": \t" + knownHash + " \t" + guessPass);
					fileScanner.close();
					return guessPass;
				}	
			}
		}
		fileScanner.close();
		return null;
	}
	
	public static void main(String[] args) {
		String dictionaryPath = "/Users/yzhang/test/dic-0294.txt";
		String knowHashFilePath1 = "/Users/yzhang/test/pw1.hex";
		String knowHashFilePath2 = "/Users/yzhang/test/pw2.hex";
		String knowHashFilePath3 = "/Users/yzhang/test/pw3.hex";
		String SknowHashFilePath1 = "/Users/yzhang/test/spw1.hex";
		String SknowHashFilePath2 = "/Users/yzhang/test/spw2.hex";
		String SknowHashFilePath3 = "/Users/yzhang/test/spw3.hex";
		String saltFilePath  = "/Users/yzhang/test/salt.hex";

		PasswdCracker pcwosalt1 = new PasswdCracker(dictionaryPath, knowHashFilePath1, false);
		PasswdCracker pcwosalt2 = new PasswdCracker(dictionaryPath, knowHashFilePath2, false);
		PasswdCracker pcwosalt3 = new PasswdCracker(dictionaryPath, knowHashFilePath3, false);
		PasswdCracker pcwsalt1 = new PasswdCracker(dictionaryPath, SknowHashFilePath1, saltFilePath, true);
		PasswdCracker pcwsalt2 = new PasswdCracker(dictionaryPath, SknowHashFilePath2, saltFilePath, true);
		PasswdCracker pcwsalt3 = new PasswdCracker(dictionaryPath, SknowHashFilePath3, saltFilePath, true);
		
		try {
			/*
			 * Calculate the running time of my program
			 * http://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
			 */
			long sT;
			startTime = System.currentTimeMillis();
			pcwosalt1.crackPassword();
			endTime   = System.currentTimeMillis();
			sT = startTime;
			NumberFormat formatter = new DecimalFormat("#0.000");
			System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds\n");
			/*
			startTime = endTime;
			pcwosalt2.crackPassword();
			endTime   = System.currentTimeMillis();
			System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds\n");
			
			startTime = endTime;
			pcwosalt3.crackPassword();
			endTime   = System.currentTimeMillis();
			System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds\n");
			
			startTime = endTime;
			pcwsalt1.crackPassword();
			endTime   = System.currentTimeMillis();
			System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds\n");
			
			startTime = endTime;
			pcwsalt1.crackPassword();
			endTime   = System.currentTimeMillis();
			System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds\n");
			
			startTime = endTime;
			pcwsalt1.crackPassword();
			endTime   = System.currentTimeMillis();
			System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds\n");
			*/
			System.out.println("Execution total time is " + formatter.format((endTime - sT) / 1000d) + " seconds\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}