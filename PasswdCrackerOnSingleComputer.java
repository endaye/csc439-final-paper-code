package singleComputer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PasswdCrackerOnSingleComputer {

	// the minimum length of password
	private static int min = 2;
	// the maximum length of password
	private static int max = 6;
	
	// the Hash value
	private static String knowHash = "202cb962ac59075b964b07152d234b70";
	// the algorithm name
	private static String algoName = "MD5";
	
	private static long startTime;
	private static long endTime;
	
	// the alphabet
	private static char[] psw = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z', '-', '_', '?', '!', '@', '$' };

	public static void main(String[] args) {
		/*
		 * Calculate the running time of my program
		 * http://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
		 */
		startTime = System.currentTimeMillis();
		permutation(psw, min, max);
		endTime   = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.000");
		System.out.println("Execution time is " + formatter.format((endTime - startTime) / 1000d) + " seconds");
	}

	private static void permutation(char[] array, int min, int max) {
		boolean gotPwd = false;
		for (int len = min; len <= max && !gotPwd; len++) {
			gotPwd = permutation("", array, len, gotPwd);
		}
		if(!gotPwd) {
			System.out.println("Password NOT Cracked.");
		}
	}

	private static boolean permutation(String s, char[] array, int len, boolean gotPwd) {
		if (gotPwd) {
			return gotPwd;
		}
		if (len == 1) {
			for (int i = 0; i < array.length; i++) {
				String result = s + array[i];
				result = result.replaceAll("(\\r|\\n|\\s+)", "");
				byte[] gPass = result.getBytes();
				gotPwd = crackPassword(gPass, knowHash, algoName);
				if (gotPwd) {
					System.out.println("Password Cracked!\nHash Value:\t" + knowHash + "\nPassword:\t" + result);
					break;
				}	
			}
		} else {
			for (int i = 0; i < array.length; i++) {
				gotPwd = permutation(s + array[i], array, len - 1, gotPwd);
				if(gotPwd) {
					break;
				}
			}
		} 
		return gotPwd;
	}

	/*
	 * Crack password code is writen by Skyler Kaufman 
	 * in CSC439 Assignment_3, Group 3rd_Bravo
	 */
	private static boolean crackPassword(byte[] password, String knownHash, String algoName) {
		try {
			MessageDigest md = MessageDigest.getInstance(algoName);
			md.reset();
			md.update(password);
			byte[] byteStr = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteStr.length; i++) {
				/*
				 * Conversion of byte [] to hex taken from
				 * http://www.mkyong.com/java/java-sha-hashing-example/
				 */
				sb.append(Integer.toString((byteStr[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			String hashString = sb.toString();
			// String hashString = new BigInteger(1, md.digest()).toString(16);
			if (hashString.equals(knownHash)) {
				return true;
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
}
