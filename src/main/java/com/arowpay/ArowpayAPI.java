package com.arowpay;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class ArowpayAPI
{
	private String				appkey;
	private String				appsecret;
	private Map<String, String>	params;
	private static Logger		logger	= LoggerFactory.getLogger(ArowpayAPI.class);
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * ctor
	 * 
	 * @param appkey from the api
	 * @param appsecret from the api
	 */
	public ArowpayAPI(String appkey, String appsecret)
	{
		this.appkey = appkey;
		this.appsecret = appsecret;
		this.params = new HashMap<String, String>();
	}

	/**
	 * Sets a specific key to a specific value
	 * 
	 * @param key key that should be set
	 * @param value value for this key. If the value is null then the key is deleted
	 * @return
	 */
	public ArowpayAPI set(String key, Object value)
	{

		if (key == null && value == null) {
			this.params.clear();
		}
		else
			if (value == null) {
				this.params.remove(key);
			}
			else {
				this.params.put(key, value.toString());
			}
		return this;
	}

	/**
	 * Encodes a single String to UTF8
	 * 
	 * @param s the string that should be encoded
	 * @return
	 */
	private String urlEncodeUTF8(String s)
	{
		try {
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Builds a HTTP URL from a key-value list
	 * 
	 * @param map
	 * @return
	 */
	private String urlEncodeUTF8(Map<?, ?> map)
	{
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey().toString()),
					urlEncodeUTF8(entry.getValue().toString())));
		}
		return sb.toString();
	}

	/**
	 * Calls the API
	 * 
	 * @param cmd
	 * @return the answer from the
	 */
	public JsonObject call(String cmd)
	{

		// Copy the current map to
		Map<String, String> req = new HashMap<String, String>();
		req.putAll(params);
		params.clear();

		try {
			// Generate the query string
			String post_data = urlEncodeUTF8(req);

			// Calculate the SIGNATURE
			String nonce = String.valueOf(Math.random() * 1000000);
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			String toSign = new StringBuilder(this.appsecret).append(nonce).append(timestamp).toString();
			String signature = String.valueOf(DigestUtils.sha1Hex(toSign));
			URL obj = new URL("https://api.arowpay.io/"+cmd);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// Set the request headers
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setHostnameVerifier(DO_NOT_VERIFY);
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("appkey", this.appkey);
			con.setRequestProperty("nonce", nonce);
			con.setRequestProperty("timestamp", timestamp);
			con.setRequestProperty("signature", signature);

			// Send post request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(post_data);
			wr.flush();
			wr.close();

			// Wait for the resüonse code
			int responseCode = con.getResponseCode();

			// Read the full response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parse the JSON response with GSON
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonTree = jsonParser.parse(response.toString());
			JsonObject jsonObject = jsonTree.getAsJsonObject();
			return jsonObject;
		}
		catch (Exception e) {
			logger.error("Exception occured: " + e.getMessage());
			throw new ArowpayAPICallException(e.getMessage());
		}
	}

    /**
	 * Validate IPNs
	 * 
	 * @return the bool result
	 */
	public Boolean validateIPN(String appkey,String signature,String nonce,String timestamp,String txid,String amount,String currency){
		  String toSign = this.appsecret + nonce + timestamp + txid + amount + currency;
          String calculateSignature = String.valueOf(DigestUtils.sha1Hex(toSign));
          if(signature.equalsIgnoreCase(calculateSignature) && appkey.equalsIgnoreCase(this.appkey)){
          	return true;
          }else{
          	return false;
          }
          
	}

	/**
	 * Exception which is thrown if the API call fails
	 * 
	 * @author gue
	 *
	 */
	public class ArowpayAPICallException extends RuntimeException
	{

		private static final long serialVersionUID = -703701416098191297L;
		public ArowpayAPICallException(String message)
		{
			super(message);
		}

	}

};