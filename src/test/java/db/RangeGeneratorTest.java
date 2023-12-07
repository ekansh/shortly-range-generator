package db;

import java.util.Map;
import utils.MyLambdaLogger;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import core.RangeGenerator;
import exceptions.NonRecoverableException;
import utils.BasicUtils;


public class RangeGeneratorTest {
	public static void main(String[] args) throws NonRecoverableException {
		LambdaLogger logger = new MyLambdaLogger().getLogger();
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
