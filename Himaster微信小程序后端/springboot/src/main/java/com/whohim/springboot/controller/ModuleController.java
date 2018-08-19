package com.whohim.springboot.controller;

import java.io.IOException;
import java.util.Map;

import com.whohim.springboot.common.Const;
import com.whohim.springboot.common.DataCache;
import com.whohim.springboot.common.ResponseCode;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.pojo.User;
import com.whohim.springboot.service.IModuleService;
import com.whohim.springboot.service.IUserService;
import com.whohim.springboot.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/module")
public class ModuleController {

	@Autowired
	private IModuleService iModuleService;

	/**
	 * 开关灯
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "led",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse  controlLed(String  token) throws IOException {
		if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
		}
		String phone = iModuleService.getPhone(token);
		if(DataCache.getKey(phone) == null){
			return ServerResponse.createByErrorMessage("登录已过期!");
		}
//			//填充业务
			return iModuleService.controlLed(token);
	}

	/**
	 *开关门
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "door",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse  controlDoor(String  token) throws IOException {
		if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
		}
		String phone = iModuleService.getPhone(token);
		if(DataCache.getKey(phone) == null){
			return ServerResponse.createByErrorMessage("登录已过期!");
		}
//			//填充业务
			return iModuleService.controlDoor(token);
	}

	/**
	 * 开关智能插座
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "smart_socket",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse  controlSmartSocket(String  token) throws IOException {
		if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
		}
		String phone = iModuleService.getPhone(token);
		if(DataCache.getKey(phone) == null){
			return ServerResponse.createByErrorMessage("登录已过期！");
		}
//			//填充业务
			return iModuleService.controlsmartSocket(token);
	}

	/**
	 * 取得模块状态及数据
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "get_module_status_and_data",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Map> getModuleStatusAndData(String  token) throws Exception {
		if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
		}
		String phone = iModuleService.getPhone(token);
		if(DataCache.getKey(phone) == null){
			return ServerResponse.createByErrorMessage("登录已过期!");
		}
//			//填充业务
			return iModuleService.getModuleStatusAndData(token);
	}

	/**
	 * 选择模式
	 * @param
	 * @param
	 * @param md
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "choose_pattern",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse  choosePattern(String  token,String md) throws IOException {
		if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
		}
		String phone = iModuleService.getPhone(token);
		if(DataCache.getKey(phone) == null){
			return ServerResponse.createByErrorMessage("登录已过期！");
		}
//			//填充业务
			return iModuleService.choosePattern(token,md);
	}


	/**
	 *获得定位地点天气
	 * @param lon 经度
	 * @param lat 纬度
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "get_weather",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse  getWeather(String lon,String lat) throws IOException {
		 return iModuleService.getWeather(lon,lat);
	}


	@RequestMapping(value = "get_doorData",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse  getDoorData(String token) {
		if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
		}
		String phone = iModuleService.getPhone(token);
		if(DataCache.getKey(phone) == null){
			return ServerResponse.createByErrorMessage("登录已过期！");
		}
//			//填充业务
		return iModuleService.getDoorData(token);
	}

}
