package singleComputer;

//import org.junit.Test;

public class T {

	// the minimum length of password
	private static int min = 1;
	// the maximum length of password
	private static int max = 10;
	// the character library, including upper and lower cases
	private static char[] psw = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public static void main(String[] args) {
		for (int i = min; i <= max; i++) {
			permutation(psw, i);
		}
	}

	/**
	 * Full array entry
	 * 
	 * @param array
	 *            character library
	 * @param len
	 *            the length of password
	 */
	private static void permutation(char[] array, int len) {
		permutation("", array, len);
	}

	/**
	 * 
	 * @param s
	 *            已生成临时字串
	 * @param array
	 *            密码数据
	 * @param n
	 *            剩余未生成的字符长度
	 */
	private static void permutation(String s, char[] array, int len) {
		if (len == 1) {
			for (int i = 0; i < array.length; i++) {
				// print out the permutation of characters
				String result = s + array[i];
				System.out.println(result);
			}
		} else {
			for (int i = 0; i < array.length; i++) {
				permutation(s + array[i], array, len - 1);
			}
		}
	}

}