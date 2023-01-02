package db;

import java.util.HashMap;
import java.util.Map;

import utils.BasicUtils;
public class JDBConnectionTest {
	static Map<Character, Integer> charsToInt = new HashMap<>();
	static Map<Integer, Character> intToChar = new HashMap<>();
	private static final int RANGE = 63, BASE = 4;
	private static final long MAX = 63;
	private static final String MAX_RANGE = "ddd";
	public static void main(String[] args) {
		
		String secret = BasicUtils.getSecret();
		Map<String,String> jsonToMap = BasicUtils.jsonToMap(secret);
		JDBCConnection jdbcConnection = new JDBCConnection();
		Map<Character, Integer> ctoi = charsToInt;
		Map<Integer, Character> itoc = intToChar;
		String start = "aaa";
		long base10Start = 0;
		int exp = 1;

		int pos = 0;
		pos = jdbcConnection.createMap('a', 'd', pos);

		for (int i = start.length() - 1; i >= 0; i--) {
			base10Start += charsToInt.get(start.charAt(i)) * (exp);
			exp = exp * BASE;
		}

		long base10End = base10Start + RANGE;
		if (base10End > MAX) {
			System.out.println(MAX_RANGE);
		} else {
			StringBuilder originalBaseRepresentation = new StringBuilder("aaa");
			int i = 0;
			while (base10End > BASE) {
				originalBaseRepresentation.setCharAt(i, intToChar.get((int) base10End % BASE));
				base10End = base10End / BASE;
				i++;
			}

			originalBaseRepresentation.setCharAt(i, intToChar.get((int) base10End));
			System.out.println(originalBaseRepresentation.reverse().toString());
		}

	}
}
