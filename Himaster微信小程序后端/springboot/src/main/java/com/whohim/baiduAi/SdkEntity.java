package com.whohim.baiduAi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

/**
 * 语音识别
 */
public class SdkEntity {

	private static final String serverURL = "http://vop.baidu.com/server_api";
	private static String token = "";
	
	// put your own params here
	// 下面3个值要填写自己申请的app对应的值
	private static final String apiKey = "e7TGIdGOWE65PTbAwschxmc1";
	private static final String secretKey = "64e7d1ff79ad44c8039ca1722aee9062";
	private static final String cuid = "1C872CA50BDA";//本机mac

//	public static  String main(String destPath) throws Exception {
//		getToken();
//		
//		method2();
//		return method1(FileName);
//	}

	public static void getToken() throws Exception {
		String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + "&client_id="
				+ apiKey + "&client_secret=" + secretKey;
		HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
		token = new JSONObject(printResponse(conn)).getString("access_token");
		System.out.println(token);
	}

	public static  String method1(String FileName) throws Exception {
		File pcmFile = new File(FileName);
		System.out.println(pcmFile.exists());
		HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();

		// construct params
		JSONObject params = new JSONObject();
		params.put("format", "pcm");
		params.put("rate", 16000);
		params.put("channel", "1");
		params.put("token", token);
		params.put("lan", "zh");
		params.put("cuid", cuid);
		params.put("len", pcmFile.length());
		params.put("speech", DatatypeConverter.printBase64Binary(loadFile(pcmFile)));

		// add request header
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

		conn.setDoInput(true);
		conn.setDoOutput(true);

		// send request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(params.toString());
		wr.flush();
		wr.close();

		
		return printResponse(conn);
	}

	private static void method2(String FileName) throws Exception {
		File pcmFile = new File(FileName);
		HttpURLConnection conn = (HttpURLConnection) new URL(serverURL + "?cuid=" + cuid + "&token=" + token)
				.openConnection();

		// add request header
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "audio/pcm; rate=16000");

		conn.setDoInput(true);
		conn.setDoOutput(true);

		// send request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(loadFile(pcmFile));
		wr.flush();
		wr.close();

		System.out.println(printResponse(conn));
	}

	/**
	 *  打印输出
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static String printResponse(HttpURLConnection conn) throws Exception {
		if (conn.getResponseCode() != 200) {
			// request error
			System.out.println("conn.getResponseCode() = " + conn.getResponseCode());
			return "";
		}
		InputStream is = conn.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		System.out.println(new JSONObject(response.toString()).toString(4));
		return response.toString();
	}

	/**
	 * 加载文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}
}
