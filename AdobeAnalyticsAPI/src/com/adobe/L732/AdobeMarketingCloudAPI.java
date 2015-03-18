package com.adobe.L732;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64; 
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;



public class AdobeMarketingCloudAPI {

	private String wsseUsername = null;
	private String wsseSecret = null;
	
	private String oauthClientID = null;
	private String oauthClientSecret = null;
	private String oauthTokenURL = null;
	
	private String oauthToken = null;
	
	/**
	 * Constructor for WSSE client 
	 * 
	 * @param username
	 * @param secret
	 */
	public AdobeMarketingCloudAPI(String username, String secret) {
		
		this.wsseUsername = username;
		this.wsseSecret = secret;
		
	}
	
	/**
	 * Constructor for OAuth 2 client
	 * 
	 * @param tokenURL
	 * @param clientID
	 * @param clientSecret
	 */
	public AdobeMarketingCloudAPI(String tokenURL, String clientID, String clientSecret){
		
		this.oauthClientID = clientID;
		this.oauthClientSecret = clientSecret;
		this.oauthTokenURL = tokenURL;
	}
	
	
	/**
	 * Returns an OAuth 2 token
	 * 
	 * @param tokenURL
	 * @param clientID
	 * @param clientSecret
	 * @return
	 */
	public String requestOauthToken(){
		
		String jsonResponse = null;

		try {
			String combinedCredentials = this.oauthClientID + ":" + this.oauthClientSecret;
			String headerValue = "Basic " + Base64.encodeBase64String(combinedCredentials.getBytes());
			
			URL url = new URL( this.oauthTokenURL );
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.addRequestProperty("Authorization", headerValue);
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write("grant_type=client_credentials");
			wr.flush();
			connection.connect();
			InputStream in = connection.getInputStream();
		    BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    StringBuffer sBuffer = new StringBuffer();
		    String temp = null;
		    while ((temp = res.readLine()) != null)
		    	sBuffer.append(temp);
		    res.close();
			jsonResponse = sBuffer.toString();
		
		} catch (IOException e) {e.printStackTrace();} catch (Exception e) {e.printStackTrace();}
		
		JSONObject json = new JSONObject( new JSONTokener( jsonResponse ));
		
		return json.getString("access_token");
	}
	
	public void setOauthToken(String t){
		this.oauthToken = t;
	}
	
	public String getOauthToken(){
		return this.oauthToken;
	}
	
	
	/**
	 * The method getWSSEHeader generates WSSEHeader information for use in callPOST and callGET methods
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String getWSSEHeader() throws NoSuchAlgorithmException{
		String WSSEHeader = null;
		
		// *** produce a time stamp
		SimpleDateFormat createdat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		createdat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timestamp = createdat.format(Calendar.getInstance().getTime()).trim();
		
		// *** create a random number for nonce
		String rand = Long.toString(new Date().getTime());
		
		// *** Base64 the random number to create a nonce
		String nonce = Base64.encodeBase64String(rand.getBytes()).trim();
		
		// *** Create a password digest from Timestamp, random number and Secret
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.reset();
		md.update(rand.getBytes());
		md.update(timestamp.getBytes());
		md.update(this.wsseSecret.getBytes());
		String passwordDigest = Base64.encodeBase64String(md.digest()).trim();
		
		// *** Create WSSE Header
		WSSEHeader = "UsernameToken Username=\""+this.wsseUsername.trim()+"\", "+"PasswordDigest=\""+passwordDigest+"\", "+"Nonce=\""+nonce+"\", "+"Created=\""+timestamp+"\"";
		WSSEHeader = WSSEHeader.replace("\n", "");
		
		return WSSEHeader;
	}
	

	/**
	 * Method to call a HTTP GET method. Accepts String URL as an argument
	 * 
	 * @param urlname
	 * @return
	 */
	public String callGET(String urlname){ 
		
		String jsonResponse = null;

		try {
			URL url = new URL(urlname);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json");
			if(this.oauthToken != null){
				connection.addRequestProperty("Authorization", "Bearer " + this.oauthToken);
			}else if(this.wsseUsername != null){
				connection.addRequestProperty("X-WSSE", getWSSEHeader());
			}
			
			connection.connect();
			InputStream in = connection.getInputStream();
		    BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    StringBuffer sBuffer = new StringBuffer();
		    String temp = null;
		    while ((temp = res.readLine()) != null)
		    	sBuffer.append(temp);
		    	res.close();
			jsonResponse = sBuffer.toString();
		
			} catch (IOException e) {e.printStackTrace();} catch (Exception e) {e.printStackTrace();}
	
		if(jsonResponse.charAt(0)=='['){
			JSONArray jsonArray = new JSONArray( new JSONTokener( jsonResponse ));
			return jsonArray.toString(4);
		}else if(jsonResponse.charAt(0)=='{'){
			JSONObject jsonObject = new JSONObject( new JSONTokener( jsonResponse ));
			return jsonObject.toString(4);
		}else return jsonResponse;
	}

	
	/**
	 * Method to call a HTTP POST method. Accepts String URL and JSON formatted post data as an argument
	 * 
	 * @param urlname
	 * @param postData
	 * @return
	 */
	public String callPOST(String urlname,String postData){  
		
		String jsonResponse=null;
		
		try {
			URL url = new URL(urlname);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json");
			if(this.oauthToken != null){
				connection.addRequestProperty("Authorization", "Bearer " + this.oauthToken);
			}else if(this.wsseUsername != null){
				connection.addRequestProperty("X-WSSE", getWSSEHeader());
			}
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(postData);
			wr.flush();
			connection.connect();
			InputStream in = connection.getInputStream();
		    BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    StringBuffer sBuffer = new StringBuffer();
		    String temp = null;
		    while ((temp = res.readLine()) != null)
		    	sBuffer.append(temp);
		    res.close();
			jsonResponse = sBuffer.toString();
		
			} catch (IOException e) {e.printStackTrace();} catch (Exception e) {e.printStackTrace();}
	
		
		if(jsonResponse.charAt(0)=='['){
			JSONArray jsonArray = new JSONArray( new JSONTokener( jsonResponse ));
			return jsonArray.toString(4);
		}else if(jsonResponse.charAt(0)=='{'){
			JSONObject jsonObject = new JSONObject( new JSONTokener( jsonResponse ));
			return jsonObject.toString(4);
		}else return jsonResponse;

	}
}
