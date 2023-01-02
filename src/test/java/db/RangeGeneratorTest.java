package db;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import core.RangeGenerator;
import exceptions.NonRecoverableException;
import utils.BasicUtils;


public class RangeGeneratorTest {
	public static void main(String[] args) throws NonRecoverableException {
		LambdaLogger logger = new LambdaLogger() {

			@Override
			public void log(byte[] message) {
				System.out.println(new String(message));
			}

			@Override
			public void log(String message) {
				System.out.println(new String(message));
			}
		};
		BasicUtils.logger = logger;
		try {
			RangeGenerator rg = new RangeGenerator();
			Map<String, String> ranges = rg.createRange("naam", logger);
			if (!ranges.get("start").equals(ranges.get("end"))) {
				String list = rg.generateAllTheUrls(ranges.get("start"), ranges.get("end"), logger);
				System.out.println(list);
			}
		} catch ( NonRecoverableException  e) {
			System.out.println(BasicUtils.exceptionTrace(e));
		} catch ( Exception e ) {
			System.out.println(BasicUtils.exceptionTrace(e));
		}  
	}
}
