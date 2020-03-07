import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.arowpay.ArowpayAPI;
import com.arowpay.ArowpayAPI.ArowpayAPICallException;

/**
 * Testcases for the arowpay api. Please ensure that your 
 * configuration allows the call of all APIs
 * @author gue
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests
{

	ArowpayAPI api = null;
	private static String statusUrl = "";
	private static String transactionId = "";
	
	@Before
	public void onStartup()
	{

		// Your keys go in here 
		String appkey = "YOUR_APP_KEY";
		String appsecret = "YOUR_APP_SECRET";
		
		
		if(appkey.equals("YOUR_APP_KEY") || appsecret.equals("YOUR_APP_SECRET"))		
		{
			throw new IllegalStateException("Please enter your appkey/appsecret  keys into the unittest");
		}
		this.api = new ArowpayAPI(appkey, appsecret);
	}
	
	@Test(expected=ArowpayAPICallException.class)
	public void test1_GetCallbackAddress()
	{
		JsonObject j = api.set("currency", "BTC")
		                  .set("custom","label1")
		                  .call("getCallbackAddress");
		String address=j.get("msg").getAsString();
	}

}
