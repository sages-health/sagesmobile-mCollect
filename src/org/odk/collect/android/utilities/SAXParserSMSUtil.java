/******************************************************************************
*                              COPYRIGHT NOTICE                              *
* Copyright (c) 2011 The Johns Hopkins University/Applied Physics Laboratory *
*                            All rights reserved.                            *
*                                                                            *
* This material was developed with U.S. Government support.                  *
* The U.S. Government has certain rights in this material.                   *
******************************************************************************/
package org.odk.collect.android.utilities;

import java.net.URISyntaxException;

/**
 * @author POKUAM1
 * @created Sep 2, 2011
 */
public class SAXParserSMSUtil {

	//private static SAXParseSMS mastersaxparser = new SAXParseSMS();
	private static String s_smsprefix = "sms://";
	
	public static boolean isValidSMSURL(String smsurl) {
		return ( smsurl.startsWith(s_smsprefix) && smsurl.substring(s_smsprefix.length()).matches("\\d+"));
	}
	
	public static String parseSMSfromURL(String smsurl) throws URISyntaxException{
		String smsnum = smsurl.substring(s_smsprefix.length());
		if (!smsnum.matches("\\d+")){
			throw new URISyntaxException(smsurl, "invalid SMS number");
		}
		return smsurl.substring(s_smsprefix.length());
	}
	
/*	
	public static class SAXParseSMS extends DefaultHandler {
		
		private StringBuilder smsStr = new StringBuilder();
		private String tmpChar = "";
		
		public SAXParseSMS(){
			super();
		}
		
		public String getSMSstring() {
			return smsStr.toString();
		}

		
		public void startElement(String uri, String name, String qName, Attributes atts) {
			System.out.println("_start_name: " + name + ", qname: " + qName);
			System.out.println("atts: " + atts.getValue(0));
			System.out.println("tmpChar: " + tmpChar);
			smsStr.append(",").append(tmpChar).append(",").append(qName);
			System.out.println("reset tmpChar");
			tmpChar = "";
		}

		public void endElement(String uri, String name, String qName) {
			System.out.println("_end_name: " + name + ", qname: " + qName);
		}

		//TODO: POKU need handling for formatted SMS also
		public void characters(char ch[], int start, int length) {
			boolean preserveFormat = false;
			
			if (!preserveFormat) {
				for (int i = start; i < start + length; i++) {
				    switch (ch[i]) {
					    case '\\':
						System.out.print("\\\\");
						break;
					    case '"':
						System.out.print("\\\"");
						break;
					    case '\n':
						System.out.print("\\n");
						ch[i] = " ".charAt(0);
						break;
					    case '\r':
						System.out.print("\\r");
						ch[i] = " ".charAt(0);
						break;
					    case '\t':
						System.out.print("\\t");
						ch[i] = " ".charAt(0); 
						break;
					    default:
						System.out.print(ch[i]);
						break;
				    }
				}
			} else {
				
			}
			tmpChar = tmpChar + new String(ch, start, length);
			System.out.println("chars: " +  tmpChar);
		}
	}*/
}
