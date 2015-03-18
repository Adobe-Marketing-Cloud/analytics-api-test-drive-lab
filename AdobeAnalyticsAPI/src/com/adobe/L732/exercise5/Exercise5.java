package com.adobe.L732.exercise5;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Exercise5 {

	public static final Boolean DEBUG_PROXY = true;

	public static void main(String[] args) throws Exception {
		if (DEBUG_PROXY) {
			System.setProperty("https.proxyHost", "127.0.0.1");
			System.setProperty("https.proxyPort", "8888");
		}
		
		

		DataInsertion di = new DataInsertion(
			"[Enter the report suite ID]",     //  the Report Suite ID
			"1234567890123456-6543210987654321"  // the Visitor ID from s_vi cookie
		);
		di.set("pageName", "Data Insertion Exercise");
		di.set("pageURL", "http://labs.adobesummit.com/L732");
		di.set("channel", "Labs");
		di.set("prop1", "[Your Full Name]");  // Put your full name here
		di.set("events", "event1");
		di.set("eVar2", "Exercise 5");

		URL url = new URL("http://summitL732.112.2o7.net/b/ss//6");
		URLConnection urlConn = null;
		DataOutputStream printout = null;
		BufferedReader input = null;
		String tmp = null;
		
		urlConn = url.openConnection();
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestProperty("Content-Type", "application/xml");

		printout = new DataOutputStream(urlConn.getOutputStream());

		printout.writeBytes(di.toString());
		printout.flush();
		printout.close();

		input = new BufferedReader(new InputStreamReader(
				urlConn.getInputStream()));

		System.out.println(di);
		while (null != ((tmp = input.readLine()))) {
			System.out.println(tmp);
		}
		printout.close();
		input.close();
	}

}