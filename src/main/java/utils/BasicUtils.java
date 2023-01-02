package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import api.RangeHandleController;
import core.RangeGenerator;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class BasicUtils {
	public static LambdaLogger logger;

	public static Map<String, JSONObject> getRequestParams(InputStream input )
			throws IOException, ParseException {
		Map<String, JSONObject> params = new HashMap<String, JSONObject>() {
			{
				put("pathParameters", new JSONObject());
				put("queryStringParameters", new JSONObject());
			}
		};

		logger.log("Invoked getRequestParams");
		JSONParser parser = new JSONParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		JSONObject event = (JSONObject) parser.parse(reader);
		if (event.get("pathParameters") != null) {
			JSONObject pps = (JSONObject) event.get("pathParameters");
			params.put("pathParameters", pps);
		}
		if (event.get("queryStringParameters") != null) {
			JSONObject qsp = (JSONObject) event.get("queryStringParameters");
			params.put("queryStringParameters", qsp);
		}

		return params;
	}

	public static Map jsonToMap(String text) {
		Map  map = new LinkedHashMap();
		JSONParser parser = new JSONParser();

		ContainerFactory containerFactory = new ContainerFactory() {
			@Override
			public Map createObjectContainer() {
				return new LinkedHashMap<>();
			}

			@Override
			public List creatArrayContainer() {
				return new LinkedList<>();
			}
		};
		try {
			map = (Map) parser.parse(text, containerFactory);
			map.forEach((k, v) -> logger.log("Key : " + k + " Value : " + v));
		} catch (ParseException pe) {
			logger.log("position: " + pe.getPosition());
			logger.log(exceptionTrace(pe));
		}
		return map;
	}
	public static Map<String,String> getEnvVariable() {
		Map<String,String> map = new HashMap<>();
		map.put("dbInstanceIdentifier", System.getenv("dbInstanceIdentifier"));
		map.put("username",System.getenv("username"));
		map.put("password", System.getenv("password"));
		map.put("host",System.getenv("host"));
		map.put("port",System.getenv("port"));
//		map.put("dbInstanceIdentifier","shortly");
//		map.put("username","root");
//		map.put("password", "Welcome1");
//		map.put("host","shortly.cqrni4n4j4dx.us-east-2.rds.amazonaws.com");
//		map.put("port","5432");
		return map;
	}
	
	public static String getSecret() {

		String secretName = "range_generator.db";
		Region region = Region.of("us-east-2");

		// Create a Secrets Manager client

		SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build();

		GetSecretValueResponse getSecretValueResponse;

		try {
			getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
		} catch (Exception e) {
			logger.log(exceptionTrace(e));
			// For a list of exceptions thrown, see
			// https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
			throw e;
		}

		String secret = getSecretValueResponse.secretString();
		return secret;
	}

	public static String exceptionTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
