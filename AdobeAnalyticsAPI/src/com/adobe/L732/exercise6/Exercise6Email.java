package com.adobe.L732.exercise6;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Exercise6Email {
	private static String SMPT_HOSTNAME = "smtp.gmail.com";
	private static String USERNAME = "[Enter a gmail email address]";
	private static String PASSWORD = "[Enter the gmail password]";

	public void send(String subjectText, String msgText, String toAddress) {

	    try{

	        Properties props = new Properties();
	        props.put("mail.smtp.host", SMPT_HOSTNAME); // for gmail use smtp.gmail.com
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.debug", "false"); 
	        props.put("mail.smtp.starttls.enable", "false");
	        props.put("mail.smtp.port", "465");
	        props.put("mail.smtp.socketFactory.port", "465");
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.socketFactory.fallback", "false");

	        Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {

	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(USERNAME, PASSWORD);
	            }
	        });

	        //mailSession.setDebug(true); // Enable the debug mode

	        Message msg = new MimeMessage( mailSession );

	        //--[ Set the FROM, TO, DATE and SUBJECT fields
	        msg.setFrom( new InternetAddress( USERNAME ) );
	        msg.setRecipients( Message.RecipientType.TO,InternetAddress.parse(toAddress) );
	        msg.setSentDate( new java.util.Date());
	        msg.setSubject( subjectText );

	        //--[ Create the body of the mail
	        msg.setText( msgText );

	        //--[ Ask the Transport class to send our mail message
	        Transport.send( msg );
	        System.out.println( "Purchase notification sent to " + toAddress);

	    }catch(Exception E){
	        System.out.println( "Oops something has gone pearshaped!");
	        System.out.println( E );
	    }
	}

}
