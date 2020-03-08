# Arowpay-java-api
Java API implementation and IPNs validation for the cryptocoin payment provider "arowpay.com"

This is a simple API for the arowpay.com platform which is an integrated payment gateway for cryptocurrencies such as Bitcoin , USDT and ethereum. The actual API documentation can be looked up at https://docs.arowpay.com

this is a simple Java wrapper to make it useable from within your Java projects. We have tried to keep it as 
simple an clean as possible, but however there are some dependencies like slf4j, commons-codec and gson which must be resolved.
This can either be done via apache-ivy or by hand. 

# Usage

## API Calls
````
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.arowpay.ArowpayAPI;
import com.arowpay.ArowpayAPI.ArowpayAPICallException;

String appkey = "your appkey";
String appsecret = "your appsecret";
ArowpayAPI api  = new ArowpayAPI(appkey, appsecret);
JsonObject j = api.set("currency", "BTC")
		          .set("custom","label1")
		          .call("getCallbackAddress");
String code = j.get("code").getAsString(); 
if(code.equals("200")){
	String address = j.get("msg").getAsString();
}
````

## Validate IPNs

IPN Data Structure

````
HTTP Header:
Content-Type:application/json
nonce:102221
timestamp:1581582671
appkey:yourappkey
signature:632667547e7cd3e0466547863e1207a8c0c0c549

HTTP Body Content :
{"txid":"c85d2669ac777574762640c36e66592df946aa90615952797d62c9070cebbeb5","address":"1PJb6kLcZjUeq4fkKJ6ubDnEbx8ELJyRfd","amount":"0.0051","currency":"BTC","time":1581582672,"custom":"yourcustomstrings"}
````

Validate IPNs
````
String appkey = "your appkey";
String appsecret = "your appsecret";
String appkey = "yourappkey";
String nonce  = "102221";
String timestamp = "1581582671";
String signature = "632667547e7cd3e0466547863e1207a8c0c0c549";
String txid = "c85d2669ac777574762640c36e66592df946aa90615952797d62c9070cebbeb5";
String amount = "0.0051";
String currency = "BTC";
ArowpayAPI api  = new ArowpayAPI(yourappkey, yourappsecret);
if(api.validateIPN(appkey,signature,nonce,timestamp,txid,amount,currency)){
    //validate , process the vars
}else{
	//invalid IPNs 
}
````
