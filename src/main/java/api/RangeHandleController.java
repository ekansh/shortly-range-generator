package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import core.RangeGenerator;
import exceptions.NonRecoverableException;
import utils.BasicUtils;

public class RangeHandleController implements RequestStreamHandler {
	
	//TODO :  Retry , Exception Handling and 
	//	multiple process trying to generate range because all of them have read the same lastEndRange ->
	// isolation level-> locking mechanism: pessimistic / optimistic
	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		BasicUtils.logger= logger;
		logger.log("Invoked RangeHandleController.handleRequest");
		JSONObject responseJson = new JSONObject();

		try {
			Map<String, JSONObject> requestParams = BasicUtils.getRequestParams(input);
			String name = (String) requestParams.get("queryStringParameters").get("name");
			logger.log("Param received " + name);
			JSONObject responseBody = new JSONObject();
			JSONObject headerJson = new JSONObject();
			RangeGenerator rg = new RangeGenerator();
			Map<String, String> ranges = rg.createRange(name, logger);
			if (  !ranges.get("start").equals(ranges.get("end"))){
				String list = rg.generateAllTheUrls(ranges.get("start"),ranges.get("end"),logger);
				responseBody.put("range_start", ranges.get("start"));
				responseBody.put("range_end", ranges.get("end"));
				responseBody.put("range_list", list);
				responseJson.put("statusCode", 200);
			}else {
				responseJson.put("statusCode", 400);
			}
			headerJson.put("x-custom-header", "my custom header value");
			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());
		} catch (ParseException e) {
			JSONObject responseBody = new JSONObject();
			responseBody.put("exception", "Error while parsing");
			responseJson.put("statusCode", 422);
			logger.log(BasicUtils.exceptionTrace(e));
		}  catch (NonRecoverableException e) {
			JSONObject responseBody = new JSONObject();
			responseBody.put("exception", "Error while parsing");
			responseJson.put("statusCode", 500);
			logger.log(BasicUtils.exceptionTrace(e));
		} catch (IOException e) {
			JSONObject responseBody = new JSONObject();
			responseBody.put("exception", "IO Exception");
			responseJson.put("statusCode", 422);
			logger.log(BasicUtils.exceptionTrace(e));
 		}
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();

	}


}
