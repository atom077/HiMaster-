package com.whohim.springboot.controller.backend;

import com.github.pagehelper.PageInfo;
import com.whohim.springboot.common.Const;
import com.whohim.springboot.common.DataCache;
import com.whohim.springboot.common.ResponseCode;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.pojo.User;
import com.whohim.springboot.service.IModuleService;
import com.whohim.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IModuleService iModuleService;

//    @RequestMapping(value = "login", method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<User> login(String phone, String password) {
//        ServerResponse<User> response = iUserService.login(phone, password);
//        if (response.isSuccess()) {
//            User user = response.getData();
//            if (user.getRole() == Const.Role.ROLE_ADMIN) {
//                //说明登录的是管理员
//                session.setAttribute(Const.CURRENT_USER, user);
//                return response;
//            } else {
//                return ServerResponse.createByErrorMessage("不是管理员,无法登录");
//            }
//        }
//        return response;
//    }
    /**
     * 查看当前himaster系统用户列表 /manage/user/list
     * 注：该查询方法只可查询，该机器码对应的系统的用户列表。
     *
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getList(String  token, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        if(token.equals(org.apache.commons.lang3.StringUtils.EMPTY)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"密钥不能为空！");
        }
        String phone = iModuleService.getPhone(token);
        if(DataCache.getKey(phone) == null){
            return ServerResponse.createByErrorMessage("登录已过期，请重新登录！");
        }

            return iUserService.getCurrentHimasterList(token,pageNum,pageSize);

    }

    @RequestMapping(value = "add_user_root",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse <User>addUserRoot(String  phone,String rasperrypie, HttpSession session){
        User  user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(),"用户未登录,请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iUserService.addUserRoot(phone,rasperrypie);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
