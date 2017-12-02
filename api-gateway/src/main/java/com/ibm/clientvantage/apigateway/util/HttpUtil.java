/**
 * ===============================================================================
 *
 * IBM Confidential
 * 
 * OCO Source Materials
 *
 * 5747-SM3
 *
 * (C) Copyright IBM Corp. 2017 All Rights Reserved.
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with 
 * the U.S. Copyright office .
 * 
 * ===============================================================================
 */
package com.ibm.clientvantage.apigateway.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpUtil {

	//private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	/**
	 * setUp a Mock Trust Manager for Https connection
	 * 
	 * @param
	 * @return
	 */
	public static void setUpMockTrustManager4Https() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		} catch (NoSuchAlgorithmException e) {
			logger.error("Algorithm not found for SSL", e);
		} catch (KeyManagementException e) {
			logger.error("not supported KeyManagement", e);
		}

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	/**
	 * Send post request to URL
	 * 
	 * @param url
	 * 
	 * @param param
	 *            name1=value1&name2=value2
	 * @return response
	 */
	public static String sendPost(String url, String param) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			// set up a mock trust manager to unconditionally trust any server
			// side certificate
			// just for convenience in PILOT phase.
			setUpMockTrustManager4Https();

			URL realUrl = new URL(url);
			// open a connection
			URLConnection conn = realUrl.openConnection();
			// set header info
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");

			// output is needed
			conn.setDoOutput(true);

			// input is needed
			conn.setDoInput(true);

			// get the output stream of connection
			out = new OutputStreamWriter(conn.getOutputStream());
			// send the post parameters
			out.write(param);
			// flush the output stream
			out.flush();
			// use BufferedReader to read the response of the POST connection
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
		} catch (IOException e) {
			
			logger.error("Exception happens when sending POST request", e);
			
			if(e.getMessage() != null && e.getMessage().indexOf("401") > 0)
				result = "REFRESHTOKEN_EXPIRED_W3ID_RETURN";
			
		} catch (Exception e) {
			logger.error("Exception happens when sending POST request", e);
		}
		
		// do not forget to close the input and output steams
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("Exception happens when closing stream", ex);
			}
		}
		return result;
	}
	/**
	 * Send get request to URL. It must send get request when authenticate the access token on cas server.
	 * 
	 * @param url
	 * 
	 * @param param
	 *            name1=value1&name2=value2
	 * @return response
	 */
	public static String sendGet(String url, String param) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			// set up a mock trust manager to unconditionally trust any server
			// side certificate
			// just for convenience in PILOT phase.
			setUpMockTrustManager4Https();
			String urlString = url + "?"+param;
            logger.info(urlString);
			URL realUrl = new URL(urlString);
			// open a connection
			URLConnection conn = realUrl.openConnection();
			// set header info
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.connect();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				//result += line;
				result.append(line);
				//logger.info("result="+result);
			}
			
		} catch (IOException e) {
			
			logger.error("Exception happens when sending Get request", e);
			
			if(e.getMessage() != null && e.getMessage().indexOf("401") > 0)
				result.append("REFRESHTOKEN_EXPIRED_W3ID_RETURN");
			
		} catch (Exception e) {
			logger.error("Exception happens when sending GET request", e);
		}
		
		// do not forget to close the input and output steams
		finally {
			try {
//				if (out != null) {
//					out.close();
//				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("Exception happens when closing stream", ex);
			}
		}
		return result.toString();
	}
}