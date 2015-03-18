package com.adobe.L732.exercise3;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.adobe.L732.AdobeMarketingCloudAPI;


public class Exercise3 {
	
	
	public static final String OAUTH2_CLIENTID = "23bfea7923-summit-lab-732"; // These credentials were disbled following the lab session, replace with your own
	public static final String OAUTH2_CLIENTSECRET = "54c2a4937f607624e917"; // These credentials were disbled following the lab session, replace with your own
	public static final String OAUTH2_TOKENURL = "https://api.omniture.com/token";
	
	public static final Boolean DEBUG_PROXY = true;
	

	public static void main(String[] args) throws IOException {
		
		if(DEBUG_PROXY){
			System.setProperty("https.proxyHost", "127.0.0.1");
			System.setProperty("https.proxyPort", "8888");
		}
		
		AdobeMarketingCloudAPI client = new AdobeMarketingCloudAPI(
				OAUTH2_TOKENURL,
				OAUTH2_CLIENTID,
				OAUTH2_CLIENTSECRET
		);
		
		/**
		 * Get an access token
		 */
		String token = client.requestOauthToken();
		System.out.println("token retrieved: " + token);
		client.setOauthToken(token);
		
		
		/**
		 * Get a filtered list of Segments in the company
		 */
		String parameters = new String(Files.readAllBytes(Paths.get("./src/com/adobe/L732/exercise3/parameters_ex3.json")));
		String jsonResponse = client.callPOST("https://api.omniture.com/admin/1.4/rest/?method=Segments.Get", parameters);
		System.out.println(jsonResponse);

	}

}
