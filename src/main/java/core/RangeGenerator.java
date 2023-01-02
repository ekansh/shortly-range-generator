package core;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import db.JDBCConnection;
import db.Range;
import exceptions.NonRecoverableException;
import utils.BasicUtils;

public class RangeGenerator {
	private Map<Character, Integer> charsToInt = new HashMap<>();
	private Map<Integer, Character> intToChar = new HashMap<>();

	private final int RANGE = 20, BASE = 64;
	private final long MAX = 4398046511103L;
	private final String MAX_RANGE = "ZZZZZZZ", MIN_RANGE = "aaaaaaa";

	public String generateAllTheUrls(String start, String end, LambdaLogger logger) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < RANGE; i++) {
			String s = getRange(start, i, logger);
			sb.append(s).append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	public int createMap(char start, char end, int pos) {
		for (char i = start; i <= end; i++) {
			charsToInt.put(i, pos);
			intToChar.put(pos, i);
			pos++;
		}
		return pos;
	}

	public Map<String, String> createRange(String name, LambdaLogger logger) throws NonRecoverableException {
		Map<String, String> map = new HashMap<>();

		int pos = 0;
		pos = createMap('a', 'z', pos);
		pos = createMap('0', '9', pos);
		pos = createMap('+', '+', pos);
		pos = createMap('=', '=', pos);
		pos = createMap('A', 'Z', pos);

		try {
			String lastEndRange = "abaaapu";
			lastEndRange = JDBCConnection.getEndRange(logger);
			logger.log("lastEndRange =" + lastEndRange);
			String newStartRange = getRange(lastEndRange, 1, logger);
			logger.log("newStartRange =" + newStartRange);
			String newEndRange = getRange(lastEndRange, RANGE, logger);
			logger.log("newEndRange =" + newEndRange);
			if (!newStartRange.equals(newEndRange)) {
				Range range = new Range();
				range.setEc2Id(name);
				range.setStartRange(newStartRange);
				range.setEndRange(newEndRange);
				range.setStatus("ACTIVE");
				JDBCConnection.insertNewRange(range, logger);
			}
			map.put("start", newStartRange);
			map.put("end", newEndRange);
		} catch (ClassNotFoundException | SQLException e) {
			logger.log("Exception occured: "+BasicUtils.exceptionTrace(e));
			throw new NonRecoverableException("Exception while interacting with database", e);
		}catch (Exception e) {
			logger.log("Un-believable exception occured: "+BasicUtils.exceptionTrace(e));
			throw new NonRecoverableException("Un-believable exception while interacting with database", e);
		}
		return map;

	}

	public String getRange(String start, int range, LambdaLogger logger) {
		long base10Start = 0;
		long exp = 1l;

		for (int i = start.length() - 1; i >= 0; i--) {
			base10Start += charsToInt.get(start.charAt(i)) * (exp);
			exp = exp * BASE;
		}
		logger.log("base10 representation = " + base10Start);
		long base10End = base10Start + range;
		if (base10End > MAX) {
			return MAX_RANGE;
		} else {
			StringBuilder originalBaseRepresentation = new StringBuilder(MIN_RANGE);
			int i = 0;
			while (base10End >= BASE) {
				originalBaseRepresentation.setCharAt(i, intToChar.get((int) (base10End % BASE)));
				base10End = base10End / BASE;
				i++;
			}

			originalBaseRepresentation.setCharAt(i, intToChar.get((int) base10End));
			return originalBaseRepresentation.reverse().toString();
		}

	}
}
