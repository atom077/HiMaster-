package com.whohim.miaodi;

import java.net.URLEncoder;

import com.whohim.springboot.util.HttpUtil;
import net.sf.json.JSONObject;

/**
 * 验证码通知短信接口
 * 
 * @ClassName: IndustrySMS
 * @Description: 验证码通知短信接口
 *
 */
public class IndustrySMS
{
	private static String operation = "/industrySMS/sendSMS";

	private static String accountSid = Config.ACCOUNT_SID;
//	private static String phone = "15016823064";
	private static String randonNumber ="666666";//验证码
	private static String smsContent = " ";


	/**
	 * 验证码通知短信
	 * @param phone
	 */
	public static String execute(String phone)
	{
		randonNumber = createRandom(true, 6);
		smsContent = "【Himaster】您的验证码为"+randonNumber+"，请于1分钟内正确输入，如非本人操作，请忽略此短信。";
		String tmpSmsContent = null;
	    try{
	      tmpSmsContent = URLEncoder.encode(smsContent, "UTF-8");
	    }catch(Exception e){

	    }
	    String url = Config.BASE_URL + operation;
	    String body = "accountSid=" + accountSid + "&to=" + phone + "&smsContent=" + tmpSmsContent
	        + HttpUtil.createCommonParam();

	    // 提交请求
	    String result = HttpUtil.post(url, body);
	    System.out.println("result:" + System.lineSeparator() + result+"randonNumber:"+randonNumber);
		JSONObject jsonObject = JSONObject.fromObject(result);
		//通过getString("")分别取出里面的信息
		String respCode = jsonObject.getString("respCode");
		System.out.println("respCode:"+respCode);
		String lastResult = respCode+","+randonNumber;
		System.out.println("lastResult:"+lastResult);
		return lastResult;
	}
	
	/**
	* 创建指定数量的随机字符串
	* @param numberFlag 是否是数字
	* @param length
	* @return
	*/
	public static String createRandom(boolean numberFlag, int length){
	 String retStr = "";
	 String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
	 int len = strTable.length();
	 boolean bDone = true;
	 do {
	  retStr = "";
	  int count = 0;
	  for (int i = 0; i < length; i++) {
	  double dblR = Math.random() * len;
	  int intR = (int) Math.floor(dblR);
	  char c = strTable.charAt(intR);
	  if (('0' <= c) && (c <= '9')) {
	   count++;
	  }
	  retStr += strTable.charAt(intR);
	  }
	  if (count >= 2) {
	  bDone = false;
	  }
	 } while (bDone);
	 System.out.println("验证码是："+retStr);
	 return retStr;
	}
}
