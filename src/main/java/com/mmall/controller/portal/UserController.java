package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

     @Autowired
     private IUserService iUserService;

    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "login.do",method = RequestMethod.POST)
     public ServerResponse<User> login(String username, String password, HttpSession session){
         ServerResponse<User> response=iUserService.login(username,password);
         if(response.isSuccess()){
             session.setAttribute(Const.CURRENT_USER,response.getData());
         }
         return response;
     }

    /**
     * 用户退出登陆
     * @param session
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "logout.do",method = RequestMethod.GET)
     public ServerResponse<String> logout(HttpSession session){
         session.removeAttribute(Const.CURRENT_USER);
         return ServerResponse.createBySuccessMessage("退出成功");
     }


    /**
     * 用户注册
     * @param user
     * @return
     */
     @ResponseBody
     @RequestMapping(value = "register.do",method = RequestMethod.POST)
     public ServerResponse<String> register(User user){
        return iUserService.register(user);
     }

     @ResponseBody
     @RequestMapping(value = "check_valid" ,method = RequestMethod.GET)
     public ServerResponse<String> checkValid(String str,String type){
          return iUserService.checkValid(str,type);
     }

}
