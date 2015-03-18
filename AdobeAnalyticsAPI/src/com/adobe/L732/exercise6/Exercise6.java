package com.adobe.L732.exercise6;


import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Exercise6 {
	
	public static final Boolean DEBUG_PROXY = true;

	public static void main(String[] args) throws Exception {
		
		if (DEBUG_PROXY) {
			System.setProperty("https.proxyHost", "127.0.0.1");
			System.setProperty("https.proxyPort", "8888");
		}

		URL url = new URL( "[enter livestream url here]" );
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setRequestMethod("GET");
		String apiToken = "[enter api token here]";
		urlConn.setRequestProperty("Authorization", "Bearer " + apiToken);
		urlConn.setRequestProperty("Accept-Encoding", "gzip");
		urlConn.setDoOutput(false);
		urlConn.connect();
		System.out.println("Connected to LiveStream...");
		
		GZIPInputStream gzInput = new GZIPInputStream(urlConn.getInputStream());

		String chunkBuffer = "";
		byte[] byteBuffer = new byte[8192];
		int bytes_read = 0;
		while ((bytes_read = gzInput.read(byteBuffer)) > 0) {
			String newChunk = new String(byteBuffer, 0, bytes_read);
			if( newChunk.length() > 0 && !newChunk.equals("\r\n")){
				chunkBuffer += newChunk;
				int index = 0;
				while((index = chunkBuffer.indexOf("\r\n")) > -1){
					processBeacon(chunkBuffer.substring(0, index));
					chunkBuffer = chunkBuffer.substring(index+2);
				}
			}	
		}
		gzInput.close();
		
	}

	
	private static void processBeacon(String beacon){
		
		JSONObject beaconObject = new JSONObject( new JSONTokener( beacon ));
			
		System.out.println( "Page Name: " + beaconObject.get("pageName") );
		//System.out.println( beaconObject.toString(4) );	
		
		
		Set<String> beaconProperties = new HashSet<String>(Arrays.asList( JSONObject.getNames(beaconObject)));
		
		if(beaconProperties.contains("events")){
			JSONObject eventsObject = beaconObject.getJSONObject("events");
			Set<String> eventsProperties = new HashSet<String>(Arrays.asList( JSONObject.getNames(eventsObject)));
			if(eventsProperties.contains("purchase")){
				/* ====== Send Notification of purchase ====== */
				String notifyAddress = "10digitphonenumber@txt.att.net";  // Verizon is 10digitphonenumber@vtext.com
				Exercise6Email email = new Exercise6Email();
				email.send("Purchase notification","Purchase ID " + beaconObject.getString("purchaseId"), notifyAddress);
			}
		}
	}
	

}